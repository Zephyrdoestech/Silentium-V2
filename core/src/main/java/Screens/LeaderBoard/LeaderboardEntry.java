package Screens.LeaderBoard;

/**
 * LeaderboardEntry — immutable record of one player's run.
 *
 * Stores:
 *  - Username (e.g. "Overlord")
 *  - Maps cleared out of total (e.g. 1 out of 3 → stored as mapsCleared = 1)
 *  - Total playtime in seconds (e.g. 270.5f → displayed as "4m 30s")
 *
 * Entries are sorted by:
 *  1. Maps cleared (descending — more maps = better)
 *  2. Time (ascending — faster = better)
 */
public class LeaderboardEntry implements Comparable<LeaderboardEntry> {

    public final String username;
    public final int    mapsCleared;   // 0–3 (or however many maps you have)
    public final float  timeSeconds;   // total playtime in seconds

    // ── Constructor ───────────────────────────────────────────────────────────

    public LeaderboardEntry(String username, int mapsCleared, float timeSeconds) {
        this.username     = username;
        this.mapsCleared  = mapsCleared;
        this.timeSeconds  = timeSeconds;
    }

    // ── Sorting ───────────────────────────────────────────────────────────────

    /**
     * Compares two entries for sorting.
     *
     * Sorting order:
     *  1. Maps cleared (descending — 3 > 2 > 1 > 0)
     *  2. Time (ascending — 100s < 200s)
     *
     * Result: Collections.sort() will put the best scores first.
     */
    @Override
    public int compareTo(LeaderboardEntry other) {
        // First: compare maps cleared (higher is better → reversed)
        int mapDiff = Integer.compare(other.mapsCleared, this.mapsCleared);
        if (mapDiff != 0) return mapDiff;

        // Tiebreaker: compare time (lower is better → normal order)
        return Float.compare(this.timeSeconds, other.timeSeconds);
    }

    // ── Display formatting ────────────────────────────────────────────────────

    /**
     * Formats time as a human-readable string:
     *   - Under 60s       → "30s"
     *   - 60s – 3599s     → "4m 30s"
     *   - 3600s and above → "1h 2m 30s"
     *
     * @return formatted time string
     */
    public String formatTime() {
        int totalSeconds = (int) timeSeconds;

        if (totalSeconds < 60) {
            return totalSeconds + "s";
        }

        int hours   = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, seconds);
        } else {
            return String.format("%dm %ds", minutes, seconds);
        }
    }

    /**
     * Formats the entry as "Username | Maps | Time"
     * Example: "Overlord | 1/3 | 4m 30s"
     *
     * @param totalMaps the total number of maps in the game (e.g. 3)
     * @return formatted leaderboard row
     */
    public String formatRow(int totalMaps) {
        return String.format("%s | %d/%d | %s", username, mapsCleared, totalMaps, formatTime());
    }

    // ── Debug ─────────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "LeaderboardEntry{" +
            "username='" + username + '\'' +
            ", mapsCleared=" + mapsCleared +
            ", timeSeconds=" + timeSeconds +
            '}';
    }
}
