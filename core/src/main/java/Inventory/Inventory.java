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

    public Item transferItem(int index) { Item temp = items.get(index); items.remove(index); return temp; }

    public void resetInventory() { items.clear(); }

    // ── Display ───────────────────────────────────────────────────────────────

    public void showInventory() {
        System.out.println("=== INVENTORY (" + items.size() + "/" + MAX_CAPACITY + ") ===");
        if (items.isEmpty()) {
            System.out.println("  (empty)");
        } else {
            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                System.out.printf("  [%d] %-22s — %s%n",
                    i + 1, item.getName(), item.getDescription());
            }
        }
    }

    // ── Use ───────────────────────────────────────────────────────────────────

//    public void useItem(CharacterHero player, int index) {
//        int i = index - 1; // convert to 0-based
//        if (i < 0 || i >= items.size()) {
//            System.out.println("[Inventory] Invalid item index: " + index);
//            return;
//        }
//        Item item = items.get(i);
//        System.out.println("[Inventory] Using: " + item.getName());
//        item.applyEffect(player);
//        items.remove(i);
//        System.out.println("[Inventory] " + item.getName() + " has been consumed.");
//    }

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
