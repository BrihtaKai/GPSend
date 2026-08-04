package driven.by.data.gpsend.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseGUI implements InventoryHolder {

    protected final Player viewer;
    protected final int size;
    protected final String title;

    private final Map<Integer, GUIAction> actions = new HashMap<>();
    private Inventory inventory;
    private boolean built = false;

    /**
     * @param viewer the player this GUI is being built for
     * @param size   inventory size; must be a multiple of 9 in [9, 54]
     * @param title  inventory title; apply {@link org.bukkit.ChatColor#translateAlternateColorCodes}
     *               yourself beforehand if you want '&amp;' color codes
     */
    protected BaseGUI(Player viewer, int size, String title) {
        if (size < 9 || size > 54 || size % 9 != 0) {
            throw new IllegalArgumentException("GUI size must be a multiple of 9 between 9 and 54, got " + size);
        }
        this.viewer = viewer;
        this.size = size;
        this.title = title;
    }

    /**
     * Populates the inventory: {@link #setItem} calls, item stacks, etc.
     * Called once, lazily, on first {@link #open()}, and again on every
     * {@link #refresh()}. Never called directly by this class's constructor.
     */
    protected abstract void build();

    @Override
    public final Inventory getInventory() {
        if (inventory == null) {
            inventory = Bukkit.createInventory(this, size, title);
        }
        return inventory;
    }

    /**
     * Builds the GUI on first use, then opens it for the viewer.
     */
    public final void open() {
        if (!built) {
            build();
            built = true;
        }
        viewer.openInventory(getInventory());
    }

    /**
     * Clears registered click actions, rebuilds the inventory contents, and pushes the update to the viewer.
     */
    public final void refresh() {
        actions.clear();
        build();
        built = true;
        viewer.updateInventory();
    }

    /**
     * Places an item with no click behavior.
     */
    protected void setItem(int slot, ItemStack item) {
        setItem(slot, item, null);
    }

    /**
     * Places an item and registers a click action for its slot. Passing a
     * {@code null} action clears any action already registered on the slot.
     */
    protected void setItem(int slot, ItemStack item, GUIAction action) {
        validateSlot(slot);
        getInventory().setItem(slot, item);
        if (action != null) {
            actions.put(slot, action);
        } else {
            actions.remove(slot);
        }
    }

    /**
     * Fills every currently empty slot with the given item, no click action attached.
     */
    protected void fill(ItemStack item) {
        for (int slot = 0; slot < size; slot++) {
            if (getInventory().getItem(slot) == null) {
                getInventory().setItem(slot, item);
            }
        }
    }

    /**
     * Removes both the item and any registered action from a slot.
     */
    protected void clearSlot(int slot) {
        validateSlot(slot);
        getInventory().setItem(slot, null);
        actions.remove(slot);
    }

    private void validateSlot(int slot) {
        if (slot < 0 || slot >= size) {
            throw new IndexOutOfBoundsException("Slot " + slot + " is out of bounds for GUI size " + size);
        }
    }

    /**
     * Looked up by {@code GUIListener} on click. Returns {@code null} if the
     * slot has no registered action.
     */
    public GUIAction getAction(int slot) {
        return actions.get(slot);
    }

    public Player getViewer() {
        return viewer;
    }

    /**
     * Hook invoked by {@code GUIListener} on {@link InventoryCloseEvent}.
     * No-op by default; override for cleanup (cancelling tasks, persisting
     * state, etc.).
     */
    public void onClose(InventoryCloseEvent event) {
    }

    @FunctionalInterface
    public interface GUIAction {
        void execute(InventoryClickEvent event);
    }
}