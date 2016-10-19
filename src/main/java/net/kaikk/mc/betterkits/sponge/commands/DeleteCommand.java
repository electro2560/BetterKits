package net.kaikk.mc.betterkits.sponge.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import net.kaikk.mc.betterkits.sponge.BetterKits;
import net.kaikk.mc.betterkits.sponge.Messages;

public class DeleteCommand implements CommandExecutor {
	private BetterKits instance;
	public DeleteCommand(BetterKits instance) {
		this.instance = instance;
	}
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		final String kitName = args.<String>getOne("kit").get();
		
		if (instance.removeKit(kitName) != null) {
			try {
				instance.saveData();
				src.sendMessage(Messages.get("KitRemoved"));
			} catch (Exception e) {
				src.sendMessage(Text.of(TextColors.RED, "An error occurred while saving data."));
				e.printStackTrace();
			}
		} else {
			src.sendMessage(Messages.get("KitNotFound"));
		}
		
		return CommandResult.success();
	}

}
