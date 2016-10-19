package net.kaikk.mc.betterkits.sponge.commands;

import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.World;

import net.kaikk.mc.betterkits.sponge.BetterKits;
import net.kaikk.mc.betterkits.sponge.Kit;
import net.kaikk.mc.betterkits.sponge.Messages;

public class CreateCommand implements CommandExecutor {
	private BetterKits instance;
	public CreateCommand(BetterKits instance) {
		this.instance = instance;
	}
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			src.sendMessage(Messages.get("OnlyPlayersCanRunThis"));
			return CommandResult.empty();
		}
		
		final String kitName = args.<String>getOne("kit").get();
		
		if (!kitName.matches("[a-zA-Z0-9_-]+")) {
			src.sendMessage(Messages.get("KitNameInvalid"));
			return CommandResult.empty();
		}
		
		if (instance.getKit(kitName) != null) {
			src.sendMessage(Messages.get("KitAlreadyExist"));
			return CommandResult.empty();
		}

		BlockRay<World> blockRay = BlockRay.from(((Player)src)).distanceLimit(5).skipFilter(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1)).build();
		Optional<BlockRayHit<World>> hitOpt = blockRay.end();
		if (hitOpt.isPresent() && instance.config().allowedChests.contains(hitOpt.get().getLocation().getBlock().getType())) {
		    instance.addKit(new Kit(kitName, hitOpt.get().getLocation()));
			try {
				instance.saveData();
				src.sendMessage(Text.of(TextColors.GREEN, "Kit created."));
			} catch (Exception e) {
				src.sendMessage(Text.of(TextColors.RED, "An error occurred while saving data."));
				e.printStackTrace();
			}
			
		} else {
			src.sendMessage(Messages.get("TargetBlockNotAllowed"));
		}
		
		return CommandResult.success();
	}

}
