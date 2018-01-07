package net.kaikk.mc.betterkits.sponge;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;

public class EventListener {
	private BetterKits instance;
	
	public EventListener(BetterKits instance) {
		this.instance = instance;
	}
	
	@Listener
	public void onPlayerJoin(ClientConnectionEvent.Join event) {
		if (System.currentTimeMillis()-event.getTargetEntity().getJoinData().firstPlayed().get().toEpochMilli()<1000) {
			Kit kit = instance.getKit(instance.config().starterKitName);
			if (kit != null) {
				instance.logger().warn("Giving default kit "+instance.config().starterKitName+" to "+event.getTargetEntity().getName());
				kit.give(event.getTargetEntity());
			}
		}
	}
}
