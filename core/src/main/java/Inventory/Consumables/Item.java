package Inventory.Consumables;

import Entities.CharacterHero;
import com.badlogic.gdx.graphics.Texture;

public abstract class Item {
    private String name;
    private String description;
    private Texture baseIcon;
    private Texture slotIcon;

    public Item(String name, String description, Texture baseIcon, Texture slotIcon) {
        this.name = name;
        this.description = description;
        this.baseIcon = baseIcon;
        this.slotIcon = slotIcon;
    }

    public String getName(){ return name;}
    public String getDescription() { return description; }
    public Texture getBaseIcon() { return baseIcon; }
    public Texture getSlotIcon() { return slotIcon; }

    public abstract void applyEffect(CharacterHero player);
}
