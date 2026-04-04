package Inventory.Consumables;

import Entities.Character;
import Inventory.Item;

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
        // TODO: hook into combat system — set enemy vulnerability flag for 2-3 turns
        System.out.println("[CrimsonChorus] Effect applied: enemy takes 5-10% more damage for 2-3 turns.");
    }
    @Override
    public com.badlogic.gdx.graphics.g2d.TextureRegion getInventoryIcon() {
        return player.getAssets().crimsonChorusInvTex;
    }

    @Override
    public com.badlogic.gdx.graphics.g2d.TextureRegion getBattleIcon() {
        return player.getAssets().crimsonChorusBattleTex;
    }
}
