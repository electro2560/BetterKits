package net.kaikk.mc.betterkits.sponge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.google.common.reflect.TypeToken;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;


public class PlayerData extends net.kaikk.mc.betterkits.common.CommonPlayerData  {
	public PlayerData(UUID playerUUID) {
		super(playerUUID);
	}
	
	public PlayerData(UUID playerUUID, Map<String, Integer> pendingKits, Map<String, Integer> cooldownKits) {
		super(playerUUID, pendingKits, cooldownKits);
	}
	
	public static class PlayerDataSerializer implements TypeSerializer<PlayerData> {
		@SuppressWarnings("serial")
		final public static TypeToken<List<PlayerData>> token = new TypeToken<List<PlayerData>>() {};
		
		@Override
		public PlayerData deserialize(TypeToken<?> token, ConfigurationNode node) throws ObjectMappingException {
			Map<String, Integer> pendingKits = new HashMap<String, Integer>();
			for (String s : node.getNode("pendingKits").getList(TypeToken.of(String.class))) {
				String[] split = s.split(",", 2);
				pendingKits.put(split[1], Integer.valueOf(split[0]));
			}
			Map<String, Integer> cooldownKits = new HashMap<String, Integer>();
			for (String s : node.getNode("cooldownKits").getList(TypeToken.of(String.class))) {
				String[] split = s.split(",", 2);
				cooldownKits.put(split[1], Integer.valueOf(split[0]));
			}
			
			return new PlayerData(UUID.fromString(node.getNode("uuid").getString()), pendingKits, cooldownKits);
		}

		@Override
		public void serialize(TypeToken<?> token, PlayerData playerData, ConfigurationNode node) throws ObjectMappingException {
			node.getNode("uuid").setValue(playerData.playerUUID.toString());
			List<String> list = new ArrayList<String>();
			for(Entry<String, Integer> entry : playerData.pendingKits.entrySet()) {
				list.add(entry.getValue()+","+entry.getKey());
			}
			node.getNode("pendingKits").setValue(list);
			
			list = new ArrayList<String>();
			for(Entry<String, Integer> entry : playerData.cooldownKits.entrySet()) {
				list.add(entry.getValue()+","+entry.getKey());
			}
			node.getNode("cooldownKits").setValue(list);
		}
	}
}
