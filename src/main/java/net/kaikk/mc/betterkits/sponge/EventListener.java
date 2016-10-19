package net.kaikk.mc.betterkits.sponge;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;

public class EventListener {
	@Listener
	public void onInventoryClick(ClickInventoryEvent event) {
		if (event.getCause().containsNamed("BetterKits")) {
			event.setCancelled(true);
		}
	}
}
