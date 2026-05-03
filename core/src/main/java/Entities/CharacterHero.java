package Entities;

import Inventory.Inventory;
import Mechanics.CombatSystem.Note;
import io.github.Zephyrdoestech.GameContext;
import java.util.Map;

public class CharacterHero {

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

    public static class Monologues {
        public String[] firstMapEntry;
        public String[] secondMapEntry;
        public String[] thirdMapEntry;

        public String[] enemyEncounterV1;
        public String[] enemyEncounterV2;
        public String[] enemyEncounterV3;
        public String[] enemyEncounterV4;

        public String[] firstLevelUp;
        public String[] secondLevelUp;
        public String[] thirdLevelUp;
        public String[] fourthLevelUp;

        public String[] firstMapExit;
        public String[] secondMapExit;
        public String[] thirdMapExit;

        public String[] preFinalBattle;
        public String[] postFinalBattleVictory;
        public String[] postFinalBattleDefeat;

        Monologues(String[] firstMapEntry, String[] secondMapEntry, String[] thirdMapEntry,
                   String[] enemyEncounterV1, String[] enemyEncounterV2, String[] enemyEncounterV3, String[] enemyEncounterV4,
                   String[] firstLevelUp, String[] secondLevelUp, String[] thirdLevelUp, String[] fourthLevelUp,
                   String[] firstMapExit, String[] secondMapExit, String[] thirdMapExit,
                   String[] preFinalBattle, String[] postFinalBattleVictory, String[] postFinalBattleDefeat){
            this.firstMapEntry = firstMapEntry;
            this.secondMapEntry = secondMapEntry;
            this.thirdMapEntry = thirdMapEntry;

            this.enemyEncounterV1 = enemyEncounterV1;
            this.enemyEncounterV2 = enemyEncounterV2;
            this.enemyEncounterV3 = enemyEncounterV3;
            this.enemyEncounterV4 = enemyEncounterV4;

            this.firstLevelUp = firstLevelUp;
            this.secondLevelUp = secondLevelUp;
            this.thirdLevelUp = thirdLevelUp;
            this.fourthLevelUp = fourthLevelUp;

            this.firstMapExit = firstMapExit;
            this.secondMapExit = secondMapExit;
            this.thirdMapExit = thirdMapExit;

            this.preFinalBattle = preFinalBattle;
            this.postFinalBattleVictory = postFinalBattleVictory;
            this.postFinalBattleDefeat = postFinalBattleDefeat;
        }
        public String[] getFirstMapEntry() { return firstMapEntry; }
        public String[] getSecondMapEntry() { return secondMapEntry; }
        public String[] getThirdMapEntry() { return thirdMapEntry; }

        public String[] getEnemyEncounterV1() { return enemyEncounterV1; }
        public String[] getEnemyEncounterV2() { return enemyEncounterV2; }
        public String[] getEnemyEncounterV3() { return enemyEncounterV3; }
        public String[] getEnemyEncounterV4() { return enemyEncounterV4; }

        public String[] getFirstLevelUp() { return firstLevelUp;}
        public String[] getSecondLevelUp() { return secondLevelUp;}
        public String[] getThirdLevelUp() { return thirdLevelUp;}
        public String[] getFourthLevelUp() { return fourthLevelUp;}

        public String[] getFirstMapExit() { return firstMapExit; }
        public String[] getSecondMapExit() { return secondMapExit; }
        public String[] getThirdMapExit() { return thirdMapExit; }

        public String[] getPreFinalBattle() { return preFinalBattle; }
        public String[] getPostFinalBattleVictory() { return postFinalBattleVictory; }
        public String[] getPostFinalBattleDefeat() { return postFinalBattleDefeat; }
    }

    // ── Fields ────────────────────────────────────────────────────────────────
    public Map<String, Integer> inventory = new java.util.HashMap<>();

    private String name;
    private String instrument;

    private int maxHp;
    private int currentHp;
    private int maxShield;
    private int currentShield;

    private PassiveSkill passiveSkill;
    private ActiveSkill  activeSkill;
    private Monologues monologues;

    private int    level;
    private int    monstersDefeated;
    private double damageBuff;

    private Inventory playerInventory;
    // ── HP Tables (GDD values per level) ──────────────────────────────────────

    private static final int[] SONARA_HP   = { 150, 175, 225, 300, 400 };
    private static final int[] AURELIUS_HP = { 150, 175, 225, 300, 400 };
    private static final int[] LYRON_HP    = { 250, 270, 310, 370, 450 };

    // ── Constructor ───────────────────────────────────────────────────────────

