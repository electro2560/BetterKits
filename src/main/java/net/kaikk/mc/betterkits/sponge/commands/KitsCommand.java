package net.kaikk.mc.betterkits.sponge.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import net.kaikk.mc.betterkits.sponge.BetterKits;
import net.kaikk.mc.betterkits.sponge.Kit;
import net.kaikk.mc.betterkits.sponge.Messages;
import net.kaikk.mc.betterkits.sponge.PlayerData;

public class KitsCommand implements CommandExecutor {
	private BetterKits instance;
	public KitsCommand(BetterKits instance) {
		this.instance = instance;
	}
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		final List<Kit> kits;
		if (src instanceof Player) {
			final Optional<String> all = args.<String>getOne("all");
			
			Player player = (Player) src;
			final PlayerData pd = instance.getPlayersData().get(player.getUniqueId());
			if (all.isPresent() && all.get().equalsIgnoreCase("all")) {
				if (!player.hasPermission("betterkits.listall")) {
					player.sendMessage(Messages.get("PermissionDenied"));
					return CommandResult.empty();
				}
				kits = new ArrayList<Kit>(instance.getKits());
			} else {
				if (!player.hasPermission("betterkits.list")) {
					player.sendMessage(Messages.get("PermissionDenied"));
					return CommandResult.empty();
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
			src.sendMessage(Messages.get("NoKitsAvailable"));
		} else {
			StringBuilder sb = new StringBuilder();
			for (Kit kit : kits) {
				sb.append(kit.getName());
				sb.append(", ");
			}
			sb.setLength(sb.length()-2);
			src.sendMessage(Text.of(Messages.get("Kits"), sb.toString()));
		}
		
		return CommandResult.success();
	}

}
