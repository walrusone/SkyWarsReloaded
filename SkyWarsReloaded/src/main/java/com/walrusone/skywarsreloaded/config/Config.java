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
	
	private boolean bungeeMode;
	private String bungeeLobby;
	
	private int winnerXP;
	private List<String> winCommands;
	private int killerXP;
	private List<String> killCommands;
	private int vip1;
	private int vip2;
	private int vip3;
	private int vip4;
	private int vip5;
	private boolean titlesEnabled;
	private int gameTimer;
	private int waitTimer;
	private int leaderboardUpdateInterval;
	private int timeAfterMatch;
	private boolean fireworksEnabled;
	private int fireworksPer5Tick;
	private int maxMapSize;
	private Location spawn;
	private boolean pressurePlate;
	private boolean teleportOnJoin;
	private boolean teleportOnWorldEnter;
	
	private boolean suddenDeathEnabled;
	private boolean disableHealthRegen;
	private boolean enableHealthDecay;
	
	private int cooldown;
	
	private boolean particlesEnabled;
	
	private boolean optionsEnabled;
	private int optionsSlot;
	private boolean glassEnabled;
    private boolean particleEnabled;
    private boolean projectEnabled;
    private boolean killsoundEnabled;
    private boolean winsoundEnabled;
    private boolean tauntsEnabled;
	
	
	private boolean playSounds;
	private String countdown;
	
	private boolean spectateEnabled;
	private int spectateDistance;

	private boolean disableCommands;
	private List<String> enabledCommands;
	
	private boolean disableCommandsSpectate;
	private List<String> enabledCommandsSpectate;
	
	private Map<String, String> materials = new HashMap<String, String>();
	private final List<String> itemNames = Arrays.asList("kitvote", 
			"chestvote", "chestrandom", "chestbasic", "chestnormal", "chestop", "chestscavenger", 
			"nopermission", 
			"timevote", "timerandom", "timedawn", "timenoon", "timedusk", "timemidnight", 
			"weathervote", "weatherrandom", "weathersunny", "weatherrain", "weatherstorm", "weathersnow", 
			"modifiervote", "modifierrandom", "modifierspeed", "modifierjump", "modifierstrength", "modifiernone", 
			"optionselect",
			"particleselect", 
			"projectileselect",
			"killsoundselect", "killsounditem",
			"winsoundselect",
			"glassselect", "tauntselect");
		private final List<String> defItems = Arrays.asList("EYE_OF_ENDER", 
			"SHIELD", "NETHER_STAR", "STONE_SWORD", "IRON_SWORD", "DIAMOND_SWORD", "WOOD_HOE", 
			"BARRIER", 
			"WATCH", "NETHER_STAR", "WATCH", "WATCH", "WATCH", "WATCH",
			"BLAZE_POWDER", "NETHER_STAR", "PRISMARINE_SHARD", "PRISMARINE_SHARD", "PRISMARINE_SHARD", "PRISMARINE_SHARD", 
			"DRAGONS_BREATH", "NETHER_STAR", "BOOK", "BOOK", "BOOK", "BOOK",
			"EYE_OF_ENDER",
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
		
		bungeeMode = SkyWarsReloaded.get().getConfig().getBoolean("bungeeMode");
		bungeeLobby = SkyWarsReloaded.get().getConfig().getString("bungeeLobby");
		
		winnerXP = SkyWarsReloaded.get().getConfig().getInt("game.xpForWin");
		winCommands = SkyWarsReloaded.get().getConfig().getStringList("game.winCommands");
		killerXP = SkyWarsReloaded.get().getConfig().getInt("game.xpForKill");
		killCommands = SkyWarsReloaded.get().getConfig().getStringList("game.killCommands");
		vip1 = SkyWarsReloaded.get().getConfig().getInt("game.vip1Multiplier");
		vip2 = SkyWarsReloaded.get().getConfig().getInt("game.vip2Multiplier");
		vip3 = SkyWarsReloaded.get().getConfig().getInt("game.vip3Multiplier");
		vip4 = SkyWarsReloaded.get().getConfig().getInt("game.vip4Multiplier");
		vip5 = SkyWarsReloaded.get().getConfig().getInt("game.vip5Multiplier");
		spawn = Util.get().stringToLocation(SkyWarsReloaded.get().getConfig().getString("spawn"));
		timeAfterMatch = SkyWarsReloaded.get().getConfig().getInt("game.timeAfterMatch");
		fireworksPer5Tick = SkyWarsReloaded.get().getConfig().getInt("fireworks.per5Ticks");
		fireworksEnabled = SkyWarsReloaded.get().getConfig().getBoolean("fireworks.enabled");
		gameTimer = SkyWarsReloaded.get().getConfig().getInt("game.gameTimer");
		waitTimer = SkyWarsReloaded.get().getConfig().getInt("game.waitTimer");
		leaderboardUpdateInterval = SkyWarsReloaded.get().getConfig().getInt("game.leaderboardUpdateInterval");
		titlesEnabled = SkyWarsReloaded.get().getConfig().getBoolean("titles.enabled");
		spectateEnabled = SkyWarsReloaded.get().getConfig().getBoolean("game.spectateEnabled");
		spectateDistance = SkyWarsReloaded.get().getConfig().getInt("game.spectateDistance");
		maxMapSize = SkyWarsReloaded.get().getConfig().getInt("game.maxMapSize");
		pressurePlate = SkyWarsReloaded.get().getConfig().getBoolean("enablePressurePlateJoin");
		teleportOnJoin = SkyWarsReloaded.get().getConfig().getBoolean("teleportToSpawnOnJoin");
		teleportOnWorldEnter = SkyWarsReloaded.get().getConfig().getBoolean("teleportToSpawnOnWorldEnter");
		
		
		suddenDeathEnabled = SkyWarsReloaded.get().getConfig().getBoolean("game.suddendeath.enabled");
		disableHealthRegen = SkyWarsReloaded.get().getConfig().getBoolean("game.suddendeath.disableHealthRegen");
		enableHealthDecay = SkyWarsReloaded.get().getConfig().getBoolean("game.suddendeath.enableHealthDecay");
		
		cooldown = SkyWarsReloaded.get().getConfig().getInt("tauntCooldown");
		

		particlesEnabled = SkyWarsReloaded.get().getConfig().getBoolean("particles.enabled");
		optionsEnabled = SkyWarsReloaded.get().getConfig().getBoolean("enabledMenus.options");
		optionsSlot = SkyWarsReloaded.get().getConfig().getInt("enabledMenus.optionsSlot");
		glassEnabled = SkyWarsReloaded.get().getConfig().getBoolean("enabledMenus.glass");
	    particleEnabled = SkyWarsReloaded.get().getConfig().getBoolean("enabledMenus.particle");
	    projectEnabled = SkyWarsReloaded.get().getConfig().getBoolean("enabledMenus.projectile");
	    killsoundEnabled = SkyWarsReloaded.get().getConfig().getBoolean("enabledMenus.killsound");
	    winsoundEnabled = SkyWarsReloaded.get().getConfig().getBoolean("enabledMenus.winsound");
	    tauntsEnabled = SkyWarsReloaded.get().getConfig().getBoolean("enabledMenus.taunts");
	    
		playSounds = SkyWarsReloaded.get().getConfig().getBoolean("sounds.enabled");
		countdown = SkyWarsReloaded.get().getConfig().getString("sounds.countdown");
				
		enabledCommands = SkyWarsReloaded.get().getConfig().getStringList("disable-commands.exceptions");
		disableCommands = SkyWarsReloaded.get().getConfig().getBoolean("disable-commands.enabled");
		
		disableCommandsSpectate = SkyWarsReloaded.get().getConfig().getBoolean("disable-commands-spectate.enabled");
		enabledCommandsSpectate = SkyWarsReloaded.get().getConfig().getStringList("disable-commands-spectate.exceptions");

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
		
		SkyWarsReloaded.get().getConfig().set("bungeeMode", bungeeMode);
		SkyWarsReloaded.get().getConfig().set("bungeeLobby", bungeeLobby);
		
		SkyWarsReloaded.get().getConfig().set("game.xpForWin", winnerXP);
		SkyWarsReloaded.get().getConfig().set("game.winCommands", winCommands);
		SkyWarsReloaded.get().getConfig().set("game.xpForKill", killerXP);
		SkyWarsReloaded.get().getConfig().set("game.killCommands", killCommands);
		SkyWarsReloaded.get().getConfig().set("game.vip1Multiplier", vip1);
		SkyWarsReloaded.get().getConfig().set("game.vip2Multiplier", vip2);
		SkyWarsReloaded.get().getConfig().set("game.vip3Multiplier", vip3);
		SkyWarsReloaded.get().getConfig().set("game.vip4Multiplier", vip4);
		SkyWarsReloaded.get().getConfig().set("game.vip5Multiplier", vip5);
		SkyWarsReloaded.get().getConfig().set("titles.enabled", titlesEnabled);
		SkyWarsReloaded.get().getConfig().getInt("game.gameTimer", gameTimer);
		SkyWarsReloaded.get().getConfig().getInt("game.waitTimer", waitTimer);
		SkyWarsReloaded.get().getConfig().getInt("game.leaderboardUpdateInterval", leaderboardUpdateInterval);
		SkyWarsReloaded.get().getConfig().set("game.timeAfterMatch", timeAfterMatch);
		SkyWarsReloaded.get().getConfig().set("fireworks.per5Ticks", fireworksPer5Tick);
		SkyWarsReloaded.get().getConfig().set("fireworks.enabled", fireworksEnabled);
		SkyWarsReloaded.get().getConfig().set("game.spectateEnabled", spectateEnabled);
		SkyWarsReloaded.get().getConfig().set("game.spectateDistance", spectateDistance);
		SkyWarsReloaded.get().getConfig().set("game.maxMapSize", maxMapSize);
		SkyWarsReloaded.get().getConfig().set("enablePressurePlateJoin", pressurePlate);
		SkyWarsReloaded.get().getConfig().set("teleportToSpawnOnJoin", teleportOnJoin);
		SkyWarsReloaded.get().getConfig().set("teleportToSpawnOnWorldEnter", teleportOnWorldEnter);
		
		SkyWarsReloaded.get().getConfig().set("game.suddendeath.enabled", suddenDeathEnabled);
		SkyWarsReloaded.get().getConfig().set("game.suddendeath.disableHealthRegen", disableHealthRegen);
		SkyWarsReloaded.get().getConfig().set("game.suddendeath.enableHealthDecay", enableHealthDecay);
		
		SkyWarsReloaded.get().getConfig().set("tauntCooldown", cooldown);
		
		SkyWarsReloaded.get().getConfig().set("particles.enabled", particlesEnabled);
		
		SkyWarsReloaded.get().getConfig().set("enabledMenus.options", optionsEnabled);
		SkyWarsReloaded.get().getConfig().set("enabledMenus.optionsSlot", optionsSlot);
		SkyWarsReloaded.get().getConfig().set("enabledMenus.glass", glassEnabled);
	    SkyWarsReloaded.get().getConfig().set("enabledMenus.particle", particlesEnabled);
	    SkyWarsReloaded.get().getConfig().set("eabledMenus.projectile", projectEnabled);
	    SkyWarsReloaded.get().getConfig().set("enabledMenus.killsound", killsoundEnabled);
	    SkyWarsReloaded.get().getConfig().set("enabledMenus.winsound", winsoundEnabled);
	    SkyWarsReloaded.get().getConfig().set("enabledMenus.taunts", tauntsEnabled);
		
		SkyWarsReloaded.get().getConfig().set("sounds.enabled", playSounds);
		SkyWarsReloaded.get().getConfig().set("sounds.countdown", countdown);
			
		SkyWarsReloaded.get().getConfig().set("disable-commands.exceptions", enabledCommands);
		SkyWarsReloaded.get().getConfig().set("disable-commands.enabled", disableCommands);
		
		SkyWarsReloaded.get().getConfig().set("disable-commands-spectate.enabled", disableCommandsSpectate);
		SkyWarsReloaded.get().getConfig().set("disable-commands-spectate.exceptions", enabledCommandsSpectate);
		
		for (int i = 0; i < itemNames.size(); i++) {
			String name = itemNames.get(i);
			SkyWarsReloaded.get().getConfig().set("items." + name, materials.get(name));
		}
		
		SkyWarsReloaded.get().saveConfig();
	}
	
	public boolean suddenDeathEnabled() {
		return suddenDeathEnabled;
	}
	
	public boolean disableHealthRegen() {
		return disableHealthRegen;
	}
	
	public boolean enableHealthDecay() {
		return enableHealthDecay;
	}
	
	public List<String> getEnabledCommands() {
		return enabledCommands;
	}
	
	public List<String> getEnabledCommandsSpectate() {
		return enabledCommandsSpectate;
	}
	
	public int getUpdateTime() {
		return leaderboardUpdateInterval;
	}
	
	public boolean disableCommands() {
		return disableCommands;
	}
	
	public boolean disableCommandsSpectate() {
		return disableCommandsSpectate;
	}
	
	public int getGameTimer() {
		return gameTimer;
	}
	
	public int getWaitTimer() {
		return waitTimer;
	}

	public int getTimeAfterMatch() {
		return timeAfterMatch;
	}

	public int getFireWorksPer5Tick() {
		return fireworksPer5Tick;
	}
	
	public boolean fireworksEnabled() {
		return fireworksEnabled;
	}
	
	public boolean titlesEnabled() {
		return titlesEnabled;
	}

	public boolean particlesEnabled() {
		return particlesEnabled;
	}

	public boolean playSounds() {
		return playSounds;
	}
	
	public boolean spectateEnabled() {
		return spectateEnabled;
	}

	public boolean debugEnabled() {
		return debug;
	}

	public int getSpectateDistance() {
		return spectateDistance;
	}

	public int getMaxMapSize() {
		return maxMapSize;
	}

	public void setSpawn(Location location) {
		this.spawn = location;
	}
	
	public Location getSpawn() {
		return spawn;
	}
	
	public boolean bungeeMode() {
		return (bungeeMode && SkyWarsReloaded.get().isEnabled());
	}
	
	public String getBungeeLobby() {
		return bungeeLobby;
	}

	public int getCooldown() {
		return cooldown;
	}

	public String getMaterial(String string) {
		return materials.get(string);
	}

	public boolean pressurePlateJoin() {
		return pressurePlate;
	}
	
	public String getCountdownSound() {
		return countdown;
	}

	public int getWinnerXP() {
		return winnerXP;
	}
	
	public int getKillerXP() {
		return killerXP;
	}

	public int getVip1() {
		return vip1;
	}
	
	public int getVip2() {
		return vip2;
	}
	
	public int getVip3() {
		return vip3;
	}
	
	public int getVip4() {
		return vip4;
	}
	
	public int getVip5() {
		return vip5;
	}
	
	public List<String> getWinCommands() {
		return winCommands;
	}
	
	public List<String> getKillCommands() {
		return killCommands;
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

	public boolean teleportOnJoin() {
		return teleportOnJoin;
	}
	
	public boolean teleportOnWorldEnter() {
		return teleportOnWorldEnter;
	}
}
