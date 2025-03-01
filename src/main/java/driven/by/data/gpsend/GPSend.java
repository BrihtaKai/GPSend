//————————————————————————————————————————————————————————————————————————\
// Copyright (c) 2024 DrivenByData (BrihtaKai)                            |
// Licensed under the MIT license.                                        |
//                                                                        |
// Permission is hereby granted, free of charge, to any person            |
// obtaining a copy of this software and associated documentation         |
// files (the "Software"), to deal in the Software without                |
// restriction, including without limitation the rights to use,           |
// copy, modify, merge, publish, distribute, sublicense, and/or sell      |
// copies of the Software, and to permit persons to whom the              |
// Software is furnished to do so, subject to the following               |
// conditions:                                                            |
//                                                                        |
// The above copyright notice and this permission notice shall be         |
// included in all copies or substantial portions of the Software.        |
//                                                                        |
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,        |
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES        |
// OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND               |
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT            |
// HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,           |
// WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING           |
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR          |
// OTHER DEALINGS IN THE SOFTWARE.                                        |
//————————————————————————————————————————————————————————————————————————⁄


package driven.by.data.gpsend;

import driven.by.data.gpsend.command.GpsendAdmin;
import driven.by.data.gpsend.command.GpsendCommand;
import driven.by.data.gpsend.command.TabCompleter;
import driven.by.data.gpsend.gui.GUIManager;
import driven.by.data.gpsend.listener.GUIInteract;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;


public final class GPSend extends JavaPlugin {

    private static GPSend instance;
    private GUIManager guiManager;
    private static final String SPIGOT_RESOURCE_ID = "115468";

    public GPSend() {
        this.instance = this;
    }

    public static GPSend getInstance() {
        return instance;
    }
    public GUIManager getGuiManager() {
        return guiManager;
    }


    @Override
    public void onEnable() {

        this.guiManager = new GUIManager(instance);
        Bukkit.getPluginManager().registerEvents(new GUIInteract(), this);

        mkConfig();
        initMetrics();

        if (getConfig().getBoolean("check_for_updates")) {
            startUpdateCheckTask();
            checkForUpdates();
        }

        //register commands
        Objects.requireNonNull(getCommand("gpsend")).setExecutor(new GpsendCommand());
        Objects.requireNonNull(getCommand("gpsend")).setTabCompleter(new TabCompleter());
        Objects.requireNonNull(getCommand("gpsend-admin")).setExecutor(new GpsendAdmin());
        Objects.requireNonNull(getCommand("gpsend-admin")).setTabCompleter(new GpsendAdmin());

        Bukkit.getLogger().info("GPSend has been enabled!");


    }

    @Override
    public void onDisable() {

    }

    private void initMetrics() {
        int pluginId = 00000; // <-- Metrics plugin ID
        new Metrics(this, pluginId);
    }

    public void mkConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        getConfig().options().copyDefaults();
        saveDefaultConfig();
    }


    // Check for updates immediately when the plugin starts
    private void checkForUpdates() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            String currentVersion = getDescription().getVersion();
            String latestVersion = getLatestVersion();
            if (latestVersion != null && isUpdateAvailable(currentVersion, latestVersion)) {
                notifyOps(currentVersion, latestVersion);
            }
        });
    }

    // Start a repeating task to check for updates every 24 hours (20 ticks * 60 * 60 * 24 = 1 day)
    private void startUpdateCheckTask() {
        long interval = 20L * 60 * 60 * 24; // 24 hours in ticks
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, this::checkForUpdates, interval, interval);
    }

    // Fetch the latest version from Spigot's update checker API
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

    // Compare the current version with the latest version
    private boolean isUpdateAvailable(String currentVersion, String latestVersion) {
        return !currentVersion.equalsIgnoreCase(latestVersion); // Simple comparison (can be extended for more complex versioning schemes)
    }

    // Notify server operators if a new version is available
    private void notifyOps(String currentVersion, String latestVersion) {
        Bukkit.getScheduler().runTask(this, () -> {
            String message = ChatColor.translateAlternateColorCodes('&',
                    "&a[GPSend] &eA new version of GPSend is available! " +
                            "Current version: &c" + currentVersion + "&e, Latest version: &a" + latestVersion);

            Bukkit.getOnlinePlayers().stream()
                    .filter(player -> player.isOp())
                    .forEach(player -> player.sendMessage(message));

            Bukkit.getLogger().warning(message);
        });
    }


}
