package Inventory.Consumables;

import Entities.CharacterHero;
import com.badlogic.gdx.graphics.Texture;

public abstract class Item {
    private String name;
    private String description;
    private Texture baseIcon;
    private Texture slotIcon;
    private int tracker;

    public Item(String name, String description, Texture baseIcon, Texture slotIcon) {
        this.name = name;
        this.description = description;
        this.baseIcon = baseIcon;
        this.slotIcon = slotIcon;
        tracker = 0;
    }

    public String getName(){ return name;}
    public String getDescription() { return description; }
    public Texture getBaseIcon() { return baseIcon; }
    public Texture getSlotIcon() { return slotIcon; }
    public int getTracker() { return tracker; }
    public void setTracker(int tracker) { this.tracker = tracker; }

    public abstract void applyEffect(CharacterHero player);
}
