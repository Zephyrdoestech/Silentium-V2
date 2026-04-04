package Screens;

import Mechanics.MapTraversalSystem.Room;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import Entities.Character;
import Entities.Enemy;
import io.github.Zephyrdoestech.GameContext;
import io.github.Zephyrdoestech.Main;
import org.w3c.dom.Text;

/**
 * Turn-based combat screen.
 *
 * Flow:
 *   BATTLE_SCREEN → ENEMY_INTRODUCTION →
 *
 *   (loops until win or loss)
 *   TURN_MENU →
 *     Option 1: ATTACK → ATTACK_FEEDBACK →
 *               DISPLAY_CHORD_EFFECT → DISPLAY_PLAYER_DAMAGE → DISPLAY_FINAL_DAMAGE →
 *               ENEMY_ATTACK → DISPLAY_ENEMY_DAMAGE → (back to TURN_MENU)
 *     Option 2: USE_SKILL → back to TURN_MENU
 *     Option 3: OPEN_INVENTORY → USE_ITEM → back to TURN_MENU
 *
 *   Win:  CHARACTER_POSTCOMBAT_LINE → VICTORY → ExploringScreen
 *   Loss: DEFEAT → ExploringScreen
 */
public class CombatScreen extends BaseScreen {

    // ── Constants ─────────────────────────────────────────────────────────────

    private static final float DISPLAY_TIME = 1.6f;
    private static final float NOTE_DISPLAY_TIME = 1.5f;

    // ── Turn State ────────────────────────────────────────────────────────────

    private boolean notesRolledThisTurn    = false;
    private boolean activeSkillUsedThisTurn = false;
    private boolean activeSkillUsed = false;
    private boolean enemyAttacked          = false;
    private String  chordUsedThisTurn      = null;

    // ── Timers ────────────────────────────────────────────────────────────────

    /** Drives all sprite animations (loops continuously). */
    private float animTimer   = 0f;
    /** Drives timed-message display and dialogue transitions. */
    private float splashTimer = 0f;
    /** Counts down the player's turn time limit. */
    private float turnTime    = 0f;
    /** Maximum seconds the player has per turn (set per map). */
    private float maxTurnTime = 0f;

    private float noteDisplayTimer = 0f;

    // ── Damage Values ─────────────────────────────────────────────────────────

    private int     initialDamage      = 0;
    private int     finalDamage        = 0;
    private boolean metronomeActivated = false;

    // ── Menu State ────────────────────────────────────────────────────────────

    private int           turnMenuSelection = 0;
    private final String[] turnMenuOptions  = { "Attack", "Skill", "Inventory" };

    private int           confirmSelection = 0;
    private final String[] confirmOptions  = { "Use", "Cancel" };

    // ── Entity References ─────────────────────────────────────────────────────

    private Character player;
    private Enemy     enemy;

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

    // ── HUD Panel Regions ─────────────────────────────────────────────────────

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

    // ── Gap / Scale Helper ────────────────────────────────────────────────────

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

        game.gameCamera.position.set(Main.WORLD_WIDTH / 2f, Main.WORLD_HEIGHT / 2f, 0);
        game.gameCamera.update();

        game.ctx.resultTimer  = 0f;
        game.ctx.combatLog    = "";
        game.ctx.chordSystem.resetChords();
        game.ctx.metronome.reset();
        game.ctx.combatState  = GameContext.CombatState.BATTLE_SCREEN;

        animTimer             = 0f;
        splashTimer           = 0f;
        turnTime              = 0f;
        turnMenuSelection     = 0;
        confirmSelection      = 0;
        notesRolledThisTurn   = false;
        activeSkillUsedThisTurn = false;
        enemyAttacked         = false;
        chordUsedThisTurn     = null;
        initialDamage         = 0;
        finalDamage           = 0;
        metronomeActivated    = false;

        // First enemy encountered gets 30% health (tutorial difficulty reduction)
        if (player.getMonstersDefeated() == 0) {
            enemy.setMaxHp((int)(enemy.getMaxHp() * 0.3f));
        }

