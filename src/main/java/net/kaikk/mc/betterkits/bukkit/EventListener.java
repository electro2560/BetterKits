package net.kaikk.mc.betterkits.bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class EventListener implements Listener {
	private BetterKits instance;
	
	public EventListener(BetterKits instance) {
		this.instance = instance;
	}

    @EventHandler(ignoreCancelled=true)
    public void onInventoryClick(InventoryClickEvent event) {
    	if (event.getInventory().getHolder() instanceof Kit) {
    		event.setCancelled(true);
    	}
    }
    
    @EventHandler(ignoreCancelled=true)
    public void onPlayerJoin(PlayerJoinEvent event) {
    	if (!event.getPlayer().hasPlayedBefore()) {
    		Kit kit = instance.getKit(instance.config().starterKitName);
    		if (kit != null) {
    			kit.give(event.getPlayer());
    		}
    	}
    }
}
