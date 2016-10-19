package net.kaikk.mc.betterkits.bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class EventListener implements Listener {
    @EventHandler(ignoreCancelled=true)
    public void onInventoryClick(InventoryClickEvent event) {
    	if (event.getInventory().getHolder() instanceof Kit) {
    		event.setCancelled(true);
    	}
    }
}
