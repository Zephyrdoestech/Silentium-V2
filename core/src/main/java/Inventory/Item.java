package Inventory;

import Entities.Character;
import com.badlogic.gdx.graphics.Texture;
import io.github.Zephyrdoestech.Assets;

/**
 * Item — contract every inventory item must fulfill.
 *
 * All three methods are required:
 *  - getName()        used for display in menus and the console
 *  - getDescription() shown in the inventory list and item tooltips
 *  - applyEffect()    called by Inventory.useItem() during combat
 */
public interface Item {

    /** @return the item's display name (e.g. "Crimson Chorus") */
    String getName();

    /** @return one-line description of the item's effect */
    String getDescription();

    /**
     * Applies this item's effect to the player character.
     * Called once when the player selects "Use" on this item.
     *
     * @param player the active player {@link Character}
     */
    void applyEffect(Character player);
    Texture getInventoryIcon(Assets assets);
    Texture getBattleIcon(Assets assets);
}
