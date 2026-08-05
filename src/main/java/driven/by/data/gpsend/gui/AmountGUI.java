package driven.by.data.gpsend.gui;

import driven.by.data.gpsend.GPSend;
import driven.by.data.gpsend.utils.MessageUtils;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AmountGUI extends BaseGUI {

    public interface ConfirmAction {
        void execute(Player viewer, int amount);
    }

    private final GPSend instance = GPSend.getInstance();
    private final String mode;
    private int amount;
    private final ConfirmAction onConfirm;

    public AmountGUI(Player viewer, String mode, ConfirmAction onConfirm) {
        super(
                viewer,
                27,
                MessageUtils.getProcessedMessage(
                        viewer,
                        "gui.amount_gui.title",
                        true,
                        null
                )
        );
        this.mode = mode;
        this.amount = 0;
        this.onConfirm = onConfirm;
    }

    @Override
    protected void build() {
        setItem(
                instance.getConfig().getInt("gui.amount_gui.items.info.slot"),
                createInfoItem()
        );

        createButton(
                "gui.amount_gui.items.minus",
                0,
                () -> changeAmount(-getAmount("gui.amount_gui.amounts.minus1", 1000))
        );

        createButton(
                "gui.amount_gui.items.minus",
                1,
                () -> changeAmount(-getAmount("gui.amount_gui.amounts.minus2", 100))
        );

        createButton(
                "gui.amount_gui.items.minus",
                2,
                () -> changeAmount(-getAmount("gui.amount_gui.amounts.minus3", 10))
        );

        createButton(
                "gui.amount_gui.items.minus",
                3,
                () -> changeAmount(-getAmount("gui.amount_gui.amounts.minus4", 1))
        );

        createButton(
                "gui.amount_gui.items.plus",
                0,
                () -> changeAmount(getAmount("gui.amount_gui.amounts.plus1", 1))
        );

        createButton(
                "gui.amount_gui.items.plus",
                1,
                () -> changeAmount(getAmount("gui.amount_gui.amounts.plus2", 10))
        );

        createButton(
                "gui.amount_gui.items.plus",
                2,
                () -> changeAmount(getAmount("gui.amount_gui.amounts.plus3", 100))
        );

        createButton(
                "gui.amount_gui.items.plus",
                3,
                () -> changeAmount(getAmount("gui.amount_gui.amounts.plus4", 1000))
        );

        ItemStack confirm = new ItemStack(
                Material.valueOf(
                        instance.getConfig().getString(
                                "gui.amount_gui.items.confirm.material",
                                "LIME_WOOL"
                        )
                )
        );

        ItemMeta meta = confirm.getItemMeta();
        meta.setDisplayName(
                MessageUtils.getProcessedMessage(
                        viewer,
                        "gui.amount_gui.items.confirm.display_name",
                        true,
                        null
                )
        );

        int modelData = instance.getConfig().getInt(
                "gui.amount_gui.items.confirm.model_data"
        );
        if (modelData != 0) {
            meta.setCustomModelData(modelData);
        }

        confirm.setItemMeta(meta);

        setItem(
                instance.getConfig().getInt("gui.amount_gui.items.confirm.slot"),
                confirm,
                event -> confirm()
        );

        if (instance.getConfig().getBoolean("gui.info_item.amount_gui.display")) {
            setItem(
                    instance.getConfig().getInt("gui.info_item.amount_gui.slot"),
                    InfoItem.build(viewer)
            );
        }
    }

    private void changeAmount(int change) {
        amount += change;
        if (amount < 0) {
            amount = 0;
        }
        refresh();
    }

    private void confirm() {
        if (amount <= 0) {
            return;
        }

        viewer.closeInventory();
        viewer.playSound(
                viewer.getLocation(),
                Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                1.0f,
                1.0f
        );

        onConfirm.execute(viewer, amount);
    }

    private void createButton(
            String path,
            int index,
            Runnable action
    ) {
        Material material = Material.valueOf(
                instance.getConfig().getString(path + ".material")
        );

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(
                MessageUtils.getProcessedMessage(
                        viewer,
                        path + ".display_name",
                        true,
                        Map.of(
                                "%amount%",
                                String.valueOf(getButtonAmount(path, index))
                        )
                )
        );

        int modelData = instance.getConfig().getInt(path + ".model_data");
        if (modelData != 0) {
            meta.setCustomModelData(modelData);
        }

        item.setItemMeta(meta);

        List<Integer> slots = instance.getConfig().getIntegerList(path + ".slots");

        setItem(
                slots.get(index),
                item,
                event -> action.run()
        );
    }

    private ItemStack createInfoItem() {
        Material material = Material.valueOf(
                instance.getConfig().getString(
                        "gui.amount_gui.items.info.material",
                        "PAPER"
                )
        );

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(
                MessageUtils.getProcessedMessage(
                        viewer,
                        "gui.amount_gui.items.info.display_name",
                        true,
                        Map.of("%mode%", mode)
                )
        );

        int modelData = instance.getConfig().getInt(
                "gui.amount_gui.items.info.model_data"
        );
        if (modelData != 0) {
            meta.setCustomModelData(modelData);
        }

        List<String> lore = new ArrayList<>();

        boolean allMode = ChatColor.stripColor(mode).equalsIgnoreCase(
                ChatColor.stripColor(
                        instance.getConfig().getString(
                                "gui.amount_gui.items.info.all_mode_name"
                        )
                )
        );

        List<String> configLore = instance.getConfig().getStringList(
                allMode
                        ? "gui.amount_gui.items.info.lore_all"
                        : "gui.amount_gui.items.info.lore_target"
        );

        for (String line : configLore) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("%amount%", String.valueOf(amount));
            placeholders.put("%affordable%", canSend(
                    allMode
                            ? amount * (Bukkit.getOnlinePlayers().size() - 1)
                            : amount
            ));

            if (allMode) {
                placeholders.put(
                        "%total%",
                        String.valueOf(amount * (Bukkit.getOnlinePlayers().size() - 1))
                );
                placeholders.put(
                        "%affordable_single%",
                        canSend(amount)
                );
            }

            lore.add(
                    MessageUtils.getProcessedMessage(
                            viewer,
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

    private String canSend(int amount) {
        if (amount <= 0) {
            return instance.getConfig().getString(
                    "gui.amount_gui.items.info.affordable_yes"
            );
        }

        PlayerData data =
                me.ryanhamshire.GriefPrevention.GriefPrevention.instance
                        .dataStore
                        .getPlayerData(viewer.getUniqueId());

        int playerAmount;

        switch (instance.getConfig().getInt("claimblocks_type")) {
            case 0:
                playerAmount = data.getAccruedClaimBlocks() + data.getBonusClaimBlocks();
                break;
            case 1:
                playerAmount = data.getBonusClaimBlocks();
                break;
            case 2:
                playerAmount = data.getAccruedClaimBlocks();
                break;
            case 3:
                playerAmount = data.getRemainingClaimBlocks();
                break;
            case 4:
                playerAmount = Math.min(
                        data.getRemainingClaimBlocks(),
                        data.getBonusClaimBlocks()
                );
                break;
            default:
                playerAmount = 0;
        }

        return instance.getConfig().getString(
                amount <= playerAmount
                        ? "gui.amount_gui.items.info.affordable_yes"
                        : "gui.amount_gui.items.info.affordable_no"
        );
    }

    private int getAmount(String path, int def) {
        return instance.getConfig().getInt(path, def);
    }

    private int getButtonAmount(String path, int index) {
        String type = path.endsWith("plus") ? "plus" : "minus";
        return getAmount(
                "gui.amount_gui.amounts." + type + (index + 1),
                1
        );
    }
}