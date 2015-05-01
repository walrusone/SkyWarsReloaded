package com.walrusone.skywars.game;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.google.common.collect.Lists;
import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.utilities.BungeeUtil;
import com.walrusone.skywars.utilities.EmptyChest;
import com.walrusone.skywars.utilities.IconMenu;
import com.walrusone.skywars.utilities.Messaging;
import com.walrusone.skywars.utilities.StringUtils;


public class Game {

    private static final int menuSlotsPerRow = 9;
    private static final int menuSize = 54;
    private static final String timeMenuName = new Messaging.MessageFormatter().format("menu.time-menu-title");
    private static final String jumpMenuName = new Messaging.MessageFormatter().format("menu.jump-menu-title");
	private Map<GamePlayer, String> gPlayers = new HashMap<GamePlayer, String>();
	private Map<GamePlayer, String> spectators = new HashMap<GamePlayer, String>();
	private int count = 0;
	private int sanityChecker = 0;
	private int preGameTimer;
	private GameState gameState;
	private String mapName;
	private World mapWorld;
	private GameMap gameMap;
	private int gameNumber;
	private Scoreboard scoreboard;
	private Objective objective;
	private int minPlayers;
	private double minPercent;
	private boolean allowSpectating;
	private Location spawn;
	private int winValue;
	private int killValue;
	private int deathValue;
	private int leaveValue;
	private int vipMultiplier;
	private boolean bungeeMode;
	private boolean signJoinMode;
	private boolean disableWinBroadcast;
	private int numberOfSpawns;
	private boolean shutdown;
	private Map<Integer, GamePlayer> availableSpawns = new HashMap<Integer, GamePlayer>();
	private ItemStack kit;
	private ItemStack opChest;
	private ItemStack timeVote;
	private ItemStack jumpVote;
	private ItemStack exit;
	private String winSound;
	private String deathSound;
	private String joinLobbySound;
	private String leaveLobbySound;
	private String voteForTimeSound;
	private Location specSpawn;
	
	public Game(int gameNumber, String map) {
		this.gameNumber = gameNumber;
		bungeeMode = SkyWarsReloaded.get().getConfig().getBoolean("bungeeMode.enabled");
		signJoinMode = SkyWarsReloaded.get().getConfig().getBoolean("signJoinMode");
		kit = SkyWarsReloaded.getGC().getKitItem();
		exit  = SkyWarsReloaded.getGC().getExitItem();
		opChest = SkyWarsReloaded.getGC().getOpVoteItem();
		timeVote = SkyWarsReloaded.getGC().getTimeVoteItem();
		jumpVote = SkyWarsReloaded.getGC().getJumpVoteItem();
		minPercent = SkyWarsReloaded.get().getConfig().getDouble("gameVariables.minPercentPlayers")/100;
		allowSpectating = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.allowSpectating");
		disableWinBroadcast = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.disableWinBroadcast");
		winValue = SkyWarsReloaded.get().getConfig().getInt("gameVariables.pointsPerWin");
		preGameTimer = SkyWarsReloaded.get().getConfig().getInt("gameVariables.preGameTimer");
		killValue = SkyWarsReloaded.get().getConfig().getInt("gameVariables.pointsPerKill");
		deathValue = SkyWarsReloaded.get().getConfig().getInt("gameVariables.pointsLostPerDeath");
		leaveValue = SkyWarsReloaded.get().getConfig().getInt("gameVariables.pointsLostPerLeave");
		vipMultiplier = SkyWarsReloaded.get().getConfig().getInt("gameVariables.vipMultiplier");
		if (signJoinMode) {
			mapName = map;
		}
		winSound = SkyWarsReloaded.get().getConfig().getString("gameSounds.playerWin");
		deathSound = SkyWarsReloaded.get().getConfig().getString("gameSounds.playerDeath");
		joinLobbySound = SkyWarsReloaded.get().getConfig().getString("gameSounds.playerJoinLobby");
		leaveLobbySound = SkyWarsReloaded.get().getConfig().getString("gameSounds.playerLeaveLobby");
		voteForTimeSound = SkyWarsReloaded.get().getConfig().getString("gameSounds.timeVote");
		String world = SkyWarsReloaded.get().getConfig().getString("spawn.world");
		int x = SkyWarsReloaded.get().getConfig().getInt("spawn.x");
		int y = SkyWarsReloaded.get().getConfig().getInt("spawn.y");
		int z = SkyWarsReloaded.get().getConfig().getInt("spawn.z");
		float yaw = SkyWarsReloaded.get().getConfig().getInt("spawn.yaw");
		float pitch = SkyWarsReloaded.get().getConfig().getInt("spawn.pitch");
		if (world != null ) {
			spawn = new Location(SkyWarsReloaded.get().getServer().getWorld(world), x, y, z, yaw, pitch);
		} else {
			spawn = null;
		}
		gameState = GameState.PREGAME;
		getGameMap();
		getScoreBoard();
	}
	
	private void getGameMap() {
		if (!signJoinMode) {
			ArrayList<String> shuffleMaps = SkyWarsReloaded.getMC().getMaps();
	    	Collections.shuffle(shuffleMaps);
			int numberOfMaps = shuffleMaps.size();
			int random = (int)(Math.random()*numberOfMaps) + 1;
			mapName = shuffleMaps.get(random-1);
		}
		gameMap = SkyWarsReloaded.getMC().getMap(mapName);
		boolean gameLoad = gameMap.loadMap(this.gameNumber);
		mapWorld = SkyWarsReloaded.get().getServer().getWorld(mapName + "_" + this.gameNumber);
		if (gameLoad) {
			numberOfSpawns = gameMap.getSpawns().size();
	    	if (allowSpectating) {
				double x = gameMap.getSpawns().get(1).getX()+0.5;
				double y = gameMap.getSpawns().get(1).getY();
				double z =  gameMap.getSpawns().get(1).getZ()+0.5;
				specSpawn = new Location(mapWorld, x, y, z);
			}
	    	minPlayers = (int) (numberOfSpawns * minPercent);
	    	if (minPlayers < 1) {
	    		minPlayers = 1;
	    	}
			for (int i = 1; i <= numberOfSpawns; i++) {
				availableSpawns.put(i, null);
			}
			createSpawnPlatform();
		} else {
			endGame();
		}

	}
	
