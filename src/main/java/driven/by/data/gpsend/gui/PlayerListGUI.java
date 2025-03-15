package driven.by.data.gpsend.gui;

import driven.by.data.gpsend.GPSend;
import driven.by.data.gpsend.utils.ColorFormat;
import driven.by.data.gpsend.utils.PlayerStatusManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class PlayerListGUI {

    private final GPSend instance = GPSend.getInstance();
    private final boolean placeholderAPIInstalled = instance.placeholderAPIInstalled;
    private Map<UUID, Integer> playerPages = new HashMap<>();

    public Map<UUID, Integer> getPlayerPages() { return playerPages; }


    public void open(Player executor, int page) {
        playerPages.put(executor.getUniqueId(), page);
        String title;
        if (placeholderAPIInstalled) {
            title = ColorFormat.stringColorise("&#", PlaceholderAPI.setPlaceholders(executor, instance.getConfig().getString("gui2_title")));
        } else {
            title = ColorFormat.stringColorise("&#", instance.getConfig().getString("gui2_title"));
        }
        Inventory inv = Bukkit.createInventory(null, 54, title);

        ItemStack line = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta line_meta = line.getItemMeta();
        line_meta.setDisplayName(null);
        line.setItemMeta(line_meta);
        for (int i = 36; i != 45; i++) {
            inv.setItem(i, line);
        }

        ItemStack pageint = new ItemStack(Material.PAPER);
        ItemMeta page_meta = pageint.getItemMeta();
        page_meta.setItemName(String.valueOf(page));
        pageint.setItemMeta(page_meta);
        inv.setItem(49, pageint);

        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        onlinePlayers.remove(executor);
        int startIndex = page * 36;
        int endIndex = Math.min(startIndex + 36, onlinePlayers.size());

        for (int i = startIndex, slot = 0; i < endIndex; i++, slot++) {
            Player onlinePlayer = onlinePlayers.get(i);
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            if (meta != null) {
                meta.setOwningPlayer(onlinePlayer);
                meta.setDisplayName(ChatColor.YELLOW + onlinePlayer.getName());
                skull.setItemMeta(meta);
            }
            inv.setItem(slot, skull);
        }

        // Add pagination controls
        if (page > 0) {
            ItemStack prevPage = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prevPage.getItemMeta();
            if (prevMeta != null) {
                String prevMetaTtitle;
                if (placeholderAPIInstalled) {
                    prevMetaTtitle = ColorFormat.stringColorise("&#", PlaceholderAPI.setPlaceholders(executor, instance.getConfig().getString("gui2_prev_page")));
                } else {
                    prevMetaTtitle = ColorFormat.stringColorise("&#", instance.getConfig().getString("gui2_prev_page"));
                }
                prevMeta.setDisplayName(prevMetaTtitle);
                prevPage.setItemMeta(prevMeta);
            }
            inv.setItem(45, prevPage);
        }
        if (endIndex < onlinePlayers.size()) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextPage.getItemMeta();
            if (nextMeta != null) {
                String nextMetaTtitle;
                if (placeholderAPIInstalled) {
                    nextMetaTtitle = ColorFormat.stringColorise("&#", PlaceholderAPI.setPlaceholders(executor, instance.getConfig().getString("gui2_next_page")));
                } else {
                    nextMetaTtitle = ColorFormat.stringColorise("&#", instance.getConfig().getString("gui2_next_page"));
                }
                nextMeta.setDisplayName(nextMetaTtitle);
                nextPage.setItemMeta(nextMeta);
            }
            inv.setItem(53, nextPage);
        }

        executor.openInventory(inv);
        PlayerStatusManager.setPlayerStatus(executor.getUniqueId(), "gui-status", "gui2");

    }

    public void close(Player executor) {
        executor.closeInventory();
        PlayerStatusManager.removePlayerStatus(executor.getUniqueId(), "gui-status");
        playerPages.remove(executor.getUniqueId());
    }
}
