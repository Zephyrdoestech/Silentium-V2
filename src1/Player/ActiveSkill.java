package Player;

import java.util.InputMismatchException;
import java.util.Scanner;
import Display.TextDisplay;


public class ActiveSkill {
    TextDisplay text = new TextDisplay();
    Scanner sc = new Scanner(System.in);
    public String skillName;
    public String skillDescription;
    public boolean skillActive = false;
    public boolean isUsed = false;

    public void resetSkill(){
        skillActive = false;
        isUsed = false;
    }

    public void useSkill(Character player) {
        text.playerText("--- " + player.name + " ---");
        System.out.println();
        text.playerText("\t" + player.as.skillName);
        text.printSystemMessage(player.as.skillDescription);
        System.out.println();

        if (isUsed) {
            text.printSystemAnnouncement("You already used this skill!\n");
            text.shortbreak();
            return;
        }

        while (true) {
            text.printSystemInput("Activate Skill? [ Y / N ]:\t");
            String input = sc.next().trim().toUpperCase();

            if (input.equals("Y")) {
                player.as.skillActive = true;
                isUsed = true;
                break;
            } else if (input.equals("N")) {
                player.as.skillActive = false;
                break;
            } else {
                System.out.println();
                text.printSystemError("--- Invalid Input --- ");
                System.out.println();
            }
        }
        text.shortbreak();
    }

    //SONARA'S ACTIVE SKILL
    public int skillEffect(Character player, int damage) {
        if(!player.as.skillActive) return damage;

        text.shortbreak();
        text.printStats("Skill Activated", player.as.skillName, "\t\t");
        text.printStats("Current Damage", String.valueOf(damage), "\t\t");
        text.printStats("Unique Skill", "Add +1 to Final Damage", "\t\t\t");
        damage += 1;
        text.printStats("New Damage", String.valueOf(damage), "\t\t\t");
        text.shortbreak();
        player.as.skillActive = false;
        return damage;
    }

    //AURELIUS' ACTIVE SKILL
    public void skillEffectAurelius(Character player) {
        if(!player.as.skillActive) return;

        text.shortbreak();
        text.printStats("Skill Activated", player.as.skillName, "\t\t");
        text.printStats("Unique Skill", "Preserve Current Notes Damage for the Next Turn", "\t\t\t");
        text.shortbreak();
        player.as.skillActive = false;
        player.as.isUsed = true;
    }

    //LYRON'S ACTIVE SKILL
    public boolean skillEffect(Character player) {
        if(!player.as.skillActive) return false;

        text.shortbreak();
        text.printStats("Skill Activated", player.as.skillName, "\t\t");
        text.printStats("Unique Skill", "Reroll Note Damage", "\t\t\t");
        text.shortbreak();
        player.as.skillActive = false;
        return true;
    }
}
