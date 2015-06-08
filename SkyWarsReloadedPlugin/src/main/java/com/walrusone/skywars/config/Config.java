package com.walrusone.skywars.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.utilities.ItemUtils;
import com.walrusone.skywars.utilities.Messaging;

public class Config {

	private boolean signJoinMode;
	private String signJoinMaterial;
	private boolean pressurePlateJoin;
	private boolean enableLogFilter;
	private boolean allowSpectating;
	private boolean useExternalEconomy;
	private boolean disableWinBroadcast;
	private boolean noFallDamage;
	private int maxMapSize;
	private int maxNumberOfGames;
    private int maxGameLength;
	private int minPercentPlayers;
	private boolean resetPreGameTimerOnJoin;
	private int timeAfterGame;
	private int preGameTimer;
	private int resetPreGameTimerThreshold;
	private boolean clearInventoryOnJoin;
	private boolean clearXPOnJoin;
	private boolean clearPotionEffectsOnJoin;
	private boolean sendPlayersToSpawnOnJoin;
	private boolean playerNameScoreboard;
	private int statsCommandCooldown;
	private boolean lobbyScoreBoardEnabled;
	private boolean addScorePrefix;
	private boolean useExternalChat;
	
	private boolean kitsEnabled;
	private boolean shopsEnabled;
	private boolean spectateShopEnabled;
	private boolean opChestsEnabled;
	private boolean timeVoteEnabled;
	private boolean jumpVoteEnabled;
	private int highJumpValue;
	private int superJumpValue;
	private int godJumpValue;
	private boolean weatherVoteEnabled;
	private int lightningStrikeChance;
	private boolean colorGlassEnabled;
	private boolean particlesEnabled;
	private boolean trailEffectsEnabled;
	private boolean lobbyMenuEnabled;
	private boolean optionsMenuEnabled;
	private boolean purchaseColorGlassEnabled;
	private boolean purchaseParticlesEnabled;
	private boolean purchaseTrailEffectsEnabled;
	private boolean purchasePermanentKitsEnabled;
	private boolean showKitItemsandPotionEffects;
	private boolean doubleChestAlwaysOP;
	
	private int pointsPerKill;
	private int pointsLostPerDeath;
	private int pointsLostPerLeave;
	private int pointsPerWin;
	private int vipMultiplier;
		
	private boolean giveSpectateItem;
	private int spectateItemSlot;
	private boolean giveLobbyMenuItem;
	private int lobbyMenuItemSlot;
	private boolean giveJoinItem;
	private int joinItemSlot;
	private ItemStack lobbyMenuItem;
	private ItemStack spectateItem;
	private ItemStack spectatePlayerItem;
	private ItemStack joinItem;
	private ItemStack exitGameItem;
	private int exitItemSlot;
	private ItemStack kitMenuItem;
	private int kitMenuItemSlot;
	private ItemStack kitPermMenuItem;
	private int kitPermMenuItemSlot;
	private ItemStack specStoreItem;
	private int specStoreItemSlot;
	private ItemStack opChestMenuItem;
	private int opChestOptionMenuSlot;
	private ItemStack timeMenuItem;
	private int timeOptionMenuSlot;
	private ItemStack weatherMenuItem;
	private int weatherOptionMenuSlot;
	private ItemStack jumpMenuItem;
	private int jumpOptionMenuSlot;
	private ItemStack particleMenuItem;
	private int particleLobbyMenuSlot;
	private int particleOptionsMenuSlot;
	private ItemStack trailEffectMenuItem;
	private int trailEffectLobbyMenuSlot;
	private int trailEffectOptionsMenuSlot;
	private ItemStack optionsItem;
	private int optionsItemSlot;
	private ItemStack glassMenuItem;
	private int glassLobbyMenuSlot;
	private int glassOptionsMenuSlot;
	
	private Sound playerJoinLobby;
	private Sound playerLeaveLobby;
	private Sound playerDeath;
	private Sound playerWin;
	private Sound opChestVote;
	private Sound timeVote;
	private Sound weatherVote;
	private Sound jumpVote;
	
	private boolean lobbyGuard;
	private boolean hungerDisabled;
	private boolean damageDisabled;
	private boolean pvpDisabled;
	private boolean playerBuildDisabled;
	private boolean playerInteractDisabled;
	private boolean fallDamageDisabled;
	private boolean weatherDisabled;
	private boolean daylightCycleDisabled;
	
	private String world;
	private int x;
	private int y;
	private int z;
	private float yaw;
	private float pitch;
	
	private List<String> commandWhiteList = new ArrayList<String>();
	private List<String> spectatorWhiteList = new ArrayList<String>();
	
	private boolean bungeeModeEnabled;
	private String bungeeServerName;
	private String lobbyServer;
	
	public Config() {
		load();
	}
	
