package Inventory;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import Display.*;
import Player.Character;

public class Inventory {
    private ArrayList<Item> items = new ArrayList<>();
    private static final int maxSize = 10;
    private Random rd = new Random();
    private Scanner sc = new Scanner(System.in);
    TextDisplay text = new TextDisplay();

    private int battleCount = 0; // for loot progression
    private int countItems = 0;

    private int getMaxSize(){
        return  maxSize;
    }

    public boolean isFull(){
        return items.size() == maxSize;
    }

    public void addItem(Item item) {
        if(!isFull()) {
            items.add(item);
            text.printSystemAnnouncement("Item obtained: " + item.getName() + " - " + item.getDescription() + "\n");
            countItems++;
        }else{
            text.printSystemAnnouncement("Inventory Full!");
        }
    }

    public void tryDrop() {
        battleCount++;
        double dropChance;

        if (battleCount <= 3) dropChance = 0.7;
        else if (battleCount <= 5) dropChance = 0.85;
        else dropChance = 0.99;

        double roll = rd.nextDouble();

        if (roll < dropChance)
            randomDrop();
        else text.printSystemMessage("No items dropped this time...\n");
    }

    private void randomDrop() {
        int roll = rd.nextInt(5);
        Item dropped = null;

        switch (roll) {
            case 0 -> dropped = new Item("Crimson Chorus", "Enemies take 5–10% more damage for 2–3 turns.");
            case 1 -> dropped = new Item("Silent Barrier", "1 turn of full immunity.");
            case 2 -> dropped = new Item("Resolved Dissonance", "Next B Dim doesn’t cost HP.");
            case 3 -> dropped = new Item("Minor’s Grace", "+1 free Minor chord use.");
            case 4 -> dropped = new Item("Major’s Blessing", "+1 free Major chord use.");
        }
        addItem(dropped);
    }

    public void guaranteedDrop() {
        text.pause();
        randomDrop();
        showInventory();
    }

    public void doubleGuaranteedDrop() {
        text.pause();
        randomDrop();
        randomDrop();
        showInventory();
    }

    public void resetInventory(){
        items.clear();
        countItems = 0;
        battleCount = 0;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void showInventory() {
        text.printSystemMessage("--- Inventory ---\n");

        if (items.isEmpty()) {
            text.printSystemAnnouncement("Your inventory is empty.");
            return;
        }

        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            text.printSystemAnnouncement("[ " + (i + 1) + " ] " + item.getName() + " - " + item.getDescription());
        }
    }

    public void useItem(Character player) {
        if (items.isEmpty()) {
            text.printSystemAnnouncement("Your inventory is empty. Nothing to use!\n");
            return;
        }

        while (true) {
            text.printSystemMessage("--- INVENTORY ---\n");
            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                text.printSystemAnnouncement("[ " + (i + 1) + " ] " + item.getName() + " - " + item.getDescription());
            }
            text.printSystemMessage("[ 0 ] Go Back\n");

            text.printSystemInput("Choose an item to use: ");
            int choice;

            try {
                choice = sc.nextInt();
            } catch (Exception e) {
                sc.nextLine(); // clear input
                text.printSystemError("Invalid input. Please enter a number.");
                continue;
            }

            if (choice == 0) {
                text.printSystemMessage("You closed your inventory.");
                text.shortbreak();
                break;
            }

            if (choice < 1 || choice > items.size()) {
                text.printSystemError("Invalid choice. Try again.");
                continue;
            }

            Item selectedItem = items.get(choice - 1);

            text.printSystemMessage("You used " + selectedItem.getName() + "!");
            selectedItem.applyEffect(player);
            items.remove(choice - 1);
            text.printSystemMessage(selectedItem.getName() + " has been consumed.\n");

            break;
        }
    }
}
