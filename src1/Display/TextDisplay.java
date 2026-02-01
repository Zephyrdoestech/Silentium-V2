package Display;

import java.util.Scanner;
import Enemy.Monster;
import Player.Character;

public class TextDisplay{
    private static final String RESET = "\u001B[0m";
    private static final String BLUE = "\u001B[34m";    // Dialogue
    private static final String YELLOW = "\u001B[33m";   // Narration
    private static final String GREEN = "\u001B[32m";    // System Messages (Success/Info)
    private static final String RED = "\u001B[31m";      // Maestro Dialogue
    private static final String WHITE = "\u001B[37m";      // Maestro Dialogue
    private static final String VIOLET  = "\u001B[35m"; // Purple/Magenta tone
    private static final String CYAN    = "\u001B[36m";
    private static final String MAGENTA = "\u001B[95m"; // Bright Magenta
    private static final String ORANGE  = "\u001B[38;5;208m"; // 256-color Orange
    private static final String PINK    = "\u001B[38;5;205m"; // 256-color Pink

    Scanner sc = new Scanner(System.in);

    // --- Text Display Tools (Standard Output Methods) ---
    public void printNarration(String narration) {
        System.out.println(YELLOW + "\t>>> " + narration + RESET);
    }

    public void printDialogue(Character player, String dialogue) {
        System.out.println(BLUE + "\t[" + player.name + "]: " + RESET + "\t" + dialogue);
    }

    public void printDialogue(String dialogue) {
        System.out.println(RED + "\t[ MAESTRO SYOZAN ]: " + VIOLET + "\t" + dialogue);
    }

    public void printSystemMessage(String message) {
        System.out.println(GREEN + "\t\t" + message.toUpperCase() + RESET);
    }

    public void printStats(String attribute, String value, String space) {
        System.out.println(GREEN + "\t\t" + attribute + ": " + space + YELLOW + value + RESET);
    }

    public void printStats(String attribute, String value, String maxValue, String space) {
        System.out.println(GREEN + "\t\t" + attribute + ": " + space + YELLOW + value + GREEN + " / " + YELLOW + maxValue + RESET);
    }

    public void printSystemError(String message) {
        System.out.println(RED + "\t\t" + message.toUpperCase() + RESET);
    }

    public void printMap(String message) {
        System.out.print(GREEN + "\t\t" + message.toUpperCase() + RESET);
    }

    public void printExitMap(String message) {
        System.out.print(YELLOW + "\t\t" + message.toUpperCase() + RESET);
    }

    public void printEnemyMap(String message) {
        System.out.print(RED + "\t\t" + message.toUpperCase() + RESET);
    }

    public void printPlayerOnMap(String message) {
        System.out.print(BLUE + "\t\t" + message.toUpperCase() + RESET);
    }

    public void printSystemAnnouncement(String message) {
        System.out.println(YELLOW + "\t\t" + message.toUpperCase() + RESET);
    }

    public void printGameAnnouncement(String message) {
        System.out.println(YELLOW + "\t" + message.toUpperCase() + RESET);
    }


    public void printSystemInput(String message) {
        System.out.print(GREEN + "\t\t" + message.toUpperCase() + WHITE);
    }


    public void greenText(String string) { System.out.println(GREEN + string + RESET); }
    public void blueText(String string) { System.out.println(BLUE + string + RESET); }
    public void magentaText(String string) { System.out.println(MAGENTA + string + RESET); }
    public void cyanText(String string) { System.out.println(CYAN + string + RESET); }
    public void orangeText(String string) { System.out.println(ORANGE + string + RESET); }
    public void pinkText(String string) { System.out.println(PINK + string + RESET); }
    public void blueTextV2(String string) { System.out.print(BLUE + string + RESET); }
    public void redText(String string) { System.out.println(RED + string + RESET); }
    public void redTextV2(String string) { System.out.print(RED + string + RESET); }
    public void yellowText(String string) { System.out.println(YELLOW + string + RESET); }
    public void yellowTextV2(String string) { System.out.print(YELLOW + string + RESET); }

    public void playerText(String string) { System.out.println(BLUE + "\t" + string + RESET); }
    public void enemyText(String string) { System.out.println(RED + "\t" + string + RESET); }
    public void lineBreak() {
        System.out.println(GREEN + "\n\t------------------------------------------------------------------------------------------------------------------------------------------------------\n" + RESET);
    }
    public void shortbreak(){
        yellowText("\n\t================================================================\n");
    }

    public void clear(){
        String art = """
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        """;
        System.out.println(art);
    }

    public void pause() {
        greenText("\n\t================================================================\n");
        blueTextV2("\t\tPress [ENTER] to continue...");
        sc.nextLine();
        greenText("\n\t================================================================\n");
    }
}

