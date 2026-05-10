package io.github.Zephyrdoestech;

import Entities.CharacterHero;
import Entities.Enemy;
import Entities.MapCharacter;
import Mechanics.CombatSystem.Note;
import Mechanics.CombatSystem.Chord;
import Mechanics.CombatSystem.Metronome;
import Mechanics.MapTraversalSystem.Room;
import Screens.ExploringScreen;
import com.badlogic.gdx.audio.Music;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds all mutable game state that must survive screen transitions.
 * Screens read and write via game.ctx.*  — no logic lives here, only data.
 */
public class GameContext {

    // ── Enums ──────────────────────────────────────────────────────────────────

    public enum CharacterType {SONARA, AURELIUS, LYRON}

    public enum PlayerState {IDLE, WALK_UP, WALK_DOWN, WALK_LEFT, WALK_RIGHT}

    public enum Facing {LEFT, RIGHT}

    public enum CombatState {
        NONE, BATTLE_SCREEN, TUTORIAL,
        ENEMY_INTRODUCTION,
        TURN_MENU, ATTACK, ATTACK_FEEDBACK, MISSED_TURN,
        USE_SKILL, SKILL_USED, SKILL_CONFIRMED,
        OPEN_INVENTORY, USE_ITEM, ITEM_USED,
        DISPLAY_CHORD, DISPLAY_CHORD_EFFECT,
        DISPLAY_PLAYER_DAMAGE, DISPLAY_FINAL_DAMAGE,
        ENEMY_ATTACK, DISPLAY_ENEMY_DAMAGE,
        CHARACTER_POSTCOMBAT_LINE,
        VICTORY, DEFEAT, EXIT
    }

    public enum MapName {
        TOWN_OF_ECHOES, SILENT_CAVERNS, ABYSS_OF_DISSONANCE
    }

    public enum ChordStates {
        CMAJOR, DMINOR, EMINOR, FMAJOR, GMAJOR, AMINOR, BDIM, NONE
    }

    // For Leaderboard
    public float totalPlaytime = 0f;
    public int mapsCleared = 0;

    // ── CharacterHero / player state ───────────────────────────────────────────────

    public CharacterType selectedCharacter;
    public CharacterHero activeCharacterStats;  // HP, shield, level, buffs
    public MapCharacter player;                // world-space position
    public PlayerState playerState = PlayerState.IDLE;
    public Facing facing = Facing.RIGHT;
    public float stateTime = 0f;     // drives animation clock

    // player coordinates when continuing the game
    public float savedPlayerX = -1f;
    public float savedPlayerY = -1f;

    public boolean useWasd = true;

    // ── Map state ─────────────────────────────────────────────────────────────

    public List<Enemy> mapEnemies = new ArrayList<>();
    public List<Room> rooms = new ArrayList<>();
    public int maxLives = 3;
    public int lives = maxLives;
    public int enemiesDefeatedInCurrentMap = 0;
    public Room exitRoom = null;
    public ExploringScreen currentMapScreen = null;


    // Map dimensions — match your Dungeon.png pixel size
    public float MAP_SIZE = 2048f;
    public static final float CHAR_SIZE = 32f;
    public static final float SPEED     = 200f;

    public com.badlogic.gdx.Screen lastMapScreen;

    // ── Combat state ──────────────────────────────────────────────────────────

    public Enemy       currentEnemy;
    public CombatState combatState;
    public MapName mapName = MapName.TOWN_OF_ECHOES; // Set default map name

    public final Note noteHandler = new Note();
    public final Chord chordSystem = new Chord();
    public final Metronome metronome = new Metronome();

    public float resultTimer = 0f;
    public String combatLog = "";

    public boolean playerDefeated = false;
    public boolean playerWon = false;
    public int leveledUpTo = 0;

    // ── Final Boss Lines ─────────────────────────────────────────────────

    public String[] bossDialogueLines = {
        "So the little melody finally reaches me.",
        "Play, then.",
        "Let me hear what remains of your world."
    };

    // ── CharacterHero-select audio ─────────────────────────────────────────────────

    public Music currentTheme = null;
    public int lastThemeIndex = -1;  // 0=Sonara 1=Aurelius 2=Lyron  -1=none

