package Display;

import java.util.Scanner;
import Main.Task;
import Player.Character; // added import for the project's Character class

public class DisplayStory {
    private final Scanner sc = new Scanner(System.in);
    private final TextDisplay text = new TextDisplay();
    Task task = new Task();

    /**
     * Prints the entire game lore by calling the section methods.
     */
    public void displayFullLore() {
        text.lineBreak();
        text.printGameAnnouncement("\t\t\t\t\t\t\t\t\t\t\t--- SILENTIUM: COMPLETE GAME LORE DOCUMENTATION ---");
        task.load(1);
        text.lineBreak();

        displayWorldSettings();             text.pause();
        displayPlotSummary();               text.pause();
        displayCharacterBiographies();

        text.lineBreak();
        text.printSystemAnnouncement("\t\t\t\t\t\t\t\t\t\t\t\t\t\t--- DOCUMENTATION END ---");
        text.lineBreak();
        task.load(3);
    }

    /**
     * Prints the opening sequence of the game.
     */
    public void displayOpeningSequence(Character player) {
        text.lineBreak();
        text.printNarration("\tIn a world plunged into eternal silence—no voices, no songs, not even the whisper of wind—humanity drifts in a bleak, lifeless existence.");
        task.delay(2);
        text.yellowText("\t\t\tUntil a mysterious bell rings across the land, awakening shadowy monsters born from the fear of this silence.");
        task.delay(2);
        text.yellowText("\t\t\tYou are " + player.name + ", one of the Children of Silence, born in this quiet age and wielding a unique instrument: " + player.instrument + ".");
        task.delay(2);
        text.yellowText("\t\t\tMusic is your weapon and your hope. Only by playing can you weaken the encroaching shadows and restore sound to the world.");
        task.delay(2);
        text.yellowText("\t\t\tWill you bring back the world’s lost voice, or will darkness claim the last note?\n");
        task.delay(2);
    }

    /**
     * Prints the setting and environment details.
     */
    public void displayWorldSettings() {
        text.printSystemMessage("I. WORLD SETTINGS");
        text.shortbreak();

        text.blueText("\t\tRUINED TOWN OF ECHOES");
        text.printNarration("The Ruined Town stands as a skeleton of what once was, its walls cracked and leaning, roofs caved in, and windows nothing more than gaping holes.\n" +
                "\t\tEvery corner, every broken doorway, suggests a story cut short—homes abandoned, lives forgotten, yet the stillness keeps it all locked away.\n" +
                "\t\tIt is a place where time lingers, unmoving, waiting for something to stir it again.");

        text.shortbreak();
        text.blueText("\t\tSILENT CAVERNS");
        text.printNarration("Beneath the shattered town lies a cave swallowed in darkness, where silence is absolute and every sound is hunted.\n" +
                "\t\tThe monsters here are born from broken echoes, feeding on any stray note that dares to exist.\n" +
                "\t\tEvery melody played is a strike against the creeping stillness, a prelude to the final confrontation with the one who seeks to erase all sound.");

        text.shortbreak();
        text.blueText("\t\tABYSS OF DISSONANCE");
        text.printNarration("At the edge of the world lies the Abyss, a realm where silence is no longer absence but a living hunger.\n" +
                "\t\tHere dwells the sovereign who loathes sound, a being that devoured every whisper, every song.\n" +
                "\t\tThe air is heavy, crushing, as if even breath is forbidden. Within this abyss, the hero must unleash the full force of their instruments.");
    }

    /**
     * Prints the plot history, main event, progression, and climax.
     */
    public void displayPlotSummary() {
        text.printSystemMessage("II. PLOT SUMMARY");
        text.shortbreak();

        text.blueText("\t\tHISTORY & THE ETERNAL SILENCE");
        text.printNarration("Once, the towns echoed with song, pleasing a great entity. Upon the entity's request for rest, the people silenced their music.\n" +
                "\t\tUnbeknownst to them, their music, not the entity, preserved the peace. Without it, Shadow Beasts emerged, creatures that fed on stillness.\n" +
                "\t\tThe Beasts attacked, leading to the Eternal Silence —centuries of quiet that drained the soul from the living.");

        text.shortbreak();
        text.blueText("\t\tMAIN EVENT & PROGRESSION");
        text.printNarration("The silence was shattered by a single bell toll, shaking the world and awakening the Shadows in frenzy.\n" +
                "\t\tChildren, born in silence, use hidden instruments to fight through the Ruined Town of Echoes and the suffocating Silent Caverns.\n" +
                "\t\tTheir journey leads to the Abyss of Dissonance and the Maestro Syozan, the source of the silence.");

        text.shortbreak();
        text.blueText("\t\tCLIMAX");
        text.printNarration("In the heart of the Abyss, the hero plays not just to fight, but to answer an intense yearning to feel what it means to listen.\n" +
                "\t\tEach note is defiance, grief, hope, and love entwined—a final symphony against the entity that devoured all sound.");
    }

