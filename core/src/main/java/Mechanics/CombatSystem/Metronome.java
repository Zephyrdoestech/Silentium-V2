//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package Mechanics.CombatSystem;

public class Metronome {
    private int beat = 1;

    public int getBeat() {
        return this.beat;
    }

    public void reset() {
        this.beat = 1;
    }

    public int updateBeat(int initialNoteDamage) {
        if (initialNoteDamage % this.beat == 0) {
            initialNoteDamage *= this.beat;
            if (this.beat < 4) {
                ++this.beat;
            }
        } else {
            this.beat = 1;
        }

        return initialNoteDamage;
    }
}
