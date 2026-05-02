    package Screens;

    import Entities.CharacterHero;
    import Inventory.Consumables.*;
    import Inventory.Inventory;
    import Mechanics.MapTraversalSystem.Room;
    import com.badlogic.gdx.Gdx;
    import com.badlogic.gdx.Input;
    import com.badlogic.gdx.audio.Music;
    import com.badlogic.gdx.audio.Sound;
    import com.badlogic.gdx.graphics.Color;
    import com.badlogic.gdx.graphics.GL20;
    import com.badlogic.gdx.graphics.Texture;
    import com.badlogic.gdx.graphics.g2d.Animation;
    import com.badlogic.gdx.graphics.g2d.TextureRegion;
    import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
    import Entities.Enemy;
    import io.github.Zephyrdoestech.GameContext;
    import io.github.Zephyrdoestech.Main;

    import java.util.HashMap;
    import java.util.Random;

    public class CombatScreen extends BaseScreen {

        // ── Constants ─────────────────────────────────────────────────────────────

        private static final float DISPLAY_TIME = 1.6f;
        private static final float NOTE_DISPLAY_TIME = 1.5f;

        private static final float NOTE_COMPLETE_PAUSE  = 0.6f;
        private static final float NOTE_REVEAL_INTERVAL = 0.35f;

        // ── Turn State ────────────────────────────────────────────────────────────

        private boolean notesRolledThisTurn     = false;
        private boolean activeSkillUsedThisTurn = false;
        private boolean activeSkillUsed         = false;
        private boolean enemyAttacked           = false;
        private boolean chordPlayed             = false;
        private String  chordUsedThisTurn       = null;
        private boolean isPaused                = false;

        // ── Timers ────────────────────────────────────────────────────────────────

        /** Drives all sprite animations (loops continuously). */
        private float   animTimer           = 0f;
        /** Drives timed-message display and dialogue transitions. */
        private float   splashTimer         = 0f;
        /** Counts down the player's turn time limit. */
        private float   turnTime            = 0f;
        private float   additionalTime            = 0f;
        private boolean turnComplete        = false;
        /** Maximum seconds the player has per turn (set per map). */
        private float   maxTurnTime         = 0f;

        private float   noteDisplayTimer    = 0f;

        private int     revealedNoteCount   = 0;
        private float   noteRevealTimer     = 0f;
        private float   stateAnimTimer      = 0f;
        private float   deathAnimTimer      = 0f;
        private GameContext.CombatState lastStateForAnim = GameContext.CombatState.NONE;

        // ── Damage Values ─────────────────────────────────────────────────────────

        private int     initialDamage      = 0;
        private int     finalDamage        = 0;
        private boolean metronomeActivated = false;

        private int     enemyDamage         = 0;

        // ── Menu State ────────────────────────────────────────────────────────────

        private int             turnMenuSelection   = 0;
        private final String[]  turnMenuOptions     = { "Attack", "Skill", "Inventory" };

        private int             confirmSelection    = 0;
        private final String[]  confirmOptions      = { "Use", "Cancel" };

        private int             slotSelected        = 0;
        private Item            selectedItem;

        // ── Entity References ─────────────────────────────────────────────────────

        private CharacterHero   player;
        private Enemy           enemy;

        // ── Inventory and Items ─────────────────────────────────────────────────────

        private Inventory inventory;
        private HashMap<String, Integer> usedItems = new HashMap<>();

        // ── Tutorial Screen  ─────────────────────────────────────────────────────────

        private Texture tutorialScreen = null;

        // ── Screen Layout ─────────────────────────────────────────────────────────

        private final float screenLeft   = 0;
        private final float screenRight  = Main.WORLD_WIDTH;
        private final float screenTop    = Main.WORLD_HEIGHT;
        private final float screenBottom = 0;

        // ── Entity Sprite Positions (set in renderEntities, read in renderStats) ──

        private float playerSpriteX = 0;
        private float playerSpriteY = 0;
        private float enemySpriteX  = 0;
        private float enemySpriteY  = 0;

        // ── Chord & HUD Panel Regions ─────────────────────────────────────────────────────

        private final float notesPanelLeft   = 0;
        private final float notesPanelBottom = 0;
        private final float notesPanelWidth  = Main.WORLD_WIDTH  * 0.30f;
        private final float notesPanelHeight = Main.WORLD_HEIGHT * 0.40f;
        private final float notesPanelTop    = notesPanelBottom + notesPanelHeight;

        private final float timerPanelLeft   = Main.WORLD_WIDTH * 0.30f;
        private final float timerPanelBottom = 0;
        private final float timerPanelWidth  = Main.WORLD_WIDTH * 0.15f;
        private final float timerPanelHeight = Main.WORLD_HEIGHT * 0.40f;
        private final float timerPanelTop    = timerPanelBottom + timerPanelHeight;

        private final float actionPanelLeft   = Main.WORLD_WIDTH * 0.45f;
        private final float actionPanelBottom = 0;
        private final float actionPanelWidth  = Main.WORLD_WIDTH * 0.55f;
        private final float actionPanelHeight = Main.WORLD_HEIGHT * 0.40f;
        private final float actionPanelTop    = actionPanelBottom + actionPanelHeight;

        private float chordContainerWidth  = px(7.0f);
        private float chordContainerHeight = px(1.0f);
        private float chordContainerX      = screenRight - chordContainerWidth - px(1.0f);
        private float chordContainerY      = actionPanelTop + px(0f);

        // ── Audio ─────────────────────────────────────────────────────

        private Music combatBGM;
        private boolean splashSFX = false;

        private Sound noteA;
        private Sound noteB;
        private Sound noteC;
        private Sound noteD;
        private Sound noteE;
        private Sound noteF;
        private Sound noteG;
        private Sound chordAmin;
        private Sound chordBdim;
        private Sound chordCmaj;
        private Sound chordDmin;
        private Sound chordEmin;
        private Sound chordFmaj;
        private Sound chordGmaj;


        // ── Scale / Helpers ────────────────────────────────────────────────────

        private static Random rd = new Random();
        private static final float GAP = 32f;
        private float px(float factor) { return GAP * factor; }

        // ── Constructor ───────────────────────────────────────────────────────────

        public CombatScreen(Main game) { super(game); }

        // =========================================================================
        // Lifecycle
        // =========================================================================

        @Override
        public void show() {
            player = game.ctx.activeCharacterStats;
            enemy  = game.ctx.currentEnemy;

            player.setHp(500);

            //temporary  items
            player.getPlayerInventory().gainCrimsonChorus(game.assets);
            player.getPlayerInventory().gainMajorBlessing(game.assets);
            player.getPlayerInventory().gainMinorsGrace(game.assets);
            player.getPlayerInventory().gainResolvedDissonance(game.assets);
            player.getPlayerInventory().gainTimeOrb(game.assets);
            player.getPlayerInventory().gainSilentBarrier(game.assets);


            game.gameCamera.position.set(Main.WORLD_WIDTH / 2f, Main.WORLD_HEIGHT / 2f, 0);
            game.gameCamera.update();

            game.ctx.combatState  = GameContext.CombatState.BATTLE_SCREEN;
            game.ctx.resultTimer  = 0f;
            game.ctx.combatLog    = "";
            game.ctx.chordSystem.resetChords();
            game.ctx.metronome.reset();

            switch (game.ctx.mapName) {
                case TOWN_OF_ECHOES:        maxTurnTime = 15f; break;
                case SILENT_CAVERNS:        maxTurnTime = 20f; break;
                case ABYSS_OF_DISSONANCE:   maxTurnTime = 25f; break;
                default:                    maxTurnTime = 15f; break;
            }

            turnTime              = maxTurnTime;
            animTimer             = 0f;
            splashTimer           = 0f;
            stateAnimTimer        = 0f;
            deathAnimTimer        = 0f;
            lastStateForAnim      = GameContext.CombatState.NONE;
            additionalTime        = 0f;
            turnMenuSelection     = 0;
            confirmSelection      = 0;
            initialDamage         = 0;
            finalDamage           = 0;
            notesRolledThisTurn   = false;
            activeSkillUsedThisTurn = false;
            turnComplete            = false;
            enemyAttacked         = false;
            metronomeActivated    = false;
            splashSFX             = false;
            chordUsedThisTurn     = null;
            selectedItem          = null;

            inventory = player.getPlayerInventory();
            usedItems.put("Crimson Chorus", 0);
            usedItems.put("Major's Blessing", 0);
            usedItems.put("Minor's Grace", 0);
            usedItems.put("Resolved Dissonance", 0);
            usedItems.put("Silent Barrier", 0);
            usedItems.put("Time Orb", 0);

            // First enemy encountered gets 30% health (tutorial difficulty reduction)
            if (player.getMonstersDefeated() == 0) {
                enemy.setMaxHp((int)(enemy.getMaxHp() * 3.0f));
            }

            if (player.getLevel() <= 3) {
                tutorialScreen = game.assets.tutorials[player.getLevel() - 1];
            }else{
                tutorialScreen = null;
            }

            // Turn time limit varies by map

            prepareAudio();
        }

        // =========================================================================
        // Render Entry Point
        // =========================================================================

        @Override
        public void render(float delta) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
                isPaused = !isPaused;
                if (isPaused) {
                    if (combatBGM != null) combatBGM.pause();
                } else {
                    if (combatBGM != null && game.ctx.combatState != GameContext.CombatState.BATTLE_SCREEN) combatBGM.play();
                }
            }

            if (isPaused) {
                Gdx.gl.glClearColor(0, 0, 0, 1);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

                game.uiCamera.update();
                game.batch.setProjectionMatrix(game.uiCamera.combined);
                game.batch.begin();
                game.assets.font.setColor(Color.WHITE);
                String text = "Paused... press p again to continue";
                game.glyphLayout.setText(game.assets.font, text);
                game.assets.font.draw(game.batch, text, (Main.WORLD_WIDTH - game.glyphLayout.width) / 2f, Main.WORLD_HEIGHT / 2f);
                game.batch.end();
                return;
            }

            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

