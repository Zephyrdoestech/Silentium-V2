package Mechanics.CombatSystem;


public class Metronome {
    private int beat;
    public Metronome() {
        beat = 1;
    }

    public int getBeat() {
        return beat;
    }

    public void reset() {
        beat = 1;
    }

    public int updateBeat(int initialNoteDamage) {
        if (initialNoteDamage % beat == 0) {
            initialNoteDamage *= beat;
            if (beat < 4) {
                beat++;
            }
            // If beat is already 4, it stays at 4
        } else {
            beat = 1;
        }
        return initialNoteDamage;
    }
}
