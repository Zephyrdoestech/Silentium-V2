package Inventory.Consumables;

import Entities.Character;
import Inventory.Item;
import com.badlogic.gdx.graphics.Texture;
import io.github.Zephyrdoestech.Assets;

/**
 * Time Orb
 * Adds 15 seconds when the next battle starts.
 */
public class TimeOrb implements Item {

    /** Seconds added to the next battle's turn timer. */
    public static final int BONUS_SECONDS = 15;

    @Override
    public String getName() {
        return "Time Orb";
    }

    @Override
    public String getDescription() {
        return "Adds " + BONUS_SECONDS + " seconds when the next battle starts.";
    }

    @Override
    public void applyEffect(Character player) {
        // Temporary safe fallback effect.
        player.heal(25);
        System.out.println("[TimeOrb] Effect applied: " + player.getName()
            + " recovers 25 HP.");
    }

    @Override
    public Texture getInventoryIcon(Assets assets) {
        return assets.timeOrbSlotItem;
    }

    @Override
    public Texture getBattleIcon(Assets assets) {
        return assets.timeOrbBattleTex;
    }
}