	private void load() {
		if (SkyWarsReloaded.get().getConfig().getString("gameVariables.signJoinMaterial") != null) {
			copyOldConfigValues();
		} else {
			signJoinMode = SkyWarsReloaded.get().getConfig().getBoolean("gameSettings.signJoinMode");
			signJoinMaterial = SkyWarsReloaded.get().getConfig().getString("gameSettings.signJoinMaterial");
			pressurePlateJoin = SkyWarsReloaded.get().getConfig().getBoolean("gameSettings.pressurePlateJoin");
			enableLogFilter = SkyWarsReloaded.get().getConfig().getBoolean("gameSettings.enableLogFilter");
			allowSpectating = SkyWarsReloaded.get().getConfig().getBoolean("gameSettings.allowSpectating");
			useExternalEconomy = SkyWarsReloaded.get().getConfig().getBoolean("gameSettings.useExternalEconomy");
			disableWinBroadcast = SkyWarsReloaded.get().getConfig().getBoolean("gameSettings.disableWinBroadcast");
			noFallDamage = SkyWarsReloaded.get().getConfig().getBoolean("gameSettings.noFallDamage");
			minPercentPlayers = SkyWarsReloaded.get().getConfig().getInt("gameSettings.minPercentPlayers");
			preGameTimer = SkyWarsReloaded.get().getConfig().getInt("gameSettings.preGameTimer");
			resetPreGameTimerOnJoin = SkyWarsReloaded.get().getConfig().getBoolean("gameSettings.resetPreGameTimerOnJoin");
			resetPreGameTimerThreshold = SkyWarsReloaded.get().getConfig().getInt("gameSettings.resetPreGameTimerThreshold");
			maxMapSize = SkyWarsReloaded.get().getConfig().getInt("gameSettings.maxMapSize");
			maxNumberOfGames = SkyWarsReloaded.get().getConfig().getInt("gameSettings.maxNumberOfGames");
			timeAfterGame = SkyWarsReloaded.get().getConfig().getInt("gameSettings.timeAfterGame");
			playerNameScoreboard = SkyWarsReloaded.get().getConfig().getBoolean("gameSettings.playerNameScoreboard");
			lobbyScoreBoardEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameSettings.lobbyScoreBoardEnabled");
			statsCommandCooldown =  SkyWarsReloaded.get().getConfig().getInt("gameSettings.statsCommandCooldown");
			clearInventoryOnJoin =  SkyWarsReloaded.get().getConfig().getBoolean("gameSettings.clearInventoryOnJoin");
			clearXPOnJoin =  SkyWarsReloaded.get().getConfig().getBoolean("gameSettings.clearXPOnJoin");
			clearPotionEffectsOnJoin =  SkyWarsReloaded.get().getConfig().getBoolean("gameSettings.clearPotionEffectsOnJoin");
			sendPlayersToSpawnOnJoin =  SkyWarsReloaded.get().getConfig().getBoolean("gameSettings.sendPlayersToSpawnOnJoin");
			addScorePrefix = SkyWarsReloaded.get().getConfig().getBoolean("gameSettings.addScorePrefix");
			useExternalChat = SkyWarsReloaded.get().getConfig().getBoolean("gameSettings.useExternalChat");
			
			pointsPerKill = SkyWarsReloaded.get().getConfig().getInt("points.pointsPerKill");
			pointsLostPerDeath = SkyWarsReloaded.get().getConfig().getInt("points.pointsLostPerDeath");
			pointsLostPerLeave = SkyWarsReloaded.get().getConfig().getInt("points.pointsLostPerLeave");
			pointsPerWin = SkyWarsReloaded.get().getConfig().getInt("points.pointsPerWin");
			vipMultiplier = SkyWarsReloaded.get().getConfig().getInt("points.vipMultiplier");
			
			kitsEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameOptions.kitsEnabled");
			shopsEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameOptions.shopsEnabled");
			spectateShopEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameOptions.spectateShopEnabled");
			opChestsEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameOptions.opChestsEnabled");
			timeVoteEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameOptions.timeVoteEnabled");
			jumpVoteEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameOptions.jumpVoteEnabled");
			highJumpValue = SkyWarsReloaded.get().getConfig().getInt("gameOptions.highJumpValue");
			superJumpValue = SkyWarsReloaded.get().getConfig().getInt("gameOptions.superJumpValue");
			godJumpValue = SkyWarsReloaded.get().getConfig().getInt("gameOptions.godJumpValue");
			weatherVoteEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameOptions.weatherVoteEnabled");
			lightningStrikeChance = SkyWarsReloaded.get().getConfig().getInt("gameOptions.lightningStrikeChance");
			colorGlassEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameOptions.colorGlassEnabled");
			particlesEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameOptions.particlesEnabled");
			trailEffectsEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameOptions.trailEffectsEnabled");
			lobbyMenuEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameOptions.lobbyMenuEnabled");
			optionsMenuEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameOptions.optionsMenuEnabled");
			purchaseColorGlassEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameOptions.purchaseColorGlassEnabled");
			purchaseParticlesEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameOptions.purchaseParticlesEnabled");
			purchaseTrailEffectsEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameOptions.purchaseTrailEffectsEnabled");
			purchasePermanentKitsEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameOptions.purchasePermanentKitsEnabled");
		}
		resetPreGameTimerThreshold = SkyWarsReloaded.get().getConfig().getInt("gameSettings.resetPreGameTimerThreshold");
		sendPlayersToSpawnOnJoin =  SkyWarsReloaded.get().getConfig().getBoolean("gameSettings.sendPlayersToSpawnOnJoin");
		int length = SkyWarsReloaded.get().getConfig().getInt("gameSettings.maxGameLength");
		if (length != -1 && length > 0) {
		    maxGameLength = SkyWarsReloaded.get().getConfig().getInt("gameSettings.maxGameLength") * 60;
		} else {
			maxGameLength = Integer.MAX_VALUE;
		}
		
		showKitItemsandPotionEffects = SkyWarsReloaded.get().getConfig().getBoolean("gameOptions.showKitItemsandPotionEffects");
		doubleChestAlwaysOP = SkyWarsReloaded.get().getConfig().getBoolean("gameOptions.doubleChestAlwaysOP");

		
		giveSpectateItem = SkyWarsReloaded.get().getConfig().getBoolean("gameItems.giveSpectateItem");
		spectateItemSlot = SkyWarsReloaded.get().getConfig().getInt("gameItems.spectateItemSlot");
		giveLobbyMenuItem = SkyWarsReloaded.get().getConfig().getBoolean("gameItems.giveLobbyMenuItem");
		lobbyMenuItemSlot = SkyWarsReloaded.get().getConfig().getInt("gameItems.lobbyMenuItemSlot");
		giveJoinItem = SkyWarsReloaded.get().getConfig().getBoolean("gameItems.giveJoinItem");
		joinItemSlot = SkyWarsReloaded.get().getConfig().getInt("gameItems.joinItemSlot");
		exitItemSlot = SkyWarsReloaded.get().getConfig().getInt("gameItems.exitItemSlot");
		kitMenuItemSlot = SkyWarsReloaded.get().getConfig().getInt("gameItems.kitMenuItemSlot");
		kitPermMenuItemSlot = SkyWarsReloaded.get().getConfig().getInt("gameItems.kitPermMenuItemSlot");
		specStoreItemSlot = SkyWarsReloaded.get().getConfig().getInt("gameItems.specStoreItemSlot");
		opChestOptionMenuSlot = SkyWarsReloaded.get().getConfig().getInt("gameItems.opChestOptionMenuSlot");
		timeOptionMenuSlot = SkyWarsReloaded.get().getConfig().getInt("gameItems.timeOptionMenuSlot");
		weatherOptionMenuSlot = SkyWarsReloaded.get().getConfig().getInt("gameItems.weatherOptionMenuSlot");
		jumpOptionMenuSlot = SkyWarsReloaded.get().getConfig().getInt("gameItems.jumpOptionMenuSlot");
		particleLobbyMenuSlot = SkyWarsReloaded.get().getConfig().getInt("gameItems.particleLobbyMenuSlot");
		particleOptionsMenuSlot = SkyWarsReloaded.get().getConfig().getInt("gameItems.particleOptionsMenuSlot");
		trailEffectLobbyMenuSlot = SkyWarsReloaded.get().getConfig().getInt("gameItems.trailEffectLobbyMenuSlot");
		trailEffectOptionsMenuSlot = SkyWarsReloaded.get().getConfig().getInt("gameItems.trailEffectOptionsMenuSlot");
		optionsItemSlot = SkyWarsReloaded.get().getConfig().getInt("gameItems.optionsItemSlot");
		glassLobbyMenuSlot = SkyWarsReloaded.get().getConfig().getInt("gameItems.glassLobbyMenuSlot");
		glassOptionsMenuSlot = SkyWarsReloaded.get().getConfig().getInt("gameItems.glassOptionsMenuSlot");
		
		String specItem = SkyWarsReloaded.get().getConfig().getString("gameItems.spectateItem");
		List<String> specItemData = new LinkedList<String>(Arrays.asList(specItem.split(" ")));
		String name = "name:" + new Messaging.MessageFormatter().format("menu.spectategame-item-name");
		specItemData.add(name);
		spectateItem = ItemUtils.parseItem(specItemData);
		
		String specPlayerItem = SkyWarsReloaded.get().getConfig().getString("gameItems.spectateItem");
		List<String> specPlayerItemData = new LinkedList<String>(Arrays.asList(specPlayerItem.split(" ")));
		String specPlayerName = "name:" + new Messaging.MessageFormatter().format("menu.spectate-item-name");
		specPlayerItemData.add(specPlayerName);
		spectatePlayerItem = ItemUtils.parseItem(specPlayerItemData);
		
		String joinItemName = SkyWarsReloaded.get().getConfig().getString("gameItems.joinItem");
		List<String> joinItemData = new LinkedList<String>(Arrays.asList(joinItemName.split(" ")));
		String joinName = "name:" + new Messaging.MessageFormatter().format("menu.join-item-name");
		joinItemData.add(joinName);
		joinItem = ItemUtils.parseItem(joinItemData);
		
		String kitItemName = SkyWarsReloaded.get().getConfig().getString("gameItems.kitMenuItem");
		List<String> kitItemData = new LinkedList<String>(Arrays.asList(kitItemName.split(" ")));
		String kitName = "name:" + new Messaging.MessageFormatter().format("menu.kit-item-name");
		kitItemData.add(kitName);
		kitMenuItem = ItemUtils.parseItem(kitItemData);
		
		String exitItem = SkyWarsReloaded.get().getConfig().getString("gameItems.exitGameItem");
		List<String> exitItemData = new LinkedList<String>(Arrays.asList(exitItem.split(" ")));
		String exitName = "name:" + new Messaging.MessageFormatter().format("menu.returntospawn-item-name");
		exitItemData.add(exitName);
		exitGameItem = ItemUtils.parseItem(exitItemData);
		
		String optionsItemName = SkyWarsReloaded.get().getConfig().getString("gameItems.optionsItem");
		List<String> optionsItemData = new LinkedList<String>(Arrays.asList(optionsItemName.split(" ")));
		String optionsName = "name:" + new Messaging.MessageFormatter().format("menu.options-item-name");
		optionsItemData.add(optionsName);
		optionsItem = ItemUtils.parseItem(optionsItemData);
		
		String lobbyMenuItemName = SkyWarsReloaded.get().getConfig().getString("gameItems.lobbyMenuItem");
		List<String> lobbyMenuItemData = new LinkedList<String>(Arrays.asList(lobbyMenuItemName.split(" ")));
		String lobbyMenuName = "name:" + new Messaging.MessageFormatter().format("menu.lobbymenu-item-name");
		lobbyMenuItemData.add(lobbyMenuName);
		lobbyMenuItem = ItemUtils.parseItem(lobbyMenuItemData);
		
		String opVoteItem = SkyWarsReloaded.get().getConfig().getString("gameItems.opChestMenuItem");
		List<String> opVoteItemData = new LinkedList<String>(Arrays.asList(opVoteItem.split(" ")));
		opChestMenuItem = ItemUtils.parseItem(opVoteItemData);
		
		String timeVoteItem = SkyWarsReloaded.get().getConfig().getString("gameItems.timeMenuItem");
		List<String> timeVoteItemData = new LinkedList<String>(Arrays.asList(timeVoteItem.split(" ")));
		timeMenuItem = ItemUtils.parseItem(timeVoteItemData);
		
		String jumpVoteItem = SkyWarsReloaded.get().getConfig().getString("gameItems.jumpMenuItem");
		List<String> jumpVoteItemData = new LinkedList<String>(Arrays.asList(jumpVoteItem.split(" ")));
		jumpMenuItem = ItemUtils.parseItem(jumpVoteItemData);
		
		String colorItem = SkyWarsReloaded.get().getConfig().getString("gameItems.glassMenuItem");
		List<String> colorItemData = new LinkedList<String>(Arrays.asList(colorItem.split(" ")));
		glassMenuItem = ItemUtils.parseItem(colorItemData);
		
		String particleItem = SkyWarsReloaded.get().getConfig().getString("gameItems.particleMenuItem");
		List<String> particleItemData = new LinkedList<String>(Arrays.asList(particleItem.split(" ")));
		particleMenuItem = ItemUtils.parseItem(particleItemData);
		
		String projEffectItem = SkyWarsReloaded.get().getConfig().getString("gameItems.trailEffectMenuItem");
		List<String> projEffectItemData = new LinkedList<String>(Arrays.asList(projEffectItem.split(" ")));
		trailEffectMenuItem = ItemUtils.parseItem(projEffectItemData);
		
		String weatherItem = SkyWarsReloaded.get().getConfig().getString("gameItems.weatherMenuItem");
		List<String> weatherItemData = new LinkedList<String>(Arrays.asList(weatherItem.split(" ")));
		weatherMenuItem = ItemUtils.parseItem(weatherItemData);
		
		String permKitItem = SkyWarsReloaded.get().getConfig().getString("gameItems.kitPermMenuItem");
		List<String> permKitItemData = new LinkedList<String>(Arrays.asList(permKitItem.split(" ")));
		kitPermMenuItem = ItemUtils.parseItem(permKitItemData);
		
		String specShItem = SkyWarsReloaded.get().getConfig().getString("gameItems.specStoreItem");
		List<String> specShopItemData = new LinkedList<String>(Arrays.asList(specShItem.split(" ")));
		String specShopName = "name:" + new Messaging.MessageFormatter().format("menu.spectateshop-item-name");
		specShopItemData.add(specShopName);
		specStoreItem = ItemUtils.parseItem(specShopItemData);
		
		
		playerJoinLobby =  Sound.valueOf(SkyWarsReloaded.get().getConfig().getString("gameSounds.playerJoinLobby").toUpperCase());
		playerLeaveLobby = Sound.valueOf(SkyWarsReloaded.get().getConfig().getString("gameSounds.playerLeaveLobby").toUpperCase());
		playerDeath = Sound.valueOf(SkyWarsReloaded.get().getConfig().getString("gameSounds.playerDeath").toUpperCase());
		playerWin = Sound.valueOf(SkyWarsReloaded.get().getConfig().getString("gameSounds.playerWin").toUpperCase());
		opChestVote = Sound.valueOf(SkyWarsReloaded.get().getConfig().getString("gameSounds.opChestVote").toUpperCase());
		timeVote = Sound.valueOf(SkyWarsReloaded.get().getConfig().getString("gameSounds.timeVote").toUpperCase());
		weatherVote = Sound.valueOf(SkyWarsReloaded.get().getConfig().getString("gameSounds.weatherVote").toUpperCase());
		jumpVote = Sound.valueOf(SkyWarsReloaded.get().getConfig().getString("gameSounds.jumpVote").toUpperCase());
		
		lobbyGuard = SkyWarsReloaded.get().getConfig().getBoolean("lobbyGuard.enabled");
		hungerDisabled = SkyWarsReloaded.get().getConfig().getBoolean("lobbyGuard.hungerDisabled");
		fallDamageDisabled = SkyWarsReloaded.get().getConfig().getBoolean("lobbyGuard.fallDamageDisabled");
		damageDisabled = SkyWarsReloaded.get().getConfig().getBoolean("lobbyGuard.damageDisabled");
		pvpDisabled = SkyWarsReloaded.get().getConfig().getBoolean("lobbyGuard.pvpDisabled");
		playerBuildDisabled = SkyWarsReloaded.get().getConfig().getBoolean("lobbyGuard.playerBuildDisabled");
		playerInteractDisabled = SkyWarsReloaded.get().getConfig().getBoolean("lobbyGuard.damageDisabled");
		weatherDisabled = SkyWarsReloaded.get().getConfig().getBoolean("lobbyGuard.weatherDisabled");
		daylightCycleDisabled = SkyWarsReloaded.get().getConfig().getBoolean("lobbyGuard.daylightCycleDisabled");
		
		world = SkyWarsReloaded.get().getConfig().getString("spawn.world");
		x = SkyWarsReloaded.get().getConfig().getInt("spawn.x");
		y = SkyWarsReloaded.get().getConfig().getInt("spawn.y");
		z = SkyWarsReloaded.get().getConfig().getInt("spawn.z");
		yaw = SkyWarsReloaded.get().getConfig().getInt("spawn.yaw");
		pitch = SkyWarsReloaded.get().getConfig().getInt("spawn.pitch");
		
		commandWhiteList = SkyWarsReloaded.get().getConfig().getStringList("commandWhitelist");
		spectatorWhiteList = SkyWarsReloaded.get().getConfig().getStringList("spectatorWhitelist");
		
		
		bungeeModeEnabled = SkyWarsReloaded.get().getConfig().getBoolean("bungeeMode.enabled");
		lobbyServer = SkyWarsReloaded.get().getConfig().getString("bungeeMode.lobbyServer");
		bungeeServerName = SkyWarsReloaded.get().getConfig().getString("bungeeMode.bungeeServerName");
		
		validateConfigValues();
	}
	
