package Inventory.Consumables;

import Entities.Character;
import Inventory.Item;
import com.badlogic.gdx.graphics.Texture;
import io.github.Zephyrdoestech.Assets;

/**
 * Crimson Chorus
 * For 2-3 random turns, the enemy takes 5-10% more damage.
 */
public class CrimsonChorus implements Item {

    @Override
    public String getName() {
        return "Crimson Chorus";
    }

    @Override
    public String getDescription() {
        return "For 2-3 random turns, the enemy takes 5 - 10% more damage.";
    }

    @Override
    public void applyEffect(Character player) {
        // Temporary safe effect until enemy debuff state is implemented.
        player.setDamageBuff(player.getDamageBuff() + 0.10);
        System.out.println("[CrimsonChorus] Effect applied: " + player.getName()
            + " gains +10% damage temporarily.");
    }

    @Override
    public Texture getInventoryIcon(Assets assets) {
        return assets.crimsonChorusSlotItem;
    }

    @Override
    public Texture getBattleIcon(Assets assets) {
        return assets.crimsonChorusBattleTex;
    }
}
