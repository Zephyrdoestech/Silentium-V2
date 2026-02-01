package Main;

import Main.Task;
import Map.Arc;
import Player.Character;
import Player.*;
import Map.*;
import Display.*;

import java.util.InputMismatchException;
import java.util.Scanner;

public class GameMenu {
    TextDisplay text = new  TextDisplay();
    Character player;
    CombatDisplay combDisplay = new CombatDisplay();
    MapCharacter mapChar = new MapCharacter();
    Dialogue dialogue = new Dialogue();
    DisplayStory displayStory = new DisplayStory();
    HowToPlay howToPlay = new HowToPlay();
    Task task = new Task();
    AsciiArt art = new AsciiArt();

    Arc arcManager = null;

    public void start(){
        art.displayTitle();
        task.load(2);
        boolean isRunning = true;

        Scanner sc = new Scanner(System.in);
        boolean isEnabled;
        int option = 0;

        while(isRunning) {
            displayMenu();
            isEnabled = true;
            while(isEnabled){
                try{
                    option  = sc.nextInt();
                    if(option <= 0 || option > 5){
                        System.out.println();
                        text.printSystemError(" --- Invalid Input ---");
                        System.out.println();

                        text.printSystemInput("Select: ");
                    }
                    else{
                        isEnabled = false;
                        task.load(1);
                        text.shortbreak();
                    }
                }catch(InputMismatchException e){
                    System.out.println();
                    text.printSystemError(" --- Invalid Input ---");
                    System.out.println();

                    text.printSystemInput("Select: ");
                    sc.next();
                }
            }
            switch (option) {
                case 1: // PLAY
                    player = CharacterSelect();
                    dialogue.opening(player);
                    combDisplay.characterDisplay(player);

                    arcManager = new Arc(mapChar,player); // Arc Instantiation - Sangasina

                    //ARC SEQUENCE
                    boolean completeMap;
                    completeMap = arcManager.startArc1();
                    if(!completeMap){
                        art.gameOverAscii();
                        task.load(3);
                        text.lineBreak();
                        break;
                    }
                    displayStory.displayArcTransition(2);
                    completeMap = arcManager.startArc2();
                    if(!completeMap){
                        art.gameOverAscii();
                        task.load(3);
                        text.lineBreak();
                        break;
                    }

                    displayStory.displayArcTransition(3);
                    completeMap = arcManager.startArc3();
                    if(!completeMap){
                        art.gameOverAscii();
                        task.load(3);
                        text.lineBreak();
                        break;
                    }

                    break;
                case 2: //StoryLine
                    displayStory.displayFullLore();
                    break;
                case 3: //Credits
                    credits();
                    break;
                case 4: //How to Play
                    howToPlay.displayHowToPlay();
                    break;
                case 5: // Exit
                    text.redTextV2("\t\t\t\t\tExiting Silentium");
                    task.load(3);
                    isRunning = false;
                    text.shortbreak();
                    break;
            }
        }
    }


    public void displayMenu(){
        text.shortbreak();
        text.printSystemMessage("--- Main Menu ---");
        System.out.println();
        text.printSystemMessage("[ 1 ] \t--->  \tStart Game");
        text.printSystemMessage("[ 2 ] \t--->  \tStory");
        text.printSystemMessage("[ 3 ] \t--->  \tCredits");
        text.printSystemMessage("[ 4 ] \t--->  \tHow To Play");
        text.printSystemMessage("[ 5 ] \t--->  \tExit");
        System.out.println();
        text.printSystemInput("Select :   ");
    }