	public void addPlayer(GamePlayer gPlayer) {
		if (gPlayers.size() >= numberOfSpawns || gPlayers.containsKey(gPlayer)) {
			return;
		} else {
			if (gPlayer.getP() != null) {
				gPlayers.put(gPlayer, gPlayer.getP().getDisplayName());
				gPlayer.setGame(gameNumber);
				gPlayer.setInGame(true);
				if (bungeeMode) {
					BungeeUtil.sendSignUpdateRequest(this);
				}
				if (signJoinMode) {
					SkyWarsReloaded.getGC().updateSign(gameNumber);
				}
				preparePlayerForLobby(gPlayer);
			} 
		}
	}
	
	private void preparePlayerForLobby(GamePlayer gPlayer) {
		Location location;
		int spawn = getAvailableSpawn();
		location = new Location(mapWorld, gameMap.getSpawns().get(spawn).getX()+0.5, gameMap.getSpawns().get(spawn).getY(), gameMap.getSpawns().get(spawn).getZ()+0.5);
		availableSpawns.put(spawn,  gPlayer);
		
		gPlayer.saveInventory();
		gPlayer.saveGameMode();
		gPlayer.clearInventory();
		gPlayer.setRespawn(gPlayer.getP().getLocation());
		gPlayer.getP().setHealth(20);
		gPlayer.getP().setFoodLevel(20);
		gPlayer.getP().setFlying(false);
		gPlayer.setOpVote(0);
		gPlayer.setTimeVote(0);
		gPlayer.setJumpVote(0);
		for (PotionEffect effect : gPlayer.getP().getActivePotionEffects()) {
	        gPlayer.getP().removePotionEffect(effect.getType());
		}
		gPlayer.getP().setGameMode(GameMode.ADVENTURE);
		gPlayer.getP().setScoreboard(SkyWarsReloaded.get().getServer().getScoreboardManager().getNewScoreboard());
		gPlayer.getP().setScoreboard(scoreboard);
		Score score = objective.getScore(new Messaging.MessageFormatter().format("game.scoreboard-players"));
		score.setScore(getPlayers().size());
		gPlayer.getP().getInventory().setItem(8, exit);
		boolean opEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.opChestsEnabled");
		if (opEnabled) {
				gPlayer.getP().getInventory().setItem(4, opChest);
		}
		boolean timeEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.timeVoteEnabled");
		if (timeEnabled) {
				gPlayer.getP().getInventory().setItem(6, timeVote);
		}
		boolean jumpEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.jumpVoteEnabled");
		if (jumpEnabled) {
				gPlayer.getP().getInventory().setItem(2, jumpVote);
		}
		boolean enabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.kitsEnabled");
		if (enabled) {
			gPlayer.getP().getInventory().setItem(0, kit);
		}
		gPlayer.getP().teleport(location);
		sendGameMessage(new Messaging.MessageFormatter()
			.withPrefix()
			.setVariable("number", "" + gPlayers.size())
			.setVariable("total", "" + numberOfSpawns)
			.setVariable("player", gPlayer.getP().getName())
			.format("game.lobby-join"));
		playSound(joinLobbySound);
	}
	
	private int getAvailableSpawn() {
		for (int spawn: availableSpawns.keySet()) {
			if (availableSpawns.get(spawn) == null) {
				return spawn;
			}
		}
		return 1;
	}

	private void fillChests() {
		int votesForBasic = 0;
        int votesForNormal = 0;
        int votesForOP = 0;

        String vote = "normal";
    	for (GamePlayer gPlayer: getPlayers()) {
    		if (gPlayer.getOpVote() == 1) {
    			votesForBasic++;
    		} else if (gPlayer.getOpVote() == 2) {
    			votesForNormal++;
    		} else if (gPlayer.getOpVote() == 3) {
    			votesForOP++;
    		} 
    	}
    	
    	if (votesForNormal >= votesForBasic && votesForNormal >= votesForOP) {
    		vote = "normal";
    	} else if (votesForOP >= votesForBasic) {
    		vote = "op";
    	} else {
    		vote = "basic";
    	}
    	
		for (EmptyChest eChest: gameMap.getChests().values()) {
			Location loc;
			int x = eChest.getX();
			int y = eChest.getY();
			int z = eChest.getZ();
			loc = new Location (mapWorld, x, y, z);
			Chest chest = (Chest) loc.getBlock().getState();
			SkyWarsReloaded.getCC().populateChest(chest, vote);
		}
	}

	public void prepareForStart() {
		if (gameState == GameState.PREGAME) {
			if (gPlayers.size() >= minPlayers) {
				if (count == 0) {
					sendGameMessage(new Messaging.MessageFormatter()
    					.withPrefix()
    					.setVariable("time", "" + preGameTimer)
    					.format("game.countdown"));
				} else if (count > 0 && count < preGameTimer) {
					sendGameMessage(new Messaging.MessageFormatter()
    					.withPrefix()
    					.setVariable("time", "" + (preGameTimer - count))
    					.format("game.countdown-continue"));
				} else if (count >= preGameTimer) {
					startGame();
				}
				count++;
        	} else if (gPlayers.size() < minPlayers) {
        		count = 0;
        	}
        }
		if (gameState == GameState.PREGAME || gameState == GameState.PLAYING) {
			sanityChecker++;
			if (sanityChecker == 5) {
				String world = mapWorld.getName();
				for (GamePlayer gPlayer: getPlayers()) {
					if (gPlayer.getP() != null) {
						if (!gPlayer.getP().getWorld().getName().equalsIgnoreCase(world)) {
							if (gPlayer.getGame() != null) {
								Game game = gPlayer.getGame();
								if (game == this) {
									deletePlayer(gPlayer, true, false);
								} else {
									gPlayers.remove(gPlayer);
									checkForWinner();
								}
							} else {
								gPlayers.remove(gPlayer);
								checkForWinner();
							}
						}
					} else {
						gPlayers.remove(gPlayer);
						checkForWinner();
					}
				}
			} else if (sanityChecker > 5) {
				sanityChecker = 0;
			}
		}
	}

