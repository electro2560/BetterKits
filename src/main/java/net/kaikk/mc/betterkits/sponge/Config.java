package net.kaikk.mc.betterkits.sponge;

import java.net.URL;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;

import com.google.common.reflect.TypeToken;

import net.kaikk.mc.betterkits.common.CommonUtils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;

public class Config {
	public Set<BlockType> allowedChests = new HashSet<BlockType>();
	public String starterKitName;
	
	public Config(BetterKits instance) throws Exception {
		//load defaults
		URL defaultsInJarURL = CommonUtils.class.getResource("config.yml");
		YAMLConfigurationLoader defaultsLoader = YAMLConfigurationLoader.builder().setURL(defaultsInJarURL).build();
		ConfigurationNode defaults = defaultsLoader.load();

		//load config & merge defaults
		ConfigurationNode rootNode = instance.getConfigManager().load();
		rootNode.mergeValuesFrom(defaults);
		instance.getConfigManager().save(rootNode);
		
		for (String blockType : rootNode.getNode("AllowedChests").getList(TypeToken.of(String.class))) {
			Optional<BlockType> optType = Sponge.getRegistry().getType(BlockType.class, blockType);
			if (optType.isPresent()) {
				allowedChests.add(optType.get());
			} else {
				instance.logger().warn("BlockType "+blockType+" is not valid.");
			}
		}
		
		starterKitName = rootNode.getNode("StarterKit").getString("starter");
	}
}

