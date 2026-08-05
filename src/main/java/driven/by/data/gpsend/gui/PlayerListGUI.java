package driven.by.data.gpsend.gui;

import driven.by.data.gpsend.GPSend;
import driven.by.data.gpsend.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayerListGUI extends PaginatedGUI<Player> {

    public interface Selector {
        void onSelect(Player viewer, Player target);
    }

    private final GPSend instance = GPSend.getInstance();
    private final Selector selector;

    public PlayerListGUI(Player viewer, Selector selector) {
        super(
                viewer,
                54,
                MessageUtils.getProcessedMessage(
                        viewer,
                        "gui.player_list_gui.title",
                        true,
                        null
                )
        );
        this.selector = selector;
    }

    @Override
    protected List<Player> getItems() {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        players.remove(viewer);
        return players;
    }

    @Override
    protected ItemStack render(Player player) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);

        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta == null) {
            return item;
        }

        meta.setOwningPlayer(player);
        meta.setDisplayName(
                MessageUtils.getProcessedMessage(
                        viewer,
                        "gui.player_list_gui.items.player_head.display_name",
                        true,
                        Map.of("%player%", player.getName())
                )
        );

        item.setItemMeta(meta);
        return item;
    }

    @Override
    protected void onClick(Player player, InventoryClickEvent event) {
        selector.onSelect(viewer, player);
    }

    @Override
    protected void beforeBuild() {
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);

        ItemMeta meta = glass.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            glass.setItemMeta(meta);
        }

        for (int slot = 36; slot <= 44; slot++) {
            setItem(slot, glass);
        }
    }

    @Override
    protected void afterBuild() {
        ItemStack page = new ItemStack(
                Material.valueOf(
                        instance.getConfig().getString(
                                "gui.player_list_gui.items.page_info.material",
                                "PAPER"
                        )
                )
        );

        ItemMeta meta = page.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(
                    MessageUtils.getProcessedMessage(
                            viewer,
                            "gui.player_list_gui.items.page_info.display_name",
                            true,
                            Map.of(
                                    "%page%", String.valueOf(getPage() + 1),
                                    "%max%", String.valueOf(getMaxPages(true))
                            )
                    )
            );

            int modelData = instance.getConfig().getInt(
                    "gui.player_list_gui.items.page_info.model_data"
            );
            if (modelData != 0) {
                meta.setCustomModelData(modelData);
            }

            page.setItemMeta(meta);
        }

        setItem(
                instance.getConfig().getInt("gui.player_list_gui.items.page_info.slot"),
                page
        );

        if (instance.getConfig().getBoolean("gui.info_item.player_list_gui.display")) {
            setItem(
                    instance.getConfig().getInt("gui.info_item.player_list_gui.slot"),
                    InfoItem.build(viewer)
            );
        }
    }

    @Override
    protected List<Integer> contentSlots() {
        List<Integer> slots = new ArrayList<>();

        for (int i = 0; i < 36; i++) {
            slots.add(i);
        }

        return slots;
    }

    @Override
    protected int getPrevSlot() {
        return instance.getConfig().getInt(
                "gui.player_list_gui.items.prev_page.slot"
        );
    }

    @Override
    protected int getNextSlot() {
        return instance.getConfig().getInt(
                "gui.player_list_gui.items.next_page.slot"
        );
    }

    @Override
    protected ItemStack createPrevItem() {
        ItemStack item = new ItemStack(
                Material.valueOf(
                        instance.getConfig().getString(
                                "gui.player_list_gui.items.prev_page.material",
                                "ARROW"
                        )
                )
        );

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(
                    MessageUtils.getProcessedMessage(
                            viewer,
                            "gui.player_list_gui.items.prev_page.display_name",
                            true,
                            null
                    )
            );

            int modelData = instance.getConfig().getInt(
                    "gui.player_list_gui.items.prev_page.model_data"
            );
            if (modelData != 0) {
                meta.setCustomModelData(modelData);
            }

            item.setItemMeta(meta);
        }

        return item;
    }

    @Override
    protected ItemStack createNextItem() {
        ItemStack item = new ItemStack(
                Material.valueOf(
                        instance.getConfig().getString(
                                "gui.player_list_gui.items.next_page.material",
                                "ARROW"
                        )
                )
        );

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(
                    MessageUtils.getProcessedMessage(
                            viewer,
                            "gui.player_list_gui.items.next_page.display_name",
                            true,
                            null
                    )
            );

            int modelData = instance.getConfig().getInt(
                    "gui.player_list_gui.items.next_page.model_data"
            );
            if (modelData != 0) {
                meta.setCustomModelData(modelData);
            }

            item.setItemMeta(meta);
        }

        return item;
    }
}