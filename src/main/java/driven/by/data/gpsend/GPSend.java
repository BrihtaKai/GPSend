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

import driven.by.data.gpsend.command.*;
import driven.by.data.gpsend.listener.GUIInteract;
import driven.by.data.gpsend.request.RequestManager;
import driven.by.data.gpsend.utils.UpdateUtils;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;


public final class GPSend extends JavaPlugin {

    private static GPSend instance;
    private AliasManager aliasManager;
    private RequestManager requestManager;
    private UpdateUtils updateUtils;
    public boolean placeholderAPIInstalled;

    public GPSend() {
        GPSend.instance = this;
    }

    public static GPSend getInstance() {
        return instance;
    }
    public AliasManager getAliasManager() {
        return aliasManager;
    }
    public RequestManager getRequestManager() {
        return requestManager;
    }


    @Override
    public void onEnable() {

        placeholderAPIInstalled = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;

        mkConfig();
        this.aliasManager = new AliasManager();
        this.requestManager = new RequestManager();
        this.updateUtils = new UpdateUtils(instance, "115468");
        Bukkit.getPluginManager().registerEvents(new GUIInteract(), this);

        initMetrics();

        if (getConfig().getBoolean("check_for_updates")) {
            updateUtils.startUpdateCheckTask();
            updateUtils.checkForUpdates();
        }

        //register commands
        Objects.requireNonNull(getCommand("gpsend")).setExecutor(new GpsendCommand());
        Objects.requireNonNull(getCommand("gpsend")).setTabCompleter(new TabCompleterSend());

        Objects.requireNonNull(getCommand("gprequest")).setExecutor(new GprequestCommand());
        Objects.requireNonNull(getCommand("gprequest")).setTabCompleter(new TabCompleterRequest());
        aliasManager.gpsendAliasRegister();

        Bukkit.getLogger().info("\n" +
                "╭━━━┳━━━┳━━━╮╱╱╱╱╱╱╱╭╮\n" +
                "┃╭━╮┃╭━╮┃╭━╮┃╱╱╱╱╱╱╱┃┃\n" +
                "┃┃╱╰┫╰━╯┃╰━━┳━━┳━╮╭━╯┃\n" +
                "┃┃╭━┫╭━━┻━━╮┃┃━┫╭╮┫╭╮┃\n" +
                "┃╰┻━┃┃╱╱┃╰━╯┃┃━┫┃┃┃╰╯┃\n" +
                "╰━━━┻╯╱╱╰━━━┻━━┻╯╰┻━━╯ has been enabled!\n");

        if (getConfig().getInt("claimblocks_type") == 0) {
            Bukkit.getLogger().warning("You are using claimblock type 0 (TOTAL CLAIMBLOCKS) which is not recommended!");
        }

        requestManager.startExpireRemoveCycle();

    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("\n" +
                "╭━━━┳━━━┳━━━╮╱╱╱╱╱╱╱╭╮\n" +
                "┃╭━╮┃╭━╮┃╭━╮┃╱╱╱╱╱╱╱┃┃\n" +
                "┃┃╱╰┫╰━╯┃╰━━┳━━┳━╮╭━╯┃\n" +
                "┃┃╭━┫╭━━┻━━╮┃┃━┫╭╮┫╭╮┃\n" +
                "┃╰┻━┃┃╱╱┃╰━╯┃┃━┫┃┃┃╰╯┃\n" +
                "╰━━━┻╯╱╱╰━━━┻━━┻╯╰┻━━╯ has been disabled!\n");
    }

    private void initMetrics() {
        int pluginId = 22118;
        new Metrics(this, pluginId);
    }

    // MIGRATION LOGIC IS TO BE REMOVED IN ANY VERSION AFTER 3.0.0
    public void mkConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        File configFile = new File(getDataFolder(), "config.yml");
        int CURRENT_CV = 3;

        if (configFile.exists()) {
            FileConfiguration existing = YamlConfiguration.loadConfiguration(configFile);

            if (existing.getInt("cv", -1) != CURRENT_CV) {
                File backup = new File(getDataFolder(), "v2.yml");

                if (configFile.renameTo(backup)) {
                    getLogger().warning(
                            "Outdated config.yml detected (missing/old 'cv' key) — "
                                    + "backed up as v2.yml and generating a fresh default config."
                    );
                } else {
                    getLogger().warning(
                            "Outdated config.yml detected but failed to back it up to v2.yml! "
                                    + "Check file permissions in the plugin data folder."
                    );
                }
            }
        }

        getConfig().options().copyDefaults();
        saveDefaultConfig();
        reloadConfig();
    }





}
