package Entities;

import java.util.Random;

/**
 * Represents a single enemy on the map.
 * Each enemy has:
 *  - Name, HP, and a list of named attacks with damage ranges.
 *  - A world-space position (x, y) for map collision.
 *  - A defeated flag so the Arc can remove it after combat.
 */
public class Enemy {

    // ── Attack inner class ────────────────────────────────────────────────────

    public static class Attack {
        public final String name;
        public final int    minDmg;
        public final int    maxDmg;

        public Attack(String name, int minDmg, int maxDmg) {
            this.name   = name;
            this.minDmg = minDmg;
            this.maxDmg = maxDmg;
        }
    }

    // ── Fields ────────────────────────────────────────────────────────────────

    private final String   name;
    private int      maxHp;
    private       int      currentHp;
    private final Attack[] attacks;

    /** World-space pixel position on the map. */
    private float x;
    private float y;

    private boolean defeated = false;

    private static final Random RNG = new Random();

    // ── Constructor ───────────────────────────────────────────────────────────

    public Enemy(String name, int hp, float x, float y, Attack... attacks) {
        this.name     = name;
        this.maxHp    = hp;
        this.currentHp = hp;
        this.x        = x;
        this.y        = y;
        this.attacks  = attacks;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
        this.currentHp = maxHp;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public String   getName()      { return name; }
    public int      getHp()        { return currentHp; }
    public int      getMaxHp()     { return maxHp; }
    public float    getX()         { return x; }
    public float    getY()         { return y; }
    public boolean  isDefeated()   { return defeated; }

    // ── Combat ────────────────────────────────────────────────────────────────

    /** Reduces HP by the given amount. Marks defeated if HP drops to 0. */
    public void takeDamage(int damage) {
        currentHp = Math.max(0, currentHp - damage);
        if (currentHp == 0) defeated = true;
    }

    /**
     * Picks a random attack and returns the damage rolled within its range.
     * Also stores the last attack name for display purposes.
     */
    private String lastAttackName = "";
    private int    lastAttackDmg  = 0;

    public int performAttack() {
        Attack atk  = attacks[RNG.nextInt(attacks.length)];
        int    dmg  = atk.minDmg + RNG.nextInt(atk.maxDmg - atk.minDmg + 1);
        lastAttackName = atk.name;
        lastAttackDmg  = dmg;
        return dmg;
    }

    public String getLastAttackName() { return lastAttackName; }
    public int    getLastAttackDmg()  { return lastAttackDmg; }

    // ── Factory methods (one per enemy type from the GDD) ────────────────────

    public static Enemy fleshFeeder(float x, float y) {
        return new Enemy("Flesh Feeder", 250, x, y,
            new Attack("Claw Through", 10, 20),
            new Attack("Bite",         15, 30),
            new Attack("Leap",         20, 45)
        );
    }

    public static Enemy darrylion(float x, float y) {
        return new Enemy("Darrylion", 350, x, y,
            new Attack("Luminous Gaze",     10, 20),
            new Attack("Deafening Screech", 15, 25),
            new Attack("Shatter Cry",       20, 35)
        );
    }

    public static Enemy aryzachnid(float x, float y) {
        return new Enemy("Gobninil", 500, x, y,
            new Attack("Binding Webs",     20, 40),
            new Attack("Paralyzing Fangs", 25, 50),
            new Attack("Wrapup",           50, 75)
        );
    }

    public static Enemy chimericks(float x, float y) {
        return new Enemy("Chimericks", 700, x, y,
            new Attack("Strike",        15, 35),
            new Attack("Venomous Bite", 20, 40),
            new Attack("Leech On",      40, 60)
        );
    }

    public static Enemy labagoliath(float x, float y) {
        return new Enemy("Labagoliath the Void Shaker", 1000, x, y,
            new Attack("Defensive Stance", 10, 20),
            new Attack("Hammer Swipe",     25, 55),
            new Attack("Hammer Strikes",   30, 65)
        );
    }

    public static Enemy maestroSyozan(float x, float y) {
        return new Enemy("Maestro Syozan", 2000, x, y,
            new Attack("Abyssal Echo Dirge",              20,  75),
            new Attack("Dirge of the Shattered Moon",     30, 100),
            new Attack("Ebon Symphony of Consuming Night",40, 125)
        );
    }
}
