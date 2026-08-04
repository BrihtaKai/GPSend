package driven.by.data.gpsend.listener;

import driven.by.data.gpsend.gui.BaseGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

public class GUIInteract implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof BaseGUI gui)) {
            return;
        }
        event.setCancelled(true);
        BaseGUI.GUIAction action = gui.getAction(event.getSlot());
        if (action != null) {
            action.execute(event);
        }
    }


    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof BaseGUI gui)) {
            return;
        }
        gui.onClose(event);
    }
}