package com.walrusone.skywarsreloaded.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.database.DataStorage;
import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.enums.Vote;
import com.walrusone.skywarsreloaded.objects.EmptyChest;
import com.walrusone.skywarsreloaded.objects.GameKit;
import com.walrusone.skywarsreloaded.objects.GameMap;
import com.walrusone.skywarsreloaded.objects.ParticleEffect;
import com.walrusone.skywarsreloaded.objects.ParticleItem;
import com.walrusone.skywarsreloaded.objects.Party;
import com.walrusone.skywarsreloaded.objects.PlayerCard;
import com.walrusone.skywarsreloaded.objects.PlayerData;
import com.walrusone.skywarsreloaded.objects.PlayerStat;
import com.walrusone.skywarsreloaded.objects.SoundItem;
import com.walrusone.skywarsreloaded.utilities.Messaging;
import com.walrusone.skywarsreloaded.utilities.Util;
import com.walrusone.skywarsreloaded.utilities.VaultUtils;

public class MatchManager
{
    private static MatchManager instance;
    private int waitTime;
    private int gameTime;
    private String debugName;
    private boolean debug;

    
    public static MatchManager get() {
        if (MatchManager.instance == null) {
            MatchManager.instance = new MatchManager();
        }
        return MatchManager.instance;
    }
    
    public World getWorld(GameMap gameMap) {
        World mapWorld;
		mapWorld = SkyWarsReloaded.get().getServer().getWorld(gameMap.getName() + "_" + gameMap.getMapCount());
		return mapWorld;
    }
       
    public boolean joinGame(Player player) {
    	GameMap.shuffle();
    	GameMap map = null;
    	int highest = 0;
        for (final GameMap gameMap : GameMap.getMaps()) {
            if (gameMap.canAddPlayer() && highest <= gameMap.getPlayerCount()) {
                map = gameMap;
                highest = gameMap.getPlayerCount();
            }
        }
        boolean joined = false;
		if (map != null) {
			joined = map.addPlayer(player);
		}
		if (!joined) {
			return false;
		} else {
			return true;
		}
    }
    
    public boolean joinGame(Party party) {
    	GameMap.shuffle();
    	GameMap map = null;
    	int highest = 0;
        for (final GameMap gameMap : GameMap.getMaps()) {
            if (gameMap.canAddParty(party) && highest <= gameMap.getPlayerCount()) {
                map = gameMap;
                highest = gameMap.getPlayerCount();
            }
        }
        boolean joined = false;
		if (map != null) {
			joined = map.addParty(party);
		}
		if (!joined) {
			return false;
		} else {
			return true;
		}
    }
    
    public void start(final GameMap gameMap) {
    	debug = SkyWarsReloaded.getCfg().debugEnabled();
    	if (debug) {
         	debugName = ChatColor.RED + "SWR[" + gameMap.getName() + "] ";
        }
        if (gameMap == null) {
            return;
        }
        this.setWaitTime(SkyWarsReloaded.getCfg().getWaitTimer());
        this.setGameTime(SkyWarsReloaded.getCfg().getGameTimer());
        gameMap.setMatchState(MatchState.WAITINGSTART);
        gameMap.update();
        this.waitStart(gameMap);
    }
    
    
    public void message(final GameMap gameMap, final String message) {
    	for (final Player player : gameMap.getAlivePlayers()) {
        	if (player != null) {
        		player.sendMessage(message);
        	}
        }
    	for (final UUID uuid : gameMap.getSpectators()) {
        	Player player = SkyWarsReloaded.get().getServer().getPlayer(uuid);
        	if (player != null) {
        		player.sendMessage(message);
        	}
        }
    }
           
    public void teleportToArena(final GameMap gameMap, Player player, Location spawn) {
    	doTeleport(player, gameMap, spawn);
    	new BukkitRunnable() {
			@Override
			public void run() {
		    	preparePlayer(player, gameMap);
			}
    	}.runTaskLater(SkyWarsReloaded.get(), 2);
    }
    
