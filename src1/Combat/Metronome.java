package Combat;

import Player.Character;
import Display.TextDisplay;

public class Metronome {
    private int beat;
    TextDisplay text = new TextDisplay();

    public Metronome() {
        beat = 1;
    }

    public int getBeat() {
        return beat;
    }

    public void reset() {
        beat = 1;
    }

    public int updateBeat(Character player, int totalNoteDamage) {
        if (totalNoteDamage % beat == 0) {
            totalNoteDamage *= beat;
            beat++;

            if (beat > 4) beat = 4;

            text.printSystemMessage("Beat Sync! Metronome increased to " + beat + "!");

        } else {
            beat--;
            if (beat < 1) beat = 1;
            text.printSystemMessage("Off-beat! Metronome decreased to " + beat + ".");
        }

        return totalNoteDamage;
    }
}