    /**
     * Switches character-select theme safely.
     * Passing -1 stops everything without starting a new track.
     */
//    public void playTheme(int index, Assets assets) {
//        if (index == lastThemeIndex) return;
//        if (currentTheme != null) { currentTheme.stop(); currentTheme = null; }
//        lastThemeIndex = index;
//        if (index < 0) return;
//        switch (index) {
//            case 0: currentTheme = assets.sonaraTheme;   break;
//            case 1: currentTheme = assets.aureliusTheme; break;
//            case 2: currentTheme = assets.lyronTheme;    break;
//            default: return;
//        }
//        currentTheme.setVolume(0.75f);
//        currentTheme.play();
//    }

    /**
     * Stops all character-select music immediately.
     */
    public void stopTheme() {
        if (currentTheme != null) {
            currentTheme.stop();
            currentTheme = null;
        }
        lastThemeIndex = -1;
    }

    // --- SAVE LOGIC ---
    public void saveGame(String mapName, float playerX, float playerY) {
        com.badlogic.gdx.Preferences prefs = com.badlogic.gdx.Gdx.app.getPreferences("ZephyrSave");

        prefs.putString("currentMap", mapName);

        prefs.putFloat("playerX", playerX);
        prefs.putFloat("playerY", playerY);

        if (activeCharacterStats != null) {
            prefs.putString("charName", activeCharacterStats.getName());
            prefs.putString("charWeapon", activeCharacterStats.getInstrument());
            prefs.putInteger("charMaxHp", activeCharacterStats.getMaxHp());
            prefs.putInteger("charHp", activeCharacterStats.getHp());
            prefs.putInteger("charMaxShield", activeCharacterStats.getMaxShield());
            prefs.putInteger("charShield", activeCharacterStats.getShield());
            prefs.putInteger("charLevel", activeCharacterStats.getLevel());
            prefs.putInteger("charMonsters", activeCharacterStats.getMonstersDefeated());

            // Flatten the inventory Map into a single String (e.g., "CrimsonChorus:1,TimeOrb:2,")
            StringBuilder invStr = new StringBuilder();
            for (java.util.Map.Entry<String, Integer> entry : activeCharacterStats.inventory.entrySet()) {
                invStr.append(entry.getKey()).append(":").append(entry.getValue()).append(",");
            }
            prefs.putString("charInventory", invStr.toString());
        }

        prefs.flush(); // CRITICAL: This actually writes the file to the hard drive
        System.out.println("Game Auto-Saved at: " + mapName);
    }

    // --- LOAD LOGIC ---
    public String loadGame() {
        com.badlogic.gdx.Preferences prefs = com.badlogic.gdx.Gdx.app.getPreferences("ZephyrSave");

        if (!prefs.contains("currentMap")) {
            return null; // No save file exists!
        }

        // Read the saved coordinates
        this.savedPlayerX = prefs.getFloat("playerX", -1f);
        this.savedPlayerY = prefs.getFloat("playerY", -1f);

        // 1. Read the base stats
        String name = prefs.getString("charName", "Sonara");
        String weapon = prefs.getString("charWeapon", "Banjo");
        int maxHp = prefs.getInteger("charMaxHp", 150);
        int maxShield = prefs.getInteger("charMaxShield", 40);

        // --- THE FIX: Re-assign the selectedCharacter enum based on the loaded name! ---
        switch (name) {
            case "Aurelius":
                this.selectedCharacter = CharacterType.AURELIUS;
                break;
            case "Lyron":
                this.selectedCharacter = CharacterType.LYRON;
                break;
            case "Sonara":
            default:
                this.selectedCharacter = CharacterType.SONARA;
                break;
        }

        // 2. Rebuild the character stats
        CharacterHero loadedChar = new CharacterHero(name, weapon, maxHp, maxShield);
        loadedChar.setHp(prefs.getInteger("charHp", maxHp));
        loadedChar.setShield(prefs.getInteger("charShield", 0));
        loadedChar.setLevel(prefs.getInteger("charLevel", 1));
        loadedChar.setMonstersDefeated(prefs.getInteger("charMonsters", 0));

        // 3. Unpack the inventory
        String invStr = prefs.getString("charInventory", "");
        if (!invStr.isEmpty()) {
            String[] items = invStr.split(",");
            for (String item : items) {
                if (item.contains(":")) {
                    String[] parts = item.split(":");
                    loadedChar.inventory.put(parts[0], Integer.parseInt(parts[1]));
                }
            }
        }

        // 4. Set it as the active character
        this.activeCharacterStats = loadedChar;

        // 5. Tell the game which map to load
        return prefs.getString("currentMap");
    }
}