    private void preparePlayer(Player player, GameMap gameMap) {
    	if (debug) {
    		Util.get().logToFile(debugName + ChatColor.YELLOW + "Preparing " + player.getName() + " for SkyWars");
    	}
        PlayerData.getPlayerData().add(new PlayerData(player));
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setHealth(20.0);
        player.setExp(0.0f);
        player.setLevel(0);
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setGameMode(GameMode.ADVENTURE);
		player.setScoreboard(SkyWarsReloaded.get().getServer().getScoreboardManager().getNewScoreboard());
		player.setScoreboard(gameMap.getScoreboard());
		
        Util.get().clear(player);
        player.getInventory().setBoots(new ItemStack(Material.AIR, 1));
        player.getInventory().setChestplate(new ItemStack(Material.AIR, 1));
        player.getInventory().setHelmet(new ItemStack(Material.AIR, 1));
        player.getInventory().setLeggings(new ItemStack(Material.AIR, 1));
        
        if (SkyWarsReloaded.getCfg().areKitsEnabled()) {
            ItemStack kitItem = SkyWarsReloaded.getIM().getItem("kitvote");
            player.getInventory().setItem(SkyWarsReloaded.getCfg().getKitVotePos(), kitItem);
        }

        if (SkyWarsReloaded.getCfg().votingEnabled()) {
            ItemStack timeItem = SkyWarsReloaded.getIM().getItem("votingItem");
            player.getInventory().setItem(SkyWarsReloaded.getCfg().getVotingPos(), timeItem);
        }
        
        ItemStack exitItem = SkyWarsReloaded.getIM().getItem("exitGameItem");
        player.getInventory().setItem(SkyWarsReloaded.getCfg().getExitPos(), exitItem);

    	if (debug) {
    		Util.get().logToFile(debugName + ChatColor.YELLOW + "Finished Preparing " + player.getName() + " for SkyWars on map " + gameMap.getName());
    	}
    }
    
    private void doTeleport(Player player, GameMap gameMap, Location spawn){
    	if (debug) {     	
        	Util.get().logToFile(debugName + ChatColor.YELLOW + "Teleporting " + player.getName() + " to Skywars on map" + gameMap.getName());
        }
        PlayerStat pStat = PlayerStat.getPlayerStats(player);
        gameMap.setGlassColor(player, pStat.getGlassColor());
        World world = getWorld(gameMap);
		Location newSpawn = new Location(world, spawn.getX() + 0.5, spawn.getY(), spawn.getZ() + 0.5);
        player.teleport(newSpawn, TeleportCause.END_PORTAL);
        
        String key = PlayerStat.getPlayerStats(player.getUniqueId()).getParticleEffect();
        ParticleItem effect = SkyWarsReloaded.getLM().getParticleByKey(key);
        if (effect != null) {
    		List<ParticleEffect> effects = effect.getEffects();
            SkyWarsReloaded.getLM().addPlayer(player.getUniqueId(), effects);
        }
        
        if (SkyWarsReloaded.getCfg().titlesEnabled()) {
        	for (final Player p : gameMap.getAlivePlayers()) {
        		if (!p.equals(player)) {
                    Util.get().sendTitle(p, 2, 20, 2, "", 
                    		new Messaging.MessageFormatter().setVariable("player", player.getDisplayName()).format("game.joined-the-game"));
    			}
        	}
        } 
   		message(gameMap, new Messaging.MessageFormatter().setVariable("player", player.getDisplayName()).format("game.joined-the-game"));
        
       	for (final Player p : gameMap.getAlivePlayers()) {
    		if (!p.equals(player)) {
    			Util.get().playSound(p, p.getLocation(), SkyWarsReloaded.getCfg().getJoinSound(), 1, 1);
    		}
        }
        
    	if (debug) {
    		if (gameMap.getAlivePlayers().size() < gameMap.getMinPlayers()) {
        		Util.get().logToFile(debugName + ChatColor.YELLOW + "Waiting for More Players on map " + gameMap.getName());
    		} else {
    			Util.get().logToFile(debugName + ChatColor.YELLOW + "Starting Countdown for SkyWars Match on map " + gameMap.getName());
    		}
    	}
    	gameMap.setMatchState(MatchState.WAITINGSTART);
    	String designer; 
    	if (SkyWarsReloaded.getCfg().titlesEnabled()) {
        	if (gameMap.getDesigner() != null && gameMap.getDesigner().length() > 0) {
        		designer = new Messaging.MessageFormatter().setVariable("designer", gameMap.getDesigner()).format("titles.start-subtitle");
        	} else {
        		designer = "";
        	}
            Util.get().sendTitle(player, 5, 60, 5, new Messaging.MessageFormatter().setVariable("map", gameMap.getDisplayName().toUpperCase()).format("titles.start-title"), 
            		designer);
    	}
    }
    
