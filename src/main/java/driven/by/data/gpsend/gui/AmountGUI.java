package driven.by.data.gpsend.gui;

import driven.by.data.gpsend.GPSend;
import driven.by.data.gpsend.utils.ColorFormat;
import driven.by.data.gpsend.utils.PlayerStatusManager;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
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
            String countPaperName;
            if (placeholderAPIInstalled) {
                countPaperName = ColorFormat.stringColorise("&#", PlaceholderAPI.setPlaceholders(executor, instance.getConfig().getString("gui3_info_item_name")
                        .replace("%mode%", mode))
                );
            } else {
                countPaperName = ColorFormat.stringColorise("&#", instance.getConfig().getString("gui3_info_item_name")
                        .replace("%mode%", mode)
                );
            }
            paperMeta.setDisplayName(countPaperName);

            if (ChatColor.stripColor(mode).equalsIgnoreCase(ChatColor.stripColor(instance.getConfig().getString("all_mode_name")))) {
                if (!instance.getConfig().getList("gui3_info_item_lore_all").isEmpty()) {
                    ArrayList lore = new ArrayList<String>();
                    for (int line = 0; line < instance.getConfig().getList("gui3_info_item_lore_all").size(); line++) {
                        String loreLine;
                        if (placeholderAPIInstalled) {
                            loreLine = PlaceholderAPI.setPlaceholders(executor, instance.getConfig().getList("gui3_info_item_lore_all").get(line).toString()
                                    .replace("%amount%", String.valueOf(amount))
                                    .replace("%total%", String.valueOf(amount * (Bukkit.getOnlinePlayers().size() - 1)))
                                    .replace("%affordable%", this.canSend(amount * (Bukkit.getOnlinePlayers().size() - 1), executor))
                            );
                        } else {
                            loreLine = instance.getConfig().getList("gui3_info_item_lore_all").get(line).toString()
                                    .replace("%amount%", String.valueOf(amount))
                                    .replace("%total%", String.valueOf(amount * (Bukkit.getOnlinePlayers().size() - 1)))
                                    .replace("%affordable%", this.canSend(amount * (Bukkit.getOnlinePlayers().size() - 1), executor)
                            );
                        }
                        lore.add(loreLine);
                    }
                    paperMeta.setLore(ColorFormat.listColorise("&#", lore));
                }
            } else {
                if (!instance.getConfig().getList("gui3_info_item_lore_player").isEmpty()) {
                    ArrayList lore = new ArrayList<String>();
                    for (int line = 0; line < instance.getConfig().getList("gui3_info_item_lore_player").size(); line++) {
                        String loreLine;
                        if (placeholderAPIInstalled) {
                            loreLine = PlaceholderAPI.setPlaceholders(executor, instance.getConfig().getList("gui3_info_item_lore_player").get(line).toString()
                                    .replace("%amount%", String.valueOf(amount))
                                    .replace("%affordable%", this.canSend(amount, executor))
                            );
                        } else {
                            loreLine = instance.getConfig().getList("gui3_info_item_lore_player").get(line).toString()
                                    .replace("%amount%", String.valueOf(amount))
                                    .replace("%affordable%", this.canSend(amount, executor)
                            );
                        }
                        lore.add(loreLine);
                    }
                    paperMeta.setLore(ColorFormat.listColorise("&#", lore));
                }
            }
            countPaper.setItemMeta(paperMeta);
        }
        inventory.setItem(13, countPaper);

        // Decrement buttons
        // Slot 9: -1000
        ItemStack minus1000 = new ItemStack(Material.REDSTONE);
        ItemMeta minus1000Meta = minus1000.getItemMeta();
        if (minus1000Meta != null) {
            String displayName;
            if (placeholderAPIInstalled) {
                displayName = ColorFormat.stringColorise("&#", PlaceholderAPI.setPlaceholders(executor, instance.getConfig().getString("minus_1000")));
            } else {
                displayName = ColorFormat.stringColorise("&#", instance.getConfig().getString("minus_1000"));
            }
            minus1000Meta.setDisplayName(displayName);
            minus1000.setItemMeta(minus1000Meta);
            inventory.setItem(9, minus1000);

            // Slot 10: -100
            ItemStack minus100 = new ItemStack(Material.REDSTONE);
            ItemMeta minus100Meta = minus100.getItemMeta();
            if (minus100Meta != null) {
                String displayName2;
                if (placeholderAPIInstalled) {
                    displayName2 = ColorFormat.stringColorise("&#", PlaceholderAPI.setPlaceholders(executor, instance.getConfig().getString("minus_100")));
                } else {
                    displayName2 = ColorFormat.stringColorise("&#", instance.getConfig().getString("minus_100"));
                }
                minus100Meta.setDisplayName(displayName2);
                minus100.setItemMeta(minus100Meta);
            }
            inventory.setItem(10, minus100);

            // slot 11: -10
            ItemStack minus10 = new ItemStack(Material.REDSTONE);
            ItemMeta minus10Meta = minus10.getItemMeta();
            if (minus10Meta != null) {
                String displayName3;
                if (placeholderAPIInstalled) {
                    displayName3 = ColorFormat.stringColorise("&#", PlaceholderAPI.setPlaceholders(executor, instance.getConfig().getString("minus_10")));
                } else {
                    displayName3 = ColorFormat.stringColorise("&#", instance.getConfig().getString("minus_10"));
                }
                minus10Meta.setDisplayName(displayName3);
                minus10.setItemMeta(minus10Meta);
            }
            inventory.setItem(11, minus10);

            // Slot 12: -1
            ItemStack minus1 = new ItemStack(Material.REDSTONE);
            ItemMeta minus1Meta = minus1.getItemMeta();
            if (minus1Meta != null) {
                String displayName4;
                if (placeholderAPIInstalled) {
                    displayName4 = ColorFormat.stringColorise("&#", PlaceholderAPI.setPlaceholders(executor, instance.getConfig().getString("minus_1")));
                } else {
                    displayName4 = ColorFormat.stringColorise("&#", instance.getConfig().getString("minus_1"));
                }
                minus1Meta.setDisplayName(displayName4);
                minus1.setItemMeta(minus1Meta);
            }
            inventory.setItem(12, minus1);

            // Increment buttons
            // Slot 14: +1
            ItemStack plus1 = new ItemStack(Material.EMERALD);
            ItemMeta plus1Meta = plus1.getItemMeta();
            if (plus1Meta != null) {
                String displayName5;
                if (placeholderAPIInstalled) {
                    displayName5 = ColorFormat.stringColorise("&#", PlaceholderAPI.setPlaceholders(executor, instance.getConfig().getString("plus_1")));
                } else {
                    displayName5 = ColorFormat.stringColorise("&#", instance.getConfig().getString("plus_1"));
                }
                plus1Meta.setDisplayName(displayName5);
                plus1.setItemMeta(plus1Meta);
            }
            inventory.setItem(14, plus1);

            // Slot 15: +10
            ItemStack plus10 = new ItemStack(Material.EMERALD);
            ItemMeta plus10Meta = plus10.getItemMeta();
            if (plus10Meta != null) {
                String displayName6;
                if (placeholderAPIInstalled) {
                    displayName6 = ColorFormat.stringColorise("&#", PlaceholderAPI.setPlaceholders(executor, instance.getConfig().getString("plus_10")));
                } else {
                    displayName6 = ColorFormat.stringColorise("&#", instance.getConfig().getString("plus_10"));
                }
                plus10Meta.setDisplayName(displayName6);
                plus10.setItemMeta(plus10Meta);
            }
            inventory.setItem(15, plus10);

            // Slot 16: +100
            ItemStack plus100 = new ItemStack(Material.EMERALD);
            ItemMeta plus100Meta = plus100.getItemMeta();
            if (plus100Meta != null) {
                String displayName7;
                if (placeholderAPIInstalled) {
                    displayName7 = ColorFormat.stringColorise("&#", PlaceholderAPI.setPlaceholders(executor, instance.getConfig().getString("plus_100")));
                } else {
                    displayName7 = ColorFormat.stringColorise("&#", instance.getConfig().getString("plus_100"));
                }
                plus100Meta.setDisplayName(displayName7);
                plus100.setItemMeta(plus100Meta);
            }
            inventory.setItem(16, plus100);

            // Slot 17: +1000
            ItemStack plus1000 = new ItemStack(Material.EMERALD);
            ItemMeta plus1000Meta = plus1000.getItemMeta();
            if (plus1000Meta != null) {
                String displayName8;
                if (placeholderAPIInstalled) {
                    displayName8 = ColorFormat.stringColorise("&#", PlaceholderAPI.setPlaceholders(executor, instance.getConfig().getString("plus_1000")));
                } else {
                    displayName8 = ColorFormat.stringColorise("&#", instance.getConfig().getString("plus_1000"));
                }
                plus1000Meta.setDisplayName(displayName8);
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
    }

    public void close(Player executor) {
        executor.closeInventory();
        PlayerStatusManager.removePlayerStatus(executor.getUniqueId(), "gui-status");
        playerAmounts.remove(executor.getUniqueId());
        playerModes.remove(executor.getUniqueId());
    }

    public String canSend(int amount, Player executor) {
        int playerAmount;
        if (instance.getConfig().getInt("claimblocks_type") == 0) {
            playerAmount = GriefPrevention.instance.dataStore.getPlayerData(executor.getUniqueId()).getAccruedClaimBlocks();
        } else if (instance.getConfig().getInt("claimblocks_type") == 1) {
            playerAmount = GriefPrevention.instance.dataStore.getPlayerData(executor.getUniqueId()).getBonusClaimBlocks();
        } else if (instance.getConfig().getInt("claimblocks_type") == 2) {
            int accrued = GriefPrevention.instance.dataStore.getPlayerData(executor.getUniqueId()).getAccruedClaimBlocks();
            int bonus = GriefPrevention.instance.dataStore.getPlayerData(executor.getUniqueId()).getBonusClaimBlocks();
            playerAmount = accrued + bonus;
        } else {
            playerAmount = 0;
        }

        if (amount > playerAmount) {
            return instance.getConfig().getString("affordable_no");
        }
        return instance.getConfig().getString("affordable_yes");
    }
}