    public void credits(){

        text.lineBreak();
        text.printSystemAnnouncement("\t\t\t\t\t\t\t\t\t\t=====   CREDITS   =====");
        text.lineBreak();

        text.yellowText("\t\tSilentium is a project developed by the \"Team Balanghoy\" Group that is composed of 2nd year BSIT students from\n" +
                "\t\tCIT-U (Cebu Institute of Technology University).\n" +
                "\t\tThis project serves as the final output for CSIT227 – Object-Oriented Programming 1,\n" +
                "\t\tunder the guidance of Sir Kenn Migan Vincent Gumonan.");
        task.load(2);

        text.shortbreak();

        text.blueText("\t\tMEMBERS: ");
        text.yellowText("\t\tProject Manager \t Ricksmer Cabatingan\n" +
                "\t\tProject Manager \t Andrew Sangasina\n" +
                "\t\t         Member \t Yohann Abarquez\n" +
                "\t\t         Member \t Ryza Janell Mutya\n" +
                "\t\t         Member \t Precious Ann Tolentino\n");

        text.blueText("\t\tINSTRUCTOR / ADVISER:");
        text.yellowText("\t\tSir Kenn Migan Vincent Gumonan\n");
        text.blueText("\t\tSPECIAL THANKS TO:");
        text.yellowText("\t\tCIT-U College of Computer Studies\n" +
                "\t\tCSIT227 Classmates\n" +
                "\t\tFriends & Family for continuous support\n");
        text.blueText("\t\tTOOLS & TECHNOLOGIES:");
        text.yellowText("\t\tJava (OOP)\n" +
                "\t\tIntelliJ IDEA 2025.2.2\n" +
                "\t\tGitHub for version control\n");
        text.blueText("\t\tVERSION:");
        text.yellowText("\t\tSilentium v1.0 (Final Build)\n");
        text.blueText("\t\tCOPYRIGHT:");
        text.yellowText("\t\t© 2025 Team Balanghoy. All Rights Reserved.\n");

        task.load(5);
        /*String credits = """
                                           =====   CREDITS   ===== 
                                                   
                       Silentium is a project developed by the "Team Balanghoy" Group that
                    is composed of 2nd year BSIT students from CIT-U (Cebu Institute of
                    Technology University). Silentium serves as a final submission for the 
                    final project assigned by Kenn Migan Vincent Gumonan under the subject
                     code CSIT227 titled "Object Oriented Programming 1"...
                                    
                                             > Project Managers <
                                             Cabatingan, Ricksmer
                                              Sangasina, Andrew
                                               
                                              > Co-Developers <
                                               Abarquez, Yohan
                                              Mutya, Ryza Janell
                                            Tolentino, Precious Ann   
                                                                           
                """;

        text.yellowTextV2(credits); */

    }

    public Character CharacterSelect(){
        int charSelect = 1;
        boolean isEnabled;
        Character sonara = new Sonara();
        Character aurelius = new Aurelius();
        Character lyron = new Lyron();
        Character op = new Op();
        Scanner sc = new Scanner(System.in);
        CombatDisplay combDisplay = new CombatDisplay();

        do{
            combDisplay.CharacterSelect();

            isEnabled = true;
            while(isEnabled){
                try{
                    charSelect = sc.nextInt();
                    if((charSelect < 0 || charSelect > 3 ) && charSelect != -67){
                        System.out.println();
                        text.printSystemError(" --- Invalid Input ---");
                        System.out.println();

                        text.printSystemInput("Select: ");
                    }
                    else {
                        isEnabled = false;
                        task.load(1);
                        text.shortbreak();
                    }
                }
                catch(Exception e){
                    System.out.println();
                    text.printSystemMessage(" --- Invalid Input ---");
                    System.out.println();

                    text.printSystemInput("Select: ");
                    sc.next();
                }
            }


            switch (charSelect){
                case 1:
                    combDisplay.displayStats(sonara);
                    displayStory.displayCharacterSonara();
                    return sonara;
                case 2:
                    combDisplay.displayStats(aurelius);
                    displayStory.displayCharacterAurelius();
                    return aurelius;
                case 3:
                    combDisplay.displayStats(lyron);
                    displayStory.displayCharacterLyron();
                    return lyron;
                case 0:
                    combDisplay.displayStats(sonara);
                    combDisplay.displayStats(aurelius);
                    combDisplay.displayStats(lyron);
                    text.shortbreak();
                    continue;
                case -67:
                    combDisplay.displayStats(op);
                    return op;
                default:
                    break;
            }


        }while((charSelect <= 0 || charSelect > 3) && charSelect != -67);

        return null;
    }
}
