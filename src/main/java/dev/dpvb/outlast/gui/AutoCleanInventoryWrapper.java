package dev.dpvb.outlast.gui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryCloseEvent;

public abstract class AutoCleanInventoryWrapper extends InventoryWrapper {

    @Override
    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent event) {
        super.onInventoryCloseEvent(event);
        if (event.getInventory() == inventory) {
            HandlerList.unregisterAll(this);
        }
    }

}
