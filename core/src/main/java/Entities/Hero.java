package Entities;

public class Hero {
    private String name;
    private String instrument;

    private int maxHp;
    private int currentHp;
    private int maxShield;
    private int currentShield;

    private int level;
    private int monstersDefeated;
    private double damageBuff; // Multiplier bonus from chords/skills (e.g., 0.20 = +20%)

    // Base HP values per character per level, matching the GDD
    private static final int[] SONARA_HP   = {150, 175, 225, 300, 400};
    private static final int[] AURELIUS_HP = {150, 175, 225, 300, 400};
    private static final int[] LYRON_HP    = {250, 270, 310, 370, 450};

    public Hero(String name, String instrument, int maxHp, int maxShield) {
        this.name       = name;
        this.instrument = instrument;
        this.maxHp      = maxHp;
        this.currentHp  = maxHp;   // Start at full health
        this.maxShield  = maxShield;
        this.currentShield = 0;    // Shield starts empty until a chord grants it
        this.level      = 1;
        this.monstersDefeated = 0;
        this.damageBuff = 0.0;
    }

    // ─── Getters ───────────────────────────────────────────────────────────────

    public String getName()       { return name; }
    public String getInstrument() { return instrument; }

    public int getHp()      { return currentHp; }
    public int getMaxHp()   { return maxHp; }
    public int getShield()  { return currentShield; }
    public int getMaxShield() { return maxShield; }

    public int    getLevel()           { return level; }
    public int    getMonstersDefeated(){ return monstersDefeated; }
    public double getDamageBuff()      { return damageBuff; }

    // ─── Setters ───────────────────────────────────────────────────────────────

    public void setHp(int hp) {
        this.currentHp = Math.max(0, Math.min(hp, maxHp));
    }

    public void setShield(int shield) {
        this.currentShield = Math.max(0, Math.min(shield, maxShield));
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
        // Don't let currentHp exceed the new cap
        this.currentHp = Math.min(this.currentHp, maxHp);
    }

    public void setDamageBuff(double buff) {
        this.damageBuff = buff;
    }

    // ─── Combat Utilities ──────────────────────────────────────────────────────

    /**
     * Applies incoming damage, first absorbing through shield then HP.
     * @param damage Raw damage after enemy's attack roll.
     */
    public void takeDamage(int damage) {
        if (damage <= 0) return;

        if (currentShield > 0) {
            int absorbed = Math.min(currentShield, damage);
            currentShield -= absorbed;
            damage        -= absorbed;
        }

        currentHp = Math.max(0, currentHp - damage);
    }

    /**
     * Restores HP by a flat amount, capped at maxHp.
     */
    public void heal(int amount) {
        currentHp = Math.min(maxHp, currentHp + amount);
    }

    /**
     * Adds shield points, capped at maxShield.
     */
    public void gainShield(int amount) {
        currentShield = Math.min(maxShield, currentShield + amount);
    }

    public boolean isAlive() {
        return currentHp > 0;
    }

    // ─── Progression ───────────────────────────────────────────────────────────

    /** Called after defeating an enemy; tracks kill count for level-up logic. */
    public void defeatedMonster() {
        monstersDefeated++;
    }

    /**
     * Levels the hero up to the given level, updating maxHp from GDD tables.
     * @param newLevel Target level (1–5).
     */
    public void levelUp(int newLevel) {
        if (newLevel < 1 || newLevel > 5 || newLevel <= level) return;
        level = newLevel;

        int newMaxHp;
        switch (name) {
            case "Sonara":   newMaxHp = SONARA_HP[level - 1];   break;
            case "Aurelius": newMaxHp = AURELIUS_HP[level - 1]; break;
            case "Lyron":    newMaxHp = LYRON_HP[level - 1];    break;
            default:         newMaxHp = maxHp + 25;             break;
        }
        int hpGain = newMaxHp - maxHp;
        maxHp      = newMaxHp;
        currentHp  = Math.min(maxHp, currentHp + hpGain); // Grant HP delta on level-up
    }
}
