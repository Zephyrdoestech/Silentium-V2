package Inventory.Consumables;

import Entities.CharacterHero;
import io.github.Zephyrdoestech.Assets;

import java.util.Random;

/**
 * Crimson Chorus
 * For 2-3 random turns, the enemy takes 5-10% more damage.
 */
public class CrimsonChorus extends Item {
    private float extraDamage = 0f;

    public CrimsonChorus(Assets assets){
        super("Crimson Chorus",
            "For 2-3 random turns, the enemy takes 5 - 10% more damage.",
            assets.crimsonChorusBattleTex,
            assets.crimsonChorusSlotItem);
    }

    @Override
    public void applyEffect(CharacterHero player) {
        Random rd = new Random();
        setTracker(rd.nextInt(2, 4));

        extraDamage = rd.nextInt(5, 11) / 100.0f;
    }

    public float getExtraDamage() { return extraDamage; }
}
