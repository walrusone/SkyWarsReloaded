package com.walrusone.skywars.game;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.google.common.collect.Maps;
import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.controllers.MapController;
import com.walrusone.skywars.controllers.WorldController;


public class Game {

	private CopyOnWriteArrayList<GamePlayer> gPlayers = new CopyOnWriteArrayList<GamePlayer>();
	private int count = 0;
	private GameState gameState;
	private String mapName;
	private World mapWorld;
	private World lobbyWorld;
	private Map<String, Integer> signValues = Maps.newHashMap();
	private int gameNumber;
	private Scoreboard scoreboard;
	private Objective objective;
	private GameMap lobby;
	private GameMap gameMap = null;
	private int minPlayers;
	private boolean votingEnded = false;
	private List<String> shuffleMaps;
	private boolean allowSpectating;
	private Location spawn;
	private int winValue;
	private int killValue;
	private int deathValue;
	private boolean economy;
	
	
	public Game(int gameNumber) {
		minPlayers = SkyWarsReloaded.get().getConfig().getInt("gameVariables.minPlayers");
		allowSpectating = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.allowSpectating");
		economy = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.useEconomy");
		winValue = SkyWarsReloaded.get().getConfig().getInt("gameVariables.pointsPerWin");
		killValue = SkyWarsReloaded.get().getConfig().getInt("gameVariables.pointsPerKill");
		deathValue = SkyWarsReloaded.get().getConfig().getInt("gameVariables.pointsLostPerDeath");
		String world = SkyWarsReloaded.get().getConfig().getString("spawn.world");
		int x = SkyWarsReloaded.get().getConfig().getInt("spawn.x");
		int y = SkyWarsReloaded.get().getConfig().getInt("spawn.y");
		int z = SkyWarsReloaded.get().getConfig().getInt("spawn.z");
		if (world != null ) {
			spawn = new Location(SkyWarsReloaded.get().getServer().getWorld(world), x, y, z);
		} else {
			spawn = null;
		}
		gameState = GameState.WAITING;
		this.gameNumber = gameNumber;
		MapController mc = SkyWarsReloaded.getMC();
		lobby = mc.getMap("lobby");
		boolean lobbyLoad = lobby.loadMap(this.gameNumber);
		lobbyWorld = SkyWarsReloaded.get().getServer().getWorld("lobby_" + this.gameNumber);
		if (lobbyLoad) {
			lobbyWorld.getBlockAt(lobby.getSpawns().get(1).getX().intValue(), lobby.getSpawns().get(1).getY().intValue(), lobby.getSpawns().get(1).getZ().intValue()).setType(Material.AIR);
			prepareSigns();
			
		}
	}
	
	public void addPlayer(GamePlayer name) {
		name.resetLives();
		name.setGame(this);
		gPlayers.add(name);
		if(gameState == GameState.INLOBBY) {
			MapController mc = SkyWarsReloaded.getMC();
			lobby = mc.getMap("lobby");
			lobbyWorld = SkyWarsReloaded.get().getServer().getWorld("lobby_" + this.gameNumber);
			Location location = new Location(lobbyWorld, lobby.getSpawns().get(1).getX(), lobby.getSpawns().get(1).getY(), lobby.getSpawns().get(1).getZ());
			name.saveInventory();
			name.saveGameMode();
			name.clearInventory();
			name.setRespawn(name.getP().getLocation());
			name.getP().teleport(location);
			name.getP().setHealth(20);
			name.getP().setFoodLevel(20);
			name.getP().setFlying(false);
			name.getP().setGameMode(GameMode.ADVENTURE);
			SkyWarsReloaded.getKC().openKitMenu(name);
			return;
		} else if (gameState == GameState.WAITING && gPlayers.size() < minPlayers) {
			name.getP().sendMessage(ChatColor.GREEN + SkyWarsReloaded.get().getConfig().getString("messages.queue"));
			return;
		} else if (gameState == GameState.WAITING && gPlayers.size() >= minPlayers) {
			MapController mc = SkyWarsReloaded.getMC();
			lobby = mc.getMap("lobby");
			lobbyWorld = SkyWarsReloaded.get().getServer().getWorld("lobby_" + this.gameNumber);
			Location location = new Location(lobbyWorld, lobby.getSpawns().get(1).getX(), lobby.getSpawns().get(1).getY(), lobby.getSpawns().get(1).getZ());
			for (GamePlayer player: gPlayers) {
				player.saveInventory();
				player.saveGameMode();
				player.clearInventory();
				player.setRespawn(name.getP().getLocation());
				player.getP().teleport(location);
				player.getP().setHealth(20);
				player.getP().setFoodLevel(20);
				player.getP().setFlying(false);
				player.getP().setGameMode(GameMode.ADVENTURE);
				SkyWarsReloaded.getKC().openKitMenu(player);
			}
			gameState = GameState.INLOBBY;
			startVoterCount();
		}

	}
	