//            // TEMPORARY MAX HP AND SHIELD
//            player.setHp(player.getMaxHp());
//            player.setShield(player.getMaxShield());

            animTimer += delta;

            switch (game.ctx.combatState) {
                case BATTLE_SCREEN:
                case VICTORY:
                case DEFEAT:
                    renderSplashScreen(delta);
                    break;
                case TUTORIAL:
                    renderTutorial(delta);
                    break;
                default:
                    renderCombat(delta);
                    break;
            }

            game.assets.font.setColor(Color.WHITE);
            game.assets.titleFont.setColor(Color.WHITE);
        }

        // =========================================================================
        // Splash Screens (Battle Intro / Victory / Defeat)
        // =========================================================================

        private void renderSplashScreen(float delta) {
            splashTimer += delta;
            TextureRegion   frame   = null;
            Sound           sfx     = null;

            // If battleIntroAnim is null, immediately transition to ENEMY_INTRODUCTION to avoid NullPointerException.
            // This is a safety measure if the asset is missing or not yet loaded.
            if (game.ctx.combatState == GameContext.CombatState.BATTLE_SCREEN && game.assets.battleIntroAnim == null) {
                game.ctx.combatState = GameContext.CombatState.ENEMY_INTRODUCTION;
                splashTimer = 0f;
                return; // Skip drawing and animation checks for this frame
            }

            switch (game.ctx.combatState) {
                case BATTLE_SCREEN:
                    combatBGM.pause();
                    if (game.assets.battleIntroAnim != null) {
                        frame = game.assets.battleIntroAnim.getKeyFrame(splashTimer, false);
                        sfx = game.assets.enemyEncounter;
                    }
                    break;
                case VICTORY:
                    if (game.assets.victoryAnim != null) {
                        frame = game.assets.victoryAnim.getKeyFrame(splashTimer, false);
                        sfx = game.assets.victory;
                    }
                    break;
                case DEFEAT:
                    if (game.assets.defeatAnim != null) {
                        frame = game.assets.defeatAnim.getKeyFrame(splashTimer, false);
                        sfx = game.assets.defeat;
                    }
                    break;
                default: break;
            }

            if (frame != null && sfx != null) {
                if(!splashSFX) {
                    sfx.play();
                    splashSFX = true;
                }

                beginUiBatch();
                game.batch.draw(frame,
                    (Main.WORLD_WIDTH  - frame.getRegionWidth())  / 2f,
                    (Main.WORLD_HEIGHT - frame.getRegionHeight()) / 2f);
                game.batch.end();
            }

            if (game.ctx.combatState == GameContext.CombatState.BATTLE_SCREEN
                && game.assets.battleIntroAnim.isAnimationFinished(splashTimer)) {
                game.ctx.combatState = GameContext.CombatState.TUTORIAL;
                splashTimer = 0f;
            }

            if (game.ctx.combatState == GameContext.CombatState.VICTORY
                && game.assets.victoryAnim.isAnimationFinished(splashTimer)) {
                endCombat();
            }

            if (game.ctx.combatState == GameContext.CombatState.DEFEAT
                && game.assets.defeatAnim.isAnimationFinished(splashTimer)) {
                endCombat();
            }
        }


        // =========================================================================
        // Tutorial
        // =========================================================================

        private void renderTutorial(float delta){
            if(tutorialScreen == null){
                game.ctx.combatState = GameContext.CombatState.ENEMY_INTRODUCTION;
                splashTimer = 0f;
                return;
            }

            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT);

            // 4. Draw
            game.gameViewport.apply();
            beginUiBatch();

            // Draw Image (letterboxed slightly)
            game.batch.setColor(1f, 1f, 1f, 1f);
            game.batch.draw(tutorialScreen, screenLeft, screenBottom,
                Main.WORLD_WIDTH, Main.WORLD_HEIGHT);
            game.batch.setColor(0, 0, 0, 0.7f * 1f);


            // Draw Skip Hint (Always visible)
            game.assets.font.setColor(Color.GRAY);
            String displayText = "Press ENTER to continue.";
            game.assets.font.draw(game.batch, displayText,
                screenRight - px(2.0f) - textWidth(displayText),
                screenBottom + px(2.0f));

            // Reset colors and draw your overlay
            game.batch.setColor(Color.WHITE);
            game.assets.font.setColor(Color.WHITE);
            drawFadeOverlay();
            game.batch.end();

            // Proceed to combat proper
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                combatBGM.play();
                game.ctx.combatState = GameContext.CombatState.ENEMY_INTRODUCTION;
                splashTimer = 0f;
                return;
            }
        }

        // =========================================================================
        // Main Combat Render (all states except splash)
        // =========================================================================

        private void renderCombat(float delta) {
            renderBackground();
            renderEntities();
            renderStats();
            renderCombatHeader();
            renderNotesPanel(delta);
            renderTimerPanel(delta);
            renderChords();
            renderActionPanel(delta);
        }

        // =========================================================================
        // Background
        // =========================================================================

        private void renderBackground() {
            game.shapeRenderer.setProjectionMatrix(game.uiCamera.combined);
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            game.shapeRenderer.setColor(0.05f, 0.05f, 0.1f, 1f);
            game.shapeRenderer.rect(0, 0, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);
            game.shapeRenderer.end();

            Texture background = game.assets.townCombatBackground;
            if (game.ctx.mapName != null) {
                switch (game.ctx.mapName) {
                    case TOWN_OF_ECHOES:      background = game.assets.townCombatBackground;    break;
                    case SILENT_CAVERNS:      background = game.assets.cavernsCombatBackground; break;
                    case ABYSS_OF_DISSONANCE: background = game.assets.abyssCombatBackground;   break;
                }
            }

            beginUiBatch();
            game.batch.draw(background, 0, 0, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);
            game.batch.end();
        }

        // =========================================================================
        // Entity Sprites
        // =========================================================================

        private void renderEntities() {
            // Reset state animation timer when combat state changes
            if (game.ctx.combatState != lastStateForAnim) {
                stateAnimTimer   = 0f;
                lastStateForAnim = game.ctx.combatState;
            }
            stateAnimTimer += Gdx.graphics.getDeltaTime();

            boolean isPlayerAttacking = game.ctx.combatState == GameContext.CombatState.ATTACK_FEEDBACK;
            boolean isEnemyAttacking  = game.ctx.combatState == GameContext.CombatState.ENEMY_ATTACK;
            boolean isPlayerDead      = !player.isAlive();

            // Only advance death timer when player is dead
            if (isPlayerDead) { deathAnimTimer += Gdx.graphics.getDeltaTime(); }

            TextureRegion playerSprite = resolvePlayerSprite(isPlayerAttacking, isEnemyAttacking, isPlayerDead);
            TextureRegion enemySprite  = resolveEnemySprite(isEnemyAttacking);

            // Player is drawn at 3x its native sprite resolution
            float playerWidth    = playerSprite != null ? playerSprite.getRegionWidth() * 3f : 0f;
            float playerHeight   = playerSprite != null ? playerSprite.getRegionHeight() * 3f : 0f;
            float enemyWidth     = enemySprite != null ? 160f: 0f;
            float enemyHeight    = 160f;

            playerSpriteX = screenLeft  + px(5.0f);
            playerSpriteY = screenTop   - px(6.4f);
            if(isEnemyAttacking){
                if(enemy.getName().equals("Darryllion")
                || enemy.getName().equals("Labagoliath the Void Shaker")
                || enemy.getName().equals("Chimericks")){
                    enemyWidth = 512f;
                    enemySpriteX = playerSpriteX;
                }
            }else{
                enemyWidth = 160f;
                enemySpriteX  = screenRight - px(4.0f) - enemyWidth;
            }
            enemySpriteY  = screenTop   - px(6.4f);

            beginUiBatch();
            if (playerSprite != null) game.batch.draw(playerSprite, playerSpriteX, playerSpriteY, playerWidth,  playerHeight);
            if (enemySprite  != null) game.batch.draw(enemySprite,  enemySpriteX,  enemySpriteY,  enemyWidth,   enemyHeight);
            game.batch.end();
        }

        /**
         * Returns the correct animation frame for the player character.
         * Delegate to assets to avoid a large switch block here.
         */
        private TextureRegion resolvePlayerSprite(boolean isAttacking, boolean isDamaged, boolean isPlayerDead) {
            if (game.ctx.selectedCharacter == null) return null;

            // Death animation plays once and freezes on last frame
            if (isPlayerDead) {
                switch (game.ctx.selectedCharacter) {
                    case SONARA:   return game.assets.sonaraCombatDeath.getKeyFrame(deathAnimTimer, false);
                    case AURELIUS: return game.assets.aureliusCombatDeath.getKeyFrame(deathAnimTimer, false);
                    case LYRON:    return game.assets.lyronCombatDeath.getKeyFrame(deathAnimTimer, false);
                    default:       return null;
                }
            }

            // Damaged animation plays once and freezes on last frame
            if (isDamaged) {
                switch (game.ctx.selectedCharacter) {
                    case SONARA:   return game.assets.sonaraCombatDamaged.getKeyFrame(stateAnimTimer, false);
                    case AURELIUS: return game.assets.aureliusCombatDamaged.getKeyFrame(stateAnimTimer, false);
                    case LYRON:    return game.assets.lyronCombatDamaged.getKeyFrame(stateAnimTimer, false);
                    default:       return null;
                }
            }

            switch (game.ctx.selectedCharacter) {
                case SONARA:
                    // Attack plays once per attack state entry, idle loops continuously
                    return isAttacking
                        ? game.assets.sonaraCombatAttack.getKeyFrame(stateAnimTimer, false)
                        : game.assets.sonaraCombatIdle.getKeyFrame(animTimer, true);
                case AURELIUS:
                    return isAttacking
                        ? game.assets.aureliusCombatAttack.getKeyFrame(stateAnimTimer, false)
                        : game.assets.aureliusCombatIdle.getKeyFrame(animTimer, true);
                case LYRON:
                    return isAttacking
                        ? game.assets.lyronCombatAttack.getKeyFrame(stateAnimTimer, false)
                        : game.assets.lyronCombatIdle.getKeyFrame(animTimer, true);
                default:
                    return null;
            }
        }

        /**
         * Returns the correct animation frame for the current enemy.
         * Centralises the name-to-animation lookup outside of the main render flow.
         */
        private TextureRegion resolveEnemySprite(boolean isAttacking) {
            if (enemy == null || enemy.getName() == null) return null;

            // Check if enemy is in a damage display state
            boolean isDamaged = game.ctx.combatState == GameContext.CombatState.DISPLAY_PLAYER_DAMAGE
                || game.ctx.combatState == GameContext.CombatState.DISPLAY_FINAL_DAMAGE;

            switch (enemy.getName()) {
                case "Flesh Feeder":
                    // MODIFIED: damaged animation plays once on damage states, falls back to idle
                    if (isDamaged && game.assets.fleshfeederCombatDamaged != null)
                        return game.assets.fleshfeederCombatDamaged.getKeyFrame(stateAnimTimer, false);
                    return isAttacking
                        ? game.assets.fleshfeederCombatAttack.getKeyFrame(stateAnimTimer, false)
                        : game.assets.fleshfeederCombatIdle.getKeyFrame(animTimer, true);
                case "Darryllion":
                    if (isDamaged && game.assets.darryllionCombatDamaged != null)
                        return game.assets.darryllionCombatDamaged.getKeyFrame(stateAnimTimer, false);
                    return isAttacking
                        ? game.assets.darryllionCombatAttack1.getKeyFrame(stateAnimTimer, false)
                        : game.assets.darryllionCombatIdle.getKeyFrame(animTimer, true);
                case "Gobninil":
                    if (isDamaged && game.assets.gobninilCombatDamaged != null)
                        return game.assets.gobninilCombatDamaged.getKeyFrame(stateAnimTimer, false);
                    return isAttacking
                        ? game.assets.gobninilCombatAttack.getKeyFrame(stateAnimTimer, false)
                        : game.assets.gobninilCombatIdle.getKeyFrame(animTimer, true);
                case "Chimericks":
                    if (isDamaged && game.assets.chimericksCombatDamaged != null)
                        return game.assets.chimericksCombatDamaged.getKeyFrame(stateAnimTimer, false);
                    return isAttacking
                        ? game.assets.chimericksCombatAttack.getKeyFrame(stateAnimTimer, false)
                        : game.assets.chimericksCombatIdle.getKeyFrame(animTimer, true);
                case "Labagoliath the Void Shaker":
                    if (isDamaged && game.assets.labagoliathCombatDamaged != null)
                        return game.assets.labagoliathCombatDamaged.getKeyFrame(stateAnimTimer, false);
                    return isAttacking
                        ? game.assets.labagoliathCombatAttack.getKeyFrame(stateAnimTimer, false)
                        : game.assets.labagoliathCombatIdle.getKeyFrame(animTimer, true);
                case "Maestro Syozan":
                    if (isDamaged && game.assets.syozanCombatDamaged != null)
                        return game.assets.syozanCombatDamaged.getKeyFrame(stateAnimTimer, false);
                    return isAttacking
                        ? game.assets.syozanCombatAttack.getKeyFrame(stateAnimTimer, false)
                        : game.assets.syozanCombatIdle.getKeyFrame(animTimer, true);
                default:
                    return game.assets.fleshfeederCombatIdle.getKeyFrame(animTimer, true);
            }
        }

        // =========================================================================
        // Stats Bars (HP / Shield)
        // =========================================================================

        private void renderStats() {
            final float basePlayerX = screenLeft  + px(5.0f);
            final float basePlayerY = screenTop   - px(6.4f);
            final float baseEnemyX  = screenRight - px(8.0f);
            final float baseEnemyY  = screenTop   - px(6.4f);

            final float barWidth           = 144f;
            final float barHeight          = 11.8f;
            final float containerWidth     = 180f;
            final float containerHeight    = 32f;
            final float barOffsetX         = -px(1.6f);
            final float hpBarOffsetY       = -px(0.4f);
            final float shieldBarOffsetY   = -px(1.2f);
            final float containerOffsetX   = -27f;
            final float containerOffsetY   = -21f;
            final float textOffsetX        = barWidth + px(0.4f);

            float playerHpBarX     = basePlayerX + barOffsetX;
            float playerHpBarY     = basePlayerY + hpBarOffsetY;
            float playerShieldBarX = basePlayerX + barOffsetX;
            float playerShieldBarY = basePlayerY + shieldBarOffsetY;
            float enemyHpBarX      = baseEnemyX  + barOffsetX;
            float enemyHpBarY      = baseEnemyY  + hpBarOffsetY;

            game.shapeRenderer.setProjectionMatrix(game.uiCamera.combined);
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

            drawBar(game.shapeRenderer,
                playerHpBarX, playerHpBarY, barWidth, barHeight,
                (float) player.getHp() / player.getMaxHp(),
                Color.DARK_GRAY, Color.RED);

            drawBar(game.shapeRenderer,
                playerShieldBarX, playerShieldBarY, barWidth, barHeight,
                player.getMaxShield() > 0 ? (float) player.getShield() / player.getMaxShield() : 0f,
                Color.DARK_GRAY, new Color(0.07f, 0.58f, 0.93f, 1));

            drawBar(game.shapeRenderer,
                enemyHpBarX, enemyHpBarY, barWidth, barHeight,
                (float) enemy.getHp() / enemy.getMaxHp(),
                Color.DARK_GRAY, Color.RED);

            game.shapeRenderer.end();

            beginUiBatch();

            game.batch.draw(game.assets.healthBar,
                playerHpBarX + containerOffsetX, playerHpBarY + containerOffsetY,
                containerWidth, containerHeight);
            game.batch.draw(game.assets.shieldBar,
                playerShieldBarX + containerOffsetX, playerShieldBarY + containerOffsetY,
                containerWidth, containerHeight);
            game.batch.draw(game.assets.healthBar,
                enemyHpBarX + containerOffsetX, enemyHpBarY + containerOffsetY,
                containerWidth, containerHeight);

            game.assets.font.getData().setScale(1.0f);

            game.assets.font.setColor(Color.WHITE);
            game.assets.font.draw(game.batch,
                player.getHp() + " / " + player.getMaxHp(),
                playerHpBarX + textOffsetX, playerHpBarY);
            game.assets.font.draw(game.batch,
                player.getShield() + " / " + player.getMaxShield(),
                playerShieldBarX + textOffsetX, playerShieldBarY);

            game.assets.font.setColor(Color.valueOf("ff6666"));
            game.assets.font.draw(game.batch,
                enemy.getHp() + " / " + enemy.getMaxHp(),
                enemyHpBarX + textOffsetX, enemyHpBarY);

            game.batch.end();
        }

        // =========================================================================
        // Name Header
        // =========================================================================

        private void renderCombatHeader() {
            float bgHeight  = px(4.0f);
            float bgY       = screenTop - px(2.0f);
            float playerBgW = px(4.0f) + textWidth(player.getName());
            float enemyBgW  = px(4.0f) + textWidth(enemy.getName());

            game.shapeRenderer.setProjectionMatrix(game.uiCamera.combined);
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            game.shapeRenderer.setColor(20f / 255f, 30f / 255f, 50f / 255f, 1f);
            game.shapeRenderer.rect(screenLeft, bgY, playerBgW, bgHeight);
            game.shapeRenderer.rect(screenRight - enemyBgW, bgY, enemyBgW, bgHeight);
            game.shapeRenderer.end();

            game.assets.font.getData().setScale(2.0f);
            beginUiBatch();
            game.assets.font.setColor(Color.WHITE);
            game.assets.font.draw(game.batch, player.getName(),
                screenLeft + px(1.0f), screenTop - px(1.0f));
            game.assets.font.draw(game.batch, enemy.getName(),
                screenRight - px(1.0f) - textWidth(enemy.getName()), screenTop - px(1.0f));
            game.batch.end();
            game.assets.font.getData().setScale(1.0f);

            Texture mapHeader = null;
            switch (game.ctx.mapName) {
                case TOWN_OF_ECHOES -> mapHeader = game.assets.mapHeaderTownOfEchoes;
                case SILENT_CAVERNS -> mapHeader = game.assets.mapHeaderSilentCaverns;
                case ABYSS_OF_DISSONANCE -> mapHeader = game.assets.mapHeaderAbyssOfDissonance;
            }
            beginUiBatch();
            game.batch.draw(mapHeader,
                screenLeft + ((Main.WORLD_WIDTH - (mapHeader.getWidth()) * 2) / 2f),
                screenTop - px(1.6f) - mapHeader.getHeight(),
                mapHeader.getWidth() * 2, mapHeader.getHeight() * 2);
            game.batch.end();
        }

        // =========================================================================
        // Notes Panel (left HUD — note damages, beat counter)
        // =========================================================================

        private void renderNotesPanel(float delta) {
            beginUiBatch();
            game.batch.draw(game.assets.staticHudBackground,
                notesPanelLeft, notesPanelBottom, notesPanelWidth, notesPanelHeight);
            game.batch.end();

            float x = notesPanelLeft + px(1.0f);
            float y = notesPanelTop  - px(0.8f);

            // Roll notes at the start of each turn
            if (game.ctx.combatState == GameContext.CombatState.ENEMY_INTRODUCTION
                || game.ctx.combatState == GameContext.CombatState.TURN_MENU) {
                if (!notesRolledThisTurn) {
                    game.ctx.noteHandler.rollNotes();
                    notesRolledThisTurn = true;
                }
            }

            beginUiBatch();
            game.assets.font.getData().setScale(0.8f);
            game.assets.font.setColor(Color.WHITE);

            // Beat counter (locked until first kill)
            if (player.getMonstersDefeated() > 0) {
                game.assets.font.draw(game.batch, "Beat: " + game.ctx.metronome.getBeat(), x, y);
            } else {
                game.assets.font.draw(game.batch, "Metronome not yet unlocked!", x, y);
            }

            y -= px(0.4f);
            game.assets.font.setColor(Color.YELLOW);
            game.assets.font.draw(game.batch, "Note Damages:", x, y);

            String[] noteLabels  = { "A", "B", "C", "D", "E", "F", "G" };
            char[]   noteChars   = { 'a', 'b', 'c', 'd', 'e', 'f', 'g' };
            float    noteX       = notesPanelLeft + px(2.0f);
            float    noteStartY  = y - px(0.64f);

            game.assets.font.getData().setScale(1.0f);
            for (int i = 0; i < noteLabels.length; i++) {
                if (!game.ctx.noteHandler.isValidNote(noteChars[i], player.getLevel())) continue;
                float noteY = noteStartY - (px(0.48f) * i);
                game.assets.font.setColor(Color.WHITE);
                game.assets.font.draw(game.batch, noteLabels[i], noteX, noteY);
                game.assets.font.setColor(Color.GREEN);
                game.assets.font.draw(game.batch,
                    String.valueOf(game.ctx.noteHandler.noteDamage(noteChars[i])),
                    noteX + px(1.6f), noteY);
            }

            game.assets.font.getData().setScale(1.0f);
            game.batch.end();
        }

        // =========================================================================
        // Timer Panel (centre HUD)
        // =========================================================================

        private void renderTimerPanel(float delta) {
            beginUiBatch();
            game.batch.draw(game.assets.timerBackground,
                timerPanelLeft, timerPanelBottom, timerPanelWidth, timerPanelHeight);
            game.batch.end();

            // Advance or reset the turn timer
            int displayTime = 0;
            switch (game.ctx.combatState) {
                case TURN_MENU:
                case ATTACK:
                case USE_SKILL:
                case SKILL_CONFIRMED:
                case SKILL_USED:
                case OPEN_INVENTORY:
                case USE_ITEM:
                case ITEM_USED:
                    if(turnComplete == false){
                        turnTime -= delta;
                    }
                    displayTime = (int) Math.ceil(turnTime);
                    if (turnTime <= 0) {
                        if(game.ctx.selectedCharacter == GameContext.CharacterType.SONARA){
                            activeSkillUsedThisTurn = false;
                            activeSkillUsed = false;}
                        turnTime = 0f;
                        game.ctx.resultTimer = 0f;
                        game.ctx.metronome.reset();
                        game.ctx.combatState = GameContext.CombatState.MISSED_TURN;
                    }
                    break;
                default:
                    turnTime    = maxTurnTime;
                    displayTime = 0;
                    break;
            }

            // Countdown number
            beginUiBatch();
            game.assets.font.getData().setScale(2.0f);
            game.assets.font.setColor(turnComplete? Color.GREEN : (displayTime <= 3 ? Color.RED : Color.WHITE));
            float numX = timerPanelLeft + ((timerPanelWidth - textWidth(displayTime + "")) / 2f);
            float numY = timerPanelBottom + px(2.4f);

            game.assets.font.draw(game.batch, displayTime + "", numX, numY);
            game.assets.font.getData().setScale(1.0f);
            game.assets.font.setColor(Color.WHITE);

            // Timer animation sprite
            TextureRegion timerFrame = game.assets.timerAnim.getKeyFrame(animTimer, true);
            float spriteW = px(1.6f);
            float spriteH = px(2.0f);
            float spriteX = timerPanelLeft + ((timerPanelWidth - spriteW) / 2f);
            float spriteY = timerPanelTop  - px(1.0f) - spriteH;
            game.batch.draw(timerFrame, spriteX, spriteY, spriteW, spriteH);

            game.batch.end();
        }

        // =========================================================================
        // Chord Display (Above Action Panel)
        // =========================================================================

        private void renderChords() {
            if(player.getLevel() < 3) return;

            beginUiBatch();
            int numberOfChords = 7;
            for(int i = 0; i < numberOfChords; i++) {
                Texture chord = game.assets.cMajor;

                switch(i){
                    case 0: chord = (game.ctx.chordSystem.isChordUsed('C') ? game.assets.cMajorUsed : game.assets.cMajor); break;
                    case 1: chord = (game.ctx.chordSystem.isChordUsed('D') ? game.assets.dMinorUsed : game.assets.dMinor); break;
                    case 2: chord = (game.ctx.chordSystem.isChordUsed('E') ? game.assets.eMinorUsed : game.assets.eMinor); break;
                    case 3: chord = (game.ctx.chordSystem.isChordUsed('F') ? game.assets.fMajorUsed : game.assets.fMajor); break;
                    case 4: chord = (game.ctx.chordSystem.isChordUsed('G') ? game.assets.gMajorUsed : game.assets.gMajor); break;
                    case 5: chord = (game.ctx.chordSystem.isChordUsed('A') ? game.assets.aMinorUsed : game.assets.aMinor); break;
                    case 6: chord = (game.ctx.chordSystem.isChordUsed('B') ? game.assets.bDimUsed : game.assets.bDim); break;
                }

                float gap = i * px(1.0f);
                game.batch.draw(chord,
                    chordContainerX + gap, chordContainerY,
                    chordContainerWidth / 7, chordContainerHeight);
            }
            game.batch.end();

        }

        // =========================================================================
        // Action Panel (right HUD — switches by combat state)
        // =========================================================================

        private void renderActionPanel(float delta) {
            beginUiBatch();
            game.batch.draw(game.assets.dynamicHudBackground,
                actionPanelLeft, actionPanelBottom, actionPanelWidth, actionPanelHeight);
            game.batch.end();

            switch (game.ctx.combatState) {
                case ENEMY_INTRODUCTION:
                case CHARACTER_POSTCOMBAT_LINE:
                    renderDialogue(delta);
                    break;
                case TURN_MENU:
                    renderTurnMenu();
                    break;
                case ATTACK:
                    renderAttack(delta);
                    break;
                case ATTACK_FEEDBACK:
                    renderAttackFeedback(delta);
                    break;
                case USE_SKILL:
                    renderSkillMenu();
                    break;
                case OPEN_INVENTORY:
                    renderInventoryMenu();
                    break;
                case USE_ITEM:
                    renderUseItem();
                    break;
                case ITEM_USED:
                case SKILL_USED:
                case SKILL_CONFIRMED:
                case MISSED_TURN:
                case DISPLAY_CHORD:
                case DISPLAY_CHORD_EFFECT:
                case DISPLAY_PLAYER_DAMAGE:
                case DISPLAY_FINAL_DAMAGE:
                case ENEMY_ATTACK:
                case DISPLAY_ENEMY_DAMAGE:
                    renderBattleLog(delta);
                    break;
                default:
                    break;
            }
        }

        // =========================================================================
        // Dialogue (Enemy Introduction / Post-Combat Line)
        // =========================================================================

        private void renderDialogue(float delta) {
            switch (game.ctx.combatState) {
                case ENEMY_INTRODUCTION:        game.ctx.combatLog = enemy.getName() + " encountered!";       break;
                case CHARACTER_POSTCOMBAT_LINE: game.ctx.combatLog = enemy.getName() + " has been defeated!";     break;
                default: break;
            }

            beginUiBatch();
            game.assets.font.setColor(Color.valueOf("ddddaa"));
            game.assets.font.getData().setScale(1.6f);
            game.assets.font.draw(game.batch, game.ctx.combatLog,
                actionPanelLeft + ((actionPanelWidth - textWidth(game.ctx.combatLog)) / 2),
                actionPanelTop - px(2.4f));
            game.batch.end();
            game.assets.font.getData().setScale(1.0f);

            splashTimer += delta;
            if (splashTimer >= DISPLAY_TIME) {
                splashTimer = 0f;
                advanceDialogueState();
            }
        }

        /** Moves to the next state after a dialogue slide finishes. */
        private void advanceDialogueState() {
            game.assets.stateTransition.play(1.0f);
            switch (game.ctx.combatState) {
                case ENEMY_INTRODUCTION:        game.ctx.combatState = GameContext.CombatState.TURN_MENU; break;
                case CHARACTER_POSTCOMBAT_LINE:
                    game.ctx.combatState = GameContext.CombatState.VICTORY;
                    splashSFX = false;
                    break;
                default: break;
            }
        }

        // =========================================================================
        // Battle Log (timed messages for damage / chord / enemy attack results)
        // =========================================================================

        private void renderBattleLog(float delta) {
            game.ctx.resultTimer += delta;

            String  message    = resolveBattleLogMessage();
            Color   color      = resolveBattleLogColor();


            if((game.ctx.combatState == GameContext.CombatState.DISPLAY_CHORD ||
                game.ctx.combatState == GameContext.CombatState.DISPLAY_CHORD_EFFECT)
                && message.equalsIgnoreCase("null")){
                advanceBattleLogState();}
            else if(game.ctx.combatState == GameContext.CombatState.DISPLAY_CHORD){
                if(!chordPlayed){
                    switch(chordUsedThisTurn){
                        case "CMAJOR":
                            chordCmaj.play(3.0f);
                            break;
                        case "DMINOR":
                            chordDmin.play(3.0f);
                            break;
                        case "EMINOR":
                            chordEmin.play(3.0f);
                            break;
                        case "FMAJOR":
                            chordFmaj.play(3.0f);
                            break;
                        case "GMAJOR":
                            chordGmaj.play(3.0f);
                            break;
                        case "AMINOR":
                            chordAmin.play(3.0f);
                            break;
                        case "BDIM":
                            chordBdim.play(3.0f);
                            break;

                    }
                    chordPlayed = true;
                }
            }

            drawCenteredText(message, actionPanelLeft, actionPanelBottom  + px(0.8f), actionPanelWidth,
                actionPanelHeight, color, 1.6f);

            if (game.ctx.resultTimer >= DISPLAY_TIME) {
                game.ctx.resultTimer = 0f;
                advanceBattleLogState();
            }
        }

        /** Returns the display string for the current battle log state. */
        private String resolveBattleLogMessage() {
            switch (game.ctx.combatState) {
                case MISSED_TURN:
                    return "Turn missed! No damage dealt.";
                case ITEM_USED:
                    return selectedItem.getName() + " used.";
                case SKILL_CONFIRMED:
                    return activeSkillDesription();
                case SKILL_USED:
                    return (activeSkillUsedThisTurn ? "Skill currently in use." : "Skill already used.") ;
                case DISPLAY_CHORD:
                    return chordUsedThisTurn != null
                        ? game.ctx.chordSystem.getChordName(chordUsedThisTurn) + " activated!"
                        : "null";
                case DISPLAY_CHORD_EFFECT:
                    return chordUsedThisTurn != null
                        ? game.ctx.chordSystem.getChordMessage(chordUsedThisTurn, player)
                        : "null";
                case DISPLAY_PLAYER_DAMAGE:
                    return (metronomeActivated ? "Initial" : "Total")
                        + " Damage Dealt: "
                        + (metronomeActivated ? initialDamage : finalDamage);
                case DISPLAY_FINAL_DAMAGE:
                    return "Beat Sync! Total Damage Dealt: " + finalDamage;
                case ENEMY_ATTACK:
                    if (!enemyAttacked) { executeEnemyAttack(); enemyAttacked = true; }
                    return enemy.getName() + " used " + enemy.getLastAttackName();
                case DISPLAY_ENEMY_DAMAGE:
                    return "You received " + enemyDamage + " damage!";
                default:
                    return "";
            }
        }

        /** Returns the font colour for the current battle log state. */
        private Color resolveBattleLogColor() {
            switch (game.ctx.combatState) {
                case DISPLAY_CHORD_EFFECT:   return Color.WHITE;
                case ITEM_USED:
                case SKILL_CONFIRMED:
                case DISPLAY_PLAYER_DAMAGE:
                case DISPLAY_FINAL_DAMAGE:   return Color.GREEN;
                case ENEMY_ATTACK:
                case MISSED_TURN:
                case DISPLAY_ENEMY_DAMAGE:   return Color.RED;
                case DISPLAY_CHORD:
                case SKILL_USED:             return Color.YELLOW;
                default:                     return Color.WHITE;
            }
        }

        /**
         * Drives the state-machine transitions that follow each timed battle log message.
         * Rendering only calls this; all flow logic lives here.
         */
        private void advanceBattleLogState() {
            game.assets.stateTransition.play(1.0f);
            switch (game.ctx.combatState) {
                case ITEM_USED:
                    game.ctx.combatState = GameContext.CombatState.OPEN_INVENTORY;
                    break;
                case SKILL_USED:
                case SKILL_CONFIRMED:
                    game.ctx.combatState = GameContext.CombatState.TURN_MENU;
                    break;
                case DISPLAY_CHORD:
                    // If there was no chord, skip straight past; otherwise show player damage
                    game.ctx.combatState = GameContext.CombatState.DISPLAY_CHORD_EFFECT;
                case DISPLAY_CHORD_EFFECT:
                    game.ctx.combatState = GameContext.CombatState.DISPLAY_PLAYER_DAMAGE;
                    break;

                case DISPLAY_PLAYER_DAMAGE:
                    if (metronomeActivated) {
                        game.ctx.combatState = GameContext.CombatState.DISPLAY_FINAL_DAMAGE;
                    } else {
                        game.ctx.combatState = enemy.isDefeated()
                            ? GameContext.CombatState.CHARACTER_POSTCOMBAT_LINE
                            : GameContext.CombatState.ENEMY_ATTACK;
                    }
                    break;

                case DISPLAY_FINAL_DAMAGE:
                    game.ctx.combatState = enemy.isDefeated()
                        ? GameContext.CombatState.CHARACTER_POSTCOMBAT_LINE
                        : GameContext.CombatState.ENEMY_ATTACK;
                    break;
                case MISSED_TURN:
                    game.ctx.combatState = GameContext.CombatState.ENEMY_ATTACK;
                    break;

                case ENEMY_ATTACK:
                    game.ctx.combatState = GameContext.CombatState.DISPLAY_ENEMY_DAMAGE;
                    break;

                case DISPLAY_ENEMY_DAMAGE:
                    finishRound();
                    if (player.isAlive()) {
                        game.ctx.combatState = GameContext.CombatState.TURN_MENU;
                    } else {
                        game.ctx.combatState = GameContext.CombatState.DEFEAT;
                        splashTimer = 0f;
                        splashSFX = false;
                    }
                    break;

                default:
                    break;
            }
        }

        // =========================================================================
        // Turn Menu
        // =========================================================================

        private void renderTurnMenu() {
            // Input
            if (Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                turnMenuSelection = turnMenuSelection > 0 ? turnMenuSelection - 1 : turnMenuOptions.length - 1;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                turnMenuSelection = turnMenuSelection < turnMenuOptions.length - 1 ? turnMenuSelection + 1 : 0;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                switch (turnMenuSelection) {
                    case 0:
                        game.assets.stateTransition.play(0.2f);
                        game.ctx.combatState = GameContext.CombatState.ATTACK;         break;
                    case 1:
                        game.assets.stateTransition.play(0.2f);
                        if(activeSkillUsed){
                            game.ctx.combatState = GameContext.CombatState.SKILL_USED;
                        }else{
                            game.ctx.combatState = GameContext.CombatState.USE_SKILL;
                        }
                        confirmSelection = 0;                                           break;
                    case 2:
                        game.assets.stateTransition.play(0.2f);
                        game.ctx.combatState = GameContext.CombatState.OPEN_INVENTORY;
                        confirmSelection = 0;                                           break;
                }
                return;
            }

            // Draw background
            float menuW = game.assets.turnMenuHud.getWidth()  + px(0.6f);
            float menuH = game.assets.turnMenuHud.getHeight() + px(0.5f);
            float menuX = actionPanelLeft + ((actionPanelWidth  - menuW) / 2f);
            float menuY = actionPanelTop  - (actionPanelHeight / 4f) - menuH + px(0.2f);

            beginUiBatch();
            game.batch.draw(game.assets.turnMenuHud, menuX, menuY, menuW, menuH);
            game.batch.end();

            beginUiBatch();
            game.assets.font.getData().setScale(1.6f);
            // Compute max label width once (for consistent alignment)
            float maxLabelWidth = 0f;
            for (String option : turnMenuOptions) {
                float w = textWidth(option);
                if (w > maxLabelWidth) maxLabelWidth = w;
            }
            float labelX = actionPanelLeft + (actionPanelWidth - maxLabelWidth) / 2f;

            for (int i = 0; i < turnMenuOptions.length; i++) {
                float labelY = actionPanelTop - (actionPanelHeight / 16f * (4f + (i * 3f)));
                if (i == turnMenuSelection) {
                    game.assets.font.setColor(Color.YELLOW);
                    game.assets.font.draw(game.batch, "> " + turnMenuOptions[i] + " <", labelX - px(0.4f), labelY);
                } else {
                    game.assets.font.setColor(Color.WHITE);
                    game.assets.font.draw(game.batch, turnMenuOptions[i], labelX, labelY);
                }
            }
            game.assets.font.getData().setScale(1.0f);
            game.batch.end();
        }

        // =========================================================================
        // Attack Input & Note Slots
        // =========================================================================

        private void renderAttack(float delta) {
            float slotW   = px(2f);
            float slotH   = px(2f);
            float slotGap = px(1f);
            float totalW  = (3f * slotW) + (2f * slotGap);
            float startX  = actionPanelLeft + ((actionPanelWidth - totalW) / 2f);
            float startY  = actionPanelBottom + px(0.4f) + ((actionPanelHeight - slotH) / 2f);

            // Input guide / confirmation message
            boolean notesComplete = game.ctx.noteHandler.noteCount == 3;

            // Note slot containers
            beginUiBatch();
            Texture noteContainer = (notesComplete ? game.assets.noteContainerFilled : game.assets.noteContainer);
            for (int i = 0; i < 3; i++) {
                game.batch.draw(noteContainer,
                    startX + i * (slotW + slotGap), startY,
                    slotW, slotH);
            }
            game.batch.end();

            // Note letters inside slots
            float letterH = px(1.6f);
            float letterY = startY + slotH - ((slotH - letterH) * 1.45f);

            beginUiBatch();
            game.assets.font.getData().setScale(2.0f);
            for (int i = 0; i < 3; i++) {
                String letter;
                if (i < game.ctx.noteHandler.noteCount) {
                    game.assets.font.setColor(Color.WHITE);
                    letter = String.valueOf(java.lang.Character.toUpperCase(game.ctx.noteHandler.noteBuffer[i]));
                } else {
                    game.assets.font.setColor(new Color(0.4f, 0.4f, 0.55f, 1f));
                    letter = "_";
                }
                float letterW = textWidth(letter);
                float gap     = ((slotW - letterW) * 1.45f) + slotGap;
                float lx      = startX + ((slotW - letterW) / 2f) + i * gap;
                game.assets.font.draw(game.batch, letter, lx, letterY);
            }
            game.assets.font.getData().setScale(1.0f);
            game.batch.end();

            if (notesComplete) {
                turnComplete = true;
                noteDisplayTimer += delta;

                String confirmMsg = "Notes locked in! Attacking...";
                game.assets.font.getData().setScale(1.0f);
                float guideX = actionPanelLeft + ((actionPanelWidth - textWidth(confirmMsg)) / 2f);
                float guideY = actionPanelBottom + px(1.8f);

                beginUiBatch();
                game.assets.font.setColor(Color.GREEN);
                game.assets.font.draw(game.batch, confirmMsg, guideX, guideY);
                game.batch.end();

                if (noteDisplayTimer >= NOTE_DISPLAY_TIME) {
                    noteDisplayTimer = 0f;
                    resolveAttack();
                }

            } else {
                noteDisplayTimer = 0f;

                String guide = game.ctx.noteHandler.getInputGuide(player.getLevel());
                game.assets.font.getData().setScale(1.0f);
                float guideX = actionPanelLeft + ((actionPanelWidth - textWidth(guide)) / 2f);
                float guideY = actionPanelBottom + px(1.8f);

                beginUiBatch();
                game.assets.font.setColor(Color.GRAY);
                game.assets.font.draw(game.batch, guide, guideX, guideY);
                game.batch.end();

                handleNoteInput();
            }
        }

        private void renderAttackFeedback(float delta) {
            beginUiBatch();
            game.batch.draw(game.assets.musicStaff,
                actionPanelLeft, actionPanelBottom, actionPanelWidth, actionPanelHeight);
            game.batch.end();

            // Compute Y positions for each note based on their letter (A-G)
            float[] notesY = new float[3];
            float gapY = px(0.35f);
            float baseY = actionPanelBottom + px(2.0f);
            for (int i = 0; i < 3; i++) {
                String letter = String.valueOf(
                    java.lang.Character.toUpperCase(game.ctx.noteHandler.noteBuffer[i]));
                switch (letter) {
                    case "A": notesY[i] = baseY + (gapY * 2); break;
                    case "B": notesY[i] = baseY + (gapY * 3); break;
                    case "C": notesY[i] = baseY + (gapY * 4); break;
                    case "D": notesY[i] = baseY + (gapY * 5); break;
                    case "E": notesY[i] = baseY + (gapY * 6); break;
                    case "F": notesY[i] = baseY             ; break;
                    case "G": notesY[i] = baseY + (gapY * 1); break;
                    default:  notesY[i] = baseY; break;
                }
            }

            float noteWidth  = game.assets.musicNote.getWidth();
            float noteHeight = game.assets.musicNote.getHeight();
            float gapX       = px(2.4f);
            float startX     = actionPanelLeft + px(4.8f);

            // Draw only the notes that have been revealed so far
            beginUiBatch();
            for (int i = 0; i < revealedNoteCount; i++) {
                game.batch.draw(game.assets.musicNote,
                    startX + (i * gapX), notesY[i],
                    noteWidth, noteHeight);
            }
            game.batch.end();

            noteRevealTimer += delta;

            if (revealedNoteCount < 3) {
                // Still revealing notes — advance one note per interval
                if (noteRevealTimer >= NOTE_REVEAL_INTERVAL) {
                    noteRevealTimer = 0f;
                    char noteToPlay = java.lang.Character.toUpperCase(game.ctx.noteHandler.noteBuffer[revealedNoteCount]);
                    switch(noteToPlay){
                        case 'A': noteA.play(3.0f); break;
                        case 'B': noteB.play(3.0f); break;
                        case 'C': noteC.play(3.0f); break;
                        case 'D': noteD.play(3.0f); break;
                        case 'E': noteE.play(3.0f); break;
                        case 'F': noteF.play(3.0f); break;
                        case 'G': noteG.play(3.0f); break;
                    }
                    revealedNoteCount++;
                }
            } else {
                // All three notes shown — wait for the complete pause then move on
                if (noteRevealTimer >= NOTE_COMPLETE_PAUSE) {
                    noteRevealTimer   = 0f;
                    revealedNoteCount = 0;
                    game.ctx.resultTimer = 0f;
                    game.ctx.combatState = GameContext.CombatState.DISPLAY_CHORD;
                }
            }
        }

        // =========================================================================
        // Note Input Handling
        // =========================================================================

        private void handleNoteInput() {
            if (game.ctx.combatState != GameContext.CombatState.ATTACK) return;

            if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE) && game.ctx.noteHandler.noteCount > 0) {
                game.ctx.noteHandler.noteCount--;
                return;
            }

            int[]  keys  = { Input.Keys.A, Input.Keys.B, Input.Keys.C,
                Input.Keys.D, Input.Keys.E, Input.Keys.F, Input.Keys.G };
            char[] notes = { 'a', 'b', 'c', 'd', 'e', 'f', 'g' };
            int    level = player.getLevel();
            int    limit = (level == 1) ? 3 : (level == 2) ? 5 : 7;

            for (int i = 0; i < limit; i++) {
                if (!Gdx.input.isKeyJustPressed(keys[i])) continue;
                if (game.ctx.noteHandler.noteCount >= 3) break;

                char note        = notes[i];
                boolean duplicate = false;
                for (int j = 0; j < game.ctx.noteHandler.noteCount; j++) {
                    if (game.ctx.noteHandler.noteBuffer[j] == note) { duplicate = true; break; }
                }

                if (!duplicate) {
                    game.ctx.noteHandler.noteBuffer[game.ctx.noteHandler.noteCount]  = note;
                    game.ctx.noteHandler.noteDamages[game.ctx.noteHandler.noteCount] = game.ctx.noteHandler.noteDamage(note);
                    game.ctx.noteHandler.noteCount++;

                    note = java.lang.Character.toUpperCase(note);
                    switch(note){
                        case 'A': noteA.play(3.0f); break;
                        case 'B': noteB.play(3.0f); break;
                        case 'C': noteC.play(3.0f); break;
                        case 'D': noteD.play(3.0f); break;
                        case 'E': noteE.play(3.0f); break;
                        case 'F': noteF.play(3.0f); break;
                        case 'G': noteG.play(3.0f); break;
                    }
                }
                break;
            }
        }

        // =========================================================================
        // Skill Menu
        // =========================================================================

        private void renderSkillMenu() {
            if (Gdx.input.isKeyJustPressed(Input.Keys.A) || Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                confirmSelection = confirmSelection > 0 ? confirmSelection - 1 : confirmOptions.length - 1;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.D) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                confirmSelection = confirmSelection < confirmOptions.length - 1 ? confirmSelection + 1 : 0;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                if (confirmSelection == 0) {
                    player.useActiveSkill(game.ctx.noteHandler, game.ctx);
                    activeSkillUsedThisTurn = true;
                    activeSkillUsed = true;
                    game.ctx.combatState = GameContext.CombatState.SKILL_CONFIRMED;
                }else{
                    game.ctx.combatState = GameContext.CombatState.TURN_MENU;
                }
                return;
            }

            // ── Measure at correct scales before drawing ──────────────────────────────

            game.assets.font.getData().setScale(1.2f);
            String skillDescription = activeSkillDesription();
            float descW      = textWidth(skillDescription);
            float descHeight = game.assets.font.getCapHeight();

            game.assets.font.getData().setScale(1.6f);
            float useW      = textWidth("> " + confirmOptions[0]);
            float cancelW   = textWidth(confirmOptions[1]);
            float optionGap = px(3f);
            float totalOptionsW  = useW + optionGap + cancelW;
            float optionHeight   = game.assets.font.getCapHeight();

            // ── Vertical block centred in the action panel ────────────────────────────

            float lineGap     = px(1.5f);
            float blockHeight = descHeight + lineGap + optionHeight;
            float blockStartY = actionPanelBottom + (actionPanelHeight / 2f) + (blockHeight / 2f);

            float descY   = blockStartY;
            float optionY = blockStartY - descHeight - lineGap;

            // ── Horizontal centering ──────────────────────────────────────────────────

            float descX         = actionPanelLeft + ((actionPanelWidth - descW)          / 2f);
            float optionsStartX = actionPanelLeft + ((actionPanelWidth - totalOptionsW)  / 2f);
            float cancelX       = optionsStartX + useW + optionGap;

            // ── Draw ──────────────────────────────────────────────────────────────────

            beginUiBatch();

            game.assets.font.getData().setScale(1.2f);
            game.assets.font.setColor(Color.WHITE);
            game.assets.font.draw(game.batch, skillDescription, descX, descY);

            game.assets.font.getData().setScale(1.6f);

            game.assets.font.setColor(confirmSelection == 0 ? Color.YELLOW : Color.WHITE);
            game.assets.font.draw(game.batch,
                confirmSelection == 0 ? "> " + confirmOptions[0] : confirmOptions[0],
                optionsStartX, optionY);

            game.assets.font.setColor(confirmSelection == 1 ? Color.YELLOW : Color.WHITE);
            game.assets.font.draw(game.batch,
                confirmSelection == 1 ? "> " + confirmOptions[1] : confirmOptions[1],
                cancelX, optionY);

            game.assets.font.getData().setScale(1.0f);
            game.batch.end();
        }

        // =========================================================================
        // Inventory Menu
        // =========================================================================

        private void renderInventoryMenu() {
            beginUiBatch();
            game.batch.draw(game.assets.inventoryBackground,
                actionPanelLeft, actionPanelBottom, actionPanelWidth, actionPanelHeight);
            game.batch.end();


            if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
                game.ctx.combatState = GameContext.CombatState.TURN_MENU;
                return;
            }

            float Xgap = px(0.1f);
            float Ygap = px(0.1f);
            float itemSlotWidth = px(2.2f);
            float itemSlotHeight = px(2.2f);
            float itemSlotsWidth = itemSlotWidth * 5 + (4 * Xgap);
            float itemSlotsHeight = itemSlotHeight * 2 + Ygap;

            // Render Item Slots
            beginUiBatch();
            float itemXPosition = actionPanelLeft + ((actionPanelWidth - itemSlotsWidth) / 2f);
            float itemYPosition = actionPanelTop - ((actionPanelHeight - itemSlotsHeight) / 2f);
            int capacity = inventory.getCapacity();
            int cols = capacity / 2; // 5
            Texture item = game.assets.emptySlotItem;

            for(int i = 0; i < inventory.getCapacity(); i++){
                if(i < inventory.getInventorySize()){ item = inventory.getItem(i).getSlotIcon(); }
                else{ item = game.assets.emptySlotItem; }

                int col = i % cols;
                int row = i / cols;
                game.batch.draw(item,
                    itemXPosition + col * (itemSlotWidth + Xgap),
                    itemYPosition - itemSlotHeight - row * (itemSlotHeight + Ygap),
                    itemSlotWidth,
                    itemSlotHeight
                );
            }

            game.batch.end();


            // Select Item Controls
            if (Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                slotSelected = (slotSelected >= capacity / 2 && slotSelected < capacity
                    ? slotSelected - (capacity / 2) : slotSelected);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                slotSelected = (slotSelected < capacity / 2 && slotSelected >= 0
                    ? slotSelected + (capacity / 2) : slotSelected);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.A) || Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                slotSelected = (slotSelected % (capacity / 2) > 0
                    ? slotSelected - 1: slotSelected);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.D) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                slotSelected = (slotSelected % (capacity / 2) < capacity - 1
                    ? slotSelected + 1: slotSelected);
            }

            // Render Selected Item
            item = game.assets.selectedSlotItem;
            beginUiBatch();
            int col = slotSelected % cols;
            int row = slotSelected / cols;

            game.batch.draw(item,
                itemXPosition + col * (itemSlotWidth + Xgap),
                itemYPosition - itemSlotHeight - row * (itemSlotHeight + Ygap),
                itemSlotWidth,
                itemSlotHeight
            );
            game.batch.end();


            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                // Route to renderUseItem
                //reset slot selected to zero after turn and after usage/cancel
                if(slotSelected >= 0 && slotSelected < inventory.getInventorySize()){
                    selectedItem = inventory.getItem(slotSelected);
                    game.ctx.combatState = GameContext.CombatState.USE_ITEM;
                }else {
                    selectedItem = null;
                }
            }
        }

        // =========================================================================
        // Inventory Menu
        // =========================================================================

        private void renderUseItem() {
            if (Gdx.input.isKeyJustPressed(Input.Keys.A) || Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                confirmSelection = confirmSelection > 0 ? confirmSelection - 1 : confirmOptions.length - 1;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.D) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                confirmSelection = confirmSelection < confirmOptions.length - 1 ? confirmSelection + 1 : 0;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                if (confirmSelection == 0) {
                    handleItemUse();
                    game.ctx.combatState = GameContext.CombatState.ITEM_USED;
                }else{
                    game.ctx.combatState = GameContext.CombatState.OPEN_INVENTORY;
                }
                return;
            }

            // ── Measure at correct scales before drawing ──────────────────────────────

            String inventoryPrompt = "Use [ " +  selectedItem.getName() + " ] ?"; // Placeholder
            game.assets.font.getData().setScale(1.2f);
            float descW      = textWidth(inventoryPrompt);
            float descHeight = game.assets.font.getCapHeight();

            game.assets.font.getData().setScale(1.6f);
            float useW         = textWidth("> " + confirmOptions[0]);
            float cancelW      = textWidth(confirmOptions[1]);
            float optionGap    = px(3f);
            float totalOptionsW = useW + optionGap + cancelW;
            float optionHeight  = game.assets.font.getCapHeight();

            // ── Vertical block centred in the action panel ────────────────────────────

            float lineGap     = px(1.5f);
            float blockHeight = descHeight + lineGap + optionHeight;
            float blockStartY = actionPanelBottom + (actionPanelHeight / 2f) + (blockHeight / 2f);

            float descY   = blockStartY;
            float optionY = blockStartY - descHeight - lineGap;

            // ── Horizontal centering ──────────────────────────────────────────────────

            float descX         = actionPanelLeft + ((actionPanelWidth - descW)         / 2f);
            float optionsStartX = actionPanelLeft + ((actionPanelWidth - totalOptionsW) / 2f);
            float cancelX       = optionsStartX + useW + optionGap;

            // ── Draw ──────────────────────────────────────────────────────────────────

            beginUiBatch();

            game.assets.font.getData().setScale(1.2f);
            game.assets.font.setColor(Color.YELLOW);
            game.assets.font.draw(game.batch, inventoryPrompt, descX, descY);

            game.assets.font.getData().setScale(1.6f);

            game.assets.font.setColor(confirmSelection == 0 ? Color.YELLOW : Color.WHITE);
            game.assets.font.draw(game.batch,
                confirmSelection == 0 ? "> " + confirmOptions[0] : confirmOptions[0],
                optionsStartX, optionY);

            game.assets.font.setColor(confirmSelection == 1 ? Color.YELLOW : Color.WHITE);
            game.assets.font.draw(game.batch,
                confirmSelection == 1 ? "> " + confirmOptions[1] : confirmOptions[1],
                cancelX, optionY);

            game.assets.font.getData().setScale(1.0f);
            game.batch.end();
        }

        private void handleItemUse(){
            String  itemName    = selectedItem.getName();
            int     itemInEffect = selectedItem.getTracker();

            int     effectTracker = usedItems.get(itemName) + itemInEffect;
            usedItems.put(itemName, effectTracker);

            if (itemName.equals("Time Orb")
                || itemName.equals("Major's Blessing")
                || itemName.equals("Minor's Grace")) {
                handleItemEffects(selectedItem, null);
            }

            inventory.removeItem(slotSelected);
        }

        private void handleItemEffects(Item item, String chord) {
            String itemName      = item.getName();
            int    effectTracker = usedItems.get(itemName);

            // Exit early if item is not active — don't decrement
            if (effectTracker <= 0) return;

            usedItems.put(itemName, effectTracker - 1);

            Random rand = new Random();
            int chordNum;
            switch (itemName) {
                case "Major's Blessing":
                    boolean majorsBlessingUsed = false;
                    while(!majorsBlessingUsed){
                        chordNum = rand.nextInt(0,4);
                        if(!game.ctx.chordSystem.isChordUsed('C')
                            && !game.ctx.chordSystem.isChordUsed('F')
                            && !game.ctx.chordSystem.isChordUsed('G'))
                            break;

                        switch(chordNum){
                            case 0:
                                if(game.ctx.chordSystem.isChordUsed('C')){
                                    game.ctx.chordSystem.resetChord("CMAJOR");
                                    majorsBlessingUsed=true;
                                }
                                break;
                            case 1:
                                if(game.ctx.chordSystem.isChordUsed('F')){
                                    game.ctx.chordSystem.resetChord("FMAJOR");
                                    majorsBlessingUsed=true;
                                }
                                break;
                            case 2:
                                if(game.ctx.chordSystem.isChordUsed('G')){
                                    game.ctx.chordSystem.resetChord("GMAJOR");
                                    majorsBlessingUsed=true;
                                }
                                break;
                        }
                    }
                    break;
                case "Minor's Grace":
                    boolean minorsGraceUsed = false;
                    while(!minorsGraceUsed){
                        chordNum = rand.nextInt(0,4);
                        if(!game.ctx.chordSystem.isChordUsed('D')
                            && !game.ctx.chordSystem.isChordUsed('E')
                            && !game.ctx.chordSystem.isChordUsed('A'))
                            break;

                        switch(chordNum){
                            case 0:
                                if(game.ctx.chordSystem.isChordUsed('D')){
                                    game.ctx.chordSystem.resetChord("DMINOR");
                                    minorsGraceUsed=true;
                                }
                                break;
                            case 1:
                                if(game.ctx.chordSystem.isChordUsed('E')){
                                    game.ctx.chordSystem.resetChord("EMINOR");
                                    minorsGraceUsed=true;
                                }
                                break;
                            case 2:
                                if(game.ctx.chordSystem.isChordUsed('A')){
                                    game.ctx.chordSystem.resetChord("AMINOR");
                                    minorsGraceUsed=true;
                                }
                                break;
                        }
                    }
                    break;
                case "Resolved Dissonance":
                    if (chord != null && chord.equals("BDIM")) {
                        int hpLoss = (int)(player.getMaxHp() * 0.1);
                        player.setHp(player.getHp() + hpLoss);
                    }
                    break;
                case "Silent Barrier":
                    enemyDamage = 0;
                    break;
                case "Time Orb":
                    turnTime += 15f;
                    break;
            }
        }
        // =========================================================================
        // Combat Logic — Attack Resolution
        // =========================================================================

        /**
         * Called when the player has entered 3 notes.
         * Computes all damage values, applies effects in the correct order,
         * and transitions to DISPLAY_CHORD_EFFECT.
         *
         * Order of operations:
         *   1. Sum note damages
         *   2. Apply active skill modifier (if used this turn)
         *   3. Apply metronome beat multiplier
         *   4. Apply flat damage buff
         *   5. Check and apply chord effects (HP / shield / buff side effects)
         *   6. Deal finalDamage to enemy
         *   7. Trigger Lyron passive (shield on damage dealt)
         */
        private void resolveAttack() {
            initialDamage = 0;
            for (int i = 0; i < 3; i++) initialDamage += game.ctx.noteHandler.noteDamages[i];

            if (activeSkillUsedThisTurn) {
                initialDamage = player.activeSkillEffect(initialDamage);
            }

            finalDamage        = initialDamage;
            metronomeActivated = false;

            if (player.getMonstersDefeated() > 0) {
                int boosted = game.ctx.metronome.updateBeat(initialDamage);
                metronomeActivated = boosted != initialDamage;
                finalDamage        = boosted;
            }

            finalDamage = (int)(finalDamage * (1.0 + player.getDamageBuff()));

            // Chord check (level 3+); chord side effects (healing, shield, buff) applied here,
            // chord modifies initialDamage only — finalDamage already computed above is kept.
            chordUsedThisTurn = null;
            if (player.getLevel() >= 3) {
                String chord = game.ctx.chordSystem.checkChord(
                    java.lang.Character.toUpperCase(game.ctx.noteHandler.noteBuffer[0]),
                    java.lang.Character.toUpperCase(game.ctx.noteHandler.noteBuffer[1]),
                    java.lang.Character.toUpperCase(game.ctx.noteHandler.noteBuffer[2]));
                if (chord != null) {
                    finalDamage = game.ctx.chordSystem.applyChord(chord, player, finalDamage);
                    chordUsedThisTurn = chord;

                    handleItemEffects(new MinorsGrace(game.assets), chord);
                    handleItemEffects(new MajorsBlessing(game.assets), chord);
                    handleItemEffects(new ResolvedDissonance(game.assets), chord);
                }
            }

            if (usedItems.get("Crimson Chorus") > 0) {
                float extraDamage = new CrimsonChorus(game.assets).getExtraDamage() / 100f;
                finalDamage = (int)(finalDamage * (1.0f + extraDamage));
                usedItems.put("Crimson Chorus", usedItems.get("Crimson Chorus") - 1);
            }

            enemy.takeDamage(finalDamage);

            player.onDamageDealt(player, enemy, initialDamage); // Lyron passive handled inside CharacterHero

            revealedNoteCount    = 0;
            noteRevealTimer      = 0f;
            game.ctx.combatState = GameContext.CombatState.ATTACK_FEEDBACK;
            game.ctx.resultTimer = 0f;
        }

        // =========================================================================
        // Combat Logic — Enemy Attack
        // =========================================================================

        private void executeEnemyAttack() {
            enemyDamage = enemy.performAttack();

            // Silent Barrier check is handled inside handleItemEffects
            handleItemEffects(new SilentBarrier(game.assets), null);

            player.takeDamage(enemyDamage);
            player.onDamageReceived(enemy, enemyDamage);
        }

        // =========================================================================
        // Combat Logic — Round Cleanup
        // =========================================================================

        private void finishRound() {
            player.onTurnEnd(player); // Aurelius passive heal handled inside CharacterHero
            game.ctx.noteHandler.noteCount = 0;
            game.ctx.combatLog             = "";
            notesRolledThisTurn            = false;
            activeSkillUsedThisTurn        = false;
            chordUsedThisTurn              = null;
            metronomeActivated             = false;
            turnComplete                   = false;
            chordPlayed                    = false;
            revealedNoteCount              = 0;
            noteRevealTimer                = 0f;
            initialDamage                  = 0;
            finalDamage                    = 0;
            noteDisplayTimer               = 0f;
            additionalTime                 = 0f;
            enemyAttacked                  = false;
        }

        // =========================================================================
        // Combat End — Victory / Defeat
        // =========================================================================

        private void endCombat() {
            if (game.ctx.combatState == GameContext.CombatState.DEFEAT) {
                // Defeat exit — to be implemented (return to menu / respawn)
                game.ctx.playerDefeated = true;
                switch (game.ctx.mapName) {
                    case TOWN_OF_ECHOES:
                        game.setScreen(new TownOfEchoesScreen(game));
                        break;
                    case SILENT_CAVERNS:
                        game.setScreen(new SilentCavernsScreen(game));
                        break;
                    case ABYSS_OF_DISSONANCE:
                        game.setScreen(new AbyssOfDissonanceScreen(game));
                        break;
                    default:
                        game.setScreen(game.ctx.currentMapScreen); // Default to Town
                        break;
                }
                return;
            }

            if (game.ctx.combatState != GameContext.CombatState.VICTORY) return;

            // Remove defeated enemy from the world
            game.ctx.mapEnemies.remove(game.ctx.currentEnemy);
            if (game.ctx.rooms != null) {
                for (Room r : game.ctx.rooms) {
                    if (r.getEnemies().remove(game.ctx.currentEnemy)) {
                        if (r.getEnemies().isEmpty()) r.setCleared(true);
                        break;
                    }
                }
            }

            // Reset shared context before leaving
            player.resetDamageBuff();
            game.ctx.currentEnemy              = null;
            game.ctx.noteHandler.noteCount     = 0;
            game.ctx.combatLog                 = "";
            game.ctx.combatState               = GameContext.CombatState.NONE;

            // Level-up progression
            player.defeatedMonster();
            int kills    = player.getMonstersDefeated();
            int newLevel = 1;
            if      (kills >= 7) newLevel = 5;
            else if (kills >= 4) newLevel = 4;
            else if (kills >= 2) newLevel = 3;
            else if (kills >= 1) newLevel = 2;

            if (newLevel > player.getLevel()) player.levelUp(newLevel);

            game.assets.stopAllMusic();

            switch (game.ctx.mapName) {
                case TOWN_OF_ECHOES:
                    game.setScreen(new TownOfEchoesScreen(game));
                    break;
                case SILENT_CAVERNS:
                    game.setScreen(new SilentCavernsScreen(game));
                    break;
                case ABYSS_OF_DISSONANCE:
                    game.setScreen(new AbyssOfDissonanceScreen(game));
                    break;
                default:
                    game.setScreen(game.ctx.currentMapScreen); // Default to Town
                    break;
            }
        }

        // =========================================================================
        // Prepare Audio Assets
        // =========================================================================

        private void prepareAudio() {

            switch(game.ctx.mapName){
                case TOWN_OF_ECHOES:
                    combatBGM = game.assets.battleTownBGM;
                    break;
                case SILENT_CAVERNS:
                    combatBGM = game.assets.battleCavernsBGM;
                    break;
                case ABYSS_OF_DISSONANCE:
                    combatBGM = game.assets.battleAbyssBGM;
                    break;
                default:
                    combatBGM = game.assets.battleBossBGM;
                    break;
            }

            if (combatBGM != null && !combatBGM.isPlaying()) {
                combatBGM.setVolume(0.1f); // 0.0f to 1.0f
                combatBGM.play();
            }

            switch(game.ctx.selectedCharacter){
                case SONARA:
                    noteA = game.assets.noteAttackBanjoA;
                    noteB = game.assets.noteAttackBanjoB;
                    noteC = game.assets.noteAttackBanjoC;
                    noteD = game.assets.noteAttackBanjoD;
                    noteE = game.assets.noteAttackBanjoE;
                    noteF = game.assets.noteAttackBanjoF;
                    noteG = game.assets.noteAttackBanjoG;

                    chordAmin = game.assets.chordAttackBanjoAmin;
                    chordBdim = game.assets.chordAttackBanjoBdim;
                    chordCmaj = game.assets.chordAttackBanjoCmaj;
                    chordDmin = game.assets.chordAttackBanjoDmin;
                    chordEmin = game.assets.chordAttackBanjoEmin;
                    chordFmaj = game.assets.chordAttackBanjoFmaj;
                    chordGmaj = game.assets.chordAttackBanjoGmaj;
                    break;
                case AURELIUS:
                    noteA = game.assets.noteAttackFluteA;
                    noteB = game.assets.noteAttackFluteB;
                    noteC = game.assets.noteAttackFluteC;
                    noteD = game.assets.noteAttackFluteD;
                    noteE = game.assets.noteAttackFluteE;
                    noteF = game.assets.noteAttackFluteF;
                    noteG = game.assets.noteAttackFluteG;

                    chordAmin = game.assets.chordAttackFluteAmin;
                    chordBdim = game.assets.chordAttackFluteBdim;
                    chordCmaj = game.assets.chordAttackFluteCmaj;
                    chordDmin = game.assets.chordAttackFluteDmin;
                    chordEmin = game.assets.chordAttackFluteEmin;
                    chordFmaj = game.assets.chordAttackFluteFmaj;
                    chordGmaj = game.assets.chordAttackFluteGmaj;
                    break;
                case LYRON:
                    noteA = game.assets.noteAttackHarpA;
                    noteB = game.assets.noteAttackHarpB;
                    noteC = game.assets.noteAttackHarpC;
                    noteD = game.assets.noteAttackHarpD;
                    noteE = game.assets.noteAttackHarpE;
                    noteF = game.assets.noteAttackHarpF;
                    noteG = game.assets.noteAttackHarpG;

                    chordAmin = game.assets.chordAttackHarpAmin;
                    chordBdim = game.assets.chordAttackHarpBdim;
                    chordCmaj = game.assets.chordAttackHarpCmaj;
                    chordDmin = game.assets.chordAttackHarpDmin;
                    chordEmin = game.assets.chordAttackHarpEmin;
                    chordFmaj = game.assets.chordAttackHarpFmaj;
                    chordGmaj = game.assets.chordAttackHarpGmaj;
                    break;
            }
        }

        // =========================================================================
        // Render Helpers
        // =========================================================================

        /** Sets the batch projection matrix to the UI camera and begins a batch session. */
        private void beginUiBatch() {
            game.batch.setProjectionMatrix(game.uiCamera.combined);
            game.batch.begin();
        }

        /**
         * Draws text centred horizontally within an area, at a given Y, with the
         * specified colour and font scale. Restores scale to 1.0 afterwards.
         */
        private void drawCenteredText(
            String text, float areaX, float areaY, float areaWidth,
            float areaHeight, Color color, float scale) {

            beginUiBatch();
            game.assets.font.getData().setScale(scale);
            game.assets.font.setColor(color);
            float x = areaX + ((areaWidth - textWidth(text)) / 2f);
            float y = areaY + areaHeight / 2f;
            game.assets.font.draw(game.batch, text, x, y);
            game.assets.font.getData().setScale(1.0f);
            game.batch.end();
        }

        /** Measures the pixel width of a string using the current font. */
        private float textWidth(String text) {
            game.glyphLayout.setText(game.assets.font, text);
            return game.glyphLayout.width;
        }

        /** Prompt Description for Active Skill Menu */
        private String activeSkillDesription(){
            String description = "";
            switch(game.ctx.selectedCharacter){
                case SONARA:
                    description = (activeSkillUsed ? "Initial Damage + (1) Point.": "Add (1) point to initial note damage.");
                    break;
                case AURELIUS:
                    description = (activeSkillUsed ? "Damage Preserved.": "Preserve notes' current damage for next turn.");
                    break;
                case LYRON:
                    description = (activeSkillUsed ? "Damage Rerolled.": "Reroll notes' current damage.");
                    description = "";
                    break;
            }

            return description;
        }

        /** Returns a frame from an animation, or null if the animation itself is null. */
        private TextureRegion getSafeFrame(Animation<TextureRegion> anim, float timer) {
            return anim != null ? anim.getKeyFrame(timer, true) : null;
        }

        // =========================================================================
        // BaseScreen Overrides
        // =========================================================================

        @Override public void resize(int w, int h) {}
        @Override public void hide()               {}
        @Override public void dispose()            {}
    }
