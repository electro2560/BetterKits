package net.kaikk.mc.betterkits.sponge.commands;

import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import net.kaikk.mc.betterkits.sponge.BetterKits;
import net.kaikk.mc.betterkits.sponge.Kit;
import net.kaikk.mc.betterkits.sponge.Messages;

public class EditCommand implements CommandExecutor {
	private BetterKits instance;
	public EditCommand(BetterKits instance) {
		this.instance = instance;
	}
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		final String kitName = args.<String>getOne("kit").get();
		final String sub = args.<String>getOne("subcommand").get();
		final Optional<String> param = args.<String>getOne("param");
		
		final Kit kit = instance.getKit(kitName);
		if (kit == null) {
			src.sendMessage(Messages.get("KitNotFound"));
			return CommandResult.empty();
		}
		
		switch(sub.toLowerCase()) {
		case "content": {
			if (!(src instanceof Player)) {
				src.sendMessage(Messages.get("OnlyPlayersCanRunThis"));
				return CommandResult.empty();
			}
			instance.xsapi.openInventory((Player) src, kit.getChestInventory());
			src.sendMessage(Text.of(TextColors.RED, "Use /kitclearcache after you complete all the changes."));
			return CommandResult.success();
		}
		case "cooldown": {
			if (param.isPresent()) {
				kit.setCooldown(Integer.valueOf(param.get()));
				
				try {
					instance.saveData();
					src.sendMessage(Text.of(TextColors.GREEN, "Kit "+kit.getName()+" cooldown set to "+kit.getCooldown()+" seconds."));
				} catch (Exception e) {
					src.sendMessage(Text.of(TextColors.RED, "An error occurred while saving data."));
					e.printStackTrace();
				}
			} else {
				src.sendMessage(Text.of(TextColors.GREEN, "Kit "+kit.getName()+" cooldown is "+kit.getCooldown()+" seconds."));
			}
			return CommandResult.success();
		}
		case "commandslist": {
			if (kit.getCommands().isEmpty()) {
				src.sendMessage(Text.of(TextColors.RED, "Kit "+kit.getName()+" haven't any command set yet"));
			} else {
				src.sendMessage(Text.of(TextColors.GOLD, "Kit "+kit.getName()+" commands:"));
				List<String> list = kit.getCommands();
				for (int i = 0; i < list.size(); i++) {
					src.sendMessage(Text.of(TextColors.GREEN, (i + "-" + list.get(i))));
				}
			}
			return CommandResult.success();
		}
		case "addcommand": {
			if (!param.isPresent()) {
				src.sendMessage(Text.of(TextColors.RED, "Missing parameter"));
				return CommandResult.empty();
			}
			
			kit.getCommands().add(param.get());
			try {
				instance.saveData();
				src.sendMessage(Text.of(TextColors.GREEN, "Command added"));
			} catch (Exception e) {
				src.sendMessage(Text.of(TextColors.RED, "An error occurred while saving data."));
				e.printStackTrace();
			}
			return CommandResult.success();
		}
		case "removecommand": {
			if (!param.isPresent()) {
				src.sendMessage(Text.of(TextColors.RED, "Missing parameter"));
				return CommandResult.empty();
			}
			kit.getCommands().remove(Integer.valueOf(param.get()).intValue());
			try {
				instance.saveData();
				src.sendMessage(Text.of(TextColors.GREEN, "Command removed"));
			} catch (Exception e) {
				src.sendMessage(Text.of(TextColors.RED, "An error occurred while saving data."));
				e.printStackTrace();
			}
			
			return CommandResult.success();
		}
		}
		src.sendMessage(Text.of(TextColors.RED, "Invalid parameter"));
		return CommandResult.empty();
	}

}
