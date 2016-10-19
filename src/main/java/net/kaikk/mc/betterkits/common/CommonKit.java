package net.kaikk.mc.betterkits.common;

import java.util.List;

public abstract class CommonKit {
	protected String name, world;
	protected int x, y, z, cooldown;
	protected List<String> commands;
	
	public CommonKit(String name, String world, int x, int y, int z, int cooldown, List<String> commands) {
		this.name = name;
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.cooldown = cooldown;
		this.commands = commands;
	}
	
	public String getName() {
		return name;
	}

	public String getWorld() {
		return world;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public int getCooldown() {
		return cooldown;
	}
	
	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}

	public List<String> getCommands() {
		return commands;
	}
}
