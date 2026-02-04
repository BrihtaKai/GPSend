package driven.by.data.gpsend.gui;

import driven.by.data.gpsend.GPSend;
import driven.by.data.gpsend.utils.ColorFormat;
import driven.by.data.gpsend.utils.PlayerStatusManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class ChoosingGUI {

    private final GPSend gpsend = GPSend.getInstance();
    private final boolean placeholderAPIInstalled = gpsend.placeholderAPIInstalled;

    public void open(Player executor) {
        String title1;
        if (placeholderAPIInstalled) {
            title1 = ColorFormat.stringColorise("&#", PlaceholderAPI.setPlaceholders(executor, gpsend.getConfig().getString("gui1_title")));
        } else {
            title1 = ColorFormat.stringColorise("&#", gpsend.getConfig().getString("gui1_title"));
        }
        Inventory inv = Bukkit.createInventory(null, 27, title1);
        ItemStack player = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta player_meta = player.getItemMeta();
        String displayName1;
        if (placeholderAPIInstalled) {
            displayName1 = ColorFormat.stringColorise("&#", PlaceholderAPI.setPlaceholders(executor, gpsend.getConfig().getString("gui1_player_name")));
        } else {
            displayName1 = ColorFormat.stringColorise("&#", gpsend.getConfig().getString("gui1_player_name"));
        }
        player_meta.setDisplayName(displayName1);
        if (!gpsend.getConfig().getList("gui1_player_lore").isEmpty()) {
            ArrayList<String> lore = new ArrayList<String>();
            for (int line = 0; line < gpsend.getConfig().getList("gui1_player_lore").size(); line++) {
                String loreLine;
                if (placeholderAPIInstalled) {
                    loreLine = PlaceholderAPI.setPlaceholders(executor, gpsend.getConfig().getList("gui1_player_lore").get(line).toString());
                } else {
                    loreLine = gpsend.getConfig().getList("gui1_player_lore").get(line).toString();
                }
                lore.add(loreLine);
            }
            player_meta.setLore(ColorFormat.listColorise("&#", lore));
        }
        player.setItemMeta(player_meta);

        ItemStack all = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta all_meta = all.getItemMeta();
        String displayName2;
        if (placeholderAPIInstalled) {
            displayName2 = ColorFormat.stringColorise("&#", PlaceholderAPI.setPlaceholders(executor, gpsend.getConfig().getString("gui1_all_name")));
        } else {
            displayName2 = ColorFormat.stringColorise("&#", gpsend.getConfig().getString("gui1_all_name"));
        }
        all_meta.setDisplayName(displayName2);
        if (!gpsend.getConfig().getList("gui1_all_lore").isEmpty()) {
            ArrayList<String> lore = new ArrayList<String>();
            for (int line = 0; line < gpsend.getConfig().getList("gui1_all_lore").size(); line++) {
                String loreLine;
                if (placeholderAPIInstalled) {
                    loreLine = PlaceholderAPI.setPlaceholders(executor, gpsend.getConfig().getList("gui1_all_lore").get(line).toString());
                } else {
                    loreLine = gpsend.getConfig().getList("gui1_all_lore").get(line).toString();
                }
                lore.add(loreLine);
            }
            all_meta.setLore(ColorFormat.listColorise("&#", lore));
        }
        all.setItemMeta(all_meta);

        inv.setItem(12, player);
        inv.setItem(14, all);

        executor.openInventory(inv);
        PlayerStatusManager.setPlayerStatus(executor.getUniqueId(), "gui-status", "gui1");
    }

    public void close(Player executor) {
        executor.closeInventory();
        PlayerStatusManager.removePlayerStatus(executor.getUniqueId(), "gui-status");
    }

}
