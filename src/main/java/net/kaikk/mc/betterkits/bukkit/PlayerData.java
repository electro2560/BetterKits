package net.kaikk.mc.betterkits.bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class PlayerData extends net.kaikk.mc.betterkits.common.CommonPlayerData implements ConfigurationSerializable {
	public PlayerData(UUID playerUUID) {
		super(playerUUID);
	}
	
	public PlayerData(UUID playerUUID, Map<String, Integer> pendingKits, Map<String, Integer> cooldownKits) {
		super(playerUUID, pendingKits, cooldownKits);
	}
	
	public Map<String, Object> serialize() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		
		map.put("uuid", playerUUID.toString());
		
		List<String> list = new ArrayList<String>();
		for(Entry<String, Integer> entry : pendingKits.entrySet()) {
			list.add(entry.getValue()+","+entry.getKey());
		}
		map.put("pendingKits", list);
		
		list = new ArrayList<String>();
		for(Entry<String, Integer> entry : cooldownKits.entrySet()) {
			list.add(entry.getValue()+","+entry.getKey());
		}
		map.put("cooldownKits", list);
		
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public static PlayerData deserialize(Map<String, Object> map) {
		Map<String, Integer> pendingKits = new HashMap<String, Integer>();
		for (String s : ((List<String>) map.get("pendingKits"))) {
			String[] split = s.split(",", 2);
			pendingKits.put(split[1], Integer.valueOf(split[0]));
		}
		
		Map<String, Integer> cooldownKits = new HashMap<String, Integer>();
		for (String s : ((List<String>) map.get("cooldownKits"))) {
			String[] split = s.split(",", 2);
			cooldownKits.put(split[1], Integer.valueOf(split[0]));
		}
		
		return new PlayerData(UUID.fromString((String) map.get("uuid")), pendingKits, cooldownKits);
	}
}
