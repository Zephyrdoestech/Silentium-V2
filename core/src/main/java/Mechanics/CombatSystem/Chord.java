package Mechanics.CombatSystem;


import Entities.Character;

public class Chord {
    private boolean cMajor = false;
    private boolean dMinor = false;
    private boolean eMinor = false;
    private boolean fMajor = false;
    private boolean gMajor = false;
    private boolean aMinor = false;
    private boolean bDim = false;

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


        switch (chord) {
            case "CMAJOR":
                player.heal((int)(player.getMaxHp() * 0.2));
                break;
            case "DMINOR":
                player.setDamageBuff(1.2);
                break;
            case "EMINOR":
                player.heal((int)(player.getMaxHp() * 0.1));
                player.setDamageBuff(1.1);
                break;
            case "FMAJOR":
                player.gainShield(25);
                break;
            case "GMAJOR":
                player.heal((int)(player.getMaxHp() * 0.15));
                player.gainShield(15);
                break;
            case "AMINOR":
                player.gainShield(35);
                break;
            case "BDIM":
                bonusDamage = 1.3;
                player.setHp(player.getHp() - (int)(player.getMaxHp() * 0.1));
                break;
        }

        return (int) (damage * bonusDamage);
    }

    public boolean isChordUsed(char chord){
        switch (chord) {
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


    public String chordDescription(String chord) {
        switch (chord) {
            case "CMAJOR":
                return "C Major: Heals 20% of max HP.";
            case "DMINOR":
                return "D Minor: Increases damage by 20%.";
            case "EMINOR":
                return "E Minor: Heals 10% of max HP and increases damage by 10%.";
            case "FMAJOR":
                return "F Major: Gains 25 shield.";
            case "GMAJOR":
                return "G Major: Heals 15% of max HP and gains 15 shield.";
            case "AMINOR":
                return "A Minor: Gains 35 shield.";
            case "BDIM":
                return "B Diminished: Increases damage by 30% but loses 10% of max HP.";
            default:
                return "Unknown chord.";
        }
    }

    /**
     * Returns the display name of a chord key.
     * e.g. "CMAJOR" → "C Major"
     */
    public String getChordName(String chord) {
        switch (chord) {
            case "CMAJOR": return "C Major";
            case "DMINOR": return "D Minor";
            case "EMINOR": return "E Minor";
            case "FMAJOR": return "F Major";
            case "GMAJOR": return "G Major";
            case "AMINOR": return "A Minor";
            case "BDIM":   return "B Diminished";
            default:       return "Unknown";
        }
    }

    /**
     * Returns the feedback message describing what a chord did.
     * Requires the Character to compute HP-based values.
     */
    public String getChordMessage(String chord, Character c) {
        switch (chord) {
            case "CMAJOR": {
                int h = (int)(c.getMaxHp() * 0.20f);
                return "C Major! Healed " + h + " HP.";
            }
            case "DMINOR":
                return "D Minor! +20% damage buff.";
            case "EMINOR": {
                int h = (int)(c.getMaxHp() * 0.10f);
                return "E Minor! Healed " + h + " HP + 10% buff.";
            }
            case "FMAJOR":
                return "F Major! +25 shield.";
            case "GMAJOR": {
                int h = (int)(c.getMaxHp() * 0.15f);
                return "G Major! Healed " + h + " HP + 15 shield.";
            }
            case "AMINOR":
                return "A Minor! +35 shield.";
            case "BDIM": {
                int sd = (int)(c.getMaxHp() * 0.10f);
                return "B Diminished! +30% dmg, lost " + sd + " HP.";
            }
            default:
                return "";
        }
    }
}
