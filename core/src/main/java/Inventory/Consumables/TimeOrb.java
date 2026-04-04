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
        // TODO: hook into combat system — add BONUS_SECONDS to the next battle's turn timer
        System.out.println("[TimeOrb] Effect applied: +" + BONUS_SECONDS
            + " seconds added to the next battle's turn timer.");
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
