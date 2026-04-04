package Inventory.Consumables;

import Entities.Character;
import Inventory.Item;

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
        // TODO: hook into combat system — increment freeMajorChordUses on combat state
        System.out.println("[MajorsBlessing] Effect applied: " + player.getName()
            + " starts the next battle with +1 free Major chord use.");
    }
    @Override
    public com.badlogic.gdx.graphics.g2d.TextureRegion getInventoryIcon() {
        return player.getAssets().majorsBlessingInvTex;
    }

    @Override
    public com.badlogic.gdx.graphics.g2d.TextureRegion getBattleIcon() {
        return player.getAssets().majorsBlessingBattleTex;
    }
}
