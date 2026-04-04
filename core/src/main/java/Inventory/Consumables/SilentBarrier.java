package Inventory.Consumables;

import Entities.Character;
import Inventory.Item;

/**
 * Silent Barrier
 * Grants 1 turn of full immunity to damage and debuffs.
 */
public class SilentBarrier implements Item {

    @Override
    public String getName() {
        return "Silent Barrier";
    }

    @Override
    public String getDescription() {
        return "Grants 1 turn of full immunity to damage and debuffs.";
    }

    @Override
    public void applyEffect(Character player) {
        // TODO: hook into combat system — set player immunity flag for 1 turn
        System.out.println("[SilentBarrier] Effect applied: " + player.getName()
            + " is immune to damage and debuffs for 1 turn.");
    }
}
