package Combat;

import java.util.Scanner;
import java.util.Random;

import Display.AsciiArt;
import Display.Dialogue;
import Display.TextDisplay;
import Inventory.Inventory;
import Main.Task;
import Player.Character;
import Enemy.Monster;
import Display.CombatDisplay;

public class Combat {
    TextDisplay text = new TextDisplay();
    CombatDisplay combDisplay = new CombatDisplay();
    Metronome mt = new Metronome();
    Note nt = new Note();
    Random rd = new Random();
    Scanner sc = new Scanner(System.in);
    ChordSystem chordSystem = new ChordSystem();
    Dialogue dialogue = new Dialogue();
    Task task = new Task();
    AsciiArt art = new AsciiArt();
    public static final Inventory inventory = new Inventory();

    private boolean isGameOver;

    public boolean battle(Character player, Monster enemy) {
        combDisplay.battleStart();
        mt.reset();
        player.as.resetSkill();
        chordSystem.resetChords();
        int beat;
        int enemyHP = enemy.getMaxHp();
        int damage;
        boolean isWon = false;

        isGameOver = false;
        player.setHp(player.getMaxHp());
        player.setShield(player.getMaxShield());
        player.resetTemporaryEffects();
        player.setDamageBuff(1.0);

        while (!isGameOver) {
            beat = mt.getBeat();
            if(player.getMap() == 1){
                text.printSystemMessage("\t--- Ruined Town Of Echoes ---");
            } else if(player.getMap() == 2){
                text.printSystemMessage("\t--- Silent Caverns ---");
            }else if(player.getMap() == 3){
                text.printSystemMessage("\t--- Abyss of Dissonance ---");
            }
            task.delay(2);
            turnAction(player, enemy, beat);

            // Game Check - enemy defeated?
            isGameOver = isEnemyDefeated(enemy);
            if (isGameOver) {
                if(enemy.name.equalsIgnoreCase("Maestro Syozan")){
                    art.bossWinAscii();
                }
                text.printSystemMessage("--- You defeated " + enemy.name + "! ---\n");
                player.defeatedMonster();
                dialogue.victoryDialogue(player);

                if (enemy.name.equals("Abarquez the Abyss Guardian")) {
                    inventory.doubleGuaranteedDrop();
                }

                player.levelUp(player);
                isWon = true;
                break;
            }

            task.delay(2);

            // Enemy Attack
            if (player.name.equals("Sonara")) {
                damage = player.ps.skillEffect(enemy);
                isGameOver = isEnemyDefeated(enemy);
                if (isGameOver) {
                    if(enemy.name.equalsIgnoreCase("Maestro Syozan")){
                        art.gameOverAscii();
                    }
                    else{
                        text.printSystemMessage("--- You defeated " + enemy.name + "! ---\n");
                    }
                    dialogue.victoryDialogue(player);

                    if (enemy.name.equals("Abarquez the Abyss Guardian")) {
                        inventory.guaranteedDrop();
                    } else {
                        inventory.tryDrop();
                    }

                    player.levelUp(player);
                    break;
                }
            } else {
                damage = enemyAttack(enemy);
            }

            player.takeDamage(damage);
            combDisplay.playerStatsSummary(player);

            // Game Check - player defeated?
            isGameOver = isPlayerDefeated(player);
            if(isGameOver){
                return false;
            }
            player.updateTurnEffects();
            task.delay(2);
        }
        return isWon;
    }

    public void playerAttack(Character player, Monster enemy, int beat) {
        boolean isValidAttack = false;
        char note1 = ' ';
        char note2 = ' ';
        char note3 = ' ';

        while (!isValidAttack) {
            combDisplay.noteInput();

            text.printSystemInput("");
            String input = sc.nextLine().trim().toUpperCase();
            if (input.isEmpty()) {
                text.printSystemError("Input cannot be empty.");
                System.out.println();
                continue;
            }

            input = input.replaceAll("\\s+", " - ");
            String[] notes = input.split("[-\\s]+");

            if (notes.length == 3
                    && notes[0].matches("[A-G]")
                    && notes[1].matches("[A-G]")
                    && notes[2].matches("[A-G]")) {

                note1 = notes[0].charAt(0);
                note2 = notes[1].charAt(0);
                note3 = notes[2].charAt(0);

                text.yellowText("\n\t\t\t" + note1 + " - " + note2 + " - " + note3 + "\n");

                isValidAttack = checkNotes(player, note1, note2, note3);
                if (!isValidAttack) {
                    System.out.println();
                    text.printSystemError("Please re-enter your notes.");
                    System.out.println();
                }
            } else {
                System.out.println();
                text.printSystemError("Invalid input.");
                System.out.println();
            }
        }

        int damage = computeNoteDamage(player, note1, note2, note3, beat);

        if (player.getLevel() >= 3) {
            String chord = chordSystem.checkChord(note1, note2, note3);
            if (chord != null) {
                damage = chordSystem.applyChord(chord, player, damage);
            }
        }

        if(player.name.equals("Op")){ damage*=999; }

        // LYRON PASSIVE
        if (player.name.equals("Lyron")) {
            damage = player.ps.skillEffect(player, damage);
        }

        damage = (int)(damage * player.getDamageBuff());
        if(player.getDamageBuff() > 1.0){
            text.printStats("Damage Buff", String.format("%d%%", (int)((player.getDamageBuff()) * 100) - 100), "\t");
        }
        text.printStats("Total Damage Dealt", String.valueOf(damage), "\t");
        System.out.println();


        enemy.takeDamage(damage);
    }

