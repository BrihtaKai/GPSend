package driven.by.data.gpsend.gui;

import driven.by.data.gpsend.GPSend;
import driven.by.data.gpsend.utils.ColorFormat;
import driven.by.data.gpsend.utils.PlayerStatusManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ChoosingGUI {

    private final GPSend gpsend = GPSend.getInstance();

    public void open(Player executor) {
        Inventory inv = Bukkit.createInventory(null, 27, ColorFormat.stringColorise("&#", gpsend.getConfig().getString("gui1_title")));

        ItemStack player = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta player_meta = player.getItemMeta();
        player_meta.setItemName(ChatColor.translateAlternateColorCodes('&', ColorFormat.stringColorise("&#", gpsend.getConfig().getString("gui1_player_name"))));
        if (!gpsend.getConfig().getList("gui1_player_lore").isEmpty()) {
            ArrayList lore = new ArrayList<String>();
            for (int line = 0; line < gpsend.getConfig().getList("gui1_player_lore").size(); line++) {
                lore.add(gpsend.getConfig().getList("gui1_player_lore").get(line));
            }
            player_meta.setLore(ColorFormat.listColorise("&#", lore));
        }
        player.setItemMeta(player_meta);

        ItemStack all = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta all_meta = all.getItemMeta();
        all_meta.setItemName(ChatColor.translateAlternateColorCodes('&', ColorFormat.stringColorise("&#", gpsend.getConfig().getString("gui1_all_name"))));
        if (!gpsend.getConfig().getList("gui1_all_lore").isEmpty()) {
            ArrayList lore = new ArrayList<String>();
            for (int line = 0; line < gpsend.getConfig().getList("gui1_all_lore").size(); line++) {
                lore.add(gpsend.getConfig().getList("gui1_all_lore").get(line));
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
