package Inventory.Consumables;

import Entities.Character;
import Inventory.Item;
import com.badlogic.gdx.graphics.Texture;
import io.github.Zephyrdoestech.Assets;

/**
 * Minor's Grace
 * Start battle with +1 free use of any Minor chord.
 */
public class MinorsGrace implements Item {

    @Override
    public String getName() {
        return "Minor's Grace";
    }

    @Override
    public String getDescription() {
        return "Start battle with +1 free use of any Minor chord.";
    }

    @Override
    public void applyEffect(Character player) {
        // Temporary safe fallback effect.
        player.heal(15);
        System.out.println("[MinorsGrace] Effect applied: " + player.getName()
            + " recovers 15 HP.");
    }

    @Override
    public Texture getInventoryIcon(Assets assets) {
        return assets.minorsGraceSlotItem;
    }

    @Override
    public Texture getBattleIcon(Assets assets) {
        return assets.minorsGraceBattleTex;
    }
}