	public void setSpawn(Location loc) {
		world = loc.getWorld().getName().toString();
		x = loc.getBlockX();
		y = loc.getBlockY();
		z = loc.getBlockZ();
		yaw = loc.getYaw();
		pitch = loc.getPitch();
        if (daylightCycleDisabled()) {
			if (getSpawn() != null) {
				World world = getSpawn().getWorld();
				world.setTime(6000);
				world.setGameRuleValue("doDaylightCycle", "false");
				world.setStorm(false);
				world.setThundering(false);
			}
        }
		SkyWarsReloaded.get().getConfig().set("spawn.world", world);
		SkyWarsReloaded.get().getConfig().set("spawn.x", x);
		SkyWarsReloaded.get().getConfig().set("spawn.y", y);
		SkyWarsReloaded.get().getConfig().set("spawn.z", z);
		SkyWarsReloaded.get().getConfig().set("spawn.yaw", yaw);
		SkyWarsReloaded.get().getConfig().set("spawn.pitch", pitch);
		SkyWarsReloaded.get().saveConfig();
	}
	
	public boolean lobbyGuard() {
			return lobbyGuard;
	}
	
	public boolean hungerDisabled() {
		if (lobbyGuard) {
			return hungerDisabled;
		}
		return false;
	}
	
	public boolean damageDisabled() {
		if (lobbyGuard) {
			return damageDisabled;
		}
		return false;
	}
	
