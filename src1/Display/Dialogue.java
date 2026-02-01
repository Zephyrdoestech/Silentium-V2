package Display;

import java.util.Random;
import Player.Character;

public class Dialogue implements CharacterDialogue {
    private final Random random = new Random();
    private TextDisplay text = new TextDisplay();

    private String[] quotes = new String[5];

    public void setQuotes(String[] quotes) {
        this.quotes = quotes;
    }

    public String[] getQuotes() {
        return quotes;
    }

    public void opening(Character character) {
        text.lineBreak();
        text.printNarration(character.openingNarrative);
        text.pause();
    }

    // --- Storyteller Implementation (Specific Story Sequences) ---

    public void firstLevelUp(Character character) {
        if (character.name.equals("Sonara")) {
            text.lineBreak();
            text.printSystemMessage("SONARA LEVEL UP: Vicious, clean force.");
            text.printNarration("Sonara has leveled up. Vicious, clean force. Her Banjo found its edge.");
            System.out.println();
            text.printDialogue(character, "The strings are cleaner now. I won't lose this clarity again; this feeling of striking back.\n\tI won't stop until there's nowhere left for the shadows to hide.");
            text.pause();
            text.lineBreak();
        } else if (character.name.equals("Aurelius")) {
            text.lineBreak();
            text.printSystemMessage("AURELIUS LEVEL UP: Resilient barrier.");
            text.printNarration("Aurelius has leveled up. Resilient barrier. His breath stabilized the shield.");
            System.out.println();
            text.printDialogue(character, "I can see the fear, and my breath pushes it away. This instrument is a boundary.\n\tI will keep playing for those who can't.");
            text.pause();
            text.lineBreak();
        } else if (character.name.equals("Lyron")) {
            text.lineBreak();
            text.printSystemMessage("LYRON LEVEL UP: Complex, sweeping patterns.");
            text.printNarration("Lyron has leveled up. Complex, sweeping patterns. He found structure for engagement.");
            System.out.println();
            text.printDialogue(character, "I watched it vanish. The guilt is heavy, but the sound feels like my mother.\n\tI can't be a coward if I have this. I have to be strong enough to see the next shadow fall.");
            text.pause();
            text.lineBreak();
        }
    }

    public void secondLevelUp(Character character) {
        if (character.name.equals("Sonara")) {
            text.lineBreak();
            text.printSystemMessage("SONARA LEVEL 2: Deeply disruptive sound.");
            text.printNarration("Sonara has leveled up. Deeply disruptive sound. The rhythm broke their cohesion.");
            System.out.println();
            text.printDialogue(character, "The rhythm is focused. I know where to hit them.\n\tVengeance is a clean, sharp note, and I have the pattern now.");
            text.pause();
            text.lineBreak();
        } else if (character.name.equals("Aurelius")) {
            text.lineBreak();
            text.printSystemMessage("AURELIUS LEVEL 2: Layered harmonies.");
            text.printNarration("Aurelius has leveled up. Layered harmonies. The protective shield expanded.");
            System.out.println();
            text.printDialogue(character, "The music is lighter now, less burdened by my past. This is more than debt; it's hope.\n\tThe cruelty of my family ends with every breath I give.");
            text.pause();
            text.lineBreak();
        } else if (character.name.equals("Lyron")) {
            text.lineBreak();
            text.printSystemMessage("LYRON LEVEL 2: Intricate arpeggios.");
            text.printNarration("Lyron has leveled up. Intricate arpeggios. Sound-webs trapped the enemy.");
            System.out.println();
            text.printDialogue(character, "The guilt is a weapon, a promise. The Harp spins webs that trap the shadows.\n\tI'll use every trick to avoid fighting face-to-face.");
            text.pause();
            text.lineBreak();
        }
    }

    public void thirdLevelUp(Character character) {
        if (character.name.equals("Sonara")) {
            text.lineBreak();
            text.printSystemMessage("SONARA LEVEL 3: Calculated sonic bursts.");
            text.printNarration("Sonara has leveled up. Calculated sonic bursts. The hunt became surgical.");
            System.out.println();
            text.printDialogue(character, "No wasted motion, no wasted sound.\n\tMy rage isn't a fire anymore—it’s the scalpel. Every note cuts exactly where it must.\n\tThis is not feeling; this is precision.");
            text.pause();
            text.lineBreak();
        } else if (character.name.equals("Aurelius")) {
            text.lineBreak();
            text.printSystemMessage("AURELIUS LEVEL 3: Purifying trills.");
            text.printNarration("Aurelius has leveled up. Purifying trills. The sound banished mental corruption.");
            System.out.println();
            text.printDialogue(character, "They try to break my focus, but the notes keep weaving, faster.\n\tI am not brittle; I am layered resilience. My music finds everyone waiting beneath the surface.");
            text.pause();
            text.lineBreak();
        } else if (character.name.equals("Lyron")) {
            text.lineBreak();
            text.printSystemMessage("LYRON LEVEL 3: Tragic clarity.");
            text.printNarration("Lyron has leveled up. Tragic clarity. The Harp struck with the weight of his loss.");
            System.out.println();
            text.printDialogue(character, "I couldn't run. The fear is there, but I played through it.\n\tThis Harp is my witness. I will make the thing that ruined my life suffer.");
            text.pause();
            text.lineBreak();
        }
    }


