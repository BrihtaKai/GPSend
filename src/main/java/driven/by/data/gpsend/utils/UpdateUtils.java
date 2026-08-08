package driven.by.data.gpsend.utils;

import driven.by.data.gpsend.GPSend;
import org.bukkit.Bukkit;
import org.bukkit.permissions.ServerOperator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateUtils {

    private final GPSend instance;
    private final String SPIGOT_RESOURCE_ID;

    public UpdateUtils(GPSend instance, String spigotID) {
        this.instance = instance;
        this.SPIGOT_RESOURCE_ID = spigotID;
    }

    /**
     * Checks asynchronously whether a newer version of the plugin is
     * available on Spigot.
     *
     * <p>The current plugin version is obtained from the plugin's
     * {@code plugin.yml}, while the latest version is retrieved from
     * Spigot's update API.</p>
     *
     * <p>If a newer version is available, online server operators are
     * notified. No notification is sent if the latest version cannot
     * be retrieved or is not newer than the installed version.</p>
     */
    public void checkForUpdates() {
        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
            String currentVersion = instance.getDescription().getVersion();
            String latestVersion = getLatestVersion();
            if (latestVersion != null && isUpdateAvailable(currentVersion, latestVersion)) {
                notifyOps(currentVersion, latestVersion);
            }
        });
    }

    /**
     * Starts a repeating asynchronous task that checks for plugin updates
     * once every 24 hours.
     *
     * <p>The first update check is performed after the initial 24-hour
     * interval rather than immediately when this method is called.</p>
     */
    public void startUpdateCheckTask() {
        long interval = 20L * 60 * 60 * 24; // 24 hours in ticks
        Bukkit.getScheduler().runTaskTimerAsynchronously(instance, this::checkForUpdates, interval, interval);
    }

    /**
     * Retrieves the latest available plugin version from Spigot's update API.
     *
     * <p>The request uses the resource ID supplied when this utility was
     * created. The connection has a five-second timeout for both establishing
     * the connection and reading the response.</p>
     *
     * <p>If the request fails for any reason, the error is logged and
     * {@code null} is returned.</p>
     *
     * @return the latest version reported by Spigot, or {@code null} if the
     *         version could not be retrieved
     */
    private String getLatestVersion() {
        try {
            URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + SPIGOT_RESOURCE_ID);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String latestVersion = reader.readLine();
            reader.close();

            return latestVersion;
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to check for updates: " + e.getMessage());
            return null;
        }
    }

    /**
     * Checks whether a newer version is available compared to the current version.
     *
     * <p>Both versions must follow the {@code x.x.x} format, where each component
     * is a non-negative integer. The versions are compared numerically from left
     * to right, rather than lexicographically.</p>
     *
     * <p>For example, {@code 1.10.0} is correctly considered newer than
     * {@code 1.9.0}.</p>
     *
     * @param currentVersion the currently installed version
     * @param latestVersion the latest available version
     * @return {@code true} if {@code latestVersion} is newer than
     *         {@code currentVersion}; {@code false} if the versions are equal,
     *         the current version is newer, or either version has an invalid format
     */
    private boolean isUpdateAvailable(String currentVersion, String latestVersion) {
        String regex = "\\d+\\.\\d+\\.\\d+";

        if (!currentVersion.matches(regex) || !latestVersion.matches(regex)) {
            return false;
        }

        String[] current = currentVersion.split("\\.");
        String[] latest = latestVersion.split("\\.");

        // Parse all components first, before any comparison
        int[] currentParts = new int[3];
        int[] latestParts = new int[3];

        try {
            for (int i = 0; i < 3; i++) {
                currentParts[i] = Integer.parseInt(current[i]);
                latestParts[i] = Integer.parseInt(latest[i]);
            }
        } catch (NumberFormatException e) {
            return false;
        }

        // Only compare after all components are successfully parsed
        for (int i = 0; i < 3; i++) {
            if (latestParts[i] > currentParts[i]) {
                return true;
            }

            if (latestParts[i] < currentParts[i]) {
                return false;
            }
        }

        return false;
    }

    /**
     * Notifies all online server operators that a newer plugin version is
     * available.
     *
     * <p>The notification is scheduled on the server's main thread.
     * The update information is also written to the server log.</p>
     *
     * @param currentVersion the version of the plugin currently installed
     * @param latestVersion the newer version available on Spigot
     */
    private void notifyOps(String currentVersion, String latestVersion) {
        Bukkit.getScheduler().runTask(instance, () -> {
            String message = MessageUtils.getProcessedMessage(
                    null,
                    "&a[GPSend] &eA new version of GPSend is available! " + "Current version: &c" + currentVersion + "&e, Latest version: &a" + latestVersion,
                    false,
                    null
            );

            Bukkit.getOnlinePlayers().stream()
                    .filter(ServerOperator::isOp)
                    .forEach(player -> player.sendMessage(message));

            Bukkit.getLogger().warning(message);
        });
    }
}
