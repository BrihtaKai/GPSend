package driven.by.data.gpsend.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerStatusManager {

    // Map to store player statuses, allowing multiple statuses per player
    private static final Map<UUID, Map<String, String>> playerStatuses = new HashMap<>();

    /**
     * Set a specific status for a player.
     *
     * @param playerUUID The UUID of the player.
     * @param key The key identifying the status.
     * @param value The value of the status.
     */
    public static void setPlayerStatus(UUID playerUUID, String key, String value) {
        playerStatuses.computeIfAbsent(playerUUID, k -> new HashMap<>()).put(key, value);
    }

    /**
     * Get a specific status of a player.
     *
     * @param playerUUID The UUID of the player.
     * @param key The key identifying the status.
     * @return The value of the status, or null if not set.
     */
    public static String getPlayerStatus(UUID playerUUID, String key) {
        Map<String, String> statuses = playerStatuses.get(playerUUID);
        return statuses != null ? statuses.get(key) : null;
    }

    /**
     * Check if a player has a specific status.
     *
     * @param playerUUID The UUID of the player.
     * @param key The key identifying the status.
     * @return True if the player has the status, false otherwise.
     */
    public static boolean hasPlayerStatus(UUID playerUUID, String key) {
        Map<String, String> statuses = playerStatuses.get(playerUUID);
        return statuses != null && statuses.containsKey(key);
    }

    /**
     * Remove a specific status of a player.
     *
     * @param playerUUID The UUID of the player.
     * @param key The key identifying the status.
     */
    public static void removePlayerStatus(UUID playerUUID, String key) {
        Map<String, String> statuses = playerStatuses.get(playerUUID);
        if (statuses != null) {
            statuses.remove(key);
            if (statuses.isEmpty()) {
                playerStatuses.remove(playerUUID);
            }
        }
    }

    /**
     * Remove all statuses of a player.
     *
     * @param playerUUID The UUID of the player.
     */
    public static void clearPlayerStatuses(UUID playerUUID) {
        playerStatuses.remove(playerUUID);
    }

    /**
     * Clear all player statuses. Useful for cleanup.
     */
    public static void clearAllStatuses() {
        playerStatuses.clear();
    }
}
