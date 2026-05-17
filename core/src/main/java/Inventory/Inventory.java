package Inventory;

import Entities.CharacterHero;
import Inventory.Consumables.*;
import io.github.Zephyrdoestech.Assets;

import java.util.ArrayList;
import java.util.List;

public class Inventory {

    // ── Constants ─────────────────────────────────────────────────────────────

    private static final int MAX_CAPACITY = 10;

    // ── State ─────────────────────────────────────────────────────────────────

    private final ArrayList<Item> items;

    // ── Constructor ───────────────────────────────────────────────────────────

    public Inventory() {
        this.items = new ArrayList<>();
    }

    // ── Capacity checks ───────────────────────────────────────────────────────

    public boolean isEmpty() { return items.isEmpty(); }
    public boolean isFull() {
        return items.size() >= MAX_CAPACITY;
    }

    // ── Add / Remove ──────────────────────────────────────────────────────────

    public void addItem(Item item) {
        if (isFull()) { return; }
        items.add(item);
    }

    public Item getItem(int index) { return items.get(index); }

    public void removeItem(int index) { items.remove(index); }

    // ── Loot mechanics (placeholder) ──────────────────────────────────────────

    public void randomDrop() {
        // TODO: wire to loot pool / drop-rate table
        System.out.println("[Inventory] randomDrop() called — loot pool not yet implemented.");
    }

    // ── Accessor ──────────────────────────────────────────────────────────────
    public List<Item> getItems() { return new ArrayList<>(items); }
    public int getCapacity() { return MAX_CAPACITY; }
    public int getInventorySize() { return items.size(); }

    // ── Gain Item ──────────────────────────────────────────────────────────────
    public void gainCrimsonChorus(Assets assets){addItem(new CrimsonChorus(assets));}
    public void gainMajorBlessing(Assets assets){addItem(new MajorsBlessing(assets));}
    public void gainMinorsGrace(Assets assets){addItem(new MinorsGrace(assets));}
    public void gainSilentBarrier(Assets assets){addItem(new SilentBarrier(assets));}
    public void gainResolvedDissonance(Assets assets){addItem(new ResolvedDissonance(assets));}
    public void gainTimeOrb(Assets assets){addItem(new TimeOrb(assets));}
}
