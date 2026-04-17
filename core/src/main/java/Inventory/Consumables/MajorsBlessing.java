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
            assets.majorsBlessingSlotItem);
    }

    @Override
    public void applyEffect(CharacterHero player) {
        // <></>emporary safe effect until enemy debuff state is implemented.
        // For now, it will heal the player for 10 health.
        player.heal(10);
    }
}