	public boolean pvpDisabled() {
		if (lobbyGuard) {
			return pvpDisabled;
		}
		return false;
	}
	
	public boolean playerBuildDisabled() {
		if (lobbyGuard) {
			return playerBuildDisabled;
		}
		return false;
	}
	
	public boolean playerInteractDisabled() {
		if (lobbyGuard) {
			return playerInteractDisabled;
		}
		return false;
	}
	
	public boolean weatherDisabled() {
		if (lobbyGuard) {
			return weatherDisabled;
		}
		return false;
	}
	
	public boolean daylightCycleDisabled() {
		if (lobbyGuard) {
			return daylightCycleDisabled;
		}
		return false;
	}
	
	public List<String> getSpectatorWhiteList() {
		return spectatorWhiteList;
	}
	
	public List<String> getCommandWhiteList() {
		return commandWhiteList;
	}
	
	public Location getSpawn() {
		if (world != null) {
		    World checkLoaded = SkyWarsReloaded.get().getServer().getWorld(world);
		    if(checkLoaded != null) {
				return new Location(SkyWarsReloaded.get().getServer().getWorld(world), x, y, z, yaw, pitch);
		    } else {
		    	return null;
		    }
		} else {
			return null;
		}
	}
	public Sound getJumpVoteSound() {
		return jumpVote;
	}
	
	public Sound getWeatherVoteSound() {
		return weatherVote;
	}
	
	public Sound getTimeVoteSound() {
		return timeVote;
	}
	
	public Sound getChestVoteSound() {
		return opChestVote;
	}
	
	public Sound getJoinSound() {
		return playerJoinLobby;
	}
	
	public Sound getLeaveSound() {
		return playerLeaveLobby;
	}
	
	public Sound getDeathSound() {
		return playerDeath;
	}
	
	public Sound getWinSound() {
		return playerWin;
	}
	
	public ItemStack getSpecStoreMenuItem() {
		return specStoreItem.clone();
	}
	
	public ItemStack getPermKitMenuItem() {
		return kitPermMenuItem.clone();
	}
	
	public ItemStack getWeatherMenuItem() {
		return weatherMenuItem.clone();
	}
	public ItemStack getTrailEffectsMenuItem() {
		return trailEffectMenuItem.clone();
	}
	
	public ItemStack getparticleMenuItem() {
		return particleMenuItem.clone();
	}
	
	public ItemStack getGlassMenuItem() {
		return glassMenuItem.clone();
	}
	
	public ItemStack getJumpMenuItem() {
		return jumpMenuItem.clone();
	}
	
	public ItemStack getTimeMenuItem() {
		return timeMenuItem.clone();
	}
	
	public ItemStack getOpChestMenuItem() {
		return opChestMenuItem.clone();
	}
	
	public ItemStack getLobbyMenuItem() {
		return lobbyMenuItem.clone();
	}
	
	public ItemStack getOptionsItem() {
		return optionsItem.clone();
	}
	
	public ItemStack getExitGameItem() {
		return exitGameItem.clone();
	}
	
	public ItemStack getKitMenuItem() {
		return kitMenuItem.clone();
	}
	
	public ItemStack getJoinItem() {
		return joinItem.clone();
	}
	
	public ItemStack getSpectateItem() {
		return spectateItem.clone();
	}
	
	public int getJoinMenuSlot() {
		return joinItemSlot;
	}
	
	public int getExitItemSlot() {
		return exitItemSlot;
	}
	
	public int getKitMenuItemSlot() {
		return kitMenuItemSlot;
	}
	
	public int getKitPermMenuItemSlot() {
		return kitPermMenuItemSlot;
	}
	
	public int getSpecStoreItemSlot() {
		return specStoreItemSlot;
	}
	
	public int getOpChestOptionMenuSlot() {
		return opChestOptionMenuSlot;
	}
	
