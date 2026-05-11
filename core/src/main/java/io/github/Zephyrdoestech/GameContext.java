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
import Inventory.Consumables.Item;

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


    // Map dimensions
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

    public boolean isLabagoliathDefeated = false;

    // ── Final Boss Lines ─────────────────────────────────────────────────

    public boolean finalBossVictoryPending = false;
    public boolean finalBossDefeatPending = false;

    public String[] bossPreCombatLines = {
        "Ah, another one.",
        "A child of the silence, yet obsessed with the deafening clang of sound.",
        "Did you truly believe your little noise could lead you here? How pathetic.",
        "You found my work - the eternal silence - and instead of realizing its perfection, you declared it a disease.",
        "You confuse chaos for life, and order for cruelty."
    };

    public String[] bossVictoryLines = {
        "No... you fool!",
        "You have ruined the perfection!",
        "You confuse this chaos for life, and order for cruelty!",
        "I imposed this silence out of pity;",
        "stopping the world from screaming itself into oblivion;",
        "gifting humanity absolute peace from the sheer, agonizing volume of its own sorrow.",
        "You think this noise is freedom?",
        "It is the sound of inevitable pain and hatred, magnified a thousand times!",
        "You will live to regret this freedom, child; you will beg for the quiet I gifted you."
    };

    public String[] bossDefeatLines = {
        "You fought to bring back the scream of existence, but you lacked the breath to sustain it.",
        "Look around—the silence hasn't just defeated you; it has welcomed you home.",
        "You are finally part of the masterpiece."
    };

    // ── CharacterHero-select audio ─────────────────────────────────────────────────

    public Music currentTheme = null;
    public int lastThemeIndex = -1;  // 0=Sonara 1=Aurelius 2=Lyron  -1=none

    // Save state
    public String currentSaveSlot = "ZephyrSave_1";

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

    public void createNewSaveSlot() {
        com.badlogic.gdx.Preferences global = com.badlogic.gdx.Gdx.app.getPreferences("ZephyrGlobal");

        // Find the first empty slot out of the 3 available
        int availableSlot = -1;
        for (int i = 1; i <= 3; i++) {
            String slotName = "ZephyrSave_" + i;
            com.badlogic.gdx.Preferences slotPrefs = com.badlogic.gdx.Gdx.app.getPreferences(slotName);
            if (!slotPrefs.contains("currentMap")) {
                availableSlot = i;
                break;
            }
        }

        if (availableSlot != -1) {
            // Found an empty slot, use it
            currentSaveSlot = "ZephyrSave_" + availableSlot;
            int saveCount = global.getInteger("saveCount", 0);
            if (availableSlot > saveCount) {
                global.putInteger("saveCount", availableSlot);
            }
            global.putString("save_" + availableSlot, currentSaveSlot);
            global.flush();
        } else {
            // No empty slots, overwrite the 3rd slot as fallback
            currentSaveSlot = "ZephyrSave_3";
        }
    }

    public List<String> getAllSaveSlots() {
        com.badlogic.gdx.Preferences global = com.badlogic.gdx.Gdx.app.getPreferences("ZephyrGlobal");
        int saveCount = global.getInteger("saveCount", 0);
        List<String> saves = new ArrayList<>();
        for (int i = 1; i <= saveCount; i++) {
            saves.add(global.getString("save_" + i));
        }
        return saves;
    }

    // Get info for a specific save slot to display on the load screen
    public String getSaveInfo(String slotName) {
        com.badlogic.gdx.Preferences prefs = com.badlogic.gdx.Gdx.app.getPreferences(slotName);
        if (!prefs.contains("currentMap")) {
            return "Empty Slot";
        }
        String map = prefs.getString("currentMap", "Unknown Map");
        String charName = prefs.getString("charName", "Unknown");
        int level = prefs.getInteger("charLevel", 1);
        return charName + " - Lv." + level + " - " + map;
    }

    // --- SAVE LOGIC ---
    public void saveGame(String mapName, float playerX, float playerY) {
        com.badlogic.gdx.Preferences prefs = com.badlogic.gdx.Gdx.app.getPreferences(currentSaveSlot);

        prefs.putString("currentMap", mapName);

        prefs.putFloat("playerX", playerX);
        prefs.putFloat("playerY", playerY);

        // Save the map specific kill count
        prefs.putInteger("enemiesDefeatedInCurrentMap", enemiesDefeatedInCurrentMap);

        if (activeCharacterStats != null) {
            prefs.putString("charName", activeCharacterStats.getName());
            prefs.putString("charWeapon", activeCharacterStats.getInstrument());
            prefs.putInteger("charMaxHp", activeCharacterStats.getMaxHp());
            prefs.putInteger("charHp", activeCharacterStats.getHp());
            prefs.putInteger("charMaxShield", activeCharacterStats.getMaxShield());
            prefs.putInteger("charShield", activeCharacterStats.getShield());
            prefs.putInteger("charLevel", activeCharacterStats.getLevel());
            prefs.putInteger("charMonsters", activeCharacterStats.getMonstersDefeated());

            // Serialize the items in playerInventory
            StringBuilder invStr = new StringBuilder();
            if (activeCharacterStats.getPlayerInventory() != null) {
                for (Item item : activeCharacterStats.getPlayerInventory().getItems()) {
                    invStr.append(item.getName()).append(",");
                }
            }
            prefs.putString("charInventory", invStr.toString());
        }

        prefs.flush(); // CRITICAL: This actually writes the file to the hard drive
        System.out.println("Game Auto-Saved at: " + mapName + " into " + currentSaveSlot);

        // Also register in ZephyrGlobal if not already
        com.badlogic.gdx.Preferences global = com.badlogic.gdx.Gdx.app.getPreferences("ZephyrGlobal");
        int saveCount = global.getInteger("saveCount", 0);
        boolean found = false;
        for (int i = 1; i <= saveCount; i++) {
            if (global.getString("save_" + i).equals(currentSaveSlot)) {
                found = true;
                break;
            }
        }
        if (!found) {
            if (saveCount < 3) {
                saveCount++;
                global.putInteger("saveCount", saveCount);
                global.putString("save_" + saveCount, currentSaveSlot);
                global.flush();
            }
        }
    }

    // --- LOAD LOGIC ---
    public String loadGame(String slotName, Assets assets) {
        this.currentSaveSlot = slotName;
        com.badlogic.gdx.Preferences prefs = com.badlogic.gdx.Gdx.app.getPreferences(slotName);

        if (!prefs.contains("currentMap")) {
            return null; // No save file exists!
        }

        // Read the saved coordinates
        this.savedPlayerX = prefs.getFloat("playerX", -1f);
        this.savedPlayerY = prefs.getFloat("playerY", -1f);

        // Read the map-specific kill count
        this.enemiesDefeatedInCurrentMap = prefs.getInteger("enemiesDefeatedInCurrentMap", 0);

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

        // 3. Unpack the inventory into playerInventory
        String invStr = prefs.getString("charInventory", "");
        if (!invStr.isEmpty()) {
            String[] items = invStr.split(",");
            for (String itemStr : items) {
                String itemName = itemStr.trim();
                if (itemName.isEmpty()) continue;

                // If it contains a colon, we extract just the name to support backward compatibility
                // (if older saves had format "Crimson Chorus:1")
                if (itemName.contains(":")) {
                    itemName = itemName.split(":")[0].trim();
                }

                switch (itemName) {
                    case "Crimson Chorus":
                        loadedChar.getPlayerInventory().gainCrimsonChorus(assets);
                        break;
                    case "Major's Blessing":
                        loadedChar.getPlayerInventory().gainMajorBlessing(assets);
                        break;
                    case "Minor's Grace":
                        loadedChar.getPlayerInventory().gainMinorsGrace(assets);
                        break;
                    case "Silent Barrier":
                        loadedChar.getPlayerInventory().gainSilentBarrier(assets);
                        break;
                    case "Resolved Dissonance":
                        loadedChar.getPlayerInventory().gainResolvedDissonance(assets);
                        break;
                    case "Time Orb":
                        loadedChar.getPlayerInventory().gainTimeOrb(assets);
                        break;
                }
            }
        }

        // 4. Set it as the active character
        this.activeCharacterStats = loadedChar;

        // 5. Tell the game which map to load
        return prefs.getString("currentMap");
    }
}