	private void startVoterCount() {
		SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
			public void run() {
			      for (GamePlayer gPlayer: gPlayers) {
			    	  gPlayer.getP().sendMessage(ChatColor.GREEN + SkyWarsReloaded.get().getConfig().getString("messages.votingStart")); 	  
			      }
			  }
			}, 20);
		SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
			public void run() {
		    	  
			      for (GamePlayer gPlayer: gPlayers) {
			    	  gPlayer.getP().sendMessage(ChatColor.GREEN + SkyWarsReloaded.get().getConfig().getString("messages.votingEnd")); 	  
			      }
			      MapController mc = SkyWarsReloaded.getMC();
			      mapName = getHighestVotes();
			      gameMap = mc.getMap(mapName);
			      boolean gameMapLoad = gameMap.loadMap(gameNumber);
					if (gameMapLoad) {
						mapWorld = SkyWarsReloaded.get().getServer().getWorld(mapName + "_" + gameNumber);
						createSpawnPlatform();
					}
				  votingEnded = true;
			  }
			}, 600);
		
	}
	
	public void prepareForStart() {
		if (!(gameState == GameState.INLOBBY && votingEnded)) {
            return;
        } else {
        	if (gPlayers.size() < minPlayers) {
        		for(GamePlayer gPlayer: gPlayers) {
        			if (count == 0) {
        				gPlayer.getP().sendMessage(ChatColor.RED + SkyWarsReloaded.get().getConfig().getString("messages.morePlayers"));
        				count++;
        			} else {
        				count++;
        				if (count > 5) {
        					count = 0;
        				}
        			}
        		}
        		return;
        	} else {
        		startGame();
				getScoreBoard();
				for(GamePlayer gplayer: gPlayers) {
					gplayer.getP().setScoreboard(scoreboard);
					Score score = objective.getScore(gplayer.getP().getName());
					score.setScore(gplayer.getLives());
				}
        	}
        }
	}

	public void startGame() {
		int spawnCount = 1;
		MapController mc = SkyWarsReloaded.getMC();
		gameMap = mc.getMap(mapName);
		mapWorld = SkyWarsReloaded.get().getServer().getWorld(mapName + "_" + gameNumber);
		for(GamePlayer gPlayer: gPlayers) {
			double x = gameMap.getSpawns().get(spawnCount).getX()+0.5;
			double y = gameMap.getSpawns().get(spawnCount).getY();
			double z =  gameMap.getSpawns().get(spawnCount).getZ()+0.5;
			Location location = new Location(mapWorld, x, y, z);
			gPlayer.getP().teleport(location);
			gPlayer.giveKit();
	    	gPlayer.setPlaying(true);
			spawnCount++;
		}
		gameState = GameState.PREGAME;
		deleteLobby();
		countDownToPlaying();
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
			    	  gPlayer.getP().setHealth(20);
			    	  gPlayer.getP().setFoodLevel(20);
			    	  if (gPlayer.hasKitSelected()) {
			              SkyWarsReloaded.getKC().populateInventory(gPlayer.getP().getInventory(), gPlayer.getSelectedKit());
			              gPlayer.setKitSelected(false);
			    	  }
			    	  gPlayer.getP().setGameMode(GameMode.SURVIVAL);
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
        objective.setDisplayName("\247c\247lLeaderBoard");
	}
	
    private void resetScoreboard() {
        if (objective != null) {
            objective.unregister();
        }

        if (scoreboard != null) {
            scoreboard = null;
        }
    }
	
	@SuppressWarnings("deprecation")
	public void deletePlayer(GamePlayer gplayer, boolean playerQuit) {
		if (playerQuit) {
			if ((System.currentTimeMillis() - gplayer.getTagged().getTime()) < 10000) {
				GamePlayer killer = gplayer.getTagged().getPlayer();
				if (killer != gplayer) {
					killer.setKills(killer.getKills() + 1);
					gplayer.setDeaths(gplayer.getDeaths() + 1);
					for (GamePlayer gPlayer: gPlayers) {
						gPlayer.getP().sendMessage(ChatColor.RED + "" + gplayer.getP().getName() + ChatColor.GREEN + " " + SkyWarsReloaded.get().getConfig().getString("messages.death.quitWhileTagged") + " " + ChatColor.BLUE + killer.getP().getName());
					}
				}
			} else {
				for (GamePlayer gPlayer: gPlayers) {
					gPlayer.getP().sendMessage(ChatColor.RED + "" + gplayer.getP().getName() + " " + SkyWarsReloaded.get().getConfig().getString("messages.leftTheGame"));
				}
			}
		}
		if (gameState != GameState.WAITING) {
			if (allowSpectating) {
				SkyWarsReloaded.getSpectate().setSpectating(gplayer.getP(), false, true);
			}
			if (spawn != null) {
				gplayer.getP().teleport(spawn);
			} else {
				gplayer.getP().teleport(gplayer.getRespawn());
			}
			gplayer.setSpectating(false);
			gplayer.setPlaying(false);
	  		gplayer.clearInventory();
			gplayer.resetInventory();
			gplayer.getP().updateInventory();
			gplayer.resetGameMode();
			gplayer.getP().setScoreboard(SkyWarsReloaded.get().getServer().getScoreboardManager().getNewScoreboard());
		}
		gplayer.setGame(null);
		gPlayers.remove(gplayer);
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
	
	public void endGame() {
		for (GamePlayer gplayer: gPlayers) {
			deletePlayer(gplayer, false);
		}
		if (gameState == GameState.INLOBBY || gameState == GameState.WAITING) {
			deleteLobby();
		}
		if (gameState == GameState.PREGAME || gameState == GameState.PLAYING) {
			gameState = GameState.ENDING;
		}
		deleteGame();
	}
	
	public void deleteGame() {
		SkyWarsReloaded.getGC().deleteGame(this);
	}
	
	public GameState getState() {
		return gameState;
	}
	
	public Boolean isFull() {
		if (gPlayers.size() < 8) {
			return false;
		} else {
			return true;
		}
	}
	
    public void createSpawnPlatform() {
    	for(int spawn : gameMap.getSpawns().keySet()) {
            int x = gameMap.getSpawns().get(spawn).getX().intValue();
            int y = gameMap.getSpawns().get(spawn).getY().intValue();
            int z = gameMap.getSpawns().get(spawn).getZ().intValue();

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
            int x = gameMap.getSpawns().get(spawn).getX().intValue();
            int y = gameMap.getSpawns().get(spawn).getY().intValue();
            int z = gameMap.getSpawns().get(spawn).getZ().intValue();
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
    	Collections.shuffle(shuffleMaps);
    	for (int i = 0; i < shuffleMaps.size(); i++) {
        	signValues.put(shuffleMaps.get(i), 0);
    	}
    	updateSigns();
    }
	
	public void updateSigns() {
		getNewSignValues();
		MapController mc = SkyWarsReloaded.getMC();
		lobby = mc.getMap("lobby");
		lobbyWorld = SkyWarsReloaded.get().getServer().getWorld("lobby_" + gameNumber);
		if(lobby.containsSigns()) {
			for (int signNumber: lobby.getSigns().keySet()) {
				if (signNumber < shuffleMaps.size()) {
					Block b = lobbyWorld.getBlockAt(lobby.getSigns().get(signNumber).getX(), lobby.getSigns().get(signNumber).getY(), lobby.getSigns().get(signNumber).getZ());
					if(b.getType() == Material.WALL_SIGN){
						Sign s = (Sign) b.getState();
						s.setLine(0, shuffleMaps.get(signNumber));
						s.setLine(1, "");
						s.setLine(2, signValues.get(shuffleMaps.get(signNumber)).toString());
						s.setLine(3, "Votes");
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
		lobby = SkyWarsReloaded.getMC().getMap("lobby");
		if(lobby.containsSigns()) {
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
			mapName = shuffleMaps.get(random);
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
	    ENDING
	}

	public void deleteLobby() {
		WorldController wc = SkyWarsReloaded.getWC();
		lobbyWorld = SkyWarsReloaded.get().getServer().getWorld("lobby_" + gameNumber);
		File delete = lobbyWorld.getWorldFolder();
		wc.unloadWorld("lobby_" + gameNumber);
		wc.deleteWorld(delete);
	}

	public void deleteMap() {
		WorldController wc = SkyWarsReloaded.getWC();
		mapWorld = SkyWarsReloaded.get().getServer().getWorld(mapName + "_" + gameNumber);
		File delete = mapWorld.getWorldFolder();
		wc.unloadWorld(mapName + "_" + gameNumber);
		wc.deleteWorld(delete);
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
					gPlayer.getP().sendMessage(ChatColor.RED + "" + target.getP().getName() + ChatColor.GREEN + getDeathMessage(dCause, true) + ChatColor.BLUE + " " + killer.getP().getName());
				}
			}
		} else {
			for (GamePlayer gPlayer: gPlayers) {
				gPlayer.getP().sendMessage(ChatColor.RED + "" + target.getP().getName() + ChatColor.GREEN + getDeathMessage(dCause, false));
			}
		}
		playerEndGame(target);
		
	}
	
	private void playerEndGame(GamePlayer target) {
			target.setPlaying(false);
			target.setGamesPlayed(target.getGamesPlayed() + 1);
			if (allowSpectating) {
				SkyWarsReloaded.getSpectate().setSpectating(target.getP(), true, true);
				target.setSpectating(true);
				double x = gameMap.getSpawns().get(1).getX()+0.5;
				double y = gameMap.getSpawns().get(1).getY();
				double z =  gameMap.getSpawns().get(1).getZ()+0.5;
				Location location = new Location(mapWorld, x, y, z);
				target.getP().teleport(location);
				target.getP().sendMessage(ChatColor.GREEN + SkyWarsReloaded.get().getConfig().getString("messages.spectating"));
			} else {
				deletePlayer(target, false);
			}
			checkForWinner();
	}
	
	public void checkForWinner() {
		int count = 0;
		for (GamePlayer gPlayer: gPlayers) {
			if (gPlayer.isPlaying()) {
				count++;
			}
		}
		if (count < 2) {
			for (GamePlayer gPlayer: gPlayers) {
				if (gPlayer.isPlaying()) {
					gPlayer.setPlaying(false);
					gPlayer.setWins(gPlayer.getWins() + 1);
					gPlayer.setScore(gPlayer.getScore() + winValue);
					if (allowSpectating) {
						
					} else {
						
					}
					if (economy) {
						SkyWarsReloaded.econ.depositPlayer(gPlayer.getP(), winValue);
					}
					gPlayer.setGamesPlayed(gPlayer.getGamesPlayed() + 1);
					for (GamePlayer gamePlayer: SkyWarsReloaded.getPC().getAll()) {
						gamePlayer.getP().sendMessage(ChatColor.BLUE + "" + gPlayer.getP().getName() + ChatColor.GREEN + " " + SkyWarsReloaded.get().getConfig().getString("messages.gameWon") + " " + ChatColor.BLUE + mapName);
					}
					if (allowSpectating) {
						SkyWarsReloaded.getSpectate().setSpectating(gPlayer.getP(), true, true);
						gPlayer.setSpectating(true);
						gPlayer.getP().sendMessage(ChatColor.GREEN + SkyWarsReloaded.get().getConfig().getString("messages.spectating"));
						for (GamePlayer gamePlayer: gPlayers) {
							gamePlayer.getP().sendMessage(ChatColor.BLUE + SkyWarsReloaded.get().getConfig().getString("messages.gameEnding"));
						}
						SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
							public void run() {
								endGame();
							  }
							}, 600);
					} else {
						endGame();
					}
				}
			}

		}
	}
	
	public GameMap getMap() {
		return gameMap;
	}
	
	private String getDeathMessage(DamageCause dCause, boolean withHelp) {
		String first = "";
		String second = " " + SkyWarsReloaded.get().getConfig().getString("messages.death.secondPart");
		
		if (dCause.equals(DamageCause.BLOCK_EXPLOSION) || dCause.equals(DamageCause.ENTITY_EXPLOSION)) {
			first = " " + SkyWarsReloaded.get().getConfig().getString("messages.death.explosion");
		} else if (dCause.equals(DamageCause.DROWNING)) {
			first = " " + SkyWarsReloaded.get().getConfig().getString("messages.death.drowning");
		} else if (dCause.equals(DamageCause.FIRE) || dCause.equals(DamageCause.FIRE_TICK)) {
			first = " " + SkyWarsReloaded.get().getConfig().getString("messages.death.fire");
		} else if (dCause.equals(DamageCause.ENTITY_ATTACK)) {
			first = " " + SkyWarsReloaded.get().getConfig().getString("messages.death.pvp");
			second = "";
		} else if (dCause.equals(DamageCause.FALLING_BLOCK)) {
			first = " " + SkyWarsReloaded.get().getConfig().getString("messages.death.fallingBlock");
		} else if (dCause.equals(DamageCause.LAVA)) {
			first = " " + SkyWarsReloaded.get().getConfig().getString("messages.death.lava");
		} else if (dCause.equals(DamageCause.PROJECTILE)) {
			first = " " + SkyWarsReloaded.get().getConfig().getString("messages.death.projectile");
			second = "";
		} else if (dCause.equals(DamageCause.SUFFOCATION)) {
			first = " " + SkyWarsReloaded.get().getConfig().getString("messages.death.suffocation");
		} else if (dCause.equals(DamageCause.VOID)) {
			first = " " + SkyWarsReloaded.get().getConfig().getString("messages.death.void");
		} else {
			first = " " + SkyWarsReloaded.get().getConfig().getString("messages.death.general");
		}
		
		if (withHelp) {
			return first + second;
		} else {
			return first + "!";
		}
	}

}
