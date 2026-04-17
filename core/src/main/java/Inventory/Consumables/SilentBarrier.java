package Inventory.Consumables;

import Entities.CharacterHero;
import com.badlogic.gdx.graphics.Texture;
import io.github.Zephyrdoestech.Assets;

/**
 * Silent Barrier
 * Grants 1 turn of full immunity to damage and debuffs.
 */
public class SilentBarrier extends Item {
    public SilentBarrier(Assets assets){
        super("Silent Barrier",
            "Grants 1 turn of full immunity to damage and debuffs.",
            assets.silentBarrierBattleTex,
            assets.silentBarrierSlotItem);
    }

    @Override
    public void applyEffect(CharacterHero player) {
        // Temporary safe effect until enemy debuff state is implemented.
        // For now, it will heal the player for 10 health.
        player.heal(10);
    }
}
