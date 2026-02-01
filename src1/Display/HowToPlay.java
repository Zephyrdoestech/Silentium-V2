package Display;

import java.util.Scanner;
import Main.Task;
import Display.*;

public class HowToPlay {
    private final TextDisplay text = new TextDisplay();
    private final Scanner sc = new Scanner(System.in);
    Task task = new Task();
    CombatDisplay combDisplay = new CombatDisplay();

    public void displayHowToPlay() {
        text.lineBreak();
        text.printGameAnnouncement("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t--- HOW TO PLAY ---");
        task.load(1);
        text.lineBreak();

        introduction();      text.pause();
        combatSystem();      text.pause();
        noteSystem();        text.pause();
        metronomeSystem();   text.pause();
        chordSystem();       text.pause();
        itemSystem();        text.pause();
        text.lineBreak();
        text.printSystemAnnouncement("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t--- END ---");
        text.lineBreak();
        task.load(3);
    }

    private void introduction() {
        text.yellowText("\tWelcome to Silentium — a tactical rhythm RPG where music becomes your weapon against silence itself.");
        text.yellowText("\tEach battle tests your timing, strategy, and mastery of notes, chords, and rhythm. Victory depends on harmony — not haste.");
    }

    private void combatSystem() {
        text.shortbreak();
        text.printSystemMessage(" > Combat System <");
        System.out.println();
        text.yellowText("\tDuring combat, you will enter musical notes (A–G) to attack your enemies.");
        text.yellowText("\tEach note has its own damage range, and damage changes per turn depending on the note’s rhythm alignment.");
        /*text.yellowTextV2("""
                
                \t\tA -> 1–10 Damage
                \t\tB -> 5–13 Damage
                \t\tC -> 9–16 Damage
                \t\tD -> 12–18 Damage
                \t\tE -> 16–21 Damage
                \t\tF -> 19–23 Damage
                \t\tG -> 21–24 Damage

                """);
        */
        task.delay(1);
        System.out.println();
        combDisplay.displayValidNotes(5);
        task.delay(1);
        text.yellowText("\tCombine these notes strategically to deal maximum damage or synchronize with the Metronome.");
    }

    private void noteSystem() {
        text.shortbreak();
        text.printSystemMessage(" > Note Input System <");
        System.out.println();
        text.yellowText("\tBefore each attack phase, you will be asked to enter notes. Each note represents a musical strike.");
        text.yellowText("\tYou can mix notes like A, C, and G for custom combinations — experiment to find your strongest sequences.");
    }

    private void metronomeSystem() {
        text.shortbreak();
        text.printSystemMessage(" > Metronome System <");
        System.out.println();
        text.yellowText("\tAt the start of every battle, the metronome begins at tempo 1.");
        text.yellowText("\tIf the total sum of your selected notes is divisible by the current tempo, your attack is amplified!");
        text.yellowText("\tThe metronome then increases its tempo by one (up to 4). If not divisible, it decreases by one.");
        text.yellowText("\tTiming your notes and damage totals can multiply your attacks dramatically.");
    }

    private void chordSystem() {
        text.shortbreak();
        text.printSystemMessage(" > Chord System <");
        System.out.println();
        text.yellowText("\tChords can be played to heal, gain shields, or enhance your damage.");
        text.yellowText("\tEach chord can only be used once, so use them wisely.");
        task.delay(1);
        System.out.println();
        text.printSystemMessage("Chord \t\t\t\tNotes\t\t\t\t\tEffect");
        text.printSystemMessage("""
                
                \t\tC Major  \t--->  \tC-E-G   \t---> \tHeal 20% HP
                \t\tD Minor  \t--->  \tD-F-A   \t---> \t+20% Damage
                \t\tE Minor  \t--->  \tE-G-B   \t---> \tHeal 10% HP + 10% Damage
                \t\tF Major  \t--->  \tF-A-C   \t---> \tGain 25 Shield
                \t\tG Major  \t--->  \tG-B-D   \t---> \tHeal 15% HP + 15 Shield
                \t\tA Minor  \t--->  \tA-C-E   \t---> \tGain 35 Shield
                \t\tB Dim    \t--->  \tB-D-F   \t---> \t+30% Damage, Lose 10% HP
                """);
        task.delay(1);
        text.printNarration("Mastering chord usage can turn the tide of even the most difficult battles.");
    }

    private void itemSystem() {
        text.shortbreak();
        text.printSystemMessage(" > Item System <");
        System.out.println();
        text.yellowText("\tEnemies can drop powerful relics after combat:");
        task.delay(1);
        System.out.println();
        text.printSystemMessage("\t\tItem \t\t\t\t\t\t\tEffect");
        text.printSystemMessage("""
                
                \t\tCrimson Chorus   \t\t---> \tEnemies take +5–10% more damage for 2–3 turns.
                \t\tSilent Barrier   \t\t---> \tGrants 1 turn of full immunity.
                \t\tResolved Dissonance \t---> \tPlay B Diminished without losing HP.
                \t\tMinor’s Grace    \t\t---> \tStart with +1 Minor Chord.
                \t\tMajor’s Blessing \t\t---> \tStart with +1 Major Chord.
                """);
        task.delay(1);
        text.yellowText("\tCollecting these can offer temporary advantages or save you from defeat.");
    }

    /*
    private void pause() {
        text.printSystemInput("Press [ENTER] to continue...");
        sc.nextLine();
    } */
}
