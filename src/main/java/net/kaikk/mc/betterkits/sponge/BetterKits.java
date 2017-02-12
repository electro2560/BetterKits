package net.kaikk.mc.betterkits.sponge;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;

import net.kaikk.mc.betterkits.sponge.Kit.KitSerializer;
import net.kaikk.mc.betterkits.sponge.PlayerData.PlayerDataSerializer;
import net.kaikk.mc.betterkits.sponge.commands.ClearCacheCommand;
import net.kaikk.mc.betterkits.sponge.commands.CreateCommand;
import net.kaikk.mc.betterkits.sponge.commands.DeleteCommand;
import net.kaikk.mc.betterkits.sponge.commands.EditCommand;
import net.kaikk.mc.betterkits.sponge.commands.GiveCommand;
import net.kaikk.mc.betterkits.sponge.commands.KitCommand;
import net.kaikk.mc.betterkits.sponge.commands.KitRefillCommand;
import net.kaikk.mc.betterkits.sponge.commands.KitsCommand;
import net.kaikk.mc.betterkits.sponge.commands.PreviewCommand;
import net.kaikk.mc.betterkits.sponge.commands.ReloadCommand;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;

@Plugin(id=PluginInfo.id, name = PluginInfo.name, version = PluginInfo.version, description = PluginInfo.description)
public class BetterKits {
	private static BetterKits instance;
	private Config config;

	private Map<String, Kit> kits;
	private Map<UUID, PlayerData> playersData;

	@Inject
	@DefaultConfig(sharedRoot = false)
	private ConfigurationLoader<CommentedConfigurationNode> configManager;

	@Inject
	@ConfigDir(sharedRoot = false)
	private Path configDir;

	@Inject
	private Logger logger;

	@Inject
	private PluginContainer container;

