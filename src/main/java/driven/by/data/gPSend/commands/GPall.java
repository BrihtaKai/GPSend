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

import java.util.Collection;
import java.util.Objects;

/// TODO CORRECT CONSOLE HANDLING AND MESSAGES

public class GPall implements CommandExecutor {

    // ========== getConfig() ============
    private final GPSend plugin;
    public GPall(GPSend plugin) {
        this.plugin = plugin;
    }
    // ===================================


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("only_players"))));
            return true;
        }
        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("usage2"))));
            return true;
        } else if (!args[0].matches("\\d+")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("only_int"))));
            return true;
        }

        int amount = Integer.parseInt(args[0]);
        Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
        int totalBlocksNeeded = amount * (onlinePlayers.size() - 1);

        int senderB1 = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId()).getBonusClaimBlocks();
        if (senderB1 < totalBlocksNeeded) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("no_blocks"))));
            return true;
        }

        for (Player player2 : onlinePlayers) {
            if (!player2.equals(sender)) {
                int receiverB1 = GriefPrevention.instance.dataStore.getPlayerData(player2.getUniqueId()).getBonusClaimBlocks();
                GriefPrevention.instance.dataStore.getPlayerData(player2.getUniqueId()).setBonusClaimBlocks(receiverB1 + amount);
                String message = plugin.getConfig().getString("receiver")
                        .replace("{player}", player.getName())
                        .replace("{number}", String.valueOf(amount));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            } else {
                GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId()).setBonusClaimBlocks(senderB1 - totalBlocksNeeded);

            }
        }
        Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("toall")
                .replace("{player}", player.getName())
                .replace("{number}", String.valueOf(amount))
        )));

        /*
        if (!(sender instanceof Player)) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("only-players"))));
            return false;
        }

        Player player = (Player) sender;
        if (args.length != 1 || !args[0].matches("\\d+")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("usage2"))));
            return false;
        }

        int amount = Integer.parseInt(args[0]);
        Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
        int totalBlocksNeeded = amount * (onlinePlayers.size() - 1);

        if (!hasSufficientClaimBlocks(player, totalBlocksNeeded)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("no_blocks"))));
            return false;
        }

        distributeClaimBlocks(player, onlinePlayers, amount, totalBlocksNeeded);
        return true;
    }

    private boolean hasSufficientClaimBlocks(Player player, int blocksNeeded) {
        int senderClaimBlocks = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId()).getRemainingClaimBlocks();
        return senderClaimBlocks >= blocksNeeded;
    }

    private void distributeClaimBlocks(Player sender, Collection<? extends Player> players, int amount, int blocksDeducted) {
        String senderMessage = formatMessage("toall", sender, amount);
        String receiverMessage = formatMessage("receiver", sender, amount);

        for (Player player : players) {
            if (!player.equals(sender)) {
                adjustClaimBlocks(player, amount);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', receiverMessage));
            }
        }

        Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', senderMessage));
        adjustClaimBlocks(sender, -blocksDeducted);
    }

    private void adjustClaimBlocks(Player player, int amount) {
        int currentClaimBlocks = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId()).getRemainingClaimBlocks();
        GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId()).setBonusClaimBlocks(currentClaimBlocks + amount);
    }

    private String formatMessage(String key, Player sender, int amount) {
        return Objects.requireNonNull(plugin.getConfig().getString(key))
                .replace("{player}", sender.getDisplayName())
                .replace("{number}", String.valueOf(amount));
    }
    */
        return true;
    }
}
