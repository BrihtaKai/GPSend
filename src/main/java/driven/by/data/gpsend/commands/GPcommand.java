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


package driven.by.data.gpsend.commands;

import driven.by.data.gpsend.GPSend;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.logging.Level;



public class GPcommand implements CommandExecutor {

    // ========== getConfig() ============
    private final GPSend plugin;
    public GPcommand(GPSend plugin) {
        this.plugin = plugin;
    }
    // ===================================

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //HANDLE RELOAD CMD
        if (args.length > 0 && "reload".equalsIgnoreCase(args[0])) {
            handleReload(sender);
            return true;
        }
        // IF NOT RELOAD CMD CONTINUE TO CHECKING SENDER -->
        if (!(sender instanceof Player)) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("only_players"))));
            return true;
        }
        Player player = (Player) sender;

        if (args.length != 2) {
            // FORMAT IS WRONG
            if (args.length == 1) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("no_number"))));
            } else if (args.length == 0) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("no_player"))));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("no_number"))));
            }
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("usage"))));
            return true;
        } else if (!args[1].matches("\\d+")) {
            // INPUT IS NOT A NUMBER
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("only_int"))));
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[0]);
        Integer amount = Integer.parseInt(args[1]);
        // CHECK PLAYER
        if (target == null || !target.hasPlayedBefore()) {
            // PLAYER DOES NOT EXIST OR HAS NOT PLAYED BEFORE
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("player_not_found"))));
            return true;
        } else if (target.getName().equals(player.getName())) {
            // YOURSELF
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("yourself"))));
            return true;
        }

        Integer senderB1 = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId()).getBonusClaimBlocks();
        Integer targetB1 = GriefPrevention.instance.dataStore.getPlayerData(target.getUniqueId()).getBonusClaimBlocks();

        // CHECK SEND AMOUNT
        if (amount > senderB1) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("no_blocks"))));
            return true;
        }
        // continue with editing data --->
        Integer senderB2 = senderB1 - amount;
        Integer targetB2 = targetB1 + amount;
        GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId()).setBonusClaimBlocks(senderB2);
        GriefPrevention.instance.dataStore.getPlayerData(target.getUniqueId()).setBonusClaimBlocks(targetB2);

        // SEND MESSAGES
        String configSuccess = plugin.getConfig().getString("sender");
        String configSuccess2 = plugin.getConfig().getString("receiver");

        String successMessage = null;
        String success2Message = null;
        try {} catch (Exception e) {
            successMessage = configSuccess
                    .replace("{number}", String.valueOf(amount))
                    .replace("{target}", target.getName());

            success2Message = configSuccess2
                    .replace("{number}", String.valueOf(amount))
                    .replace("{player}", player.getName());
        }

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', successMessage));
        target.sendMessage(ChatColor.translateAlternateColorCodes('&', success2Message));

        return true;
        /*
        Player player = (sender instanceof Player) ? (Player) sender : null;

        if (sender instanceof Player) {

        } else {

            return true;
        }

        if (args.length == 0) {
            sendMessage(player, "no_player", "no_number", "usage");
            return true;
        }

        if ("reload".equalsIgnoreCase(args[0])) {
            handleReload(player);
            return true;
        }

        if (args.length == 1) {
            sendMessage(player, "no_number", "usage");
            return true;
        }

        if (args.length == 2 && player != null) {
            handleClaimTransfer(player, args);
            return true;
        }

        Bukkit.getLogger().log(Level.INFO, ChatColor.translateAlternateColorCodes('&',
                Objects.requireNonNull(plugin.getConfig().getString("only-players"))));
        return false;
    }

    private void handleReload(Player player) {
        if (player != null && player.hasPermission("gpsend.reload")) {
            plugin.reloadConfig();
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    Objects.requireNonNull(plugin.getConfig().getString("reload"))));
        } else if (player == null) {
            plugin.reloadConfig();
            Bukkit.getLogger().log(Level.INFO, ChatColor.translateAlternateColorCodes('&',
                    Objects.requireNonNull(plugin.getConfig().getString("reload"))));
        } else {
            sendMessage(player, "no_perm", "usage");
        }
    }

    private void handleClaimTransfer(Player player, String[] args) {
        String targetName = args[0];
        String amountStr = args[1];

        if (!amountStr.matches("\\d+")) {
            sendMessage(player, "only_int");
            return;
        }

        int amount = Integer.parseInt(amountStr);
        Player target = Bukkit.getPlayerExact(targetName);

        if (target == null || !target.hasPlayedBefore()) {
            sendMessage(player, "player_not_found");
            return;
        }

        if (targetName.equals(player.getName())) {
            sendMessage(player, "yourself");
            return;
        }

        int senderClaimBlocks = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId()).getRemainingClaimBlocks();

        if (senderClaimBlocks < amount) {
            sendMessage(player, "no_blocks");
            return;
        }

        // Update bonus claim blocks for sender and target
        GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId())
                .setBonusClaimBlocks(senderClaimBlocks - amount);

        int targetClaimBlocks = GriefPrevention.instance.dataStore.getPlayerData(target.getUniqueId()).getRemainingClaimBlocks();
        GriefPrevention.instance.dataStore.getPlayerData(target.getUniqueId())
                .setBonusClaimBlocks(targetClaimBlocks + amount);

        // Send success messages to both sender and receiver
        sendFormattedMessage(player, "sender", "{target}", targetName, "{number}", amountStr);
        sendFormattedMessage(target, "receiver", "{player}", player.getDisplayName(), "{number}", amountStr);
    }

    private void sendMessage(Player player, String... keys) {
        StringBuilder message = new StringBuilder();
        for (String key : keys) {
            message.append(Objects.requireNonNull(plugin.getConfig().getString(key))).append("\n&r");
        }
        sendMessage(player, message.toString().trim());
    }

    private void sendMessage(Player player, String message) {
        if (player == null) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    private void sendFormattedMessage(Player player, String key, String... placeholders) {
        String message = Objects.requireNonNull(plugin.getConfig().getString(key));
        for (int i = 0; i < placeholders.length; i += 2) {
            message = message.replace(placeholders[i], placeholders[i + 1]);
        }
        sendMessage(player, message);
    }

         */

    }

    private void handleReload(CommandSender sender) {
        if ((sender instanceof Player) && !sender.hasPermission("gpsend.reload")) {
            // SENDER IS NOT CONSOLE AND SENDER HAS NO PERM
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("no_perm"))));
            return;
        }
        // RELOAD CONFIG
        plugin.reloadConfig();
        // SEND MSG
        if (sender instanceof Player) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("reload"))));
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("reload"))));
        }

    }
}
