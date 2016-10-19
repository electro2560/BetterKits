package net.kaikk.mc.betterkits.bukkit;

import java.util.Collections;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;

public class Utils {
	public static boolean isFakePlayer(Player player) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if(player==p) {
				return false;
			}
		}
		return true;
	}
	
	public static String locationToString(Location location) {
		return "[" + location.getWorld().getName() + ", " + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ() + "]";
	}
	
	public static Player getOnlinePlayer() {
		Player player = null;
		for (Player p : Bukkit.getOnlinePlayers()) {
			player = p;
			break;
		}
		return player;
	}
	
	public static void safeTeleportTask(final Player player, final Location targetLocation) {
		final Chunk chunk = targetLocation.getChunk();
		new BukkitRunnable() {
			@Override
			public void run() {
				if (chunk.isLoaded()) {
					this.cancel();
					
					targetLocation.getBlock().setType(Material.AIR);
					targetLocation.getBlock().getRelative(BlockFace.UP).setType(Material.AIR);
					targetLocation.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.UP).setType(Material.AIR);
					
					player.teleport(targetLocation.add(0, 1, 0));
				}
				chunk.load(true);
			}
		}.runTaskTimer(BetterKits.instance(), 0L, 1L);
	}
	
	public static Block getTargetBlock(Player player, int maxDistance) {
		return getTargetBlock(player, maxDistance, Collections.emptySet());
	}
	
	public static Block getTargetBlock(Player player, int maxDistance, Set<Material> transparentMaterials) {
		final BlockIterator iterator = new BlockIterator(player.getLocation(), player.getEyeHeight(), maxDistance);
		Block result = player.getLocation().getBlock().getRelative(BlockFace.UP);
		while (iterator.hasNext()) {
			result = iterator.next();
			if (result.getType() != Material.AIR && !transparentMaterials.contains(result.getType())) {
				return result;
			}
		}

		return result;
	}
}
