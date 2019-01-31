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
	private boolean economyEnabled;
	
	private int leaderSize;
	private boolean leaderSignsEnabled;
	private boolean leaderHeadsEnabled;
	private int leaderboardUpdateInterval;
	private boolean eloEnabled;
	private boolean winsEnabled;
	private boolean lossesEnabled;
	private boolean killsEnabled;
	private boolean deathsEnabled;
	private boolean xpEnabled;
	private boolean lobbyBoardEnabled;
	private boolean protectlobby;
	private boolean displayPlayerExeperience;
	
	private Location spawn;
	
	private boolean joinEnabled;
	private int joinSlot;
	private boolean optionsEnabled;
	private int optionsSlot;
	private boolean glassEnabled;
    private boolean particleEnabled;
    private boolean projectEnabled;
    private boolean killsoundEnabled;
    private boolean winsoundEnabled;
    private boolean tauntsEnabled;
	
	
	private boolean playSounds;
	private String openJoinMenu;
	private String openOptionsMenu;
	private String openGlassMenu;
	private String openWinSoundMenu;
	private String openKillSoundMenu;
	private String openParticleMenu;
	private String openProjectileMenu;
	private String openTauntMenu;
	private String confirmSelection;
	private String errorSound;
	
	private List<String> gameServers;
	
	private Map<String, String> materials = new HashMap<String, String>();
	private final List<String> itemNames = Arrays.asList("joinselect",
			"nopermission",
			"optionselect",
			"particleselect", 
			"projectileselect",
			"killsoundselect", "killsounditem",
			"winsoundselect",
			"glassselect", "tauntselect");
		private final List<String> defItems = Arrays.asList("DIAMOND_HELMET",
			"BARRIER",
			"EYE_OF_ENDER",
			"BLAZE_POWDER",
			"ARROW",
			"DIAMOND_SWORD", "NOTE_BLOCK", 
			"DRAGON_EGG", 
			"STAINED_GLASS", "SHIELD");
		private final List<String> defItems18 = Arrays.asList("DIAMOND_HELMET",	
				"BARRIER",
				"EYE_OF_ENDER",
				"BLAZE_POWDER",
				"ARROW",
				"DIAMOND_SWORD", "NOTE_BLOCK", 
				"DRAGON_EGG", 
				"STAINED_GLASS", "DRAGON_EGG");
	
		private final List<String> signItems = Arrays.asList("blockoffline", "blockwaiting", "blockplaying", "blockending", "almostfull", "threefull", "halffull", "almostempty");
		private final List<String> signDef = Arrays.asList("COAL_BLOCK", "EMERALD_BLOCK", "REDSTONE_BLOCK", "LAPIS_BLOCK", "DIAMOND_HELMET", "GOLD_HELMET", "IRON_HELMET", "LEATHER_HELMET");
	
	public Config() {
		load();
	}
	
	private void load() {
		debug = SkyWarsReloaded.get().getConfig().getBoolean("debugMode");
		economyEnabled = SkyWarsReloaded.get().getConfig().getBoolean("economyEnabled");
		
		lobbyBoardEnabled = SkyWarsReloaded.get().getConfig().getBoolean("lobbyBoardEnabled");
		protectlobby = SkyWarsReloaded.get().getConfig().getBoolean("enabledLobbyGuard");
		displayPlayerExeperience = SkyWarsReloaded.get().getConfig().getBoolean("displayPlayerLevelOnXpBar");
		leaderSize = SkyWarsReloaded.get().getConfig().getInt("leaderboards.length");
		leaderSignsEnabled = SkyWarsReloaded.get().getConfig().getBoolean("leaderboards.signsEnabled");
		leaderHeadsEnabled = SkyWarsReloaded.get().getConfig().getBoolean("leaderboards.headsEnabled");
		eloEnabled = SkyWarsReloaded.get().getConfig().getBoolean("leaderboards.eloLeaderboardEnabled");
		winsEnabled = SkyWarsReloaded.get().getConfig().getBoolean("leaderboards.winsLeaderboardEnabled");
		lossesEnabled = SkyWarsReloaded.get().getConfig().getBoolean("leaderboards.lossesLeaderboardEnabled");
		killsEnabled = SkyWarsReloaded.get().getConfig().getBoolean("leaderboards.killsLeaderboardEnabled");
		deathsEnabled = SkyWarsReloaded.get().getConfig().getBoolean("leaderboards.deathsLeaderboardEnabled");
		xpEnabled = SkyWarsReloaded.get().getConfig().getBoolean("leaderboards.xpLeaderboardEnabled");
		leaderboardUpdateInterval = SkyWarsReloaded.get().getConfig().getInt("leaderboards.leaderboardUpdateInterval");
		
		spawn = Util.get().stringToLocation(SkyWarsReloaded.get().getConfig().getString("spawn"));
		
		joinEnabled = SkyWarsReloaded.get().getConfig().getBoolean("enabledMenus.join");
		joinSlot = SkyWarsReloaded.get().getConfig().getInt("enabledMenus.joinSlot");
		optionsEnabled = SkyWarsReloaded.get().getConfig().getBoolean("enabledMenus.options");
		optionsSlot = SkyWarsReloaded.get().getConfig().getInt("enabledMenus.optionsSlot");
		glassEnabled = SkyWarsReloaded.get().getConfig().getBoolean("enabledMenus.glass");
	    particleEnabled = SkyWarsReloaded.get().getConfig().getBoolean("enabledMenus.particle");
	    projectEnabled = SkyWarsReloaded.get().getConfig().getBoolean("enabledMenus.projectile");
	    killsoundEnabled = SkyWarsReloaded.get().getConfig().getBoolean("enabledMenus.killsound");
	    winsoundEnabled = SkyWarsReloaded.get().getConfig().getBoolean("enabledMenus.winsound");
	    tauntsEnabled = SkyWarsReloaded.get().getConfig().getBoolean("enabledMenus.taunts");
	    
		playSounds = SkyWarsReloaded.get().getConfig().getBoolean("sounds.enabled");
		openJoinMenu = SkyWarsReloaded.get().getConfig().getString("sounds.openJoinMenu");
		openOptionsMenu = SkyWarsReloaded.get().getConfig().getString("sounds.openOptionsMenu");
		openGlassMenu = SkyWarsReloaded.get().getConfig().getString("sounds.openGlassMenu");
		openWinSoundMenu = SkyWarsReloaded.get().getConfig().getString("sounds.openWinSoundMenu");
		openKillSoundMenu = SkyWarsReloaded.get().getConfig().getString("sounds.openKillSoundMenu");
		openParticleMenu = SkyWarsReloaded.get().getConfig().getString("sounds.openParticleMenu");
		openProjectileMenu = SkyWarsReloaded.get().getConfig().getString("sounds.openProjectileMenu");
		openTauntMenu = SkyWarsReloaded.get().getConfig().getString("sounds.openTauntMenu");
		confirmSelection = SkyWarsReloaded.get().getConfig().getString("sounds.confirmSelectionSound");
		errorSound = SkyWarsReloaded.get().getConfig().getString("sounds.errorSound");
		
		gameServers = SkyWarsReloaded.get().getConfig().getStringList("gameServers");
		
		for (int i = 0; i < itemNames.size(); i++) {
			String name = itemNames.get(i);
			String def;
			if (SkyWarsReloaded.getNMS().getVersion() < 9) {
				def = defItems18.get(i);
			} else {
				def = defItems.get(i);
			}
			addMaterial(name, SkyWarsReloaded.get().getConfig().getString("items." + name), def);
		}
		
		for (int i = 0; i < signItems.size(); i++) {
			String name = signItems.get(i);
			String def = signDef.get(i);
			addMaterial(name, SkyWarsReloaded.get().getConfig().getString("signs." + name), def);
		}
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
		
		SkyWarsReloaded.get().getConfig().set("economyEnabled", economyEnabled);
			
		SkyWarsReloaded.get().getConfig().set("lobbyBoardEnabled", lobbyBoardEnabled);
		SkyWarsReloaded.get().getConfig().set("lobbyBoardEnabled", lobbyBoardEnabled);
		SkyWarsReloaded.get().getConfig().set("enabledLobbyGuard", protectlobby);
		SkyWarsReloaded.get().getConfig().set("leaderboards.length", leaderSize);
		SkyWarsReloaded.get().getConfig().set("leaderboards.signsEnabled", leaderSignsEnabled);
		SkyWarsReloaded.get().getConfig().set("leaderboards.headsEnabled", leaderHeadsEnabled);
		SkyWarsReloaded.get().getConfig().set("leaderboards.eloLeaderboardEnabled", eloEnabled);
		SkyWarsReloaded.get().getConfig().set("leaderboards.winsLeaderboardEnabled", winsEnabled);
		SkyWarsReloaded.get().getConfig().set("leaderboards.lossesLeaderboardEnabled", lossesEnabled);
		SkyWarsReloaded.get().getConfig().set("leaderboards.killsLeaderboardEnabled", killsEnabled);
		SkyWarsReloaded.get().getConfig().set("leaderboards.deathsLeaderboardEnabled", deathsEnabled);
		SkyWarsReloaded.get().getConfig().set("leaderboards.xpLeaderboardEnabled", xpEnabled);
		SkyWarsReloaded.get().getConfig().set("leaderboards.leaderboardUpdateInterval", leaderboardUpdateInterval);
		
		SkyWarsReloaded.get().getConfig().set("enabledMenus.join", joinEnabled);
		SkyWarsReloaded.get().getConfig().set("enabledMenus.joinSlot", joinSlot);
		SkyWarsReloaded.get().getConfig().set("enabledMenus.options", optionsEnabled);
		SkyWarsReloaded.get().getConfig().set("enabledMenus.optionsSlot", optionsSlot);
		SkyWarsReloaded.get().getConfig().set("enabledMenus.particle", particleEnabled);
		SkyWarsReloaded.get().getConfig().set("enabledMenus.glass", glassEnabled);
	    SkyWarsReloaded.get().getConfig().set("enabledMenus.projectile", projectEnabled);
	    SkyWarsReloaded.get().getConfig().set("enabledMenus.killsound", killsoundEnabled);
	    SkyWarsReloaded.get().getConfig().set("enabledMenus.winsound", winsoundEnabled);
	    SkyWarsReloaded.get().getConfig().set("enabledMenus.taunts", tauntsEnabled);
		
		SkyWarsReloaded.get().getConfig().set("sounds.enabled", playSounds);
		SkyWarsReloaded.get().getConfig().set("sounds.openJoinMenu", openJoinMenu);
		SkyWarsReloaded.get().getConfig().set("sounds.openOptionsMenu", openOptionsMenu);
		SkyWarsReloaded.get().getConfig().set("sounds.openGlassMenu", openGlassMenu);
		SkyWarsReloaded.get().getConfig().set("sounds.openWinSoundMenu", openWinSoundMenu);
		SkyWarsReloaded.get().getConfig().set("sounds.openKillSoundMenu", openKillSoundMenu);
		SkyWarsReloaded.get().getConfig().set("sounds.openParticleMenu", openParticleMenu);
		SkyWarsReloaded.get().getConfig().set("sounds.openProjectileMenu", openProjectileMenu);
		SkyWarsReloaded.get().getConfig().set("sounds.openTauntMenu", openTauntMenu);
		SkyWarsReloaded.get().getConfig().set("sounds.confirmSelectionSound", confirmSelection);
		SkyWarsReloaded.get().getConfig().set("sounds.errorSound", errorSound);
		
		SkyWarsReloaded.get().getConfig().set("gameServers", gameServers);
				
		for (int i = 0; i < itemNames.size(); i++) {
			String name = itemNames.get(i);
			SkyWarsReloaded.get().getConfig().set("items." + name, materials.get(name));
		}
		
		SkyWarsReloaded.get().saveConfig();
	}
	
	public int getUpdateTime() {
		return leaderboardUpdateInterval;
	}

	public boolean soundsEnabled() {
		return playSounds;
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
	
	public boolean optionsMenuEnabled() {
		return optionsEnabled;
	}
	
	public int getOptionsSlot() {
		return optionsSlot;
	}

	public int getLeaderSize() {
		return leaderSize;
	}

	public boolean eloEnabled() {
		return eloEnabled;
	}

	public boolean winsEnabled() {
		return winsEnabled;
	}

	public boolean lossesEnabled() {
		return lossesEnabled;
	}

	public boolean killsEnabled() {
		return killsEnabled;
	}

	public boolean deathsEnabled() {
		return deathsEnabled;
	}

	public boolean xpEnabled() {
		return xpEnabled;
	}

	public boolean leaderSignsEnabled() {
		return leaderSignsEnabled;
	}

	public boolean leaderHeadsEnabled() {
		return leaderHeadsEnabled;
	}

	public boolean lobbyBoardEnabled() {
		return lobbyBoardEnabled;
	}

	public String getOpenOptionsMenuSound() {
		return openOptionsMenu;
	}

	public String getOpenParticleMenuSound() {
		return openParticleMenu;
	}
	
	public String getOpenProjectileMenuSound() {
		return openProjectileMenu;
	}
	
	public String getOpenKillSoundMenuSound() {
		return openKillSoundMenu;
	}
	
	public String getOpenWinSoundMenuSound() {
		return openWinSoundMenu;
	}
	
	public String getOpenTauntMenuSound() {
		return openTauntMenu;
	}
	
	public String getOpenGlassMenuSound() {
		return openGlassMenu;
	}
	
	public String getConfirmeSelctionSound() {
		return confirmSelection;
	}
	
	public String getErrorSound() {
		return errorSound;
	}

	public boolean economyEnabled() {
		return economyEnabled;
	}

	public boolean protectLobby() {
		return protectlobby;
	}

	public boolean displayPlayerExeperience() {
		return displayPlayerExeperience;
	}

	public List<String> getGameServers() {
		return gameServers;
	}

	public boolean joinMenuEnabled() {
		return joinEnabled;
	}
	
	public int getJoinSlot() {
		return joinSlot;
	}
	
	public String getOpenJoinMenuSound() {
		return openJoinMenu;
	}
}


