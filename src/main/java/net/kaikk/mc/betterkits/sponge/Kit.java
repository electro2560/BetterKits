package net.kaikk.mc.betterkits.sponge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.google.common.reflect.TypeToken;

import net.kaikk.mc.betterkits.common.CommonKit;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

public class Kit extends CommonKit {
	private ItemStackSnapshot[] itemStacksCache;

	public Kit(String name, String world, int x, int y, int z, int cooldown, List<String> commands) {
		super(name, world, x, y, z, cooldown, commands);
	}

	public Kit(String name, Location<World> block) {
		super(name, block.getExtent().getName(), block.getBlockX(), block.getBlockY(), block.getBlockZ(), 0, Collections.emptyList());
	}

	public Location<World> getBlockLocation() {
		return new Location<World>(Sponge.getServer().getWorld(this.getWorld()).get(), x, y, z);
	}

	public Inventory getChestInventory() {
		this.getBlockLocation().getExtent().loadChunk(this.getBlockLocation().getChunkPosition(), false);
		return ((TileEntityCarrier) this.getBlockLocation().getTileEntity().get()).getInventory();
	}

	public void give(Player player) {
		this.checkCache();
		for (ItemStackSnapshot iss : itemStacksCache) {
			InventoryTransactionResult result = player.getInventory().offer(iss.createStack());
			for (ItemStackSnapshot is : result.getRejectedItems()) {
				Item item = (Item) player.getLocation().getExtent().createEntity(EntityTypes.ITEM, player.getLocation().getPosition());
				item.offer(Keys.REPRESENTED_ITEM, is.copy());
				player.getWorld().spawnEntity(item, Cause.of(NamedCause.source(player), NamedCause.owner("BetterKits")));
			}
		}

		for (String command : this.getCommands()) {
			try {
				Sponge.getCommandManager().process(Sponge.getServer().getConsole(), command.replace("%name", player.getName()).replace("%uuid", player.getUniqueId().toString()));
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	public void refill(Carrier carrier) {
		this.checkCache();

		for (ItemStackSnapshot iss : itemStacksCache) {
			carrier.getInventory().clear();
			carrier.getInventory().offer(iss.createStack());
		}
	}

	public void checkCache(){
		if (itemStacksCache == null) {
			List<ItemStackSnapshot> list = new ArrayList<ItemStackSnapshot>();
			for (Inventory s : this.getChestInventory().slots()) {
				if (s.peek().isPresent()) {
					list.add(s.peek().get().createSnapshot());
				}
			}
			itemStacksCache = list.toArray(new ItemStackSnapshot[list.size()]);
		}
	}

	public void openKitPreview(Player player) {
		player.openInventory(this.getChestInventory(), BetterKits.instance().getCause());
	}

	public void clearCache() {
		itemStacksCache = null;
	}

	public static class KitSerializer implements TypeSerializer<Kit> {
		@SuppressWarnings("serial")
		final public static TypeToken<List<Kit>> token = new TypeToken<List<Kit>>() {};

		@Override
		public Kit deserialize(TypeToken<?> token, ConfigurationNode node) throws ObjectMappingException {
			return new Kit(node.getNode("name").getString(), node.getNode("world").getString(), node.getNode("x").getInt(), node.getNode("y").getInt(), node.getNode("z").getInt(), node.getNode("cooldown").getInt(), node.getNode("commands").getList(TypeToken.of(String.class)));
		}

		@Override
		public void serialize(TypeToken<?> token, Kit kit, ConfigurationNode node) throws ObjectMappingException {
			node.getNode("name").setValue(kit.name);
			node.getNode("world").setValue(kit.world);
			node.getNode("x").setValue(kit.x);
			node.getNode("y").setValue(kit.y);
			node.getNode("z").setValue(kit.z);
			node.getNode("cooldown").setValue(kit.cooldown);
			node.getNode("commands").setValue(kit.commands);
		}

	}
}
