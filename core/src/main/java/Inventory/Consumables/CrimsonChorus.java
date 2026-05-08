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
    public CrimsonChorus(Assets assets){
        super("Crimson Chorus",
            "Converts the player’s current shield into an additional damage buff.",
            assets.crimsonChorusBattleTex,
            assets.crimsonChorusSlotItem, 1);
    }

}
