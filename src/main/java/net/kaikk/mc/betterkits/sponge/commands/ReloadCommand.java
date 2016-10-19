package net.kaikk.mc.betterkits.sponge.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import net.kaikk.mc.betterkits.sponge.BetterKits;

public class ReloadCommand implements CommandExecutor {
	private BetterKits instance;
	public ReloadCommand(BetterKits instance) {
		this.instance = instance;
	}
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		try {
			instance.load();
		} catch (Exception e) {
			src.sendMessage(Text.of(TextColors.RED, "An error occurred while reloading the plugin!"));
			e.printStackTrace();
		}
		return CommandResult.success();
	}

}
