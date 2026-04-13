package Inventory.Consumables;

import Entities.Character;
import Inventory.Item;
import com.badlogic.gdx.graphics.Texture;
import io.github.Zephyrdoestech.Assets;

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
        player.setShield(player.getMaxShield());
        System.out.println("[SilentBarrier] Effect applied: " + player.getName()
            + " gains full shield.");
    }

    @Override
    public Texture getInventoryIcon(Assets assets) {
        return assets.silentBarrierSlotItem;
    }

    @Override
    public Texture getBattleIcon(Assets assets) {
        return assets.silentBarrierBattleTex;
    }
}
