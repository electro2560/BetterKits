package net.kaikk.mc.betterkits.common;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommonPlayerData {
	protected UUID playerUUID;
	protected Map<String, Integer> pendingKits;
	protected Map<String, Integer> cooldownKits;
	
	public CommonPlayerData(UUID playerUUID) {
		this(playerUUID, new HashMap<String, Integer>(), new HashMap<String, Integer>());
	}
	
	public CommonPlayerData(UUID playerUUID, Map<String, Integer> pendingKits, Map<String, Integer> cooldownKits) {
		this.playerUUID = playerUUID;
		this.pendingKits = pendingKits;
		this.cooldownKits = cooldownKits;
	}

	public UUID getPlayerUUID() {
		return playerUUID;
	}

	public Map<String, Integer> getPendingKits() {
		return pendingKits;
	}

	public Map<String, Integer> getCooldownKits() {
		return cooldownKits;
	}
}
