package Combat;

import Player.Character;
import Display.TextDisplay;

public class ChordSystem {
    private boolean cMajor;
    private boolean dMinor;
    private boolean eMinor;
    private boolean fMajor;
    private boolean gMajor;
    private boolean aMinor;
    private boolean bDim;

    TextDisplay text = new TextDisplay();

    public ChordSystem() {
        resetChords();
    }

    //reset all chords
    public void resetChords() {
        cMajor = false;
        dMinor = false;
        eMinor = false;
        fMajor = false;
        gMajor = false;
        aMinor = false;
        bDim = false;
    }

    //chord detection
    public String checkChord(char n1, char n2, char n3) {
        String notes = "" + n1 + n2 + n3;

        if (!cMajor && (contains(notes, 'C') && contains(notes, 'E') && contains(notes, 'G'))) {
            cMajor = true;
            return "CMAJOR";
        }
        if (!dMinor && (contains(notes, 'D') && contains(notes, 'F') && contains(notes, 'A'))) {
            dMinor = true;
            return "DMINOR";
        }
        if (!eMinor && (contains(notes, 'E') && contains(notes, 'G') && contains(notes, 'B'))) {
            eMinor = true;
            return "EMINOR";
        }
        if (!fMajor && (contains(notes, 'F') && contains(notes, 'A') && contains(notes, 'C'))) {
            fMajor = true;
            return "FMAJOR";
        }
        if (!gMajor && (contains(notes, 'G') && contains(notes, 'B') && contains(notes, 'D'))) {
            gMajor = true;
            return "GMAJOR";
        }
        if (!aMinor && (contains(notes, 'A') && contains(notes, 'C') && contains(notes, 'E'))) {
            aMinor = true;
            return "AMINOR";
        }
        if (!bDim && (contains(notes, 'B') && contains(notes, 'D') && contains(notes, 'F'))) {
            bDim = true;
            return "BDIM";
        }

        return null;
    }

    private boolean contains(String notes, char c) {
        return notes.indexOf(c) != -1;
    }

    //main buff system
    public int applyChord(String chord, Character player, int damage) {
        if (chord == null) return damage;

        double bonusDamage = 1;

        //AURELIUS PASSIVE

        if(player.name.equals("Aurelius")) player.ps.skillEffect(player);
        System.out.println();
        text.printSystemAnnouncement("=============================================================");
        System.out.println();

        switch (chord) {
            case "CMAJOR":
                player.heal((int)(player.getMaxHp() * 0.2));
                System.out.print("\t\t  ");
                text.printStats("Chord Activated", "C Major! Healed 20% HP", "\t");
                break;
            case "DMINOR":
                player.setDamageBuff(player.getDamageBuff() + 0.2);
                System.out.print("\t\t  ");
                text.printStats("Chord Activated", "D Minor! +20% Damage Buff.", "\t");
                break;
            case "EMINOR":
                player.heal((int)(player.getMaxHp() * 0.1));
                player.setDamageBuff(player.getDamageBuff() + 0.1);
                System.out.print("\t\t  ");
                text.printStats("Chord Activated", "E Minor! Heal 10% HP & +10% Damage Buff.", "\t");break;
            case "FMAJOR":
                player.addShield(25);
                System.out.print("\t\t  ");
                text.printStats("Chord Activated", "F Major! +25 Shield Points.", "\t");
                break;
            case "GMAJOR":
                player.heal((int)(player.getMaxHp() * 0.15));
                player.addShield(15);
                System.out.print("\t\t  ");
                text.printStats("Chord Activated", "G Major! Heal 15% HP & +15 Shield Points.", "\t");
                break;
            case "AMINOR":
                player.addShield(35);
                System.out.print("\t\t  ");
                text.printStats("Chord Activated", "A Minor! +35 Shield Points.", "\t");
                break;
            case "BDIM":
                bonusDamage += 1.3;
                System.out.print("\t\t  ");
                text.printStats("Chord Activated", "B Dim! +30% Damage but lose 10% HP.", "\t");

                if (player.canIgnoreBDiminished()) {
                    text.printGameAnnouncement("\tResolved Dissonance prevented HP loss from B Dim!");
                    player.setIgnoreBDiminished(false); // consume the effect
                } else {
                    int hpLoss = (int)(player.getMaxHp() * 0.1);
                    player.setHp(player.getHp() - hpLoss);
                    text.printGameAnnouncement("\tYou lost " + hpLoss + " HP due to B Dim chord!");
                }
                break;
        }

        System.out.println();
        text.printGameAnnouncement("\t=============================================================");
        System.out.println();
        return (int) (damage * bonusDamage);
    }

    public boolean isChordUsed(char chord){
        switch (java.lang.Character.toUpperCase(chord)) {
            case 'C':
                return cMajor;
            case 'D':
                return dMinor;
            case 'E':
                return eMinor;
            case 'F':
                return fMajor;
            case 'G':
                return gMajor;
            case 'A':
                return aMinor;
            case 'B':
                return bDim;
            default:
                return false;
        }
    }
}