    /**
     * Prints the biographies and endings for all three main characters.
     */
    public void displayCharacterBiographies() {
        text.printSystemMessage("III. CHARACTER BIOGRAPHIES & ENDINGS");
        text.shortbreak();

        displaySonaraLore();
        displayAureliusLore();
        displayLyronLore();
    }

    public void displaySonaraLore(){
        text.blueText("\t\tSONARA (The Fury) - Banjo “The Scourge of Echoes”");
        text.printNarration("Background: Lived in a comforting delusion that her family was still alive, unaware the surrounding silence was hiding the truth of their violent loss.\n" +
                "\t\tPersonality: Defined by a newfound, unyielding intensity and deep, barely contained grief. Her music is the raw, twangy sound of rebellion, demanding retribution.\n" +
                "\t\tIntroduction: The bell shattered her delusion, forcing her to grab the hidden Banjo.\n" +
                "\t\tHer final vow: I will use the sound to destroy the silence... until my family is finally, truly avenged.");
        text.shortbreak();
    }

    public void displayAureliusLore(){
        text.blueText("\t\tAURELIUS (The Shield) - Flute “The Whispering Bulwark”");
        text.printNarration("Background: Grew up in a cruel, privileged family, secretly harboring guilt and using a hidden Flute to process the empathy he felt for the suffering world.\n" +
                "\t\tPersonality: Fundamentally empathetic and driven by a strong moral duty. Prefers to observe and protect. His music is light, airy, and protective.\n" +
                "\t\tIntroduction: The bell galvanized his shame into purpose, prompting him to flee and use his Flute's sound to create a protective barrier.\n" +
                "\t\tHis resolve: My silence was shame. My sound will be shelter.");
        text.shortbreak();
    }

    public void displayLyronLore(){
        text.blueText("\t\tLYRON (The Resolve) - Harp “The Shattered Harmony”");
        text.printNarration("Background: Deemed the weakest in his family, he felt crippling shame, but held onto a sliver of hope represented by his mother's untouched Harp.\n" +
                "\t\tPersonality: Riddled with guilt and paralyzed by fear, he pushes forward as a reluctant avenger. His commitment provides the fragile resolve he needs.\n" +
                "\t\tIntroduction: After witnessing his family's murder, he instinctively struck the Harp in desperate fear, banishing the monster and gaining a tool for vengeance.");
        text.shortbreak();
    }

    public void displayCharacterSonara(){
        System.out.println();
        text.blueText("\t\tSONARA (The Fury) - Banjo “The Scourge of Echoes”");
        text.printNarration("Introduction: Sonara lived in a quiet cottage on the edge of the village, clinging to the fragile illusion that her family was still beside her.\n" +
                        "\t\tShe survived by holding onto that false peace, unaware of how deeply the truth had buried itself within her.\n" +
                        "\t\tWhen the Bell chimed and monsters drifted through the village, her cottage was among the first they forced their way into.\n" +
                        "\t\tPanicked and alone, Sonara grabbed the old keepsake her father had hidden away, unaware that this moment would reveal what it truly was.\n");
        task.delay(2);
    }

    public void displayCharacterAurelius() {
        System.out.println();
        text.blueText("\t\tAURELIUS (The Shield) - Flute “The Whispering Bulwark”");
        text.printNarration("Introduction: Aurelius lived in a crumbling mansion stained by the quiet wrongs of his family.\n" +
                        "\t\tThough raised in their harsh legacy, he carried a gentle heart and held onto the old heirloom his father left him—a reminder that their lineage had once been kind.\n" +
                        "\t\tWhen the Bell chimed and monsters wandered through the village, breaking into homes without pattern, their mansion was soon breached.\n" +
                        "\t\tIn the chaos, Aurelius fled with the heirloom in hand, unaware that he was moments away from learning its true purpose.\n");
        task.delay(2);
    }

