package Inventory;

import Entities.Character;
import java.util.ArrayList;
import java.util.List;

/**
 * Inventory — manages up to {@value #MAX_CAPACITY} items for a single character.
 *
 * Responsibilities:
 *  - Adding and removing items
 *  - Using items (applies effect then removes from list)
 *  - Displaying the current contents to the console
 *  - Placeholder loot-drop mechanics (to be wired to combat later)
 */
public class Inventory {

    // ── Constants ─────────────────────────────────────────────────────────────

    private static final int MAX_CAPACITY = 10;

    // ── State ─────────────────────────────────────────────────────────────────

    private final List<Item> items;

    // ── Constructor ───────────────────────────────────────────────────────────

    public Inventory() {
        this.items = new ArrayList<>();
    }

    // ── Capacity checks ───────────────────────────────────────────────────────

    /**
     * Returns {@code true} when the inventory has reached its maximum capacity
     * and no more items can be added.
     */
    public boolean isFull() {
        return items.size() >= MAX_CAPACITY;
    }

    // ── Add / Remove ──────────────────────────────────────────────────────────

    /**
     * Adds an item to the inventory if there is space.
     * Prints a warning to the console if the inventory is already full.
     *
     * @param item the {@link Item} to add
     */
    public void addItem(Item item) {
        if (isFull()) {
            System.out.println("[Inventory] Cannot add \"" + item.getName()
                + "\" — inventory is full (" + MAX_CAPACITY + "/" + MAX_CAPACITY + ").");
            return;
        }
        items.add(item);
        System.out.println("[Inventory] Added: " + item.getName()
            + " (" + items.size() + "/" + MAX_CAPACITY + ")");
    }

    /**
     * Removes a specific item from the inventory if it is present.
     * This is the "drop" action — the item is not used, just discarded.
     *
     * @param item the {@link Item} to remove
     */
    public void tryDrop(Item item) {
        if (items.remove(item)) {
            System.out.println("[Inventory] Dropped: " + item.getName());
        } else {
            System.out.println("[Inventory] Could not drop \"" + item.getName()
                + "\" — item not found in inventory.");
        }
    }

    /**
     * Clears all items from the inventory.
     * Intended for use at the start of a new run or after game-over.
     */
    public void resetInventory() {
        items.clear();
        System.out.println("[Inventory] Inventory has been reset.");
    }

    // ── Display ───────────────────────────────────────────────────────────────

    /**
     * Prints the current inventory contents to the console.
     * Lists each item's index, name, and description.
     */
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
        System.out.println("=================================");
    }

    // ── Use ───────────────────────────────────────────────────────────────────

    /**
     * Uses the item at the given (1-based) index:
     * calls {@link Item#applyEffect(Character)} then removes the item.
     *
     * @param player the active player {@link Character}
     * @param index  1-based index as shown by {@link #showInventory()}
     */
    public void useItem(Character player, int index) {
        int i = index - 1; // convert to 0-based
        if (i < 0 || i >= items.size()) {
            System.out.println("[Inventory] Invalid item index: " + index);
            return;
        }
        Item item = items.get(i);
        System.out.println("[Inventory] Using: " + item.getName());
        item.applyEffect(player);
        items.remove(i);
        System.out.println("[Inventory] " + item.getName() + " has been consumed.");
    }

    // ── Loot mechanics (placeholder) ──────────────────────────────────────────

    /**
     * Placeholder — randomly selects and adds one item from the loot pool.
     * Hook this up to the combat reward system once the item pool is defined.
     */
    public void randomDrop() {
        // TODO: wire to loot pool / drop-rate table
        System.out.println("[Inventory] randomDrop() called — loot pool not yet implemented.");
    }

    /**
     * Placeholder — adds a specific guaranteed item to the inventory after combat.
     *
     * @param item the {@link Item} that is guaranteed to drop
     */
    public void guaranteedDrop(Item item) {
        System.out.println("[Inventory] Guaranteed drop: " + item.getName());
        addItem(item);
    }

    /**
     * Placeholder — adds two specific guaranteed items to the inventory after combat.
     *
     * @param item1 first guaranteed drop
     * @param item2 second guaranteed drop
     */
    public void doubleGuaranteedDrop(Item item1, Item item2) {
        System.out.println("[Inventory] Double guaranteed drop: "
            + item1.getName() + " & " + item2.getName());
        addItem(item1);
        addItem(item2);
    }

    // ── Accessor ──────────────────────────────────────────────────────────────

    /** @return a copy of the current item list (read-only view for UI rendering) */
    public List<Item> getItems() {
        return new ArrayList<>(items);
    }
}
