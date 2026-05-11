package Screens.LeaderBoard;

import com.badlogic.gdx.Gdx;
import io.github.Zephyrdoestech.CustomPreferences;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * LeaderboardManager
 *
 * Handles all leaderboard persistence using LibGDX {@link CustomPreferences}.
 *
 * Storage format (indexed entries in one preferences file):
 *   entry_0_name  = "Overlord"
 *   entry_0_maps  = 1
 *   entry_0_time  = 270.5
 *   entry_1_name  = "Shadow"
 *   entry_1_maps  = 2
 *   entry_1_time  = 450.0
 *   ...
 *
 * ── USAGE ─────────────────────────────────────────────────────────────────
 *   Add entry:   LeaderboardManager.addEntry(new LeaderboardEntry(...));
 *   Load all:    List<LeaderboardEntry> entries = LeaderboardManager.loadEntries();
 *   Clear:       LeaderboardManager.clearLeaderboard();
 */
public final class LeaderboardManager {

    // ── Constants ─────────────────────────────────────────────────────────────

    private static final String PREFS_NAME    = "silentium_leaderboard";
    private static final String KEY_COUNT     = "entry_count";
    private static final int    MAX_ENTRIES   = 10;  // keep only top 10 scores

    // ── Private constructor — static-only utility ─────────────────────────────

    private LeaderboardManager() {}

    // ── Core API ──────────────────────────────────────────────────────────────

    /**
     * Adds a new entry to the leaderboard, re-sorts, trims to {@value #MAX_ENTRIES},
     * and writes to disk.
     *
     * @param entry the new {@link LeaderboardEntry}
     */
    public static void addEntry(LeaderboardEntry entry) {
        List<LeaderboardEntry> entries = loadEntries();
        entries.add(entry);
        Collections.sort(entries);  // uses LeaderboardEntry.compareTo()

        // Trim to max size (keep only top 10)
        if (entries.size() > MAX_ENTRIES) {
            entries = entries.subList(0, MAX_ENTRIES);
        }

        saveEntries(entries);
        Gdx.app.log("LeaderboardManager", "Added entry: " + entry.username
            + " | " + entry.mapsCleared + " maps | " + entry.formatTime());
    }

    /**
     * Loads all stored entries from {@link CustomPreferences} and returns them
     * in sorted order (best scores first).
     *
     * @return a sorted list of {@link LeaderboardEntry}, or an empty list if none exist
     */
    public static List<LeaderboardEntry> loadEntries() {
        CustomPreferences prefs = CustomPreferences.getPreferences(PREFS_NAME);
        int count = prefs.getInteger(KEY_COUNT, 0);

        List<LeaderboardEntry> entries = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String name = prefs.getString ("entry_" + i + "_name", "Unknown");
            int    maps = prefs.getInteger("entry_" + i + "_maps", 0);
            float  time = prefs.getFloat  ("entry_" + i + "_time", 0f);
            entries.add(new LeaderboardEntry(name, maps, time));
        }

        Collections.sort(entries);  // ensure sorted even if prefs were modified externally
        return entries;
    }

    /**
     * Overwrites the entire leaderboard with the given list.
     * Used internally by {@link #addEntry(LeaderboardEntry)} and can be
     * called externally to bulk-import entries.
     *
     * @param entries the complete list of entries to store (will be sorted)
     */
    public static void saveEntries(List<LeaderboardEntry> entries) {
        CustomPreferences prefs = CustomPreferences.getPreferences(PREFS_NAME);
        prefs.clear();  // wipe old data to avoid orphaned keys

        Collections.sort(entries);  // ensure sorted before writing

        prefs.putInteger(KEY_COUNT, entries.size());
        for (int i = 0; i < entries.size(); i++) {
            LeaderboardEntry e = entries.get(i);
            prefs.putString ("entry_" + i + "_name", e.username);
            prefs.putInteger("entry_" + i + "_maps", e.mapsCleared);
            prefs.putFloat  ("entry_" + i + "_time", e.timeSeconds);
        }

        prefs.flush();
        Gdx.app.log("LeaderboardManager", "Saved " + entries.size() + " entries.");
    }

    /**
     * Permanently deletes the entire leaderboard.
     * Use sparingly — typically only in a dev/debug menu.
     */
    public static void clearLeaderboard() {
        CustomPreferences prefs = CustomPreferences.getPreferences(PREFS_NAME);
        prefs.clear();
        prefs.flush();
        Gdx.app.log("LeaderboardManager", "Leaderboard cleared.");
    }

    // ── Utility ───────────────────────────────────────────────────────────────

    /**
     * Returns true if there is at least one entry in the leaderboard.
     * Can be used to decide whether to show a "View Leaderboard" button
     * in the main menu.
     */
    public static boolean hasEntries() {
        return CustomPreferences.getPreferences(PREFS_NAME).getInteger(KEY_COUNT, 0) > 0;
    }

    /**
     * Returns the current max entries limit (read-only).
     * If you want a configurable limit, make MAX_ENTRIES non-final.
     */
    public static int getMaxEntries() {
        return MAX_ENTRIES;
    }
}
