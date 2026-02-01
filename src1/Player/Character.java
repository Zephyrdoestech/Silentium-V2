package Player;

import Display.*;
import Combat.Combat;
import Inventory.Inventory;

import java.text.DecimalFormat;

public class Character {
    Dialogue dialogue = new Dialogue();
    TextDisplay text = new TextDisplay();
    DecimalFormat df = new DecimalFormat("0.00");

    public String name;
    public String instrument;
    public String openingNarrative;
    public PassiveSkill ps = new PassiveSkill();
    public ActiveSkill as = new ActiveSkill();
    private int hp;
    private int maxHp;
    private int maxShield;
    private int shield;
    private int level;
    private double damageBuff = 1.0;
    private int monstersDefeated = 0;
    private int map;
    private int lives = 3;
    public Dialogue narrative = new Dialogue();

    private int buffTurnsRemaining = 0;
    private int barrierTurnsRemaining = 0;
    private boolean ignoreBDiminished = false;
    private int minorChordBonus = 0;
    private int majorChordBonus = 0;

    public int getMaxShield() { return maxShield; }
    public void setMaxShield(int maxShield) { this.maxShield = maxShield; }

    public int getShield() { return shield; }
    public void setShield(int shield) { this.shield = shield; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getMonstersDefeated() { return monstersDefeated; }
    // Increments the count of defeated monsters.
    public void defeatedMonster() { this.monstersDefeated++; };

    public int getMap() { return map; }
    public void setMap(int map) {
        if(this.map < 3){
            this.map = map;
        }
    }

    public int getMaxHp() { return maxHp; }
    public void setMaxHp(int maxHp) { this.maxHp = maxHp; }

    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = hp; }

    public double getDamageBuff() { return damageBuff; }
    public void setDamageBuff(double buff) { this.damageBuff = buff; }

    public int getLives() { return lives; }
    public void setLives(int lives) { this.lives = lives; }
    public void playerDied() { this.lives--; }

    private Inventory inventory = new Inventory();
    public Inventory getInventory() {
        return inventory;
    }

    public void displayName() {
        System.out.println("Name: " + name);
        System.out.println("Instrument: " + instrument);
        System.out.println("HP: " + hp);
    }

    public void addShield(int amount) {
        shield += amount;
        if (shield > maxShield) shield = maxShield;
    }

    public void takeDamage(int dmg) {
        if (barrierTurnsRemaining > 0) {
            text.printSystemAnnouncement("Barrier active! No damage taken this turn!");
            return;
        }

        if (shield > 0) {
            int reduced = dmg - shield;
            if (reduced < 0) reduced = 0;
            shield -= dmg;
            if (shield < 0) shield = 0;
            setHp(getHp() - reduced);
        } else {
            setHp(getHp() - dmg);
        }
    }

    public int heal(int amount) {
        setHp(Math.min(getHp() + amount, maxHp));
        return (int)amount;
    }

    public void levelUp(Character player){
        if(map==1){
            if(level < 3){
                level++;
                if(level==2) dialogue.firstLevelUp(player);
                if(level==3) dialogue.secondLevelUp(player);
                if(name.equals("Sonara") || name.equals("Aurelius")) maxHp += (int) ((level - 1) * 25);
                if(name.equals("Lyron")) maxHp += (int) ((level - 1) * 20);

                text.printSystemMessage("Player leveled up!");
            } else {
                text.pause();
                Combat.inventory.tryDrop();
                Combat.inventory.showInventory();
                text.shortbreak();
            }
        } else if(map==2){
            if(level < 5){
                level++;
                if(level==4) dialogue.thirdLevelUp(player);
                if(level==5) dialogue.fourthLevelUp(player);
                if(name.equals("Sonara") || name.equals("Aurelius")) maxHp += (int) ((level - 1) * 25);
                if(name.equals("Lyron")) maxHp += (int) ((level - 1) * 20);
                text.printSystemMessage("Player leveled up!");
            } else{
                text.pause();
                Combat.inventory.tryDrop();
                Combat.inventory.showInventory();
            }
        }
        text.pause();
    }

    public void activateTemporaryBuff(double buffMultiplier, int turns) {
        this.damageBuff = buffMultiplier;
        this.buffTurnsRemaining = turns;
        text.printSystemMessage("Damage buff applied: " + df.format(((buffMultiplier - 1) * 100)) + "% for " + turns + " turns!");
    }

    public void activateBarrier(int turns) {
        this.barrierTurnsRemaining = turns;
        text.printSystemMessage("Barrier activated for " + turns + " turn(s)!");
    }

    public void setIgnoreBDiminished(boolean state) {
        this.ignoreBDiminished = state;
    }
    public boolean canIgnoreBDiminished() {
        return ignoreBDiminished;
    }

    public void addMinorChordUse(int amount) {
        minorChordBonus += amount;
    }
    public int getMinorChordBonus() {
        return minorChordBonus;
    }

    public void addMajorChordUse(int amount) {
        majorChordBonus += amount;
    }
    public int getMajorChordBonus() {
        return majorChordBonus;
    }

    public void updateTurnEffects() {
        if (buffTurnsRemaining > 0) {
            buffTurnsRemaining--;
            if (buffTurnsRemaining == 0) {
                damageBuff = 1.0;
                text.printSystemAnnouncement("Your damage buff has worn off!\n");
            }
        }

        if (barrierTurnsRemaining > 0) {
            barrierTurnsRemaining--;
            if (barrierTurnsRemaining == 0) {
                text.printSystemAnnouncement("Your barrier has faded!\n");
            }
        }

        if (ignoreBDiminished) {
            text.printSystemMessage("Resolved Dissonance effect is active for next B Diminished.\n");
        }
    }

    public void resetTemporaryEffects() {
        damageBuff = 1.0;
        buffTurnsRemaining = 0;

        barrierTurnsRemaining = 0;

        ignoreBDiminished = false;

        minorChordBonus = 0;
        majorChordBonus = 0;
    }
}