	public int getTimeOptionMenuSlot() {
		return timeOptionMenuSlot;
	}
	
	public int getWeatherOptionMenuSlot() {
		return weatherOptionMenuSlot;
	}
	
	public int getJumpOptionMenuSlot() {
		return jumpOptionMenuSlot;
	}
	
	public int getParticleLobbyMenuSlot() {
		return particleLobbyMenuSlot;
	}
	
	public int getParticleOptionsMenuSlot() {
		return particleOptionsMenuSlot;
	}
	
	public int getTrailEffectLobbyMenuSlot() {
		return trailEffectLobbyMenuSlot;
	}
	
	public int getTrailEffectOptionsMenuSlot() {
		return trailEffectOptionsMenuSlot;
	}
	
	public int getOptionsItemSlot() {
		return optionsItemSlot;
	}
	
	public int getGlassLobbyMenuSlot() {
		return glassLobbyMenuSlot;
	}
	
	public int getGlassOptionsMenuSlot() {
		return glassOptionsMenuSlot;
	}

	public boolean giveJoinMenuItem() {
		return giveJoinItem;
	}
	
	public int getLobbyMenuSlot() {
		return lobbyMenuItemSlot;
	}
	
	public boolean giveLobbyMenuItem() {
		return giveLobbyMenuItem;
	}
	
	public int getSpectateItemSlot() {
		return spectateItemSlot;
	}
	
	public boolean giveSpectateItem() {
		return giveSpectateItem;
	}
		
	public boolean purchasePermanentKitsEnabled() {
		return purchasePermanentKitsEnabled;
	}
	
	public boolean showKitItemsandPotionEffects() {
		return showKitItemsandPotionEffects;
	}
	
	public boolean doubleChestAlwaysOP() {
		return doubleChestAlwaysOP;
	}
	
	public boolean purchaseTrailEffectsEnabled() {
		return purchaseTrailEffectsEnabled;
	}
	
	public boolean purchaseParticlesEnabled() {
		return purchaseParticlesEnabled;
	}
	
	public boolean purchaseColorClassEnabled() {
		return purchaseColorGlassEnabled;
	}
	
	public boolean optionsMenuEnabled() {
		return optionsMenuEnabled;
	}
	
	public boolean lobbyMenuEnabled() {
		return lobbyMenuEnabled;
	}
	
	public boolean trailEffectsEnabled() {
		return trailEffectsEnabled;
	}
	
	public boolean particlesEnabled() {
		return particlesEnabled;
	}
	
	public boolean colorGlassEnabled() {
		return colorGlassEnabled;
	}
	
	public int getStrikeChance() {
		return lightningStrikeChance;
	}
	
	public int getMaxGameLength() {
		return maxGameLength;
	}
	
	public boolean weatherVoteEnabled() {
		return weatherVoteEnabled;
	}
	
	public int getGodJumpValue() {
		return godJumpValue;
	}
	
	public int getSuperJumpValue() {
		return superJumpValue;
	}
	
	public int getHighJumpValue() {
		return highJumpValue;
	}
	
	public boolean jumpVoteEnabled() {
		return jumpVoteEnabled;
	}
	
	public boolean timeVoteEnabled() {
		return timeVoteEnabled;
	}
	
	public boolean opChestsEnabled() {
		return opChestsEnabled;
	}
	
	public boolean spectateShopEnabled() {
		return spectateShopEnabled;
	}
	
	public boolean shopsEnabled() {
		return shopsEnabled;
	}
	
	public boolean kitsEnabled() {
		return kitsEnabled;
	}
	
	public int getKillValue() {
		return pointsPerKill;
	}
	
	public int getDeathValue() {
		return pointsLostPerDeath;
	}
	
	public int getLeaveValue() {
		return pointsLostPerLeave;
	}
	
	public int getWinValue() {
		return pointsPerWin;
	}
	
	public int getVIPMultiplier() {
		return vipMultiplier;
	}
	
	public int getMinPercentPlayers() {
		return minPercentPlayers;
	}
	
	public int preGameTimer() {
		return preGameTimer;
	}
	
	public boolean resetTimerEnabled() {
		return resetPreGameTimerOnJoin;
	}
	
	public int getResetTimerThreshold() {
		return resetPreGameTimerThreshold;
	}
	
	public int getMaxMapSize() {
		return maxMapSize;
	}
	
	public int getMaxNumberOfGames() {
		return maxNumberOfGames;
	}
	
	public int getTimeAfterGame() {
		return timeAfterGame;
	}
	
	public boolean usePlayerNames() {
		return playerNameScoreboard;
	}
	
	public boolean LobbyScoreboardEnabeld() {
		return lobbyScoreBoardEnabled;
	}
	
	public int getStatsCommandCooldown() {
		return statsCommandCooldown;
	}
	
	public boolean clearInventoryOnJoin() {
		return clearInventoryOnJoin;
	}
	
	public boolean clearXPOnJoin() {
		return clearXPOnJoin;
	}
	
	public boolean clearPotionEffectsOnJoin() {
		return clearPotionEffectsOnJoin;
	}
	
	public boolean useExternalChat() {
		return useExternalChat;
	}
	
	public boolean addScorePrefix() {
		return addScorePrefix;
	}
	
	public boolean signJoinMode() {
		return signJoinMode;
	}
	
	public String getSignJoinMaterial() {
		return signJoinMaterial;
	}
	
	public boolean logFilterEnabled() {
		return enableLogFilter;
	}
	
	public boolean spectatingEnabled() {
		return allowSpectating;
	}
	
	public boolean pressurePlateJoin() {
		return pressurePlateJoin;
	}
	
	public boolean usingExternalEcomony() {
		return useExternalEconomy;
	}
	
	public boolean WinBroadcastDisabled() {
		return disableWinBroadcast;
	}
	
	public boolean FallDamageDisabled() {
		return noFallDamage;
	}
	
	public boolean LobbyFallDamageDisabled() {
		if (lobbyGuard) {
			return fallDamageDisabled;
		}
		return false;
	}
	
	public boolean bungeeEnabled() {
		return bungeeModeEnabled;
	}

	public String getLobbyServer() {
		return lobbyServer;
	}
	
	public String getBungeeServer() {
		return bungeeServerName;
	}

	public ItemStack getSpectatePlayerItem() {
		return spectatePlayerItem.clone();
	}
	
