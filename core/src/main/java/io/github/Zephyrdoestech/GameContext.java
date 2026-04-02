package io.github.Zephyrdoestech;

import Entities.Character;
import Entities.Enemy;
import Entities.MapCharacter;
import Mechanics.CombatSystem.Note;
import Mechanics.CombatSystem.Chord;
import Mechanics.CombatSystem.Metronome;
import Mechanics.MapTraversalSystem.Room;
import com.badlogic.gdx.audio.Music;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds all mutable game state that must survive screen transitions.
 * Screens read and write via game.ctx.*  — no logic lives here, only data.
 */
public class GameContext {

    // ── Enums ──────────────────────────────────────────────────────────────────

    public enum CharacterType { SONARA, AURELIUS, LYRON }

    public enum PlayerState { IDLE, WALK_UP, WALK_DOWN, WALK_LEFT, WALK_RIGHT }

    public enum Facing { LEFT, RIGHT }

    public enum CombatState {
        BATTLE_SCREEN, TUTORIAL,
        ENEMY_INTRODUCTION,
        TURN_MENU, ATTACK, ATTACK_FEEDBACK,
        USE_SKILL, OPEN_INVENTORY, USE_ITEM,
        DISPLAY_CHORD_EFFECT, DISPLAY_PLAYER_DAMAGE, ENEMY_ATTACK, DISPLAY_ENEMY_DAMAGE,
        CHARACTER_POSTCOMBAT_LINE,
        VICTORY, DEFEAT, EXIT
    }

    public enum MapName{
        TOWN_OF_ECHOES, SILENT_CAVERNS, ABYSS_OF_DISSONANCE
    }

    public enum ChordStates{
        CMAJOR, DMINOR, EMINOR, FMAJOR, GMAJOR, AMINOR, BDIM, NONE
    }

    // ── Character / player state ───────────────────────────────────────────────

    public CharacterType  selectedCharacter;
    public Character      activeCharacterStats;  // HP, shield, level, buffs
    public MapCharacter   player;                // world-space position
    public PlayerState    playerState  = PlayerState.IDLE;
    public Facing         facing       = Facing.RIGHT;
    public float          stateTime    = 0f;     // drives animation clock

    // ── Map state ─────────────────────────────────────────────────────────────

    public List<Enemy> mapEnemies = new ArrayList<>();
    public List<Room>  rooms      = new ArrayList<>();

    // Map dimensions — match your Dungeon.png pixel size
    public static final float MAP_SIZE  = 2048f;
    public static final float CHAR_SIZE = 32f;
    public static final float SPEED     = 150f;

    // ── Combat state ──────────────────────────────────────────────────────────

    public Enemy       currentEnemy;
    public CombatState combatState;
    public MapName mapName = MapName.TOWN_OF_ECHOES; // Set default map name

    public final Note noteHandler = new Note();
    public final Chord chordSystem = new Chord();
    public final Metronome metronome = new Metronome();

    public float  resultTimer       = 0f;
    public int    playerDamageDealt = 0;
    public int    enemyDamageDealt  = 0;
    public String combatLog         = "";

    // ── Character-select audio ─────────────────────────────────────────────────

    public Music currentTheme = null;
    public int   lastThemeIndex = -1;  // 0=Sonara 1=Aurelius 2=Lyron  -1=none

    /**
     * Switches character-select theme safely.
     * Passing -1 stops everything without starting a new track.
     */
}
