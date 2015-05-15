package com.walrusone.skywars.game;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
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
import org.bukkit.block.Biome;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_8_R2.CraftServer;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.Entity;
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

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.api.GameEndEvent;
import com.walrusone.skywars.utilities.BungeeUtil;
import com.walrusone.skywars.utilities.EmptyChest;
import com.walrusone.skywars.utilities.GlassColor;
import com.walrusone.skywars.utilities.Messaging;
import com.walrusone.skywars.utilities.StringUtils;


public class Game {

    private List<GamePlayer> gPlayers = new ArrayList<GamePlayer>();
	private List<GamePlayer> spectators = new ArrayList<GamePlayer>();
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
	private boolean shutdown = false;
	private Map<Integer, GamePlayer> availableSpawns = new HashMap<Integer, GamePlayer>();
	private Map<String, Integer> scoreboardData = new HashMap<String, Integer>();
	private ItemStack kit;
	private ItemStack options;
	private ItemStack exit;
	private String winSound;
	private String deathSound;
	private String joinLobbySound;
	private String leaveLobbySound;
	private Location specSpawn;
	
	public Game(int gameNumber, String map) {
		this.gameNumber = gameNumber;
		bungeeMode = SkyWarsReloaded.get().getConfig().getBoolean("bungeeMode.enabled");
		signJoinMode = SkyWarsReloaded.get().getConfig().getBoolean("signJoinMode");
		kit = SkyWarsReloaded.getGC().getKitItem();
		options = SkyWarsReloaded.getGC().getOptionsItem();
		exit  = SkyWarsReloaded.getGC().getExitItem();
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
		String world = SkyWarsReloaded.get().getConfig().getString("spawn.world");
		int x = SkyWarsReloaded.get().getConfig().getInt("spawn.x");
		int y = SkyWarsReloaded.get().getConfig().getInt("spawn.y");
		int z = SkyWarsReloaded.get().getConfig().getInt("spawn.z");
		float yaw = SkyWarsReloaded.get().getConfig().getInt("spawn.yaw");
		float pitch = SkyWarsReloaded.get().getConfig().getInt("spawn.pitch");
		if (world != null) {
			spawn = new Location(SkyWarsReloaded.get().getServer().getWorld(world), x, y, z, yaw, pitch);
		} else {
			SkyWarsReloaded.get().getLogger().info("YOU MUST SET SPAWN IN THE LOBBY WORLD WITH /SWR SETSPAWN BEFORE STARTING A GAME");
			endGame();
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
		if (gPlayers.size() >= numberOfSpawns || gPlayers.contains(gPlayer)) {
			return;
		} else {
			if (gPlayer.getP() != null) {
				gPlayers.add(gPlayer);
				gPlayer.setGame(gameNumber);
				gPlayer.setInGame(true);
				if (bungeeMode) {
					BungeeUtil.sendSignUpdateRequest(this);
				}
				if (signJoinMode) {
					SkyWarsReloaded.getGC().updateSign(gameNumber);
				}
				SkyWarsReloaded.getInvC().add(gPlayer.getP());
				preparePlayerForLobby(gPlayer);
				boolean resetTimer = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.resetPreGameTimerOnJoin");
				if (resetTimer) {
					count = 0;
				}
			} 
		}
	}
	
	private void preparePlayerForLobby(final GamePlayer gPlayer) {
		Location location;
		int spawn = getAvailableSpawn();
		location = new Location(mapWorld, gameMap.getSpawns().get(spawn).getX()+0.5, gameMap.getSpawns().get(spawn).getY(), gameMap.getSpawns().get(spawn).getZ()+0.5);
		availableSpawns.put(spawn,  gPlayer);
		gPlayer.getP().teleport(location);
		SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
			public void run() {
				if (gPlayer.getP() != null) {
					gPlayer.getP().getInventory().clear();
					gPlayer.getP().getInventory().setHelmet(null);
				    gPlayer.getP().getInventory().setChestplate(null);
				    gPlayer.getP().getInventory().setLeggings(null);
				    gPlayer.getP().getInventory().setBoots(null);
				    gPlayer.getP().setLevel(0);
				    gPlayer.getP().setExp(0);
					gPlayer.getP().setHealth(20);
					gPlayer.getP().setFoodLevel(20);
					gPlayer.getP().setFlying(false);
					gPlayer.setOpVote(0);
					gPlayer.setWeatherVote(0);
					gPlayer.setTimeVote(0);
					gPlayer.setJumpVote(0);
					for (PotionEffect effect : gPlayer.getP().getActivePotionEffects()) {
				        gPlayer.getP().removePotionEffect(effect.getType());
					}
					gPlayer.getP().setGameMode(GameMode.ADVENTURE);
					scoreboardData.put(gPlayer.getP().getName(), 1);
					gPlayer.getP().setScoreboard(SkyWarsReloaded.get().getServer().getScoreboardManager().getNewScoreboard());
					gPlayer.getP().setScoreboard(scoreboard);
					updateScoreboard();
					gPlayer.getP().getInventory().setItem(8, exit);
					gPlayer.getP().getInventory().setItem(4, options);
					boolean enabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.kitsEnabled");
					if (enabled) {
						gPlayer.getP().getInventory().setItem(0, kit);
					}
					String color = gPlayer.getGlass();
					if (color == null) {
						color = "normal";
					}
					if (!color.equalsIgnoreCase("normal")) {
						GlassColor colorGlass = SkyWarsReloaded.getGLC().getByName(color);
						setGlass(colorGlass.getMaterial(), colorGlass.getData(), gPlayer);
					} else {
						setGlass(Material.GLASS, gPlayer);
					}
				}
			  }
			}, 5);
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
					if ((preGameTimer - count) % 5 == 0) {
						sendGameMessage(new Messaging.MessageFormatter()
    					.withPrefix()
    					.setVariable("time", "" + (preGameTimer - count))
    					.format("game.countdown-continue"));
					} else if ((preGameTimer - count) < 5 ) {
						sendGameMessage(new Messaging.MessageFormatter()
    					.withPrefix()
    					.setVariable("time", "" + (preGameTimer - count))
    					.format("game.countdown-continue"));
					}

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
			    gPlayer.getP().getInventory().remove(options);
			    gPlayer.getP().getInventory().remove(exit);
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
	    boolean weatherEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.weatherVoteEnabled");
		if (weatherEnabled) {
			SkyWarsReloaded.getMV().getMVWorldManager().getMVWorld(mapWorld).setEnableWeather(true);
			setWeather();
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
			if (player != null) {
				player.teleport(spawn);
			}
		}
		
