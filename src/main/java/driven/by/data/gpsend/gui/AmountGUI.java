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
import me.clip.placeholderapi.PlaceholderAPI;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AmountGUI {

    private final GPSend instance = GPSend.getInstance();
    private final boolean placeholderAPIInstalled = instance.placeholderAPIInstalled;
    private Map<UUID, Integer> playerAmounts = new HashMap<>();
    public Map<UUID, Integer> getPlayerAmounts() {
        return playerAmounts;
    }

    public void open(Player executor, String mode) {
        playerAmounts.putIfAbsent(executor.getUniqueId(), 0);
        int amount = playerAmounts.get(executor.getUniqueId());
        String title;
        if (placeholderAPIInstalled) {
            title = ColorFormat.stringColorise("&#", PlaceholderAPI.setPlaceholders(executor, instance.getConfig().getString("gui3_title")));
        } else {
            title = ColorFormat.stringColorise("&#", instance.getConfig().getString("gui3_title"));
        }
        Inventory inventory = Bukkit.createInventory(null, 27, title);

        // Create a paper item in slot 13 that displays the current count
        ItemStack countPaper = new ItemStack(Material.PAPER);
        ItemMeta paperMeta = countPaper.getItemMeta();
        if (paperMeta != null) {
            paperMeta.setDisplayName(mode + ":" + amount);
            countPaper.setItemMeta(paperMeta);
        }
        inventory.setItem(13, countPaper);

        // Decrement buttons
        // Slot 9: -1000
        ItemStack minus1000 = new ItemStack(Material.REDSTONE);
        ItemMeta minus1000Meta = minus1000.getItemMeta();
        if (minus1000Meta != null) {
            minus1000Meta.setDisplayName("-1000");
            minus1000.setItemMeta(minus1000Meta);
        }
        inventory.setItem(9, minus1000);

        // Slot 10: -100
        ItemStack minus100 = new ItemStack(Material.REDSTONE);
        ItemMeta minus100Meta = minus100.getItemMeta();
        if (minus100Meta != null) {
            minus100Meta.setDisplayName("-100");
            minus100.setItemMeta(minus100Meta);
        }
        inventory.setItem(10, minus100);

        // slot 11: -10
        ItemStack minus10 = new ItemStack(Material.REDSTONE);
        ItemMeta minus10Meta = minus10.getItemMeta();
        if (minus10Meta != null) {
            minus10Meta.setDisplayName("-10");
            minus10.setItemMeta(minus10Meta);
        }
        inventory.setItem(11, minus10);

        // Slot 12: -1
        ItemStack minus1 = new ItemStack(Material.REDSTONE);
        ItemMeta minus1Meta = minus1.getItemMeta();
        if (minus1Meta != null) {
            minus1Meta.setDisplayName("-1");
            minus1.setItemMeta(minus1Meta);
        }
        inventory.setItem(12, minus1);

        // Increment buttons
        // Slot 14: +1
        ItemStack plus1 = new ItemStack(Material.EMERALD);
        ItemMeta plus1Meta = plus1.getItemMeta();
        if (plus1Meta != null) {
            plus1Meta.setDisplayName("+1");
            plus1.setItemMeta(plus1Meta);
        }
        inventory.setItem(14, plus1);

        // Slot 15: +10
        ItemStack plus10 = new ItemStack(Material.EMERALD);
        ItemMeta plus10Meta = plus10.getItemMeta();
        if (plus10Meta != null) {
            plus10Meta.setDisplayName("+10");
            plus10.setItemMeta(plus10Meta);
        }
        inventory.setItem(15, plus10);

        // Slot 16: +100
        ItemStack plus100 = new ItemStack(Material.EMERALD);
        ItemMeta plus100Meta = plus100.getItemMeta();
        if (plus100Meta != null) {
            plus100Meta.setDisplayName("+100");
            plus100.setItemMeta(plus100Meta);
        }
        inventory.setItem(16, plus100);

        // Slot 17: +1000
        ItemStack plus1000 = new ItemStack(Material.EMERALD);
        ItemMeta plus1000Meta = plus1000.getItemMeta();
        if (plus1000Meta != null) {
            plus1000Meta.setDisplayName("+1000");
            plus1000.setItemMeta(plus1000Meta);
        }
        inventory.setItem(17, plus1000);

        // Confirm button on slot 26
        ItemStack confirm = new ItemStack(Material.LIME_WOOL);
        ItemMeta confirmMeta = confirm.getItemMeta();
        if (confirmMeta != null) {
            String confirmTitle;
            if (placeholderAPIInstalled) {
                confirmTitle = ColorFormat.stringColorise("&#", PlaceholderAPI.setPlaceholders(executor, instance.getConfig().getString("gui3_confirm_name")));
            } else {
                confirmTitle = ColorFormat.stringColorise("&#", instance.getConfig().getString("gui3_confirm_name"));
            }
            confirmMeta.setDisplayName(confirmTitle);
            confirm.setItemMeta(confirmMeta);
        }
        inventory.setItem(26, confirm);

        executor.openInventory(inventory);
        PlayerStatusManager.setPlayerStatus(executor.getUniqueId(), "gui-status", "gui3");
    }

    public void close(Player executor) {
        executor.closeInventory();
        PlayerStatusManager.removePlayerStatus(executor.getUniqueId(), "gui-status");
        playerAmounts.remove(executor.getUniqueId());
    }
}