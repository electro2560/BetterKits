package net.kaikk.mc.betterkits.sponge.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import net.kaikk.mc.betterkits.sponge.BetterKits;
import net.kaikk.mc.betterkits.sponge.Kit;
import net.kaikk.mc.betterkits.sponge.Messages;
import net.kaikk.mc.betterkits.sponge.PlayerData;

public class GiveCommand implements CommandExecutor {
	private BetterKits instance;
	public GiveCommand(BetterKits instance) {
		this.instance = instance;
	}
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		final User user = args.<User>getOne("user").get();
		final String kitName = args.<String>getOne("kit").get();

		final Kit kit = instance.getKit(kitName);
		if (kit == null) {
			src.sendMessage(Messages.get("KitNotFound"));
			return CommandResult.empty();
		}
		
		PlayerData pd = instance.getPlayersData().get(user.getUniqueId());
		if (pd == null) {
			pd = new PlayerData(user.getUniqueId());
			instance.getPlayersData().put(user.getUniqueId(), pd);
		}
		
		final int amount = args.<Integer>getOne("amount").isPresent() ? args.<Integer>getOne("amount").get() : 1;
		
		Integer current = pd.getPendingKits().get(kit.getName());
		if (current == null) {
			current = 0;
		}
		pd.getPendingKits().put(kit.getName(), amount+current);
		
		try {
			instance.saveData();
			src.sendMessage(Messages.get("PlayerHasNow", "name", user.getName(), "amount", String.valueOf(amount+current), "kit", kit.getName()));
		} catch (Exception e) {
			src.sendMessage(Text.of(TextColors.RED, "An error occurred while saving data."));
			e.printStackTrace();
		}
		
		return CommandResult.success();
	}
}
