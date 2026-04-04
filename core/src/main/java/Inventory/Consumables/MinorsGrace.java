package Inventory.Consumables;

import Entities.Character;
import Inventory.Item;

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
        // TODO: hook into combat system — increment freeMinorChordUses on combat state
        System.out.println("[MinorsGrace] Effect applied: " + player.getName()
            + " starts the next battle with +1 free Minor chord use.");
    }
    @Override
    public com.badlogic.gdx.graphics.g2d.TextureRegion getInventoryIcon(io.github.Zephyrdoestech.Assets assets) {
        return assets.minorsGraceInvTex;
    }

    @Override
    public com.badlogic.gdx.graphics.g2d.TextureRegion getBattleIcon(io.github.Zephyrdoestech.Assets assets) {
        return assets.minorsGraceBattleTex;
    }
}
