package driven.by.data.gpsend.utils;

import driven.by.data.gpsend.GPSend;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendingHandler {

    private static final GPSend instance = GPSend.getInstance();

    public static void handleSending(Player sender, String targetName, int amount, boolean showSenderMessage) {
        Player target = Bukkit.getPlayerExact(targetName);

        if (target == null) {
            MessageUtils.sendMessage(sender, "player_not_found", true, null);
            return;
        }

        if (sender.getName().equalsIgnoreCase(targetName)) {
            if (showSenderMessage) {
                MessageUtils.sendMessage(sender, "cannot_send_to_self", true, null);
            }
            return;
        }

        int mode = instance.getConfig().getInt("claimblocks_type");
        String type = getTypeName(mode);

        PlayerData senderData = GriefPrevention.instance.dataStore.getPlayerData(sender.getUniqueId());
        PlayerData targetData = GriefPrevention.instance.dataStore.getPlayerData(target.getUniqueId());

        if (!hasEnough(senderData, amount, mode)) {
            int shortfall = amount - getBalance(senderData, mode);
            Map<String, String> replacers = new HashMap<>();
            replacers.put("%type%", type);
            replacers.put("%need%", String.valueOf(shortfall));
            MessageUtils.sendMessage(sender, "no_enough_blocks", true, replacers);
            return;
        }

        deductCB(senderData, targetData, amount, mode);
        saveData(sender, senderData, target, targetData);

        if (showSenderMessage) {
            Map<String, String> replacers = new HashMap<>();
            replacers.put("%amount%", String.valueOf(amount));
            replacers.put("%target%", target.getName());
            replacers.put("%type%", type);
            MessageUtils.sendMessage(sender, "sender", true, replacers);
        }

        Map<String, String> targetReplacers = new HashMap<>();
        targetReplacers.put("%player%", sender.getName());
        targetReplacers.put("%amount%", String.valueOf(amount));
        targetReplacers.put("%type%", type);
        MessageUtils.sendMessage(target, "receiver", true, targetReplacers);
    }

    public static void handleAllSending(Player sender, int amount) {
        List<Player> targets = new ArrayList<>(Bukkit.getOnlinePlayers());
        targets.remove(sender);

        if (targets.isEmpty()) {
            MessageUtils.sendMessage(sender, "no_players", true, null);
            return;
        }

        int mode = instance.getConfig().getInt("claimblocks_type");
        int totalAmount = amount * targets.size();
        String type = getTypeName(mode);

        PlayerData senderData = GriefPrevention.instance.dataStore.getPlayerData(sender.getUniqueId());

        if (!hasEnough(senderData, totalAmount, mode)) {
            int shortfall = totalAmount - getBalance(senderData, mode);
            Map<String, String> replacers = new HashMap<>();
            replacers.put("%type%", type);
            replacers.put("%need%", String.valueOf(shortfall));
            MessageUtils.sendMessage(sender, "no_enough_blocks", true, replacers);
            return;
        }

        int actualTotal = 0;
        for (Player target : targets) {
            if (!target.isOnline()) continue;
            handleSending(sender, target.getName(), amount, false);
            actualTotal += amount;
        }

        if (instance.getConfig().getBoolean("broadcast_on_sendall")) {
            Map<String, String> replacers = new HashMap<>();
            replacers.put("%player%", sender.getName());
            replacers.put("%type%", type);
            replacers.put("%amount%", String.valueOf(amount));
            replacers.put("%total%", String.valueOf(actualTotal));
            String message = MessageUtils.getProcessedMessage(sender, "broadcast_message", true, replacers);
            Bukkit.broadcastMessage(message);
        }
    }


    /**
     * Deducts {@code amount} claim blocks from {@code senderData} and credits
     * them to {@code targetData} according to the active mode.
     * Both PlayerData objects are mutated in place + caller is responsible for saving
     *
     * @param senderData GP PlayerData of the sender   (mutated)
     * @param targetData GP PlayerData of the receiver (mutated)
     * @param amount     number of blocks to transfer  (must be > 0)
     * @param mode       claimblocks_type value (0–4)
     */
    private static void deductCB(PlayerData senderData, PlayerData targetData, int amount, int mode) {
        switch (mode) {

            case 0: {
                // 0 is stupid mode. doesn't make sense. shouldnt be used

                // total: drain accrued first bonus covers rest
                int sBonus    = senderData.getBonusClaimBlocks();
                int sAccrued  = senderData.getAccruedClaimBlocks();
                int sNewTotal = (sBonus + sAccrued) - amount;

                if (sNewTotal >= sBonus) {
                    senderData.setAccruedClaimBlocks(sNewTotal - sBonus);
                    senderData.setBonusClaimBlocks(sBonus);
                } else {
                    senderData.setAccruedClaimBlocks(0);
                    senderData.setBonusClaimBlocks(sNewTotal);
                }

                // credit target: top up accrued, bonus unchanged
                int tBonus    = targetData.getBonusClaimBlocks();
                int tAccrued  = targetData.getAccruedClaimBlocks();
                int tNewTotal = (tBonus + tAccrued) + amount;

                if (tNewTotal >= tBonus) {
                    targetData.setAccruedClaimBlocks(tNewTotal - tBonus);
                    targetData.setBonusClaimBlocks(tBonus);
                } else {
                    targetData.setAccruedClaimBlocks(0);
                    targetData.setBonusClaimBlocks(tNewTotal);
                }
                break;
            }

            case 1:
            case 4: {
                // bonus only (mode 4 uses a stricter balance check but same deduction)
                senderData.setBonusClaimBlocks(senderData.getBonusClaimBlocks() - amount);
                targetData.setBonusClaimBlocks(targetData.getBonusClaimBlocks() + amount);
                break;
            }

            case 2: {
                // accrued only
                senderData.setAccruedClaimBlocks(senderData.getAccruedClaimBlocks() - amount);
                targetData.setAccruedClaimBlocks(targetData.getAccruedClaimBlocks() + amount);
                break;
            }

            case 3: {
                // deplete bonus first, then accrued
                int bonus        = senderData.getBonusClaimBlocks();
                int bonusToUse   = Math.min(bonus, amount);
                int accruedToUse = amount - bonusToUse;

                senderData.setBonusClaimBlocks(bonus - bonusToUse);
                senderData.setAccruedClaimBlocks(senderData.getAccruedClaimBlocks() - accruedToUse);
                targetData.setBonusClaimBlocks(targetData.getBonusClaimBlocks() + amount);
                break;
            }
        }
    }


    /** Returns true if {@code data} has at least {@code amount} blocks for the given mode. */
    private static boolean hasEnough(PlayerData data, int amount, int mode) {
        return getBalance(data, mode) >= amount;
    }

    /** Returns the spendable balance for the given mode. */
    private static int getBalance(PlayerData data, int mode) {
        switch (mode) {
            case 0:  return data.getBonusClaimBlocks() + data.getAccruedClaimBlocks();
            case 1:  return data.getBonusClaimBlocks();
            case 2:  return data.getAccruedClaimBlocks();
            case 3:  return data.getRemainingClaimBlocks();
            case 4:  return Math.min(data.getRemainingClaimBlocks(), data.getBonusClaimBlocks());
            default: return 0;
        }
    }

    /** Returns the display name for a mode, used in messages. */
    private static String getTypeName(int mode) {
        switch (mode) {
            case 0:  return "total";
            case 1:  return "bonus";
            case 2:  return "accrued";
            case 3:  return "remaining";
            case 4:  return "remaining-bonus";
            default: return "*";
        }
    }

    /** Saves both PlayerData objects and logs any failure clearly. */
    private static void saveData(Player sender, PlayerData senderData, Player target, PlayerData targetData) {
        try {
            GriefPrevention.instance.dataStore.savePlayerDataSync(sender.getUniqueId(), senderData);
            GriefPrevention.instance.dataStore.savePlayerDataSync(target.getUniqueId(), targetData);
        } catch (Exception e) {
            instance.getLogger().severe("Failed to save claim block data for "
                    + sender.getName() + " -> " + target.getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}