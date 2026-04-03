package Screens;

import Mechanics.Room;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import Entities.Character;
import Entities.Enemy;
import io.github.Zephyrdoestech.GameContext;
import io.github.Zephyrdoestech.Main;

import java.util.Arrays;
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

    public CombatScreen(Main game) { super(game); }

    @Override
    public void show() {
        game.gameCamera.position.set(Main.WORLD_WIDTH / 2f, Main.WORLD_HEIGHT / 2f, 0);
        game.gameCamera.update();
        game.ctx.resultTimer = 0f;
    }

    // ── Render ────────────────────────────────────────────────────────────────

    @Override
    public void render(float delta) {
        handleCombatInput();

        game.batch.setProjectionMatrix(game.uiCamera.combined);
        game.batch.begin();
        game.batch.end();

        // Dark background
        game.shapeRenderer.setProjectionMatrix(game.uiCamera.combined);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.05f, 0.05f, 0.1f, 1f);
        game.shapeRenderer.rect(0, 0, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);

        // Enemy HP bar
        Enemy e = game.ctx.currentEnemy;
        float eHp = e.getMaxHp() > 0 ? (float) e.getHp() / e.getMaxHp() : 0f;
        drawBar(game.shapeRenderer, 30, Main.WORLD_HEIGHT - 50, 340, 18,
            eHp, Color.DARK_GRAY, Color.valueOf("cc2222"));

        // Player HP bar
        Character c = game.ctx.activeCharacterStats;
        drawBar(game.shapeRenderer, 30, 115, 250, 16,
            (float) c.getHp() / c.getMaxHp(), Color.DARK_GRAY, Color.RED);
        drawBar(game.shapeRenderer, 30,  94, 250, 14,
            c.getMaxShield() > 0 ? (float) c.getShield() / c.getMaxShield() : 0f,
            Color.DARK_GRAY, Color.CYAN);

        // Enemy placeholder sprite
        game.shapeRenderer.setColor(0.55f, 0.1f, 0.1f, 1f);
        game.shapeRenderer.rect(500, 150, 200, 200);

        // Note slot boxes
        for (int i = 0; i < 3; i++) {
            boolean filled = i < game.ctx.noteCount;
            game.shapeRenderer.setColor(filled
                ? new Color(0.2f, 0.5f, 0.9f, 1f)
                : new Color(0.15f, 0.15f, 0.25f, 1f));
            game.shapeRenderer.rect(290 + i * 70, 125, 55, 55);
        }
        game.shapeRenderer.end();

        // Text pass
        game.batch.setProjectionMatrix(game.uiCamera.combined);
        game.batch.begin();

        // Enemy name + HP
        game.assets.titleFont.setColor(Color.valueOf("ff6666"));
        game.assets.titleFont.draw(game.batch, e.getName(), 30, Main.WORLD_HEIGHT - 20);
        game.assets.font.setColor(Color.WHITE);
        game.assets.font.draw(game.batch,
            "HP: " + e.getHp() + " / " + e.getMaxHp(), 380, Main.WORLD_HEIGHT - 20);

        game.assets.font.setColor(Color.valueOf("ff9999"));
        game.assets.font.draw(game.batch, "[" + e.getName() + "]", 508, 265);

        // Player stats
        game.assets.font.setColor(Color.WHITE);
        game.assets.font.draw(game.batch,
            c.getName() + "  HP: " + c.getHp() + "/" + c.getMaxHp(), 30, 132);
        game.assets.font.draw(game.batch,
            "Shield: " + c.getShield() + "/" + c.getMaxShield(), 30, 110);

        // Note slots
        game.assets.font.setColor(Color.CYAN);
        game.assets.font.draw(game.batch, "Notes:", 190, 166);
        for (int i = 0; i < 3; i++) {
            if (i < game.ctx.noteCount) {
                game.assets.font.setColor(Color.WHITE);
                game.assets.font.draw(game.batch,
                    String.valueOf(java.lang.Character.toUpperCase(game.ctx.noteBuffer[i])),
                    310 + i * 70, 165);
            } else {
                game.assets.font.setColor(new Color(0.4f, 0.4f, 0.55f, 1f));
                game.assets.font.draw(game.batch, "_", 310 + i * 70, 165);
            }
        }

        // Damage preview
        if (game.ctx.noteCount > 0) {
            int preview = 0;
            for (int i = 0; i < game.ctx.noteCount; i++) preview += game.ctx.noteDamages[i];
            game.assets.font.setColor(Color.YELLOW);
            game.assets.font.draw(game.batch, "Dmg so far: " + preview, 510, 166);
        }

        // Combat log
        game.assets.font.setColor(Color.valueOf("ddddaa"));
        game.assets.font.draw(game.batch, game.ctx.combatLog, 30, 88);

        // State overlay
        renderStateOverlay(delta);

        game.assets.font.setColor(Color.WHITE);
        game.assets.titleFont.setColor(Color.WHITE);
        game.batch.end();
    }

    private void renderStateOverlay(float delta) {
        switch (game.ctx.combatState) {
            case ATTACK:
                game.assets.font.setColor(Color.GRAY);
                game.assets.font.draw(game.batch,
                    "Press A–G to enter notes.  BACKSPACE to undo.", 30, 60);
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
                game.assets.font.draw(game.batch, "Press ENTER to restart.", 255, 200);
                if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
//                    game.ctx.player = null; // triggers full reset in ExploringScreen.show()
                    game.setScreen(game.ctx.lastMapScreen);
                }
                break;

            default: break;
        }
    }

    // ── Note input ────────────────────────────────────────────────────────────

    private void handleCombatInput() {
        if (game.ctx.combatState != GameContext.CombatState.ATTACK) return;

        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE) && game.ctx.noteCount > 0) {
            game.ctx.noteCount--;
            return;
        }

        int[]  keys  = { Input.Keys.A, Input.Keys.B, Input.Keys.C,
            Input.Keys.D, Input.Keys.E, Input.Keys.F, Input.Keys.G };
        char[] notes = { 'a', 'b', 'c', 'd', 'e', 'f', 'g' };

        for (int i = 0; i < keys.length; i++) {
            if (Gdx.input.isKeyJustPressed(keys[i]) && game.ctx.noteCount < 3) {
                game.ctx.noteBuffer[game.ctx.noteCount]  = notes[i];
                game.ctx.noteDamages[game.ctx.noteCount] = rollNote(notes[i]);
                game.ctx.noteCount++;
                break;
            }
        }
        if (game.ctx.noteCount == 3) resolveAttack();
    }

    private int rollNote(char n) {
        switch (n) {
            case 'a': return 1  + RNG.nextInt(10);
            case 'b': return 5  + RNG.nextInt(9);
            case 'c': return 9  + RNG.nextInt(8);
            case 'd': return 12 + RNG.nextInt(7);
            case 'e': return 16 + RNG.nextInt(6);
            case 'f': return 19 + RNG.nextInt(5);
            case 'g': return 21 + RNG.nextInt(4);
            default:  return 0;
        }
    }

    // ── Attack resolution ─────────────────────────────────────────────────────

    private void resolveAttack() {
        int total = 0;
        for (int i = 0; i < 3; i++) total += game.ctx.noteDamages[i];
        total = (int)(total * (1.0 + game.ctx.activeCharacterStats.getDamageBuff()));

        String chordMsg = checkChord();
        game.ctx.playerDamageDealt = total;
        game.ctx.currentEnemy.takeDamage(total);

        game.ctx.combatLog = chordMsg.isEmpty()
            ? "You played " + noteDisplay() + " for " + total + " damage."
            : "CHORD: " + noteDisplay() + " — " + chordMsg;

        game.ctx.combatState  = GameContext.CombatState.DISPLAY_PLAYER_DAMAGE;
        game.ctx.resultTimer  = 0f;
    }

    private void doEnemyAttack() {
        if (game.ctx.currentEnemy.isDefeated()) {
            game.ctx.combatState = GameContext.CombatState.VICTORY;
            return;
        }
        int dmg = game.ctx.currentEnemy.performAttack();
        game.ctx.activeCharacterStats.takeDamage(dmg);
        game.ctx.enemyDamageDealt = dmg;
        game.ctx.combatState      = GameContext.CombatState.DISPLAY_ENEMY_DAMAGE;
        game.ctx.resultTimer      = 0f;
    }

    private void nextPlayerTurn() {
        if (!game.ctx.activeCharacterStats.isAlive()) {
            game.ctx.combatState = GameContext.CombatState.DEFEAT; return;
        }
        game.ctx.noteCount   = 0;
        game.ctx.combatLog   = "";
        game.ctx.combatState = GameContext.CombatState.ATTACK;
    }

    private void endCombat() {
        Enemy defeated = game.ctx.currentEnemy;

        if (defeated != null && defeated.isDefeated()) {
            game.ctx.enemiesDefeatedInCurrentMap++;
        }

        if (game.ctx.rooms != null) {
            for (Room r : game.ctx.rooms) {
                if (r.getEnemies().remove(defeated)) {
                    if (r.getEnemies().isEmpty()) {
                        r.setCleared(true);
                    }
                    break;
                }
            }
        }
        game.ctx.currentEnemy = null;
        game.ctx.noteCount    = 0;
        game.ctx.combatLog    = "";
        game.setScreen(game.ctx.lastMapScreen);
    }

    // ── Chord detection ───────────────────────────────────────────────────────

    private String checkChord() {
        char[] ch = { game.ctx.noteBuffer[0], game.ctx.noteBuffer[1], game.ctx.noteBuffer[2] };
        Arrays.sort(ch);
        String s = new String(ch);
        Character c = game.ctx.activeCharacterStats;
        switch (s) {
            case "ceg": { int h = (int)(c.getMaxHp()*0.20f); c.heal(h);
                return "C Major! Healed " + h + " HP."; }
            case "adf": c.setDamageBuff(c.getDamageBuff()+0.20);
                return "D Minor! +20% damage buff.";
            case "beg": { int h = (int)(c.getMaxHp()*0.10f); c.heal(h);
                c.setDamageBuff(c.getDamageBuff()+0.10);
                return "E Minor! Healed "+h+" HP + 10% buff."; }
            case "acf": c.gainShield(25); return "F Major! +25 shield.";
            case "bdg": { int h = (int)(c.getMaxHp()*0.15f); c.heal(h); c.gainShield(15);
                return "G Major! Healed "+h+" HP + 15 shield."; }
            case "ace": c.gainShield(35); return "A Minor! +35 shield.";
            case "bdf": { c.setDamageBuff(c.getDamageBuff()+0.30);
                int sd = (int)(c.getMaxHp()*0.10f); c.takeDamage(sd);
                return "B Diminished! +30% dmg, lost "+sd+" HP."; }
            default: return "";
        }
    }

    private String noteDisplay() {
        return java.lang.Character.toUpperCase(game.ctx.noteBuffer[0]) + "-"
            + java.lang.Character.toUpperCase(game.ctx.noteBuffer[1]) + "-"
            + java.lang.Character.toUpperCase(game.ctx.noteBuffer[2]);
    }

    @Override public void resize(int w, int h) {}
    @Override public void hide()    {}
    @Override public void dispose() {}
}
