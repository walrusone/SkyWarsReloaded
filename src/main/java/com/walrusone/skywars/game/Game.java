package com.walrusone.skywars.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.google.common.collect.Maps;
import com.pgcraft.spectatorplus.Arena;
import com.sk89q.worldedit.CuboidClipboard;
import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.bungee.BungeeSocket;
import com.walrusone.skywars.controllers.MapController;
import com.walrusone.skywars.utilities.BungeeUtil;
import com.walrusone.skywars.utilities.Messaging;
import com.walrusone.skywars.utilities.StringUtils;


@SuppressWarnings("deprecation")
public class Game {

	private CopyOnWriteArrayList<GamePlayer> gPlayers = new CopyOnWriteArrayList<GamePlayer>();
	private CopyOnWriteArrayList<GamePlayer> spectators = new CopyOnWriteArrayList<GamePlayer>();
	private int islandSize = SkyWarsReloaded.get().getConfig().getInt("gameVariables.maxMapSize") + 100;
	private int count = 0;
	private String bungeeName;
	private GameState gameState;
	private String mapName;
	private World mapWorld;
	private World lobbyWorld;
	private Map<String, Integer> signValues = Maps.newHashMap();
	private int gameNumber;
	private Scoreboard scoreboard;
	private Objective objective;
	private int minPlayers;
	private boolean votingEnded = false;
	private boolean votingTimerStarted = false;
	private CopyOnWriteArrayList<String> shuffleMaps;
	private boolean allowSpectating;
	private Location spawn;
	private int winValue;
	private int killValue;
	private int deathValue;
	private int leaveValue;
	private boolean economy;
	private boolean bungeeMode;
	private int numberOfSpawns;
	private boolean directToCages;
	private boolean shutdown;
	private Map<Integer, GamePlayer> availableSpawns = new HashMap<Integer, GamePlayer>();
	private Map<Integer, Location> spawns = new HashMap<Integer, Location>();
	private Map<Integer, Location> lobbySpawns = new HashMap<Integer, Location>();
	private ArrayList<Location> chests = new ArrayList<Location>();
	private Map<Integer, Location> signs = new HashMap<Integer, Location>();
	private int[] mapCoords;
	private int[] lobbyCoords;
	private ItemStack chest = new ItemStack(Material.STORAGE_MINECART, 1);
	private ItemStack door = new ItemStack(Material.IRON_DOOR, 1);
	
	
	public Game(int gameNumber) {
		bungeeName = SkyWarsReloaded.get().getConfig().getString("bungeeMode.name") + gameNumber;
		bungeeMode = SkyWarsReloaded.get().getConfig().getBoolean("bungeeMode.enabled");
		ItemMeta meta = chest.getItemMeta();
		meta.setDisplayName("Open Kit Menu");
		chest.setItemMeta(meta);
		chest.addUnsafeEnchantment(Enchantment.LUCK, 1);
		ItemMeta meta1 = door.getItemMeta();
		meta1.setDisplayName("Return to Spawn");
		door.setItemMeta(meta1);
		door.addUnsafeEnchantment(Enchantment.LUCK, 1);
		minPlayers = SkyWarsReloaded.get().getConfig().getInt("gameVariables.minPlayers");
		allowSpectating = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.allowSpectating");
		economy = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.useEconomy");
		winValue = SkyWarsReloaded.get().getConfig().getInt("gameVariables.pointsPerWin");
		killValue = SkyWarsReloaded.get().getConfig().getInt("gameVariables.pointsPerKill");
		deathValue = SkyWarsReloaded.get().getConfig().getInt("gameVariables.pointsLostPerDeath");
		leaveValue = SkyWarsReloaded.get().getConfig().getInt("gameVariables.pointsLostPerLeave");
		directToCages = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.directToCages");
		String world = SkyWarsReloaded.get().getConfig().getString("spawn.world");
		int x = SkyWarsReloaded.get().getConfig().getInt("spawn.x");
		int y = SkyWarsReloaded.get().getConfig().getInt("spawn.y");
		int z = SkyWarsReloaded.get().getConfig().getInt("spawn.z");
		float yaw = SkyWarsReloaded.get().getConfig().getInt("spawn.yaw");
		float pitch = SkyWarsReloaded.get().getConfig().getInt("spawn.pitch");
		getScoreBoard();
		if (world != null ) {
			spawn = new Location(SkyWarsReloaded.get().getServer().getWorld(world), x, y, z, yaw, pitch);
		} else {
			spawn = null;
		}
		gameState = GameState.WAITING;
		if (SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.directToLobby") || bungeeMode || directToCages) {
			gameState = GameState.INLOBBY;
		}
		this.gameNumber = gameNumber;
		if (directToCages) {
			getGameMap();
			votingEnded = true;
		} else {
			getGameLobby();
		}

	}
	
