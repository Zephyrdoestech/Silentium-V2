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
    private float hudStaticTopEdge = screenBottomEdge * Main.WORLD_HEIGHT * 0.40f;
    private float hudStaticBottomEdge = screenBottomEdge;
    private float hudStaticWidth = Main.WORLD_WIDTH * 0.30f;
    private float hudStaticHeight = Main.WORLD_HEIGHT * 0.40f;
    private float hudDynamicLeftEdge = screenLeftEdge + Main.WORLD_WIDTH * 0.45f;
    private float hudDynamicRightEdge = screenRightEdge;
    private float hudDynamicTopEdge = screenBottomEdge + Main.WORLD_HEIGHT * 0.40f;
    private float hudBottomEdge = screenBottomEdge;
    private float hudDynamicWidth = Main.WORLD_WIDTH * 0.55f;
    private float hudDynamicHeight = Main.WORLD_HEIGHT * 0.40f;

    // ── Damage Values ────────────────────────────────────────────────────────────────
    private int initialDamage;
    private int finalDamage;
    private boolean metronomeActivated;

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

//    private void renderCombat(float delta){
//        // Render everything
//        // Background (Includes animation for the battle screen)
//        renderCombatDisplay();
//
//        // Player and Enemy Stats Bar
//        renderStatsBar();
//
//        // Player Turn Menu
//        handleTurnMenu();
//
//        // Text pass
//        game.batch.setProjectionMatrix(game.uiCamera.combined);
//        game.batch.begin();
//
//        // Player Stats
//        renderStats();
//
//        // Note Damage Values
//        renderNotesAndMetronome();
//
//        //Rendering Dynamic HUD
//        switch(game.ctx.combatState){
//            case ENEMY_INTRODUCTION:
//                break;
//            case CHARACTER_PRECOMBAT_LINE:
//                break;
//            case TURN_MENU:
//                break;
//            case ATTACK:
//                break;
//            case USE_SKILL:
//                break;
//            case OPEN_INVENTORY:
//                break;
//        }
//    }


    private void renderStateOverlay(float delta) {
        switch (game.ctx.combatState) {
            case ATTACK:
                game.assets.font.setColor(Color.GRAY);
                String inputGuide = getInputGuide(game.ctx.activeCharacterStats.getLevel());

                // We must use a batch for drawing font
                game.batch.setProjectionMatrix(game.uiCamera.combined);
                game.batch.begin();
                game.assets.font.draw(game.batch, inputGuide, 320, 50);
                game.batch.end();
                break;



            case VICTORY:
                game.batch.setProjectionMatrix(game.uiCamera.combined);
                game.batch.begin();
                game.assets.bigFont.setColor(Color.GOLD);
                game.assets.bigFont.draw(game.batch, "VICTORY!", 290, 260);
                game.assets.font.setColor(Color.WHITE);
                game.assets.font.draw(game.batch, "Press ENTER to return to the map.", 255, 200);
                game.batch.end();

                if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) endCombat();
                break;

            case DEFEAT:
                game.batch.setProjectionMatrix(game.uiCamera.combined);
                game.batch.begin();
                game.assets.bigFont.setColor(Color.RED);
                game.assets.bigFont.draw(game.batch, "DEFEATED", 255, 260);
                game.assets.font.setColor(Color.WHITE);
                game.assets.font.draw(game.batch,
                    "Press ENTER to return to the main menu.", 215, 200);
                game.batch.end();

                if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER))
                    game.setScreen(new MainMenuScreen(game));
                break;

            default: break;
        }
    }

    private void renderSplashScreen(float delta){
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
        game.shapeRenderer.setProjectionMatrix(game.uiCamera.combined);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(20f/255f, 30f/255f, 50f/255f, 1f);
        game.shapeRenderer.rect(screenLeftEdge, screenTopEdge - (Main.WORLD_HEIGHT * 0.14f), Main.WORLD_WIDTH * 0.24f, Main.WORLD_HEIGHT * 0.12f);
        game.shapeRenderer.rect(screenRightEdge - (Main.WORLD_WIDTH * 0.20f), screenTopEdge - (Main.WORLD_HEIGHT * 0.14f), Main.WORLD_WIDTH * 0.24f, Main.WORLD_HEIGHT * 0.12f);
        game.shapeRenderer.end();

        game.batch.setProjectionMatrix(game.uiCamera.combined);
        game.batch.begin();
        game.assets.font.getData().setScale(2.0f);
        game.assets.font.setColor(Color.WHITE);
        game.assets.font.draw(game.batch, c.getName(),
            screenLeftEdge + addGap(1.0f), screenTopEdge - addGap(1.0f));
        float textWidth = getStringWidth(e.getName());
        game.assets.font.draw(game.batch, e.getName(),
            screenRightEdge - addGap(1.0f) - textWidth,
            screenTopEdge - addGap(1.0f));
        game.batch.end();
    }

    private void renderStaticHUD(float delta){
        // Background
        game.shapeRenderer.setProjectionMatrix(game.uiCamera.combined);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(180f/255, 190f/255, 200f/255, 1f);
        game.shapeRenderer.rect(hudStaticLeftEdge, hudStaticBottomEdge,
            Main.WORLD_WIDTH * 0.30f, Main.WORLD_HEIGHT * 0.40f);
        game.shapeRenderer.end();

        // Only show beat if not first battle
        game.batch.setProjectionMatrix(game.uiCamera.combined);
        game.batch.begin();
        if (game.ctx.activeCharacterStats.getMonstersDefeated() > 0) {
            game.assets.font.setColor(Color.WHITE);
            game.assets.font.getData().setScale(0.2f);
            game.assets.font.draw(game.batch,
                "Beat: " + game.ctx.metronome.getBeat(),
                hudStaticLeftEdge + addGap(1.0f),
                hudStaticTopEdge - addGap(0.8f));
        }else{
            game.assets.font.setColor(Color.WHITE);
            game.assets.font.draw(game.batch,
                "Metronome not yet unlocked!",
                hudStaticLeftEdge + addGap(1.0f),
                hudStaticTopEdge - addGap(0.8f));
        }

        if(game.ctx.combatState == GameContext.CombatState.ENEMY_INTRODUCTION || game.ctx.combatState == GameContext.CombatState.TURN_MENU){
            // Roll Notes at the start of the Turn
            if (!notesRolledThisTurn) {
                game.ctx.noteHandler.rollNotes();
                notesRolledThisTurn = true;
            }
        }

        game.assets.font.getData().setScale(0.2f);
        game.assets.font.setColor(Color.YELLOW);
        game.assets.font.draw(game.batch,
            "Note Damages:",
            hudStaticLeftEdge + addGap(1.0f),
            hudStaticTopEdge - addGap(1.2f));
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
        float displayX = hudStaticLeftEdge + addGap(1.0f);
        float displayY = hudStaticTopEdge - addGap(1.3f);
        for (int i = 0; i < noteLabels.length; i++) {
            // Only show notes available for player's level
            if (game.ctx.noteHandler.isValidNote(noteLabels[i].charAt(0), c.getLevel())) {
                game.assets.font.setColor(Color.CYAN);
                game.assets.font.draw(game.batch, noteLabels[i], displayX, displayY + (addGap(0.1f) * i));
                game.assets.font.setColor(Color.WHITE);
                game.assets.font.draw(game.batch, String.valueOf(noteDamages[i]), displayX + addGap(1.0f),displayY + (addGap(0.1f) * i));
            }
        }
        game.assets.font.getData().setScale(1.0f); // Reset to small scale
        game.batch.end();
    }



    private void renderTimer(float delta){
        game.shapeRenderer.setProjectionMatrix(game.uiCamera.combined);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(90f/255f, 110f/255f, 130f/255f, 1f);
        game.shapeRenderer.rect(screenLeftEdge + Main.WORLD_WIDTH * 0.30f, screenBottomEdge,
            Main.WORLD_WIDTH * 0.15f, Main.WORLD_HEIGHT * 0.40f);
        game.shapeRenderer.end();

    }

    private void renderDynamicHUD(float delta){
        // Background
        game.shapeRenderer.setProjectionMatrix(game.uiCamera.combined);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(80f/255f, 120f/255f, 160f/255f, 1f);
        game.shapeRenderer.rect(hudDynamicLeftEdge, hudBottomEdge, hudDynamicWidth, hudDynamicHeight);
        game.shapeRenderer.end();

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
        game.assets.font.draw(game.batch, game.ctx.combatLog,
            hudDynamicLeftEdge + (1.6f), hudDynamicTopEdge - (1.6f));
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
                if(chordUsedThisTurn != null){
                    game.ctx.resultTimer += delta;
                    game.batch.setProjectionMatrix(game.uiCamera.combined);
                    game.batch.begin();

                    game.assets.font.getData().setScale(1.2f);
                    game.ctx.combatLog = "[" + getChordName(chordUsedThisTurn) + "] " + getChordMessage(chordUsedThisTurn);
                    // Center Log Display
                    textWidth = getStringWidth(game.ctx.combatLog);
                    XPosition = hudDynamicLeftEdge + ((hudDynamicWidth - textWidth) / 2f);
                    YPosition = hudDynamicHeight / 2f;
                    game.assets.font.draw(game.batch, game.ctx.combatLog,
                        XPosition, YPosition);
                    game.assets.font.getData().setScale(1.0f);
                    game.batch.end();
                    if (game.ctx.resultTimer >= DISPLAY_TIME) {
                        game.ctx.resultTimer = 0f;
                    }
                }

                game.ctx.combatState = GameContext.CombatState.DISPLAY_PLAYER_DAMAGE;
                break;

            case DISPLAY_PLAYER_DAMAGE:
                game.ctx.resultTimer += delta;
                game.batch.setProjectionMatrix(game.uiCamera.combined);
                game.batch.begin();
                game.ctx.combatLog = metronomeActivated ? "Initial": "Total";
                game.ctx.combatLog += " Damage Dealt: " + finalDamage;
                game.assets.font.getData().setScale(1.2f);
                game.assets.font.setColor(Color.GREEN);

                // Center Log Display
                textWidth = getStringWidth(game.ctx.combatLog);
                XPosition = hudDynamicLeftEdge + ((hudDynamicWidth - textWidth) / 2f);
                YPosition = hudDynamicHeight / 2f;
                game.assets.font.draw(game.batch, game.ctx.combatLog,
                    XPosition, YPosition);
                if (game.ctx.resultTimer >= DISPLAY_TIME) {
                    game.ctx.resultTimer = 0f;
                }
                game.batch.end();

                // Additional display for Final Damage
                if (metronomeActivated) {
                    game.ctx.resultTimer += delta;
                    game.batch.setProjectionMatrix(game.uiCamera.combined);
                    game.batch.begin();
                    game.ctx.combatLog = "Beat Sync! Total Damage Dealt: " + finalDamage;
                    game.assets.font.getData().setScale(1.2f);
                    game.assets.font.setColor(Color.GREEN);

                    // Center Log Display
                    textWidth = getStringWidth(game.ctx.combatLog);
                    XPosition = hudDynamicLeftEdge + ((hudDynamicWidth - textWidth) / 2f);
                    YPosition = hudDynamicHeight / 2f;
                    game.assets.font.draw(game.batch, game.ctx.combatLog,
                        XPosition, YPosition);
                    if (game.ctx.resultTimer >= DISPLAY_TIME) {
                        game.ctx.resultTimer = 0f;
                    }
                    game.batch.end();
                }

                if (e.isDefeated()) {
                    game.ctx.combatState = GameContext.CombatState.VICTORY;
                }else{
                    game.ctx.combatState = GameContext.CombatState.ENEMY_ATTACK;
                }
                break;

            case ENEMY_ATTACK:
                doEnemyAttack();
                game.ctx.resultTimer += delta;
                game.batch.setProjectionMatrix(game.uiCamera.combined);
                game.batch.begin();
                game.ctx.combatLog = e.getName() + " used " + e.getLastAttackName();
                game.assets.font.getData().setScale(1.2f);
                game.assets.font.setColor(Color.RED);

                // Center Log Display
                textWidth = getStringWidth(game.ctx.combatLog);
                XPosition = hudDynamicLeftEdge + ((hudDynamicWidth - textWidth) / 2f);
                YPosition = hudDynamicHeight / 2f;
                game.assets.font.draw(game.batch, game.ctx.combatLog,
                    XPosition, YPosition);
                game.assets.font.getData().setScale(1.0f);
                if (game.ctx.resultTimer >= DISPLAY_TIME) {
                    game.ctx.resultTimer = 0f;
                }
                game.batch.end();
                game.ctx.combatState = GameContext.CombatState.DISPLAY_ENEMY_DAMAGE;
                break;

            case DISPLAY_ENEMY_DAMAGE:
                game.ctx.resultTimer += delta;
                game.batch.setProjectionMatrix(game.uiCamera.combined);
                game.batch.begin();
                game.ctx.combatLog = "You received " + game.ctx.enemyDamageDealt + " damage!";
                game.assets.font.getData().setScale(1.2f);
                game.assets.font.setColor(Color.RED);
                game.assets.font.draw(game.batch, game.ctx.combatLog, 200, 75);
                if (game.ctx.resultTimer >= DISPLAY_TIME) {
                    game.ctx.resultTimer = 0f;
                }

                game.ctx.resultTimer += delta;
                game.batch.setProjectionMatrix(game.uiCamera.combined);
                game.batch.begin();
                game.assets.font.setColor(Color.valueOf("ff6666"));
                game.assets.font.draw(game.batch,
                    e.getName()
                        + " used " + e.getLastAttackName()
                        + " for " + e.getLastAttackDmg() + " damage!",
                    30, 60);
                game.batch.end();

                if (game.ctx.resultTimer >= DISPLAY_TIME) {
                    game.ctx.resultTimer = 0f;
                    finishRound();
                }

                if (!c.isAlive()) {
                    game.ctx.combatState = GameContext.CombatState.DEFEAT;
                }else{
                    game.ctx.combatState = GameContext.CombatState.TURN_MENU;
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
                handleCombatInput();
                renderNotesAndMetronome();
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

        game.batch.setProjectionMatrix(game.uiCamera.combined);
        game.batch.begin();
        game.assets.font.getData().setScale(1.6f);
        for (int i = 0; i < turnMenuOptions.length; i++) {
            float textWidth = 0f;
            float XPlacement = 0f;
            float YPlacement = (hudDynamicHeight / 5) * (i + 1);

            if (i == turnMenuSelection) {
                game.assets.font.setColor(Color.YELLOW);
                textWidth = getStringWidth(turnMenuOptions[i]);
                XPlacement = (hudDynamicWidth - textWidth) / 2f;
                game.assets.font.draw(game.batch,
                    "> " + turnMenuOptions[i],
                    XPlacement, YPlacement);
            } else {
                game.assets.font.setColor(Color.WHITE);
                game.assets.font.draw(game.batch,
                    turnMenuOptions[i],
                    XPlacement,YPlacement);
            }
        }
        game.assets.font.getData().setScale(1.0f);
        game.batch.end();
    }

    private float getStringWidth(String s){
        game.batch.begin();
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
        game.shapeRenderer.rect(hudDynamicLeftEdge + 20, hudBottomEdge + 40, 300, Main.WORLD_HEIGHT * 0.30f);
        game.shapeRenderer.end();

        game.batch.setProjectionMatrix(game.uiCamera.combined);
        game.batch.begin();
        game.assets.font.getData().setScale(1.2f);
        game.assets.font.setColor(Color.WHITE);
        game.assets.font.draw(game.batch, "Use Skill?", hudDynamicLeftEdge + 100, hudBottomEdge + 150);

        for (int i = 0; i < confirmOptions.length; i++) {
            if (i == confirmSelection) {
                game.assets.font.setColor(Color.YELLOW);
                game.assets.font.draw(game.batch, "> " + confirmOptions[i], hudDynamicLeftEdge + 50 + (i * 100), hudBottomEdge + 80);
            } else {
                game.assets.font.setColor(Color.WHITE);
                game.assets.font.draw(game.batch, confirmOptions[i], hudDynamicLeftEdge + 70 + (i * 100), hudBottomEdge + 80);
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
        game.shapeRenderer.rect(hudDynamicLeftEdge + 20, hudBottomEdge + 40, 350, Main.WORLD_HEIGHT * 0.30f);
        game.shapeRenderer.end();

        game.batch.setProjectionMatrix(game.uiCamera.combined);
        game.batch.begin();
        game.assets.font.getData().setScale(1.2f);
        game.assets.font.setColor(Color.WHITE);
        game.assets.font.draw(game.batch, "Select an item to use: Use item?", hudDynamicLeftEdge + 30, hudBottomEdge + 150);

        for (int i = 0; i < confirmOptions.length; i++) {
            if (i == confirmSelection) {
                game.assets.font.setColor(Color.YELLOW);
                game.assets.font.draw(game.batch, "> " + confirmOptions[i], hudDynamicLeftEdge + 80 + (i * 100), hudBottomEdge + 80);
            } else {
                game.assets.font.setColor(Color.WHITE);
                game.assets.font.draw(game.batch, confirmOptions[i], hudDynamicLeftEdge + 100 + (i * 100), hudBottomEdge + 80);
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

    private void renderNotesAndMetronome(){
        game.shapeRenderer.setProjectionMatrix(game.uiCamera.combined);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        // Note slot boxes
        for (int i = 0; i < 3; i++) {
            boolean filled = i < game.ctx.noteHandler.noteCount;
            game.shapeRenderer.setColor(filled
                ? new Color(0.2f, 0.5f, 0.9f, 1f)
                : new Color(0.15f, 0.15f, 0.25f, 1f));
            game.shapeRenderer.rect(hudDynamicLeftEdge + 20 + (i * 80), screenBottomEdge + 60, 60, 60);
        }
        game.shapeRenderer.end();

        game.batch.setProjectionMatrix(game.uiCamera.combined);
        game.batch.begin();
        // Note slots (display letters centered in boxes) - bigger font for buffer
        game.assets.font.getData().setScale(2.0f);
        for (int i = 0; i < 3; i++) {
            if (i < game.ctx.noteHandler.noteCount) {
                game.assets.font.setColor(Color.WHITE);
                game.assets.font.draw(game.batch,
                    String.valueOf(java.lang.Character.toUpperCase(game.ctx.noteHandler.noteBuffer[i])),
                    hudDynamicLeftEdge + 45 + (i * 80), screenBottomEdge + 105);
            } else {
                game.assets.font.setColor(new Color(0.4f, 0.4f, 0.55f, 1f));
                game.assets.font.draw(game.batch, "_", hudDynamicLeftEdge + 45 + (i * 80), screenBottomEdge + 105);
            }
        }
        game.assets.font.getData().setScale(1.0f);
        game.batch.end();
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
        game.ctx.combatState = GameContext.CombatState.TURN_MENU;
    }

    private void endCombat() {
        Character c = game.ctx.activeCharacterStats;
        Enemy defeated = game.ctx.currentEnemy;
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
        game.ctx.currentEnemy = null;
        game.ctx.noteHandler.noteCount    = 0;
        game.ctx.combatLog    = "";
        game.setScreen(new ExploringScreen(game));
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
            case 3: return "Input A-G notes (3 total)  |  Form chords for bonuses!";
            default: return "Input A-G notes (3 total)  |  Chords for powerful bonuses!";
        }
    }

    @Override public void resize(int w, int h) {}
    @Override public void hide()    {}
    @Override public void dispose() {}
}
