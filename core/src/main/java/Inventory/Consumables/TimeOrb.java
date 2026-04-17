package Inventory.Consumables;

import Entities.CharacterHero;
import com.badlogic.gdx.graphics.Texture;
import io.github.Zephyrdoestech.Assets;

/**
 * Time Orb
 * Adds 15 seconds when the next battle starts.
 */
public class TimeOrb extends Item {
    public TimeOrb(Assets assets){
        super("Time Orb",
            "Adds 15 seconds when the next battle starts.",
            assets.timeOrbBattleTex,
            assets.timeOrbSlotItem);
    }

    @Override
    public void applyEffect(CharacterHero player) {
        // Temporary safe effect until enemy debuff state is implemented.
        // For now, it will heal the player for 10 health.
        player.heal(10);
    }
}
