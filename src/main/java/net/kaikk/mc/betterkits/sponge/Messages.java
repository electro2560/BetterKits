package net.kaikk.mc.betterkits.sponge;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;

public class Messages {
	private static Map<String, String> messages = new HashMap<String, String>();
	
	public static void load(BetterKits instance, String fileName) {
		//load defaults
		try {
			URL defaultsInJarURL = new URL("jar:file:/"+Sponge.getPluginManager().fromInstance(instance).get().getSource().get()+"!/"+fileName);
			YAMLConfigurationLoader defaultsLoader = YAMLConfigurationLoader.builder().setURL(defaultsInJarURL).build();
			ConfigurationNode defaults = defaultsLoader.load();
			
			YAMLConfigurationLoader loader = YAMLConfigurationLoader.builder().setPath(instance.getConfigDir().resolve(fileName)).build();
			ConfigurationNode root = loader.load();
			root.mergeValuesFrom(defaults);
			loader.save(root);
			
			for (Entry<Object, ? extends ConfigurationNode> entry : root.getChildrenMap().entrySet()) {
				messages.put(entry.getKey().toString(), entry.getValue().getString());
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Text get(String id) {
		String m = messages.get(id);
		if (m == null) {
			return Text.of(TextColors.RED, "Couldn't find the message id \""+id+"\"!");
		}
		
		return TextSerializers.FORMATTING_CODE.deserialize(m);
	}
	
	public static Text get(String id, String... replacements) {
		String m = messages.get(id);
		if (m == null) {
			return Text.of(TextColors.RED, "Couldn't find the message id \""+id+"\"!");
		}
		for (int i = 0; i<replacements.length; i++) {
			m = m.replace("%"+replacements[i], replacements[++i]);
		}
		
		return TextSerializers.FORMATTING_CODE.deserialize(m);
		
	}
}
 