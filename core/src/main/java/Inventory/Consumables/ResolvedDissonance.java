package Inventory.Consumables;

import Entities.Character;
import Inventory.Item;

/**
 * Resolved Dissonance
 * The next time the player plays B Diminished, they don't lose HP.
 */
public class ResolvedDissonance implements Item {

    @Override
    public String getName() {
        return "Resolved Dissonance";
    }

    @Override
    public String getDescription() {
        return "The next time you play B Diminished, you don't lose HP.";
    }

    @Override
    public void applyEffect(Character player) {
        // TODO: hook into combat system — set resolvedDissonance flag on player or combat state
        System.out.println("[ResolvedDissonance] Effect applied: " + player.getName()
            + "'s next B Diminished chord will not cost HP.");
    }
}
