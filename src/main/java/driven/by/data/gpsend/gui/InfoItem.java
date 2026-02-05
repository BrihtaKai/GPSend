package driven.by.data.gpsend.gui;

import driven.by.data.gpsend.GPSend;
import driven.by.data.gpsend.utils.ColorFormat;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class InfoItem {

    private InfoItem() {
    }

    public static ItemStack build(Player player) {
        GPSend instance = GPSend.getInstance();
        PlayerData data = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());

        int accrued = data.getAccruedClaimBlocks();
        int bonus = data.getBonusClaimBlocks();
        int total = accrued + bonus;
        int remaining = data.getRemainingClaimBlocks();

        int mode = instance.getConfig().getInt("claimblocks_type");
        int maxSendable;
        String modeLabel;

        switch (mode) {
            case 0:
                modeLabel = "Total only";
                maxSendable = total;
                break;
            case 1:
                modeLabel = "Bonus only";
                maxSendable = bonus;
                break;
            case 2:
                modeLabel = "Accrued only";
                maxSendable = accrued;
                break;
            case 3:
                modeLabel = "Remaining only";
                maxSendable = remaining;
                break;
            case 4:
                modeLabel = "Remaining (capped to Bonus)";
                maxSendable = Math.min(remaining, bonus);
                break;
            default:
                modeLabel = "unknown";
                maxSendable = 0;
                break;
        }

        ItemStack sign = new ItemStack(Material.OAK_SIGN);
        ItemMeta meta = sign.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ColorFormat.stringColorise("&#", "&eSendable Blocks"));
            List<String> lore = new ArrayList<>();
            lore.add(ColorFormat.stringColorise("&#", "&6Max sendable: &f" + maxSendable));
            lore.add(ColorFormat.stringColorise("&#", "&7Accrued: &f" + accrued));
            lore.add(ColorFormat.stringColorise("&#", "&7Bonus: &f" + bonus));
            lore.add(ColorFormat.stringColorise("&#", "&7Total: &f" + total));
            lore.add(ColorFormat.stringColorise("&#", "&7Remaining: &f" + remaining));
            lore.add(ColorFormat.stringColorise("&#", "&8Mode: " + modeLabel));
            meta.setLore(lore);
            sign.setItemMeta(meta);
        }

        return sign;
    }
}
