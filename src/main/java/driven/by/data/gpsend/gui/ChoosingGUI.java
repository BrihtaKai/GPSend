package driven.by.data.gpsend.gui;

import driven.by.data.gpsend.GPSend;
import driven.by.data.gpsend.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ChoosingGUI extends BaseGUI {

    private final GPSend instance = GPSend.getInstance();

    public ChoosingGUI(Player viewer) {
        super(
                viewer,
                27,
                MessageUtils.getProcessedMessage(
                        viewer,
                        "gui.choosing_gui.title",
                        true,
                        null
                )
        );
    }

    @Override
    protected void build() {
        boolean hasAllPerm = viewer.hasPermission("gpsend.sendall") || viewer.isOp();

        int playerSlot = instance.getConfig().getInt("gui.choosing_gui.items.player.slot");
        int allSlot = instance.getConfig().getInt("gui.choosing_gui.items.all.slot");

        if (hasAllPerm) {
            setItem(
                    playerSlot,
                    createItem("gui.choosing_gui.items.player"),
                    event -> new PlayerListGUI(viewer).open()
            );

            setItem(
                    allSlot,
                    createItem("gui.choosing_gui.items.all"),
                    event -> new AmountGUI(
                            viewer,
                            instance.getConfig().getString("gui.amount_gui.items.info.all_mode_name")
                    ).open()
            );
        } else {
            // Keep the original behavior of centering the player button
            setItem(
                    13,
                    createItem("gui.choosing_gui.items.player"),
                    event -> new PlayerListGUI(viewer).open()
            );
        }

        if (instance.getConfig().getBoolean("gui.info_item.choosing_gui.display")) {
            setItem(
                    instance.getConfig().getInt("gui.info_item.choosing_gui.slot"),
                    InfoItem.build(viewer)
            );
        }
    }

    private ItemStack createItem(String path) {
        Material material = Material.valueOf(
                instance.getConfig().getString(path + ".material", "STONE")
        );

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(
                MessageUtils.getProcessedMessage(
                        viewer,
                        path + ".display_name",
                        true,
                        null
                )
        );

        int modelData = instance.getConfig().getInt(path + ".model_data");
        if (modelData != 0) {
            meta.setCustomModelData(modelData);
        }

        List<String> lore = new ArrayList<>();
        for (String line : instance.getConfig().getStringList(path + ".lore")) {
            lore.add(
                    MessageUtils.getProcessedMessage(
                            viewer,
                            line,
                            false,
                            null
                    )
            );
        }

        if (!lore.isEmpty()) {
            meta.setLore(MessageUtils.listColorise("&#", lore));
        }

        item.setItemMeta(meta);
        return item;
    }
}