	public void startGame() {
		gameState = GameState.PLAYING;
		if (bungeeMode) {
			BungeeUtil.sendSignUpdateRequest(this);
		}
		if (signJoinMode) {
			SkyWarsReloaded.getGC().updateSign(gameNumber);
		}
		fillChests();
		removeSpawnHousing();
		for (GamePlayer gPlayer: getPlayers()) {
			if (gPlayer.getP() != null) {
				gPlayer.getP().setGameMode(GameMode.SURVIVAL);
			    gPlayer.getP().getInventory().remove(kit);
			    gPlayer.getP().getInventory().remove(exit);
			    gPlayer.getP().getInventory().remove(opChest);
			    gPlayer.getP().getInventory().remove(timeVote);
			    gPlayer.getP().getInventory().remove(jumpVote);
			    gPlayer.getP().getOpenInventory().close();
			    gPlayer.getP().setHealth(20);
			    gPlayer.getP().setFoodLevel(20);
			    gPlayer.getP().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 5));
			    if (gPlayer.hasKitSelected()) {
			    	SkyWarsReloaded.getKC().populateInventory(gPlayer.getP().getInventory(), gPlayer.getSelectedKit());
			        SkyWarsReloaded.getKC().givePotionEffects(gPlayer, gPlayer.getSelectedKit());
			        gPlayer.setKitSelected(false);
			    }
			}
		}
	    boolean timeEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.timeVoteEnabled");
		if (timeEnabled) {
			setTime();
		}
	    boolean jumpEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.jumpVoteEnabled");
		if (jumpEnabled) {
			setJump();
		}
	}
	
	public void endGame() {
		gameState = GameState.ENDING;
		if (bungeeMode && !shutdown) {
			BungeeUtil.sendSignUpdateRequest(this);
		}
		if (signJoinMode && !shutdown) {
			SkyWarsReloaded.getGC().updateSign(gameNumber);
		}
		for (GamePlayer gplayer: getPlayers()) {
			deletePlayer(gplayer, false, false);
		}
		if (allowSpectating) {
			for (GamePlayer gPlayer: getSpectators()) {
				removeSpectator(gPlayer);
			}
		}
		for (Player player: mapWorld.getPlayers()) {
			String world2 = SkyWarsReloaded.get().getConfig().getString("spawn.world");
			int x = SkyWarsReloaded.get().getConfig().getInt("spawn.x");
			int y = SkyWarsReloaded.get().getConfig().getInt("spawn.y");
			int z = SkyWarsReloaded.get().getConfig().getInt("spawn.z");
			Location spawn;
			if (world2 != null ) {
				spawn = new Location(SkyWarsReloaded.get().getServer().getWorld(world2), x, y, z);
			} else {
				spawn = SkyWarsReloaded.getPC().getPlayer(player.getUniqueId()).getRespawn();
			}
			if (player != null) {
				player.teleport(spawn);
			}
		}
		deleteGame();
	}
		
	private void getScoreBoard() {
		if (scoreboard != null) {
            resetScoreboard();
        }
		ScoreboardManager manager = SkyWarsReloaded.get().getServer().getScoreboardManager();
		scoreboard = manager.getNewScoreboard();
        objective = scoreboard.registerNewObjective("info", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        String leaderboard = new Messaging.MessageFormatter().setVariable("mapname", mapName.toUpperCase()).format("game.scoreboard-title");
        objective.setDisplayName(leaderboard);
	}
	
    private void resetScoreboard() {
        if (objective != null) {
            objective.unregister();
        }

        if (scoreboard != null) {
            scoreboard = null;
        }
    }
	
	public void deletePlayer(final GamePlayer gplayer, boolean playerQuit, boolean hardQuit) {
		if (gPlayers.containsKey(gplayer)) {
			gPlayers.remove(gplayer);
		}
		if (playerQuit) {
			playerQuit(gplayer);
		}
		if (!hardQuit) {
			preparePlayerForExit(gplayer);
		}
		if (gameState == GameState.PREGAME || gameState == GameState.PLAYING) {
			if (bungeeMode && !shutdown) {
				BungeeUtil.sendSignUpdateRequest(this);
			}
			if (signJoinMode && !shutdown) {
				SkyWarsReloaded.getGC().updateSign(gameNumber);
			}
		}
		if (bungeeMode && !shutdown && !hardQuit) {
			if (gplayer.getP() != null) {
				gplayer.getP().teleport(spawn);
				BungeeUtil.connectToServer(gplayer.getP(), SkyWarsReloaded.get().getConfig().getString("bungeeMode.lobbyServer"));
			}
		} else {
			if (gplayer.getP() != null) {
				if (spawn != null) {
					gplayer.getP().teleport(spawn);
				} else {
					gplayer.getP().teleport(gplayer.getRespawn());
				}
				SkyWarsReloaded.getScore().getScoreboard(gplayer.getP());
			}
		}
		if (gameState == GameState.PLAYING) {
			checkForWinner();
		}
	}
	
	public void shutdown() {
		shutdown = true;
	}
	
	private void playerQuit(GamePlayer gPlayer) {
		Score score = objective.getScore(new Messaging.MessageFormatter().format("game.scoreboard-players"));
		score.setScore(getPlayers().size());
		gPlayer.setInGame(false);
		GamePlayer killer = gPlayer.getTagged().getPlayer();
		if (gameState == GameState.PLAYING) {
			if (!gPlayer.isSpectating()) {
				if ((System.currentTimeMillis() - gPlayer.getTagged().getTime()) < 10000 && killer != gPlayer) {
					if (killer != gPlayer) {
						killer.setKills(killer.getKills() + 1);
						gPlayer.setDeaths(gPlayer.getDeaths() + 1);
						int killTotal = killValue;
						if (killer.getP() != null) {
							if (SkyWarsReloaded.perms.has(killer.getP(), "swr.vip")) {
								killTotal = killValue * vipMultiplier;
							} else {
								killTotal = killValue;
							}
						}
						killer.setScore(killer.getScore() + killTotal);
						gPlayer.setScore(gPlayer.getScore() - deathValue);
						addBalance(killer, killTotal);
						removeBalance(gPlayer, deathValue);
						sendGameMessage(new Messaging.MessageFormatter()
						.withPrefix()
						.setVariable("player", gPlayer.getName())
						.setVariable("player_score", StringUtils.formatScore(-deathValue))
						.setVariable("killer", killer.getName())
						.setVariable("killer_score", StringUtils.formatScore(killTotal))
						.format("game.death.quit-while-tagged"));
					}
				} else {
					gPlayer.setScore(gPlayer.getScore() - leaveValue);
					removeBalance(gPlayer, leaveValue);
					sendGameMessage(new Messaging.MessageFormatter()
							.withPrefix()
							.setVariable("player", gPlayer.getName())
							.setVariable("score", StringUtils.formatScore(-leaveValue))
							.format("game.left-the-game"));
					}
				}
			} else if (gameState == GameState.PREGAME) {
				scoreboard.resetScores(gPlayer.getName());
				for (int spawn: availableSpawns.keySet()) {
					if (availableSpawns.get(spawn) == gPlayer) {
						availableSpawns.put(spawn,  null);
					}
				}
				sendGameMessage(new Messaging.MessageFormatter()
						.withPrefix()
						.setVariable("number", "" + (gPlayers.size()-1))
						.setVariable("total", "" + numberOfSpawns)
						.setVariable("player", gPlayer.getName())
						.format("game.lobby-leave"));
				playSound(leaveLobbySound);
			}
	}
	
	private void preparePlayerForExit(GamePlayer gplayer) {
		if (gplayer.getP() != null) {
	  		gplayer.clearInventory();
			gplayer.resetInventory();
			gplayer.getP().updateInventory();
			gplayer.resetGameMode();
			gplayer.setKitSelected(false);
			gplayer.setOpVote(0);
			gplayer.setTimeVote(0);
			gplayer.setJumpVote(0);
			gplayer.getP().setScoreboard(SkyWarsReloaded.get().getServer().getScoreboardManager().getNewScoreboard());
			for (PotionEffect effect : gplayer.getP().getActivePotionEffects()) {
		        gplayer.getP().removePotionEffect(effect.getType());
			}
			gplayer.setGame(-1);
		}
	}
		
	public Boolean playerExists(GamePlayer name) {
		for (GamePlayer gPlayer: gPlayers.keySet()) {
			if (name == gPlayer) {
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<GamePlayer> getPlayers() {
		ArrayList<GamePlayer> players = new ArrayList<GamePlayer>();
		for (GamePlayer g: gPlayers.keySet()) {
			players.add(g);
		}
		return players;
	}
	
	public ArrayList<GamePlayer> getSpectators() {
		ArrayList<GamePlayer> players = new ArrayList<GamePlayer>();
		for (GamePlayer g: spectators.keySet()) {
			players.add(g);
		}
		return players;
	}
	
	public void deleteGame() {
		SkyWarsReloaded.getMV().getMVWorldManager().deleteWorld(mapName + "_" + gameNumber);
		deleteWorldGuardFolder(mapName + "_" + gameNumber);
		SkyWarsReloaded.getGC().deleteGame(gameNumber);
	}
	
	private void deleteWorldGuardFolder(String name) {
		File workingDirectory = new File(SkyWarsReloaded.get().getServer().getWorldContainer().getAbsolutePath());
		workingDirectory = new File(workingDirectory, "/plugins/WorldGuard/worlds/");
	    File[] contents = workingDirectory.listFiles();
        if (contents != null) {
	            for (File file : contents) {
	                if (!file.isDirectory() || !file.getName().matches(name)) {
	                    continue;
	                }
	                SkyWarsReloaded.getWC().deleteWorld(file);
	            }
	        }
	}
	
	public GameState getState() {
		return gameState;
	}
	
	public Boolean isFull() {
		if (gPlayers.size() >= numberOfSpawns) {
			return true;
		} else {
			return false;
		}
	}
	
    public void createSpawnPlatform() {
    	for(int spawn : gameMap.getSpawns().keySet()) {
            int x = gameMap.getSpawns().get(spawn).getBlockX();
            int y = gameMap.getSpawns().get(spawn).getBlockY();
            int z = gameMap.getSpawns().get(spawn).getBlockZ();

            mapWorld.getBlockAt(x, y, z).setType(Material.GLASS);
            mapWorld.getBlockAt(x, y + 1, z + 1).setType(Material.GLASS);
            mapWorld.getBlockAt(x, y + 1, z - 1).setType(Material.GLASS);
            mapWorld.getBlockAt(x + 1, y + 1, z).setType(Material.GLASS);
            mapWorld.getBlockAt(x - 1, y + 1, z).setType(Material.GLASS);
            mapWorld.getBlockAt(x, y + 2, z + 1).setType(Material.GLASS);
            mapWorld.getBlockAt(x, y + 2, z - 1).setType(Material.GLASS);
            mapWorld.getBlockAt(x + 1, y + 2, z).setType(Material.GLASS);
            mapWorld.getBlockAt(x - 1, y + 2, z).setType(Material.GLASS);
            mapWorld.getBlockAt(x, y + 3, z + 1).setType(Material.GLASS);
            mapWorld.getBlockAt(x, y + 3, z - 1).setType(Material.GLASS);
            mapWorld.getBlockAt(x + 1, y + 3, z).setType(Material.GLASS);
            mapWorld.getBlockAt(x - 1, y + 3, z).setType(Material.GLASS);
            mapWorld.getBlockAt(x, y + 4, z).setType(Material.GLASS);
    	}
    }
    
    public void removeSpawnHousing() {
    	for(int spawn : gameMap.getSpawns().keySet()) {
            int x = gameMap.getSpawns().get(spawn).getBlockX();
            int y = gameMap.getSpawns().get(spawn).getBlockY();
            int z = gameMap.getSpawns().get(spawn).getBlockZ();
            
            mapWorld.getBlockAt(x, y, z).setType(Material.AIR);
            mapWorld.getBlockAt(x, y + 1, z + 1).setType(Material.AIR);
            mapWorld.getBlockAt(x, y + 1, z - 1).setType(Material.AIR);
            mapWorld.getBlockAt(x + 1, y + 1, z).setType(Material.AIR);
            mapWorld.getBlockAt(x - 1, y + 1, z).setType(Material.AIR);
            mapWorld.getBlockAt(x, y + 2, z + 1).setType(Material.AIR);
            mapWorld.getBlockAt(x, y + 2, z - 1).setType(Material.AIR);
            mapWorld.getBlockAt(x + 1, y + 2, z).setType(Material.AIR);
            mapWorld.getBlockAt(x - 1, y + 2, z).setType(Material.AIR);
            mapWorld.getBlockAt(x, y + 3, z + 1).setType(Material.AIR);
            mapWorld.getBlockAt(x, y + 3, z - 1).setType(Material.AIR);
            mapWorld.getBlockAt(x + 1, y + 3, z).setType(Material.AIR);
            mapWorld.getBlockAt(x - 1, y + 3, z).setType(Material.AIR);
            mapWorld.getBlockAt(x, y + 4, z).setType(Material.AIR);
    	}
    }
  
	public enum GameState {
	    PREGAME,
	    PLAYING,
	    ENDING, 
	}

	public void setState(GameState gState) {
		gameState = gState;
	}

	public void onPlayerDeath(GamePlayer target, DamageCause dCause, Location loc) {
		gPlayers.remove(target);
		target.setInGame(false);
		target.setKitSelected(false);
		target.setOpVote(0);
		target.setTimeVote(0);
		target.setJumpVote(0);
		
		if (target.getP() != null) {
			target.getP().setScoreboard(SkyWarsReloaded.get().getServer().getScoreboardManager().getNewScoreboard());
			for (PotionEffect effect : target.getP().getActivePotionEffects()) {
		        target.getP().removePotionEffect(effect.getType());
			}
		}
		
		target.setGame(-1);
		if (allowSpectating) {
			target.spectateMode(true, this);
		} 
		
		if (target.getP() != null) {
			target.getP().spigot().respawn();
			if (!allowSpectating) {
					deletePlayer(target, false, false);
			}
		}
		
		Score score = objective.getScore(new Messaging.MessageFormatter().format("game.scoreboard-players"));
		score.setScore(getPlayers().size());
		target.setDeaths(target.getDeaths() + 1);
		mapWorld.playEffect(loc, Effect.MOBSPAWNER_FLAMES, 4);
		if ((System.currentTimeMillis() - target.getTagged().getTime()) < 10000) {
			GamePlayer killer = target.getTagged().getPlayer();
			if (killer != target) {
				killer.setKills(killer.getKills() + 1);
				int killTotal = killValue;
				if (killer.getP() != null) {
					if (SkyWarsReloaded.perms.has(killer.getP(), "swr.vip")) {
						killTotal = killValue * vipMultiplier;
					} else {
						killTotal = killValue;
					}
				}
				killer.setScore(killer.getScore() + killTotal);
				target.setScore(target.getScore() - deathValue);
				addBalance(killer, killTotal);
				removeBalance(target, deathValue);
				target.setTagged(target);
				sendGameMessage(getDeathMessage(dCause, true, target, killer));
				playSound(deathSound);
			}
		} else {
			target.setScore(target.getScore() - deathValue);
			removeBalance(target, deathValue);
			sendGameMessage(getDeathMessage(dCause, false, target, target));
			playSound(deathSound);
		}
		target.setGamesPlayed(target.getGamesPlayed() + 1);
		if (gameState == GameState.PREGAME || gameState == GameState.PLAYING) {
			if (bungeeMode && !shutdown) {
				BungeeUtil.sendSignUpdateRequest(this);
			}
			if (signJoinMode && !shutdown) {
				SkyWarsReloaded.getGC().updateSign(gameNumber);
			}
		}
		checkForWinner();
	}
	
	public void checkForWinner() {
		if (gameState == GameState.PLAYING) {
			if (getPlayers().size() == 1) {
				gameState = GameState.ENDING;
				if (bungeeMode) {
        			BungeeUtil.sendSignUpdateRequest(this);
        		}
    			if (signJoinMode) {
    				SkyWarsReloaded.getGC().updateSign(gameNumber);
    			}
    			GamePlayer gPlayer = getPlayers().get(0);
    			gPlayers.remove(gPlayer);
    			gPlayer.setInGame(false);
    			gPlayer.setKitSelected(false);
    			gPlayer.setOpVote(0);
    			gPlayer.setTimeVote(0);
    			gPlayer.setJumpVote(0);
    			if (gPlayer.getP() != null) {
        			gPlayer.getP().setScoreboard(SkyWarsReloaded.get().getServer().getScoreboardManager().getNewScoreboard());
        			for (PotionEffect effect : gPlayer.getP().getActivePotionEffects()) {
        		        gPlayer.getP().removePotionEffect(effect.getType());
        		        launchFirework(gPlayer.getP(), 50);
        			}
    			}
    			gPlayer.setGame(-1);
				gPlayer.setWins(gPlayer.getWins() + 1);
				gPlayer.setGamesPlayed(gPlayer.getGamesPlayed() + 1);
				playSound(winSound);
				int winTotal = winValue;
    			if (gPlayer.getP() != null) {
    				if (SkyWarsReloaded.perms.has(gPlayer.getP(), "swr.vip")) {
    					winTotal = winValue * vipMultiplier;
    				} else {
    					winTotal = winValue;
    				}
    			}
				gPlayer.setScore(gPlayer.getScore() + winTotal);
				addBalance(gPlayer, winTotal);
				if (disableWinBroadcast) {
						sendGameMessage(new Messaging.MessageFormatter().setVariable("player", gPlayer.getName())
								.withPrefix()
								.setVariable("player_score", StringUtils.formatScore(winTotal))
								.setVariable("mapname", mapName)
								.format("game.win"));
				} else {
					for (GamePlayer gamePlayer: SkyWarsReloaded.getPC().getAll()) {
						if (gamePlayer.getP() != null) {
							gamePlayer.getP().sendMessage(new Messaging.MessageFormatter().setVariable("player", gPlayer.getName())
									.withPrefix()
									.setVariable("player_score", StringUtils.formatScore(winTotal))
									.setVariable("mapname", mapName)
									.format("game.win"));
						}
					}
				}
				if (allowSpectating) {
					if (gPlayer.getP() != null) {
						gPlayer.spectateMode(true, this);
						gPlayer.getP().sendMessage(new Messaging.MessageFormatter().withPrefix().format("game.spectating"));
						sendGameMessage(new Messaging.MessageFormatter().withPrefix()
								.setVariable("time", "" + SkyWarsReloaded.get().getConfig().getInt("gameVariables.timeAfterGame"))
								.format("game.gameEnding"));
					}
					SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
						public void run() {
							endGame();
						  }
						}, 20 * SkyWarsReloaded.get().getConfig().getInt("gameVariables.timeAfterGame"));
				} else {
					endGame();
				}
    			
			} else if (getPlayers().size() < 1) {
				gameState = GameState.ENDING;
				if (bungeeMode) {
        			BungeeUtil.sendSignUpdateRequest(this);
        		}
    			if (signJoinMode) {
    				SkyWarsReloaded.getGC().updateSign(gameNumber);
    			}
				if (allowSpectating) {
					sendGameMessage(new Messaging.MessageFormatter().withPrefix()
							.setVariable("time", "" + SkyWarsReloaded.get().getConfig().getInt("gameVariables.timeAfterGame"))
							.format("game.gameEnding"));
					SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
						public void run() {
							endGame();
						  }
						}, 20 * SkyWarsReloaded.get().getConfig().getInt("gameVariables.timeAfterGame"));
				} else {
					endGame();
				}
			}
		}
	}
	
	private void sendGameMessage(String message) {
		for (GamePlayer gPlayer: getPlayers()) {
			if (gPlayer.getP() != null) {
				gPlayer.getP().sendMessage(message);
			}
		}
		if (allowSpectating) {
			for (GamePlayer spectator: getSpectators()) {
				if (spectator.getP() != null) {
					spectator.getP().sendMessage(message);
				}
			}
		}
	}
	
	public void playSound(String sound) {
		for (GamePlayer gamePlayer: getPlayers()) {
			if (gamePlayer.getP() != null) {
				gamePlayer.getP().playSound(gamePlayer.getP().getLocation(), Sound.valueOf(sound.toUpperCase()), 1, 1);
			}
		}
		for (GamePlayer gamePlayer: getSpectators()) {
			if(!gPlayers.containsKey(gamePlayer)) {
				if (gamePlayer.getP() != null) {
					gamePlayer.getP().playSound(gamePlayer.getP().getLocation(), Sound.valueOf(sound.toUpperCase()), 1, 1);
				}

			}
		}
	}

	private String getDeathMessage(DamageCause dCause, boolean withHelp, GamePlayer target, GamePlayer killer) {
		String first = "";
		int killTotal = killValue;
		if (killer.getP() != null) {
			if (SkyWarsReloaded.perms.has(killer.getP(), "swr.vip")) {
				killTotal = killValue * vipMultiplier;
			} else {
				killTotal = killValue;
			}
		}
		String second = new Messaging.MessageFormatter()
						.setVariable("killer", killer.getName())
						.setVariable("killer_score", StringUtils.formatScore(killTotal))
						.format("game.death.killer-section");
		
		if (dCause.equals(DamageCause.BLOCK_EXPLOSION) || dCause.equals(DamageCause.ENTITY_EXPLOSION)) {
			first = new Messaging.MessageFormatter()
					.withPrefix()
					.setVariable("player", target.getName())
					.setVariable("player_score", StringUtils.formatScore(-deathValue))
					.format("game.death.explosion");
		} else if (dCause.equals(DamageCause.DROWNING)) {
			first = new Messaging.MessageFormatter()
			.withPrefix()
			.setVariable("player", target.getName())
			.setVariable("player_score", StringUtils.formatScore(-deathValue))
			.format("game.death.drowning");
		} else if (dCause.equals(DamageCause.FIRE) || dCause.equals(DamageCause.FIRE_TICK)) {
			first = new Messaging.MessageFormatter()
			.withPrefix()
			.setVariable("player", target.getName())
			.setVariable("player_score", StringUtils.formatScore(-deathValue))
			.format("game.death.fire");
		} else if (dCause.equals(DamageCause.ENTITY_ATTACK)) {
			first = new Messaging.MessageFormatter()
			.withPrefix()
			.setVariable("player", target.getName())
			.setVariable("player_score", StringUtils.formatScore(-deathValue))
			.setVariable("killer", killer.getName())
			.setVariable("killer_score", StringUtils.formatScore(killTotal))
			.format("game.death.pvp");
			second = "";
		} else if (dCause.equals(DamageCause.FALLING_BLOCK)) {
			first = new Messaging.MessageFormatter()
			.withPrefix()
			.setVariable("player", target.getName())
			.setVariable("player_score", StringUtils.formatScore(-deathValue))
			.format("game.death.falling-block");
		} else if (dCause.equals(DamageCause.LAVA)) {
			first = new Messaging.MessageFormatter()
			.withPrefix()
			.setVariable("player", target.getName())
			.setVariable("player_score", StringUtils.formatScore(-deathValue))
			.format("game.death.lava");
		} else if (dCause.equals(DamageCause.PROJECTILE)) {
			first = new Messaging.MessageFormatter()
			.withPrefix()
			.setVariable("player", target.getName())
			.setVariable("player_score", StringUtils.formatScore(-deathValue))
			.setVariable("killer", killer.getName())
			.setVariable("killer_score", StringUtils.formatScore(killTotal))
			.format("game.death.projectile");
			second = "";
		} else if (dCause.equals(DamageCause.SUFFOCATION)) {
			first = new Messaging.MessageFormatter()
			.withPrefix()
			.setVariable("player", target.getName())
			.setVariable("player_score", StringUtils.formatScore(-deathValue))
			.format("game.death.suffocation");
		} else if (dCause.equals(DamageCause.VOID)) {
			first = new Messaging.MessageFormatter()
			.withPrefix()
			.setVariable("player", target.getName())
			.setVariable("player_score", StringUtils.formatScore(-deathValue))
			.format("game.death.void");
		} else {
			first = new Messaging.MessageFormatter()
			.withPrefix()
			.setVariable("player", target.getName())
			.setVariable("player_score", StringUtils.formatScore(-deathValue))
			.format("game.death.general");
		}
		
		if (withHelp) {
			return first + second;
		} else {
			return first + "!";
		}
	}

	public int getGameNumber() {
		return gameNumber;
	}
	
	public int getNumberOfSpawns() {
		return numberOfSpawns;
	}
	
	public boolean containsPlayer(Player player) {
		for (GamePlayer gPlayer: gPlayers.keySet()) {
			if (gPlayer.getP() == player) {
				return true;
			}
		}
		return false;
	}

	public World getMapWorld() {
		return mapWorld;
	}

	public void addSpectator(GamePlayer gPlayer) {
		spectators.put(gPlayer, gPlayer.getName());
	}

	public Location getSpawn() {
		return specSpawn;
	}

	public String getMapName() {
		return mapName;
	}

	public Scoreboard getScoreboard() {
		return scoreboard;
	}

	public void launchFirework(Player p, int speed) {
	    Firework fw = (Firework) p.getWorld().spawn(p.getEyeLocation(), Firework.class);
	    FireworkMeta meta = fw.getFireworkMeta();
	    FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(Color.YELLOW,Color.ORANGE).withFade(Color.RED).with(Type.STAR).trail(true).build();
	    meta.addEffect(effect);
	    fw.setVelocity(p.getLocation().getDirection().multiply(speed));
	}
	
	public void removeSpectator(GamePlayer gPlayer) {
		gPlayer.spectateMode(false, this);
		spectators.remove(gPlayer);
		if (bungeeMode) {
			if (gPlayer.getP() != null) {
				gPlayer.getP().teleport(spawn);
				BungeeUtil.connectToServer(gPlayer.getP(), SkyWarsReloaded.get().getConfig().getString("bungeeMode.lobbyServer"));
			}
		} else {
			if (spawn != null) {
				if (gPlayer.getP() != null) {
					gPlayer.getP().teleport(spawn);
				}
			} else {
				if (gPlayer.getP() != null) {
					gPlayer.getP().teleport(gPlayer.getRespawn());
				}
			}
		}
		if (gPlayer.getP() != null) {
			SkyWarsReloaded.getScore().getScoreboard(gPlayer.getP());
		}
	}
	
	public void sendSpectatorMessage(Player player, String message) {
		GamePlayer sender = SkyWarsReloaded.getPC().getPlayer(player.getUniqueId());
		int score = sender.getScore();
    	String sValue;
		if (score < 0) {
            sValue = ChatColor.RED + "(-" + score + ")";
    	} else {
    		sValue = ChatColor.GREEN + "(+" + score + ")";
    	}
		String name = player.getDisplayName();
		for (GamePlayer gPlayer: getSpectators()) {
			if (gPlayer.getP() != null) {
				gPlayer.getP().sendMessage(new Messaging.MessageFormatter()
				.setVariable("score", sValue)
				.setVariable("player", name)
				.setVariable("message", message)
				.format("spectatorchat"));
			}
		}
	}

	
    public boolean hasTimePermission(Player player) {
        return player.isOp() || SkyWarsReloaded.perms.has(player, "swr.timevote");
    }
    
	private void setTime() {
		String time = getTime();
		World world = SkyWarsReloaded.get().getServer().getWorld(mapName + "_" + gameNumber);
		if (time.equalsIgnoreCase("dawn")) {
			world.setTime(0);
		} else if (time.equalsIgnoreCase("noon")) {
			world.setTime(6000);
		} else if (time.equalsIgnoreCase("dusk")) {
			world.setTime(12000);
		} else if (time.equalsIgnoreCase("midnight")) {
			world.setTime(18000);
		}
	}
	
	private String getTime() {
        int votesForDawn = 0;
        int votesForNoon = 0;
        int votesForDusk = 0;
        int votesForMidnight = 0;
    	for (GamePlayer gPlayer: getPlayers()) {
    		if (gPlayer.getTimeVote() == 1) {
    			votesForDawn++;
    		} else if (gPlayer.getTimeVote() == 2) {
    			votesForNoon++;
    		} else if (gPlayer.getTimeVote() == 3) {
    			votesForDusk++;
    		} else if (gPlayer.getTimeVote() == 4) {
    			votesForMidnight++;
    		}
    	}
    	if (votesForDawn >= votesForNoon && votesForDawn >= votesForDusk && votesForDawn >= votesForMidnight) {
    		return "dawn";
    	} else if (votesForNoon >= votesForDusk && votesForNoon >= votesForMidnight) {
    		return "noon";
    	} else if (votesForDusk >= votesForMidnight) {
    		return "dusk";
    	} else {
    		return "midnight";
    	}
	}
    
    public void openTimeMenu(final GamePlayer gamePlayer) {

        int rowCount = menuSlotsPerRow;
        while (rowCount < 8 && rowCount < menuSize) {
            rowCount += menuSlotsPerRow;
        }

        SkyWarsReloaded.getIC().create(gamePlayer.getP(), timeMenuName, rowCount, new IconMenu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IconMenu.OptionClickEvent event) {    
                String vote = event.getName();

                event.setWillClose(true);
                event.setWillDestroy(true);
                
            	if (!hasTimePermission(event.getPlayer())) {
            		event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("error.no-permission-time"));
                    return;
            	} 
                
                int lastVote = gamePlayer.getTimeVote();
                
                if (vote.equalsIgnoreCase("dawn")) {
                	gamePlayer.setTimeVote(1);
                } else if (vote.equalsIgnoreCase("noon")) {
                	gamePlayer.setTimeVote(2);
                } else if (vote.equalsIgnoreCase("dusk")) {
                	gamePlayer.setTimeVote(3);
                } else if (vote.equalsIgnoreCase("midnight")) {
                	gamePlayer.setTimeVote(4);
                }
                
                if (gamePlayer.getTimeVote() != lastVote) {
                	Game game = gamePlayer.getGame();
                	for (GamePlayer gPlayer: game.getPlayers()) {
                		if (gPlayer.getP() != null) {
                    		gPlayer.getP().sendMessage(new Messaging.MessageFormatter()
        					.withPrefix()
        					.setVariable("player", gamePlayer.getName())
        					.setVariable("time", vote.toUpperCase())
        					.format("game.voted-for-time"));
                		}
                	}
                	game.playSound(voteForTimeSound);
                }
            }
        });

        Game game = gamePlayer.getGame();
        int votesForDawn = 0;
        int votesForNoon = 0;
        int votesForDusk = 0;
        int votesForMidnight = 0;
    	for (GamePlayer gPlayer: game.getPlayers()) {
    		if (gPlayer.getTimeVote() == 1) {
    			votesForDawn++;
    		} else if (gPlayer.getTimeVote() == 2) {
    			votesForNoon++;
    		} else if (gPlayer.getTimeVote() == 3) {
    			votesForDusk++;
    		} else if (gPlayer.getTimeVote() == 4) {
    			votesForMidnight++;
    		}
    	}
        
    	if (gamePlayer.getP() != null) {
            List<String> loreList = Lists.newLinkedList();
            loreList.add(new Messaging.MessageFormatter().format("menu.time-dawn"));
            SkyWarsReloaded.getIC().setOption(
    	                    gamePlayer.getP(),
    	                    1,
    	                    new ItemStack(Material.WATCH, votesForDawn),
    	                    "DAWN",
    	                    loreList.toArray(new String[loreList.size()]));
            
            List<String> loreList3 = Lists.newLinkedList();
            loreList3.add(new Messaging.MessageFormatter().format("menu.time-noon"));
            SkyWarsReloaded.getIC().setOption(
    	                    gamePlayer.getP(),
    	                    3,
    	                    new ItemStack(Material.WATCH, votesForNoon),
    	                    "NOON",
    	                    loreList3.toArray(new String[loreList3.size()]));
            
            List<String> loreList2 = Lists.newLinkedList();
            loreList2.add(new Messaging.MessageFormatter().format("menu.time-dusk"));
            SkyWarsReloaded.getIC().setOption(
    	                    gamePlayer.getP(),
    	                    5,
    	                    new ItemStack(Material.WATCH, (votesForDusk)),
    	                    "DUSK",
    	                    loreList2.toArray(new String[loreList2.size()]));

            List<String> loreList4 = Lists.newLinkedList();
            loreList4.add(new Messaging.MessageFormatter().format("menu.time-midnight"));
            SkyWarsReloaded.getIC().setOption(
    	                    gamePlayer.getP(),
    	                    7,
    	                    new ItemStack(Material.WATCH, (votesForMidnight)),
    	                    "MIDNIGHT",
    	                    loreList4.toArray(new String[loreList4.size()]));
            SkyWarsReloaded.getIC().show(gamePlayer.getP());
    	}
    }
	
	private void setJump() {
		String jump = getJump();
		if (jump.equalsIgnoreCase("normal")) {
		} else if (jump.equalsIgnoreCase("high jump")) {
			for (GamePlayer gPlayer: getPlayers()) {
				if (gPlayer.getP() != null) {
					gPlayer.getP().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 2));
				}
			}
		} else if (jump.equalsIgnoreCase("super jump")) {
			for (GamePlayer gPlayer: getPlayers()) {
				if (gPlayer.getP() != null) {
					gPlayer.getP().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 5));
				}
			}
		} else if (jump.equalsIgnoreCase("god jump")) {
			for (GamePlayer gPlayer: getPlayers()) {
				if (gPlayer.getP() != null) {
					gPlayer.getP().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 10));
				}
			}
		}
	}
	
	private String getJump() {
        int votesForNormal = 0;
        int votesForHigh = 0;
        int votesForSuper = 0;
        int votesForGod = 0;
    	for (GamePlayer gPlayer: getPlayers()) {
    		if (gPlayer.getJumpVote() == 1) {
    			votesForNormal++;
    		} else if (gPlayer.getJumpVote() == 2) {
    			votesForHigh++;
    		} else if (gPlayer.getJumpVote() == 3) {
    			votesForSuper++;
    		} else if (gPlayer.getJumpVote() == 4) {
    			votesForGod++;
    		}
    	}
    	if (votesForNormal >= votesForHigh && votesForNormal >= votesForSuper && votesForNormal >= votesForGod) {
    		return "normal";
    	} else if (votesForHigh >= votesForSuper && votesForHigh >= votesForGod) {
    		return "high jump";
    	} else if (votesForSuper >= votesForGod) {
    		return "super jump";
    	} else {
    		return "god jump";
    	}
	}
    
    public boolean hasJumpPermission(Player player) {
        return player.isOp() || SkyWarsReloaded.perms.has(player, "swr.jumpvote");
    }
    
    public void openJumpMenu(final GamePlayer gamePlayer) {

        int rowCount = menuSlotsPerRow;
        while (rowCount < 8 && rowCount < menuSize) {
            rowCount += menuSlotsPerRow;
        }

        SkyWarsReloaded.getIC().create(gamePlayer.getP(), jumpMenuName, rowCount, new IconMenu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IconMenu.OptionClickEvent event) {    
                String vote = event.getName();

                event.setWillClose(true);
                event.setWillDestroy(true);
                
            	if (!hasJumpPermission(event.getPlayer())) {
            		event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("error.no-permission-jump"));
                    return;
            	} 
                
                int lastVote = gamePlayer.getJumpVote();
                
                if (vote.equalsIgnoreCase("normal")) {
                	gamePlayer.setJumpVote(1);
                } else if (vote.equalsIgnoreCase("high jump")) {
                	gamePlayer.setJumpVote(2);
                } else if (vote.equalsIgnoreCase("super jump")) {
                	gamePlayer.setJumpVote(3);
                } else if (vote.equalsIgnoreCase("god jump")) {
                	gamePlayer.setJumpVote(4);
                }
                
                if (gamePlayer.getJumpVote() != lastVote) {
                	Game game = gamePlayer.getGame();
                	for (GamePlayer gPlayer: game.getPlayers()) {
                		if (gPlayer.getP() != null) {
                    		gPlayer.getP().sendMessage(new Messaging.MessageFormatter()
        					.withPrefix()
        					.setVariable("player", gamePlayer.getName())
        					.setVariable("jump", vote.toUpperCase())
        					.format("game.voted-for-jump"));
                		}
                	}
                	game.playSound(voteForTimeSound);
                }
            }
        });

        Game game = gamePlayer.getGame();
        int votesForNormal = 0;
        int votesForHigh = 0;
        int votesForSuper = 0;
        int votesForGod = 0;
    	for (GamePlayer gPlayer: game.getPlayers()) {
    		if (gPlayer.getJumpVote() == 1) {
    			votesForNormal++;
    		} else if (gPlayer.getJumpVote() == 2) {
    			votesForHigh++;
    		} else if (gPlayer.getJumpVote() == 3) {
    			votesForSuper++;
    		} else if (gPlayer.getJumpVote() == 4) {
    			votesForGod++;
    		}
    	}
        
    	if (gamePlayer.getP() != null) {
            List<String> loreList = Lists.newLinkedList();
            loreList.add(new Messaging.MessageFormatter().format("menu.jump-normal"));
            SkyWarsReloaded.getIC().setOption(
    	                    gamePlayer.getP(),
    	                    1,
    	                    new ItemStack(Material.DIAMOND_BOOTS, votesForNormal),
    	                    "NORMAL",
    	                    loreList.toArray(new String[loreList.size()]));
            
            List<String> loreList3 = Lists.newLinkedList();
            loreList3.add(new Messaging.MessageFormatter().format("menu.jump-highjump"));
            SkyWarsReloaded.getIC().setOption(
    	                    gamePlayer.getP(),
    	                    3,
    	                    new ItemStack(Material.DIAMOND_BOOTS, votesForHigh),
    	                    "HIGH JUMP",
    	                    loreList3.toArray(new String[loreList3.size()]));
            
            List<String> loreList2 = Lists.newLinkedList();
            loreList2.add(new Messaging.MessageFormatter().format("menu.jump-superjump"));
            SkyWarsReloaded.getIC().setOption(
    	                    gamePlayer.getP(),
    	                    5,
    	                    new ItemStack(Material.DIAMOND_BOOTS, (votesForSuper)),
    	                    "SUPER JUMP",
    	                    loreList2.toArray(new String[loreList2.size()]));

            List<String> loreList4 = Lists.newLinkedList();
            loreList4.add(new Messaging.MessageFormatter().format("menu.jump-godjump"));
            SkyWarsReloaded.getIC().setOption(
    	                    gamePlayer.getP(),
    	                    7,
    	                    new ItemStack(Material.DIAMOND_BOOTS, (votesForGod)),
    	                    "GOD JUMP",
    	                    loreList4.toArray(new String[loreList4.size()]));
            SkyWarsReloaded.getIC().show(gamePlayer.getP());
    	}
    }
    
    private void removeBalance(GamePlayer p, int x) {
    	boolean economy = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.useExternalEconomy");
    	if (economy) {
        	SkyWarsReloaded.econ.withdrawPlayer(SkyWarsReloaded.get().getServer().getOfflinePlayer(p.getUUID()), x);
    	} else {
    		p.setBalance(p.getBalance() - x);
    	}
    }
    
    private void addBalance(GamePlayer p, int x) {
    	boolean economy = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.useExternalEconomy");
    	if (economy) {
    		SkyWarsReloaded.econ.depositPlayer(SkyWarsReloaded.get().getServer().getOfflinePlayer(p.getUUID()), x);
    	} else {
    		p.setBalance(p.getBalance() + x);
    	}
    }
}