	private void getGameMap() {
    	CuboidClipboard randomMap = SkyWarsReloaded.getMC().getRandom();
    	mapWorld = SkyWarsReloaded.getWC().createMap(this, randomMap);
    	numberOfSpawns = spawns.size();
		for (int i = 0; i < spawns.size(); i++) {
			availableSpawns.put(i, null);
		}
		createSpawnPlatform();
		if (allowSpectating) {
			int x = mapCoords[0] * islandSize;
	        int y = -35;
	        int z = mapCoords[1] * islandSize;
	        int x1 = mapCoords[0] * islandSize + islandSize;
	        int y1 = 256;
	        int z1 = mapCoords[1] * islandSize + islandSize;
	        Location loc1 = new Location(mapWorld, x, y, z);
	        Location loc2 = new Location(mapWorld, x1, y1, z1);
			SkyWarsReloaded.getSpectate().getArenasManager().registerArena(new Arena(bungeeName, loc1, loc2));
			Arena arena = SkyWarsReloaded.getSpectate().getArenasManager().getArena(bungeeName);
			arena.setLobby(spawns.get(0));
		}
		fillChests();
	}
	
	private void getGameLobby() {
		MapController mc = SkyWarsReloaded.getMC();
		CuboidClipboard lobby = mc.getMap("lobby");
		lobbyWorld = SkyWarsReloaded.getWC().createLobby(this, lobby);
		prepareSigns();
	}
	
	public void addPlayer(GamePlayer name) {
		name.setGame(this);
		gPlayers.add(name);
		if (bungeeMode) {
			BungeeUtil.sendSignUpdateRequest(bungeeName, this);
		}
		if(gameState == GameState.INLOBBY) {
			preparePlayerForLobby(name);
		} else if (gameState == GameState.WAITING && gPlayers.size() < minPlayers) {
			name.getP().sendMessage(new Messaging.MessageFormatter().format("game.queue"));
		} else if (gameState == GameState.WAITING && gPlayers.size() >= minPlayers) {
			for (GamePlayer player: gPlayers) {
				preparePlayerForLobby(player);
			}
			gameState = GameState.INLOBBY;
		}
		if (gameState == GameState.INLOBBY && gPlayers.size() >= minPlayers && !votingTimerStarted && !directToCages) {
			startVoterCount();
			votingTimerStarted = true;
		}

	}
	
	private void preparePlayerForLobby(GamePlayer gPlayer) {
		gPlayer.resetLives();
		Location location;
		if (directToCages) {
			int spawn = getAvailableSpawn();
			Location spawnPoint = spawns.get(spawn);
			int x = spawnPoint.getBlock().getX();
			int y = spawnPoint.getBlock().getY();
			int z = spawnPoint.getBlock().getZ();
			World world = spawnPoint.getBlock().getWorld();
			location = new Location(world, x + 0.5, y, z + 0.5);
			availableSpawns.put(spawn,  gPlayer);
			if (allowSpectating) {
				SkyWarsReloaded.getSpectate().setArenaForPlayer(gPlayer.getP(), SkyWarsReloaded.getSpectate().getArenasManager().getArena(bungeeName), false);
			}
		} else {
			location = lobbySpawns.get(0);
			if (signs.size() > 1) {
				gPlayer.getP().sendMessage(new Messaging.MessageFormatter().format("game.voting-start"));
			}
		}
		gPlayer.saveInventory();
		gPlayer.saveGameMode();
		gPlayer.clearInventory();
		gPlayer.getP().getInventory().setItem(8, door);
		gPlayer.setRespawn(gPlayer.getP().getLocation());
		gPlayer.getP().teleport(location);
		gPlayer.getP().setHealth(20);
		gPlayer.getP().setFoodLevel(20);
		gPlayer.getP().setFlying(false);
		gPlayer.getP().setGameMode(GameMode.ADVENTURE);
		gPlayer.getP().setScoreboard(scoreboard);
		Score score = objective.getScore(gPlayer.getP().getName());
		score.setScore(gPlayer.getLives());
		boolean enabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.kitsEnabled");
		if (enabled) {
			gPlayer.getP().getInventory().setItem(0, chest);
		}
		for (GamePlayer gamePlayer: gPlayers) {
			gamePlayer.getP().sendMessage(new Messaging.MessageFormatter()
			.withPrefix()
			.setVariable("number", "" + gPlayers.size())
			.setVariable("total", "" + numberOfSpawns)
			.setVariable("player", gPlayer.getP().getName())
			.format("game.lobby-join"));
		}
	}
	
