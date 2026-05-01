package driven.by.data.gpsend.utils;

import driven.by.data.gpsend.GPSend;
import me.clip.placeholderapi.PlaceholderAPI;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SendingHandler {

    private static final GPSend plugin = GPSend.getInstance();


    public static void handleAllSending(Player player, int amount) {
        PlayerData senderData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        int senderBonus = senderData.getBonusClaimBlocks();
        int senderAccrued = senderData.getAccruedClaimBlocks();
        int senderTotal = senderBonus + senderAccrued;

        int onlinePlayersCount = Bukkit.getOnlinePlayers().size() - 1;
        int totalAmount = amount * onlinePlayersCount;
        if (onlinePlayersCount == 0) {
            MessageUtils.sendMessage(player, "no_players", true);
            return;
        }

        int mode = plugin.getConfig().getInt("claimblocks_type");
        String type = "*";
        switch (mode) {
            case 0: {
                type = "total";
                if (senderTotal < totalAmount) {
                    String message = plugin.getConfig().getString("no_enough_blocks")
                            .replace("%type%", type)
                            .replace("%need%", String.valueOf(totalAmount - senderTotal));

                    MessageUtils.sendMessage(player, message, false);
                    return;
                }
                break;
            }
            case 1: {
                type = "bonus";
                if (senderBonus < totalAmount) {
                    String message = plugin.getConfig().getString("no_enough_blocks")
                            .replace("%type%", type)
                            .replace("%need%", String.valueOf(totalAmount - senderBonus));

                    MessageUtils.sendMessage(player, message, false);
                    return;
                }
                break;
            }
            case 2: {
                type = "accrued";
                if (senderAccrued < totalAmount) {
                    String message = plugin.getConfig().getString("no_enough_blocks")
                            .replace("%type%", type)
                            .replace("%need%", String.valueOf(totalAmount - senderAccrued));

                    MessageUtils.sendMessage(player, message, false);
                    return;
                }
                break;
            }
            case 3: {
                type = "remaining";
                int senderRemaining = senderData.getRemainingClaimBlocks();
                if (senderRemaining < totalAmount) {
                    String message = plugin.getConfig().getString("no_enough_blocks")
                            .replace("%type%", type)
                            .replace("%need%", String.valueOf(totalAmount - senderRemaining));

                    MessageUtils.sendMessage(player, message, false);
                    return;
                }
                break;
            }
            case 4: {
                type = "remaining-bonus";
                int senderRemaining = senderData.getRemainingClaimBlocks();
                int maxSendable = Math.min(senderRemaining, senderBonus);
                if (maxSendable < totalAmount) {
                    String message = plugin.getConfig().getString("no_enough_blocks")
                            .replace("%type%", type)
                            .replace("%need%", String.valueOf(totalAmount - maxSendable));

                    MessageUtils.sendMessage(player, message, false);
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
            String message = plugin.getConfig().getString("broadcast_message")
                    .replace("%player%", player.getName())
                    .replace("%type%", type)
                    .replace("%amount%", String.valueOf(amount))
                    .replace("%total%", String.valueOf(totalAmount));

            MessageUtils.sendMessage(player, message, false);
        }
    }

    public static void handleSending(Player sender, String targetName, int amount, boolean all) {
        Player targetPlayer = Bukkit.getPlayerExact(targetName);

        // Check if the target player is online
        if (targetPlayer == null) {
            MessageUtils.sendMessage(sender, "player_not_found", true);
            return;
        }

        if (sender.getName().equalsIgnoreCase(targetName)) {
            if (all) {
                MessageUtils.sendMessage(sender, "cannot_send_to_self", true);
            }
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
                    String message = plugin.getConfig().getString("no_enough_blocks")
                            .replace("%type%", type)
                            .replace("%need%", String.valueOf(amount - senderTotal));

                    MessageUtils.sendMessage(sender, message, false);
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
                    String senderMessage = plugin.getConfig().getString("sender")
                            .replace("%amount%", String.valueOf(amount))
                            .replace("%target%", targetPlayer.getName())
                            .replace("%type%", type);

                    MessageUtils.sendMessage(sender, senderMessage, false);
                }

                String targetMessage = plugin.getConfig().getString("receiver")
                        .replace("%player%", sender.getName())
                        .replace("%amount%", String.valueOf(amount))
                        .replace("%type%", type);

                MessageUtils.sendMessage(targetPlayer, targetMessage, false);
                return;
            }
            case 1: {
                // BONUS CLAIM BLOCKS
                type = "bonus";
                if (senderBonus < amount) {
                    String message = plugin.getConfig().getString("no_enough_blocks")
                            .replace("%type%", type)
                            .replace("%need%", String.valueOf(amount - senderBonus));

                    MessageUtils.sendMessage(sender, message, false);
                    return;
                }

                int senderNewBonus = senderBonus - amount;
                int targetNewBonus = targetBonus + amount;

                senderData.setBonusClaimBlocks(senderNewBonus);
                targetData.setBonusClaimBlocks(targetNewBonus);

                if (all) {
                    String senderMessage = plugin.getConfig().getString("sender")
                            .replace("%amount%", String.valueOf(amount))
                            .replace("%target%", targetPlayer.getName())
                            .replace("%type%", type);

                    MessageUtils.sendMessage(sender, senderMessage, false);
                }

                String targetMessage = plugin.getConfig().getString("receiver")
                        .replace("%player%", sender.getName())
                        .replace("%amount%", String.valueOf(amount))
                        .replace("%type%", type);

                MessageUtils.sendMessage(targetPlayer, targetMessage, false);
                return;
            }
            case 2: {
                // ACCRUED CLAIM BLOCKS
                type = "accrued";
                if (senderAccrued < amount) {
                    String message = plugin.getConfig().getString("no_enough_blocks")
                            .replace("%type%", type)
                            .replace("%need%", String.valueOf(amount - senderAccrued));

                    MessageUtils.sendMessage(sender, message, false);
                    return;
                }

                int senderNewAccrued = senderAccrued - amount;
                int targetNewAccrued = targetAccrued + amount;

                senderData.setAccruedClaimBlocks(senderNewAccrued);
                targetData.setAccruedClaimBlocks(targetNewAccrued);

                if (all) {
                    String senderMessage = plugin.getConfig().getString("sender")
                            .replace("%amount%", String.valueOf(amount))
                            .replace("%target%", targetPlayer.getName())
                            .replace("%type%", type);

                    MessageUtils.sendMessage(sender, senderMessage, false);
                }

                String targetMessage = plugin.getConfig().getString("receiver")
                        .replace("%player%", sender.getName())
                        .replace("%amount%", String.valueOf(amount))
                        .replace("%type%", type);

                MessageUtils.sendMessage(targetPlayer, targetMessage, false);
                return;
            }
            case 3: {
                // REMAINING CLAIM BLOCKS (available blocks not used in claims)
                type = "remaining";
                int senderRemaining = senderData.getRemainingClaimBlocks();
                if (senderRemaining < amount) {
                    String message = plugin.getConfig().getString("no_enough_blocks")
                            .replace("%type%", type)
                            .replace("%need%", String.valueOf(amount - senderRemaining));

                    MessageUtils.sendMessage(sender, message, false);
                    return;
                }

                // For remaining blocks mode, deplete bonus first, then accrued
                int bonusToUse = Math.min(senderBonus, amount);
                int accruedToUse = amount - bonusToUse;

                senderData.setBonusClaimBlocks(senderBonus - bonusToUse);
                senderData.setAccruedClaimBlocks(senderAccrued - accruedToUse);

                int targetNewBonus = targetBonus + amount;
                targetData.setBonusClaimBlocks(targetNewBonus);

                if (all) {
                    String senderMessage = plugin.getConfig().getString("sender")
                            .replace("%amount%", String.valueOf(amount))
                            .replace("%target%", targetPlayer.getName())
                            .replace("%type%", type);

                    MessageUtils.sendMessage(sender, senderMessage, false);
                }

                String targetMessage = plugin.getConfig().getString("receiver")
                        .replace("%player%", sender.getName())
                        .replace("%amount%", String.valueOf(amount))
                        .replace("%type%", type);

                MessageUtils.sendMessage(targetPlayer, targetMessage, false);
                return;
            }
            case 4: {
                // REMAINING + BONUS CAP (min of remaining and bonus)
                type = "remaining-bonus";
                int senderRemaining = senderData.getRemainingClaimBlocks();
                int maxSendable = Math.min(senderRemaining, senderBonus);
                if (maxSendable < amount) {
                    String message = plugin.getConfig().getString("no_enough_blocks")
                            .replace("%type%", type)
                            .replace("%need%", String.valueOf(amount - maxSendable));

                    MessageUtils.sendMessage(sender, message, false);
                    return;
                }

                int senderNewBonus = senderBonus - amount;
                int targetNewBonus = targetBonus + amount;

                senderData.setBonusClaimBlocks(senderNewBonus);
                targetData.setBonusClaimBlocks(targetNewBonus);

                if (all) {
                    String senderMessage = plugin.getConfig().getString("sender")
                            .replace("%amount%", String.valueOf(amount))
                            .replace("%target%", targetPlayer.getName())
                            .replace("%type%", type);

                    MessageUtils.sendMessage(sender, senderMessage, false);
                }

                String targetMessage = plugin.getConfig().getString("receiver")
                        .replace("%player%", sender.getName())
                        .replace("%amount%", String.valueOf(amount))
                        .replace("%type%", type);

                MessageUtils.sendMessage(targetPlayer, targetMessage, false);
            }
        }
    }
}