	@Listener
	public void onGameInitialization(GameInitializationEvent event) throws Exception {
		instance = this;

		TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(Kit.class), new KitSerializer());
		TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(PlayerData.class), new PlayerDataSerializer());

		this.load();

		// Register listener
		Sponge.getEventManager().registerListeners(this, new EventListener(this));

		// Register command
		Sponge.getCommandManager().register(this,
				CommandSpec.builder()
				.permission("betterkits.clearcache")
				.description(Text.of("ClearCacheCommand"))
				.executor(new ClearCacheCommand(this)).build(), "kitclearcache");

		Sponge.getCommandManager().register(this,
				CommandSpec.builder()
				.permission("betterkits.create")
				.description(Text.of("KitCreateCommand"))
				.arguments(GenericArguments.string(Text.of("kit")))
				.executor(new CreateCommand(this)).build(), "kitcreate");

		Sponge.getCommandManager().register(this,
				CommandSpec.builder()
				.permission("betterkits.delete")
				.description(Text.of("DeleteCommand"))
				.arguments(GenericArguments.string(Text.of("kit")))
				.executor(new DeleteCommand(this)).build(), "kitdelete");

		Sponge.getCommandManager().register(this,
				CommandSpec.builder()
				.permission("betterkits.edit")
				.description(Text.of("EditCommand"))
				.arguments(GenericArguments.string(Text.of("kit")), Utils.buildChoices("subcommand", "content", "cooldown", "commandslist", "addcommand", "removecommand"), GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("param"))))
				.executor(new EditCommand(this)).build(), "kitedit");

		Sponge.getCommandManager().register(this,
				CommandSpec.builder()
				.permission("betterkits.give")
				.description(Text.of("GiveCommand"))
				.arguments(GenericArguments.user(Text.of("user")), GenericArguments.string(Text.of("kit")))
				.executor(new GiveCommand(this)).build(), "kitgive");

		Sponge.getCommandManager().register(this,
				CommandSpec.builder()
				.description(Text.of("KitCommand"))
				.arguments(GenericArguments.string(Text.of("kit")))
				.executor(new KitCommand(this)).build(), "kit", "bkit", "getkit");

		Sponge.getCommandManager().register(this,
				CommandSpec.builder()
				.description(Text.of("KitsCommand"))
				.arguments(GenericArguments.optional(Utils.buildChoices("all", "all")))
				.executor(new KitsCommand(this)).build(), "kits", "bkits", "kitlist");

		Sponge.getCommandManager().register(this,
				CommandSpec.builder()
				.permission("betterkits.preview")
				.description(Text.of("PreviewCommand"))
				.arguments(GenericArguments.string(Text.of("kit")))
				.executor(new PreviewCommand(this)).build(), "kitpreview", "kitp", "bkitp");

		Sponge.getCommandManager().register(this,
				CommandSpec.builder()
				.permission("betterkits.reload")
				.description(Text.of("ReloadCommand"))
				.executor(new ReloadCommand(this)).build(), "kitreload");


		Sponge.getCommandManager().register(this,
				CommandSpec.builder()
				.permission("betterkits.refill")
				.description(Text.of("Kit refill command"))
				.arguments(GenericArguments.string(Text.of("kit")), GenericArguments.location(Text.of("location")))
				.executor(new KitRefillCommand(this)).build(), "kitrefill");
	}

	@Listener
	public void onPluginReload(GameReloadEvent event) throws Exception {
		this.load();
	}

	public void load() throws Exception {
		this.config = new Config(this);

		Messages.load(this);
		this.loadData();
	}

	public static BetterKits instance() {
		return instance;
	}

	public Config config() {
		return config;
	}

	public Logger logger() {
		return logger;
	}

	public static void log(String message) {
		if (instance.logger()!=null) {
			instance.logger().info(message);
		} else {
			System.out.println(message);
		}
	}

	public ConfigurationLoader<CommentedConfigurationNode> getConfigManager() {
		return configManager;
	}

	public Path getConfigDir() {
		return configDir;
	}

	public Kit getKit(String name) {
		return this.kits.get(name.toLowerCase());
	}

	public Kit addKit(Kit kit) {
		return this.kits.put(kit.getName().toLowerCase(), kit);
	}

	public Kit removeKit(String name) {
		return this.kits.remove(name.toLowerCase());
	}

	public Collection<Kit> getKits() {
		return Collections.unmodifiableCollection(this.kits.values());
	}

	public Map<UUID, PlayerData> getPlayersData() {
		return playersData;
	}

	public HoconConfigurationLoader getDataLoader() {
		return HoconConfigurationLoader.builder().setPath(this.configDir.resolve("data.conf")).build();
	}

	synchronized public void loadData() throws IOException, ObjectMappingException {
		HoconConfigurationLoader loader = getDataLoader();
		ConfigurationNode rootNode = loader.load();

		List<Kit> kitsList = rootNode.getNode("Kits").getList(TypeToken.of(Kit.class));
		this.kits = new HashMap<String, Kit>();
		for (Kit kit : kitsList) {
			this.kits.put(kit.getName().toLowerCase(), kit);
		}

		List<PlayerData> playersDataList = rootNode.getNode("PlayersData").getList(TypeToken.of(PlayerData.class));
		this.playersData = new HashMap<UUID, PlayerData>();
		for (PlayerData pd : playersDataList) {
			this.playersData.put(pd.getPlayerUUID(), pd);
		}
	}

	synchronized public void saveData() throws IOException, ObjectMappingException {
		HoconConfigurationLoader loader = getDataLoader();
		ConfigurationNode rootNode = loader.load();

		rootNode.getNode("Kits").setValue(KitSerializer.token, new ArrayList<Kit>(this.kits.values()));
		rootNode.getNode("PlayersData").setValue(PlayerDataSerializer.token, new ArrayList<PlayerData>(this.playersData.values()));

		loader.save(rootNode);
	}

	public PluginContainer getContainer() {
		return container;
	}

	public Cause getCause() {
		return Cause.source(container).build();
	}
}
