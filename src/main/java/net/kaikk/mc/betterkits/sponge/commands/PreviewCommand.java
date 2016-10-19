package net.kaikk.mc.betterkits.sponge.commands;

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

public class PreviewCommand implements CommandExecutor {
	private BetterKits instance;
	public PreviewCommand(BetterKits instance) {
		this.instance = instance;
	}
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		try {
			final String kitName = args.<String>getOne("kit").get();
			final Kit kit = instance.getKit(kitName);
			if (kit == null) {
				src.sendMessage(Messages.get("KitNotFound"));
				return CommandResult.empty();
			}
			
			kit.openKitPreview((Player) src);
		} catch (Throwable e) {
			src.sendMessage(Text.of(TextColors.RED, "This command is currently unsupported by the current SpongeAPI (missing Inventory API)."));
			e.printStackTrace();
		}
		return CommandResult.success();
	}

}