    public void displayCharacterLyron() {
        System.out.println();
        text.blueText("\t\tLYRON (The Resolve) - Harp “The Shattered Harmony”");
        text.printNarration("Introduction: Lyron was considered the weakest in his family, leading to him being isolated at home while his stronger relatives scavenged,\n" +
                "\t\twhich instilled in him deep shame and a crippling fear of the dangerous, silent world. Driven by a desire to contribute, he finally ventured out,\n" +
                "\t\tsuccessfully gathered food, and was greeted by his family's relief and a brief celebration, though his exposure had unknowingly marked him.\n" +
                "\t\tAll the while, a small, beautiful Harp sat in their house—a silent, cherished relic meticulously maintained by his mother but never played.");
        task.delay(2);
    }

    public void displayEndingSequence(Character player) {
        System.out.println();
        if (player.name.equals("Sonara")) {
            text.blueText("\t\tSONARA");
            text.printNarration("After the final symphony, Sonara's rage softens. Vengeance gives way to purpose.\n" +
                    "\t\tShe becomes a traveling musician and an advocate for healing, using rhythm to mend the \n" +
                    "\t\tscars left by silence.");
        } else if (player.name.equals("Aurelius")) {
            text.blueText("\t\tAURELIUS");
            text.printNarration("Aurelius rises as the moral compass of the new era. He uses his flute not only to protect,\n" +
                    "\t\tbut to guide communities toward justice and shared harmony, rebuilding trust through gentle melodies.");
        } else if (player.name.equals("Lyron")) {
            text.blueText("\t\tLYRON");
            text.printNarration("Lyron lays down vengeance and accepts his past. He becomes a Custodian of Memory, \n" +
                    "\t\tplaying soft, comforting harp pieces that help others heal from guilt and fear.");
        }
    }

    /**
     * Displays the story transition between different game arcs based on the next map.
     * @param nextMap The integer representing the upcoming map/arc.
     */
    public void displayArcTransition(int nextMap) {
        text.lineBreak();
        text.printGameAnnouncement("\t\t\t\t\t\t\t\t\t\t\t--- A NEW PATH REVEALS ITSELF ---");
        task.delay(2);
        text.lineBreak();


        switch (nextMap) {
            case 2: // Transition to Silent Caverns
                text.printNarration("\tHaving cleared the Ruined Town of its most menacing shadows, you discover a hidden stairway beneath the rubble.");
                task.delay(2);
                text.printNarration("\tIt descends into the earth, leading to the Silent Caverns—a place where the silence is not just an absence of sound, but a predator.");
                break;
            case 3: // Transition to Abyss of Dissonance
                text.printNarration("\tThe oppressive silence of the caverns finally gives way to a gaping chasm—the Abyss of Dissonance.");
                task.delay(2);
                text.printNarration("\tHere, at the edge of the world, sound is devoured instantly. Each note you play will be an act of pure defiance.");
                break;
        }
        task.delay(2);
    }

    /**
     * Displays the story sequence just before the final boss encounter.
     */
    public void displayPreBossTransition() {
        text.lineBreak();
        text.printGameAnnouncement("\t\t\t\t\t\t\t\t\t\t\t--- THE MAESTRO AWAITS ---");
        task.delay(2);
        text.lineBreak();
        text.printNarration("\tYou've reached the heart of the Abyss, a throne room of absolute nothingness.");
        task.delay(2);
        text.printNarration("\tBefore you sits Maestro Syozan, the architect of the Eternal Silence, cloaked in shadow and dissonance.");
        task.delay(2);
        text.printNarration("\tThis is the final performance. Your music must be the dawn that breaks this endless night.");
        task.delay(2);
    }

    /**
     * Displays the story sequence after the final boss is defeated.
     */
    public void displayPostBossTransition() {
        text.lineBreak();
        text.printGameAnnouncement("\t\t\t\t\t\t\t\t\t\t\t--- THE SILENCE IS BROKEN ---");
        task.delay(2);
        text.lineBreak();
        text.printNarration("\tWith the final note, Maestro Syozan's shadowy form dissolves into nothingness, his grip on the world shattered.");
        task.delay(2);
        text.printNarration("\tA gentle hum fills the void, growing into a chorus of sound. The whisper of wind, the rustle of leaves, the distant echo of a forgotten song...");
        task.delay(2);
        text.printNarration("\tSound has returned. The world can finally breathe again.");
        task.delay(2);
    }

    public void displayEnd(){
        text.printGameAnnouncement("\t\t\t\t\t\t\t\t\t\t\t--- END OF SILENTIUM ---)");
        text.lineBreak();
        new AsciiArt().endASCII();
    }

}
