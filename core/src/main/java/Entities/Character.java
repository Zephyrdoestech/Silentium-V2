package Entities;

public class Character {
    public static class PassiveSkill{
        public final String psName;
        public final String psDescription;

        PassiveSkill(String psName, String psDescription){
            this.psName = psName;
            this.psDescription = psDescription;
        }
    }

    public static class ActiveSkill{
        public final String asName;
        public final String asDescription;
        public boolean activate;

        ActiveSkill(String asName, String asDescription){
            this.asName = asName;
            this.asDescription = asDescription;
            this.activate = false;
        }
    }

    private String name;
    private String instrument;

    private int maxHp;
    private int currentHp;
    private int maxShield;
    private int currentShield;
    private PassiveSkill passiveSkill;
    private ActiveSkill activeSkill;

    private int level;
    private int monstersDefeated;
    private double damageBuff; // Multiplier bonus from chords/skills (e.g., 0.20 = +20%)

    // Base HP values per character per level, matching the GDD
    private static final int[] SONARA_HP   = {150, 175, 225, 300, 400};
    private static final int[] AURELIUS_HP = {150, 175, 225, 300, 400};
    private static final int[] LYRON_HP    = {250, 270, 310, 370, 450};

    public Character(String name, String instrument, int maxHp, int maxShield) {
        this.name       = name;
        this.instrument = instrument;
        this.maxHp      = maxHp;
        this.currentHp  = maxHp;   // Start at full health
        this.maxShield  = maxShield;
        this.currentShield = 0;    // Shield starts empty until a chord grants it
        this.level      = 1;
        this.monstersDefeated = 0;
        this.damageBuff = 0.0;
        setPassiveSkill(name);
        setActiveSkill(name);
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

    public void setPassiveSkill(String name){
        switch(name){
            case "Sonara":   this.passiveSkill = new PassiveSkill("Body of Thorns", "Sonara's passive skill reflects 15% of incoming damage back to the attacker."); break;
            case "Aurelius": this.passiveSkill = new PassiveSkill("Melodic Remedy", "Aurelius's passive skill heals him for 5% of his max HP at the start of each turn."); break;
            case "Lyron":    this.passiveSkill = new PassiveSkill("Winner Takes It All", "Lyron's passive skill grants him a shield equal to 25% of the damage he deals to enemies."); break;
            default:         this.passiveSkill = new PassiveSkill("",""); break;
        }
    }
    public void setActiveSkill(String name){
        switch(name){
            case "Sonara":   this.activeSkill = new ActiveSkill("Melodic Impromptu", "Sonara's active skill adds one (1) point to her initial damage."); break;
            case "Aurelius": this.activeSkill = new ActiveSkill("Conservation", "Aurelius's active skill preserves the notes' current damage for next turn"); break;
            case "Lyron":    this.activeSkill = new ActiveSkill("Musical Roulette", "Lyron's active skill rerolls the current damage of the notes."); break;
            default:         this.activeSkill = new ActiveSkill("",""); break;
        }
    }

    public void setDamageBuff(double buff) {
        this.damageBuff = buff;
    }

    // ─── Combat Utilities ──────────────────────────────────────────────────────

//    Applies incoming damage, first absorbing through shield then HP.
//    @param damage Raw damage after enemy's attack roll.
    public void takeDamage(int damage) {
        if (damage <= 0) return;

        if (currentShield > 0) {
            int absorbed = Math.min(currentShield, damage);
            currentShield -= absorbed;
            damage        -= absorbed;
        }

        currentHp = Math.max(0, currentHp - damage);
    }


//    Restores HP by a flat amount, capped at maxHp.
    public void heal(int amount) {
        currentHp = Math.min(maxHp, currentHp + amount);
    }

//    Adds shield points, capped at maxShield.
    public void gainShield(int amount) {
        currentShield = Math.min(maxShield, currentShield + amount);
    }

    public boolean isAlive() {
        return currentHp > 0;
    }

    // ─── Progression ───────────────────────────────────────────────────────────

//    Called after defeating an enemy; tracks kill count for level-up logic.
    public void defeatedMonster() {
        monstersDefeated++;
    }

//    Levels the hero up to the given level, updating maxHp from GDD tables.
//    @param newLevel Target level (1–5).
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
        currentHp  = Math.min(maxHp, currentHp + hpGain);
    }

    public void resetStats() {
        this.currentHp        = this.maxHp;
        this.currentShield    = 0;
        this.level            = 1;
        this.monstersDefeated = 0;
        this.damageBuff       = 0.0;
    }

    // ─── Passive Skill ────────────────────────────────────────────────────────

    public void PassiveSkillEffect(Character player, Enemy monster, int damage) {;
        switch(name){
            case "Sonara":
                // Body of Thorns is handled in the Combat system when taking damage
                SonaraPassiveSkillEffect(monster, damage);
                break;
            case "Aurelius":
                // Melodic Remedy is handled in the Combat system at the start of each turn
                AureliusPassiveSkillEffect(player);
                break;
            case "Lyron":
                // Winner Takes It All is handled in the Combat system when dealing damage
                LyronPassiveSkillEffect(player, damage);
                break;
             default:
                 break;
        }
    }

    public void SonaraPassiveSkillEffect(Enemy monster, int damage) {
        int thornDamage = (int)(damage * 0.15);
        //Display update enemy HP after thorn damage

        // This thorn damage would be applied back to the enemy in the Combat system
        monster.takeDamage(thornDamage);
    }

    public void AureliusPassiveSkillEffect(Character player) {
        int healAmount = (int)(player.getMaxHp() * 0.05);
        //Display update player HP after heal

        // This heal would be applied at the start of the player's turn in the Combat system
        player.heal(healAmount);
    }

    public void LyronPassiveSkillEffect(Character player, int damageDealt) {
        int shieldAmount = (int)(damageDealt * 0.25);
        player.gainShield(shieldAmount);
    }

    // ─── Active Skill ─────────────────────────────────────────────────────────
    public int activeSkillEffect(int damage) {
        switch (this.name) {
            case "Sonara" -> {
                return damage + 1;
            }
            default -> {
                return damage;
            }
        }
    }

    public boolean activeSkillReRoll() {
        switch (this.name) {
            case "Lyron" -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }


}