    private void waitStart(final GameMap gameMap) {
        gameMap.setTimer(this.getWaitTime());
        new BukkitRunnable() {
            public void run() {
            	if (gameMap.getMatchState() != MatchState.WAITINGSTART) {
            		this.cancel();
            	}
            	if (gameMap.getAlivePlayers().size() >= gameMap.getMinPlayers() || gameMap.getForceStart()) {
                    if (gameMap.getTimer() <= 0) {
                        this.cancel();
                        if (gameMap.getMatchState() != MatchState.ENDING) {
                        	for (final Player player : gameMap.getAlivePlayers()) {
                        		Util.get().playSound(player, player.getLocation(), SkyWarsReloaded.getCfg().getCountdownSound(), 1, 1F);
                        	}
                            MatchManager.this.startMatch(gameMap);
                        }
                    } else {
                        if (gameMap.getTimer() <= 5 && gameMap.getMatchState() != MatchState.ENDING) {
                        	for (final Player player : gameMap.getAlivePlayers()) {
                        		if (SkyWarsReloaded.getCfg().titlesEnabled()) {
                                    Util.get().sendTitle(player, 2, 20, 2, new Messaging.MessageFormatter().
                                    		setVariable("time", "" + gameMap.getTimer()).format("titles.warmup-title"), 
                                    		new Messaging.MessageFormatter().format("titles.warmup-subtitle"));
                        		}
                                if (gameMap.getTimer() == 5) {
                                	Util.get().playSound(player, player.getLocation(), SkyWarsReloaded.getCfg().getCountdownSound(), 1, 0.5F);
                                } else if (gameMap.getTimer() == 4) {
                                	Util.get().playSound(player, player.getLocation(), SkyWarsReloaded.getCfg().getCountdownSound(), 1, 0.6F);
                                } else if (gameMap.getTimer() == 3) {
                                	Util.get().playSound(player, player.getLocation(), SkyWarsReloaded.getCfg().getCountdownSound(), 1, 0.7F);
                                } else if (gameMap.getTimer() == 2) {
                                	Util.get().playSound(player, player.getLocation(), SkyWarsReloaded.getCfg().getCountdownSound(), 1, 0.8F);
                                } else if (gameMap.getTimer() == 1) {
                                	Util.get().playSound(player, player.getLocation(), SkyWarsReloaded.getCfg().getCountdownSound(), 1, 0.9F);
                                } 
                            }
                        }
                        if (gameMap.getTimer() % 5 == 0 || gameMap.getTimer() <= 5) {
                        	MatchManager.this.announceTimer(gameMap, "timer.wait-timer");
                        }
                    }
                    gameMap.setTimer(gameMap.getTimer() - 1);
            	} else {
            		gameMap.setTimer(waitTime);
            	}
            }
        }.runTaskTimer((Plugin)SkyWarsReloaded.get(), 0L, 20L);
    }
    
    public void forceStart(Player player) {
    	GameMap gameMap = this.getPlayerMap(player);
    	gameMap.setForceStart(true);
    }
    
	private void startMatch(final GameMap gameMap) {
    	if (debug) {
    		Util.get().logToFile(debugName + ChatColor.YELLOW + "Starting SkyWars Match");
    	}
    	for (Player player: gameMap.getAlivePlayers()) {
    		player.closeInventory();
    		player.setGameMode(GameMode.SURVIVAL);
    	}
        if (gameMap.getMatchState() != MatchState.ENDING) {
        	this.matchCountdown(gameMap);
        }
        fillChests(gameMap);
        selectTime(gameMap);
        selectWeather(gameMap);
        selectModifier(gameMap);
		selectKit(gameMap);
    	gameMap.removeSpawnHousing();
    }
    
    private void fillChests(GameMap gameMap) {
    	World mapWorld = getWorld(gameMap);
        Vote cVote = gameMap.getVoted("chest");
        for (EmptyChest eChest: gameMap.getChests().values()) {
			Location loc;
			int x = eChest.getX();
			int y = eChest.getY();
			int z = eChest.getZ();
			loc = new Location (mapWorld, x, y, z);
			Chest chest = (Chest) loc.getBlock().getState();
			SkyWarsReloaded.getCM().populateChest(chest, cVote);
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
			SkyWarsReloaded.getCM().populateChest(dc, cVote);
		}
    }
    
    private void selectTime(GameMap gameMap) {
    	Vote time = gameMap.getVoted("time");
		int t = 0;
		if (time == Vote.TIMENOON) {
			t = 6000;
		} else if (time == Vote.TIMEDUSK) {
			t = 12000;
		} else if (time == Vote.TIMEMIDNIGHT) {
			t = 18000;
		}
		for (Player player: gameMap.getAllPlayers()) {
			player.setPlayerTime(t, false);
		}
    }
    
