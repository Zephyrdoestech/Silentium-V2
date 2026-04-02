    package Screens;

    import Mechanics.MapTraversalSystem.Room;
    import com.badlogic.gdx.Gdx;
    import com.badlogic.gdx.Input;
    import com.badlogic.gdx.graphics.Color;
    import com.badlogic.gdx.graphics.GL20;
    import com.badlogic.gdx.graphics.Texture;
    import com.badlogic.gdx.graphics.g2d.TextureRegion;
    import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
    import Entities.Character;
    import Entities.Enemy;
    import io.github.Zephyrdoestech.GameContext;
    import io.github.Zephyrdoestech.Main;

    import java.util.Random;
    import java.util.Timer;

    /**
     * Turn-based combat screen.
     *
     * Flow:
     *   ATTACK → (player types 3 notes) → DISPLAY_PLAYER_DAMAGE
     *   → ENEMY_ATTACK → DISPLAY_ENEMY_DAMAGE → ATTACK (next turn)
     *   → VICTORY or DEFEAT
     */
    public class CombatScreen extends BaseScreen {
        private static final Random RNG                 = new Random();
        private static final float  DISPLAY_TIME = 2.2f;

        private boolean             notesRolledThisTurn = false;
        private boolean             activeSkillUsedThisTurn = false;

        private String              chordUsedThisTurn = null;
        private float               stateTimer = 0f;

        private boolean             enemyAttacked = false;
        private boolean             playerAttacked = false;

        // ── Game Display ────────────────────────────────────────────────────────────────

        private float               screenLeftEdge = 0;
        private float               screenVerticalCenter = Main.WORLD_WIDTH / 2f;
        private float               screenRightEdge = Main.WORLD_WIDTH;

        private float               screenTopEdge = Main.WORLD_HEIGHT;
        private float               screenHorizontalCenter = Main.WORLD_HEIGHT / 2f;
        private float               screenBottomEdge = 0;

        // ── Entity Display ────────────────────────────────────────────────────────────────

        private float               playerXPosition = 0;
        private float               playerYPosition = 0;
        private float               enemyXPosition = 0;
        private float               enemyYPosition = 0;
        private float               gap = 32;
        private float               addGap(float scale){ return gap * scale; }

        // ── HUD Display ────────────────────────────────────────────────────────────────

        private float hudStaticLeftEdge = screenLeftEdge;
        private float hudStaticRightEdge = screenLeftEdge + Main.WORLD_WIDTH * 0.30f;
        private float hudStaticTopEdge = screenBottomEdge + Main.WORLD_HEIGHT * 0.40f;
        private float hudStaticBottomEdge = screenBottomEdge;
        private float hudStaticWidth = Main.WORLD_WIDTH * 0.30f;
        private float hudStaticHeight = Main.WORLD_HEIGHT * 0.40f;

        private float TimerLeftEdge = screenLeftEdge + Main.WORLD_WIDTH * 0.30f;
        private float TimerRightEdge = screenLeftEdge + Main.WORLD_WIDTH * 0.45f;
        private float TimerTopEdge = screenBottomEdge + Main.WORLD_HEIGHT * 0.40f;
        private float TimerBottomEdge = screenBottomEdge;
        private float TimerWidth = Main.WORLD_WIDTH * 0.15f;
        private float TimerHeight = Main.WORLD_HEIGHT * 0.40f;

        private float hudDynamicLeftEdge = screenLeftEdge + Main.WORLD_WIDTH * 0.45f;
        private float hudDynamicRightEdge = screenRightEdge;
        private float hudDynamicTopEdge = screenBottomEdge + Main.WORLD_HEIGHT * 0.40f;
        private float hudDynamicBottomEdge = screenBottomEdge;
        private float hudDynamicWidth = Main.WORLD_WIDTH * 0.55f;
        private float hudDynamicHeight = Main.WORLD_HEIGHT * 0.40f;

        // ── Damage Values ────────────────────────────────────────────────────────────────

        private int initialDamage;
        private int finalDamage;
        private boolean metronomeActivated;

        private float maxTurnTime = 0f;
        private float turnTime = 0f;

        private int                 turnMenuSelection = 0;
        private final String[]      turnMenuOptions = {"Attack", "Skill", "Inventory"};

        private int                 confirmSelection = 0;
        private final String[]      confirmOptions = {"Use", "Cancel"};

        private Character c;
        private Enemy e;

        public CombatScreen(Main game) { super(game); }

        @Override
        public void show() {
            c = game.ctx.activeCharacterStats;
            e = game.ctx.currentEnemy;

            game.gameCamera.position.set(Main.WORLD_WIDTH / 2f, Main.WORLD_HEIGHT / 2f, 0);
            game.gameCamera.update();
            game.ctx.resultTimer = 0f;
            game.ctx.combatLog = "";
            game.ctx.chordSystem.resetChords();
            game.ctx.metronome.reset();
            game.ctx.combatState = GameContext.CombatState.BATTLE_SCREEN;
            notesRolledThisTurn = false;
            stateTimer = 0f;
            turnMenuSelection = 0;
            confirmSelection = 0;

            // First enemy encountered (monstersDefeated == 0) gets 30% health
            if (game.ctx.activeCharacterStats.getMonstersDefeated() == 0) {
                int originalHp = e.getMaxHp();
                int reducedHp = (int)(originalHp * 0.3f);
                e.setMaxHp(reducedHp);
            }

            switch(game.ctx.mapName){
                case TOWN_OF_ECHOES:
                    maxTurnTime = 15f;
                    break;
                case SILENT_CAVERNS:
                    maxTurnTime = 20f;
                    break;
                case ABYSS_OF_DISSONANCE:
                    maxTurnTime = 25f;
                    break;
                default:
            }
            activeSkillUsedThisTurn = false;
        }

        // ── Render ────────────────────────────────────────────────────────────────

        @Override
        public void render(float delta) {
            Gdx.gl.glClearColor(0,0,0,1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            switch (game.ctx.combatState){
                case BATTLE_SCREEN:
                case VICTORY:
                case DEFEAT:
                    renderSplashScreen(delta);
                    break;
                case TUTORIAL:
    //                renderTutorial();
                    break;
                default:
                    renderCombat(delta);
                    break;
            }

            game.assets.font.setColor(Color.WHITE);
            game.assets.titleFont.setColor(Color.WHITE);
        }


        private void renderSplashScreen(float delta){
            stateTimer += delta;
            TextureRegion currentFrame = null;

            switch (game.ctx.combatState){
                case BATTLE_SCREEN:
                    currentFrame = game.assets.battleIntroAnim.getKeyFrame(stateTimer, false);
                    break;
                case VICTORY:
                    currentFrame = game.assets.victoryAnim.getKeyFrame(stateTimer, false);
                    break;
                case DEFEAT:
                    currentFrame = game.assets.defeatAnim.getKeyFrame(stateTimer, false);
                    break;
                default:
                    break;
            }

            if (currentFrame != null) {
                game.batch.setProjectionMatrix(game.uiCamera.combined);
                game.batch.begin();
                game.batch.draw(currentFrame,
                    (Main.WORLD_WIDTH - currentFrame.getRegionWidth()) / 2f,
                    (Main.WORLD_HEIGHT - currentFrame.getRegionHeight()) / 2f);
                game.batch.end();
            }

            if(game.ctx.combatState == GameContext.CombatState.BATTLE_SCREEN
                && game.assets.battleIntroAnim.isAnimationFinished(stateTimer))
                game.ctx.combatState = GameContext.CombatState.ENEMY_INTRODUCTION;

            if(game.ctx.combatState == GameContext.CombatState.VICTORY
                && game.assets.victoryAnim.isAnimationFinished(stateTimer)){
                endCombat();
            }

            if(game.ctx.combatState == GameContext.CombatState.DEFEAT
                && game.assets.defeatAnim.isAnimationFinished(stateTimer)){
                endCombat();
            }

        }

        private void renderCombat(float delta){
            if(game.ctx.combatState == GameContext.CombatState.BATTLE_SCREEN ||
            game.ctx.combatState == GameContext.CombatState.TUTORIAL ||
            game.ctx.combatState == GameContext.CombatState.VICTORY ||
            game.ctx.combatState == GameContext.CombatState.DEFEAT) return;

            //Combat Graphics
            renderBackground();
            renderEntities(delta);
            renderStats();

            //Name Header
            renderNameHeader();

            //Static HUD
            renderStaticHUD(delta);

            //Timer
            renderTimer(delta);

            //Dynamic HUD
            renderDynamicHUD(delta);
        }

        private  void renderNameHeader(){
            // Background
            float textWidth = 0f;
            float backgroundWidth = 0f;
            float backgroundHeight = addGap(4.0f);
            float YPosition =screenTopEdge - addGap(2.0f);
            game.shapeRenderer.setProjectionMatrix(game.uiCamera.combined);
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            game.shapeRenderer.setColor(20f/255f, 30f/255f, 50f/255f, 1f);
            backgroundWidth = addGap( 4.0f) + getStringWidth(c.getName());
            game.shapeRenderer.rect(
                screenLeftEdge, YPosition,
                backgroundWidth,backgroundHeight);
            backgroundWidth = addGap( 4.0f) + getStringWidth(e.getName());
            game.shapeRenderer.rect(
                screenRightEdge - backgroundWidth,
                YPosition,
                backgroundWidth,backgroundHeight);
            game.shapeRenderer.end();

            game.batch.setProjectionMatrix(game.uiCamera.combined);
            game.batch.begin();
            game.assets.font.getData().setScale(2.0f);
            game.assets.font.setColor(Color.WHITE);
            game.assets.font.draw(game.batch, c.getName(),
                screenLeftEdge + addGap(1.0f), screenTopEdge - addGap(1.0f));
            textWidth = getStringWidth(e.getName());
            game.assets.font.draw(game.batch, e.getName(),
                screenRightEdge - addGap(1.0f) - textWidth,
                screenTopEdge - addGap(1.0f));
            game.batch.end();
        }

        private void renderStaticHUD(float delta){
            // Background
            game.batch.setProjectionMatrix(game.uiCamera.combined);
            game.batch.begin();
            game.batch.draw(game.assets.StaticHUDBackground,
                hudStaticLeftEdge, hudStaticBottomEdge,
                hudStaticWidth,hudStaticHeight);
            game.batch.end();

            float XPosition = hudStaticLeftEdge + addGap(1.0f);
            float YPosition = hudStaticTopEdge - addGap(0.8f);

            // Only show beat if not first battle
            game.batch.setProjectionMatrix(game.uiCamera.combined);
            game.batch.begin();
            game.assets.font.getData().setScale(0.8f);
            if (game.ctx.activeCharacterStats.getMonstersDefeated() > 0) {
                game.assets.font.setColor(Color.WHITE);
                game.assets.font.draw(game.batch,
                    "Beat: " + game.ctx.metronome.getBeat(),
                    XPosition, YPosition);
            }else{
                game.assets.font.setColor(Color.WHITE);
                game.assets.font.draw(game.batch,
                    "Metronome not yet unlocked!",
                    XPosition, YPosition);
            }

            if(game.ctx.combatState == GameContext.CombatState.ENEMY_INTRODUCTION || game.ctx.combatState == GameContext.CombatState.TURN_MENU){
                // Roll Notes at the start of the Turn
                if (!notesRolledThisTurn) {
                    game.ctx.noteHandler.rollNotes();
                    notesRolledThisTurn = true;
                }
            }

            YPosition -= addGap(0.4f);
            game.assets.font.getData().setScale(0.8f);
            game.assets.font.setColor(Color.YELLOW);
            game.assets.font.draw(game.batch,
                "Note Damages:",
                XPosition, YPosition);

            String[] noteLabels = {"A", "B", "C", "D", "E", "F", "G"};
            int[] noteDamages = {
                game.ctx.noteHandler.noteDamage('a'),
                game.ctx.noteHandler.noteDamage('b'),
                game.ctx.noteHandler.noteDamage('c'),
                game.ctx.noteHandler.noteDamage('d'),
                game.ctx.noteHandler.noteDamage('e'),
                game.ctx.noteHandler.noteDamage('f'),
                game.ctx.noteHandler.noteDamage('g')
            };

            XPosition = hudStaticLeftEdge + addGap(2.0f);
            YPosition -= addGap(0.64f);
            float newYPosition = YPosition;
            for (int i = 0; i < noteLabels.length; i++) {
                // Only show notes available for player's level
                if (game.ctx.noteHandler.isValidNote(noteLabels[i].charAt(0), c.getLevel())) {
                    newYPosition = YPosition - (addGap(0.48f) * i);
                    game.assets.font.getData().setScale(1.0f);
                    game.assets.font.setColor(Color.WHITE);
                    game.assets.font.draw(game.batch, noteLabels[i],
                        XPosition, newYPosition);
                    game.assets.font.setColor(Color.GREEN);
                    game.assets.font.draw(game.batch, "-->",
                        XPosition + addGap(8f),
                        newYPosition);
                    game.assets.font.draw(game.batch, String.valueOf(noteDamages[i]),
                        XPosition + addGap(1.6f),
                        newYPosition);
                }
            }
            game.assets.font.getData().setScale(1.0f); // Reset to small scale
            game.batch.end();
        }



        private void renderTimer(float delta){
            // Background
            game.batch.setProjectionMatrix(game.uiCamera.combined);
            game.batch.begin();
            game.batch.draw(game.assets.TimerBackground,
                TimerLeftEdge, TimerBottomEdge,
                TimerWidth,TimerHeight);
            game.batch.end();

            int time = 0;
            switch (game.ctx.combatState) {
                case TURN_MENU:
                case ATTACK:
                case USE_SKILL:
                case OPEN_INVENTORY:
                case USE_ITEM:
                    turnTime += delta;
                    time = (int) Math.ceil(maxTurnTime - turnTime);
                    if (turnTime >= maxTurnTime) {
                        turnTime = 0f;
                        game.ctx.resultTimer = 0;
                        game.ctx.combatState = GameContext.CombatState.ENEMY_ATTACK;
                    }
                    break;
                default:
                    turnTime = 0f; // Reset timer when not in player's turn
                    time = 0;
                    break;
            }

            game.ctx.resultTimer += delta;
            game.batch.setProjectionMatrix(game.uiCamera.combined);
            game.batch.begin();
            game.assets.font.getData().setScale(1.2f);
            if(time <= 3){
                game.assets.font.setColor(Color.RED);
            }else{
                game.assets.font.setColor(Color.GREEN);
            }

            // Center Log Display
            float textWidth = getStringWidth(time + "");
            float XPosition = TimerLeftEdge + ((TimerWidth - textWidth) / 2f);
            float YPosition = TimerBottomEdge + addGap(2.0f);
            game.assets.font.draw(game.batch, time + "",
                XPosition, YPosition);
            game.assets.font.getData().setScale(1.0f);
            game.assets.font.setColor(Color.WHITE);
            game.batch.end();

            // Timer Animation
            Texture TimerFrame = game.assets.timerAnim.getKeyFrame(stateTimer, true).getTexture();
            float spriteWidth = addGap(1.0f);
            float spriteHeight = addGap(1.6f);
            XPosition = TimerLeftEdge + ((TimerWidth - spriteWidth) / 2f);
            YPosition = TimerTopEdge - addGap(1.0f) - spriteHeight;
            game.batch.setProjectionMatrix(game.uiCamera.combined);
            game.batch.begin();
            game.batch.draw(TimerFrame, XPosition, YPosition, spriteWidth,spriteHeight);
            game.batch.end();

        }

        private void renderDynamicHUD(float delta){
            // Background
            game.batch.setProjectionMatrix(game.uiCamera.combined);
            game.batch.begin();
            game.batch.draw(game.assets.DynamicHUDBackground,
                hudDynamicLeftEdge, hudDynamicBottomEdge,
                hudDynamicWidth, hudDynamicHeight);
            game.batch.end();

            switch(game.ctx.combatState){
                case ENEMY_INTRODUCTION:
                case CHARACTER_POSTCOMBAT_LINE:
                    renderDialogueNarrate(delta);
                    break;
                case TURN_MENU:
                case ATTACK:
                case ATTACK_FEEDBACK:
                case USE_SKILL:
                case OPEN_INVENTORY:
                case USE_ITEM:
                    renderCombatInterface(delta);
                    break;
                case DISPLAY_CHORD_EFFECT:
                case DISPLAY_PLAYER_DAMAGE:
                case DISPLAY_FINAL_DAMAGE:
                case ENEMY_ATTACK:
                case DISPLAY_ENEMY_DAMAGE:
                    renderBattleLog(delta);
                    break;
                default:
                    return;
            }


        }

        private void renderDialogueNarrate(float delta){
            game.batch.setProjectionMatrix(game.uiCamera.combined);
            game.batch.begin();

            switch(game.ctx.combatState){
                case ENEMY_INTRODUCTION:
                    game.ctx.combatLog = e.getName() + " encountered!";
                    break;
                case CHARACTER_POSTCOMBAT_LINE:
                    game.ctx.combatLog = "Insert Post Combat Dialogue Here.";
                    break;
                default:
                    break;
            }

            game.assets.font.setColor(Color.valueOf("ddddaa"));
            game.assets.font.getData().setScale(1.6f);
            game.assets.font.draw(game.batch, game.ctx.combatLog,
                hudDynamicLeftEdge + addGap(1.6f), hudDynamicTopEdge - addGap(1.6f));
            game.batch.end();

            stateTimer += delta;
            if (stateTimer >= DISPLAY_TIME) {
                stateTimer = 0f;
                if (game.ctx.combatState == GameContext.CombatState.ENEMY_INTRODUCTION) {
                    game.ctx.combatState = GameContext.CombatState.TURN_MENU;
                } else if (game.ctx.combatState == GameContext.CombatState.CHARACTER_POSTCOMBAT_LINE) {
                    game.ctx.combatState = GameContext.CombatState.VICTORY;
                }
            }
        }

        public void renderBattleLog(float delta){
            float textWidth = 0f;
            float XPosition = 0f;
            float YPosition = 0f;

            switch (game.ctx.combatState) {
                case DISPLAY_CHORD_EFFECT:
                    if(!playerAttacked){
                        playerAttacked= true;
                    }
                    if(chordUsedThisTurn != null){
                        game.ctx.resultTimer += delta;
                        game.batch.setProjectionMatrix(game.uiCamera.combined);
                        game.batch.begin();

                        game.assets.font.getData().setScale(1.6f);
                        game.ctx.combatLog = "[" + getChordName(chordUsedThisTurn) + "] " + getChordMessage(chordUsedThisTurn);
                        // Center Log Display
                        textWidth = getStringWidth(game.ctx.combatLog);
                        XPosition = hudDynamicLeftEdge + ((hudDynamicWidth - textWidth) / 2f);
                        YPosition = hudDynamicHeight / 2f + addGap(0.4f);
                        game.assets.font.draw(game.batch, game.ctx.combatLog,
                            XPosition, YPosition);
                        game.assets.font.getData().setScale(1.0f);
                        game.batch.end();
                        if (game.ctx.resultTimer >= DISPLAY_TIME) {
                            game.ctx.resultTimer = 0f;
                            game.ctx.combatState = GameContext.CombatState.DISPLAY_PLAYER_DAMAGE;
                        }
                    }else{
                        game.ctx.combatState = GameContext.CombatState.DISPLAY_PLAYER_DAMAGE;
                    }
                    break;

                case DISPLAY_PLAYER_DAMAGE:
                    game.ctx.resultTimer += delta;
                    game.batch.setProjectionMatrix(game.uiCamera.combined);
                    game.batch.begin();
                    game.ctx.combatLog = metronomeActivated ? "Initial" : "Total";
                    game.ctx.combatLog += " Damage Dealt: " +
                        (metronomeActivated ? initialDamage : finalDamage);
                    game.assets.font.getData().setScale(1.6f);
                    game.assets.font.setColor(Color.GREEN);

                    // Center Log Display
                    textWidth = getStringWidth(game.ctx.combatLog);
                    XPosition = hudDynamicLeftEdge + ((hudDynamicWidth - textWidth) / 2f);
                    YPosition = hudDynamicHeight / 2f + addGap(0.4f);
                    game.assets.font.draw(game.batch, game.ctx.combatLog,
                        XPosition, YPosition);
                    game.batch.end();

                    if (game.ctx.resultTimer >= DISPLAY_TIME) {
                        game.ctx.resultTimer = 0f;

                        if (metronomeActivated) {
                            game.ctx.combatState = GameContext.CombatState.DISPLAY_FINAL_DAMAGE;
                        } else {
                            game.ctx.combatState = e.isDefeated()
                                ? GameContext.CombatState.VICTORY
                                : GameContext.CombatState.ENEMY_ATTACK;
                        }
                    }
                    break;
                case DISPLAY_FINAL_DAMAGE:
                    game.ctx.resultTimer += delta;

                    game.batch.setProjectionMatrix(game.uiCamera.combined);
                    game.batch.begin();

                    game.ctx.combatLog = "Beat Sync! Total Damage Dealt: " + finalDamage;

                    game.assets.font.getData().setScale(1.6f);
                    game.assets.font.setColor(Color.GREEN);

                    textWidth = getStringWidth(game.ctx.combatLog);
                    XPosition = hudDynamicLeftEdge + ((hudDynamicWidth - textWidth) / 2f);
                    YPosition = hudDynamicHeight / 2f  + addGap(0.4f);

                    game.assets.font.draw(game.batch, game.ctx.combatLog, XPosition, YPosition);
                    game.batch.end();

                    if (game.ctx.resultTimer >= DISPLAY_TIME) {
                        game.ctx.resultTimer = 0f;

                        game.ctx.combatState = e.isDefeated()
                            ? GameContext.CombatState.VICTORY
                            : GameContext.CombatState.ENEMY_ATTACK;
                    }
                    break;
                case ENEMY_ATTACK:
                    if(!enemyAttacked){
                        doEnemyAttack();
                        enemyAttacked = true;
                    }
                    game.ctx.resultTimer += delta;
                    game.batch.setProjectionMatrix(game.uiCamera.combined);
                    game.batch.begin();
                    game.ctx.combatLog = e.getName() + " used " + e.getLastAttackName();
                    game.assets.font.getData().setScale(1.6f);
                    game.assets.font.setColor(Color.RED);

                    // Center Log Display
                    textWidth = getStringWidth(game.ctx.combatLog);
                    XPosition = hudDynamicLeftEdge + ((hudDynamicWidth - textWidth) / 2f);
                    YPosition = hudDynamicHeight / 2f  + addGap(0.4f);
                    game.assets.font.draw(game.batch, game.ctx.combatLog,
                        XPosition, YPosition);
                    game.assets.font.getData().setScale(1.0f);
                    game.batch.end();

                    if (game.ctx.resultTimer >= DISPLAY_TIME) {
                        game.ctx.resultTimer = 0f;
                        game.ctx.combatState = GameContext.CombatState.DISPLAY_ENEMY_DAMAGE;
                    }
                    break;

                case DISPLAY_ENEMY_DAMAGE:
                    game.ctx.resultTimer += delta;
                    game.batch.setProjectionMatrix(game.uiCamera.combined);
                    game.batch.begin();
                    game.ctx.combatLog = "You received " + game.ctx.enemyDamageDealt + " damage!";
                    game.assets.font.getData().setScale(1.6f);
                    game.assets.font.setColor(Color.RED);
                    textWidth = getStringWidth(game.ctx.combatLog);
                    XPosition = hudDynamicLeftEdge + ((hudDynamicWidth - textWidth) / 2f);
                    YPosition = hudDynamicHeight / 2f  + addGap(0.4f);
                    game.assets.font.draw(game.batch, game.ctx.combatLog,
                        XPosition, YPosition);
                    game.batch.end();
                    if (game.ctx.resultTimer >= DISPLAY_TIME) {
                        game.ctx.resultTimer = 0f;
                        finishRound();
                        if (!c.isAlive()) {
                            game.ctx.combatState = GameContext.CombatState.DEFEAT;
                        }else{
                            game.ctx.combatState = GameContext.CombatState.TURN_MENU;
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        private void renderCombatInterface(float delta) {

            switch (game.ctx.combatState){
                case TURN_MENU:
                    handleTurnMenu();
                    break;
                case ATTACK:
                    renderAttack();
                    break;
                case ATTACK_FEEDBACK:
                    renderAttackFeedback();
                    break;
                case USE_SKILL:
                    handleSkillMenu();
                    break;
                case OPEN_INVENTORY:
                    handleInventoryMenu();
                    break;
                case USE_ITEM:
                    break;
                default:
                    break;
            }
        }

        private void renderAttackFeedback(){

        }

        private void handleTurnMenu() {
            if(game.ctx.combatState != GameContext.CombatState.TURN_MENU) return;
            if (Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                turnMenuSelection = turnMenuSelection > 0 ? turnMenuSelection - 1 : turnMenuOptions.length - 1;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                turnMenuSelection = turnMenuSelection < turnMenuOptions.length - 1 ? turnMenuSelection + 1 : 0;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                if (turnMenuSelection == 0) {
                    game.ctx.combatState = GameContext.CombatState.ATTACK;
                } else if (turnMenuSelection == 1) {
                    game.ctx.combatState = GameContext.CombatState.USE_SKILL;
                    confirmSelection = 0;
                } else if (turnMenuSelection == 2) {
                    game.ctx.combatState = GameContext.CombatState.OPEN_INVENTORY;
                    confirmSelection = 0;
                }
                return;
            }

            // Options Background Display
            float menuHUDWidth = game.assets.TurnMenuHUD.getWidth() + addGap(0.6f);
            float menuHUDHeight = game.assets.TurnMenuHUD.getHeight() + addGap(0.5f);
            float menuXPosition = hudDynamicLeftEdge + ((hudDynamicWidth - menuHUDWidth)/2f);
            float menuYPosition = hudDynamicTopEdge - (hudDynamicHeight / 4) - menuHUDHeight + addGap(0.2f);

            game.batch.setProjectionMatrix(game.uiCamera.combined);
            game.batch.begin();

            // Option boxes
            game.batch.draw(game.assets.TurnMenuHUD,
                menuXPosition,menuYPosition,
                menuHUDWidth, menuHUDHeight);
            game.batch.end();

            //Text Placement
            game.batch.setProjectionMatrix(game.uiCamera.combined);
            game.batch.begin();
            game.assets.font.getData().setScale(1.6f);
            for (int i = 0; i < turnMenuOptions.length; i++) {
                float textWidth = 0f;
                float XPlacement = 0f;
                float YPlacement = hudDynamicTopEdge - (hudDynamicHeight / 16 * (4 + (i * 3)));

                float maxTextWidth = 0f;
                for (String option : turnMenuOptions) {
                    float w = getStringWidth(option);
                    if (w > maxTextWidth) maxTextWidth = w;
                }

                XPlacement = hudDynamicLeftEdge + (hudDynamicWidth - maxTextWidth) / 2f;
                if (i == turnMenuSelection) {
                    game.assets.font.setColor(Color.YELLOW);
                    game.assets.font.draw(game.batch, "> " + turnMenuOptions[i], XPlacement, YPlacement);
                } else {
                    game.assets.font.setColor(Color.WHITE);
                    game.assets.font.draw(game.batch, turnMenuOptions[i], XPlacement, YPlacement);
                }
            }
            game.assets.font.getData().setScale(1.0f);
            game.batch.end();
        }

        private float getStringWidth(String s){
            game.glyphLayout.setText(game.assets.font, s);
            return game.glyphLayout.width;
        }

        private void handleSkillMenu() {
            if (Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.A) || Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                confirmSelection = confirmSelection > 0 ? confirmSelection - 1 : confirmOptions.length - 1;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.D) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                confirmSelection = confirmSelection < confirmOptions.length - 1 ? confirmSelection + 1 : 0;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                if (confirmSelection == 0) { // USE
                    Character charStats = game.ctx.activeCharacterStats;
                    if (charStats.getName().equals("Aurelius")) {
                        game.ctx.noteHandler.lockNoteDamage();
                        game.ctx.combatLog = charStats.getName() + " used Conservation! Note damages locked.";
                    } else if (charStats.getName().equals("Lyron")) {
                        game.ctx.noteHandler.rollNotes();
                        game.ctx.combatLog = charStats.getName() + " used Musical Roulette! Notes rerolled.";
                    } else if (charStats.getName().equals("Sonara")) {
                        game.ctx.combatLog = charStats.getName() + " used Melodic Impromptu! +1 to initial damage.";
                    }
                    activeSkillUsedThisTurn = true;
                    game.ctx.combatState = GameContext.CombatState.TURN_MENU;
                } else { // CANCEL
                    game.ctx.combatState = GameContext.CombatState.TURN_MENU;
                }
                return;
            }

            game.shapeRenderer.setProjectionMatrix(game.uiCamera.combined);
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            game.shapeRenderer.setColor(0.1f, 0.1f, 0.2f, 0.8f);
            game.shapeRenderer.rect(hudDynamicLeftEdge + 20, hudDynamicBottomEdge + 40, 300, Main.WORLD_HEIGHT * 0.30f);
            game.shapeRenderer.end();

            game.batch.setProjectionMatrix(game.uiCamera.combined);
            game.batch.begin();
            game.assets.font.getData().setScale(1.2f);
            game.assets.font.setColor(Color.WHITE);
            game.assets.font.draw(game.batch, "Use Skill?", hudDynamicLeftEdge + 100, hudDynamicBottomEdge + 150);

            for (int i = 0; i < confirmOptions.length; i++) {
                if (i == confirmSelection) {
                    game.assets.font.setColor(Color.YELLOW);
                    game.assets.font.draw(game.batch, "> " + confirmOptions[i], hudDynamicLeftEdge + 50 + (i * 100), hudDynamicBottomEdge + 80);
                } else {
                    game.assets.font.setColor(Color.WHITE);
                    game.assets.font.draw(game.batch, confirmOptions[i], hudDynamicLeftEdge + 70 + (i * 100), hudDynamicBottomEdge + 80);
                }
            }
            game.assets.font.getData().setScale(1.0f);
            game.batch.end();
        }

        private void handleInventoryMenu() {
            if (Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.A) || Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                confirmSelection = confirmSelection > 0 ? confirmSelection - 1 : confirmOptions.length - 1;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.D) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                confirmSelection = confirmSelection < confirmOptions.length - 1 ? confirmSelection + 1 : 0;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                if (confirmSelection == 0) { // USE
                    game.ctx.combatLog = "Item used!"; // Placeholder
                    game.ctx.combatState = GameContext.CombatState.TURN_MENU;
                } else { // CANCEL
                    game.ctx.combatState = GameContext.CombatState.TURN_MENU;
                }
                return;
            }

            game.shapeRenderer.setProjectionMatrix(game.uiCamera.combined);
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            game.shapeRenderer.setColor(0.1f, 0.1f, 0.2f, 0.8f);
            game.shapeRenderer.rect(hudDynamicLeftEdge + 20, hudDynamicBottomEdge + 40, 350, Main.WORLD_HEIGHT * 0.30f);
            game.shapeRenderer.end();

            game.batch.setProjectionMatrix(game.uiCamera.combined);
            game.batch.begin();
            game.assets.font.getData().setScale(1.2f);
            game.assets.font.setColor(Color.WHITE);
            game.assets.font.draw(game.batch, "Select an item to use: Use item?", hudDynamicLeftEdge + 30, hudDynamicBottomEdge + 150);

            for (int i = 0; i < confirmOptions.length; i++) {
                if (i == confirmSelection) {
                    game.assets.font.setColor(Color.YELLOW);
                    game.assets.font.draw(game.batch, "> " + confirmOptions[i], hudDynamicLeftEdge + 80 + (i * 100), hudDynamicBottomEdge + 80);
                } else {
                    game.assets.font.setColor(Color.WHITE);
                    game.assets.font.draw(game.batch, confirmOptions[i], hudDynamicLeftEdge + 100 + (i * 100), hudDynamicBottomEdge + 80);
                }
            }
            game.assets.font.getData().setScale(1.0f);
            game.batch.end();
        }

        private void renderEntities(float delta){
            game.batch.setProjectionMatrix(game.uiCamera.combined);
            game.batch.begin();

            TextureRegion playerSprite = null;
            TextureRegion enemySprite = null;

            boolean playerAttackState = false;
            if(game.ctx.combatState == GameContext.CombatState.ATTACK_FEEDBACK){ playerAttackState = true;}
            if (game.ctx.selectedCharacter != null) {
                switch(game.ctx.selectedCharacter){
                    case SONARA:
                        if(playerAttackState)
                            playerSprite = game.assets.sonaraCombatAttack.getKeyFrame(stateTimer, true);
                        else
                            playerSprite = game.assets.sonaraCombatIdle.getKeyFrame(stateTimer, true);
                        break;
                    case AURELIUS:
                        if(playerAttackState)
                            playerSprite = game.assets.aureliusCombatAttack.getKeyFrame(stateTimer, true);
                        else
                            playerSprite = game.assets.aureliusCombatIdle.getKeyFrame(stateTimer, true);
                        break;
                    case LYRON:
                        if(playerAttackState)
                            playerSprite = game.assets.sonaraCombatAttack.getKeyFrame(stateTimer, true);
                        else
                            playerSprite = game.assets.lyronCombatAttack.getKeyFrame(stateTimer, true);
                        break;
                }
            }

            boolean enemyAttackState = false;
            if(game.ctx.combatState == GameContext.CombatState.ENEMY_ATTACK){enemyAttackState = true;}
            if (game.ctx.currentEnemy != null && game.ctx.currentEnemy.getName() != null) {
                switch(game.ctx.currentEnemy.getName()){
                    case "Flesh Feeder":
                        if(enemyAttackState)
                            enemySprite = game.assets.fleshfeederCombatAttack.getKeyFrame(stateTimer, true);
                        else
                            enemySprite = game.assets.fleshfeederCombatIdle.getKeyFrame(stateTimer, true);
                        break;
                    case "Darrylion":
                        if(enemyAttackState)
                            enemySprite = game.assets.darrylionCombatAttack.getKeyFrame(stateTimer, true);
                        else
                            enemySprite = game.assets.darrylionCombatIdle.getKeyFrame(stateTimer, true);
                        break;
                    case "Aryzachnid":
                        if(enemyAttackState)
                            enemySprite = game.assets.gobninilCombatAttack.getKeyFrame(stateTimer, true);
                        else
                            enemySprite = game.assets.gobninilCombatIdle.getKeyFrame(stateTimer, true);
                        break;
                    case "Chimericks":
                        if(enemyAttackState)
                            enemySprite = game.assets.chimericksCombatAttack.getKeyFrame(stateTimer, true);
                        else
                            enemySprite = game.assets.chimericksCombatIdle.getKeyFrame(stateTimer, true);
                        break;
                    case "Labagoliath":
                        if(enemyAttackState)
                            enemySprite = game.assets.labagoliathCombatAttack.getKeyFrame(stateTimer, true);
                        else
                            enemySprite = game.assets.labagoliathCombatIdle.getKeyFrame(stateTimer, true);
                        break;
                    case "Maestro Syozan":
                        if(enemyAttackState)
                            enemySprite = game.assets.syozanCombatAttack.getKeyFrame(stateTimer, true);
                        else
                            enemySprite = game.assets.syozanCombatIdle.getKeyFrame(stateTimer, true);
                        break;
                    default:
                        enemySprite = game.assets.fleshfeederCombatIdle.getKeyFrame(stateTimer, true);
                        break;
                }
            }

            float spriteWidth = 96;
            float spriteHeight = 96;
            playerXPosition = screenLeftEdge + addGap(4.0f);
            playerYPosition = screenTopEdge - addGap(6.4f);

            if (playerSprite != null) {
                game.batch.draw(playerSprite,
                    playerXPosition,
                    playerYPosition,
                    spriteWidth, spriteHeight);
            }

            spriteWidth = 160;
            spriteHeight = 160;

            enemyXPosition = screenRightEdge - addGap(4.0f) - spriteWidth;
            enemyYPosition = screenTopEdge - addGap(6.4f);
            if (enemySprite != null) {
                game.batch.draw(enemySprite,
                    enemyXPosition,
                    enemyYPosition,
                    spriteWidth,spriteHeight);
            }

            game.batch.end();
        }

        private void renderBackground(){
            // Dark background
            game.shapeRenderer.setProjectionMatrix(game.uiCamera.combined);
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            game.shapeRenderer.setColor(0.05f, 0.05f, 0.1f, 1f);
            game.shapeRenderer.rect(0, 0, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);
            game.shapeRenderer.end();

            // Default fallback to prevent crash if mapName is null
            Texture background = game.assets.townCombatBackground;

            if (game.ctx.mapName != null) {
                switch(game.ctx.mapName){
                    case TOWN_OF_ECHOES:
                        background = game.assets.townCombatBackground;
                        break;
                    case SILENT_CAVERNS:
                        background = game.assets.cavernsCombatBackground;
                        break;
                    case ABYSS_OF_DISSONANCE:
                        background = game.assets.abyssCombatBackground;
                        break;
                }
            }

            game.batch.setProjectionMatrix(game.uiCamera.combined);
            game.batch.begin();
            if (background != null) {
                game.batch.draw(background, 0, 0, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);
            }
            game.batch.end();
        }

        private void renderStats(){
            // Player
            float playerHealthBarXPosition = playerXPosition - addGap(1.2f);
            float playerHealthBarYPosition = playerYPosition - addGap(0.4f);

            float playerHealthBarContainerXPosition = playerHealthBarXPosition - 27;
            float playerHealthBarContainerYPosition = playerHealthBarYPosition - 21;

            float playerShieldBarXPosition = playerXPosition - addGap(1.2f);
            float playerShieldBarYPosition = playerYPosition - addGap(1.2f);

            float playerShieldBarContainerXPosition = playerShieldBarXPosition - 27;
            float playerShieldBarContainerYPosition = playerShieldBarYPosition - 21;

            // Enemy
            float enemyHealthBarXPosition = enemyXPosition - addGap(1.2f);
            float enemyHealthBarYPosition = enemyYPosition - addGap(0.4f);

            float enemyHealthBarContainerXPosition = enemyHealthBarXPosition - 27;
            float enemyHealthBarContainerYPosition = enemyHealthBarYPosition - 21;

            float barWidth = 144;
            float barHeight = 11.8f;
            float barContainerWidth = 180;
            float barContainerHeight = 32;


            game.shapeRenderer.setProjectionMatrix(game.uiCamera.combined);
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

            // Player HP bar
            drawBar(game.shapeRenderer, playerHealthBarXPosition,playerHealthBarYPosition, barWidth,barHeight,
                (float) c.getHp() / c.getMaxHp(),
                Color.DARK_GRAY, Color.RED);
            drawBar(game.shapeRenderer,
                playerShieldBarXPosition,playerShieldBarYPosition,barWidth,barHeight,
                c.getMaxShield() > 0 ? (float) c.getShield() / c.getMaxShield() : 0f,
                Color.DARK_GRAY, Color.CYAN);

            // Enemy HP bar
            drawBar(game.shapeRenderer,
                enemyHealthBarXPosition,enemyHealthBarYPosition, barWidth,barHeight,
                (float) e.getHp() / e.getMaxHp(), Color.DARK_GRAY, Color.RED);
            game.shapeRenderer.end();

            // Draw Container Bar
            game.batch.setProjectionMatrix(game.uiCamera.combined);
            game.batch.begin();
            game.batch.draw(game.assets.HealthBar,
                playerHealthBarContainerXPosition, playerHealthBarContainerYPosition,
                barContainerWidth, barContainerHeight);
            game.batch.draw(game.assets.ShieldBar,
                playerShieldBarContainerXPosition, playerShieldBarContainerYPosition,
                barContainerWidth, barContainerHeight);
            game.batch.draw(game.assets.HealthBar,
                enemyHealthBarContainerXPosition, enemyHealthBarContainerYPosition,
                barContainerWidth, barContainerHeight);


            // Player stats text
            game.assets.font.getData().setScale(1.0f);
            game.assets.font.setColor(Color.WHITE);
            game.assets.font.draw(game.batch,  c.getHp() + " / " + c.getMaxHp(),
                playerHealthBarXPosition + barWidth + addGap(0.4f),playerHealthBarYPosition);
            game.assets.font.draw(game.batch, c.getShield() + " / " + c.getMaxShield(),
                playerShieldBarXPosition + barWidth + addGap(0.4f),playerShieldBarYPosition);

            // Enemy stats (right side)
            game.assets.font.setColor(Color.valueOf("ff6666"));
            game.assets.font.draw(game.batch, e.getHp() + " / " + e.getMaxHp(),
                enemyHealthBarXPosition + barWidth + addGap(0.4f),enemyHealthBarYPosition);
            game.batch.end();
        }

        private void renderAttack(){
            float noteContainerWidth = addGap(2);
            float noteContainerHeight = addGap(2);
            float containerGap = addGap(1);
            float noteBackgroundWidth = (3f * noteContainerWidth) + (2f * containerGap);
            float containerXPosition = hudDynamicLeftEdge + ((hudDynamicWidth - noteBackgroundWidth)/2f);
            float containerYPosition = hudDynamicBottomEdge + addGap(0.4f) + ((hudDynamicHeight - noteContainerHeight) / 2f);
//            game.shapeRenderer.setProjectionMatrix(game.uiCamera.combined);
//            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//            // Note slot boxes
//            for (int i = 0; i < 3; i++) {
//                boolean filled = i < game.ctx.noteHandler.noteCount;
//                game.shapeRenderer.setColor(filled
//                    ? new Color(0.2f, 0.5f, 0.9f, 1f)
//                    : new Color(0.15f, 0.15f, 0.25f, 1f));
//                game.shapeRenderer.rect(
//                    containerXPosition + (i * containerGap) + (i * noteContainerWidth), containerYPosition,
//                    noteContainerWidth, noteContainerHeight);
//            }
//            game.shapeRenderer.end();

            game.batch.setProjectionMatrix(game.uiCamera.combined);
            game.batch.begin();
            // Note slot boxes
            for (int i = 0; i < 3; i++) {
                game.batch.draw(game.assets.NoteContainer,
                    containerXPosition + (i * containerGap) + (i * noteContainerWidth), containerYPosition,
                    noteContainerWidth, noteContainerHeight);
            }
            game.batch.end();

            game.batch.setProjectionMatrix(game.uiCamera.combined);
            game.batch.begin();
            float noteGap = 0f;
            float noteWidth = 0f;
            float noteHeight = addGap(1.6f);
            float XPosition = 0f;
            float YPosition = noteContainerHeight + containerYPosition - ((noteContainerHeight - noteHeight) * 1.45f);
            String note = "";

            // Note slots (display letters centered in boxes) - bigger font for buffer
            game.assets.font.getData().setScale(2.0f);
            for (int i = 0; i < 3; i++) {
                if (i < game.ctx.noteHandler.noteCount) {
                    game.assets.font.setColor(Color.WHITE);
                    note = String.valueOf(java.lang.Character.toUpperCase(game.ctx.noteHandler.noteBuffer[i]));

                } else {
                    game.assets.font.setColor(new Color(0.4f, 0.4f, 0.55f, 1f));
                    note = "_";
                }
                noteWidth = getStringWidth(note);
                noteGap = ((noteContainerWidth - noteWidth) * 1.45f) + containerGap;
                XPosition = containerXPosition + ((noteContainerWidth - noteWidth) / 2f);
                game.assets.font.draw(game.batch, note,
                    XPosition + (i * noteGap),
                    YPosition);
            }
            game.assets.font.getData().setScale(1.0f);
            game.batch.end();

            // Input guide
            game.assets.font.setColor(Color.GRAY);
            String inputGuide = getInputGuide(game.ctx.activeCharacterStats.getLevel());
            float textWidth = getStringWidth(inputGuide);
            XPosition = hudDynamicLeftEdge + ((hudDynamicWidth - textWidth) / 2f);
            YPosition = hudDynamicBottomEdge + addGap(1.8f);
            game.batch.setProjectionMatrix(game.uiCamera.combined);
            game.batch.begin();
            game.assets.font.draw(game.batch, inputGuide, XPosition, YPosition);
            game.batch.end();

            handleCombatInput();
        }

        // ── Note input ────────────────────────────────────────────────────────────

        private void handleCombatInput() {
            if (game.ctx.combatState != GameContext.CombatState.ATTACK) return;


            if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE) && game.ctx.noteHandler.noteCount > 0) {
                game.ctx.noteHandler.noteCount--;
                return;
            }

            int[]  keys  = { Input.Keys.A, Input.Keys.B, Input.Keys.C,
                Input.Keys.D, Input.Keys.E, Input.Keys.F, Input.Keys.G };
            char[] notes = { 'a', 'b', 'c', 'd', 'e', 'f', 'g' };

            int level = c.getLevel();
            int maxIndex = (level == 1) ? 3 : (level == 2) ? 5 : 7;

            for (int i = 0; i < maxIndex; i++) {
                if (Gdx.input.isKeyJustPressed(keys[i]) && game.ctx.noteHandler.noteCount < 3) {
                    char currentNote = notes[i];

                    // Check if this note is already in the buffer (no duplicates)
                    boolean isDuplicate = false;
                    for (int j = 0; j < game.ctx.noteHandler.noteCount; j++) {
                        if (game.ctx.noteHandler.noteBuffer[j] == currentNote) {
                            isDuplicate = true;
                            break;
                        }
                    }

                    // Only add if not a duplicate
                    if (!isDuplicate) {
                        game.ctx.noteHandler.noteBuffer[game.ctx.noteHandler.noteCount]  = currentNote;
                        game.ctx.noteHandler.noteDamages[game.ctx.noteHandler.noteCount] = game.ctx.noteHandler.noteDamage(currentNote);
                        game.ctx.noteHandler.noteCount++;
                    }
                    break;
                }
            }
            if (game.ctx.noteHandler.noteCount == 3) resolveAttack();
        }


        // ── Attack resolution ─────────────────────────────────────────────────────
        // SHOULD HAVE ALL THE LOGIC HERE
        // SETS THE COMBAT STATE FOR RENDERING
        private void resolveAttack() {
            Character c = game.ctx.activeCharacterStats;
            initialDamage = 0;
            for (int i = 0; i < 3; i++) initialDamage += game.ctx.noteHandler.noteDamages[i];

            // Apply active skill effects only if skill was used this turn
            if (activeSkillUsedThisTurn) {
                initialDamage = c.activeSkillEffect(initialDamage);
            }

            finalDamage = initialDamage;
            // Apply metronome only if not first battle
            metronomeActivated = false;
            if (game.ctx.activeCharacterStats.getMonstersDefeated() > 0) {
                finalDamage = game.ctx.metronome.updateBeat(initialDamage);
                metronomeActivated = initialDamage != finalDamage;
            }

            // Apply damage buff
            finalDamage = (int)(finalDamage * (1.0 + c.getDamageBuff()));


            //Apply chords
            chordUsedThisTurn = null;
            if (c.getLevel() >= 3) {
                String chord = game.ctx.chordSystem.checkChord(
                    java.lang.Character.toUpperCase(game.ctx.noteHandler.noteBuffer[0]),
                    java.lang.Character.toUpperCase(game.ctx.noteHandler.noteBuffer[1]),
                    java.lang.Character.toUpperCase(game.ctx.noteHandler.noteBuffer[2]));
                if (chord != null) {
                    initialDamage = game.ctx.chordSystem.applyChord(chord, c, initialDamage);
                    chordUsedThisTurn = chord; // Track chord for notification
                }
            }

            game.ctx.currentEnemy.takeDamage(finalDamage);

            // Lyron passive: gain shield based on damage dealt
            if (c.getName().equals("Lyron")) {
                c.LyronPassiveSkillEffect(c, initialDamage);
            }

            game.ctx.combatState  = GameContext.CombatState.DISPLAY_CHORD_EFFECT;
            game.ctx.resultTimer  = 0f;
        }

        private void doEnemyAttack() {
            int dmg = game.ctx.currentEnemy.performAttack();
            c.takeDamage(dmg);

            // Sonara passive: reflect damage back to enemy
            if (c.getName().equals("Sonara")) {
                c.SonaraPassiveSkillEffect(game.ctx.currentEnemy, dmg);
            }

            game.ctx.enemyDamageDealt = dmg;
        }

        private void finishRound() {
            Character c = game.ctx.activeCharacterStats;
            if (!c.isAlive()) {
                game.ctx.combatState = GameContext.CombatState.DEFEAT; return;
            }
            // Aurelius passive: heal at start of turn
            if (c.getName().equals("Aurelius")) {
                c.AureliusPassiveSkillEffect(c);
            }
            game.ctx.noteHandler.noteCount   = 0;
            game.ctx.combatLog   = "";
            notesRolledThisTurn = false;
            activeSkillUsedThisTurn = false;
            chordUsedThisTurn = null;
            metronomeActivated = false;
            initialDamage = 0;
            finalDamage = 0;
            enemyAttacked = false;
            playerAttacked = false;
        }

        private void endCombat() {
            switch(game.ctx.combatState){
                case DEFEAT:
                    break;
                case VICTORY:
                    Enemy defeated = e;

                    game.ctx.mapEnemies.remove(defeated);
                    if (game.ctx.rooms != null) {
                        for (Room r : game.ctx.rooms) {
                            if (r.getEnemies().remove(defeated)) {
                                if (r.getEnemies().isEmpty()) r.setCleared(true);
                                break;
                            }
                        }
                    }

                    // Level up the player after victory
                    c.defeatedMonster();
                    int monstersDefeated = c.getMonstersDefeated();

                    // Level up logic: level 2 at 1 kill, 3 at 2, 4 at 4, 5 at 7 (adjusted progression)
                    int newLevel = 1;
                    if (monstersDefeated >= 7) newLevel = 5;
                    else if (monstersDefeated >= 4) newLevel = 4;
                    else if (monstersDefeated >= 2) newLevel = 3;
                    else if (monstersDefeated >= 1) newLevel = 2;

                    if (newLevel > c.getLevel()) {
                        c.levelUp(newLevel);
                    }
                    game.ctx.noteHandler.noteCount = 0;
                    game.ctx.combatLog = "";
                    game.ctx.combatState = null; // or whatever your default/idle state is
                    enemyAttacked = false;
                    playerAttacked = false;
                    notesRolledThisTurn = false;
                    activeSkillUsedThisTurn = false;

                    Gdx.app.log("DEBUG", "Switching to ExploringScreen. combatState="
                        + game.ctx.combatState + " enemy=" + game.ctx.currentEnemy);

                    game.setScreen(new ExploringScreen(game));
                    break;
                default:
                    return;

            }
        }

        private String getChordName(String chord) {
            switch (chord) {
                case "CMAJOR": return "C Major";
                case "DMINOR": return "D Minor";
                case "EMINOR": return "E Minor";
                case "FMAJOR": return "F Major";
                case "GMAJOR": return "G Major";
                case "AMINOR": return "A Minor";
                case "BDIM": return "B Diminished";
                default: return "Unknown";
            }
        }

        private String getChordMessage(String chord) {
            Character c = game.ctx.activeCharacterStats;
            switch (chord) {
                case "CMAJOR": { int h = (int)(c.getMaxHp()*0.20f);
                    return "C Major! Healed " + h + " HP."; }
                case "DMINOR":
                    return "D Minor! +20% damage buff.";
                case "EMINOR": { int h = (int)(c.getMaxHp()*0.10f);
                    return "E Minor! Healed "+h+" HP + 10% buff."; }
                case "FMAJOR": return "F Major! +25 shield.";
                case "GMAJOR": { int h = (int)(c.getMaxHp()*0.15f);
                    return "G Major! Healed "+h+" HP + 15 shield."; }
                case "AMINOR": return "A Minor! +35 shield.";
                case "BDIM": { int sd = (int)(c.getMaxHp()*0.10f);
                    return "B Diminished! +30% dmg, lost "+sd+" HP."; }
                default: return "";
            }
        }

        private String noteDisplay() {
            return java.lang.Character.toUpperCase(game.ctx.noteHandler.noteBuffer[0]) + "-"
                + java.lang.Character.toUpperCase(game.ctx.noteHandler.noteBuffer[1]) + "-"
                + java.lang.Character.toUpperCase(game.ctx.noteHandler.noteBuffer[2]);
        }

        private String getInputGuide(int level) {
            switch (level) {
                case 1: return "Input A-C notes (3 total)";
                case 2: return "Input A-E notes (3 total)  |  BACKSPACE to undo";
                default: return "Input A-G notes (3 total)  |  BACKSPACE to undo";
            }
        }

        @Override public void resize(int w, int h) {}
        @Override public void hide()    {}
        @Override public void dispose() {}
    }
