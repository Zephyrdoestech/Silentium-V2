package Inventory.Consumables;

import Entities.CharacterHero;
import com.badlogic.gdx.graphics.Texture;
import io.github.Zephyrdoestech.Assets;

/**
 * Minor's Grace
 * Start battle with +1 free use of any Minor chord.
 */
public class MinorsGrace extends Item {
    public MinorsGrace(Assets assets){
        super("Minor's Grace",
            "Start battle with +1 free use of any Minor chord.",
            assets.minorsGraceBattleTex,
            assets.minorsGraceSlotItem, 1);
    }
}
