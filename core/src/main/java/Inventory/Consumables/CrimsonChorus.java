package Inventory.Consumables;

import Entities.CharacterHero;
import io.github.Zephyrdoestech.Assets;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Random;

/**
 * Crimson Chorus
 * For 2-3 random turns, the enemy takes 5-10% more damage.
 */
public class CrimsonChorus extends Item {
    private float extraDamage = 0f;
    Random rd = new Random();
    private int tracker = rd.nextInt(2,4);

    public CrimsonChorus(Assets assets){
        super("Crimson Chorus",
            "For 2-3 random turns, the enemy takes 5 - 10% more damage.",
            assets.crimsonChorusBattleTex,
            assets.crimsonChorusSlotItem, 0);

        setTracker(tracker);
        extraDamage = rd.nextFloat(5,11);
    }

    public float getExtraDamage(){ return extraDamage; }
}