    @SuppressWarnings("deprecation")
	private void selectWeather(GameMap gameMap) {
    	int max;
		int min;
		int size = SkyWarsReloaded.getCfg().getMaxMapSize()/2;
		min = 0 - size;
		max = 0 + size;	
		Vote weather = gameMap.getVoted("weather");
		WeatherType w = WeatherType.CLEAR;
		if (weather != Vote.WEATHERSUN) {
			w = WeatherType.DOWNFALL;
		} 
		if (weather == Vote.WEATHERTHUNDER) {
			gameMap.setThunderStorm(true);
			gameMap.setNextStrike(Util.get().getRandomNum(20, 3));
			gameMap.setStrikeCounter(0);
		} else if (weather == Vote.WEATHERSNOW) {
			World world = gameMap.getAlivePlayers().get(0).getWorld();
			for (int x = min; x < max; x++) {
				for (int z = min; z < max; z++) {
					world.setBiome(x, z, Biome.ICE_MOUNTAINS);
				}
			}
			List<Chunk> chunks = Util.get().getChunks(world);
			for (Chunk chunk: chunks) {
				world.refreshChunk(chunk.getX(), chunk.getZ());
			}
		}
		for (Player player: gameMap.getAllPlayers()) {
			player.setPlayerWeather(w);
		}
    }
    
