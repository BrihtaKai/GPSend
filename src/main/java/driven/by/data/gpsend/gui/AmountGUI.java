package driven.by.data.gpsend.gui;

import driven.by.data.gpsend.GPSend;
import driven.by.data.gpsend.utils.MessageUtils;
import driven.by.data.gpsend.utils.PlayerStatusManager;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import me.clip.placeholderapi.PlaceholderAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AmountGUI {

    private final GPSend instance = GPSend.getInstance();
    private final boolean placeholderAPIInstalled = instance.placeholderAPIInstalled;
    private Map<UUID, Integer> playerAmounts = new HashMap<>();
    private Map<UUID, String> playerModes = new HashMap<>();
    public Map<UUID, Integer> getPlayerAmounts() {
        return playerAmounts;
    }
    public Map<UUID, String> getPlayerModes() { return playerModes; }

    public void open(Player executor, String mode) {
        playerAmounts.putIfAbsent(executor.getUniqueId(), 0);
        playerModes.putIfAbsent(executor.getUniqueId(), mode);
        int amount = playerAmounts.get(executor.getUniqueId());
        
        String title = MessageUtils.getProcessedMessage(executor, "gui3_title", true, null);
        Inventory inventory = Bukkit.createInventory(null, 27, title);

        // Create info item in slot 13
        ItemStack countPaper = createInfoItem(executor, mode, amount);
        inventory.setItem(13, countPaper);

        // Create decrement buttons (slots 9-12, materials)
        createButtonItem(Material.REDSTONE, "minus_4", 9, executor, inventory);
        createButtonItem(Material.REDSTONE, "minus_3", 10, executor, inventory);
        createButtonItem(Material.REDSTONE, "minus_2", 11, executor, inventory);
        createButtonItem(Material.REDSTONE, "minus_1", 12, executor, inventory);

        // Create increment buttons (slots 14-17, materials)
        createButtonItem(Material.EMERALD, "plus_1", 14, executor, inventory);
        createButtonItem(Material.EMERALD, "plus_2", 15, executor, inventory);
        createButtonItem(Material.EMERALD, "plus_3", 16, executor, inventory);
        createButtonItem(Material.EMERALD, "plus_4", 17, executor, inventory);

        // Create confirm button
        createButtonItem(Material.LIME_WOOL, "gui3_confirm_name", 22, executor, inventory);

        inventory.setItem(26, InfoItem.build(executor));

        executor.openInventory(inventory);
        PlayerStatusManager.setPlayerStatus(executor.getUniqueId(), "gui-status", "gui3");
    }

    public void close(Player executor) {
        executor.closeInventory();
        PlayerStatusManager.removePlayerStatus(executor.getUniqueId(), "gui-status");
        playerAmounts.remove(executor.getUniqueId());
        playerModes.remove(executor.getUniqueId());
    }

    public String canSend(int amount, Player executor) {
        int playerAmount;
        PlayerData data = GriefPrevention.instance.dataStore.getPlayerData(executor.getUniqueId());
        if (amount <= 0) return instance.getConfig().getString("affordable_yes");
        if (instance.getConfig().getInt("claimblocks_type") == 0) {
            int accrued = data.getAccruedClaimBlocks();
            int bonus = data.getBonusClaimBlocks();
            playerAmount = accrued + bonus;
        } else if (instance.getConfig().getInt("claimblocks_type") == 1) {
            playerAmount = data.getBonusClaimBlocks();
        } else if (instance.getConfig().getInt("claimblocks_type") == 2) {
            playerAmount = data.getAccruedClaimBlocks();
        } else if (instance.getConfig().getInt("claimblocks_type") == 3) {
            playerAmount = data.getRemainingClaimBlocks();
        } else if (instance.getConfig().getInt("claimblocks_type") == 4) {
            int remaining = data.getRemainingClaimBlocks();
            int bonus = data.getBonusClaimBlocks();
            playerAmount = Math.min(remaining, bonus);
        } else {
            playerAmount = 0;
        }

        String key = amount > playerAmount ? "affordable_no" : "affordable_yes";
        return instance.getConfig().getString(key);
    }

    private void createButtonItem(Material material, String configKey, int slot, Player player, Inventory inventory) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        String displayName = MessageUtils.getProcessedMessage(player, configKey, true, null);
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);
        inventory.setItem(slot, item);
    }

    private ItemStack createInfoItem(Player executor, String mode, int amount) {
        ItemStack countPaper = new ItemStack(Material.PAPER);
        ItemMeta paperMeta = countPaper.getItemMeta();
        if (paperMeta != null) {
            String countPaperName = MessageUtils.getProcessedMessage(executor, "gui3_info_item_name", true, Map.of("%mode%", mode));
            paperMeta.setDisplayName(countPaperName);

            ArrayList<String> lore = new ArrayList<>();
            boolean isAllMode = ChatColor.stripColor(mode)
                    .equalsIgnoreCase(ChatColor.stripColor(instance.getConfig().getString("all_mode_name")));

            if (isAllMode) {
                for (Object loreObj : instance.getConfig().getList("gui3_info_item_lore_all", new ArrayList<>())) {
                    String loreLine = MessageUtils.getProcessedMessage(executor, "gui3_info_item_lore_all", true, Map.of(
                            "%amount%", String.valueOf(amount),
                            "%total%", String.valueOf(amount * (Bukkit.getOnlinePlayers().size() - 1)),
                            "%affordable%", canSend(amount * (Bukkit.getOnlinePlayers().size() - 1), executor))
                    );
                    lore.add(loreLine);
                }
            } else {
                for (Object loreObj : instance.getConfig().getList("gui3_info_item_lore_player", new ArrayList<>())) {
                    String loreLine = MessageUtils.getProcessedMessage(executor, "gui3_info_item_lore_player", true, Map.of(
                            "%amount%", String.valueOf(amount),
                            "%affordable%", canSend(amount, executor)));
                    lore.add(loreLine);
                }
            }

            if (!lore.isEmpty()) {
                paperMeta.setLore(MessageUtils.listColorise("&#", lore));
            }
            countPaper.setItemMeta(paperMeta);
        }
        return countPaper;
    }
}