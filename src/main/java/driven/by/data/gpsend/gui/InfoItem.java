package driven.by.data.gpsend.gui;

import driven.by.data.gpsend.GPSend;
import driven.by.data.gpsend.utils.MessageUtils;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfoItem {

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
                modeLabel = instance.getConfig().getString("gui.info_item.modes.total");
                maxSendable = total;
                break;

            case 1:
                modeLabel = instance.getConfig().getString("gui.info_item.modes.bonus");
                maxSendable = bonus;
                break;

            case 2:
                modeLabel = instance.getConfig().getString("gui.info_item.modes.accrued");
                maxSendable = accrued;
                break;

            case 3:
                modeLabel = instance.getConfig().getString("gui.info_item.modes.remaining");
                maxSendable = remaining;
                break;

            case 4:
                modeLabel = instance.getConfig().getString("gui.info_item.modes.remaining_bonus");
                maxSendable = Math.min(remaining, bonus);
                break;

            default:
                modeLabel = "Unknown";
                maxSendable = 0;
                break;
        }

        Material material = Material.valueOf(
                instance.getConfig().getString(
                        "gui.info_item.material",
                        "OAK_SIGN"
                )
        );

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return item;
        }

        meta.setDisplayName(
                MessageUtils.getProcessedMessage(
                        player,
                        "gui.info_item.display_name",
                        true,
                        null
                )
        );

        int modelData = instance.getConfig().getInt("gui.info_item.model_data");
        if (modelData != 0) {
            meta.setCustomModelData(modelData);
        }

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("%max_sendable%", String.valueOf(maxSendable));
        placeholders.put("%accrued%", String.valueOf(accrued));
        placeholders.put("%bonus%", String.valueOf(bonus));
        placeholders.put("%total%", String.valueOf(total));
        placeholders.put("%remaining%", String.valueOf(remaining));
        placeholders.put("%mode%", modeLabel);

        List<String> lore = new ArrayList<>();

        for (String line : instance.getConfig().getStringList("gui.info_item.lore")) {
            lore.add(
                    MessageUtils.getProcessedMessage(
                            player,
                            line,
                            false,
                            placeholders
                    )
            );
        }

        meta.setLore(MessageUtils.listColorise("&#", lore));
        item.setItemMeta(meta);

        return item;
    }
}