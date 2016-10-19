package net.kaikk.mc.betterkits.bukkit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

public class BetterKits extends JavaPlugin {
	private static BetterKits instance;
	private Config config;
	private Map<String, Kit> kits;
	private Map<UUID, PlayerData> playersData;
	
	@Override
	public void onEnable() {
		instance=this;
		
		ConfigurationSerialization.registerClass(Kit.class);
		ConfigurationSerialization.registerClass(PlayerData.class);

		config = new Config(instance);
		this.loadData();
		Messages.load(this, "messages.yml");
		
		this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
		
		// commands executor
		CommandExec ce = new CommandExec(this);
		this.getDescription().getCommands().keySet().forEach(cmd -> this.getCommand(cmd).setExecutor(ce));
	}
	
	public static BetterKits instance() {
		return instance;
	}
	
	public Config config() {
		return config;
	}
	
	public Kit getKit(String name) {
		return this.kits.get(name.toLowerCase());
	}
	
	public Kit addKit(Kit kit) {
		return this.kits.put(kit.getName().toLowerCase(), kit);
	}
	
	public Kit removeKit(String name) {
		return this.kits.remove(name.toLowerCase());
	}
	
	public Collection<Kit> getKits() {
		return Collections.unmodifiableCollection(this.kits.values());
	}
	
	public Map<UUID, PlayerData> getPlayersData() {
		return playersData;
	}

	@SuppressWarnings("unchecked")
	synchronized public void loadData() {
		FileConfiguration data = YamlConfiguration.loadConfiguration(new File(instance.getDataFolder(), "data.yml"));
		if (data==null) {
			instance.getLogger().severe("There was an error while loading data.yml!");
		} else {
			List<Kit> kitsList = (List<Kit>) data.getList("Kits", Collections.emptyList());
			this.kits = new HashMap<String, Kit>(); 
			for (Kit kit : kitsList) {
				this.kits.put(kit.getName().toLowerCase(), kit);
			}

			List<PlayerData> playersDataList = (List<PlayerData>) data.getList("PlayersData", Collections.emptyList());
			this.playersData = new HashMap<UUID, PlayerData>(); 
			for (PlayerData pd : playersDataList) {
				this.playersData.put(pd.getPlayerUUID(), pd);
			}
		}
	}
	
	synchronized public void saveData() {
		File file = new File(this.getDataFolder(), "data.yml");
		FileConfiguration data = YamlConfiguration.loadConfiguration(file);
		if (data==null) {
			this.getLogger().severe("There was an error while saving data.yml!");
		} else {
			data.set("Kits", new ArrayList<Kit>(this.kits.values()));
			data.set("PlayersData", new ArrayList<PlayerData>(this.playersData.values()));
			try {
				data.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
