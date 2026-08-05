package driven.by.data.gpsend.gui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link BaseGUI} that lays out a list of arbitrary items {@code T} across
 * pages, with prev/next navigation on the bottom row.
 *
 * <p>Subclasses only need to say how to turn one {@code T} into an
 * {@link ItemStack} ({@link #render}) and what a click on one should do
 * ({@link #onClick}) — paging, slot math, and the nav buttons are handled
 * here.
 *
 * @param <T> the type of item being paginated
 */
public abstract class PaginatedGUI<T> extends BaseGUI {

    private int page;

    protected PaginatedGUI(Player viewer, int size, String title) {
        super(viewer, size, title);
    }

    /**
     * Returns the items currently displayed.
     * Called every refresh so data can stay up-to-date.
     */
    protected abstract List<T> getItems();

    /**
     * Creates the ItemStack for one item.
     */
    protected abstract ItemStack render(T item);

    /**
     * Handles clicking one item.
     */
    protected abstract void onClick(T item, InventoryClickEvent event);

    /**
     * Called before items are rendered.
     */
    protected void beforeBuild() {}

    /**
     * Called after items + navigation are rendered.
     */
    protected void afterBuild() {}

    /**
     * Slots used for paginated content.
     */
    protected List<Integer> contentSlots() {
        List<Integer> slots = new ArrayList<>();

        for (int i = 0; i < size - 9; i++) {
            slots.add(i);
        }

        return slots;
    }

    /**
     * Previous page button slot.
     * Return -1 to disable.
     */
    protected int getPrevSlot() {
        return size - 9;
    }

    /**
     * Next page button slot.
     * Return -1 to disable.
     */
    protected int getNextSlot() {
        return size - 1;
    }

    protected ItemStack createPrevItem() {
        return createNavItem(Material.ARROW, "&ePrevious Page");
    }

    protected ItemStack createNextItem() {
        return createNavItem(Material.ARROW, "&eNext Page");
    }

    @Override
    protected final void build() {

        beforeBuild();

        List<T> items = getItems();
        List<Integer> slots = contentSlots();

        int perPage = slots.size();

        int maxPage = getMaxPages(false);
        page = Math.min(page, maxPage);

        int start = page * perPage;

        for (int i = 0; i < perPage; i++) {

            int slot = slots.get(i);
            int index = start + i;

            if (index >= items.size()) {
                clearSlot(slot);
                continue;
            }

            T item = items.get(index);

            setItem(
                    slot,
                    render(item),
                    event -> onClick(item, event)
            );
        }

        buildNavigation(items.size(), perPage);

        afterBuild();
    }

    private void buildNavigation(int totalItems, int perPage) {

        int prevSlot = getPrevSlot();

        if (prevSlot >= 0) {

            if (page > 0) {

                setItem(prevSlot, createPrevItem(), e -> {
                    page--;
                    refresh();
                });

            } else {

                clearSlot(prevSlot);

            }
        }

        int nextSlot = getNextSlot();

        if (nextSlot >= 0) {

            boolean hasNext = (page + 1) * perPage < totalItems;

            if (hasNext) {

                setItem(nextSlot, createNextItem(), e -> {
                    page++;
                    refresh();
                });

            } else {

                clearSlot(nextSlot);

            }
        }
    }

    protected ItemStack createNavItem(Material material, String name) {

        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        item.setItemMeta(meta);

        return item;
    }

    public int getPage() {
        return page;
    }

    public int getMaxPages(boolean p1) {
        List<T> items = getItems();
        List<Integer> slots = contentSlots();

        int perPage = slots.size();
        return p1
                ? Math.max(1, (items.size() + perPage - 1) / perPage)
                : Math.max(0, (items.size() - 1) / perPage);
    }

    public void setPage(int page) {
        this.page = Math.max(0, page);
    }
}
