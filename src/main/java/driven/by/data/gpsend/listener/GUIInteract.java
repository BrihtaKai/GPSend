package driven.by.data.gpsend.listener;

import driven.by.data.gpsend.GPSend;
import driven.by.data.gpsend.utils.ColorFormat;
import driven.by.data.gpsend.utils.PlayerStatusManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.logging.Level;

public class GUIInteract implements Listener {

    private final GPSend instance = GPSend.getInstance();
    // Add a set to track players who are reopening GUIs
    private Set<UUID> reopeningGUI = new HashSet<>();

    /*
    GUI1 : CHOOSING GUI
    GUI2 : PLAYER LIST
    GUI3 : AMOUNT
     */

    @EventHandler
    public void onInteract(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        if (!PlayerStatusManager.hasPlayerStatus(p.getUniqueId(), "gui-status")) {
            // PLAYER DID NOT OPEN THIS PLUGIN'S GUI
            return;
        }

        String status = PlayerStatusManager.getPlayerStatus(p.getUniqueId(), "gui-status");
        switch (status) {
            case "gui1": {
                handleGUI1(event, p);
                break;
            }
            case "gui2": {
                handleGUI2(event, p);
                break;
            }
            case "gui3": {
                handleGUI3(event, p);
                break;
            }
        }
    }

    private void handleGUI1(InventoryClickEvent event, Player p) {
        event.setCancelled(true);
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null) {return;}

        if (clickedItem.getType() == Material.PLAYER_HEAD) {
            instance.getGuiManager().getChoosingGUI().close(p);
            p.performCommand("gpsend player");
        } else  if (clickedItem.getType() == Material.EMERALD_BLOCK) {
            instance.getGuiManager().getChoosingGUI().close(p);
            p.performCommand("gpsend all");
        } else {
            //none of buttons clicked
        }
    }

    private void handleGUI2(InventoryClickEvent event, Player p) {
        event.setCancelled(true);
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        Map<UUID, Integer> playerPages = instance.getGuiManager().getPlayerListGUI().getPlayerPages();

        if (clickedItem.getType() == Material.ARROW) {
            String itemName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
            int currentPage = playerPages.getOrDefault(p.getUniqueId(), 0);
            if ("Previous Page".equals(itemName)) {
                reopeningGUI.add(p.getUniqueId()); // Add player to reopening set
                instance.getGuiManager().getPlayerListGUI().close(p);
                instance.getGuiManager().getPlayerListGUI().open(p, currentPage - 1);
            } else if ("Next Page".equals(itemName)) {
                reopeningGUI.add(p.getUniqueId()); // Add player to reopening set
                instance.getGuiManager().getPlayerListGUI().close(p);
                instance.getGuiManager().getPlayerListGUI().open(p, currentPage + 1);
            }
        } else if (clickedItem.getType() == Material.PLAYER_HEAD) {
            String targetName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
            Player target = Bukkit.getPlayer(targetName);

            instance.getGuiManager().getPlayerListGUI().close(p);
            p.performCommand("gpsend player " + targetName);
        }
    }

    private void handleGUI3(InventoryClickEvent event, Player p) {
        event.setCancelled(true);

        Map<UUID, Integer> playerAmounts = instance.getGuiManager().getAmountGUI().getPlayerAmounts();
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta() || clicked.getItemMeta().getDisplayName() == null) {
            return;
        }

        int amount = playerAmounts.getOrDefault(p.getUniqueId(), 0);
        String mode = instance.getGuiManager().getAmountGUI().getPlayerModes().get(p.getUniqueId());

        String displayName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());

        // Check if the clicked inventory and slot 13 exist
        if (event.getClickedInventory() == null) {
            return;
        }

        // Handle adjustments to the amount
        if (clicked.getType() == Material.LIME_WOOL) {
            instance.getGuiManager().getAmountGUI().close(p);
            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);

            // Execute the gpsend command based on the mode
            if (instance.getConfig().getString("all_mode_name").equals(mode)) {
                p.performCommand("gpsend all " + amount);
            } else {
                p.performCommand("gpsend player " + mode + " " + amount);
            }
            return;
        }

        // Handle the amount adjustments
        switch (displayName) {
            case "-1000": {
                if (amount >= 1000) {
                    amount -= 1000;
                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                } else {
                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_FALL, 1.0f, 1.0f);
                }
                break;
            }
            case "-100": {
                if (amount >= 100) {
                    amount -= 100;
                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                } else {
                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_FALL, 1.0f, 1.0f);
                }
                break;
            }
            case "-10": {
                if (amount >= 10) {
                    amount -= 10;
                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                } else {
                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_FALL, 1.0f, 1.0f);
                }
                break;
            }
            case "-1": {
                if (amount >= 1) {
                    amount -= 1;
                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                } else {
                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_FALL, 1.0f, 1.0f);
                }
                break;
            }
            case "+1": {
                amount += 1;
                p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                break;
            }
            case "+10": {
                amount += 10;
                p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                break;
            }
            case "+100": {
                amount += 100;
                p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                break;
            }
            case "+1000": {
                amount += 1000;
                p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                break;
            }
            default:
                return;
        }

        // Update player's amount in the map
        playerAmounts.put(p.getUniqueId(), amount);

        // Add player to reopening set before opening new inventory
        reopeningGUI.add(p.getUniqueId());

        // Refresh the GUI with the updated amount
        instance.getGuiManager().getAmountGUI().open(p, mode);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // Check if this player is reopening a GUI
        if (reopeningGUI.contains(playerUUID)) {
            // Remove from set but don't clear status
            reopeningGUI.remove(playerUUID);
            return;
        }

        // Only clear status if player actually has a GUI status
        if (PlayerStatusManager.hasPlayerStatus(playerUUID, "gui-status")) {
            PlayerStatusManager.removePlayerStatus(playerUUID, "gui-status");
            instance.getGuiManager().getPlayerListGUI().getPlayerPages().remove(playerUUID);
            instance.getGuiManager().getAmountGUI().getPlayerAmounts().remove(playerUUID);
            instance.getGuiManager().getAmountGUI().getPlayerModes().remove(playerUUID);
        }
    }
}