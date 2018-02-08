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
	private boolean economyEnabled;
	private String bungeeLobby;

	
	private String resourcePack;
	private boolean promptResource;
	
	private int kitvotepos;
	private boolean kitsEnabled;
	private int chestvotepos;
	private boolean chestVoteEnabled;
	private int timevotepos;
	private boolean timeVoteEnabled;
	private int weathervotepos;
	private boolean weatherVoteEnabled;
	private int modifiervotepos;
	private boolean modifierVoteEnabled;
	
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
	private boolean allowFallDamage;
	private boolean kitVotingEnabled;
	private int gameTimer;
	private int waitTimer;

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
	
	private int randPos;
	private int noKitPos;
	private String randMat;
	private String noKitMat;
	
	private boolean particlesEnabled;
	private int ticksPerUpdate;
	
	private boolean joinEnabled;
	private int joinSlot;
	private boolean spectateMenuEnabled;
	private int spectateSlot;
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
	private String joinSound;
	private String leaveSound;
	private String openJoinMenu;
	private String openSpectateMenu;
	private String openOptionsMenu;
	private String openGlassMenu;
	private String openWinSoundMenu;
	private String openKillSoundMenu;
	private String openParticleMenu;
	private String openProjectileMenu;
	private String openTauntMenu;
	private String openKitMenu;
	private String openChestMenu;
	private String openTimeMenu;
	private String openWeatherMenu;
	private String openModifierMenu;
	private String confirmSelection;
	private String errorSound;
	
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
			"joinselect",
			"spectateselect",
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
			"DIAMOND_HELMET",
			"LEATHER_HELMET",			
			"EYE_OF_ENDER",
			"BLAZE_POWDER",
			"ARROW",
			"DIAMOND_SWORD", "NOTE_BLOCK", 
			"DRAGON_EGG", 
			"STAINED_GLASS", "SHIELD");
		private final List<String> defItems18 = Arrays.asList("EYE_OF_ENDER", 
				"DIAMOND", "NETHER_STAR", "STONE_SWORD", "IRON_SWORD", "DIAMOND_SWORD", "WOOD_HOE", 
				"BARRIER", 
				"WATCH", "NETHER_STAR", "WATCH", "WATCH", "WATCH", "WATCH",
				"BLAZE_POWDER", "NETHER_STAR", "PRISMARINE_SHARD", "PRISMARINE_SHARD", "PRISMARINE_SHARD", "PRISMARINE_SHARD", 
				"DRAGON_EGG", "NETHER_STAR", "BOOK", "BOOK", "BOOK", "BOOK",
				"DIAMOND_HELMET",
				"LEATHER_HELMET",			
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
		
		bungeeMode = SkyWarsReloaded.get().getConfig().getBoolean("bungeeMode");
		economyEnabled = SkyWarsReloaded.get().getConfig().getBoolean("economyEnabled");
		bungeeLobby = SkyWarsReloaded.get().getConfig().getString("bungeeLobby");
		
		resourcePack = SkyWarsReloaded.get().getConfig().getString("resourcepack");
		promptResource = SkyWarsReloaded.get().getConfig().getBoolean("promptForResourcePackOnJoin");
		
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
		titlesEnabled = SkyWarsReloaded.get().getConfig().getBoolean("titles.enabled");
		allowFallDamage = SkyWarsReloaded.get().getConfig().getBoolean("game.allowFallDamage");
		kitVotingEnabled = SkyWarsReloaded.get().getConfig().getBoolean("game.kitVotingEnabled");
		spectateEnabled = SkyWarsReloaded.get().getConfig().getBoolean("game.spectateEnabled");
		spectateDistance = SkyWarsReloaded.get().getConfig().getInt("game.spectateDistance");
		maxMapSize = SkyWarsReloaded.get().getConfig().getInt("game.maxMapSize");
		pressurePlate = SkyWarsReloaded.get().getConfig().getBoolean("enablePressurePlateJoin");
		teleportOnJoin = SkyWarsReloaded.get().getConfig().getBoolean("teleportToSpawnOnJoin");
		teleportOnWorldEnter = SkyWarsReloaded.get().getConfig().getBoolean("teleportToSpawnOnWorldEnter");
		
		kitvotepos = SkyWarsReloaded.get().getConfig().getInt("items.kitVotePosition");
		kitsEnabled = SkyWarsReloaded.get().getConfig().getBoolean("items.kitsEnabled");
		chestvotepos = SkyWarsReloaded.get().getConfig().getInt("items.chestVotePosition");
		chestVoteEnabled = SkyWarsReloaded.get().getConfig().getBoolean("items.chestVoteEnabled");
		timevotepos = SkyWarsReloaded.get().getConfig().getInt("items.timeVotePosition");
		timeVoteEnabled = SkyWarsReloaded.get().getConfig().getBoolean("items.timeVoteEnabled");
		weathervotepos = SkyWarsReloaded.get().getConfig().getInt("items.weatherVotePosition");
		weatherVoteEnabled = SkyWarsReloaded.get().getConfig().getBoolean("items.weatherVoteEnabled");
		modifiervotepos = SkyWarsReloaded.get().getConfig().getInt("items.modifierVotePosition");
		modifierVoteEnabled = SkyWarsReloaded.get().getConfig().getBoolean("items.modifierVoteEnabled");
		
		suddenDeathEnabled = SkyWarsReloaded.get().getConfig().getBoolean("game.suddendeath.enabled");
		disableHealthRegen = SkyWarsReloaded.get().getConfig().getBoolean("game.suddendeath.disableHealthRegen");
		enableHealthDecay = SkyWarsReloaded.get().getConfig().getBoolean("game.suddendeath.enableHealthDecay");
		
		cooldown = SkyWarsReloaded.get().getConfig().getInt("tauntCooldown");
		
		randPos = SkyWarsReloaded.get().getConfig().getInt("kit.randPos");
		noKitPos = SkyWarsReloaded.get().getConfig().getInt("kit.noKitPos");
		randMat = SkyWarsReloaded.get().getConfig().getString("kit.randItem");
		noKitMat = SkyWarsReloaded.get().getConfig().getString("kit.noKitItem");

		particlesEnabled = SkyWarsReloaded.get().getConfig().getBoolean("particles.enabled");
		ticksPerUpdate = SkyWarsReloaded.get().getConfig().getInt("particles.ticksperupdate");
		
		spectateMenuEnabled= SkyWarsReloaded.get().getConfig().getBoolean("enabledMenus.spectate");
		spectateSlot = SkyWarsReloaded.get().getConfig().getInt("enabledMenus.spectateSlot");
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
		countdown = SkyWarsReloaded.get().getConfig().getString("sounds.countdown");
		joinSound = SkyWarsReloaded.get().getConfig().getString("sounds.join");
		leaveSound = SkyWarsReloaded.get().getConfig().getString("sounds.leave");
		openJoinMenu = SkyWarsReloaded.get().getConfig().getString("sounds.openJoinMenu");
		openSpectateMenu = SkyWarsReloaded.get().getConfig().getString("sounds.openSpectateMenu");
		openOptionsMenu = SkyWarsReloaded.get().getConfig().getString("sounds.openOptionsMenu");
		openGlassMenu = SkyWarsReloaded.get().getConfig().getString("sounds.openGlassMenu");
		openWinSoundMenu = SkyWarsReloaded.get().getConfig().getString("sounds.openWinSoundMenu");
		openKillSoundMenu = SkyWarsReloaded.get().getConfig().getString("sounds.openKillSoundMenu");
		openParticleMenu = SkyWarsReloaded.get().getConfig().getString("sounds.openParticleMenu");
		openProjectileMenu = SkyWarsReloaded.get().getConfig().getString("sounds.openProjectileMenu");
		openTauntMenu = SkyWarsReloaded.get().getConfig().getString("sounds.openTauntMenu");
		openKitMenu = SkyWarsReloaded.get().getConfig().getString("sounds.openKitMenu");
		openChestMenu = SkyWarsReloaded.get().getConfig().getString("sounds.openChestMenu");
		openTimeMenu = SkyWarsReloaded.get().getConfig().getString("sounds.openTimeMenu");
		openWeatherMenu = SkyWarsReloaded.get().getConfig().getString("sounds.openWeatherMenu");
		openModifierMenu = SkyWarsReloaded.get().getConfig().getString("sounds.openModifierMenu");
		confirmSelection = SkyWarsReloaded.get().getConfig().getString("sounds.confirmSelectionSound");
		errorSound = SkyWarsReloaded.get().getConfig().getString("sounds.errorSound");
				
		enabledCommands = SkyWarsReloaded.get().getConfig().getStringList("disable-commands.exceptions");
		disableCommands = SkyWarsReloaded.get().getConfig().getBoolean("disable-commands.enabled");
		
		disableCommandsSpectate = SkyWarsReloaded.get().getConfig().getBoolean("disable-commands-spectate.enabled");
		enabledCommandsSpectate = SkyWarsReloaded.get().getConfig().getStringList("disable-commands-spectate.exceptions");

		for (int i = 0; i < itemNames.size(); i++) {
			String name = itemNames.get(i);
			String def;
			if (SkyWarsReloaded.getNMS().isOnePointEight()) {
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
		SkyWarsReloaded.get().getConfig().set("bungeeMode", bungeeMode);
		SkyWarsReloaded.get().getConfig().set("bungeeLobby", bungeeLobby);
		
		SkyWarsReloaded.get().getConfig().set("resourcepack", resourcePack);
		SkyWarsReloaded.get().getConfig().set("promptForResourcePackOnJoin", promptResource);
		
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
		SkyWarsReloaded.get().getConfig().set("game.gameTimer", gameTimer);
		SkyWarsReloaded.get().getConfig().set("game.waitTimer", waitTimer);
		SkyWarsReloaded.get().getConfig().set("game.timeAfterMatch", timeAfterMatch);
		SkyWarsReloaded.get().getConfig().set("fireworks.per5Ticks", fireworksPer5Tick);
		SkyWarsReloaded.get().getConfig().set("fireworks.enabled", fireworksEnabled);
		SkyWarsReloaded.get().getConfig().set("game.spectateEnabled", spectateEnabled);
		SkyWarsReloaded.get().getConfig().set("game.spectateDistance", spectateDistance);
		SkyWarsReloaded.get().getConfig().set("game.maxMapSize", maxMapSize);
		SkyWarsReloaded.get().getConfig().set("enablePressurePlateJoin", pressurePlate);
		SkyWarsReloaded.get().getConfig().set("teleportToSpawnOnJoin", teleportOnJoin);
		SkyWarsReloaded.get().getConfig().set("teleportToSpawnOnWorldEnter", teleportOnWorldEnter);
		SkyWarsReloaded.get().getConfig().set("game.allowFallDamage", allowFallDamage);
		SkyWarsReloaded.get().getConfig().set("game.kitVotingEnabled", kitVotingEnabled);
		
		SkyWarsReloaded.get().getConfig().set("game.suddendeath.enabled", suddenDeathEnabled);
		SkyWarsReloaded.get().getConfig().set("game.suddendeath.disableHealthRegen", disableHealthRegen);
		SkyWarsReloaded.get().getConfig().set("game.suddendeath.enableHealthDecay", enableHealthDecay);
		
		SkyWarsReloaded.get().getConfig().set("items.kitVotePosition", kitvotepos);
		SkyWarsReloaded.get().getConfig().set("items.kitsEnabled", kitsEnabled);
		SkyWarsReloaded.get().getConfig().set("items.chestVotePosition", chestvotepos);
		SkyWarsReloaded.get().getConfig().set("items.chestVoteEnabled", chestVoteEnabled);
		SkyWarsReloaded.get().getConfig().set("items.timeVotePosition", timevotepos);
		SkyWarsReloaded.get().getConfig().set("items.timeVoteEnabled", timeVoteEnabled);
		SkyWarsReloaded.get().getConfig().set("items.weatherVotePosition", weathervotepos);
		SkyWarsReloaded.get().getConfig().set("items.weatherVoteEnabled", weatherVoteEnabled);
		SkyWarsReloaded.get().getConfig().set("items.modifierVotePosition", modifiervotepos);
		SkyWarsReloaded.get().getConfig().set("items.modifierVoteEnabled", modifierVoteEnabled);
		
		SkyWarsReloaded.get().getConfig().set("tauntCooldown", cooldown);
		
		SkyWarsReloaded.get().getConfig().set("kit.randPos", randPos);
		SkyWarsReloaded.get().getConfig().set("kit.noKitPos", noKitPos);
		SkyWarsReloaded.get().getConfig().set("kit.randItem", randMat);
		SkyWarsReloaded.get().getConfig().set("kit.noKitItem", noKitMat);
		
		SkyWarsReloaded.get().getConfig().set("particles.enabled", particlesEnabled);
		SkyWarsReloaded.get().getConfig().set("particles.ticksperupdate", ticksPerUpdate);
		
		SkyWarsReloaded.get().getConfig().set("enabledMenus.spectate", spectateMenuEnabled);
		SkyWarsReloaded.get().getConfig().set("enabledMenus.spectateSlot", spectateSlot);
		SkyWarsReloaded.get().getConfig().set("enabledMenus.join", joinEnabled);
		SkyWarsReloaded.get().getConfig().set("enabledMenus.joinSlot", joinSlot);
		SkyWarsReloaded.get().getConfig().set("enabledMenus.options", optionsEnabled);
		SkyWarsReloaded.get().getConfig().set("enabledMenus.optionsSlot", optionsSlot);
		SkyWarsReloaded.get().getConfig().set("enabledMenus.glass", glassEnabled);
	    SkyWarsReloaded.get().getConfig().set("enabledMenus.particle", particlesEnabled);
	    SkyWarsReloaded.get().getConfig().set("enabledMenus.projectile", projectEnabled);
	    SkyWarsReloaded.get().getConfig().set("enabledMenus.killsound", killsoundEnabled);
	    SkyWarsReloaded.get().getConfig().set("enabledMenus.winsound", winsoundEnabled);
	    SkyWarsReloaded.get().getConfig().set("enabledMenus.taunts", tauntsEnabled);
		
		SkyWarsReloaded.get().getConfig().set("sounds.enabled", playSounds);
		SkyWarsReloaded.get().getConfig().set("sounds.countdown", countdown);
		SkyWarsReloaded.get().getConfig().set("sounds.join", joinSound);
		SkyWarsReloaded.get().getConfig().set("sounds.leave", leaveSound);
		SkyWarsReloaded.get().getConfig().set("sounds.openJoinMenu", openJoinMenu);
		SkyWarsReloaded.get().getConfig().set("sounds.openSpectateMenu", openSpectateMenu);
		SkyWarsReloaded.get().getConfig().set("sounds.openOptionsMenu", openOptionsMenu);
		SkyWarsReloaded.get().getConfig().set("sounds.openGlassMenu", openGlassMenu);
		SkyWarsReloaded.get().getConfig().set("sounds.openWinSoundMenu", openWinSoundMenu);
		SkyWarsReloaded.get().getConfig().set("sounds.openKillSoundMenu", openKillSoundMenu);
		SkyWarsReloaded.get().getConfig().set("sounds.openParticleMenu", openParticleMenu);
		SkyWarsReloaded.get().getConfig().set("sounds.openProjectileMenu", openProjectileMenu);
		SkyWarsReloaded.get().getConfig().set("sounds.openTauntMenu", openTauntMenu);
		SkyWarsReloaded.get().getConfig().set("sounds.openKitMenu", openKitMenu);
		SkyWarsReloaded.get().getConfig().set("sounds.openChestMenu", openChestMenu);
		SkyWarsReloaded.get().getConfig().set("sounds.openTimeMenu", openTimeMenu);
		SkyWarsReloaded.get().getConfig().set("sounds.openWeatherMenu", openWeatherMenu);
		SkyWarsReloaded.get().getConfig().set("sounds.openModifierMenu", openModifierMenu);
		SkyWarsReloaded.get().getConfig().set("sounds.confirmSelectionSound", confirmSelection);
		SkyWarsReloaded.get().getConfig().set("sounds.errorSound", errorSound);
			
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

	public boolean soundsEnabled() {
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
	
	public boolean joinMenuEnabled() {
		return joinEnabled;
	}
	
	public int getJoinSlot() {
		return joinSlot;
	}
	
	public boolean spectateMenuEnabled() {
		return spectateMenuEnabled;
	}
	
	public int getSpectateSlot() {
		return spectateSlot;
	}

	public boolean teleportOnJoin() {
		return teleportOnJoin;
	}
	
	public boolean teleportOnWorldEnter() {
		return teleportOnWorldEnter;
	}

	public int getRandPos() {
		return randPos;
	}
	
	public Material getRandMat() {
		Material mat = Material.valueOf(randMat);
		if (mat == null) {
			return Material.NETHER_STAR;
		}
		return mat;
	}
	
	public int getNoKitPos() {
		return noKitPos;
	}
	
	public Material getNoKitMat() {
		Material mat = Material.valueOf(noKitMat);
		if (mat == null) {
			return Material.GLASS;
		}
		return mat;
	}
	
	public boolean promptForResource() {
		return promptResource;
	}
	
	public String getResourceLink() {
		return resourcePack;
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

	public long getTicksPerUpdate() {
		return ticksPerUpdate;
	}

	public boolean allowFallDamage() {
		return allowFallDamage;
	}

	public boolean kitVotingEnabled() {
		return kitVotingEnabled;
	}
	
	public int getKitVotePos() {
		return kitvotepos;
	}
	
	public boolean areKitsEnabled() {
		return kitsEnabled;
	}
	
	public int getChestVotePos() {
		return chestvotepos;
	}
	
	public boolean isChestVoteEnabled() {
		return chestVoteEnabled;
	}
	
	public int getTimeVotePos() {
		return timevotepos;
	}
	
	public boolean isTimeVoteEnabled() {
		return timeVoteEnabled;
	}
	
	public int getWeatherVotePos() {
		return weathervotepos;
	}
	
	public boolean isWeatherVoteEnabled() {
		return weatherVoteEnabled;
	}
	
	public int getModifierVotePos() {
		return modifiervotepos;
	}
	
	public boolean isModifierVoteEnabled() {
		return modifierVoteEnabled;
	}

	public boolean lobbyBoardEnabled() {
		return lobbyBoardEnabled;
	}
	
	public String getJoinSound() {
		return joinSound;
	}
	
	public String getLeaveSound() {
		return leaveSound;
	}

	public String getOpenOptionsMenuSound() {
		return openOptionsMenu;
	}
	
	public String getOpenJoinMenuSound() {
		return openJoinMenu;
	}
	
	public String getOpenSpectateMenuSound() {
		return openSpectateMenu;
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
	
	public String getOpenGlassMenuSound() {
		return openGlassMenu;
	}
	
	public String getOpenTauntMenuSound() {
		return openTauntMenu;
	}

	public String getOpenKitMenuSound() {
		return openKitMenu;
	}
	
	public String getOpenChestMenuSound() {
		return openChestMenu;
	}
	
	public String getOpenTimeMenuSound() {
		return openTimeMenu;
	}
	
	public String getOpenWeatherMenuSound() {
		return openWeatherMenu;
	}
	
	public String getOpenModifierMenuSound() {
		return openModifierMenu;
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
}


