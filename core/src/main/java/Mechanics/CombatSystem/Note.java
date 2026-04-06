package Mechanics.CombatSystem;

import java.util.Random;

public class Note{
    public final Random RND = new Random();

    public final char[] noteBuffer  = new char[3];
    public       int    noteCount   = 0;
    public final int[]  noteDamages = new int[3];


    int A;
    int B;
    int C;
    int D;
    int E;
    int F;
    int G;


    private boolean lockNoteDamage = false;
    public void setLockNoteDamage(boolean lock) { lockNoteDamage = lock; }
    public void lockNoteDamage() { setLockNoteDamage(true); }
    public void unlockNoteDamage() { setLockNoteDamage(false); }

    public void rollNotes(){
        if (lockNoteDamage) {
            unlockNoteDamage();
            return;
        }

        A = 1  + RND.nextInt(10);
        B = 5 + RND.nextInt(9);
        C = 9 + RND.nextInt(8);
        D = 12 + RND.nextInt(7);
        E = 16 + RND.nextInt(6);
        F = 19 + RND.nextInt(5);
        G = 21 + RND.nextInt(4);
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
                return 0;
        }
    }

    public boolean isValidNote(char inputNote, int level){
        char note = java.lang.Character.toUpperCase(inputNote);
        if(level == 1){
            switch(note){
                case 'A', 'B', 'C':
                    return true;
                default: return false;
            }
        }else if(level == 2){
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

    /**
     * Returns the input prompt shown to the player during the ATTACK state.
     * Reflects which notes are available at the player's current level.
     */
    public String getInputGuide(int level) {
        switch (level) {
            case 1:  return "Input A-C notes (3 total)";
            case 2:  return "Input A-E notes (3 total)  |  BACKSPACE to undo";
            default: return "Input A-G notes (3 total)  |  BACKSPACE to undo";
        }
    }
}
