package Inventory.Consumables;

import Entities.CharacterHero;
import io.github.Zephyrdoestech.Assets;

/**
 * Crimson Chorus
 * For 2-3 random turns, the enemy takes 5-10% more damage.
 */
public class CrimsonChorus extends Item {
    public CrimsonChorus(Assets assets){
        super("Crimson Chorus",
            "For 2-3 random turns, the enemy takes 5 - 10% more damage.",
            assets.crimsonChorusBattleTex,
            assets.crimsonChorusSlotItem);
    }

    @Override
    public void applyEffect(CharacterHero player) {
        // Temporary safe effect until enemy debuff state is implemented.
        // For now, it will heal the player for 10 health.
        player.heal(10);
    }
}
