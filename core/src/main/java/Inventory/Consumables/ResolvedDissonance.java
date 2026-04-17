package Inventory.Consumables;

import Entities.CharacterHero;
import com.badlogic.gdx.graphics.Texture;
import io.github.Zephyrdoestech.Assets;

/**
 * Resolved Dissonance
 * The next time the player plays B Diminished, they don't lose HP.
 */
public class ResolvedDissonance extends Item {
    public ResolvedDissonance(Assets assets){
        super("Resolved Dissonance",
            "The next time the player plays B Diminished, they don't lose HP.",
            assets.resolvedDissonanceBattleTex,
            assets.resolvedDissonanceSlotItem);
    }

    @Override
    public void applyEffect(CharacterHero player) {
        // Temporary safe effect until enemy debuff state is implemented.
        // For now, it will heal the player for 10 health.
        player.heal(10);
    }
}
