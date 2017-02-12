package net.kaikk.mc.betterkits.bukkit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

import net.kaikk.mc.betterkits.common.CommonUtils;


public class CommandExec implements CommandExecutor {
	private BetterKits instance;
	private Set<Material> targetBlockFilter = new HashSet<Material>();
	private Set<UUID> alertedPlayers = new HashSet<UUID>();

	public CommandExec(BetterKits instance) {
		this.instance = instance;
		this.targetBlockFilter.add(Material.TORCH);
		this.targetBlockFilter.add(Material.WALL_SIGN);
		this.targetBlockFilter.add(Material.SIGN_POST);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		switch(cmd.getName()) {
		case "kitgive":
			return give(sender, label, args);
		case "kitclearcache":
			return clearCache(sender, label, args);
		case "kits":
			return kits(sender, label, args);
		case "kitrefill":
			return refill(sender, label, args);
		}

		if (!(sender instanceof Player)) {
			sender.sendMessage(Messages.get("OnlyPlayersCanRunThis"));
			return false;
		}

		Player player = (Player) sender;
		switch(cmd.getName()) {
		case "kit":
			return kit(player, label, args);
		case "kitpreview":
			return preview(player, label, args);
		case "kitcreate":
			return create(player, label, args);
		case "kitdelete":
			return delete(player, label, args);
		case "kitedit":
			return edit(player, label, args);
		case "kitreload":
			if (!player.hasPermission("betterkits.reload")) {
				player.sendMessage(Messages.get("PermissionDenied"));
				return false;
			}
			Bukkit.getPluginManager().disablePlugin(instance);
			Bukkit.getPluginManager().enablePlugin(instance);
			sender.sendMessage(ChatColor.GOLD + "BetterKits reloaded.");
			break;
		}

		return false;
	}

	public boolean give(CommandSender sender, String label, String[] args) {
		if (!sender.hasPermission("betterkits.give")) {
			sender.sendMessage(Messages.get("PermissionDenied"));
			return false;
		}

		if (args.length<2) {
			sender.sendMessage("Usage: /"+label+" (Player) (KitName) [Amount]");
			return false;
		}

		Kit kit = instance.getKit(args[1]);
		if (kit == null) {
			sender.sendMessage(Messages.get("KitNotFound"));
			return false;
		}

		@SuppressWarnings("deprecation")
		OfflinePlayer oPlayer  = Bukkit.getOfflinePlayer(args[0]);
		if (!oPlayer.isOnline() && !oPlayer.hasPlayedBefore()) {
			sender.sendMessage(Messages.get("PlayerHasNeverPlayed"));
			return false;
		}

		PlayerData pd = instance.getPlayersData().get(oPlayer.getUniqueId());
		if (pd == null) {
			pd = new PlayerData(oPlayer.getUniqueId());
			instance.getPlayersData().put(oPlayer.getUniqueId(), pd);
		}

		final int amount;
		if (args.length==2) {
			amount = 1;
		} else {
			amount = Integer.valueOf(args[2]);
		}

		Integer current = pd.getPendingKits().get(kit.getName());
		if (current == null) {
			current = 0;
		}
		pd.getPendingKits().put(kit.getName(), amount+current);

		instance.saveData();
		sender.sendMessage(Messages.get("PlayerHasNow",
				"name", oPlayer.getName(),
				"amount", String.valueOf(amount+current),
				"kit", kit.getName()));
		return true;
	}

	public boolean create(Player player, String label, String[] args) {
		if (!player.hasPermission("betterkits.create")) {
			player.sendMessage(Messages.get("PermissionDenied"));
			return false;
		}

		if (args.length==0) {
			player.sendMessage(ChatColor.RED + "Usage: /"+label+" (KitName) - use while pointing at a chest");
			return false;
		}

		if (!args[0].matches("[a-zA-Z0-9_-]+")) {
			player.sendMessage(Messages.get("KitNameInvalid"));
			return false;
		}

		if (instance.getKit(args[0]) != null) {
			player.sendMessage(Messages.get("KitAlreadyExist"));
			return false;
		}

		Block block = Utils.getTargetBlock(player, 5, targetBlockFilter);
		if (block == null || !instance.config().allowedChests.contains(block.getType())) {
			player.sendMessage(Messages.get("TargetBlockNotAllowed"));
			return false;
		}

		instance.addKit(new Kit(args[0], block));
		instance.saveData();
		player.sendMessage(ChatColor.GREEN + "Kit created.");
		return true;
	}

