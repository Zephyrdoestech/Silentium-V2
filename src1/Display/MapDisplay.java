package Display;

import Player.Character;
import Map.*;

public class MapDisplay implements MapPrint{
    TextDisplay text = new TextDisplay();

    public void mapIntroduction(int map){
        if(map == 1){
            text.lineBreak();
            text.printSystemMessage("ENTERING: RUINED TOWN OF ECHOES");
            System.out.println();
            text.printNarration("The Ruined Town stands as a skeleton of what once was, its walls cracked and leaning, roofs caved in, and windows nothing more than gaping holes.\n" +
                    "\t\tDust settles thick on streets where no footsteps fall, and weeds grow wild between the stones, claiming what the people left behind.\n" +
                    "\t\tThe air feels heavy, as if the place itself remembers the weight of life that once filled it.\n" +
                    "\t\tEvery corner, every broken doorway, suggests a story cut short—homes abandoned, lives forgotten, yet the stillness keeps it all locked away.\n" +
                    "\t\tIt is a place where time lingers, unmoving, waiting for something to stir it again..");
            text.lineBreak();
        }

        else if(map == 2){
            text.lineBreak();
            text.printSystemMessage("ENTERING: SILENT CAVERNS");
            text.printNarration("Beneath the shattered town lies a cave swallowed in darkness, where silence is absolute and every sound is hunted.\n" +
                    "\t\tThe monsters here are born from broken echoes, feeding on any stray note that dares to exist.\n" +
                    "\t\tIn this suffocating void, the hero presses forward, wielding their instruments not as fragile relics but as powerful weapons that defy the silence itself.\n" +
                    "\t\tEvery melody played is a strike against the creeping stillness, a prelude to the final confrontation with the one who seeks to erase all sound.");
            text.lineBreak();
        }

        else if(map == 3){
            text.lineBreak();
            text.printSystemMessage("ENTERING: ABYSS OF DISSONANCE");
            text.printNarration("At the edge of the world lies the Abyss, a realm where silence is no longer absence but a living hunger.\n" +
                    "\t\tHere dwells the sovereign who loathes sound, a being that devoured every whisper, every song, and twisted them into a void of unending quiet.\n" +
                    "\t\tThe air is heavy, crushing, as if even breath is forbidden. Within this abyss, the hero makes their final stand.");
            text.lineBreak();
        }

    }

    public void displayMapConfused(Character player){
        if(player.name.equals("Sonara")){
            text.printDialogue(player, "I know exploring is my expertise but I gotta have some directions....");
        }else if(player.name.equals("Aurelius")){
            text.printDialogue(player, "So, Where am I supposed to go again?");
        }else if(player.name.equals("Lyron")){
            text.printDialogue(player, "Where am I supposed to go?");
        }
    }

    public void displayMapBlocked(Character player){
        if(player.name.equals("Sonara")){
            text.printDialogue(player, "Even I can't climb that wall...");
        }else if(player.name.equals("Aurelius")){
            text.printDialogue(player, "Well... Something is in the way it seems;");
        }else if(player.name.equals("Lyron")){
            text.printDialogue(player, "I wish I could break this thing that is blocking my way...");
        }
    }

    public void displayEmptySpot(Character player){
        if(player.name.equals("Sonara")){
            text.printDialogue(player, "No one is here");
        }else if(player.name.equals("Aurelius")){
            text.printDialogue(player, "Looks like I'm alone");
        }else if(player.name.equals("Lyron")){
            text.printDialogue(player, "No one's here...");
        }
    }

    public void displayEnemyEncounter(Character player){
        if(player.name.equals("Sonara")){
            text.printDialogue(player, "Well, This sucks...");
        }else if(player.name.equals("Aurelius")){
            text.printDialogue(player, "Forgive me for I shall put you to rest.");
        }else  if(player.name.equals("Lyron")){
            text.printDialogue(player, "Another kill to my name!");
        }
    }

    public void displayExit(Character player){
        if(player.name.equals("Sonara")){
            text.printDialogue(player, "Where does this lead to?");
        }else if(player.name.equals("Aurelius")){
            text.printDialogue(player, "I shall traverse through the unknown.");
        }else if(player.name.equals("Lyron")){
            text.printDialogue(player, "I'll get off now");
        }
    }

    public void displayEntrance(Character player){
        if(player.name.equals("Sonara")){
            text.printDialogue(player, "Hey, this is where I got in here");
        }else if(player.name.equals("Aurelius")){
            text.printDialogue(player, "This is the entrance from where I got in.");
        }else if(player.name.equals("Lyron")){
            text.printDialogue(player, "The scent of the beast lead me here.");
        }
    }

    public void displayMap(Character player, Map map, Map mapDisp){
        int i, j;

        if(player.getMap() == 1){
            mapDisp = new TownOfEchoes();
        }else if(player.getMap() == 2){
            mapDisp = new SilentCaverns();
        }else{
            mapDisp = new AbyssOfDissonance();
        }


        for(i=0;i<mapDisp.getMapRow();i++){
            for(j=0;j<mapDisp.getMapCol();j++){
                mapDisp.setMapPos(i,j,0);
                if(map.getIndex(i,j) == 2){
                    mapDisp.setMapPos(i,j, 1);
                }
            }
            System.out.println();
        }

        for(i=0;i<mapDisp.getMapRow();i++){
            for(j=0;j<mapDisp.getMapCol();j++){
                //if(po)
                text.printMap("" + mapDisp.getIndex(i,j));
            }
            System.out.println();
        }
    }
}
