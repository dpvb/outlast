package dev.dpvb.outlast.gui;

import dev.dpvb.outlast.internal.OutlastPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public abstract class InventoryWrapper implements Listener {

    protected Inventory inventory;

    /**
     * This method is called once using {@link InventoryWrapper#getInventory()}
     * The method should be used to create the Inventory contents.
     * @return The {@link Inventory} to represent this wrapper.
     */
    protected abstract Inventory generateInventory();

    /**
     * Define custom behavior to call during InventoryClickEvent.
     *
     * @param event an {@link InventoryClickEvent} of this {@link Inventory}
     * @implNote This method is defined as no-op by default.
     */
    public void handle(InventoryClickEvent event) {} // no-op

    /**
     * Define custom behavior to call during InventoryCloseEvent.
     *
     * @param event an {@link InventoryCloseEvent} of this {@link Inventory}
     * @implNote This method is defined as no-op by default.
     */
    public void handle(InventoryCloseEvent event) {} // no-op

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        if (event.getInventory() == inventory) {
            handle(event);
        }
    }

    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent event) {
        if (event.getInventory() == inventory) {
            handle(event);
        }
    }

    /**
     * Register the Listener for this inventory Wrapper. You should call this when you create a new instance
     * of this class.
     * @return itself
     */
    public InventoryWrapper register() {
        Bukkit.getPluginManager().registerEvents(this, OutlastPlugin.getPlugin(OutlastPlugin.class));
        return this;
    }

    /**
     * Creates a new inventory with {@link InventoryWrapper#generateInventory()} if it doesn't already exist.
     * @return The {@link Inventory}
     */
    public Inventory getInventory() {
        if (inventory == null) {
            inventory = generateInventory();
        }

        return inventory;
    }

    /**
     * Unregister all the Listeners associated to this instance.
     */
    public void unregister() {
        HandlerList.unregisterAll(this);
    }

}
