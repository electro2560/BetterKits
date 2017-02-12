package net.kaikk.mc.betterkits.bukkit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import net.kaikk.mc.betterkits.common.CommonKit;

@SerializableAs("Kit")
public class Kit extends CommonKit implements InventoryHolder, ConfigurationSerializable {
	private Inventory cachedInventory;
	private ItemStack[] cachedContents;
	private String cachedTitle;

	public Kit(String name, String world, int x, int y, int z, int cooldown, List<String> commands) {
		super(name, world, x, y, z, cooldown, commands);
	}

	public Kit(String name, Block block) {
		super(name, block.getWorld().getName(), block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ(), 0, Collections.emptyList());
	}

	public Block getBlock() {
		return Bukkit.getWorld(this.world).getBlockAt(x, y, z);
	}

	public Inventory getChestInventory() {
		return ((InventoryHolder) this.getBlock().getState()).getInventory();
	}

	public void give(Player player) {
		Map<Integer,ItemStack> map = player.getInventory().addItem(this.getContents());
		for (ItemStack is : map.values()) {
			player.getWorld().dropItem(player.getLocation(), is);
		}

		for (String cmd : this.getCommands()) {
			try {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%name", player.getName()).replace("%uuid", player.getUniqueId().toString()));
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	public void refill(InventoryHolder inventoryHolder) {
		inventoryHolder.getInventory().clear();
		inventoryHolder.getInventory().addItem(this.getContents());
	}

	public void openKitPreview(Player player) {
		player.openInventory(this.getInventory());
	}

	public ItemStack[] getContents() {
		if (cachedContents == null) {
			List<ItemStack> itemStacks = new ArrayList<ItemStack>(this.getChestInventory().getContents().length);
			for (ItemStack is : this.getChestInventory().getContents()) {
				if (is != null) {
					itemStacks.add(is);
				}
			}
			cachedContents = itemStacks.toArray(new ItemStack[itemStacks.size()]);
		}
		return cachedContents;
	}

	@Override
	public Inventory getInventory() {
		if (cachedInventory == null) {
			cachedInventory = Bukkit.createInventory(this, this.shortestInventorySize(), this.getCachedTitle());
			cachedInventory.addItem(this.getContents());
		}
		return cachedInventory;
	}

	public String getCachedTitle() {
		if (this.cachedTitle == null) {
			this.cachedTitle = ChatColor.translateAlternateColorCodes('&', BetterKits.instance().config().kitTitleFormat.replace("%name", this.getName()));
			if (this.cachedTitle.length() > 32) {
				this.cachedTitle = this.cachedTitle.substring(0, 32);
			}
		}
		return this.cachedTitle;
	}

	public int shortestInventorySize() {
		return this.getContents().length % 9 == 0 ? this.getContents().length : (((int)(this.getContents().length / 9)) * 9) + 9;
	}

	public void clearCache() {
		cachedInventory = null;
		cachedContents = null;
		cachedTitle = null;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("name", this.name);
		map.put("world", this.world);
		map.put("x", this.x);
		map.put("y", this.y);
		map.put("z", this.z);
		map.put("cooldown", this.cooldown);
		map.put("commands", this.commands);
		return map;
	}

	@SuppressWarnings("unchecked")
	public static Kit deserialize(Map<String, Object> map) {
		return new Kit((String)map.get("name"), (String)map.get("world"), (int)map.get("x"), (int)map.get("y"), (int)map.get("z"), (int)map.get("cooldown"), (List<String>)map.get("commands"));
	}
}
