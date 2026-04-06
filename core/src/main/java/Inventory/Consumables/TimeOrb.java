package Inventory.Consumables;

import Entities.Character;
import Inventory.Item;

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
    public com.badlogic.gdx.graphics.g2d.TextureRegion getInventoryIcon(io.github.Zephyrdoestech.Assets assets) {
        return assets.timeOrbInvTex;
    }

    @Override
    public com.badlogic.gdx.graphics.g2d.TextureRegion getBattleIcon(io.github.Zephyrdoestech.Assets assets) {
        return assets.timeOrbBattleTex;
    }
}