	public boolean kit(Player player, String label, String[] args) {
		if (args.length==0) {
			player.sendMessage(Messages.get("SpecifyKitName"));
			return false;
		}

		final Kit kit = instance.getKit(args[0]);
		if (kit == null) {
			player.sendMessage(Messages.get("KitNotFound"));
			return false;
		}

		PlayerData pd = instance.getPlayersData().get(player.getUniqueId());
		if (pd == null) {
			pd = new PlayerData(player.getUniqueId());
		}

		final String lcName = kit.getName().toLowerCase();
		Integer pendingKitAmount = pd.getPendingKits().get(lcName);
		if ((pendingKitAmount == null || pendingKitAmount <= 0) && !player.hasPermission("betterkits.allkits") && !player.hasPermission("betterkits.kit."+lcName)) {
			player.sendMessage(Messages.get("NotAllowedToUseThisKit"));
			return false;
		}

		Integer time = pd.getCooldownKits().get(lcName);
		if (time != null && CommonUtils.epoch() - time < kit.getCooldown() && !player.hasPermission("betterkits.bypasscooldown.allkits") && !player.hasPermission("betterkits.bypasscooldown.kit."+lcName)) {
			player.sendMessage(Messages.get("WaitKitCooldown", "remaining", CommonUtils.timeToString(kit.getCooldown() - (CommonUtils.epoch() - time)), "total", CommonUtils.timeToString(kit.getCooldown())));
			return false;
		}

		int freeSlots = Utils.freeSlots(player.getInventory().getContents());
		int kitSlots = kit.getContents().length;
		if (freeSlots < kitSlots && !this.alertedPlayers.add(player.getUniqueId())) {
			player.sendMessage(Messages.get("KitItemsDropWarning", "kitname", kit.getName(), "kitslots", kitSlots+"", "freeslots", freeSlots+""));
			return false;
		}

		pd.getCooldownKits().put(lcName, CommonUtils.epoch());
		if (pendingKitAmount != null) {
			if (pendingKitAmount > 1) {
				pd.getPendingKits().put(lcName, pendingKitAmount-1);
			} else {
				pd.getPendingKits().remove(lcName);
			}
		}

		instance.getPlayersData().put(player.getUniqueId(), pd);
		instance.saveData();

		kit.give(player);

		player.sendMessage(Messages.get("YouReceivedKit", "name", kit.getName()));
		return true;
	}

	public boolean kits(CommandSender sender, String label, String[] args) {
		final List<Kit> kits;
		if (sender instanceof Player) {
			Player player = (Player) sender;
			final PlayerData pd = instance.getPlayersData().get(player.getUniqueId());
			if (args.length > 0 && args[0].equalsIgnoreCase("all")) {
				if (!player.hasPermission("betterkits.listall")) {
					player.sendMessage(Messages.get("PermissionDenied"));
					return false;
				}
				kits = new ArrayList<Kit>(instance.getKits());
			} else {
				if (!player.hasPermission("betterkits.list")) {
					player.sendMessage(Messages.get("PermissionDenied"));
					return false;
				}
				kits = new ArrayList<Kit>();
				for (Kit kit : instance.getKits()) {
					final String lcName = kit.getName().toLowerCase();
					if ((pd != null && pd.getPendingKits().get(lcName) != null && pd.getPendingKits().get(lcName) > 0) || player.hasPermission("betterkits.kit."+lcName)) {
						kits.add(kit);
					}
				}
			}
		} else {
			kits = new ArrayList<Kit>(instance.getKits());
		}


		if (kits.isEmpty()) {
			sender.sendMessage(Messages.get("NoKitsAvailable"));
		} else {
			StringBuilder sb = new StringBuilder(Messages.get("Kits"));
			for (Kit kit : kits) {
				sb.append(kit.getName());
				sb.append(", ");
			}
			sb.setLength(sb.length()-2);
			sender.sendMessage(sb.toString());
		}
		return true;
	}

	public boolean preview(Player player, String label, String[] args) {
		if (!player.hasPermission("betterkits.preview")) {
			player.sendMessage(Messages.get("PermissionDenied"));
			return false;
		}

		if (args.length==0) {
			player.sendMessage(Messages.get("SpecifyKitName"));
			return false;
		}

		final Kit kit = instance.getKit(args[0]);
		if (kit == null) {
			player.sendMessage(Messages.get("KitNotFound"));
			return false;
		}

		kit.openKitPreview(player);
		return true;
	}

	public boolean delete(Player player, String label, String[] args) {
		if (!player.hasPermission("betterkits.delete")) {
			player.sendMessage(Messages.get("PermissionDenied"));
			return false;
		}

		if (args.length==0) {
			player.sendMessage(Messages.get("SpecifyKitName"));
			return false;
		}

		if (instance.removeKit(args[0]) != null) {
			player.sendMessage(Messages.get("KitRemoved"));
			instance.saveData();
		} else {
			player.sendMessage(Messages.get("KitNotFound"));
		}
		return true;
	}

