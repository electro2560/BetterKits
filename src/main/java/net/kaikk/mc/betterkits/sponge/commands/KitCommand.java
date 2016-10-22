package net.kaikk.mc.betterkits.sponge.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import net.kaikk.mc.betterkits.common.CommonUtils;
import net.kaikk.mc.betterkits.sponge.BetterKits;
import net.kaikk.mc.betterkits.sponge.Kit;
import net.kaikk.mc.betterkits.sponge.Messages;
import net.kaikk.mc.betterkits.sponge.PlayerData;

public class KitCommand implements CommandExecutor {
	private BetterKits instance;
	public KitCommand(BetterKits instance) {
		this.instance = instance;
	}
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			src.sendMessage(Messages.get("OnlyPlayersCanRunThis"));
			return CommandResult.empty();
		}
		
		final String kitName = args.<String>getOne("kit").get();
		final Kit kit = instance.getKit(kitName);
		if (kit == null) {
			src.sendMessage(Messages.get("KitNotFound"));
			return CommandResult.empty();
		}
		
		Player player = (Player) src;
		PlayerData pd = instance.getPlayersData().get(player.getUniqueId());
		if (pd == null) {
			pd = new PlayerData(player.getUniqueId());
		}
		
		final String lcName = kit.getName().toLowerCase();
		Integer pendingKitAmount = pd.getPendingKits().get(lcName);
		if ((pendingKitAmount == null || pendingKitAmount <= 0) && !player.hasPermission("betterkits.allkits") && !player.hasPermission("betterkits.kit."+lcName)) {
			player.sendMessage(Messages.get("NotAllowedToUseThisKit"));
			return CommandResult.empty();
		}
		
		Integer time = pd.getCooldownKits().get(lcName);
		if (time != null && CommonUtils.epoch() - time < kit.getCooldown() && !player.hasPermission("betterkits.bypasscooldown.allkits") && !player.hasPermission("betterkits.bypasscooldown.kit."+lcName)) {
			player.sendMessage(Messages.get("WaitKitCooldown", "remaining", CommonUtils.timeToString(kit.getCooldown() - (CommonUtils.epoch() - time)), "total", CommonUtils.timeToString(kit.getCooldown())));
			return CommandResult.empty();
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
		
		try {
			instance.saveData();
			kit.give(player);
			player.sendMessage(Messages.get("YouReceivedKit", "name", kit.getName()));
		} catch (Exception e) {
			src.sendMessage(Text.of(TextColors.RED, "An error occurred while saving data."));
			e.printStackTrace();
		}
		
		return CommandResult.success();
	}

}