	private int getAvailableSpawn() {
		for (int spawn: availableSpawns.keySet()) {
			if (availableSpawns.get(spawn) == null) {
				return spawn;
			}
		}
		return 1;
	}

	private void startVoterCount() {
		SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
			public void run() {
				getMapAfterVote();
			  }
			}, 600);
		
	}
	
	private void getMapAfterVote() {
	      for (GamePlayer gPlayer: gPlayers) {
	    	  	if (signs.size() > 1) {
	    	  		gPlayer.getP().sendMessage(new Messaging.MessageFormatter().format("game.voting-end"));
	  			}
	      }
	      MapController mc = SkyWarsReloaded.getMC();
	      mapName = getHighestVotes();
	      CuboidClipboard map = mc.getMap(mapName);
	      mapWorld = SkyWarsReloaded.getWC().createMap(this, map);
	      if (allowSpectating) {
				int x = mapCoords[0] * islandSize;
		        int y = -35;
		        int z = mapCoords[1] * islandSize;
		        int x1 = mapCoords[0] * islandSize + islandSize;
		        int y1 = 256;
		        int z1 = mapCoords[1] * islandSize + islandSize;
		        Location loc1 = new Location(mapWorld, x, y, z);
		        Location loc2 = new Location(mapWorld, x1, y1, z1);
				SkyWarsReloaded.getSpectate().getArenasManager().registerArena(new Arena(bungeeName, loc1, loc2));
				Arena arena = SkyWarsReloaded.getSpectate().getArenasManager().getArena(bungeeName);
				arena.setLobby(spawns.get(0));
		  }
	      createSpawnPlatform();
	      fillChests();
		  votingEnded = true;
	}
	
	private void fillChests() {
		for (Location loc: chests) {
			Chest chest = (Chest) loc.getBlock().getState();
			SkyWarsReloaded.getCC().populateChest(chest);
		}
	}

	public void prepareForStart() {
		if (!(gameState == GameState.INLOBBY && votingEnded)) {
            return;
        } else {
        	if (gPlayers.size() < minPlayers && !directToCages) {
        		for(GamePlayer gPlayer: gPlayers) {
        			if (count == 0) {
        				gPlayer.getP().sendMessage(new Messaging.MessageFormatter().format("game.more-players"));
        			}
        			count++;
    				if (count > 10) {
    					count = 0;
    				}
        		}
        		return;
        	} else if (gPlayers.size() >= minPlayers && !directToCages) {
        		gameState = GameState.PREGAME;
        		if (bungeeMode) {
        			BungeeUtil.sendSignUpdateRequest(bungeeName, this);
        		}
        		startGame();
        	} else if (gPlayers.size() >= minPlayers && directToCages) {
        		for(GamePlayer gPlayer: gPlayers) {
        			if (count == 0) {
        				gPlayer.getP().sendMessage(ChatColor.RED + "Game Starting in 10");
        			} else if (count > 0 && count < 10){
        				gPlayer.getP().sendMessage(ChatColor.RED + "" + (10 - count));
        			} else if (count == 10) {
        				startGame();
        				gameState = GameState.PLAYING;
                		if (bungeeMode) {
                			BungeeUtil.sendSignUpdateRequest(bungeeName, this);
                		}
        			}
        		}
        		count++;
        	} else if (gPlayers.size() < minPlayers && directToCages) {
        		count = 0;
        	}
        }
	}

	public void startGame() {
		if (!directToCages) {
			teleportToSpawns();
			countDownToPlaying();
		} else {
			removeSpawnHousing();
		    for (GamePlayer gPlayer: gPlayers) {
		    	  gPlayer.getP().setGameMode(GameMode.SURVIVAL);
		    	  gPlayer.getP().getInventory().remove(chest);
		    	  gPlayer.getP().getInventory().remove(door);
		    	  gPlayer.getP().setHealth(20);
		    	  gPlayer.getP().setFoodLevel(20);
		    	  gPlayer.setPlaying(true);
		    	  if (gPlayer.hasKitSelected()) {
		              SkyWarsReloaded.getKC().populateInventory(gPlayer.getP().getInventory(), gPlayer.getSelectedKit());
		              SkyWarsReloaded.getKC().givePotionEffects(gPlayer, gPlayer.getSelectedKit());
		              gPlayer.setKitSelected(false);
		    	  }
		    	  
		    }
		}
	}
	
	public void endGame() {
		for (GamePlayer gplayer: gPlayers) {
			deletePlayer(gplayer, false);
		}
				if (allowSpectating) {
			for (GamePlayer gPlayer: spectators) {
				SkyWarsReloaded.getSpectate().setSpectating(gPlayer.getP(), false, true);
				gPlayer.setSpectating(false);
				if (bungeeMode) {
					BungeeUtil.connectToServer(gPlayer.getP(), SkyWarsReloaded.get().getConfig().getString("bungeeMode.lobbyServer"));
				} else {
					gPlayer.getP().teleport(spawn);
				}
			}
			SkyWarsReloaded.getSpectate().getArenasManager().unregisterArena(SkyWarsReloaded.getSpectate().getArenasManager().getArena(bungeeName));
		}
		deleteGame();
	}
		
	
	private void teleportToSpawns() {
		int spawnCount = 0;
		for(GamePlayer gPlayer: gPlayers) {
			int x = spawns.get(spawnCount).getBlock().getX();
			int y = spawns.get(spawnCount).getBlock().getY();
			int z = spawns.get(spawnCount).getBlock().getZ();
			World world = spawns.get(spawnCount).getBlock().getWorld();
			Location location = new Location(world, x + 0.5, y, z + 0.5);
			gPlayer.getP().teleport(location);
			gPlayer.getP().getInventory().remove(chest);
			gPlayer.getP().getInventory().remove(door);
			if (allowSpectating) {
				SkyWarsReloaded.getSpectate().setArenaForPlayer(gPlayer.getP(), SkyWarsReloaded.getSpectate().getArenasManager().getArena(bungeeName), false);
			}
	    	gPlayer.setPlaying(true);
			spawnCount++;
		}
	}
	
	public void countDownToPlaying() {
	    for (GamePlayer gPlayer: gPlayers) {
	    	  gPlayer.getP().sendMessage(ChatColor.RED + "Game will start in 5"); 	  
	    }
		SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
			public void run() {
			      for (GamePlayer gPlayer: gPlayers) {
			    	  gPlayer.getP().sendMessage(ChatColor.RED + "4"); 	  
			      }
			  }
			}, 20);
		SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
			public void run() {
			      for (GamePlayer gPlayer: gPlayers) {
			    	  gPlayer.getP().sendMessage(ChatColor.RED + "3"); 	  
			      }
			  }
			}, 40);
		SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
			public void run() {
			      for (GamePlayer gPlayer: gPlayers) {
			    	  gPlayer.getP().sendMessage(ChatColor.RED + "2");  	  
			      }
			  }
			}, 60);
		SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
			public void run() {
			      for (GamePlayer gPlayer: gPlayers) {
			    	  gPlayer.getP().sendMessage(ChatColor.RED + "1"); 	  
			      }
			  }
			}, 80);
		SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
			public void run() {
				removeSpawnHousing();
			    for (GamePlayer gPlayer: gPlayers) {
			    	  gPlayer.getP().setGameMode(GameMode.SURVIVAL);
			    	  gPlayer.getP().setHealth(20);
			    	  gPlayer.getP().setFoodLevel(20);
			    	  if (gPlayer.hasKitSelected()) {
			              SkyWarsReloaded.getKC().populateInventory(gPlayer.getP().getInventory(), gPlayer.getSelectedKit());
			              SkyWarsReloaded.getKC().givePotionEffects(gPlayer, gPlayer.getSelectedKit());
			              gPlayer.setKitSelected(false);
			    	  }
			    	  
			    }
				gameState = GameState.PLAYING;
			  }
			}, 100);
	}
	
	private void getScoreBoard() {
		if (scoreboard != null) {
            resetScoreboard();
        }
		ScoreboardManager manager = SkyWarsReloaded.get().getServer().getScoreboardManager();
		scoreboard = manager.getNewScoreboard();
        objective = scoreboard.registerNewObjective("info", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        String leaderboard = new Messaging.MessageFormatter().format("game.scoreboard-name");
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
	
	public void deletePlayer(final GamePlayer gplayer, boolean playerQuit) {
		if (playerQuit) {
			playerQuit(gplayer);
		}
		if (gameState != GameState.WAITING) {
			preparePlayerForExit(gplayer);
			gPlayers.remove(gplayer);
			if (bungeeMode && !shutdown) {
				gplayer.getP().teleport(gplayer.getRespawn());
				SkyWarsReloaded.get().getServer().getScheduler().runTaskLater(SkyWarsReloaded.get(), new Runnable() {
					public void run() {
						BungeeUtil.connectToServer(gplayer.getP(), SkyWarsReloaded.get().getConfig().getString("bungeeMode.lobbyServer"));
					}
				}, 5);
			} else {
				if (spawn != null) {
					gplayer.getP().teleport(spawn);
				} else {
					gplayer.getP().teleport(gplayer.getRespawn());
				}
			}
			if (gameState == GameState.INLOBBY) {
				if (bungeeMode && !shutdown) {
					BungeeUtil.sendSignUpdateRequest(bungeeName, this);
					if (gPlayers.size() == 0) {
						BungeeSocket.sendSignUpdate(this);
					}
				}
			}
		}
	}
	
	public void shutdown() {
		shutdown = true;
	}
	
	private void playerQuit(GamePlayer gPlayer) {
		GamePlayer killer = gPlayer.getTagged().getPlayer();
		if ((System.currentTimeMillis() - gPlayer.getTagged().getTime()) < 10000 && killer != gPlayer) {
			if (killer != gPlayer) {
				killer.setKills(killer.getKills() + 1);
				gPlayer.setDeaths(gPlayer.getDeaths() + 1);
				killer.setScore(killer.getScore() + killValue);
				gPlayer.setScore(gPlayer.getScore() - deathValue);
				if (economy) {
					SkyWarsReloaded.econ.depositPlayer(killer.getP(), killValue);
					SkyWarsReloaded.econ.withdrawPlayer(gPlayer.getP(), deathValue);
				}
				for (GamePlayer gamePlayer: gPlayers) {
					gamePlayer.getP().sendMessage(new Messaging.MessageFormatter()
					.withPrefix()
					.setVariable("player", gPlayer.getP().getName())
					.setVariable("player_score", StringUtils.formatScore(-deathValue))
					.setVariable("killer", killer.getP().getName())
					.setVariable("killer_score", StringUtils.formatScore(killValue))
					.format("game.death.quit-while-tagged"));
				}
			}
		} else {
			if (gameState == GameState.PREGAME || gameState == GameState.PLAYING) {
				if (!gPlayer.getSpectating()) {
					gPlayer.setScore(gPlayer.getScore() - deathValue);
					if (economy) {
						SkyWarsReloaded.econ.withdrawPlayer(gPlayer.getP(), deathValue);
					}
					for (GamePlayer gamePlayer: gPlayers) {
						gamePlayer.getP().sendMessage(new Messaging.MessageFormatter()
						.withPrefix()
						.setVariable("player", gPlayer.getP().getName())
						.setVariable("score", StringUtils.formatScore(-leaveValue))
						.format("game.left-the-game"));
					}
				}
			} else if (gameState == GameState.INLOBBY) {
				scoreboard.resetScores(gPlayer.getP().getName());
				if (directToCages) {
					for (int spawn: availableSpawns.keySet()) {
						
						if (availableSpawns.get(spawn) == gPlayer) {
							SkyWarsReloaded.get().getLogger().info(gPlayer.getP().getName());
							availableSpawns.put(spawn,  null);
						}
					}
				}
				for (GamePlayer gamePlayer: gPlayers) {
					if (gamePlayer != gPlayer) {
						gamePlayer.getP().sendMessage(new Messaging.MessageFormatter()
						.withPrefix()
						.setVariable("number", "" + (gPlayers.size()-1))
						.setVariable("total", "" + numberOfSpawns)
						.setVariable("player", gPlayer.getP().getName())
						.format("game.lobby-leave"));
					}
				}
			}
		}
	}
	
	private void preparePlayerForExit(GamePlayer gplayer) {
		if (allowSpectating) {
			SkyWarsReloaded.getSpectate().setSpectating(gplayer.getP(), false, true);
		}
		gplayer.setSpectating(false);
		gplayer.setPlaying(false);
  		gplayer.clearInventory();
		gplayer.resetInventory();
		gplayer.getP().updateInventory();
		gplayer.resetGameMode();
		gplayer.setKitSelected(false);
		gplayer.getP().setScoreboard(SkyWarsReloaded.get().getServer().getScoreboardManager().getNewScoreboard());
		for (PotionEffect effect : gplayer.getP().getActivePotionEffects()) {
	        gplayer.getP().removePotionEffect(effect.getType());
		}
		gplayer.setGame(null);
	}
		
	public Boolean playerExists(GamePlayer name) {
		for (GamePlayer gPlayer: gPlayers) {
			if (name == gPlayer) {
				return true;
			}
		}
		return false;
	}
	
	public CopyOnWriteArrayList<GamePlayer> getPlayers() {
		return gPlayers;
	}
	
	public String getName() {
		return mapName;
	}
	
	public void deleteGame() {
		SkyWarsReloaded.getWC().unloadMap(this);
		if (!directToCages) {
			SkyWarsReloaded.getWC().unloadLobby(this);
		}
		SkyWarsReloaded.getGC().deleteGame(this);
	}
	
	public GameState getState() {
		return gameState;
	}
	
	public Boolean isFull() {
		if (gPlayers.size() < spawns.size()) {
			return false;
		} else {
			return true;
		}
	}
	
    public void createSpawnPlatform() {
    	for(Location spawn : spawns.values()) {
            int x = spawn.getBlock().getX();
            int y = spawn.getBlock().getY()-1;
            int z = spawn.getBlock().getZ();

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
    	for(Location spawn : spawns.values()) {
            int x = spawn.getBlock().getX();
            int y = spawn.getBlock().getY()-1;
            int z = spawn.getBlock().getZ();
            
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
    
    public void prepareSigns() {
    	MapController mc = SkyWarsReloaded.getMC();
    	shuffleMaps = mc.getMaps();
    	shuffleMaps.remove("lobby");
    	int x = (int)(Math.random()*shuffleMaps.size()) + 1;
    	CuboidClipboard random = SkyWarsReloaded.getMC().getMap(shuffleMaps.get(x-1));
    	numberOfSpawns = SkyWarsReloaded.getMC().getCachedSpawns(random).size();
    	for (String name: shuffleMaps) {
    		CuboidClipboard shuffle = SkyWarsReloaded.getMC().getMap(name);
    		if (SkyWarsReloaded.getMC().getCachedSpawns(shuffle).size() != numberOfSpawns) {
    			shuffleMaps.remove(name);
    		}
    	}
    	Collections.shuffle(shuffleMaps);
    	for (int i = 0; i < shuffleMaps.size(); i++) {
        	signValues.put(shuffleMaps.get(i), 0);
    	}
    	updateSigns();
    }
	
	public void updateSigns() {
		getNewSignValues();
		if(signs.size() > 0) {
			for (int signNumber: signs.keySet()) {
				if (signNumber < shuffleMaps.size()) {
					Block b = signs.get(signNumber).getBlock();
					if(b.getType() == Material.WALL_SIGN){
						Sign s = (Sign) b.getState();
						s.setLine(0, new Messaging.MessageFormatter().setVariable("mapname", shuffleMaps.get(signNumber)).format("signs.line1"));
						s.setLine(1, new Messaging.MessageFormatter().format("signs.line2"));
						s.setLine(2, new Messaging.MessageFormatter().setVariable("count", signValues.get(shuffleMaps.get(signNumber)).toString()).format("signs.line3"));
						s.setLine(3, new Messaging.MessageFormatter().format("signs.line4"));
						s.update();
					}
				}
			}
		}
	}
	
	private void getNewSignValues() {
		for (int i = 0; i < shuffleMaps.size(); i++) {
        	signValues.put(shuffleMaps.get(i), 0);
    	}
		for (int j = 0; j < gPlayers.size(); j++) {
			String mName = gPlayers.get(j).getVoted();
			if (signValues.containsKey(mName)) {
				int vote = signValues.get(mName) + 1;
				signValues.put(mName, vote);
			}
		}
	}
	
	public String getHighestVotes() {
		String mapName;
		if(signs.size() > 0) {
			getNewSignValues();
			int highest = 0;
			mapName = "";
			for(String gMap: signValues.keySet()) {
				if (signValues.get(gMap) >= highest) {
					mapName = gMap;
					highest = signValues.get(gMap);
				}
			}
		} else {
			int numberOfMaps = shuffleMaps.size();
			int random = (int)(Math.random()*numberOfMaps) + 1;
			mapName = shuffleMaps.get(random-1);
		}
		return mapName;
	}
	
	public boolean votingStatus() {
		return votingEnded;
	}

	public enum GameState {

	    WAITING,
	    INLOBBY,
	    PREGAME,
	    PLAYING,
	    ENDING, 
	    RESTARTING
	}

	public void setState(GameState gState) {
		gameState = gState;
	}

	public void onPlayerDeath(GamePlayer target, DamageCause dCause) {
		target.setLives(target.getLives() - 1);
		Score score = objective.getScore(target.getP().getName());
		score.setScore(target.getLives());
		target.setDeaths(target.getDeaths() + 1);
		if ((System.currentTimeMillis() - target.getTagged().getTime()) < 10000) {
			GamePlayer killer = target.getTagged().getPlayer();
			if (killer != target) {
				killer.setKills(killer.getKills() + 1);
				killer.setScore(killer.getScore() + killValue);
				target.setScore(target.getScore() - deathValue);
				if (economy) {
					SkyWarsReloaded.econ.depositPlayer(killer.getP(), killValue);
					SkyWarsReloaded.econ.withdrawPlayer(target.getP(), deathValue);
				}
				target.setTagged(target);
				for (GamePlayer gPlayer: gPlayers) {
					gPlayer.getP().sendMessage(getDeathMessage(dCause, true, target, killer));
				}
			}
		} else {
			target.setScore(target.getScore() - deathValue);
			if (economy) {
				SkyWarsReloaded.econ.withdrawPlayer(target.getP(), deathValue);
			}
			for (GamePlayer gPlayer: gPlayers) {
				gPlayer.getP().sendMessage(getDeathMessage(dCause, false, target, target));
			}
		}
		playerEndGame(target);
		
	}
	
	private void playerEndGame(GamePlayer target) {
			target.setPlaying(false);
			target.setGamesPlayed(target.getGamesPlayed() + 1);
			if (allowSpectating) {
				SkyWarsReloaded.getSpectate().setSpectating(target.getP(), true, true);
				target.getP().getInventory().setItem(8, door);
				target.setSpectating(true);
				Location location = spawns.get(0);
				target.getP().teleport(location);
				target.getP().sendMessage(new Messaging.MessageFormatter().withPrefix().format("game.spectating"));
			} else {
				deletePlayer(target, false);
			}
			
			checkForWinner();
	}
	
	public void checkForWinner() {
		if (gameState == GameState.PLAYING || gameState == GameState.PREGAME) {
			int count = 0;
			for (GamePlayer gPlayer: gPlayers) {
				if (gPlayer.isPlaying()) {
					count++;
				}
			}
			if (count < 2) {
				gameState = GameState.ENDING;
        		if (bungeeMode) {
        			BungeeUtil.sendSignUpdateRequest(bungeeName, this);
        		}
				if (gPlayers.size() == 0 || count == 0) {
					if (allowSpectating) {
						for (GamePlayer gamePlayer: gPlayers) {
							gamePlayer.getP().sendMessage(new Messaging.MessageFormatter().withPrefix().setVariable("time", "" + SkyWarsReloaded.get().getConfig().getInt("gameVariables.timeAfterGame")).format("game.gameEnding"));
						}
						SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
							public void run() {
								endGame();
							  }
							}, 20 * SkyWarsReloaded.get().getConfig().getInt("gameVariables.timeAfterGame"));
					} else {
						endGame();
					}
				}
				for (GamePlayer gPlayer: gPlayers) {
					if (gPlayer.isPlaying()) {
						gPlayer.setPlaying(false);
						gPlayer.setWins(gPlayer.getWins() + 1);
						gPlayer.setScore(gPlayer.getScore() + winValue);
						if (economy) {
							SkyWarsReloaded.econ.depositPlayer(gPlayer.getP(), winValue);
						}
						gPlayer.setGamesPlayed(gPlayer.getGamesPlayed() + 1);
						for (GamePlayer gamePlayer: SkyWarsReloaded.getPC().getAll()) {
							gamePlayer.getP().sendMessage(new Messaging.MessageFormatter().setVariable("player", gPlayer.getP().getName())
									.withPrefix()
									.setVariable("player_score", StringUtils.formatScore(winValue))
									.setVariable("mapname", mapName)
									.format("game.win"));
						}
						if (allowSpectating) {
							SkyWarsReloaded.getSpectate().setArenaForPlayer(gPlayer.getP(), SkyWarsReloaded.getSpectate().getArenasManager().getArena(bungeeName));
							SkyWarsReloaded.getSpectate().setSpectating(gPlayer.getP(), true, true);
							gPlayer.setSpectating(true);
							gPlayer.getP().getInventory().setItem(8, door);
							gPlayer.getP().sendMessage(new Messaging.MessageFormatter().withPrefix().format("game.spectating"));
							for (GamePlayer gamePlayer: gPlayers) {
								gamePlayer.getP().sendMessage(new Messaging.MessageFormatter().withPrefix().setVariable("time", "" + SkyWarsReloaded.get().getConfig().getInt("gameVariables.timeAfterGame")).format("game.gameEnding"));
							}
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
		}
	}
	
	private String getDeathMessage(DamageCause dCause, boolean withHelp, GamePlayer target, GamePlayer killer) {
		String first = "";
		String second = new Messaging.MessageFormatter()
						.setVariable("killer", killer.getP().getName())
						.setVariable("killer_score", StringUtils.formatScore(killValue))
						.format("game.death.killer-section");
		
		if (dCause.equals(DamageCause.BLOCK_EXPLOSION) || dCause.equals(DamageCause.ENTITY_EXPLOSION)) {
			first = new Messaging.MessageFormatter()
					.withPrefix()
					.setVariable("player", target.getP().getName())
					.setVariable("player_score", StringUtils.formatScore(-deathValue))
					.format("game.death.explosion");
		} else if (dCause.equals(DamageCause.DROWNING)) {
			first = new Messaging.MessageFormatter()
			.withPrefix()
			.setVariable("player", target.getP().getName())
			.setVariable("player_score", StringUtils.formatScore(-deathValue))
			.format("game.death.drowning");
		} else if (dCause.equals(DamageCause.FIRE) || dCause.equals(DamageCause.FIRE_TICK)) {
			first = new Messaging.MessageFormatter()
			.withPrefix()
			.setVariable("player", target.getP().getName())
			.setVariable("player_score", StringUtils.formatScore(-deathValue))
			.format("game.death.fire");
		} else if (dCause.equals(DamageCause.ENTITY_ATTACK)) {
			first = new Messaging.MessageFormatter()
			.withPrefix()
			.setVariable("player", target.getP().getName())
			.setVariable("player_score", StringUtils.formatScore(-deathValue))
			.setVariable("killer", killer.getP().getName())
			.setVariable("killer_score", StringUtils.formatScore(killValue))
			.format("game.death.pvp");
			second = "";
		} else if (dCause.equals(DamageCause.FALLING_BLOCK)) {
			first = new Messaging.MessageFormatter()
			.withPrefix()
			.setVariable("player", target.getP().getName())
			.setVariable("player_score", StringUtils.formatScore(-deathValue))
			.format("game.death.falling-block");
		} else if (dCause.equals(DamageCause.LAVA)) {
			first = new Messaging.MessageFormatter()
			.withPrefix()
			.setVariable("player", target.getP().getName())
			.setVariable("player_score", StringUtils.formatScore(-deathValue))
			.format("game.death.lava");
		} else if (dCause.equals(DamageCause.PROJECTILE)) {
			first = new Messaging.MessageFormatter()
			.withPrefix()
			.setVariable("player", target.getP().getName())
			.setVariable("player_score", StringUtils.formatScore(-deathValue))
			.setVariable("killer", killer.getP().getName())
			.setVariable("killer_score", StringUtils.formatScore(killValue))
			.format("game.death.projectile");
			second = "";
		} else if (dCause.equals(DamageCause.SUFFOCATION)) {
			first = new Messaging.MessageFormatter()
			.withPrefix()
			.setVariable("player", target.getP().getName())
			.setVariable("player_score", StringUtils.formatScore(-deathValue))
			.format("game.death.suffocation");
		} else if (dCause.equals(DamageCause.VOID)) {
			first = new Messaging.MessageFormatter()
			.withPrefix()
			.setVariable("player", target.getP().getName())
			.setVariable("player_score", StringUtils.formatScore(-deathValue))
			.format("game.death.void");
		} else {
			first = new Messaging.MessageFormatter()
			.withPrefix()
			.setVariable("player", target.getP().getName())
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
	
	public String getBungeeName() {
		return bungeeName;
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

	public World getLobbyWorld() {
		return lobbyWorld;
	}
	
	public int[] getMapCoordinates() {
		return mapCoords;
	}
	
	public void setMapCoordinates(int[] islandCoordinates) {
		mapCoords = islandCoordinates;
	}
	
	public int[] getLobbyCoordinates() {
		return lobbyCoords;
	}
	
	public void setLobbyCoordinates(int[] islandCoordinates) {
		lobbyCoords = islandCoordinates;
	}

	public void addChest(Location chest) {
		chests.add(chest);
	}

	public void addSpawn(Integer key, Location location) {
		spawns.put(key, location);
	}
	
	public void addLobbySpawn(Integer key, Location location) {
		lobbySpawns.put(key, location);
	}
	
	public void addSpectator(GamePlayer gPlayer) {
		spectators.add(gPlayer);
	}

	public void addLobbySign(Integer key, Location location) {
		signs.put(key, location);
	}

	public Location getSpawn() {
		return spawns.get(0);
	}

	public void removeSpectator(GamePlayer gPlayer) {
		spectators.remove(gPlayer);
		gPlayer.setSpecGame(null);
	}
	
}