		for (Entity entity: mapWorld.getEntities()) {
			if (entity != null) {
				entity.remove();
			}
		}
		if (!allowSpectating && !shutdown) {
				SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
					@Override
					public void run() {
						deleteGame();
					}
				}, 100);
		} else {
			deleteGame();
		}
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
	
	private void updateScoreboard() {
        if (objective != null) {
            objective.unregister();
        }
        objective = scoreboard.registerNewObjective("info", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        String leaderboard = new Messaging.MessageFormatter().setVariable("mapname", mapName.toUpperCase()).format("game.scoreboard-title");
        objective.setDisplayName(leaderboard);
		
		boolean players = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.playerNameScoreboard");
		if (players) {
			for (String name: scoreboardData.keySet()) {
				if (scoreboardData.get(name) == 0) {
					Score score = objective.getScore(ChatColor.RED + name);
					score.setScore(scoreboardData.get(name));
				} else {
					Score score = objective.getScore(ChatColor.GREEN + name);
					score.setScore(scoreboardData.get(name));
				}
			}
		} else {
			Score score = objective.getScore(new Messaging.MessageFormatter().format("game.scoreboard-players"));
			score.setScore(getPlayers().size());
		}
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
		if (gPlayers.contains(gplayer)) {
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
				SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
					public void run() {
						SkyWarsReloaded.getInvC().restoreInventory(gplayer.getP());
						BungeeUtil.connectToServer(gplayer.getP(), SkyWarsReloaded.get().getConfig().getString("bungeeMode.lobbyServer"));
					}
				}, 5);
			}
		} else {
			if (gplayer.getP() != null) {
				gplayer.getP().teleport(spawn);
				if (!shutdown) {
					SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
						public void run() {
							if (gplayer.getP() != null) {
								SkyWarsReloaded.getInvC().restoreInventory(gplayer.getP());
								SkyWarsReloaded.getScore().getScoreboard(gplayer.getP());
							}
						}
					}, 5);
				}
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
		gPlayer.setInGame(false);
		GamePlayer killer = gPlayer.getTagged().getPlayer();
		if (gameState == GameState.PLAYING) {
			scoreboardData.put(gPlayer.getP().getName(), 0);
			updateScoreboard();
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
				scoreboardData.remove(gPlayer.getP().getName());
				updateScoreboard();
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
			gplayer.setInGame(false);
			gplayer.setKitSelected(false);
			gplayer.setOpVote(0);
			gplayer.setWeatherVote(0);
			gplayer.setTimeVote(0);
			gplayer.setJumpVote(0);
			gplayer.getP().setScoreboard(SkyWarsReloaded.get().getServer().getScoreboardManager().getNewScoreboard());
			for (PotionEffect effect : gplayer.getP().getActivePotionEffects()) {
		        gplayer.getP().removePotionEffect(effect.getType());
			}
			gplayer.getP().setFireTicks(0);
			gplayer.setGame(-1);
		}
	}
		
	public Boolean playerExists(GamePlayer name) {
		for (GamePlayer gPlayer: gPlayers) {
			if (name.getUUID().toString().equalsIgnoreCase(gPlayer.getUUID().toString())) {
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<GamePlayer> getPlayers() {
		ArrayList<GamePlayer> players = new ArrayList<GamePlayer>();
		for (GamePlayer g: gPlayers) {
			players.add(g);
		}
		return players;
	}
	
	public ArrayList<GamePlayer> getSpectators() {
		ArrayList<GamePlayer> players = new ArrayList<GamePlayer>();
		for (GamePlayer g: spectators) {
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
		scoreboardData.put(target.getP().getName(), 0);
		updateScoreboard();
		preparePlayerForExit(target);

		if (allowSpectating) {
			target.setSpectating(true);
			target.setSpecGame(gameNumber);
		} 
		
		final GamePlayer respawn = target;
		if (target.getP() != null) {
			SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {

				@Override
				public void run() {
					respawnPlayer(respawn.getP());
					if (!allowSpectating) {
						deletePlayer(respawn, false, false);
					}
				}
				
			}, 1);

		}
		
		updateScoreboard();
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
				if (target.getP() != null) {
					target.getP().sendMessage(getDeathMessage(dCause, true, target, killer));
					target.getP().playSound(target.getP().getLocation(), Sound.valueOf(deathSound.toUpperCase()), 1, 1);
				}
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
    			preparePlayerForExit(gPlayer);
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
 				Bukkit.getServer().getPluginManager().callEvent(new GameEndEvent(gPlayer.getP(), mapName));
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
						gPlayer.spectateMode(true, this, gPlayer.getP().getLocation(), shutdown);
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
					deletePlayer(gPlayer, false, false);
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
			if(!gPlayers.contains(gamePlayer)) {
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
		for (GamePlayer gPlayer: gPlayers) {
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
		spectators.add(gPlayer);
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
		spectators.remove(gPlayer);
		if (bungeeMode) {
			if (gPlayer.getP() != null) {
				gPlayer.spectateMode(false, this, spawn, shutdown);
				BungeeUtil.connectToServer(gPlayer.getP(), SkyWarsReloaded.get().getConfig().getString("bungeeMode.lobbyServer"));
			}
		} else {
			if (gPlayer.getP() != null) {
				gPlayer.spectateMode(false, this, spawn, shutdown);
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
    
	private void setWeather() {
		String weather = getWeather();
		final World world = SkyWarsReloaded.get().getServer().getWorld(mapName + "_" + gameNumber);
		if (weather.equalsIgnoreCase("sunny")) {
			world.setStorm(false);
			world.setWeatherDuration(Integer.MAX_VALUE);
		} else if (weather.equalsIgnoreCase("rain")) {
			world.setStorm(true);
			world.setWeatherDuration(Integer.MAX_VALUE);
		} else if (weather.equalsIgnoreCase("thunder storm")) {
			world.setStorm(true);
			world.setWeatherDuration(Integer.MAX_VALUE);
			world.setThundering(true);
			world.setThunderDuration(Integer.MAX_VALUE);
		} else if (weather.equalsIgnoreCase("snow")) {
			int size = SkyWarsReloaded.get().getConfig().getInt("gameVariables.maxMapSize")/2;
			int min = 0 - size;
			int max = 0 + size;
			for (int x = min; x < max; x++) {
				for (int z = min; z < max; z++) {
					world.setBiome(x, z, Biome.ICE_PLAINS);
				}
			}
			world.setStorm(true);
			world.setWeatherDuration(Integer.MAX_VALUE);
			for (GamePlayer gPlayer: getPlayers()) {
				if (gPlayer.getP() != null) {
					final Location current = gPlayer.getP().getLocation();
					final Player player = gPlayer.getP();
					gPlayer.getP().teleport(new Location(mapWorld, 2000, 0, 2000));
					SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
						public void run() {
							if (player != null) {
								player.teleport(current);
							}
						}
					}, 3);
				}
			}
		}
	}
	
	private String getWeather() {
        int votesForSunny = 0;
        int votesForRain = 0;
        int votesForThunder = 0;
        int votesForSnow = 0;
    	for (GamePlayer gPlayer: getPlayers()) {
    		if (gPlayer.getWeatherVote() == 1) {
    			votesForSunny++;
    		} else if (gPlayer.getWeatherVote() == 2) {
    			votesForRain++;
    		} else if (gPlayer.getWeatherVote() == 3) {
    			votesForThunder++;
    		} else if (gPlayer.getWeatherVote() == 4) {
    			votesForSnow++;
    		}
    	}
    	if (votesForSunny >= votesForRain && votesForSunny >= votesForThunder && votesForSunny >= votesForSnow) {
    		return "sunny";
    	} else if (votesForRain >= votesForThunder && votesForRain >= votesForSnow) {
    		return "rain";
    	} else if (votesForThunder >= votesForSnow) {
    		return "thunder storm";
    	} else {
    		return "snow";
    	}
	}
	
	private void setJump() {
		String jump = getJump();
		if (jump.equalsIgnoreCase("normal")) {
		} else if (jump.equalsIgnoreCase("high jump")) {
			for (GamePlayer gPlayer: getPlayers()) {
				if (gPlayer.getP() != null) {
					gPlayer.getP().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, SkyWarsReloaded.get().getConfig().getInt("gameVariables.highJumpValue")));
				}
			}
		} else if (jump.equalsIgnoreCase("super jump")) {
			for (GamePlayer gPlayer: getPlayers()) {
				if (gPlayer.getP() != null) {
					gPlayer.getP().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, SkyWarsReloaded.get().getConfig().getInt("gameVariables.superJumpValue")));
				}
			}
		} else if (jump.equalsIgnoreCase("god jump")) {
			for (GamePlayer gPlayer: getPlayers()) {
				if (gPlayer.getP() != null) {
					gPlayer.getP().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, SkyWarsReloaded.get().getConfig().getInt("gameVariables.godJumpValue")));
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
    
    public void respawnPlayer(Player paramPlayer) {
    	if (paramPlayer != null) {
    		if (paramPlayer.isDead()) {
      		  String bukkitversion = Bukkit.getServer().getClass().getPackage().getName().substring(23);
      		  if (bukkitversion.equalsIgnoreCase("v1_8_R2")) {
          		  ((CraftServer)Bukkit.getServer()).getHandle().moveToWorld(((CraftPlayer)paramPlayer).getHandle(), 0, false);
      		  } else {
      			  try {
      		            String path = "net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
      		            Object nmsPlayer = paramPlayer.getClass().getMethod("getHandle").invoke(paramPlayer);
      		            Object respawnEnum = Class.forName(path + ".EnumClientCommand").getEnumConstants()[0];
      		            Constructor<?>[] constructors = Class.forName(path + ".PacketPlayInClientCommand").getConstructors();
      		            for (Constructor<?> constructor : constructors) {
      		                Class<?>[] args = constructor.getParameterTypes();
      		                if (args.length == 1 && args[0] == respawnEnum.getClass()) {
      		                    Object packet = Class.forName(path + ".PacketPlayInClientCommand").getConstructor(args).newInstance(respawnEnum);
      		                    Object connection = nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);
      		                    connection.getClass().getMethod("a", packet.getClass()).invoke(connection, packet);
      		                    break;
      		                }
      		            }
      		        } catch (Throwable e) {
      		            e.printStackTrace();
      		        }
      		  }
      	  }
    	}
    }

    public int getPlayerSpawn(GamePlayer gPlayer) {
    	for (int spawn: availableSpawns.keySet()) {
    		if (availableSpawns.get(spawn) == gPlayer) {
    			return spawn;
    		}
    	}
		return -1;
    }
    
	public void setGlass(Material color, GamePlayer gPlayer) {
		if (containsPlayer(gPlayer.getP()) && gameState == GameState.PREGAME) {
			Material material = color;
			
			int spawn = getPlayerSpawn(gPlayer);
			if (spawn != -1) {
	            int x = gameMap.getSpawns().get(spawn).getBlockX();
	            int y = gameMap.getSpawns().get(spawn).getBlockY();
	            int z = gameMap.getSpawns().get(spawn).getBlockZ();

	            mapWorld.getBlockAt(x, y, z).setType(material);
	            mapWorld.getBlockAt(x, y + 1, z + 1).setType(material);
	            mapWorld.getBlockAt(x, y + 1, z - 1).setType(material);
	            mapWorld.getBlockAt(x + 1, y + 1, z).setType(material);
	            mapWorld.getBlockAt(x - 1, y + 1, z).setType(material);
	            mapWorld.getBlockAt(x, y + 2, z + 1).setType(material);
	            mapWorld.getBlockAt(x, y + 2, z - 1).setType(material);
	            mapWorld.getBlockAt(x + 1, y + 2, z).setType(material);
	            mapWorld.getBlockAt(x - 1, y + 2, z).setType(material);
	            mapWorld.getBlockAt(x, y + 3, z + 1).setType(material);
	            mapWorld.getBlockAt(x, y + 3, z - 1).setType(material);
	            mapWorld.getBlockAt(x + 1, y + 3, z).setType(material);
	            mapWorld.getBlockAt(x - 1, y + 3, z).setType(material);
	            mapWorld.getBlockAt(x, y + 4, z).setType(material);
			}
		}
	}
   
	@SuppressWarnings("deprecation")
	public void setGlass(Material color, byte data, GamePlayer gPlayer) {
		if (containsPlayer(gPlayer.getP()) && gameState == GameState.PREGAME) {
			Material material = color;
			
			int spawn = getPlayerSpawn(gPlayer);
			if (spawn != -1) {
	            int x = gameMap.getSpawns().get(spawn).getBlockX();
	            int y = gameMap.getSpawns().get(spawn).getBlockY();
	            int z = gameMap.getSpawns().get(spawn).getBlockZ();

	            mapWorld.getBlockAt(x, y, z).setType(material);
	            mapWorld.getBlockAt(x, y, z).setData(data);
	            mapWorld.getBlockAt(x, y + 1, z + 1).setType(material);
	            mapWorld.getBlockAt(x, y + 1, z + 1).setData(data);
	            mapWorld.getBlockAt(x, y + 1, z - 1).setType(material);
	            mapWorld.getBlockAt(x, y + 1, z - 1).setData(data);
	            mapWorld.getBlockAt(x + 1, y + 1, z).setType(material);
	            mapWorld.getBlockAt(x + 1, y + 1, z).setData(data);
	            mapWorld.getBlockAt(x - 1, y + 1, z).setType(material);
	            mapWorld.getBlockAt(x - 1, y + 1, z).setData(data);
	            mapWorld.getBlockAt(x, y + 2, z + 1).setType(material);
	            mapWorld.getBlockAt(x, y + 2, z + 1).setData(data);
	            mapWorld.getBlockAt(x, y + 2, z - 1).setType(material);
	            mapWorld.getBlockAt(x, y + 2, z - 1).setData(data);
	            mapWorld.getBlockAt(x + 1, y + 2, z).setType(material);
	            mapWorld.getBlockAt(x + 1, y + 2, z).setData(data);
	            mapWorld.getBlockAt(x - 1, y + 2, z).setType(material);
	            mapWorld.getBlockAt(x - 1, y + 2, z).setData(data);
	            mapWorld.getBlockAt(x, y + 3, z + 1).setType(material);
	            mapWorld.getBlockAt(x, y + 3, z + 1).setData(data);
	            mapWorld.getBlockAt(x, y + 3, z - 1).setType(material);
	            mapWorld.getBlockAt(x, y + 3, z - 1).setData(data);
	            mapWorld.getBlockAt(x + 1, y + 3, z).setType(material);
	            mapWorld.getBlockAt(x + 1, y + 3, z).setData(data);
	            mapWorld.getBlockAt(x - 1, y + 3, z).setType(material);
	            mapWorld.getBlockAt(x - 1, y + 3, z).setData(data);
	            mapWorld.getBlockAt(x, y + 4, z).setType(material);
	            mapWorld.getBlockAt(x, y + 4, z).setData(data);
			}
		}
	}
}
