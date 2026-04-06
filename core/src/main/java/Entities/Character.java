package Entities;

import Inventory.Item;
import Mechanics.CombatSystem.Note;
import io.github.Zephyrdoestech.GameContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Character {

    // ── Inner Classes ─────────────────────────────────────────────────────────

    public static class PassiveSkill {
        public final String psName;
        public final String psDescription;

        PassiveSkill(String psName, String psDescription) {
            this.psName        = psName;
            this.psDescription = psDescription;
        }

        public String getPsName(){return psName;}
        public String getPsDescription(){return psDescription;}
    }

    public static class ActiveSkill {
        public final String asName;
        public final String asDescription;
        public boolean activate;

        ActiveSkill(String asName, String asDescription) {
            this.asName        = asName;
            this.asDescription = asDescription;
            this.activate      = false;
        }

        public String getAsName(){return asName;}
        public String getAsDescription(){return asDescription;}
    }

    public static class InventoryEntry {
        private final Item item;
        private int quantity;

        public InventoryEntry(Item item, int quantity) {
            this.item = item;
            this.quantity = Math.max(0, quantity);
        }

        public Item getItem() {
            return item;
        }

        public int getQuantity() {
            return quantity;
        }

        public void addQuantity(int amount) {
            quantity = Math.max(0, quantity + amount);
        }

        public void removeQuantity(int amount) {
            quantity = Math.max(0, quantity - amount);
        }
    }

    // ── Fields ────────────────────────────────────────────────────────────────

    private String name;
    private String instrument;

    private int maxHp;
    private int currentHp;
    private int maxShield;
    private int currentShield;

    private PassiveSkill passiveSkill;
    private ActiveSkill  activeSkill;

    private int    level;
    private int    monstersDefeated;
    private double damageBuff; // Multiplier bonus from chords/skills (e.g. 0.20 = +20%)
    private boolean inCombat; // Track combat state

    public Map<String, Integer> inventory = new HashMap<>();
    private final Map<String, InventoryEntry> consumableInventory;

    // ── HP Tables (GDD values per level) ──────────────────────────────────────

    private static final int[] SONARA_HP   = { 150, 175, 225, 300, 400 };
    private static final int[] AURELIUS_HP = { 150, 175, 225, 300, 400 };
    private static final int[] LYRON_HP    = { 250, 270, 310, 370, 450 };

    // ── Constructor ───────────────────────────────────────────────────────────

    public Character(String name, String instrument, int maxHp, int maxShield) {
        this.name          = name;
        this.instrument    = instrument;
        this.maxHp         = maxHp;
        this.currentHp     = maxHp;   // Start at full health
        this.maxShield     = maxShield;
        this.currentShield = 0;       // Shield starts empty until a chord grants it
        this.level         = 1;
        this.monstersDefeated = 0;
        this.damageBuff    = 0.0;
        this.inCombat      = false;   // Initialize combat state
        this.consumableInventory = new LinkedHashMap<>();
        setPassiveSkill(name);
        setActiveSkill(name);
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public String getName()        { return name; }
    public String getInstrument()  { return instrument; }

    public int getHp()             { return currentHp; }
    public int getMaxHp()          { return maxHp; }
    public int getShield()         { return currentShield; }
    public int getMaxShield()      { return maxShield; }

    public int    getLevel()            { return level; }
    public int    getMonstersDefeated() { return monstersDefeated; }
    public double getDamageBuff()       { return damageBuff; }

    public PassiveSkill getPassiveSkill() { return passiveSkill; }
    public ActiveSkill  getActiveSkill()  { return activeSkill; }

    public List<InventoryEntry> getConsumableInventoryEntries() {
        List<InventoryEntry> entries = new ArrayList<>();
        for (InventoryEntry entry : consumableInventory.values()) {
            if (entry != null && entry.getQuantity() > 0) {
                entries.add(entry);
            }
        }
        return entries;
    }

    public int getItemQuantity(String itemName) {
        InventoryEntry entry = consumableInventory.get(itemName);
        return entry == null ? 0 : entry.getQuantity();
    }

    // ── Setters ───────────────────────────────────────────────────────────────

    public void setHp(int hp) {
        this.currentHp = Math.max(0, Math.min(hp, maxHp));
    }

    public void setShield(int shield) {
        this.currentShield = Math.max(0, Math.min(shield, maxShield));
    }

    public void setMaxHp(int maxHp) {
        this.maxHp     = maxHp;
        this.currentHp = Math.min(this.currentHp, maxHp);
    }

    public void setDamageBuff(double buff) {
        this.damageBuff = buff;
    }

    public void setInCombat(boolean inCombat) {
        this.inCombat = inCombat;
    }

    public boolean isInCombat() {
        return inCombat;
    }

    public void setPassiveSkill(String name) {
        switch (name) {
            case "Sonara":
                this.passiveSkill = new PassiveSkill("Body of Thorns",
                    "Sonara's passive skill reflects 15% of incoming damage back to the attacker.");
                break;
            case "Aurelius":
                this.passiveSkill = new PassiveSkill("Melodic Remedy",
                    "Aurelius's passive skill heals him for 5% of his max HP at the start of each turn.");
                break;
            case "Lyron":
                this.passiveSkill = new PassiveSkill("Winner Takes It All",
                    "Lyron's passive skill grants him a shield equal to 25% of the damage he deals.");
                break;
            default:
                this.passiveSkill = new PassiveSkill("", "");
                break;
        }
    }

    public void setActiveSkill(String name) {
        switch (name) {
            case "Sonara":
                this.activeSkill = new ActiveSkill("Melodic Impromptu",
                    "Sonara's active skill adds one (1) point to her initial damage.");
                break;
            case "Aurelius":
                this.activeSkill = new ActiveSkill("Conservation",
                    "Aurelius's active skill preserves the notes' current damage for next turn.");
                break;
            case "Lyron":
                this.activeSkill = new ActiveSkill("Musical Roulette",
                    "Lyron's active skill rerolls the current damage of the notes.");
                break;
            default:
                this.activeSkill = new ActiveSkill("", "");
                break;
        }
    }

    // ── Inventory Methods ─────────────────────────────────────────────────────

    public void addItem(String itemName, int amount) {
        if (itemName == null || itemName.trim().isEmpty() || amount <= 0) return;
        inventory.put(itemName, inventory.getOrDefault(itemName, 0) + amount);
    }

    public void addItem(Item item) {
        addItem(item, 1);
    }

    public void addItem(Item item, int quantity) {
        if (item == null || quantity <= 0) return;

        InventoryEntry entry = consumableInventory.get(item.getName());
        if (entry == null) {
            consumableInventory.put(item.getName(), new InventoryEntry(item, quantity));
        } else {
            entry.addQuantity(quantity);
        }

        addItem(item.getName(), quantity);
    }

    public boolean removeItem(String itemName) {
        return removeItem(itemName, 1);
    }

    public boolean removeItem(String itemName, int quantity) {
        if (itemName == null || quantity <= 0) return false;

        InventoryEntry entry = consumableInventory.get(itemName);
        if (entry == null || entry.getQuantity() < quantity) {
            Integer currentCount = inventory.get(itemName);
            if (currentCount == null || currentCount < quantity) return false;

            int newCount = currentCount - quantity;
            if (newCount <= 0) {
                inventory.remove(itemName);
            } else {
                inventory.put(itemName, newCount);
            }
            return true;
        }

        entry.removeQuantity(quantity);
        if (entry.getQuantity() <= 0) {
            consumableInventory.remove(itemName);
        }

        Integer currentCount = inventory.get(itemName);
        if (currentCount != null) {
            int newCount = currentCount - quantity;
            if (newCount <= 0) {
                inventory.remove(itemName);
            } else {
                inventory.put(itemName, newCount);
            }
        }

        return true;
    }

    public boolean useItem(String itemName) {
        InventoryEntry entry = consumableInventory.get(itemName);
        if (entry == null || entry.getQuantity() <= 0) return false;

        entry.getItem().applyEffect(this);
        return removeItem(itemName, 1);
    }

    public Item getItemByName(String itemName) {
        InventoryEntry entry = consumableInventory.get(itemName);
        return entry == null ? null : entry.getItem();
    }

    // ── Combat Utilities ──────────────────────────────────────────────────────

    /**
     * Applies incoming damage, first absorbing through shield then HP.
     * @param damage Raw damage value.
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

    /** Restores HP by a flat amount, capped at maxHp. */
    public void heal(int amount) {
        currentHp = Math.min(maxHp, currentHp + amount);
    }

    /** Adds shield points, capped at maxShield. */
    public void gainShield(int amount) {
        currentShield = Math.min(maxShield, currentShield + amount);
    }

    public boolean isAlive() {
        return currentHp > 0;
    }

    // ── Progression ───────────────────────────────────────────────────────────

    /** Called after defeating an enemy; tracks kill count for level-up logic. */
    public void defeatedMonster() {
        monstersDefeated++;
    }

    /**
     * Levels the character up to the given level, updating maxHp from GDD tables.
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
        maxHp     = newMaxHp;
        currentHp = Math.min(maxHp, currentHp + hpGain);
    }

    // ── Passive Skill Effects ─────────────────────────────────────────────────

    /**
     * Called after the player deals damage to an enemy.
     * Lyron: gains shield equal to 25% of damage dealt.
     *
     * @param self   the character who attacked
     * @param target the enemy that was hit
     * @param damage the damage that was dealt
     */
    public void onDamageDealt(Character self, Enemy target, int damage) {
        if (name.equals("Lyron")) {
            int shieldAmount = (int)(damage * 0.25f);
            self.gainShield(shieldAmount);
        }
    }

    /**
     * Called after the player receives damage from an enemy.
     * Sonara: reflects 15% of received damage back to the attacker.
     *
     * @param source the enemy that attacked
     * @param damage the damage received
     */
    public void onDamageReceived(Enemy source, int damage) {
        if (name.equals("Sonara")) {
            int thornDamage = (int)(damage * 0.15f);
            source.takeDamage(thornDamage);
        }
    }

    /**
     * Called at the end of each round, after the enemy attack resolves.
     * Aurelius: heals 5% of max HP at the end of each turn.
     *
     * @param self the character whose turn just ended
     */
    public void onTurnEnd(Character self) {
        if (name.equals("Aurelius")) {
            int healAmount = (int)(self.getMaxHp() * 0.05f);
            self.heal(healAmount);
        }
    }

    // ── Active Skill ──────────────────────────────────────────────────────────

    /**
     * Applies this character's active skill damage modifier.
     * Called during attack resolution if the skill was used this turn.
     * @param damage the current initial damage value
     * @return the modified damage value
     */
    public int activeSkillEffect(int damage) {
        if (name.equals("Sonara")) {
            return damage + 1;
        }
        return damage;
    }

    /**
     * Called when the player confirms "Use" in the skill menu.
     * Executes the character's active skill and writes to the combat log.
     *
     * @param noteHandler the current Note handler (skills may reroll or lock notes)
     * @param ctx         the current GameContext (for writing to combatLog)
     */
    public void useActiveSkill(Note noteHandler, GameContext ctx) {
        switch (name) {
            case "Aurelius":
                noteHandler.lockNoteDamage();
                ctx.combatLog = name + " used Conservation! Note damages locked.";
                break;
            case "Lyron":
                noteHandler.rollNotes();
                ctx.combatLog = name + " used Musical Roulette! Notes rerolled.";
                break;
            case "Sonara":
                ctx.combatLog = name + " used Melodic Impromptu! +1 to initial damage.";
                break;
            default:
                break;
        }
    }

    public void resetStats() {
        this.setHp(this.getMaxHp());
        this.setShield(0);
        this.setInCombat(false); // Reset combat state
    }
}