    public CharacterHero(String name, String instrument, int maxHp, int maxShield) {
        this.name          = name;
        this.instrument    = instrument;
        this.maxHp         = maxHp;
        this.currentHp     = maxHp;   // Start at full health
        this.maxShield     = maxShield;
        this.currentShield = 0;       // Shield starts empty until a chord grants it
        this.level         = 1;
        this.monstersDefeated = 0;
        this.damageBuff    = 0.0;
        playerInventory    = new Inventory();
        setPassiveSkill(name);
        setDialogues(name);
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

    public Inventory getPlayerInventory() { return playerInventory; }
    public void setPlayerInventory(Inventory playerInventory) { this.playerInventory = playerInventory; }

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

    public void resetDamageBuff() { this.damageBuff = 0.0; }

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

    public void setDialogues(String name){
        switch (name){
            case "Sonara":
                this.monologues = new Monologues(
                    // Map Entries
                    new String[]{"They were gone this whole time and I didn't even know.",
                        "I don't know what I'm doing.",
                        "I just know this instrument hurt one of them and I'm not putting it down."},
                    new String[]{"The instrument works.",
                        "I've seen it work. I'm not just swinging blindly anymore.",
                        "I'm scared but the anger is louder."},
                    new String[]{"I know what I'm doing now. The anger is still there but it's mine.",
                        "I'm using it.",
                        "It's not using me anymore."},

                    // Enemy Encounter
                    new String[]{"I’ve had enough of this dull.",
                        "I'm not going to let it take anyone else."},
                    new String[]{"There’s a debt of emptiness in this town, and I’m here to collect it."},
                    new String[]{"One more time.",
                        "That’s all it’s going to take to wipe that void off your face."},
                    new String[]{"You’re blocking my view.",
                        "I have a world to wake up and a family to avenge.",
                        "Move."},

                    // Level Ups
                    new String[]{"The strings are cleaner now.",
                        "I won't lose this clarity again; this feeling of striking back.",
                        "I won't stop until there's nowhere left for the shadows to hide."},
                    new String[]{"This feeling is getting stronger.",
                        "I know where to hit them.",
                        "Vengeance is clean and sharp.",
                        "I get the pattern now."},
                    new String[]{"I understand it now.",
                        "How it works.",
                        "What it does.",
                        "One step closer to avenging my family."},
                    new String[]{"There is no turning back.",
                        "The new feeling is a permanent promise etched onto my soul.",
                        "This \"sound\"...",
                        "They will know how much it hurts."},

                    // Map Exits
                    new String[]{"Don't think.",
                        "Don't stop.",
                        "Find the next one.",
                        "If I stop moving that empty feeling is going to swallow me again."},
                    new String[]{"The void down here feels like the one I grew up in.",
                        "Blank and shivering.",
                        "I spent my whole life inside that lie and I didn't even know."},
                    new String[]{"This place dug into the grief I've been burying under all the rage.",
                        "It hurt.",
                        "But I felt it and kept going.",
                        "I didn't know I could do that."},

                    // Pre Final Battle
                    new String[]{"I came here only for my family.",
                        "But somewhere along the way I started caring about everyone this void took from.",
                        "I didn't plan for that. But it's real."},
                    // Post Final Battle Victory
                    new String[]{"Do you feel that?",
                        "That’s not just a sound — it’s my heart beating.",
                        "I spent so long drowning in my own anger, but this experience...",
                        "It’s different.",
                        "It’s life.",
                        "The emptiness is filled, and for the first time, my thoughts go beyond the void.",
                        "I’m finally free."},
                    // Post Final Battle Defeat
                    new String[]{"No... not like this.",
                        "I can’t let the emptiness take over again.",
                        "Everything is getting so cold...",
                        "I just wanted to feel...",
                        "one last...",
                        "sound..."}
                );
                break;
            case "Aurelius":
                this.monologues = new Monologues(
                    // Map Entries
                    new String[]{"I'm not a fighter.",
                        "But people got hurt while I was safe inside and my family didn't flinch.",
                        "I can't be that.",
                        "I'm going out there."},
                    new String[]{"I'm still here.",
                        "I haven't turned back.",
                        "A week ago I was watching this from a window.",
                        "I don't fully recognize myself and I think that's okay."},
                    new String[]{"I used to do this out of guilt.",
                        "But the guilt is lighter now and I'm still here.",
                        "Still choosing this.",
                        "That means it's not about the debt anymore.",
                        "It's just who I am."},

                    // Enemy Encounter
                    new String[]{"You are the apathy that swallowed this world.",
                        "My instrument will be the shield you cannot break."},
                    new String[]{"My breath will be the barrier you cannot cross.",
                        "Step no further."},
                    new String[]{"A challenging task.",
                        "I have nothing to hesitate",
                        "I am ready."},
                    new String[]{"A simple confrontation.",
                        "My breath is steady, and my courage is absolute."},

                    // Level Ups
                    new String[]{"I can see the fear, and my breath pushes it away.",
                        "This instrument is a boundary.",
                        "I will keep playing for those who can't."},
                    new String[]{"This feeling is lighter now, less burdened by my past."
                        ,"This is more than debt; it's hope.",
                        "The cruelty of my family ends with every breath I give."},
                    new String[]{"They try to break my focus, but this new experience keeps weaving faster.",
                        "I am not brittle",
                        "I am layered resilience.",
                        "My \"sound\" finds everyone waiting beneath the surface."},
                    new String[]{"The path is clear.",
                        "This is the rebuilding.",
                        "The trust I feel is a foundation.",
                        "I play for a world that knows the truth of justice."},

                    // Map Exits
                    new String[]{"We weren't better off.",
                        "We were just further from the consequences.",
                        "I didn't understand that before.",
                        "Not really."},
                    new String[]{"Dad said what I choose defines me.",
                        "I used to think that was just something nice he said.",
                        "Standing here still going, I think I finally get it."},
                    new String[]{"I didn't leave everything behind just to stop here.",
                        "And I'm not moving because I'm scared of my family's legacy anymore.",
                        "I'm moving because I actually want to."},

                    // Pre Final Battle
                    new String[]{"I'm not the kid sneaking out of a mansion to fix what his family broke.",
                        "I don't know when that changed but it did.",
                        "I'm here because I choose to be."},
                    // Post Final Battle Victory
                    new String[]{"The sound of apathy is finished.",
                        "You tried to turn the world into a grave, but this experience is meant to be lived, not feared.",
                        "My shame is lifted with every sound that returns to the air.",
                        "We will rebuild, and we will do it with life."},
                    // Post Final Battle Defeat
                    new String[]{"My strength... it wavered.",
                        "I failed to protect the people.",
                        "The weight of this stillness... It’s too heavy.",
                        "My legacy can't go on..."}
                );
                break;
            case "Lyron":
                this.monologues = new Monologues(
                    // Map Entries
                    new String[]{"They made me hide and I let them and now they're gone.",
                        "I grabbed the instrument because it was next to her.",
                        "I didn't know what else to do."},
                    new String[]{"I got one.",
                        "Eyes closed, hands shaking, but I got one.",
                        "The instrument feels just like how she held my hand.",
                        "I think that means keep going."},
                    new String[]{"Something changed and I didn't notice it happening.",
                        "I stopped just surviving and started actually fighting.",
                        "My eyes are still half closed but I'm swinging first now."},

                    // Enemy Encounter
                    new String[]{"Ugh, not again!",
                        "Just... stay back!"},
                    new String[]{"It’s just a shadow...",
                        "it’s just a shadow...",
                        "I’ve done this before, it’ll be easy."},
                    new String[]{"I... I’m not hiding anymore!",
                        "This instrument says I can do this!"},
                    new String[]{"One more sound and I can go back to being safe.",
                        "Let’s make it quick!"},

                    // Level Ups
                    new String[]{"I watched it vanish.",
                        "The guilt is heavy, but this feeling reminds me of my mother.",
                        "I can't be a coward if I have this.",
                        "I have to be strong enough to see the next shadow fall."},
                    new String[]{"The guilt is a weapon, a promise.",
                        "This instrument threatens them.",
                        "I'll use every trick to avoid fighting face-to-face."},
                    new String[]{"I couldn't run.",
                        "The fear is there, but I played through it.",
                        "This instrument is my witness.",
                        "I will make the thing that ruined my life suffer."},
                    new String[]{"I can't undo what happened, but I can finish this.",
                        "I'm afraid, yes, but the need to see this through is louder than my fear.",
                        "This final music is for them."},

                    // Map Exits
                    new String[]{"I don't feel brave.",
                        "I feel like the reason this happened.",
                        "But if I stop moving I have to sit with all of it and I'm not ready for that."},
                    new String[]{"She never once made me feel like a burden",
                        "Not once.",
                        "I'm starting to think that means something I haven't let myself believe yet."},
                    new String[]{"This place wants me to feel like that kid hiding in the dark.",
                        "But that kid got up.",
                        "That kid came all the way here.",
                        "Maybe I was never just weak.",
                        "Maybe I just never had a reason big enough."},

                    // Pre Final Battle
                    new String[]{"I can't bring them back.",
                        "I'm saying it out loud now. I can't.",
                        "But I can finish this and I can stop punishing myself for being the one who lived."},
                    // Post Final Battle Victory
                    new String[]{"I did it.",
                        "I didn't run.",
                        "They told me I was too small, too quiet, but this instrument made a moment that can't be erased.",
                        "They are can rest now.",
                        "The fear is gone, and I’ll make sure the world never has to be afraid of the dark or the silence ever again."},
                    // Post Final Battle Defeat
                    new String[]{"I'm sorry...",
                        "I wasn't brave enough.",
                        "I couldn’t use it properly... and I can't... I can't hear them anymore.",
                        "Please, don't let it go... empty..."});
                break;
        }
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setMonstersDefeated(int count) {
        this.monstersDefeated = count;
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
    public void onDamageDealt(CharacterHero self, Enemy target, int damage) {
        if (name.equals("Lyron")) {
            int shieldAmount = (int)(damage * 0.15f);
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
    public void onTurnEnd(CharacterHero self) {
        if (name.equals("Aurelius") && self.getHp() > 0) {
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
    }
}
