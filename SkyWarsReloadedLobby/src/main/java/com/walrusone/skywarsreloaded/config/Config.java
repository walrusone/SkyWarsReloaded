package com.walrusone.skywarsreloaded.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.utilities.Util;

public class Config {

	private boolean debug;
	
	private int leaderboardUpdateInterval;
	private Location spawn;
	
	private boolean glassEnabled;
    private boolean particleEnabled;
    private boolean projectEnabled;
    private boolean killsoundEnabled;
    private boolean winsoundEnabled;
    private boolean tauntsEnabled;
		
	private Map<String, String> materials = new HashMap<String, String>();
	private List<String> servers;
	
	private final List<String> itemNames = Arrays.asList(
			"particleselect", 
			"projectileselect",
			"killsoundselect", "killsounditem",
			"winsoundselect",
			"glassselect", "tauntselect");
		private final List<String> defItems = Arrays.asList(
			"BLAZE_POWDER",
			"ARROW",
			"DIAMOND_SWORD", "NOTE_BLOCK", 
			"DRAGON_EGG", 
			"STAINED_GLASS", "SHIELD");
	
	private final List<String> signItems = Arrays.asList("blockoffline", "blockwaiting", "blockplaying", "blockending");
	private final List<String> signDef = Arrays.asList("COAL_BLOCK", "EMERALD_BLOCK", "REDSTONE_BLOCK", "LAPIS_BLOCK");
	
	public Config() {
		load();
	}
	
	private void load() {
		debug = SkyWarsReloaded.get().getConfig().getBoolean("debugMode");
		spawn = Util.get().stringToLocation(SkyWarsReloaded.get().getConfig().getString("spawn"));
		leaderboardUpdateInterval = SkyWarsReloaded.get().getConfig().getInt("leaderboardUpdateInterval");
		glassEnabled = SkyWarsReloaded.get().getConfig().getBoolean("enabledMenus.glass");
	    particleEnabled = SkyWarsReloaded.get().getConfig().getBoolean("enabledMenus.particle");
	    projectEnabled = SkyWarsReloaded.get().getConfig().getBoolean("enabledMenus.projectile");
	    killsoundEnabled = SkyWarsReloaded.get().getConfig().getBoolean("enabledMenus.killsound");
	    winsoundEnabled = SkyWarsReloaded.get().getConfig().getBoolean("enabledMenus.winsound");
	    tauntsEnabled = SkyWarsReloaded.get().getConfig().getBoolean("enabledMenus.taunts");

		for (int i = 0; i < itemNames.size(); i++) {
			String name = itemNames.get(i);
			String def = defItems.get(i);
			addMaterial(name, SkyWarsReloaded.get().getConfig().getString("items." + name), def);
		}
		
		for (int i = 0; i < signItems.size(); i++) {
			String name = signItems.get(i);
			String def = signDef.get(i);
			addMaterial(name, SkyWarsReloaded.get().getConfig().getString("signs." + name), def);
		}
		servers = SkyWarsReloaded.get().getConfig().getStringList("gameServers");
	}
	
	private void addMaterial(String key, String mat, String def) {
		Material material = Material.matchMaterial(mat);
		if (material == null) {
			materials.put(key, def);
		} else {
			materials.put(key, mat);
		}	
	}

	public void save() {
		SkyWarsReloaded.get().getConfig().set("debugMode", debug);
		
		SkyWarsReloaded.get().getConfig().set("spawn", Util.get().locationToString(spawn));
		SkyWarsReloaded.get().getConfig().getInt("game.leaderboardUpdateInterval", leaderboardUpdateInterval);
				
		for (int i = 0; i < itemNames.size(); i++) {
			String name = itemNames.get(i);
			SkyWarsReloaded.get().getConfig().set("items." + name, materials.get(name));
		}
		SkyWarsReloaded.get().getConfig().set("gameServers", servers);
		
		
		SkyWarsReloaded.get().saveConfig();
	}
	
	public int getUpdateTime() {
		return leaderboardUpdateInterval;
	}
	
	public boolean debugEnabled() {
		return debug;
	}

	public void setSpawn(Location location) {
		this.spawn = location;
	}
	
	public Location getSpawn() {
		return spawn;
	}
	
	public String getMaterial(String string) {
		return materials.get(string);
	}

	public boolean glassMenuEnabled() {
		return glassEnabled;
	}
	
	public boolean particleMenuEnabled() {
		return particleEnabled;
	}
	
	public boolean projectileMenuEnabled() {
		return projectEnabled;
	}
	
	public boolean killsoundMenuEnabled() {
		return killsoundEnabled;
	}
	
	public boolean winsoundMenuEnabled() {
		return winsoundEnabled;
	}
	
	public boolean tauntsMenuEnabled() {
		return tauntsEnabled;
	}
	
	public List<String> getGameServers() {
		return servers;
	}
}
