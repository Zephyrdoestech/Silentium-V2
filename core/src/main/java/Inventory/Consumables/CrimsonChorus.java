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
        // Temporary safe effect until enemy debuff state is implemented.
        player.setDamageBuff(player.getDamageBuff() + 0.10);
        System.out.println("[CrimsonChorus] Effect applied: " + player.getName()
            + " gains +10% damage temporarily.");
    }

    @Override
    public com.badlogic.gdx.graphics.g2d.TextureRegion getInventoryIcon(io.github.Zephyrdoestech.Assets assets) {
        return assets.crimsonChorusInvTex;
    }

    @Override
    public com.badlogic.gdx.graphics.g2d.TextureRegion getBattleIcon(io.github.Zephyrdoestech.Assets assets) {
        return assets.crimsonChorusBattleTex;
    }
}
