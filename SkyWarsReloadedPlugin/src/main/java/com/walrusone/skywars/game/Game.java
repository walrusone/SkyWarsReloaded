package com.walrusone.skywars.game;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
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
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.Vector;

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
	private Map<GamePlayer, Integer> kills = new HashMap<GamePlayer, Integer>();
	private boolean forceEnd = false;
	private int fireworksCount = 0;
	private int count = 0;
	private int gameLength = 0;
	private boolean thunderStorm = false;
	private int nextStrike = 5;
	private int strikeCounter = 0;
	private int sanityChecker = 0;
	private GameState gameState;
	private String mapName;
	private World mapWorld;
	private GameMap gameMap;
	private int gameNumber;
	private Scoreboard scoreboard;
	private Objective objective;
	private int min;
	private int max;
	private int minPlayers;
	private int numberOfSpawns;
	private boolean shutdown = false;
	private Map<Integer, GamePlayer> availableSpawns = new HashMap<Integer, GamePlayer>();
	private Map<String, Integer> scoreboardData = new HashMap<String, Integer>();
	private Location specSpawn;
	
	public Game(int gameNumber, String map) {
		this.gameNumber = gameNumber;
		if (SkyWarsReloaded.getCfg().signJoinMode()) {
			mapName = map;
		}
		int size = SkyWarsReloaded.getCfg().getMaxMapSize()/2;
		min = 0 - size;
		max = 0 + size;
		if (SkyWarsReloaded.getCfg().getSpawn() == null) {
			SkyWarsReloaded.get().getLogger().info("YOU MUST SET SPAWN IN THE LOBBY WORLD WITH /SWR SETSPAWN BEFORE STARTING A GAME");
			endGame();
		}
		gameState = GameState.PREGAME;
		getGameMap();
		getScoreBoard();
	}
	
	private void getGameMap() {
		if (!SkyWarsReloaded.getCfg().signJoinMode()) {
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
	    	if (SkyWarsReloaded.getCfg().spectatingEnabled()) {
				double x = gameMap.getSpawns().get(1).getX()+0.5;
				double y = gameMap.getSpawns().get(1).getY();
				double z =  gameMap.getSpawns().get(1).getZ()+0.5;
				specSpawn = new Location(mapWorld, x, y, z);
			}
	    	minPlayers = (int) (numberOfSpawns * SkyWarsReloaded.getCfg().getMinPercentPlayers()/100);
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
				kills.put(gPlayer, 0);
				gPlayer.setGame(gameNumber);
				gPlayer.setInGame(true);
				if (SkyWarsReloaded.getCfg().bungeeEnabled()) {
					BungeeUtil.sendSignUpdateRequest(this);
				}
				if (SkyWarsReloaded.getCfg().signJoinMode()) {
					SkyWarsReloaded.getGC().updateSign(gameNumber);
				}
				SkyWarsReloaded.getInvC().add(gPlayer.getP());
				preparePlayerForLobby(gPlayer);
				if (SkyWarsReloaded.getCfg().resetTimerEnabled()) {
					if (SkyWarsReloaded.getCfg().getResetTimerThreshold() >= (gPlayers.size()/numberOfSpawns)) {
						count = 0;
					}
				}
			} 
		}
	}
	
	private void preparePlayerForLobby(final GamePlayer gPlayer) {
		Location location;
		int spawn = getAvailableSpawn();
		location = new Location(mapWorld, gameMap.getSpawns().get(spawn).getX()+0.5, gameMap.getSpawns().get(spawn).getY(), gameMap.getSpawns().get(spawn).getZ()+0.5);
		availableSpawns.put(spawn,  gPlayer);
		gPlayer.getP().teleport(location, TeleportCause.PLUGIN);
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
					SkyWarsReloaded.getNMS().sendTitle(gPlayer.getP(), 20, 60, 20, new Messaging.MessageFormatter().setVariable("mapname", mapName).formatNoColor("titles.joinGameTitle"), SkyWarsReloaded.getMessaging().getMessage("titles.joinGameSubtitle"));
					for (PotionEffect effect : gPlayer.getP().getActivePotionEffects()) {
				        gPlayer.getP().removePotionEffect(effect.getType());
					}
					gPlayer.getP().setGameMode(GameMode.ADVENTURE);
					scoreboardData.put(gPlayer.getP().getName(), 1);
					gPlayer.getP().setScoreboard(SkyWarsReloaded.get().getServer().getScoreboardManager().getNewScoreboard());
					gPlayer.getP().setScoreboard(scoreboard);
					updateScoreboard();
					gPlayer.getP().getInventory().setItem(SkyWarsReloaded.getCfg().getExitItemSlot(), SkyWarsReloaded.getCfg().getExitGameItem());
					if (SkyWarsReloaded.getCfg().optionsMenuEnabled()) {
						gPlayer.getP().getInventory().setItem(SkyWarsReloaded.getCfg().getOptionsItemSlot(), SkyWarsReloaded.getCfg().getOptionsItem());
					}
					if (SkyWarsReloaded.getCfg().kitsEnabled()) {
						gPlayer.getP().getInventory().setItem(SkyWarsReloaded.getCfg().getKitMenuItemSlot(), SkyWarsReloaded.getCfg().getKitMenuItem());
					}
					String color = gPlayer.getGlass();
					if (color == null) {
						color = "normal";
					}
					if (!color.equalsIgnoreCase("normal")) {
						GlassColor colorGlass = SkyWarsReloaded.getGLC().getByColor(color);
						if (colorGlass != null) {
							setGlass(colorGlass.getMaterial(), colorGlass.getData(), gPlayer);
						} else {
							setGlass(Material.GLASS, gPlayer);
						}
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
		playSound(SkyWarsReloaded.getCfg().getJoinSound());
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
		
		for (EmptyChest eChest: gameMap.getDoubleChests().values()) {
			Location loc;
			int x = eChest.getX();
			int y = eChest.getY();
			int z = eChest.getZ();
			loc = new Location (mapWorld, x, y, z);
			Chest chest = (Chest) loc.getBlock().getState();
            InventoryHolder ih = chest.getInventory().getHolder();
            DoubleChest dc = (DoubleChest) ih;
			SkyWarsReloaded.getCC().populateDoubleChest(dc, vote);
		}
	}

	public void prepareForStart() {
		if (gameState == GameState.PREGAME) {
			if (gPlayers.size() >= minPlayers) {
				if (count == 0) {
					sendGameMessage(new Messaging.MessageFormatter()
    					.withPrefix()
    					.setVariable("time", "" + SkyWarsReloaded.getCfg().preGameTimer())
    					.format("game.countdown"));
				} else if (count > 0 && count < SkyWarsReloaded.getCfg().preGameTimer()) {
					if ((SkyWarsReloaded.getCfg().preGameTimer() - count) % 5 == 0) {
						sendGameMessage(new Messaging.MessageFormatter()
    					.withPrefix()
    					.setVariable("time", "" + (SkyWarsReloaded.getCfg().preGameTimer() - count))
    					.format("game.countdown-continue"));
					} else if ((SkyWarsReloaded.getCfg().preGameTimer() - count) < 5 ) {
						sendGameMessage(new Messaging.MessageFormatter()
    					.withPrefix()
    					.setVariable("time", "" + (SkyWarsReloaded.getCfg().preGameTimer() - count))
    					.format("game.countdown-continue"));
					}

				} else if (count >= SkyWarsReloaded.getCfg().preGameTimer()) {
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
									kills.remove(gPlayer);
									checkForWinner();
								}
							} else {
								gPlayers.remove(gPlayer);
								kills.remove(gPlayer);
								checkForWinner();
							}
						}
					} else {
						gPlayers.remove(gPlayer);
						kills.remove(gPlayer);
						checkForWinner();
					}
				}
			} else if (sanityChecker > 5) {
				sanityChecker = 0;
			}
		}
		
		if(thunderStorm) {
			if (strikeCounter == nextStrike) {
				int hitPlayer = getRandomNum(100, 1);
				if (hitPlayer <= SkyWarsReloaded.getCfg().getStrikeChance()) {
					int size = gPlayers.size();
					Player player = gPlayers.get(getRandomNum(size-1, 0)).getP();
					mapWorld.strikeLightning(player.getLocation());
				} else {
					int x = getRandomNum(max, min);
					int z = getRandomNum(max, min);
					int y = getRandomNum(50, 20);
					mapWorld.strikeLightningEffect(new Location(mapWorld, x, y, z));
				}
				nextStrike = getRandomNum(20, 3);
				strikeCounter = 0;
			} else {
				strikeCounter++;
			}
		}
		
		if(gameState == GameState.PLAYING && !forceEnd) {
			gameLength++;
			if ((SkyWarsReloaded.getCfg().getMaxGameLength() - gameLength) == 300) {
				sendGameMessage(new Messaging.MessageFormatter()
				.withPrefix()
				.setVariable("time", "" + 5)
				.format("game.gameEndingTimer"));
			} else if ((SkyWarsReloaded.getCfg().getMaxGameLength() - gameLength) == 120) {
				sendGameMessage(new Messaging.MessageFormatter()
				.withPrefix()
				.setVariable("time", "" + 2)
				.format("game.gameEndingTimer"));
			} else if ((SkyWarsReloaded.getCfg().getMaxGameLength() - gameLength) == 60) {
				sendGameMessage(new Messaging.MessageFormatter()
				.withPrefix()
				.setVariable("time", "" + 1)
				.format("game.gameEndingTimer"));
			} else if ((SkyWarsReloaded.getCfg().getMaxGameLength() - gameLength) <= 0) {
				forceEnd = true;
				sendGameMessage(new Messaging.MessageFormatter()
				.withPrefix()
				.format("game.forceGameEnd"));
				int highest = 0; 
				GamePlayer winner = null;
				for (GamePlayer gplayer: kills.keySet()) {
					if (kills.get(gplayer) >= highest) {
						winner = gplayer;
					}
				}
				for (GamePlayer gplayer: getPlayers()) {
					if (winner != gplayer) {
						gplayer.getP().teleport(new Location(mapWorld, 0, -64, 0), TeleportCause.PLUGIN);
					}
				}
			}
		}

	}

	public void startGame() {
		gameState = GameState.PLAYING;
		if (SkyWarsReloaded.getCfg().bungeeEnabled()) {
			BungeeUtil.sendSignUpdateRequest(this);
		}
		if (SkyWarsReloaded.getCfg().signJoinMode()) {
			SkyWarsReloaded.getGC().updateSign(gameNumber);
		}
		fillChests();
		removeSpawnHousing();
		for (GamePlayer gPlayer: getPlayers()) {
			if (gPlayer.getP() != null) {
				gPlayer.getP().setGameMode(GameMode.SURVIVAL);
			    gPlayer.getP().getInventory().remove(SkyWarsReloaded.getCfg().getKitMenuItem());
			    gPlayer.getP().getInventory().remove(SkyWarsReloaded.getCfg().getOptionsItem());
			    gPlayer.getP().getInventory().remove(SkyWarsReloaded.getCfg().getExitGameItem());
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
		if (SkyWarsReloaded.getCfg().timeVoteEnabled()) {
			setTime();
		}
		if (SkyWarsReloaded.getCfg().jumpVoteEnabled()) {
			setJump();
		}
		if (SkyWarsReloaded.getCfg().weatherVoteEnabled()) {
			setWeather();
		}
	}
	
	public void endGame() {
		gameState = GameState.ENDING;
		if (SkyWarsReloaded.getCfg().bungeeEnabled()&& !shutdown) {
			BungeeUtil.sendSignUpdateRequest(this);
		}
		if (SkyWarsReloaded.getCfg().signJoinMode() && !shutdown) {
			SkyWarsReloaded.getGC().updateSign(gameNumber);
		}
		for (GamePlayer gplayer: getPlayers()) {
			deletePlayer(gplayer, false, false);
		}
		if (SkyWarsReloaded.getCfg().spectatingEnabled()) {
			for (GamePlayer gPlayer: getSpectators()) {
				removeSpectator(gPlayer);
			}
		}
		for (Player player: mapWorld.getPlayers()) {
			if (player != null) {
				player.teleport(SkyWarsReloaded.getCfg().getSpawn(), TeleportCause.PLUGIN);
			}
		}
		
		for (Entity entity: mapWorld.getEntities()) {
			if (entity != null) {
				entity.remove();
			}
		}
		if (!SkyWarsReloaded.getCfg().spectatingEnabled() && !shutdown) {
				SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
					@Override
					public void run() {
						deleteGame();
					}
				}, 20 * SkyWarsReloaded.getCfg().getTimeAfterGame());
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
		
		if (SkyWarsReloaded.getCfg().usePlayerNames()) {
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
			kills.remove(gplayer);
		}
		if (playerQuit) {
			playerQuit(gplayer);
		}
		if (!hardQuit) {
			preparePlayerForExit(gplayer);
		}
		if (gameState == GameState.PREGAME || gameState == GameState.PLAYING) {
			if (SkyWarsReloaded.getCfg().bungeeEnabled() && !shutdown) {
				BungeeUtil.sendSignUpdateRequest(this);
			}
			if (SkyWarsReloaded.getCfg().signJoinMode() && !shutdown) {
				SkyWarsReloaded.getGC().updateSign(gameNumber);
			}
		}
		if (SkyWarsReloaded.getCfg().bungeeEnabled() && !shutdown && !hardQuit) {
			if (gplayer.getP() != null) {
				gplayer.getP().teleport(SkyWarsReloaded.getCfg().getSpawn(), TeleportCause.PLUGIN);
				SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
					public void run() {
						SkyWarsReloaded.getInvC().restoreInventory(gplayer.getP());
						BungeeUtil.connectToServer(gplayer.getP(), SkyWarsReloaded.getCfg().getLobbyServer());
					}
				}, 5);
			}
		} else {
			if (gplayer.getP() != null) {
				gplayer.getP().teleport(SkyWarsReloaded.getCfg().getSpawn(), TeleportCause.PLUGIN);
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
						kills.put(killer, kills.get(killer) + 1);
						gPlayer.setDeaths(gPlayer.getDeaths() + 1);
						int killTotal = SkyWarsReloaded.getCfg().getKillValue();
						if (killer.getP() != null) {
							if (killer.getP().hasPermission("swr.vip")) {
								killTotal = SkyWarsReloaded.getCfg().getKillValue() * SkyWarsReloaded.getCfg().getVIPMultiplier();
							} else {
								killTotal = SkyWarsReloaded.getCfg().getKillValue();
							}
						}
						killer.setScore(killer.getScore() + killTotal);
						gPlayer.setScore(gPlayer.getScore() - SkyWarsReloaded.getCfg().getDeathValue());
						addBalance(killer, killTotal);
						removeBalance(gPlayer, SkyWarsReloaded.getCfg().getDeathValue());
						sendGameMessage(new Messaging.MessageFormatter()
						.withPrefix()
						.setVariable("player", gPlayer.getName())
						.setVariable("player_score", StringUtils.formatScore(-SkyWarsReloaded.getCfg().getDeathValue()))
						.setVariable("killer", killer.getName())
						.setVariable("killer_score", StringUtils.formatScore(killTotal))
						.format("game.death.quit-while-tagged"));
					}
				} else {
					gPlayer.setScore(gPlayer.getScore() - SkyWarsReloaded.getCfg().getLeaveValue());
					removeBalance(gPlayer, SkyWarsReloaded.getCfg().getLeaveValue());
					sendGameMessage(new Messaging.MessageFormatter()
							.withPrefix()
							.setVariable("player", gPlayer.getName())
							.setVariable("score", StringUtils.formatScore(-SkyWarsReloaded.getCfg().getLeaveValue()))
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
						.setVariable("number", "" + (gPlayers.size()))
						.setVariable("total", "" + numberOfSpawns)
						.setVariable("player", gPlayer.getName())
						.format("game.lobby-leave"));
				playSound(SkyWarsReloaded.getCfg().getLeaveSound());
			}
	}
	
	private void preparePlayerForExit(GamePlayer gplayer) {
		if (gplayer.getP() != null) {
			gplayer.setInGame(false);
			gplayer.setKitSelected(false);
			gplayer.getP().getInventory().clear();
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
		SkyWarsReloaded.getWC().deleteWorld(mapName + "_" + gameNumber);
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

	public void onPlayerDeath(final GamePlayer target, final DamageCause dCause, final Location loc) {
		gPlayers.remove(target);
		kills.remove(target);
		scoreboardData.put(target.getP().getName(), 0);
		updateScoreboard();
		preparePlayerForExit(target);

		if (SkyWarsReloaded.getCfg().spectatingEnabled()) {
			target.setSpectating(true);
			target.setSpecGame(gameNumber);
		} 
		
		final Game game = this;
		
		SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
			@Override
			public void run() {
				if (target.getP() != null) {
					SkyWarsReloaded.getNMS().respawnPlayer(target.getP());
				}
				if (!SkyWarsReloaded.getCfg().spectatingEnabled()) {
					deletePlayer(target, false, false);
				}
				target.setDeaths(target.getDeaths() + 1);
				mapWorld.playEffect(loc, Effect.MOBSPAWNER_FLAMES, 4);
				if ((System.currentTimeMillis() - target.getTagged().getTime()) < 10000) {
					GamePlayer killer = target.getTagged().getPlayer();
					if (killer != target) {
						killer.setKills(killer.getKills() + 1);
						if (kills.get(killer) != null) {
							kills.put(killer, kills.get(killer) + 1);
						}
						int killTotal = SkyWarsReloaded.getCfg().getKillValue();
						if (killer.getP() != null) {
							if (killer.getP().hasPermission("swr.vip")) {
								killTotal = SkyWarsReloaded.getCfg().getKillValue() * SkyWarsReloaded.getCfg().getVIPMultiplier();
							} else {
								killTotal = SkyWarsReloaded.getCfg().getKillValue();
							}
						}
						killer.setScore(killer.getScore() + killTotal);
						target.setScore(target.getScore() - SkyWarsReloaded.getCfg().getDeathValue());
						addBalance(killer, killTotal);
						removeBalance(target, SkyWarsReloaded.getCfg().getDeathValue());
						target.setTagged(target);
						if (target.getP() != null) {
							target.getP().sendMessage(getDeathMessage(dCause, true, target, killer));
							target.getP().playSound(target.getP().getLocation(), SkyWarsReloaded.getCfg().getDeathSound(), 1, 1);
						}
						sendGameMessage(getDeathMessage(dCause, true, target, killer));
						playSound(SkyWarsReloaded.getCfg().getDeathSound());
					}
				} else {
					target.setScore(target.getScore() - SkyWarsReloaded.getCfg().getDeathValue());
					removeBalance(target, SkyWarsReloaded.getCfg().getDeathValue());
					sendGameMessage(getDeathMessage(dCause, false, target, target));
					playSound(SkyWarsReloaded.getCfg().getDeathSound());
				}
				target.setGamesPlayed(target.getGamesPlayed() + 1);
				if (gameState == GameState.PREGAME || gameState == GameState.PLAYING) {
					if (SkyWarsReloaded.getCfg().bungeeEnabled() && !shutdown) {
						BungeeUtil.sendSignUpdateRequest(game);
					}
					if (SkyWarsReloaded.getCfg().signJoinMode() && !shutdown) {
						SkyWarsReloaded.getGC().updateSign(gameNumber);
					}
				}
				checkForWinner();
			}
		}, 1);
	}
	
	public void checkForWinner() {
		if (gameState == GameState.PLAYING) {
			if (getPlayers().size() == 1) {
				gameState = GameState.ENDING;
				if (SkyWarsReloaded.getCfg().bungeeEnabled()) {
        			BungeeUtil.sendSignUpdateRequest(this);
        		}
    			if (SkyWarsReloaded.getCfg().signJoinMode()) {
    				SkyWarsReloaded.getGC().updateSign(gameNumber);
    			}
    			GamePlayer gPlayer = getPlayers().get(0);
    			gPlayers.remove(gPlayer);
    			kills.remove(gPlayer);
    			preparePlayerForExit(gPlayer);
				gPlayer.setWins(gPlayer.getWins() + 1);
				gPlayer.setGamesPlayed(gPlayer.getGamesPlayed() + 1);
				playSound(SkyWarsReloaded.getCfg().getWinSound());
				if (gPlayer.getP() != null) {
					Location loc = gPlayer.getP().getEyeLocation();
					launchFireworkDisplay(mapWorld, loc);
				}
				int winTotal = SkyWarsReloaded.getCfg().getWinValue();
    			if (gPlayer.getP() != null) {
    				if (gPlayer.getP().hasPermission("swr.vip")) {
    					winTotal = SkyWarsReloaded.getCfg().getWinValue() * SkyWarsReloaded.getCfg().getVIPMultiplier();
    				} else {
    					winTotal = SkyWarsReloaded.getCfg().getWinValue();
    				}
    			}
				gPlayer.setScore(gPlayer.getScore() + winTotal);
				addBalance(gPlayer, winTotal);
 				Bukkit.getServer().getPluginManager().callEvent(new GameEndEvent(gPlayer.getP(), mapName));
 				if (SkyWarsReloaded.getCfg().spectatingEnabled()) {
					if (gPlayer.getP() != null) {
						gPlayer.spectateMode(true, this, gPlayer.getP().getLocation(), shutdown);
						sendGameMessage(new Messaging.MessageFormatter().withPrefix()
								.setVariable("time", "" + SkyWarsReloaded.getCfg().getTimeAfterGame())
								.format("game.gameEnding"));
					}
					SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
						public void run() {
							endGame();
						  }
						}, 20 * SkyWarsReloaded.getCfg().getTimeAfterGame());
				} else {
					deletePlayer(gPlayer, false, false);
					endGame();
				}
				if (SkyWarsReloaded.getCfg().WinBroadcastDisabled()) {
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
			} else if (getPlayers().size() < 1) {
				gameState = GameState.ENDING;
				if (SkyWarsReloaded.getCfg().bungeeEnabled()) {
        			BungeeUtil.sendSignUpdateRequest(this);
        		}
    			if (SkyWarsReloaded.getCfg().signJoinMode()) {
    				SkyWarsReloaded.getGC().updateSign(gameNumber);
    			}
				if (SkyWarsReloaded.getCfg().spectatingEnabled()) {
					sendGameMessage(new Messaging.MessageFormatter().withPrefix()
							.setVariable("time", "" + SkyWarsReloaded.getCfg().getTimeAfterGame())
							.format("game.gameEnding"));
					SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
						public void run() {
							endGame();
						  }
						}, 20 * SkyWarsReloaded.getCfg().getTimeAfterGame());
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
		if (SkyWarsReloaded.getCfg().spectatingEnabled()) {
			for (GamePlayer spectator: getSpectators()) {
				if (spectator.getP() != null) {
					spectator.getP().sendMessage(message);
				}
			}
		}
	}
	
	public void playSound(Sound sound) {
		for (GamePlayer gamePlayer: getPlayers()) {
			if (gamePlayer.getP() != null) {
				gamePlayer.getP().playSound(gamePlayer.getP().getLocation(), sound, 1, 1);
			}
		}
		for (GamePlayer gamePlayer: getSpectators()) {
			if(!gPlayers.contains(gamePlayer)) {
				if (gamePlayer.getP() != null) {
					gamePlayer.getP().playSound(gamePlayer.getP().getLocation(), sound, 1, 1);
				}

			}
		}
	}

	private String getDeathMessage(DamageCause dCause, boolean withHelp, GamePlayer target, GamePlayer killer) {
		String first = "";
		int killTotal = SkyWarsReloaded.getCfg().getKillValue();
		if (killer.getP() != null) {
			if (killer.getP().hasPermission("swr.vip")) {
				killTotal = SkyWarsReloaded.getCfg().getKillValue() * SkyWarsReloaded.getCfg().getVIPMultiplier();
			} else {
				killTotal = SkyWarsReloaded.getCfg().getKillValue();
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
					.setVariable("player_score", StringUtils.formatScore(-SkyWarsReloaded.getCfg().getDeathValue()))
					.format("game.death.explosion");
		} else if (dCause.equals(DamageCause.DROWNING)) {
			first = new Messaging.MessageFormatter()
			.withPrefix()
			.setVariable("player", target.getName())
			.setVariable("player_score", StringUtils.formatScore(-SkyWarsReloaded.getCfg().getDeathValue()))
			.format("game.death.drowning");
		} else if (dCause.equals(DamageCause.FIRE) || dCause.equals(DamageCause.FIRE_TICK)) {
			first = new Messaging.MessageFormatter()
			.withPrefix()
			.setVariable("player", target.getName())
			.setVariable("player_score", StringUtils.formatScore(-SkyWarsReloaded.getCfg().getDeathValue()))
			.format("game.death.fire");
		} else if (dCause.equals(DamageCause.ENTITY_ATTACK)) {
			first = new Messaging.MessageFormatter()
			.withPrefix()
			.setVariable("player", target.getName())
			.setVariable("player_score", StringUtils.formatScore(-SkyWarsReloaded.getCfg().getDeathValue()))
			.setVariable("killer", killer.getName())
			.setVariable("killer_score", StringUtils.formatScore(killTotal))
			.format("game.death.pvp");
			second = "";
		} else if (dCause.equals(DamageCause.FALLING_BLOCK)) {
			first = new Messaging.MessageFormatter()
			.withPrefix()
			.setVariable("player", target.getName())
			.setVariable("player_score", StringUtils.formatScore(-SkyWarsReloaded.getCfg().getDeathValue()))
			.format("game.death.falling-block");
		} else if (dCause.equals(DamageCause.LAVA)) {
			first = new Messaging.MessageFormatter()
			.withPrefix()
			.setVariable("player", target.getName())
			.setVariable("player_score", StringUtils.formatScore(-SkyWarsReloaded.getCfg().getDeathValue()))
			.format("game.death.lava");
		} else if (dCause.equals(DamageCause.PROJECTILE)) {
			first = new Messaging.MessageFormatter()
			.withPrefix()
			.setVariable("player", target.getName())
			.setVariable("player_score", StringUtils.formatScore(-SkyWarsReloaded.getCfg().getDeathValue()))
			.setVariable("killer", killer.getName())
			.setVariable("killer_score", StringUtils.formatScore(killTotal))
			.format("game.death.projectile");
			second = "";
		} else if (dCause.equals(DamageCause.SUFFOCATION)) {
			first = new Messaging.MessageFormatter()
			.withPrefix()
			.setVariable("player", target.getName())
			.setVariable("player_score", StringUtils.formatScore(-SkyWarsReloaded.getCfg().getDeathValue()))
			.format("game.death.suffocation");
		} else if (dCause.equals(DamageCause.VOID)) {
			first = new Messaging.MessageFormatter()
			.withPrefix()
			.setVariable("player", target.getName())
			.setVariable("player_score", StringUtils.formatScore(-SkyWarsReloaded.getCfg().getDeathValue()))
			.format("game.death.void");
		} else {
			first = new Messaging.MessageFormatter()
			.withPrefix()
			.setVariable("player", target.getName())
			.setVariable("player_score", StringUtils.formatScore(-SkyWarsReloaded.getCfg().getDeathValue()))
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

	public void launchFireworkDisplay(final World w, final Location loc) {
	    Firework fw = (Firework) w.spawn(loc.clone().add(new Vector(getRandomNum(5, -5), 1, getRandomNum(5, -5))), Firework.class);
	    FireworkMeta meta = fw.getFireworkMeta();
	    FireworkEffect effect = SkyWarsReloaded.getNMS().getFireworkEffect(getRandomColor(),getRandomColor(), getRandomColor(), getRandomColor(), getRandomColor(), getRandomType());
	    meta.addEffect(effect);
	    meta.setPower(getRandomNum(4, 1));
	    fw.setFireworkMeta(meta);
	    fireworksCount++;
	    if (fireworksCount < ((SkyWarsReloaded.getCfg().getTimeAfterGame() - 5)*4)) {
			SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(),  new Runnable() {
				public void run() {
					launchFireworkDisplay(w, loc);
				}
			}, 5);
	    }
	}
	
	public int getRandomNum(int max, int min) {
		Random rand = new Random();
	    int ii = min + rand.nextInt(((max - (min)) + 1));
	    return ii;
	}
	
	public Type getRandomType() {
		int type = getRandomNum(5, 1);
		switch (type) {
		case 1: return Type.STAR;
		case 2: return Type.CREEPER;
		case 3: return Type.BURST;
		case 4: return Type.BALL_LARGE;
		case 5: return Type.BALL;
		default: return Type.STAR;
		}
	}
	
	public Color getRandomColor() {
		int color = getRandomNum(17, 1);
		switch (color) {
		case 1: return Color.AQUA;
		case 2: return Color.BLACK;
		case 3: return Color.BLUE;
		case 4: return Color.FUCHSIA;
		case 5: return Color.GRAY;
		case 6: return Color.GREEN;
		case 7: return Color.LIME;
		case 8: return Color.MAROON;
		case 9: return Color.NAVY;
		case 10: return Color.OLIVE;
		case 11: return Color.ORANGE;
		case 12: return Color.PURPLE;
		case 13: return Color.RED;
		case 14: return Color.SILVER;
		case 15: return Color.TEAL;
		case 16: return Color.WHITE;
		case 17: return Color.YELLOW;
		default: return Color.RED;
		}
	}
	
	public void removeSpectator(GamePlayer gPlayer) {
		spectators.remove(gPlayer);
		if (SkyWarsReloaded.getCfg().bungeeEnabled()) {
			if (gPlayer.getP() != null) {
				gPlayer.spectateMode(false, this, SkyWarsReloaded.getCfg().getSpawn(), shutdown);
				BungeeUtil.connectToServer(gPlayer.getP(), SkyWarsReloaded.getCfg().getLobbyServer());
			}
		} else {
			if (gPlayer.getP() != null) {
				gPlayer.spectateMode(false, this, SkyWarsReloaded.getCfg().getSpawn(), shutdown);
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
			world.setThundering(true);
			world.setThunderDuration(Integer.MAX_VALUE);
			world.setWeatherDuration(Integer.MAX_VALUE);
			thunderStorm = true;
		} else if (weather.equalsIgnoreCase("snow")) {
			for (int x = min; x < max; x++) {
				for (int z = min; z < max; z++) {
					world.setBiome(x, z, Biome.ICE_PLAINS);
				}
			}
			world.setStorm(true);
			world.setWeatherDuration(Integer.MAX_VALUE);
			List<Chunk> chunks = getChunks();
			SkyWarsReloaded.getNMS().updateChunks(mapWorld, chunks);
			world.setStorm(true);
			world.setWeatherDuration(Integer.MAX_VALUE);
		}
	}
	
	
	
	private List<Chunk> getChunks() {
		int minX = min;
		int minZ = min;
		int maxX = max;
		int maxZ = max;
		int minY = 0;
		int maxY = 0;
		Block min = mapWorld.getBlockAt(minX, minY, minZ);
		Block max = mapWorld.getBlockAt(maxX, maxY, maxZ);
		Chunk cMin = min.getChunk();
		Chunk cMax = max.getChunk();
		List<Chunk> chunks = new ArrayList<Chunk>();
		
		for(int cx = cMin.getX(); cx < cMax.getX(); cx++) {
			for(int cz = cMin.getZ(); cz < cMax.getZ(); cz++) {
		           Chunk currentChunk = mapWorld.getChunkAt(cx, cz);
		           chunks.add(currentChunk);
			}
		}
		return chunks;
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
					gPlayer.getP().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, SkyWarsReloaded.getCfg().getHighJumpValue()));
				}
			}
		} else if (jump.equalsIgnoreCase("super jump")) {
			for (GamePlayer gPlayer: getPlayers()) {
				if (gPlayer.getP() != null) {
					gPlayer.getP().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, SkyWarsReloaded.getCfg().getSuperJumpValue()));
				}
			}
		} else if (jump.equalsIgnoreCase("god jump")) {
			for (GamePlayer gPlayer: getPlayers()) {
				if (gPlayer.getP() != null) {
					gPlayer.getP().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, SkyWarsReloaded.getCfg().getGodJumpValue()));
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
    	if (SkyWarsReloaded.getCfg().usingExternalEcomony()) {
        	SkyWarsReloaded.econ.withdrawPlayer(SkyWarsReloaded.get().getServer().getOfflinePlayer(p.getUUID()), x);
    	} else {
    		p.setBalance(p.getBalance() - x);
    	}
    }
    
    private void addBalance(GamePlayer p, int x) {
    	if (SkyWarsReloaded.getCfg().usingExternalEcomony()) {
    		SkyWarsReloaded.econ.depositPlayer(SkyWarsReloaded.get().getServer().getOfflinePlayer(p.getUUID()), x);
    	} else {
    		p.setBalance(p.getBalance() + x);
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
