package net.kaikk.mc.betterkits.sponge.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import net.kaikk.mc.betterkits.sponge.BetterKits;
import net.kaikk.mc.betterkits.sponge.Kit;

public class ClearCacheCommand implements CommandExecutor {
	private BetterKits instance;
	public ClearCacheCommand(BetterKits instance) {
		this.instance = instance;
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		for (Kit kit : instance.getKits()) {
			kit.clearCache();
		}
		src.sendMessage(Text.of(TextColors.GREEN, "Kits cache cleared."));
		return CommandResult.success();
	}

}
