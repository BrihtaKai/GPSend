package driven.by.data.gpsend.command;

import driven.by.data.gpsend.GPSend;
import driven.by.data.gpsend.utils.ColorFormat;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GpsendCommand implements CommandExecutor {

    private final GPSend plugin = GPSend.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getServer().getLogger().warning("[GPSend] Only players can use this command!");
            return false;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("gpsend.send") && !player.isOp()) {
            player.sendMessage(ColorFormat.stringColorise("&#", plugin.getConfig().getString("no_permission")));
            return false;
        }

        int argLength = args.length;
        if (argLength == 0) {
            plugin.getGuiManager().getChoosingGUI().open(player);
            return false;
        } else {
            String type = args[0].toLowerCase();
            switch (type) {
                case "player": {
                    if (argLength == 1) {
                        plugin.getGuiManager().getPlayerListGUI().open(player, 0);
                        return true;
                    }
                    if (argLength == 2) {
                        String playerName = args[1];
                        plugin.getGuiManager().getAmountGUI().open(player, playerName);
                        return true;
                    }
                    if (argLength >= 3) {
                        try {
                            String playerName = args[1];
                            int amount = Integer.parseInt(args[2]);
                            handleSending(player, playerName, amount, true);
                        } catch (NumberFormatException e) {
                            player.sendMessage(ColorFormat.stringColorise("&#", plugin.getConfig().getString("invalid_amount")));
                        }
                        return true;
                    }
                    break;
                }
                case "all": {
                    if (!player.hasPermission("gpsend.sendall") && !player.isOp()) {
                        player.sendMessage(ColorFormat.stringColorise("&#", plugin.getConfig().getString("no_permission")));
                        return false;
                    }

                    if (argLength == 1) {
                        plugin.getGuiManager().getAmountGUI().open(player, "ALL");
                        return true;
                    }
                    if (argLength >= 2) {
                        try {
                            int amount = Integer.parseInt(args[1]);
                            handleAllSending(player, amount);
                        } catch (NumberFormatException e) {
                            player.sendMessage(ColorFormat.stringColorise("&#", plugin.getConfig().getString("invalid_amount")));
                        }
                        return true;
                    }
                    break;
                }
                default: {
                    player.sendMessage(ColorFormat.stringColorise("&#", plugin.getConfig().getString("invalid_type")));
                    break;
                }
            }
        }

        return false;
    }


    public void handleAllSending(Player player, int amount) {

        PlayerData senderData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        int senderBonus = senderData.getBonusClaimBlocks();
        int senderAccrued = senderData.getAccruedClaimBlocks();
        int senderTotal = senderBonus + senderAccrued;

        int onlinePlayersCount = Bukkit.getOnlinePlayers().size() - 1;
        int totalAmount = amount * onlinePlayersCount;

        int mode = plugin.getConfig().getInt("claimblocks_type");
        String type = "*";
        switch (mode) {
            case 0: {
                type = "total";
                if (senderTotal < totalAmount) {
                    player.sendMessage(ColorFormat.stringColorise("&#", plugin.getConfig().getString("no_enough_blocks")
                            .replace("%type%", type)
                            .replace("%need%", String.valueOf(totalAmount - senderTotal))
                    ));
                    return;
                }
                break;
            }
            case 1: {
                type = "bonus";
                if (senderBonus < totalAmount) {
                    player.sendMessage(ColorFormat.stringColorise("&#", plugin.getConfig().getString("no_enough_blocks")
                            .replace("%type%", type)
                            .replace("%need%", String.valueOf(totalAmount - senderBonus))
                    ));
                    return;
                }
                break;
            }
            case 2: {
                type = "accrued";
                if (senderAccrued < totalAmount) {
                    player.sendMessage(ColorFormat.stringColorise("&#", plugin.getConfig().getString("no_enough_blocks")
                            .replace("%type%", type)
                            .replace("%need%", String.valueOf(totalAmount - senderAccrued))
                    ));
                    return;
                }
                break;
            }
        }

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer != player) {
                handleSending(player, onlinePlayer.getName(), amount, plugin.getConfig().getBoolean("sendall_log"));
            }
        }

        if (plugin.getConfig().getBoolean("broadcast_on_sendall")){
            Bukkit.broadcastMessage(ColorFormat.stringColorise("&#", plugin.getConfig().getString("broadcast_message"))
                    .replace("%player%", player.getName())
                    .replace("%type%", type)
                    .replace("%amount%", String.valueOf(amount))
                    .replace("%total%", String.valueOf(totalAmount))
            );
        }

    }

    public void handleSending(Player sender, String targetName, int amount, boolean all) {
        Player targetPlayer = Bukkit.getPlayerExact(targetName);

        // Check if the target player is online
        if (targetPlayer == null) {
            sender.sendMessage(ColorFormat.stringColorise("&#", plugin.getConfig().getString("player_not_found")));
            return;
        }

        int mode = plugin.getConfig().getInt("claimblocks_type");

        // Fetch PlayerData once for both sender and target
        PlayerData senderData = GriefPrevention.instance.dataStore.getPlayerData(sender.getUniqueId());
        PlayerData targetData = GriefPrevention.instance.dataStore.getPlayerData(targetPlayer.getUniqueId());

        int senderBonus = senderData.getBonusClaimBlocks();
        int senderAccrued = senderData.getAccruedClaimBlocks();
        int senderTotal = senderBonus + senderAccrued;

        int targetBonus = targetData.getBonusClaimBlocks();
        int targetAccrued = targetData.getAccruedClaimBlocks();
        int targetTotal = targetBonus + targetAccrued;

        String type = "*";
        switch (mode) {
            case 0: {
                // TOTAL CLAIM BLOCKS
                type = "total";
                if (senderTotal < amount) {
                    sender.sendMessage(ColorFormat.stringColorise("&#", plugin.getConfig().getString("no_enough_blocks")
                            .replace("%type%", type)
                            .replace("%need%", String.valueOf(amount - senderTotal))
                    ));
                    return;
                }

                int senderNewTotal = senderTotal - amount;
                int targetNewTotal = targetTotal + amount;

                // Update Sender Claim Blocks
                if (senderNewTotal > senderBonus) {
                    senderData.setAccruedClaimBlocks(senderNewTotal - senderBonus);
                    senderData.setBonusClaimBlocks(senderBonus);
                } else {
                    senderData.setAccruedClaimBlocks(0);
                    senderData.setBonusClaimBlocks(senderNewTotal);
                }

                // Update Target Claim Blocks
                if (targetNewTotal > targetBonus) {
                    targetData.setAccruedClaimBlocks(targetNewTotal - targetBonus);
                    targetData.setBonusClaimBlocks(targetBonus);
                } else {
                    targetData.setAccruedClaimBlocks(0);
                    targetData.setBonusClaimBlocks(targetNewTotal);
                }

                if (all) {
                    sender.sendMessage(ColorFormat.stringColorise("&#", plugin.getConfig().getString("sender"))
                            .replace("%amount%", String.valueOf(amount))
                            .replace("%target%", targetPlayer.getName())
                            .replace("%type%", type));
                }
                targetPlayer.sendMessage(ColorFormat.stringColorise("&#", plugin.getConfig().getString("receiver"))
                        .replace("%player%", sender.getName())
                        .replace("%amount%", String.valueOf(amount))
                        .replace("%type%", type)
                );
                return;
            }
            case 1: {
                // BONUS CLAIM BLOCKS
                type = "bonus";
                if (senderBonus < amount) {
                    sender.sendMessage(ColorFormat.stringColorise("&#", plugin.getConfig().getString("no_enough_blocks")
                            .replace("%type%", type)
                            .replace("%need%", String.valueOf(amount - senderBonus))
                    ));
                    return;
                }

                int senderNewBonus = senderBonus - amount;
                int targetNewBonus = targetBonus + amount;

                senderData.setBonusClaimBlocks(senderNewBonus);
                targetData.setBonusClaimBlocks(targetNewBonus);

                if (all) {
                    sender.sendMessage(ColorFormat.stringColorise("&#", plugin.getConfig().getString("sender"))
                            .replace("%amount%", String.valueOf(amount))
                            .replace("%target%", targetPlayer.getName())
                            .replace("%type%", type));
                }
                targetPlayer.sendMessage(ColorFormat.stringColorise("&#", plugin.getConfig().getString("receiver"))
                        .replace("%player%", sender.getName())
                        .replace("%amount%", String.valueOf(amount))
                        .replace("%type%", type)
                );
                return;
            }
            case 2: {
                // ACCRUED CLAIM BLOCKS
                type = "accrued";
                if (senderAccrued < amount) {
                    sender.sendMessage(ColorFormat.stringColorise("&#", plugin.getConfig().getString("no_enough_blocks")
                            .replace("%type%", type)
                            .replace("%need%", String.valueOf(amount - senderAccrued))
                    ));
                    return;
                }

                int senderNewAccrued = senderAccrued - amount;
                int targetNewAccrued = targetAccrued + amount;

                senderData.setAccruedClaimBlocks(senderNewAccrued);
                targetData.setAccruedClaimBlocks(targetNewAccrued);

                if (all) {
                    sender.sendMessage(ColorFormat.stringColorise("&#", plugin.getConfig().getString("sender"))
                            .replace("%amount%", String.valueOf(amount))
                            .replace("%target%", targetPlayer.getName())
                            .replace("%type%", type));
                }
                targetPlayer.sendMessage(ColorFormat.stringColorise("&#", plugin.getConfig().getString("receiver"))
                        .replace("%player%", sender.getName())
                        .replace("%amount%", String.valueOf(amount))
                        .replace("%type%", type)
                );
                return;
            }
        }
    }
}
