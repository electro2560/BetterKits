package net.kaikk.mc.betterkits.sponge.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import net.kaikk.mc.betterkits.sponge.BetterKits;
import net.kaikk.mc.betterkits.sponge.Kit;
import net.kaikk.mc.betterkits.sponge.Messages;

public class KitRefillCommand implements CommandExecutor {
	private BetterKits instance;

	public KitRefillCommand(BetterKits instance) {
		this.instance = instance;
	}
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		final String kitName = args.<String>getOne("kit").get();
		final Kit kit = instance.getKit(kitName);
		if (kit == null) {
			src.sendMessage(Messages.get("KitNotFound"));
			return CommandResult.empty();
		}

		Location<World> location = args.<Location<World>>getOne(Text.of("location")).get();
		if (!location.hasTileEntity() || !(location.getTileEntity().get() instanceof Carrier)) {
			src.sendMessage(Text.of("The target block has not a valid inventory"));
			return CommandResult.empty();
		}

		kit.refill((Carrier) location.getTileEntity().get());
		if (src instanceof Player) {
			src.sendMessage(Text.of(TextColors.GOLD, "The target block has been refilled"));
		}
		return CommandResult.success();
	}

}