	public boolean edit(Player player, String label, String[] args) {
		if (!player.hasPermission("betterkits.edit")) {
			player.sendMessage(Messages.get("PermissionDenied"));
			return false;
		}

		if (args.length<2) {
			player.sendMessage(ChatColor.RED + "Usage: /kitedit [kitname] <content|cooldown|commandslist|addcommand|removecommand> <[cooldownSeconds]|[commandPosition]|[command..]>");
			return false;
		}

		final Kit kit = instance.getKit(args[0]);
		if (kit == null) {
			player.sendMessage(Messages.get("KitNotFound"));
			return false;
		}

		switch(args[1].toLowerCase()) {
		case "content": {
			player.openInventory(kit.getChestInventory());
			player.sendMessage(ChatColor.RED + "Use /kitclearcache after you complete all the changes.");
			return true;
		}
		case "cooldown": {
			if (args.length == 2) {
				player.sendMessage(ChatColor.GREEN + "Kit "+kit.getName()+" cooldown is "+kit.getCooldown()+" seconds.");
			} else {
				kit.setCooldown(Integer.valueOf(args[2]));
				player.sendMessage(ChatColor.GREEN + "Kit "+kit.getName()+" cooldown set to "+kit.getCooldown()+" seconds.");
				instance.saveData();
			}
			return true;
		}
		case "commandslist": {
			if (kit.getCommands().isEmpty()) {
				player.sendMessage(ChatColor.RED + "Kit "+kit.getName()+" haven't any command set yet");
			} else {
				player.sendMessage(ChatColor.GOLD + "Kit "+kit.getName()+" commands:");
				List<String> list = kit.getCommands();
				for (int i = 0; i < list.size(); i++) {
					player.sendMessage(ChatColor.GREEN + (i + "-" + list.get(i)));
				}
			}
			return true;
		}
		case "addcommand": {
			if (args.length == 2) {
				player.sendMessage(ChatColor.RED + "Missing parameter");
			} else {
				kit.getCommands().add(CommonUtils.mergeStringArrayFromIndex(args, 2));
				player.sendMessage(ChatColor.GREEN + "Command added");
				instance.saveData();
			}
			return true;
		}
		case "removecommand": {
			if (args.length == 2) {
				player.sendMessage(ChatColor.RED + "Missing parameter");
			} else {
				kit.getCommands().remove(Integer.valueOf(args[2]));
				player.sendMessage(ChatColor.GREEN + "Command removed");
				instance.saveData();
			}
			return true;
		}
		}
		player.sendMessage(ChatColor.RED + "Invalid parameter");
		return false;
	}

	public boolean clearCache(CommandSender sender, String label, String[] args) {
		if (!sender.hasPermission("betterkits.clearcache")) {
			sender.sendMessage(Messages.get("PermissionDenied"));
			return false;
		}
		for (Kit kit : instance.getKits()) {
			kit.clearCache();
		}
		sender.sendMessage(ChatColor.GREEN + "Kits cache cleared.");
		return true;
	}

	public boolean refill(CommandSender sender, String label, String[] args) {
		if (!sender.hasPermission("betterkits.refill")) {
			sender.sendMessage(Messages.get("PermissionDenied"));
			return false;
		}

		if (args.length < 5) {
			sender.sendMessage("Usage: /"+label+" (kit) (world) (x) (y) (z)");
			return false;
		}

		final Kit kit = instance.getKit(args[0]);
		if (kit == null) {
			sender.sendMessage(Messages.get("KitNotFound"));
			return false;
		}

		World world = Bukkit.getWorld(args[1]);
		if (world == null) {
			sender.sendMessage("Invalid world");
			return false;
		}

		int x, y, z;
		try {
			x = Integer.valueOf(args[2]);
			y = Integer.valueOf(args[3]);
			z = Integer.valueOf(args[4]);
		} catch (NumberFormatException e) {
			sender.sendMessage("Invalid coordinates");
			return false;
		}

		Location loc = new Location(world, x, y, z);
		if (!(loc.getBlock().getState() instanceof InventoryHolder)) {
			sender.sendMessage("Target block "+loc.getBlock().getType()+" is not an InventoryHolder");
			return false;
		}

		kit.refill((InventoryHolder) loc.getBlock().getState());
		if (sender instanceof Player) {
			sender.sendMessage(ChatColor.GOLD + "The target block has been refilled");
		}
		return true;
	}
}