    public void fourthLevelUp(Character character) {
        if (character.name.equals("Sonara")) {
            text.lineBreak();
            text.printSystemMessage("SONARA LEVEL 4: Final signature.");
            text.printNarration("Sonara has leveled up. Final signature. The sound became retribution's voice.");
            System.out.println();
            text.printDialogue(character, "There is no turning back.\n\tThe sound is a permanent promise etched onto my soul. I will play the final, deafening chord.");
            text.pause();
            text.lineBreak();
        } else if (character.name.equals("Aurelius")) {
            text.lineBreak();
            text.printSystemMessage("AURELIUS LEVEL 4: Voice of justice.");
            text.printNarration("Aurelius has leveled up. Voice of justice. His sound carried the promise of the future.");
            System.out.println();
            text.printDialogue(character, "The path is clear. This is the rebuilding.\n\tThe trust I feel is a foundation. I play for a world that knows the quiet truth of justice.");
            text.pause();
            text.lineBreak();
        } else if (character.name.equals("Lyron")) {
            text.lineBreak();
            text.printSystemMessage("LYRON LEVEL 4: Quiet dignity.");
            text.printNarration("Lyron has leveled up. Quiet dignity. The instrument was seamlessly integrated.");
            System.out.println();
            text.printDialogue(character, "I can't undo what happened, but I can finish this.\n\tI'm afraid, yes, but the need to see this through is louder than my fear.\nThis final music is for them.");
            text.pause();
            text.lineBreak();
        }
    }
    // --- Randomized Post-Combat Dialogue Implementation ---


    public void victoryDialogue(Character character) {
        if (character.name.equals("Sonara")) {
            text.lineBreak();
            text.printNarration("Sonara won. A rush of cold, satisfying certainty replaced the heat of the fight. The air felt clear.");
            String quote = character.narrative.getQuotes()[random.nextInt(character.narrative.getQuotes().length)];
            text.printDialogue(character, quote);
            text.lineBreak();
        } else if (character.name.equals("Aurelius")) {
            text.lineBreak();
            text.printNarration("Aurelius won. A profound sense of relief that the vulnerable were safe. His duty was performed.");
            String quote = character.narrative.getQuotes()[random.nextInt(character.narrative.getQuotes().length)];
            text.printDialogue(character, quote);
            text.lineBreak();
        } else if (character.name.equals("Lyron")) {
            text.lineBreak();
            text.printNarration("Aurelius won. A profound sense of relief that the vulnerable were safe. His duty was performed.");
            String quote = character.narrative.getQuotes()[random.nextInt(character.narrative.getQuotes().length)];
            text.printDialogue(character, quote);
            text.lineBreak();
        }
    }
    // --- Final Boss Maestro Syozan Dialogue (Formatted for readability) ---

    public void bossPreBattleDialogue() {
        text.lineBreak();
        text.printNarration("MAESTRO SYOZAN appears, radiating a crushing, absolute silence.");
        System.out.println();
        text.printDialogue("Ah, another one. A child of the silence, yet obsessed with the deafening clang of sound. Did you truly believe\n" +
                "\t\t\t\t\t\t\tyour little noise could lead you here? How pathetic. You found my work—the eternal silence —  and instead of\n" +
                "\t\t\t\t\t\t\trealizing its perfection, you declared it a disease. You confuse chaos for life, and order for cruelty.");
        text.lineBreak();
    }

    public void bossPostBattleDialogue() {
        text.lineBreak();
        text.printDialogue("No... you fool! You have ruined the perfection! You confuse this chaos for life, and order for cruelty!\n" +
                "\t\t\t\t\t\t\tI imposed this silence out of pity, stopping the world from screaming itself into oblivion, gifting humanity\n" +
                "\t\t\t\t\t\t\tabsolute peace from the sheer, agonizing volume of its own sorrow. You think this noise is freedom? \n" +
                "\t\t\t\t\t\t\tIt is the sound of inevitable pain and hatred, magnified a thousand times! You will live to regret this freedom,\n" +
                "\t\t\t\t\t\t\tchild; you will beg for the quiet I gifted you.");
        System.out.println();
        text.printNarration("Maestro Syozan dissipates into silence as the world rushes with sound.");
    }


    // --- Final Boss Victory Moments (EXACTLY from document) ---

    public void finalVictory(Character character) {
        if (character.name.equals("Sonara")) {
            text.lineBreak();
            text.printSystemMessage("VICTORY! THE SILENCE IS SHATTERED!");
            text.printNarration("Sonara won. The shattering sound of her Banjo ended Syozan's stillness. The deafening rage in her heart was finally replaced by clean, profound peace, and the world was free.");
            System.out.println();
            text.printDialogue(character, "It’s over. The deafening silence is gone, and so is the rage. I gave them back the world, not just my anger; I am finally free.");
        } else if (character.name.equals("Aurelius")) {
            text.lineBreak();
            text.printSystemMessage("VICTORY! CONVICTION RESTORED!");
            text.printNarration("Aurelius won. Syozan dissolved beneath his soaring conviction. The crushing shame of his past lifted, leaving him resolute and ready to lead the world into its noisy, just future.");
            System.out.println();
            text.printDialogue(character, "The silence is shattered, and the world breathes. The battle is over, and my shame is finally lifted. I will listen to the sounds of freedom, and I will lead the rebuilding.");

        } else if (character.name.equals("Lyron")) {
            text.lineBreak();
            text.printSystemMessage("VICTORY! UNBURDENED GRIEF!");
            text.printNarration("Lyron won. The Maestro's defeat immediately banished his fear. The cold resolve of vengeance gave way to unburdened grief, marking him as a survivor finally at peace with his bravery.");
            System.out.println();
            text.printDialogue(character, "It’s finished. They are avenged. The fear that drove me is gone; I will live for them, never forgetting the bravery I found.");
        }
    }
}