	private void copyOldConfigValues() {
		signJoinMode = SkyWarsReloaded.get().getConfig().getBoolean("signJoinMode");
		signJoinMaterial = SkyWarsReloaded.get().getConfig().getString("gameVariables.signJoinMaterial");
		pressurePlateJoin = SkyWarsReloaded.get().getConfig().getBoolean("pressurePlateJoin");
		enableLogFilter = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.enableLogFilter");
		allowSpectating = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.allowSpectating");
		useExternalEconomy = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.useExternalEconomy");
		disableWinBroadcast = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.disableWinBroadcast");
		noFallDamage = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.noFallDamage");
		fallDamageDisabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.noFallDamageLobby");
		minPercentPlayers = SkyWarsReloaded.get().getConfig().getInt("gameVariables.minPercentPlayers");
		preGameTimer = SkyWarsReloaded.get().getConfig().getInt("gameVariables.preGameTimer");
		resetPreGameTimerOnJoin = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.resetPreGameTimerOnJoin");
		resetPreGameTimerThreshold = SkyWarsReloaded.get().getConfig().getInt("gameVariables.resetPreGameTimerThreshold");
		maxMapSize = SkyWarsReloaded.get().getConfig().getInt("gameVariables.maxMapSize");
		maxNumberOfGames = SkyWarsReloaded.get().getConfig().getInt("gameVariables.maxNumberOfGames");
		timeAfterGame = SkyWarsReloaded.get().getConfig().getInt("gameVariables.timeAfterGame");
		playerNameScoreboard = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.playerNameScoreboard");
		lobbyScoreBoardEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.lobbyScoreBoardEnabled");
		statsCommandCooldown =  SkyWarsReloaded.get().getConfig().getInt("gameVariables.statsCommandCooldown");
		clearInventoryOnJoin =  SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.clearInventoryOnJoin");
		clearXPOnJoin =  SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.clearXPOnJoin");
		clearPotionEffectsOnJoin =  SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.clearPotionEffectsOnJoin");
		addScorePrefix = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.addScorePrefix");
		useExternalChat = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.useExternalChat");
		
		pointsPerKill = SkyWarsReloaded.get().getConfig().getInt("gameVariables.pointsPerKill");
		pointsLostPerDeath = SkyWarsReloaded.get().getConfig().getInt("gameVariables.pointsLostPerDeath");
		pointsLostPerLeave = SkyWarsReloaded.get().getConfig().getInt("gameVariables.pointsLostPerLeave");
		pointsPerWin = SkyWarsReloaded.get().getConfig().getInt("gameVariables.pointsPerWin");
		vipMultiplier = SkyWarsReloaded.get().getConfig().getInt("gameVariables.vipMultiplier");
		
		kitsEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.kitsEnabled");
		shopsEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.shopsEnabled");
		spectateShopEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.spectateShopEnabled");
		opChestsEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.opChestsEnabled");
		timeVoteEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.timeVoteEnabled");
		jumpVoteEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.jumpVoteEnabled");
		highJumpValue = SkyWarsReloaded.get().getConfig().getInt("gameVariables.highJumpValue");
		superJumpValue = SkyWarsReloaded.get().getConfig().getInt("gameVariables.superJumpValue");
		godJumpValue = SkyWarsReloaded.get().getConfig().getInt("gameVariables.godJumpValue");
		weatherVoteEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.weatherVoteEnabled");
		lightningStrikeChance = SkyWarsReloaded.get().getConfig().getInt("gameVariables.lightningStrikeChance");
		colorGlassEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.colorGlassEnabled");
		particlesEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.particlesEnabled");
		trailEffectsEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.trailEffectsEnabled");
		lobbyMenuEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.lobbyMenuEnabled");
		optionsMenuEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.optionsMenuEnabled");
		purchaseColorGlassEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.purchaseColorGlassEnabled");
		purchaseParticlesEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.purchaseParticlesEnabled");
		purchaseTrailEffectsEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.purchaseTrailEffectsEnabled");
		purchasePermanentKitsEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.purchasePermanentKitsEnabled");
		
		SkyWarsReloaded.get().getConfig().set("gameVariables", null);
		SkyWarsReloaded.get().getConfig().set("signJoinMode", null);
		SkyWarsReloaded.get().getConfig().set("pressurePlateJoin", null);
		
		SkyWarsReloaded.get().getConfig().set("gameSettings.signJoinMode", signJoinMode);
		SkyWarsReloaded.get().getConfig().set("gameSettings.signJoinMaterial", signJoinMaterial);
		SkyWarsReloaded.get().getConfig().set("gameSettings.pressurePlateJoin", pressurePlateJoin);
		SkyWarsReloaded.get().getConfig().set("gameSettings.enableLogFilter", enableLogFilter);
		SkyWarsReloaded.get().getConfig().set("gameSettings.allowSpectating", allowSpectating);
		SkyWarsReloaded.get().getConfig().set("gameSettings.useExternalEconomy", useExternalEconomy);
		SkyWarsReloaded.get().getConfig().set("gameSettings.disableWinBroadcast", disableWinBroadcast);
		SkyWarsReloaded.get().getConfig().set("gameSettings.noFallDamage", noFallDamage);
		SkyWarsReloaded.get().getConfig().set("lobbyGuard.fallDamageDisabled", fallDamageDisabled);
		SkyWarsReloaded.get().getConfig().set("gameSettings.minPercentPlayers", minPercentPlayers);
		SkyWarsReloaded.get().getConfig().set("gameSettings.preGameTimer", preGameTimer);
		SkyWarsReloaded.get().getConfig().set("gameSettings.resetPreGameTimerOnJoin", resetPreGameTimerOnJoin);
		SkyWarsReloaded.get().getConfig().set("gameSettings.maxMapSize", maxMapSize);
		SkyWarsReloaded.get().getConfig().set("gameSettings.maxNumberOfGames", maxNumberOfGames);
		SkyWarsReloaded.get().getConfig().set("gameSettings.timeAfterGame", timeAfterGame);
		SkyWarsReloaded.get().getConfig().set("gameSettings.playerNameScoreboard", playerNameScoreboard);
		SkyWarsReloaded.get().getConfig().set("gameSettings.lobbyScoreBoardEnabled", lobbyScoreBoardEnabled);
		SkyWarsReloaded.get().getConfig().set("gameSettings.statsCommandCooldown", statsCommandCooldown);
		SkyWarsReloaded.get().getConfig().set("gameSettings.clearInventoryOnJoin", clearInventoryOnJoin);
		SkyWarsReloaded.get().getConfig().set("gameSettings.clearXPOnJoin", clearXPOnJoin);
		SkyWarsReloaded.get().getConfig().set("gameSettings.clearPotionEffectsOnJoin", clearPotionEffectsOnJoin);
		SkyWarsReloaded.get().getConfig().set("gameSettings.addScorePrefix", addScorePrefix);
		SkyWarsReloaded.get().getConfig().set("gameSettings.useExternalChat", useExternalChat);
		
		SkyWarsReloaded.get().getConfig().set("points.pointsPerKill", pointsPerKill);
		SkyWarsReloaded.get().getConfig().set("points.pointsLostPerDeath", pointsLostPerDeath);
		SkyWarsReloaded.get().getConfig().set("points.pointsLostPerLeave", pointsLostPerLeave);
		SkyWarsReloaded.get().getConfig().set("points.pointsPerWin", pointsPerWin);
		SkyWarsReloaded.get().getConfig().set("points.vipMultiplier", vipMultiplier);
		
		SkyWarsReloaded.get().getConfig().getBoolean("gameOptions.kitsEnabled", kitsEnabled);
		SkyWarsReloaded.get().getConfig().getBoolean("gameOptions.shopsEnabled", shopsEnabled);
		SkyWarsReloaded.get().getConfig().getBoolean("gameOptions.spectateShopEnabled", spectateShopEnabled);
		SkyWarsReloaded.get().getConfig().getBoolean("gameOptions.opChestsEnabled", opChestsEnabled);
		SkyWarsReloaded.get().getConfig().getBoolean("gameOptions.timeVoteEnabled", timeVoteEnabled);
		SkyWarsReloaded.get().getConfig().getBoolean("gameOptions.jumpVoteEnabled", jumpVoteEnabled);
		SkyWarsReloaded.get().getConfig().getInt("gameOptions.highJumpValue", highJumpValue);
		SkyWarsReloaded.get().getConfig().getInt("gameOptions.superJumpValue", superJumpValue);
		SkyWarsReloaded.get().getConfig().getInt("gameOptions.godJumpValue", godJumpValue);
		SkyWarsReloaded.get().getConfig().getBoolean("gameOptions.weatherVoteEnabled", weatherVoteEnabled);
		SkyWarsReloaded.get().getConfig().getInt("gameOptions.lightningStrikeChance", lightningStrikeChance);
		SkyWarsReloaded.get().getConfig().getBoolean("gameOptions.colorGlassEnabled", colorGlassEnabled);
		SkyWarsReloaded.get().getConfig().getBoolean("gameOptions.particlesEnabled", particlesEnabled);
		SkyWarsReloaded.get().getConfig().getBoolean("gameOptions.trailEffectsEnabled", trailEffectsEnabled);
		SkyWarsReloaded.get().getConfig().getBoolean("gameOptions.lobbyMenuEnabled", lobbyMenuEnabled);
		SkyWarsReloaded.get().getConfig().getBoolean("gameOptions.optionsMenuEnabled", optionsMenuEnabled);
		SkyWarsReloaded.get().getConfig().getBoolean("gameOptions.purchaseColorGlassEnabled", purchaseColorGlassEnabled);
		SkyWarsReloaded.get().getConfig().getBoolean("gameOptions.purchaseParticlesEnabled", purchaseParticlesEnabled);
		SkyWarsReloaded.get().getConfig().getBoolean("gameOptions.purchaseTrailEffectsEnabled", purchaseTrailEffectsEnabled);
		SkyWarsReloaded.get().getConfig().getBoolean("gameOptions.purchasePermanentKitsEnabled", purchasePermanentKitsEnabled);
		SkyWarsReloaded.get().saveConfig();
	}
	
