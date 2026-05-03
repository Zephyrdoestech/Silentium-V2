package Inventory.Consumables;

import Entities.CharacterHero;
import com.badlogic.gdx.graphics.Texture;
import io.github.Zephyrdoestech.Assets;

/**
 * Silent Barrier
 * Grants 1 turn of full immunity to damagegit.
 */
public class SilentBarrier extends Item {
    public SilentBarrier(Assets assets){
        super("Silent Barrier",
            "Grants 1 turn of full immunity to damage.",
            assets.silentBarrierBattleTex,
            assets.silentBarrierSlotItem, 1);
    }
}