    public boolean checkNotes(Character player, char n1, char n2, char n3) {
        if (nt.isValidNote(n1, player) && nt.isValidNote(n2, player) && nt.isValidNote(n3, player)) {
            if (n1 == n2 || n1 == n3 || n2 == n3) {
                text.printSystemError(" --- Duplicate notes detected! Please enter different notes. ---\n");
                combDisplay.displayValidNotes(player.getLevel());
            } else {
                return true;
            }
        } else {
            combDisplay.displayValidNotes(player.getLevel());
        }
        return false;
    }

    public int enemyAttack(Monster enemy) {
        int damage = 0;

        damage = enemy.attack(enemy);

        System.out.println();
        text.redText("\t\tMonster attacks Player and deals " + damage + " damage!");
        System.out.println();
        return damage;
    }

    public int computeNoteDamage(Character player, char note1, char note2, char note3, int beat) {
        int initialDamage = 0;
        initialDamage += nt.noteDamage(note1);
        initialDamage += nt.noteDamage(note2);
        initialDamage += nt.noteDamage(note3);
        text.printStats("Initial Damage", String.valueOf(initialDamage), "\t\t");

        if (player.name.equals("Sonara")) {
            initialDamage = player.as.skillEffect(player, initialDamage);
        }

        if (player.getLevel() > 1) {
            text.printStats("Metronome", String.valueOf(beat), "\t\t\t\t");
            int finalDamage = mt.updateBeat(player, initialDamage);
            text.printStats("Final Damage", String.valueOf(finalDamage), "\t\t\t");
            return finalDamage;
        }
        return initialDamage;
    }

    public boolean isPlayerDefeated(Character player) {
        if (player.getHp() <= 0) {
            player.setHp(0);
            combDisplay.battleEnd(Boolean.valueOf(false));
            return true;
        }
        return false;
    }

    public boolean isEnemyDefeated(Monster enemy) {
        if (enemy.getHp() <= 0) {
            enemy.setHp(0);
            return true;
        }
        return false;
    }

    public void turnAction(Character player, Monster enemy, int beat) {
        boolean isTurnOver;
        int action = 0;
        boolean isEnabled;

        nt.generateNotes();

        if (player.getLevel() < 3) {
            do {
                if (player.name.equals("Lyron")) { if (player.as.skillEffect(player)) nt.generateNotes(); }
                if (player.name.equals("Aurelius")) {
                    nt.setPreserveNextDamage(player.as.skillActive);
                    player.as.skillEffectAurelius(player);
                }

                nt.damagePerNote(player, beat);
                isTurnOver = false;
                task.delay(1);
                combDisplay.turnAction(player);
                isEnabled = true;

                while (isEnabled) {
                    text.printSystemInput("Select :   ");
                    String input = sc.nextLine().trim();

                    try {
                        action = Integer.parseInt(input);
                        System.out.println();
                        if (action <= 0 || action > 4) {
                            System.out.println();
                            text.printSystemError("--- Invalid Input ---");
                            System.out.println();
                        } else {
                            isEnabled = false;
                            text.shortbreak();
                        }
                    } catch (NumberFormatException e) {
                        System.out.println();
                        text.printSystemError("--- Invalid Input ---");
                        System.out.println();
                    }
                }

                switch (action) {
                    case 1:
                        playerAttack(player, enemy, beat);
                        task.delay(1);
                        combDisplay.enemyStatsSummary(enemy);
                        isTurnOver = true;
                        break;
                    case 2:
                        player.as.useSkill(player);
                        break;
                    case 3:
                        combDisplay.playerStatsSummary(player);
                        combDisplay.enemyStatsSummary(enemy);
                        break;
                    case 4:
                        combDisplay.attackGuide(player);
                        break;
                    case 5:
                        text.printSystemError("DIED");
                        player.setHp(0);
                        isTurnOver = true;
                        break;
                }

                task.load(2);
            } while (!isTurnOver);
        }
        else {
            do {
                if (player.name.equals("Lyron")) { if (player.as.skillEffect(player)) nt.generateNotes(); }
                if (player.name.equals("Aurelius")) {
                    nt.setPreserveNextDamage(player.as.skillActive);
                    player.as.skillEffectAurelius(player);
                }

                nt.damagePerNote(player, beat); // This will now correctly reflect the preserved damage
                isTurnOver = false;
                task.delay(1);
                combDisplay.turnAction(player);
                isEnabled = true;

                while (isEnabled) {
                    text.printSystemInput("Select :   ");
                    String input = sc.nextLine().trim();

                    try {
                        action = Integer.parseInt(input);
                        System.out.println();
                        if (action <= 0 || action > 6){
                            System.out.println();
                            text.printSystemError("--- Invalid Input ---");
                            System.out.println();
                        } else {
                            isEnabled = false;
                            text.shortbreak();
                        }
                    } catch (NumberFormatException e) {
                        System.out.println();
                        text.printSystemError("--- Invalid Input ---");
                        System.out.println();
                    }
                }

                switch (action) {
                    case 1:
                        playerAttack(player, enemy, beat);
                        task.delay(1);
                        combDisplay.enemyStatsSummary(enemy);
                        isTurnOver = true;
                        break;
                    case 2:
                        player.as.useSkill(player);
                        break;
                    case 3:
                        inventory.useItem(player);
                        break;
                    case 4:
                        combDisplay.chordChart(chordSystem);
                        break;
                    case 5:
                        combDisplay.playerStatsSummary(player);
                        combDisplay.enemyStatsSummary(enemy);
                        break;
                    case 6:
                        combDisplay.attackGuide(player);
                        break;
                    case 7:
                        text.printSystemError("DIED");
                        player.setHp(0);
                        isTurnOver = true;
                        break;
                }
                task.load(2);
            } while (!isTurnOver);
        }
    }
}
