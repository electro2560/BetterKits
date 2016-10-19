package net.kaikk.mc.betterkits.bukkit;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public class Config {
	public Set<Material> allowedChests = new HashSet<Material>();
	public String kitTitleFormat, starterKitName;
	
	public Config(JavaPlugin instance) {
		instance.saveDefaultConfig();
		instance.reloadConfig();
		
		for (String s : instance.getConfig().getStringList("AllowedChests")) {
			Material m = Material.getMaterial(s);
			if (m == null) {
				instance.getLogger().warning("Invalid material "+s);
			} else {
				allowedChests.add(m);
			}
		}
		
		kitTitleFormat = instance.getConfig().getString("KitTitleFormat", "&4%name");
		starterKitName = instance.getConfig().getString("StarterKit", "starter");
	}
}