        // Turn time limit varies by map
        switch (game.ctx.mapName) {
            case TOWN_OF_ECHOES:        maxTurnTime = 15f; break;
            case SILENT_CAVERNS:        maxTurnTime = 20f; break;
            case ABYSS_OF_DISSONANCE:   maxTurnTime = 25f; break;
            default:                    maxTurnTime = 15f; break;
        }
    }

    // =========================================================================
    // Render Entry Point
    // =========================================================================

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        animTimer += delta;

        switch (game.ctx.combatState) {
            case BATTLE_SCREEN:
            case VICTORY:
            case DEFEAT:
                renderSplashScreen(delta);
                break;
            case TUTORIAL:
                // To be implemented
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
        TextureRegion frame = null;

        // If battleIntroAnim is null, immediately transition to ENEMY_INTRODUCTION to avoid NullPointerException.
        // This is a safety measure if the asset is missing or not yet loaded.
        if (game.ctx.combatState == GameContext.CombatState.BATTLE_SCREEN && game.assets.battleIntroAnim == null) {
            game.ctx.combatState = GameContext.CombatState.ENEMY_INTRODUCTION;
            return; // Skip drawing and animation checks for this frame
        }

        switch (game.ctx.combatState) {
            case BATTLE_SCREEN:
                if (game.assets.battleIntroAnim != null) {
                    frame = game.assets.battleIntroAnim.getKeyFrame(splashTimer, false);
                }
                break;
            case VICTORY:
                if (game.assets.victoryAnim != null) {
                    frame = game.assets.victoryAnim.getKeyFrame(splashTimer, false);
                }
                break;
            case DEFEAT:
                if (game.assets.defeatAnim != null) {
                    frame = game.assets.defeatAnim.getKeyFrame(splashTimer, false);
                }
                break;
            default: break;
        }

        if (frame != null) {
            beginUiBatch();
            game.batch.draw(frame,
                (Main.WORLD_WIDTH  - frame.getRegionWidth())  / 2f,
                (Main.WORLD_HEIGHT - frame.getRegionHeight()) / 2f);
            game.batch.end();
        }

        if (game.ctx.combatState == GameContext.CombatState.BATTLE_SCREEN
            && game.assets.battleIntroAnim != null
            && game.assets.battleIntroAnim.isAnimationFinished(splashTimer)) {
            game.ctx.combatState = GameContext.CombatState.ENEMY_INTRODUCTION;
        }

        if (game.ctx.combatState == GameContext.CombatState.VICTORY
            && game.assets.victoryAnim != null
            && game.assets.victoryAnim.isAnimationFinished(splashTimer)) {
            endCombat();
        }

        if (game.ctx.combatState == GameContext.CombatState.DEFEAT
            && game.assets.defeatAnim != null
            && game.assets.defeatAnim.isAnimationFinished(splashTimer)) {
            endCombat();
        }
    }

    // =========================================================================
    // Main Combat Render (all states except splash)
    // =========================================================================

    private void renderCombat(float delta) {
        renderBackground();
        renderEntities();
        renderStats();
        renderNameHeader();
        renderNotesPanel(delta);
        renderTimerPanel(delta);
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
        boolean isPlayerAttacking = game.ctx.combatState == GameContext.CombatState.ATTACK_FEEDBACK;
        boolean isEnemyAttacking  = game.ctx.combatState == GameContext.CombatState.ENEMY_ATTACK;

        TextureRegion playerSprite = resolvePlayerSprite(isPlayerAttacking);
        TextureRegion enemySprite  = resolveEnemySprite(isEnemyAttacking);

        float playerWidth  = 96f;
        float playerHeight = 96f;
        float enemyWidth   = 160f;
        float enemyHeight  = 160f;

        playerSpriteX = screenLeft  + px(4.0f);
        playerSpriteY = screenTop   - px(6.4f);
        enemySpriteX  = screenRight - px(4.0f) - enemyWidth;
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
    private TextureRegion resolvePlayerSprite(boolean isAttacking) {
        if (game.ctx.selectedCharacter == null) return null;
        switch (game.ctx.selectedCharacter) {
            case SONARA:
                return isAttacking
                    ? getSafeFrame(game.assets.sonaraCombatAttack, animTimer)
                    : getSafeFrame(game.assets.sonaraCombatIdle, animTimer);
            case AURELIUS:
                return isAttacking
                    ? getSafeFrame(game.assets.aureliusCombatAttack, animTimer)
                    : getSafeFrame(game.assets.aureliusCombatIdle, animTimer);
            case LYRON:
                return isAttacking
                    ? getSafeFrame(game.assets.lyronCombatAttack, animTimer)
                    : getSafeFrame(game.assets.lyronCombatIdle, animTimer);
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
        switch (enemy.getName()) {
            case "Flesh Feeder":
                return isAttacking
                    ? getSafeFrame(game.assets.fleshfeederCombatAttack, animTimer)
                    : getSafeFrame(game.assets.fleshfeederCombatIdle, animTimer);
            case "Darrylion":
                return isAttacking
                    ? getSafeFrame(game.assets.darrylionCombatAttack, animTimer)
                    : getSafeFrame(game.assets.darrylionCombatIdle, animTimer);
            case "Aryzachnid":
                return isAttacking
                    ? getSafeFrame(game.assets.gobninilCombatAttack, animTimer)
                    : getSafeFrame(game.assets.gobninilCombatIdle, animTimer);
            case "Chimericks":
                return isAttacking
                    ? getSafeFrame(game.assets.chimericksCombatAttack, animTimer)
                    : getSafeFrame(game.assets.chimericksCombatIdle, animTimer);
            case "Labagoliath":
                return isAttacking
                    ? getSafeFrame(game.assets.labagoliathCombatAttack, animTimer)
                    : getSafeFrame(game.assets.labagoliathCombatIdle, animTimer);
            case "Maestro Syozan":
                return isAttacking
                    ? getSafeFrame(game.assets.syozanCombatAttack, animTimer)
                    : getSafeFrame(game.assets.syozanCombatIdle, animTimer);
            default:
                return getSafeFrame(game.assets.fleshfeederCombatIdle, animTimer);
        }
    }

    // =========================================================================
    // Stats Bars (HP / Shield)
    // =========================================================================

    private void renderStats() {
        final float barWidth           = 144f;
        final float barHeight          = 11.8f;
        final float containerWidth     = 180f;
        final float containerHeight    = 32f;
        final float barOffsetX         = -px(1.2f);
        final float hpBarOffsetY       = -px(0.4f);
        final float shieldBarOffsetY   = -px(1.2f);
        final float containerOffsetX   = -27f;
        final float containerOffsetY   = -21f;
        final float textOffsetX        = barWidth + px(0.4f);

        float playerHpBarX     = playerSpriteX + barOffsetX;
        float playerHpBarY     = playerSpriteY + hpBarOffsetY;
        float playerShieldBarX = playerSpriteX + barOffsetX;
        float playerShieldBarY = playerSpriteY + shieldBarOffsetY;
        float enemyHpBarX      = enemySpriteX  + barOffsetX;
        float enemyHpBarY      = enemySpriteY  + hpBarOffsetY;

        game.shapeRenderer.setProjectionMatrix(game.uiCamera.combined);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        drawBar(game.shapeRenderer,
            playerHpBarX, playerHpBarY, barWidth, barHeight,
            (float) player.getHp() / player.getMaxHp(),
            Color.DARK_GRAY, Color.RED);

        drawBar(game.shapeRenderer,
            playerShieldBarX, playerShieldBarY, barWidth, barHeight,
            player.getMaxShield() > 0 ? (float) player.getShield() / player.getMaxShield() : 0f,
            Color.DARK_GRAY, Color.CYAN);

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

    private void renderNameHeader() {
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
                turnTime += delta;
                displayTime = (int) Math.ceil(maxTurnTime - turnTime);
                if (turnTime >= maxTurnTime) {
                    turnTime = 0f;
                    game.ctx.resultTimer = 0f;
                    game.ctx.combatState = GameContext.CombatState.MISSED_TURN;
                }
                break;
            default:
                turnTime    = 0f;
                displayTime = 0;
                break;
        }

        // Countdown number
        float numX = timerPanelLeft + ((timerPanelWidth - textWidth(displayTime + "")) / 2f);
        float numY = timerPanelBottom + px(2.0f);

        beginUiBatch();
        game.assets.font.getData().setScale(1.2f);
        game.assets.font.setColor(displayTime <= 3 ? Color.RED : Color.GREEN);
        game.assets.font.draw(game.batch, displayTime + "", numX, numY);
        game.assets.font.getData().setScale(1.0f);
        game.assets.font.setColor(Color.WHITE);

        // Timer animation sprite
        TextureRegion timerFrame = game.assets.timerAnim.getKeyFrame(animTimer, true);
        float spriteW = px(1.0f);
        float spriteH = px(1.6f);
        float spriteX = timerPanelLeft + ((timerPanelWidth - spriteW) / 2f);
        float spriteY = timerPanelTop  - px(1.0f) - spriteH;
        game.batch.draw(timerFrame, spriteX, spriteY, spriteW, spriteH);

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
                renderAttackFeedback();
                break;
            case USE_SKILL:
                renderSkillMenu();
                break;
            case OPEN_INVENTORY:
                renderInventoryMenu();
                break;
            case USE_ITEM:
                break;
            case SKILL_USED:
            case SKILL_CONFIRMED:
            case MISSED_TURN:
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
            case CHARACTER_POSTCOMBAT_LINE: game.ctx.combatLog = "Insert Post Combat Dialogue Here.";     break;
            default: break;
        }

        beginUiBatch();
        game.assets.font.setColor(Color.valueOf("ddddaa"));
        game.assets.font.getData().setScale(1.6f);
        game.assets.font.draw(game.batch, game.ctx.combatLog,
            actionPanelLeft + px(1.6f), actionPanelTop - px(1.6f));
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
        switch (game.ctx.combatState) {
            case ENEMY_INTRODUCTION:        game.ctx.combatState = GameContext.CombatState.TURN_MENU; break;
            case CHARACTER_POSTCOMBAT_LINE: game.ctx.combatState = GameContext.CombatState.VICTORY;   break;
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
            case SKILL_CONFIRMED:
                return activeSkillDesription();
            case SKILL_USED:
                return (activeSkillUsedThisTurn ? "Skill currently in use." : "Skill already used.") ;
            case DISPLAY_CHORD_EFFECT:
                return chordUsedThisTurn != null
                    ? "[" + game.ctx.chordSystem.getChordName(chordUsedThisTurn) + "] "
                    + game.ctx.chordSystem.getChordMessage(chordUsedThisTurn, player)
                    : "";
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
                return "You received " + game.ctx.enemyDamageDealt + " damage!";
            default:
                return "";
        }
    }

    /** Returns the font colour for the current battle log state. */
    private Color resolveBattleLogColor() {
        switch (game.ctx.combatState) {
            case DISPLAY_CHORD_EFFECT:   return Color.WHITE;
            case SKILL_CONFIRMED:
            case DISPLAY_PLAYER_DAMAGE:
            case DISPLAY_FINAL_DAMAGE:   return Color.GREEN;
            case ENEMY_ATTACK:
            case MISSED_TURN:
            case DISPLAY_ENEMY_DAMAGE:   return Color.RED;
            case SKILL_USED:             return Color.YELLOW;
            default:                     return Color.WHITE;
        }
    }

    /**
     * Drives the state-machine transitions that follow each timed battle log message.
     * Rendering only calls this; all flow logic lives here.
     */
    private void advanceBattleLogState() {
        switch (game.ctx.combatState) {
            case SKILL_USED:
            case SKILL_CONFIRMED:
                game.ctx.combatState = GameContext.CombatState.TURN_MENU;
                break;
            case DISPLAY_CHORD_EFFECT:
                // If there was no chord, skip straight past; otherwise show player damage
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
                game.ctx.combatState = player.isAlive()
                    ? GameContext.CombatState.TURN_MENU
                    : GameContext.CombatState.DEFEAT;
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
                case 0: game.ctx.combatState = GameContext.CombatState.ATTACK;         break;
                case 1:
                    if(activeSkillUsed){
                        game.ctx.combatState = GameContext.CombatState.SKILL_USED;
                    }else{
                        game.ctx.combatState = GameContext.CombatState.USE_SKILL;
                    }
                    confirmSelection = 0;                                           break;
                case 2: game.ctx.combatState = GameContext.CombatState.OPEN_INVENTORY;
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
                game.assets.font.draw(game.batch, "> " + turnMenuOptions[i], labelX, labelY);
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

    private void renderAttackFeedback() {
        // To be implemented
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
            }
            break;
        }
    }

    // =========================================================================
    // Skill Menu
    // =========================================================================

    private void renderSkillMenu() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP)
            || Gdx.input.isKeyJustPressed(Input.Keys.A) || Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            confirmSelection = confirmSelection > 0 ? confirmSelection - 1 : confirmOptions.length - 1;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)
            || Gdx.input.isKeyJustPressed(Input.Keys.D) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP)
            || Gdx.input.isKeyJustPressed(Input.Keys.A) || Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            confirmSelection = confirmSelection > 0 ? confirmSelection - 1 : confirmOptions.length - 1;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)
            || Gdx.input.isKeyJustPressed(Input.Keys.D) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            confirmSelection = confirmSelection < confirmOptions.length - 1 ? confirmSelection + 1 : 0;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (confirmSelection == 0) {
                game.ctx.combatLog = "Item used!"; // Placeholder
            }
            game.ctx.combatState = GameContext.CombatState.TURN_MENU;
            return;
        }

        // ── Measure at correct scales before drawing ──────────────────────────────

        String inventoryPrompt = "No items in inventory."; // Placeholder
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
        game.assets.font.setColor(Color.WHITE);
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
                game.ctx.chordSystem.applyChord(chord, player, initialDamage);
                chordUsedThisTurn = chord;
            }
        }

        enemy.takeDamage(finalDamage);
        player.onDamageDealt(player, enemy, initialDamage); // Lyron passive handled inside Character

        game.ctx.combatState = GameContext.CombatState.DISPLAY_CHORD_EFFECT;
        game.ctx.resultTimer = 0f;
    }

    // =========================================================================
    // Combat Logic — Enemy Attack
    // =========================================================================

    private void executeEnemyAttack() {
        int dmg = enemy.performAttack();
        player.takeDamage(dmg);
        player.onDamageReceived(enemy, dmg); // Sonara passive handled inside Character
        game.ctx.enemyDamageDealt = dmg;
    }

    // =========================================================================
    // Combat Logic — Round Cleanup
    // =========================================================================

    private void finishRound() {
        player.onTurnEnd(player); // Aurelius passive heal handled inside Character
        game.ctx.noteHandler.noteCount = 0;
        game.ctx.combatLog             = "";
        notesRolledThisTurn            = false;
        activeSkillUsedThisTurn        = false;
        chordUsedThisTurn              = null;
        metronomeActivated             = false;
        initialDamage                  = 0;
        finalDamage                    = 0;
        noteDisplayTimer               = 0f;
        enemyAttacked                  = false;
    }

    // =========================================================================
    // Combat End — Victory / Defeat
    // =========================================================================

    private void endCombat() {
        if (game.ctx.combatState == GameContext.CombatState.DEFEAT) {
            // Defeat exit — to be implemented (return to menu / respawn)
            return;
        }

        if (game.ctx.combatState != GameContext.CombatState.VICTORY) return;

        // Remove defeated enemy from the world
        game.ctx.mapEnemies.remove(enemy);
        if (game.ctx.rooms != null) {
            for (Room r : game.ctx.rooms) {
                if (r.getEnemies().remove(enemy)) {
                    if (r.getEnemies().isEmpty()) r.setCleared(true);
                    break;
                }
            }
        }

        // Level-up progression
        player.defeatedMonster();
        int kills    = player.getMonstersDefeated();
        int newLevel = 1;
        if      (kills >= 7) newLevel = 5;
        else if (kills >= 4) newLevel = 4;
        else if (kills >= 2) newLevel = 3;
        else if (kills >= 1) newLevel = 2;

        if (newLevel > player.getLevel()) player.levelUp(newLevel);

        // Reset shared context before leaving
        game.ctx.currentEnemy              = null;
        game.ctx.noteHandler.noteCount     = 0;
        game.ctx.combatLog                 = "";
        game.ctx.combatState               = GameContext.CombatState.NONE;

        game.setScreen(new ExploringScreen(game));
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
