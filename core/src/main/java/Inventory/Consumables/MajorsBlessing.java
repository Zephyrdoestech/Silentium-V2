package Inventory.Consumables;

import Entities.CharacterHero;
import com.badlogic.gdx.graphics.Texture;
import io.github.Zephyrdoestech.Assets;

/**
 * Major's Blessing
 * Start battle with +1 free use of any Major chord.
 */
public class MajorsBlessing extends Item {
    public MajorsBlessing(Assets assets){
        super("Major's Blessing",
            "Start battle with +1 free use of any Major chord.",
            assets.majorsBlessingBattleTex,
            assets.majorsBlessingSlotItem, 1);
    }

}
