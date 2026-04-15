package Inventory.Consumables;

import Entities.Character;
import Inventory.Item;
import com.badlogic.gdx.graphics.Texture;
import io.github.Zephyrdoestech.Assets;

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
        // Temporary safe fallback effect.
        player.heal(10);
        System.out.println("[ResolvedDissonance] Effect applied: " + player.getName()
            + " recovers 10 HP.");
    }

    @Override
    public Texture getInventoryIcon(Assets assets) {
        return assets.resolvedDissonanceSlotItem;
    }

    @Override
    public Texture getBattleIcon(Assets assets) {
        return assets.resolvedDissonanceBattleTex;
    }
}