    private void selectModifier(GameMap gameMap) {
    	Vote modifier = gameMap.getVoted("modifier");
		if (modifier == Vote.MODIFIERSPEED) {
	    	for (Player player: gameMap.getAlivePlayers()) {
	    		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2, true, false));
	    	}
		} else if (modifier ==  Vote.MODIFIERJUMP) {
	    	for (Player player: gameMap.getAlivePlayers()) {
	    		player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 2, true, false));
	    	}
		} else if (modifier ==  Vote.MODIFIERSTRENGTH) {
	    	for (Player player: gameMap.getAlivePlayers()) {
	    		player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 2, true, false));
	    	}
		} 
    }
    
    private void selectKit(GameMap gameMap) {
    	if (SkyWarsReloaded.getCfg().kitVotingEnabled()) {
        	gameMap.getVotedKit();
        	for (final Player player : gameMap.getAlivePlayers()) {
        		GameKit.giveKit(player, gameMap.getKit());         
        	}
    	} else {
        	for (final Player player : gameMap.getAlivePlayers()) {
        		GameKit.giveKit(player, gameMap.getSelectedKit(player));         
        	}
    	}

    }
       
    private void matchCountdown(final GameMap gameMap) {
        if (gameMap.getMatchState() == MatchState.ENDING) {
            return;
        }
        gameMap.setMatchState(MatchState.PLAYING);
        gameMap.update();
        gameMap.setTimer(this.getGameTime());
        new BukkitRunnable() {
            public void run() {
                if (gameMap.getTimer() == 0 && gameMap.getMatchState() != MatchState.ENDING && gameMap.getMatchState() != MatchState.SUDDENDEATH && SkyWarsReloaded.getCfg().suddenDeathEnabled()) {
                	gameMap.setMatchState(MatchState.SUDDENDEATH);
                	if (SkyWarsReloaded.getCfg().disableHealthRegen()) {
                		gameMap.setAllowRegen(false);
                	}
                }
                if (gameMap.getMatchState() == MatchState.SUDDENDEATH) {
                	if (SkyWarsReloaded.getCfg().enableHealthDecay()) {
                		if (gameMap.getAlivePlayers().size() > 1) {
                    		for (Player player: gameMap.getAlivePlayers()) {
                    			if (player != null) {
                    				double newHealth = player.getHealth() - 1;
                    				if (newHealth < 0) {
                    					newHealth = 0;
                    				}
                    				player.setHealth(newHealth);
                    			}
                    		}
                    	}
                	}
                }
                if (gameMap.getMatchState() == MatchState.ENDING) {
                    this.cancel();
                } else {
                	if (SkyWarsReloaded.getCfg().suddenDeathEnabled()) {
                		if (gameMap.getMatchState() != MatchState.SUDDENDEATH) {
                            MatchManager.this.announceTimer(gameMap, "timer.game-timer");
                		}
                	}
                }
                if (gameMap.isThunder()) {
                	if (gameMap.getStrikeCounter() == gameMap.getNextStrike()) {
    					World mapWorld = getWorld(gameMap);
        				int hitPlayer = new Random().nextInt(100);
        				if (hitPlayer <= 10) {
        					int size = gameMap.getAlivePlayers().size();
        					Player player = gameMap.getAlivePlayers().get(new Random().nextInt(size));
        					mapWorld.strikeLightning(player.getLocation());
        				} else {
        					int max;
        					int min;
        					int size = SkyWarsReloaded.getCfg().getMaxMapSize()/2;
        					min = 0 - size;
        					max = 0 + size;
        					int x = Util.get().getRandomNum(max, min);
        					int z = Util.get().getRandomNum(max, min);
        					int y = Util.get().getRandomNum(50, 20);
        					mapWorld.strikeLightningEffect(new Location(mapWorld, x, y, z));
        				}
        				gameMap.setNextStrike(Util.get().getRandomNum(20, 3));
        				gameMap.setStrikeCounter(0);
        			} else {
        				gameMap.setStrikeCounter(gameMap.getStrikeCounter() + 1);
        			}
                }                  
                gameMap.setTimer((gameMap.getTimer() > 0) ? gameMap.getTimer() - 1 : 0);
                gameMap.setDisplayTimer(gameMap.getDisplayTimer() + 1);
                gameMap.updateScoreboard();
            }
        }.runTaskTimer(SkyWarsReloaded.get(), 0L, 20L);
    }
           
    private void won(final GameMap gameMap, final Player win) {             
        if (win != null) {
        	if (debug) {
            	Util.get().logToFile(debugName + ChatColor.YELLOW + win.getName() + "Won the Match");
        	}
        	
        	gameMap.setWinner(win.getDisplayName());
            final PlayerCard pCard = gameMap.getPlayerCard(win);
            pCard.setPlace(1);
            pCard.calculateELO();
            final int eloChange = pCard.getEloChange();
        	
            final PlayerStat winnerData = PlayerStat.getPlayerStats(win.getUniqueId().toString());
            winnerData.setWins(winnerData.getWins() + 1);
    		final int multiplier = Util.get().getMultiplier(win);
    		winnerData.setXp(winnerData.getXp() + (multiplier * SkyWarsReloaded.getCfg().getWinnerXP()));
            winnerData.setElo(pCard.getPostElo());
            if (SkyWarsReloaded.getCfg().economyEnabled()) {
            	VaultUtils.get().give(win, multiplier * SkyWarsReloaded.getCfg().getWinnerEco());
            }
            
            SoundItem sound = SkyWarsReloaded.getLM().getKillSoundByKey(winnerData.getWinSound());
            if (sound != null) {
            	sound.playSound(win.getLocation());
            } 
            final String winner = win.getName();
            final String map = gameMap.getDisplayName();
            if (SkyWarsReloaded.get().isEnabled()) {
                new BukkitRunnable() {
    				@Override
    				public void run() {
    					 	SkyWarsReloaded.get().getServer().broadcastMessage(new Messaging.MessageFormatter()
    			            .setVariable("player1", winner).setVariable("map", map).format("game.broadcast-win"));            
    			        	
                    		if (SkyWarsReloaded.getCfg().titlesEnabled()) {
                    			Util.get().sendTitle(win, 5, 80, 5, new Messaging.MessageFormatter().format("titles.endgame-title-won"), new Messaging.MessageFormatter().format("titles.endgame-subtitle-won"));
                    		}
    					 	if (SkyWarsReloaded.getCfg().fireworksEnabled()) {
    			            	Util.get().fireworks(win, 5, SkyWarsReloaded.getCfg().getFireWorksPer5Tick());
    			        	}
    			    		if (SkyWarsReloaded.getCfg().particlesEnabled()) {
    			    			List<String> particles = new ArrayList<String>();
    			    			particles.add("FIREWORKS_SPARK");
    			        		Util.get().surroundParticles(win, 1, particles, 8, 0);
    			    		}
                    		win.sendMessage(new Messaging.MessageFormatter()
                                .setVariable("score", Util.get().formatScore(eloChange))
                                .setVariable("map", gameMap.getName()).format("game.won"));                            	
                    		}
                }.runTaskLater(SkyWarsReloaded.get(), 20); 
                new BukkitRunnable() {
    				@Override
    				public void run() {
    					Util.get().sendActionBar(win, ChatColor.GREEN + "WIN: " + ChatColor.AQUA + "+" + (multiplier * SkyWarsReloaded.getCfg().getWinnerXP()) + " XP" );
    			        Util.get().doCommands(SkyWarsReloaded.getCfg().getWinCommands(), win);
    					win.setAllowFlight(true);
    					win.setFlying(true);
    				}
                }.runTaskLater(SkyWarsReloaded.get(), 50); 
            }
        }
        if (gameMap.getMatchState() != MatchState.OFFLINE) {
        	gameMap.setMatchState(MatchState.ENDING);
        }
        this.endGame(gameMap);
    }
  
    private void endGame(final GameMap gameMap) {
    	if (debug) {
        	Util.get().logToFile(debugName + ChatColor.YELLOW + "SkyWars Match Has Ended - Wating for teleport");
    	}
        gameMap.update();
        gameMap.setTimer(0);
        if (SkyWarsReloaded.get().isEnabled() && !gameMap.getMatchState().equals(MatchState.OFFLINE)) {
        	for (final Player player: gameMap.getAllPlayers()) {
        		new BukkitRunnable() {
                    public void run() {
                    	PlayerStat toSave = PlayerStat.getPlayerStats(player.getUniqueId().toString());
                    	if (toSave != null) {
                            DataStorage.get().saveStats(toSave);
                    	}
                    }
                }.runTaskAsynchronously(SkyWarsReloaded.get());
        	}
            new BukkitRunnable() {
                public void run() {
                	for (final UUID uuid: gameMap.getSpectators()) {
                		final Player player = SkyWarsReloaded.get().getServer().getPlayer(uuid);
                		if (player != null) {
                        	removeSpectator(gameMap, player);
                		}
                	}
                	gameMap.getSpectators().clear();
                	for (final Player player : gameMap.getAlivePlayers()) {
                        	if (player != null) {
                            	if (PlayerData.getPlayerData(player.getUniqueId()) != null) {
                                	PlayerData.getPlayerData(player.getUniqueId()).setTaggedBy(null);
                            	}
                                MatchManager.this.playerLeave(player, DamageCause.CUSTOM, true, true);
                        	}
                    }
                    gameMap.refreshMap();
                    if (debug) {
                    	Util.get().logToFile(debugName + ChatColor.YELLOW + "SkyWars Match Has Ended - Anena has been refreshed");
                	}
                }
            }.runTaskLater(SkyWarsReloaded.get(), (SkyWarsReloaded.getCfg().getTimeAfterMatch() * 20));
        }
    }
       
    public void removeSpectator(GameMap gameMap, Player player) {
    	if (debug) {
        	Util.get().logToFile(debugName + ChatColor.YELLOW + player.getName() + " has been removed from spectators");
    	}
    	PlayerData pData = PlayerData.getPlayerData(player.getUniqueId());
    	if (pData != null) {
        	pData.restore();
        	PlayerData.getPlayerData().remove(pData);
    	}
    }
    
    public void playerLeave(final Player player, DamageCause dCause, final boolean leftGame, boolean sendMessages) {
    	SkyWarsReloaded.getLM().removePlayer(player.getUniqueId());
    	
    	final GameMap gameMap = this.getPlayerMap(player);
        if (gameMap == null) {
            return;
        }
        if (gameMap.getMatchState() != MatchState.WAITINGSTART && gameMap.getMatchState() != MatchState.ENDING) {
            gameMap.getDead().add(player.getUniqueId());
            gameMap.getAlivePlayers().remove(player.getUniqueId());
            PlayerCard pCard = gameMap.getPlayerCard(player);
            pCard.setPlace(gameMap.getPlayerCount() + 1 - gameMap.getDead().size());
            pCard.calculateELO();
            final int eloChange = pCard.getEloChange();
            player.setNoDamageTicks(1);
            final PlayerData playerData = PlayerData.getPlayerData(player.getUniqueId());
            if (playerData != null) {
                if (leftGame) {
                	if (player != null) {
                		if (playerData.getTaggedBy() != null && playerData.getTaggedBy().getPlayer() != null && playerData.getTaggedBy().getPlayer() != player && System.currentTimeMillis() - playerData.getTaggedBy().getTime() < 10000) {
                        	if (sendMessages) {
                        		this.message(gameMap, new Messaging.MessageFormatter()
                				.withPrefix()
                				.setVariable("player", player.getName())
                				.setVariable("killer", playerData.getTaggedBy().getPlayer().getName())
                				.format("game.death.quit-while-tagged"));
                        		updatePlayerData(player, pCard, playerData);
                        	}
                		} else {
                			if (sendMessages) {
                    			if (gameMap.getMatchState() != MatchState.ENDING) {
                            		this.message(gameMap, new Messaging.MessageFormatter().setVariable("player", player.getName()).format("game.left-the-game"));
                            		PlayerStat loserData = PlayerStat.getPlayerStats(player.getUniqueId().toString());
                            		loserData.setElo(pCard.getPostElo());
                        		}
                			}
                		}
                	}
                    playerData.restore();
                    PlayerData.getPlayerData().remove(playerData);
                } else {
                	if (player != null) {
                    	if (debug) {
                    		Util.get().logToFile(debugName + ChatColor.YELLOW + player.getName() + " died. Respawning.");
                    	}
                    	if (sendMessages) {
                        	if (playerData.getTaggedBy() != null && System.currentTimeMillis() - playerData.getTaggedBy().getTime() < 10000) {
                            	this.message(gameMap, Util.get().getDeathMessage(dCause, true, player, playerData.getTaggedBy().getPlayer()));
                            	updatePlayerData(player, pCard, playerData);
                        	} else {
                        		this.message(gameMap, Util.get().getDeathMessage(dCause, false, player, player));
                        		PlayerStat loserData = PlayerStat.getPlayerStats(player.getUniqueId().toString());
                        		loserData.setDeaths(loserData.getDeaths() + 1);
                        		loserData.setElo(pCard.getPostElo());
                        	}
                    	}
                    	if (SkyWarsReloaded.get().isEnabled()) {
                            new BukkitRunnable() {
                                public void run() {
                                	Util.get().respawnPlayer(player);
                                }
                            }.runTaskLater(SkyWarsReloaded.get(), 3L);
                           
                            new BukkitRunnable() {
                            	public void run() {
                            		player.sendMessage(new Messaging.MessageFormatter()
                                    .setVariable("score", Util.get().formatScore(eloChange))
                                    .setVariable("map", gameMap.getName()).format("game.lost")); 
                                }
                            }.runTaskLater(SkyWarsReloaded.get(), 10L);
                    	}
                	}
                }         
                if (sendMessages) {
                	if (gameMap.getMatchState() != MatchState.ENDING || gameMap.getMatchState() != MatchState.WAITINGSTART) {
                     	PlayerStat loserData = PlayerStat.getPlayerStats(player.getUniqueId().toString());
                        loserData.setLosts(loserData.getLosses() + 1);
                    }
                	if (gameMap.getAlivePlayers().size() <= 1) {
                		if (gameMap.getAlivePlayers().size() >= 1) {
                			this.won(gameMap, gameMap.getAlivePlayers().get(0));
                		} else {
                			this.won(gameMap, null);
                		}
                		
                	}
                }
            }
            for (UUID uuid: gameMap.getSpectators()) {
            	Player spec = SkyWarsReloaded.get().getServer().getPlayer(uuid);
            	prepareSpectateInv(spec, gameMap);
            }
        } else {
        	gameMap.removePlayer(player);
			
	        if (SkyWarsReloaded.getCfg().titlesEnabled()) {
	        	for (final Player p : gameMap.getAlivePlayers()) {
	        		if (!p.equals(player)) {
	                    Util.get().sendTitle(p, 2, 20, 2, "", 
	                    		new Messaging.MessageFormatter().setVariable("player", player.getDisplayName()).format("game.left-the-game"));
	    			}
	        	}
	        }
	        message(gameMap, new Messaging.MessageFormatter().setVariable("player", player.getDisplayName()).format("game.left-the-game"));
	        
	        
        	for (final Player p : gameMap.getAlivePlayers()) {
        		Util.get().playSound(p, p.getLocation(), SkyWarsReloaded.getCfg().getLeaveSound(), 1, 1);
        	}
			
            final PlayerData playerData = PlayerData.getPlayerData(player.getUniqueId());
            if (playerData != null) {
                playerData.restore();
                PlayerData.getPlayerData().remove(playerData);
            }
        }
    	if (debug) {
        	Util.get().logToFile(debugName + ChatColor.YELLOW + player.getName() + " Has Left The SkyWars Match on map" + gameMap.getName());
    	}
    }
    
    private void updatePlayerData(Player player, PlayerCard pCard, PlayerData playerData) {
    	PlayerStat loserData = PlayerStat.getPlayerStats(player.getUniqueId().toString());
		loserData.setDeaths(loserData.getDeaths() + 1);
		loserData.setElo(pCard.getPostElo());
		Player killer = playerData.getTaggedBy().getPlayer();
		PlayerStat killerData = PlayerStat.getPlayerStats(killer);
		int multiplier = Util.get().getMultiplier(killer);
		killerData.setKills(killerData.getKills() + 1);
		killerData.setXp(killerData.getXp() + (multiplier * SkyWarsReloaded.getCfg().getKillerXP()));
		if (SkyWarsReloaded.getCfg().economyEnabled()) {
			VaultUtils.get().give(killer, multiplier * SkyWarsReloaded.getCfg().getKillerEco());
		}
		Util.get().sendActionBar(killer, ChatColor.GREEN + "KILL: " + ChatColor.AQUA + "+" + (multiplier * SkyWarsReloaded.getCfg().getKillerXP()) + " XP" );
		Util.get().doCommands(SkyWarsReloaded.getCfg().getKillCommands(), killer);
		
		SoundItem sound = SkyWarsReloaded.getLM().getKillSoundByKey(killerData.getKillSound());
	    if (sound != null) {
	    	sound.playSound(killer.getLocation());
	    }
    }

	public GameMap getPlayerMap(final Player v0) {
    	if (v0 != null) {
            for (final GameMap gameMap : GameMap.getMaps()) {
            	for (final Player player : gameMap.getAlivePlayers()) {
            		if (v0.equals(player)) {
            			return gameMap;	
            		}
            	}
            }
    	}
        return null;
    }
	
	public GameMap getDeadPlayerMap(final Player v0) {
		if (v0 != null) {
            for (final GameMap gameMap : GameMap.getMaps()) {
            	for (final UUID uuid : gameMap.getDead()) {
            		if (v0.getUniqueId().equals(uuid)) {
            			return gameMap;	
            		}
            	}
            }
    	}
		return null;
	}
    
    public GameMap getSpectatorMap(final Player player) {
    	UUID uuid = null;
    	if (player != null) {
        	uuid = player.getUniqueId();
    	}

    	if (uuid != null) {
            for (final GameMap gameMap : GameMap.getMaps()) {
            	for (final UUID id : gameMap.getSpectators()) {
            		if (uuid.equals(id)) {
            				return gameMap;	
            		}
                }
            }
    	}
		return null;
    }
    
    public boolean isSpectating(final Player player) {
    	return this.getSpectatorMap(player) != null;
    }
     
    public int getGameTime() {
    	return gameTime;
    }
    
    public int getWaitTime() {
    	return waitTime;
    }
    
    public void setGameTime(int gameTime) {
    	this.gameTime = gameTime;
    }
    
    public void setWaitTime(int waitTime) {
    	this.waitTime = waitTime;
    }
    
	public void addSpectator(final GameMap gameMap, final Player player) {
        if (player != null) {
        	World world = getWorld(gameMap);
			Location spectateSpawn = new Location(world, 0, 95, 0);
            player.teleport(spectateSpawn, TeleportCause.END_PORTAL);
            player.setAllowFlight(true);
            player.setFlying(true);
            
            new BukkitRunnable() {
				@Override
				public void run() {
					PlayerData pd = PlayerData.getPlayerData(player.getUniqueId());
					if (pd == null) {
						PlayerData.getPlayerData().add(new PlayerData(player));
					}
		            Util.get().clear(player);
		            player.getInventory().setBoots(new ItemStack(Material.AIR, 1));
		            player.getInventory().setChestplate(new ItemStack(Material.AIR, 1));
		            player.getInventory().setHelmet(new ItemStack(Material.AIR, 1));
		            player.getInventory().setLeggings(new ItemStack(Material.AIR, 1));
		            player.setFoodLevel(20);
		            player.setHealth(20.0);
		            player.setExp(0.0f);
		            player.setLevel(0);
		            player.setGameMode(GameMode.SPECTATOR);
		            player.setScoreboard(gameMap.getScoreboard());
		            
		            prepareSpectateInv(player, gameMap);
		    		
		    		ItemStack exitItem = new ItemStack(Material.IRON_DOOR, 1);
		    		ItemMeta exit = exitItem.getItemMeta();
		    		exit.setDisplayName(new Messaging.MessageFormatter().format("spectate.exititemname"));
		    		List<String> lore = new ArrayList<String>();
		    		lore.add(new Messaging.MessageFormatter().format("spectate.exititemlore"));
		    		exit.setLore(lore);
		    		exitItem.setItemMeta(exit);
		            player.getInventory().setItem(8, exitItem);
		            player.sendMessage(new Messaging.MessageFormatter().format("spectate.startmessage"));
		            player.sendMessage(new Messaging.MessageFormatter().format("spectate.startmessage2"));
		        	if (debug) {
		            	Util.get().logToFile(debugName + ChatColor.YELLOW + player.getName() + " has been added to spectators");
		        	}
				}

            }.runTaskLater(SkyWarsReloaded.get(), 3);
            gameMap.getSpectators().add(player.getUniqueId());
        }
	}
	
	@SuppressWarnings("deprecation")
	private void prepareSpectateInv(Player player, GameMap gameMap) {
		int slot = 9;
        for (Player player1: gameMap.getAlivePlayers()) {
            if (player1 != null) {
	            ItemStack playerhead1 = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
	    		SkullMeta meta1 = (SkullMeta)playerhead1.getItemMeta();
	    		meta1.setOwner(player1.getName());
	    		meta1.setDisplayName(ChatColor.YELLOW + player1.getName());
	    		List<String> lore = new ArrayList<String>();
	    		lore.add(new Messaging.MessageFormatter().setVariable("player", player1.getName()).format("spectate.playeritemlore"));
	    		meta1.setLore(lore);
	    		playerhead1.setItemMeta(meta1);
	    		player.getInventory().setItem(slot, playerhead1);
	    		slot++;
            }
        }
        player.updateInventory();
	}
	
	private void announceTimer(final GameMap gameMap, String message) {
        final int v1 = gameMap.getTimer();
        String time;
        if (v1 % 60 == 0) {
            time = v1 / 60 + " " + ((v1 > 60) ? "minutes" : "minute");
        }
        else {
            if (v1 >= 60 || (v1 % 10 != 0 && v1 >= 10) || v1 <= 0) {
                return;
            }
            time = v1 + " " + ((v1 > 1) ? "seconds" : "second");
        }
        this.message(gameMap, new Messaging.MessageFormatter().setVariable("time", time).format(message));
    }
}
