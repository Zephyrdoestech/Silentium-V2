package Combat;

import java.util.Random;
import Player.Character;
import Display.TextDisplay;

public class Note{
    Random rd = new Random();
    TextDisplay text = new TextDisplay();

    /*
    A : ( 1 - 10 );
    B : ( 5 - 13 );
    C : ( 9 - 16 );
    D : ( 12 - 18 );
    E : ( 16 - 21 );
    F : ( 19 - 23 );
    G : ( 21 - 24 );
    */

    int minA = 1;
    int maxA = 10;
    int minB = 5;
    int maxB = 13;
    int minC = 9;
    int maxC = 16;
    int minD = 12;
    int maxD = 18;
    int minE = 16;
    int maxE = 21;
    int minF = 19;
    int maxF = 23;
    int minG = 21;
    int maxG = 24;

    int A;
    int B;
    int C;
    int D;
    int E;
    int F;
    int G;

    int rangeA = maxA- minA + 1;
    int rangeB = maxB- minB + 1;
    int rangeC = maxC- minC + 1;
    int rangeD = maxD- minD + 1;
    int rangeE = maxE- minE + 1;
    int rangeF = maxF- minF + 1;
    int rangeG = maxG- minG + 1;

    //for Aurelius' active skill
    private boolean preserveNextDamage = false;
    public void setPreserveNextDamage(boolean preserve) {
        preserveNextDamage = preserve;
    }

    public void generateNotes(){
        if (preserveNextDamage) {
            preserveNextDamage = false;
            return;
        }

        A = rd.nextInt(rangeA) + minA;
        B = rd.nextInt(rangeB) + minB;
        C = rd.nextInt(rangeC) + minC;
        D = rd.nextInt(rangeD) + minD;
        E = rd.nextInt(rangeE) + minE;
        F = rd.nextInt(rangeF) + minF;
        G = rd.nextInt(rangeG) + minG;
    }

    public void damagePerNote(Character player, int beat){
        text.shortbreak();
        if (player.getLevel() > 1) text.printStats("Metronome", String.valueOf(beat), "\t\t");
        System.out.println();
        text.printSystemMessage("--- Notes ---");
        System.out.println();
        text.printSystemMessage("\tA --> \t" + A);
        text.printSystemMessage("\tB --> \t" + B);
        text.printSystemMessage("\tC --> \t" + C);

        if(player.getLevel() >= 2){
            text.printSystemMessage("\tD --> \t" + D);
            text.printSystemMessage("\tE --> \t" + E);
        }

        if(player.getLevel() >= 3){
            text.printSystemMessage("\tF --> \t" + F);
            text.printSystemMessage("\tG --> \t" + G);
        }
    }

    public int noteDamage(char note){
        switch(note){
            case 'A', 'a': return A;
            case 'B', 'b': return B;
            case 'C', 'c': return C;
            case 'D', 'd': return D;
            case 'E', 'e': return E;
            case 'F', 'f': return F;
            case 'G', 'g': return G;
            default:
                text.printSystemMessage("Invalid note.");
                return 0;
        }
    }

    public boolean isValidNote(char note, Character player){
        if(player.getLevel() == 1){
            switch(java.lang.Character.toUpperCase(note)){
                case 'A':
                case 'B':
                case 'C': return true;
                default: return false;
            }
        }else if(player.getLevel() == 2){
            switch(note){
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E': return true;
                default: return false;
            }
        }else{
            switch(note) {
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                    return true;
                default:
                    return false;
            }
        }
    }
}