	private void validateConfigValues() {
		if (minPercentPlayers < 0 || minPercentPlayers > 100) {
			minPercentPlayers = 100;
			SkyWarsReloaded.get().getLogger().info("minPercentPlayers must be between 0 and 100, defaulting to 100");
		}
		
		if (lightningStrikeChance < 0 || lightningStrikeChance > 100) {
			lightningStrikeChance = 5;
			SkyWarsReloaded.get().getLogger().info("lightningStrikeChance must be between 0 and 100, defaulting to 5");
		}
		
		if (resetPreGameTimerThreshold < 0 || resetPreGameTimerThreshold > 100) {
			resetPreGameTimerThreshold = 80;
			SkyWarsReloaded.get().getLogger().info("resetPreGameTimerThreshold must be between 0 and 100, defaulting to 80");
		}
		
		if (spectateItemSlot < 0 || spectateItemSlot > 8) {
			spectateItemSlot = 0;
			SkyWarsReloaded.get().getLogger().info("spectateItemSlot must be between 0 and 8, defaulting to 0");
		}
		
		if (lobbyMenuItemSlot < 0 || lobbyMenuItemSlot > 8) {
			lobbyMenuItemSlot = 4;
			SkyWarsReloaded.get().getLogger().info("lobbyMenuItemSlot must be between 0 and 8, defaulting to 4");
		}
		
		if (joinItemSlot < 0 || joinItemSlot > 8) {
			joinItemSlot = 8;
			SkyWarsReloaded.get().getLogger().info("joinItemSlot must be between 0 and 8, defaulting to 8");
		}
		
		if (timeAfterGame < 0) {
			timeAfterGame = 10;
			SkyWarsReloaded.get().getLogger().info("timeAfterGame must be greater than 0, defaulting to 10");
		}
		
		if (preGameTimer < 0) {
			preGameTimer = 10;
			SkyWarsReloaded.get().getLogger().info("preGameTimer must be greater than 0, defaulting to 10");
		}
		
		if (!signJoinMaterial.equalsIgnoreCase("wool") && !signJoinMaterial.equalsIgnoreCase("glass") && !signJoinMaterial.equalsIgnoreCase("clay") && !signJoinMaterial.equalsIgnoreCase("none")) {
			signJoinMaterial = "none";
			SkyWarsReloaded.get().getLogger().info("signJoinMaterial must be wool, glass, clay or none, defaulting to NONE");
		}
		
		if (highJumpValue < 0) {
			highJumpValue = 2;
			SkyWarsReloaded.get().getLogger().info("highJumpValue must be greater than 0, defaulting to 2");
		}
		
		if (superJumpValue < 0) {
			superJumpValue = 4;
			SkyWarsReloaded.get().getLogger().info("superJumpValue must be greater than 0, defaulting to 4");
		}
		
		if (godJumpValue < 0) {
			godJumpValue = 8;
			SkyWarsReloaded.get().getLogger().info("godJumpValue must be greater than 0, defaulting to 8");
		}
		
		if (lobbyMenuItem == null) {
			String lobbyMenuItemName = "eye_of_ender 1 luck:1";
			List<String> lobbyMenuItemData = new LinkedList<String>(Arrays.asList(lobbyMenuItemName.split(" ")));
			String lobbyMenuName = "name:" + new Messaging.MessageFormatter().format("menu.lobbymenu-item-name");
			lobbyMenuItemData.add(lobbyMenuName);
			lobbyMenuItem = ItemUtils.parseItem(lobbyMenuItemData);
			SkyWarsReloaded.get().getLogger().info("lobbyMenuItem in config is not valid, using default item");
		}
		
		if (spectateItem == null) {
			String specItem = "compass 1 luck:1";
			List<String> specItemData = new LinkedList<String>(Arrays.asList(specItem.split(" ")));
			String name = "name:" + new Messaging.MessageFormatter().format("menu.spectategame-item-name");
			specItemData.add(name);
			spectateItem = ItemUtils.parseItem(specItemData);
			SkyWarsReloaded.get().getLogger().info("spectateItem in config is not valid, using default item");
		}

		if (spectatePlayerItem == null) {
			String specPlayerItem = "compass 1 luck:1";
			List<String> specPlayerItemData = new LinkedList<String>(Arrays.asList(specPlayerItem.split(" ")));
			String specPlayerName = "name:" + new Messaging.MessageFormatter().format("menu.spectate-item-name");
			specPlayerItemData.add(specPlayerName);
			spectatePlayerItem = ItemUtils.parseItem(specPlayerItemData);
			SkyWarsReloaded.get().getLogger().info("spectatePlayerItem in config is not valid, using default item");
		}

		if (joinItem == null) {
			String joinItemName = "daylight_detector 1 luck:1";
			List<String> joinItemData = new LinkedList<String>(Arrays.asList(joinItemName.split(" ")));
			String joinName = "name:" + new Messaging.MessageFormatter().format("menu.join-item-name");
			joinItemData.add(joinName);
			joinItem = ItemUtils.parseItem(joinItemData);
			SkyWarsReloaded.get().getLogger().info("joinItem in config is not valid, using default item");
		}
		
		if (kitMenuItem == null) {
			String kitItemName = "storage_minecart 1 luck:1";
			List<String> kitItemData = new LinkedList<String>(Arrays.asList(kitItemName.split(" ")));
			kitMenuItem = ItemUtils.parseItem(kitItemData);
			SkyWarsReloaded.get().getLogger().info("kitMenuItem in config is not valid, using default item");
		}
		
		if (exitGameItem == null) {
			String exitItem = "iron_door 1 luck:1";
			List<String> exitItemData = new LinkedList<String>(Arrays.asList(exitItem.split(" ")));
			exitGameItem = ItemUtils.parseItem(exitItemData);
			SkyWarsReloaded.get().getLogger().info("exitGameItem in config is not valid, using default item");
		}
		
		if (optionsItem == null) {
			String optionsItemName = "cake 1 luck:1";
			List<String> optionsItemData = new LinkedList<String>(Arrays.asList(optionsItemName.split(" ")));
			String optionsName = "name:" + new Messaging.MessageFormatter().format("menu.options-item-name");
			optionsItemData.add(optionsName);
			optionsItem = ItemUtils.parseItem(optionsItemData);
			SkyWarsReloaded.get().getLogger().info("optionsItem in config is not valid, using default item");
		}
		
		if (opChestMenuItem == null) {
			String opVoteItem = "diamond 1";
			List<String> opVoteItemData = new LinkedList<String>(Arrays.asList(opVoteItem.split(" ")));
			opChestMenuItem = ItemUtils.parseItem(opVoteItemData);
			SkyWarsReloaded.get().getLogger().info("opChestMenuItem in config is not valid, using default item");
		}

		if (timeMenuItem == null) {
			String timeVoteItem = "watch 1";
			List<String> timeVoteItemData = new LinkedList<String>(Arrays.asList(timeVoteItem.split(" ")));
			timeMenuItem = ItemUtils.parseItem(timeVoteItemData);
			SkyWarsReloaded.get().getLogger().info("timeMenuItem in config is not valid, using default item");
		}
		
		if (jumpMenuItem == null) {
			String jumpVoteItem = "diamond_boots 1";
			List<String> jumpVoteItemData = new LinkedList<String>(Arrays.asList(jumpVoteItem.split(" ")));
			jumpMenuItem = ItemUtils.parseItem(jumpVoteItemData);
			SkyWarsReloaded.get().getLogger().info("jumpMenuItem in config is not valid, using default item");
		}
		
		if (glassMenuItem == null) {
			String colorItem = "stained_glass 1";
			List<String> colorItemData = new LinkedList<String>(Arrays.asList(colorItem.split(" ")));
			glassMenuItem = ItemUtils.parseItem(colorItemData);
			SkyWarsReloaded.get().getLogger().info("glassMenuItem in config is not valid, using default item");
		}
		
		if (particleMenuItem == null) {
			String particleItem = "pumpkin_seeds 1";
			List<String> particleItemData = new LinkedList<String>(Arrays.asList(particleItem.split(" ")));
			particleMenuItem = ItemUtils.parseItem(particleItemData);
			SkyWarsReloaded.get().getLogger().info("particleMenuItem in config is not valid, using default item");
		}
		
		if (trailEffectMenuItem == null) {
			String projEffectItem = "arrow 1";
			List<String> projEffectItemData = new LinkedList<String>(Arrays.asList(projEffectItem.split(" ")));
			trailEffectMenuItem = ItemUtils.parseItem(projEffectItemData);
			SkyWarsReloaded.get().getLogger().info("trailEffectMenuItem in config is not valid, using default item");
		}

		if (weatherMenuItem == null) {
			String weatherItem = "glowstone 1";
			List<String> weatherItemData = new LinkedList<String>(Arrays.asList(weatherItem.split(" ")));
			weatherMenuItem = ItemUtils.parseItem(weatherItemData);
			SkyWarsReloaded.get().getLogger().info("weatherMenuItem in config is not valid, using default item");
		}

		if (kitPermMenuItem == null) {
			String permKitItem = "storage_minecart 1";
			List<String> permKitItemData = new LinkedList<String>(Arrays.asList(permKitItem.split(" ")));
			kitPermMenuItem = ItemUtils.parseItem(permKitItemData);
			SkyWarsReloaded.get().getLogger().info("kitPermMenuItem in config is not valid, using default item");
		}

		if (specStoreItem == null) {
			String specShItem = "anvil 1 luck:1";
			List<String> specShopItemData = new LinkedList<String>(Arrays.asList(specShItem.split(" ")));
			String specShopName = "name:" + new Messaging.MessageFormatter().format("menu.spectateshop-item-name");
			specShopItemData.add(specShopName);
			specStoreItem = ItemUtils.parseItem(specShopItemData);
			SkyWarsReloaded.get().getLogger().info("specStoreItem in config is not valid, using default item");
		}
		
		if (playerJoinLobby == null) {
			playerJoinLobby =  Sound.SLIME_WALK2;
			SkyWarsReloaded.get().getLogger().info("playerJoinLobby sound in config is not valid, using default sound");
		}
		
		if (playerLeaveLobby == null) {
			playerLeaveLobby =  Sound.SLIME_ATTACK;
			SkyWarsReloaded.get().getLogger().info("playerLeaveLobby sound in config is not valid, using default sound");
		}
		
		if (playerDeath == null) {
			playerDeath =  Sound.AMBIENCE_THUNDER;
			SkyWarsReloaded.get().getLogger().info("playerDeath sound in config is not valid, using default sound");
		}
		
		if (playerWin == null) {
			playerWin =  Sound.FIREWORK_LARGE_BLAST;
			SkyWarsReloaded.get().getLogger().info("playerWin sound in config is not valid, using default sound");
		}
		
		if (opChestVote == null) {
			opChestVote =  Sound.CHEST_OPEN;
			SkyWarsReloaded.get().getLogger().info("opChestVote sound in config is not valid, using default sound");
		}
		
		if (timeVote == null) {
			timeVote =  Sound.FUSE;
			SkyWarsReloaded.get().getLogger().info("timeVote sound in config is not valid, using default sound");
		}
		
		if (weatherVote == null) {
			weatherVote =  Sound.SPLASH;
			SkyWarsReloaded.get().getLogger().info("weatherVote sound in config is not valid, using default sound");
		}
		
		if (jumpVote == null) {
			jumpVote =  Sound.MAGMACUBE_JUMP;
			SkyWarsReloaded.get().getLogger().info("jumpVote sound in config is not valid, using default sound");
		}
	}

	public boolean sendPlayersToSpawnOnJoin() {
		return sendPlayersToSpawnOnJoin;
	}
	
}
