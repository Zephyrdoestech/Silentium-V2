package Screens;

import Mechanics.MapTraversalSystem.Room;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
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
    private static final float  RESULT_DISPLAY_TIME = 2.2f;
    private boolean             notesRolledThisTurn = false;
    private boolean             activeSkillUsedThisTurn = false;
    private String              chordUsedThisTurn = null; // Track chord for notification
    private float               chordNotificationTimer = 0f;

    public CombatScreen(Main game) { super(game); }

    @Override
    public void show() {
        game.gameCamera.position.set(Main.WORLD_WIDTH / 2f, Main.WORLD_HEIGHT / 2f, 0);
        game.gameCamera.update();
        game.ctx.resultTimer = 0f;
        game.ctx.chordSystem.resetChords();
        game.ctx.metronome.reset();
        notesRolledThisTurn = false;

        // First enemy encountered (monstersDefeated == 0) gets 30% health
        if (game.ctx.activeCharacterStats.getMonstersDefeated() == 0) {
            Enemy e = game.ctx.currentEnemy;
            int originalHp = e.getMaxHp();
            int reducedHp = (int)(originalHp * 0.3f);
            e.setMaxHp(reducedHp);
        }
        activeSkillUsedThisTurn = false;
    }

    // ── Render ────────────────────────────────────────────────────────────────

    @Override
    public void render(float delta) {
        // If player is attacking
        handleCombatInput();

        // Dark background
        game.shapeRenderer.setProjectionMatrix(game.uiCamera.combined);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.05f, 0.05f, 0.1f, 1f);
        game.shapeRenderer.rect(0, 0, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);

        // Player HP bar (moved higher)
        Character c = game.ctx.activeCharacterStats;
        drawBar(game.shapeRenderer, 30, 250, 300, 18,
            (float) c.getHp() / c.getMaxHp(), Color.DARK_GRAY, Color.RED);
        drawBar(game.shapeRenderer, 30, 225, 300, 16,
            c.getMaxShield() > 0 ? (float) c.getShield() / c.getMaxShield() : 0f,
            Color.DARK_GRAY, Color.CYAN);

        // Enemy HP bar
        Enemy e = game.ctx.currentEnemy;
        drawBar(game.shapeRenderer, 450, 240, 300, 18,
            (float) e.getHp() / e.getMaxHp(), Color.DARK_GRAY, Color.RED);

        // Note slot boxes (shape rendering)
        for (int i = 0; i < 3; i++) {
            boolean filled = i < game.ctx.noteHandler.noteCount;
            game.shapeRenderer.setColor(filled
                ? new Color(0.2f, 0.5f, 0.9f, 1f)
                : new Color(0.15f, 0.15f, 0.25f, 1f));
            game.shapeRenderer.rect(30 + i * 80, 114, 60, 60);
        }
        game.shapeRenderer.end();

        // Text pass
        game.batch.setProjectionMatrix(game.uiCamera.combined);
        game.batch.begin();

        // Set small font scale for all text
        game.assets.font.getData().setScale(1.0f);

        // Player stats (left side)
        game.assets.font.setColor(Color.WHITE);
        game.assets.font.draw(game.batch, c.getName() + ":", 30, 280);
        game.assets.font.draw(game.batch, "HP (" + c.getHp() + "):", 30, 260);
        game.assets.font.draw(game.batch, "Shield (" + c.getShield() + "):", 30, 220);

        // Enemy stats (right side)
        game.assets.font.setColor(Color.valueOf("ff6666"));
        game.assets.font.draw(game.batch, e.getName() + ":", 450, 280);
        game.assets.font.draw(game.batch, "HP (" + e.getHp() + "):", 450, 260);

        // Only show beat if not first battle
        if (game.ctx.activeCharacterStats.getMonstersDefeated() > 0) {
            game.assets.font.setColor(Color.WHITE);
            game.assets.font.draw(game.batch,
                "Beat: " + game.ctx.metronome.getBeat(), 340, 187);
        }

        // Note damage values display (only during ATTACK state, after notes are rolled)
        if (game.ctx.combatState == GameContext.CombatState.ATTACK && notesRolledThisTurn) {
            game.assets.font.getData().setScale(1.0f); // Smaller scale for note damages
            game.assets.font.setColor(Color.YELLOW);
            game.assets.font.draw(game.batch, "Note Damages:", 30, 114);
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
            int displayX = 30;
            for (int i = 0; i < noteLabels.length; i++) {
                // Only show notes available for player's level
                if (game.ctx.noteHandler.isValidNote(noteLabels[i].toLowerCase().charAt(0), c.getLevel())) {
                    game.assets.font.setColor(Color.CYAN);
                    game.assets.font.draw(game.batch, noteLabels[i], displayX, 94);
                    game.assets.font.setColor(Color.WHITE);
                    game.assets.font.draw(game.batch, String.valueOf(noteDamages[i]), displayX + 12, 94);
                    displayX += 40;
                }
            }
            game.assets.font.getData().setScale(1.0f); // Reset to small scale
        }

        // Note slots (display letters centered in boxes) - bigger font for buffer
        game.assets.font.getData().setScale(2.0f);
        for (int i = 0; i < 3; i++) {
            if (i < game.ctx.noteHandler.noteCount) {
                game.assets.font.setColor(Color.WHITE);
                game.assets.font.draw(game.batch,
                    String.valueOf(java.lang.Character.toUpperCase(game.ctx.noteHandler.noteBuffer[i])),
                    55 + i * 80, 144);
            } else {
                game.assets.font.setColor(new Color(0.4f, 0.4f, 0.55f, 1f));
                game.assets.font.draw(game.batch, "_", 55 + i * 80, 144);
            }
        }
        game.assets.font.getData().setScale(1.0f); // Reset to small scale

        // Combat log
        game.assets.font.setColor(Color.valueOf("ddddaa"));
        game.assets.font.draw(game.batch, game.ctx.combatLog, 30, 14);

        // State overlay
        renderStateOverlay(delta);

        // Chord Notification (prominent display if active - displayed after overlay to appear on top)
        if (chordUsedThisTurn != null && chordNotificationTimer > 0) {
            chordNotificationTimer -= Gdx.graphics.getDeltaTime();
            game.assets.font.getData().setScale(1.2f);
            game.assets.font.setColor(Color.GOLD);
            String chordNotification = "[" + getChordName(chordUsedThisTurn) + "] " + getChordMessage(chordUsedThisTurn);
            game.assets.font.draw(game.batch, chordNotification, 200, 75);
            game.assets.font.getData().setScale(1.0f);
        }


        game.assets.font.setColor(Color.WHITE);
        game.assets.titleFont.setColor(Color.WHITE);
        game.batch.end();
    }

    private void renderStateOverlay(float delta) {
        switch (game.ctx.combatState) {
            case ATTACK:
                game.assets.font.setColor(Color.GRAY);
                String inputGuide = getInputGuide(game.ctx.activeCharacterStats.getLevel());
                game.assets.font.draw(game.batch, inputGuide, 320, 50);
                break;

            case DISPLAY_PLAYER_DAMAGE:
                game.ctx.resultTimer += delta;
                game.assets.font.setColor(Color.GREEN);
                game.assets.font.draw(game.batch,
                    "You dealt " + game.ctx.playerDamageDealt + " damage!", 30, 60);
                if (game.ctx.resultTimer >= RESULT_DISPLAY_TIME) {
                    game.ctx.resultTimer = 0f;
                    doEnemyAttack();
                }
                break;

            case DISPLAY_ENEMY_DAMAGE:
                game.ctx.resultTimer += delta;
                game.assets.font.setColor(Color.valueOf("ff6666"));
                game.assets.font.draw(game.batch,
                    game.ctx.currentEnemy.getName()
                        + " used " + game.ctx.currentEnemy.getLastAttackName()
                        + " for " + game.ctx.currentEnemy.getLastAttackDmg() + " damage!",
                    30, 60);
                if (game.ctx.resultTimer >= RESULT_DISPLAY_TIME) {
                    game.ctx.resultTimer = 0f;
                    nextPlayerTurn();
                }
                break;

            case VICTORY:
                game.assets.bigFont.setColor(Color.GOLD);
                game.assets.bigFont.draw(game.batch, "VICTORY!", 290, 260);
                game.assets.font.setColor(Color.WHITE);
                game.assets.font.draw(game.batch, "Press ENTER to return to the map.", 255, 200);
                if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) endCombat();
                break;

            case DEFEAT:
                game.assets.bigFont.setColor(Color.RED);
                game.assets.bigFont.draw(game.batch, "DEFEATED", 255, 260);
                game.assets.font.setColor(Color.WHITE);
                game.assets.font.draw(game.batch,
                    "Press ENTER to return to the main menu.", 215, 200);
                if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER))
                    game.setScreen(new MainMenuScreen(game));
                break;

            default: break;
        }
    }

    // ── Note input ────────────────────────────────────────────────────────────

    private void handleCombatInput() {
        if (game.ctx.combatState != GameContext.CombatState.ATTACK) return;

        Character c = game.ctx.activeCharacterStats;

        // Roll notes once per turn at the start
        if (!notesRolledThisTurn) {
            game.ctx.noteHandler.rollNotes();
            notesRolledThisTurn = true;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE) && game.ctx.noteHandler.noteCount > 0) {
            game.ctx.noteHandler.noteCount--;
            return;
        }

        // Active Skill Activation (S key)
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            if (c.getName().equals("Aurelius")) {
                game.ctx.noteHandler.lockNoteDamage();
                game.ctx.combatLog = c.getName() + " used " + "Conservation! Note damages locked.";
            } else if (c.getName().equals("Lyron")) {
                game.ctx.noteHandler.rollNotes();
                game.ctx.combatLog = c.getName() + " used " + "Musical Roulette! Notes rerolled.";
            } else if (c.getName().equals("Sonara")) {
                game.ctx.combatLog = c.getName() + " used " + "Melodic Impromptu! +1 to initial damage.";
            }
            activeSkillUsedThisTurn = true;
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

    private void resolveAttack() {
        Character c = game.ctx.activeCharacterStats;
        int initialDamage = 0;
        for (int i = 0; i < 3; i++) initialDamage += game.ctx.noteHandler.noteDamages[i];

        // Store initial damage before metronome
        int damageBeforeMetronome = initialDamage;

        // Apply metronome only if not first battle
        boolean metronomeActivated = false;
        if (game.ctx.activeCharacterStats.getMonstersDefeated() > 0) {
            initialDamage = game.ctx.metronome.updateBeat(initialDamage);
            metronomeActivated = initialDamage != damageBeforeMetronome;
        }

        // Apply active skill effects only if skill was used this turn
        if (activeSkillUsedThisTurn) {
            initialDamage = c.activeSkillEffect(initialDamage);
        }

        // Apply damage buff
        initialDamage = (int)(initialDamage * (1.0 + c.getDamageBuff()));


        //Apply chords
        String chordMsg = "";
        chordUsedThisTurn = null;
        if (c.getLevel() >= 3) {
            String chord = game.ctx.chordSystem.checkChord(
                java.lang.Character.toUpperCase(game.ctx.noteHandler.noteBuffer[0]),
                java.lang.Character.toUpperCase(game.ctx.noteHandler.noteBuffer[1]),
                java.lang.Character.toUpperCase(game.ctx.noteHandler.noteBuffer[2]));
            if (chord != null) {
                initialDamage = game.ctx.chordSystem.applyChord(chord, c, initialDamage);
                chordMsg = getChordMessage(chord);
                chordUsedThisTurn = chord; // Track chord for notification
                chordNotificationTimer = 3.0f; // Display for 3 seconds
            }
        }

        game.ctx.playerDamageDealt = initialDamage;
        game.ctx.currentEnemy.takeDamage(initialDamage);

        // Lyron passive: gain shield based on damage dealt
        if (c.getName().equals("Lyron")) {
            c.LyronPassiveSkillEffect(c, initialDamage);
        }

        // Build damage message
        String damageMsg;
        if (metronomeActivated) {
            damageMsg = "Initial: " + damageBeforeMetronome + " | Final: " + initialDamage;
        } else {
            damageMsg = "Damage: " + initialDamage;
        }

        game.ctx.combatLog = chordMsg.isEmpty()
            ? "You played " + noteDisplay() + " for " + damageMsg + " damage."
            : "CHORD: " + noteDisplay() + " — " + chordMsg + " (" + damageMsg + ")";

        game.ctx.combatState  = GameContext.CombatState.DISPLAY_PLAYER_DAMAGE;
        game.ctx.resultTimer  = 0f;
    }

    private void doEnemyAttack() {
        Character c = game.ctx.activeCharacterStats;
        if (game.ctx.currentEnemy.isDefeated()) {
            game.ctx.combatState = GameContext.CombatState.VICTORY;
            return;
        }
        int dmg = game.ctx.currentEnemy.performAttack();
        c.takeDamage(dmg);

        // Sonara passive: reflect damage back to enemy
        if (c.getName().equals("Sonara")) {
            c.SonaraPassiveSkillEffect(game.ctx.currentEnemy, dmg);
        }

        game.ctx.enemyDamageDealt = dmg;
        game.ctx.combatState      = GameContext.CombatState.DISPLAY_ENEMY_DAMAGE;
        game.ctx.resultTimer      = 0f;
    }

    private void nextPlayerTurn() {
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
        chordNotificationTimer = 0f;
        game.ctx.combatState = GameContext.CombatState.ATTACK;
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
