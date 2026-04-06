package Inventory.Consumables;

import Entities.Character;
import Inventory.Item;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Major's Blessing
 * Start battle with +1 free use of any Major chord.
 */
public class MajorsBlessing implements Item {

    @Override
    public String getName() {
        return "Major's Blessing";
    }

    @Override
    public String getDescription() {
        return "Start battle with +1 free use of any Major chord.";
    }

    @Override
    public void applyEffect(Character player) {
        // Temporary safe fallback effect.
        player.heal(20);
        System.out.println("[MajorsBlessing] Effect applied: " + player.getName()
            + " recovers 20 HP.");
    }

    @Override
    public com.badlogic.gdx.graphics.g2d.TextureRegion getInventoryIcon(io.github.Zephyrdoestech.Assets assets) {
        return assets.majorsBlessingInvTex;
    }

    @Override
    public com.badlogic.gdx.graphics.g2d.TextureRegion getBattleIcon(io.github.Zephyrdoestech.Assets assets) {
        return assets.majorsBlessingBattleTex;
    }
}
