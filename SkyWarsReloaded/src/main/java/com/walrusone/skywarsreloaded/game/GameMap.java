package com.walrusone.skywarsreloaded.game;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.managers.PlayerStat;
import com.walrusone.skywarsreloaded.managers.WorldManager;
import com.walrusone.skywarsreloaded.menus.ArenaMenu;
import com.walrusone.skywarsreloaded.menus.ArenasMenu;
import com.walrusone.skywarsreloaded.menus.gameoptions.ChestOption;
import com.walrusone.skywarsreloaded.menus.gameoptions.GameOption;
import com.walrusone.skywarsreloaded.menus.gameoptions.HealthOption;
import com.walrusone.skywarsreloaded.menus.gameoptions.KitVoteOption;
import com.walrusone.skywarsreloaded.menus.gameoptions.ModifierOption;
import com.walrusone.skywarsreloaded.menus.gameoptions.TimeOption;
import com.walrusone.skywarsreloaded.menus.gameoptions.WeatherOption;
import com.walrusone.skywarsreloaded.menus.gameoptions.objects.EmptyChest;
import com.walrusone.skywarsreloaded.menus.gameoptions.objects.GameKit;
import com.walrusone.skywarsreloaded.menus.playeroptions.GlassColorOption;
import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.utilities.Messaging;
import com.walrusone.skywarsreloaded.utilities.Party;
import com.walrusone.skywarsreloaded.utilities.Util;

import java.io.File;
import java.io.IOException;

import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;

public class GameMap {
 
	static {
		new ArenasMenu();
	}
	private static ArrayList<GameMap> arenas;
	private static Map<String, MapData> mapData = new HashMap<String, MapData>();

	private boolean forceStart;
	private boolean allowFallDamage;
	private boolean allowRegen;
    private boolean thunder;
    private String winner = "";
    private int strikeCounter;
    private int nextStrike;
    private MatchState matchState;
    private ArrayList<PlayerCard> playerCards;
    private ArrayList<UUID> dead = new ArrayList<UUID>();
    private ArrayList<UUID> spectators = new ArrayList<UUID>();
    private String name;
    private int timer;
    private int displayTimer = 0;
    private int restartTimer = -1;
    private int minPlayers;
    private GameKit kit;
    private String currentTime;
    private String currentHealth;
    private String currentChest;
    private String currentWeather;
    private String currentModifier;
    private KitVoteOption kitVoteOption;
    private ChestOption chestOption;
    private HealthOption healthOption;
    private TimeOption timeOption;
    private WeatherOption weatherOption;
    private ModifierOption modifierOption;
	private Map<Integer, EmptyChest> chests = Maps.newHashMap();
	private Map<Integer, EmptyChest> doubleChests = Maps.newHashMap();
	private int mapUseCount;
	private String displayName;
	private String designedBy;
	private ArrayList<SWRSign> signs;
	private Scoreboard scoreboard;
	private Objective objective;
	private MapData mp;
	private boolean registered;
	private String arenakey;
		
    public GameMap(final String name, boolean forceRegister, boolean startup) {
    	playerCards = new ArrayList<PlayerCard>();
    	mp = mapData.get(name);
        if (mp == null) {
        	mapData.put(name, new MapData(name, name, ((int) playerCards.size()/2) >= 2 ? (int) playerCards.size()/2 : 2 , "", null, forceRegister));
         	saveMapData();
         	mp = mapData.get(name);
        }
         
        this.minPlayers = mp.getMinPlayers() >= 2 ? mp.getMinPlayers() : 2;
        this.displayName = mp.getDisplayName();
        this.designedBy = mp.getCreator();
        signs = new ArrayList<SWRSign>();
		this.name = name.toLowerCase();
		
        this.thunder = false;
        allowRegen = true;
        
        if(startup) {
        	this.registered = mp.isRegistered();
        } else {
        	this.registered = forceRegister;
        }
        this.matchState = MatchState.OFFLINE;
        timer = SkyWarsReloaded.getCfg().getWaitTimer();
        mapUseCount = 0;
        
        arenakey = name + "menu";
        new ArenaMenu(arenakey, this);
        
        if (mp.getSigns() != null) {
        	for (String sign: mp.getSigns()) {
        		signs.add(new SWRSign(this.getName(), Util.get().stringToLocation(sign)));
        	}
        }
                   
        if (registered) {
        	attemptRegistration();
        } 
    }
   
	public void update() {
		updateArenasManager();
		this.updateArenaManager();
        this.updateSigns();
        this.sendBungeeUpdate();
        if (this.isRegistered()) {
            this.updateScoreboard();
        }
        if (SkyWarsReloaded.getIC().has("joinmenu")) {
            SkyWarsReloaded.getIC().getMenu("joinmenu").update();
        }
	}
   
	/*Player Handling Methods*/
	
	public boolean addPlayer(final Player player) {
		if (Util.get().isBusy(player.getUniqueId())) {
			return false;
		}
		
		Collections.shuffle(playerCards);
		for (PlayerCard pCard: playerCards) {
			if (pCard.getPlayer() == null && pCard.getSpawn() != null) {
    			pCard.setPlayer(player);
    			pCard.setPreElo(PlayerStat.getPlayerStats(player.getUniqueId()).getElo());
   			
    			MatchManager.get().teleportToArena(this, pCard);
    			if (SkyWarsReloaded.getCfg().kitVotingEnabled()) {
    				kitVoteOption.updateKitVotes();
    			}
    	        timer = SkyWarsReloaded.getCfg().getWaitTimer();
    	        this.update();
    			return true;
    		}  		
		}
    	this.update();
    	updateScoreboard();
    	return false;
    }
	
	public boolean addParty(final Party party) {
		ArrayList<Player> players = new ArrayList<Player>();
		for (UUID uuid: party.getMembers()) {
			Player player = Bukkit.getPlayer(uuid);
			if (Util.get().isBusy(uuid)) {
				party.sendPartyMessage(new Messaging.MessageFormatter().setVariable("player", player.getName()).format("party.memberbusy"));
			} else {
				PlayerStat ps = PlayerStat.getPlayerStats(uuid);
				Collections.shuffle(playerCards);
				for (PlayerCard pCard: playerCards) {
		    		if (pCard.getPlayer() == null && pCard.getSpawn() != null) {
		    			pCard.setPlayer(player);
		    			pCard.setPreElo(ps.getElo());
		    			players.add(player);
		    			break;
		    		}
				}
			}
		}
		
		if (players.size() == party.getSize()) {
			for (Player player: players) {
				PlayerCard pCard = this.getPlayerCard(player);
				MatchManager.get().teleportToArena(this, pCard);
				if (SkyWarsReloaded.getCfg().kitVotingEnabled()) {
			        kitVoteOption.updateKitVotes();
				}
		        timer = SkyWarsReloaded.getCfg().getWaitTimer();
		        this.update();
			}
			return true;
		} else {
			for (Player player: players) {
				PlayerCard pCard = this.getPlayerCard(player);
				pCard.reset();
			}
		}
    	this.update();
    	updateScoreboard();
    	return false;
	}

	public boolean removePlayer(final UUID uuid) {
		for (PlayerCard pCard: playerCards) {
    		if (uuid != null) {
    			if (pCard.getUUID() != null) {
    				if(pCard.getUUID().equals(uuid)) {
            			pCard.reset();
            			this.update();
            			return true;
            		}
    			}
    		}
    	}
    	this.update();
    	return false;
    }
 
    public ArrayList<Player> getAlivePlayers() {
    	ArrayList<Player> alivePlayers = new ArrayList<Player>();
    	for (PlayerCard pCard: playerCards) {
    		if (pCard.getPlayer() != null) {
        		if (!this.getDead().contains(pCard.getPlayer().getUniqueId())) {
            		alivePlayers.add(pCard.getPlayer());
        		}
    		}
    	}
    	return alivePlayers;
    }
    
    public ArrayList<Player> getAllPlayers() {
    	ArrayList<Player> allPlayers = new ArrayList<Player>();
    	for (PlayerCard pCard: playerCards) {
    		if (pCard.getPlayer() != null) {
            	allPlayers.add(pCard.getPlayer());
    		}
    	}
    	return allPlayers;
    }
    
    public boolean canAddPlayer() {
    	int playerCount = getPlayerCount();
        return (this.matchState == MatchState.WAITINGSTART && playerCount < playerCards.size() && this.registered);
    }
    
    public boolean canAddParty(Party party) {
    	int playerCount = getPlayerCount();
    	return (this.matchState == MatchState.WAITINGSTART && playerCount + party.getSize()-1 < playerCards.size() && this.registered);
    }
    
	/*Map Handling Methods*/
	
    static {
        GameMap.arenas = new ArrayList<GameMap>();
    }
	
	public static GameMap getMap(final String mapName) {
    	shuffle();
    	for (final GameMap map : GameMap.arenas) {
            if (map.name.equalsIgnoreCase(ChatColor.stripColor(mapName))) {
                return map;
            }
        }
        return null;
    }
	
	public static boolean addMap(String name, boolean forceRegister, boolean startup) {
		GameMap gMap = new GameMap(name, forceRegister, startup);
		arenas.add(new GameMap(name, forceRegister, startup));
		return gMap.isRegistered();
	}
	
	public void removeMap() {
		unregister();
		mapData.remove(name);
		File dataDirectory = new File (SkyWarsReloaded.get().getDataFolder(), "maps");
		File target = new File (dataDirectory, name);
		SkyWarsReloaded.getWM().deleteWorld(target);
		arenas.remove(this);
		saveMapData();
	}
        
    public static void loadMaps() {
    	loadMapData();
    	arenas.clear();
    	File dataDirectory = SkyWarsReloaded.get().getDataFolder();
		File maps = new File (dataDirectory, "maps");
		if (maps.exists() && maps.isDirectory()) {
			for (File map : maps.listFiles()) {
				if (map.isDirectory()) {
					addMap(map.getName().toLowerCase(), true, true);
				} 
			}
		} else {
			SkyWarsReloaded.get().getLogger().info("Maps directory is missing or no Maps were found!");
		} 
		saveMapData();
    }
    
	public static World createNewMap(String mapName) {
    	World newWorld = SkyWarsReloaded.getWM().createEmptyWorld(mapName);
		if (newWorld == null) {
			return null;
		}
		newWorld.save();
		SkyWarsReloaded.getWM().unloadWorld(mapName);
		File dataDirectory = new File (SkyWarsReloaded.get().getDataFolder(), "maps");
		File target = new File (dataDirectory, mapName);
		SkyWarsReloaded.getWM().deleteWorld(target);
		File source = new File (SkyWarsReloaded.get().getServer().getWorldContainer().getAbsolutePath(), mapName);
		SkyWarsReloaded.getWM().copyWorld(source, target);
		SkyWarsReloaded.getWM().loadWorld(mapName);
		addMap(mapName, false, false);
		return SkyWarsReloaded.get().getServer().getWorld(mapName);
	}
	
	public void unregister() {
		this.registered = false;
		saveMapData();
		stopGameInProgress();
	}
	
	public void stopGameInProgress() {
		this.matchState = MatchState.OFFLINE;
		for (final UUID uuid: this.getSpectators()) {
    		final Player player = SkyWarsReloaded.get().getServer().getPlayer(uuid);
    		if (player != null) {
    			MatchManager.get().removeSpectator(this, player);
    		}
    	}
        for (final Player player : this.getAlivePlayers()) {
        	if (player != null) {
                MatchManager.get().playerLeave(player, DamageCause.CUSTOM, true, false);
        	}
        }
        SkyWarsReloaded.getWM().deleteWorld(this.getName() + "_" + this.getMapCount());
	}
	
	public boolean attemptRegistration() {
		if (!this.matchState.equals(MatchState.OFFLINE)) {
			stopGameInProgress();
		}
    	SkyWarsReloaded.get().getLogger().info("Attempting to register Map: " + name);
        boolean loaded = loadWorldForScanning(name);
        if (loaded) {
        	ChunkIterator();
 			SkyWarsReloaded.getWM().deleteWorld(name);
            if (playerCards.size() >= 2 && registered) {
                if (SkyWarsReloaded.getCfg().kitVotingEnabled()) {
                	kitVoteOption = new KitVoteOption(this, name + "kitVote");
                }
                chestOption = new ChestOption(this, name + "chest");
                healthOption = new HealthOption(this, name + "health");
                timeOption =  new TimeOption(this, name + "time");
                weatherOption = new WeatherOption(this, name + "weather");
                modifierOption = new ModifierOption(this, name + "modifier");
               
                refreshMap();
                SkyWarsReloaded.get().getLogger().info("Registered Map " + name + "!");
                return true;
            } else {
            	this.registered = false;
             	saveMapData();
             	SkyWarsReloaded.get().getLogger().info("Could Not Register Map: " + name + " - Map must have at least 2 Spawn Points");
             	return false;
            }
        } else {
        	return false;
        }  
	}
    
    public static boolean loadWorldForScanning(String name) {
        	File dataDirectory = SkyWarsReloaded.get().getDataFolder();
    		File maps = new File (dataDirectory, "maps");
    		
    			String root = SkyWarsReloaded.get().getServer().getWorldContainer().getAbsolutePath();
    			File rootDirectory = new File(root);
    			WorldManager wm = SkyWarsReloaded.getWM();
    			File source = new File(maps, name);
    			File target = new File(rootDirectory, name);
    			wm.copyWorld(source, target);
    			boolean mapExists = false;
    			if(target.isDirectory()) {			 
    				if(target.list().length > 0) {
    		 			mapExists = true;
    				}	 
    			}
    			if (mapExists) {
    				SkyWarsReloaded.getWM().deleteWorld(name);
    			}
    			
    			wm.copyWorld(source, target);
    			
    			boolean loaded = SkyWarsReloaded.getWM().loadWorld(name);
    			if(!loaded) {
    				SkyWarsReloaded.get().getLogger().info("Could Not Load Map: " + name);
    			}
    			return loaded;
	}

	public static ArrayList<GameMap> getMaps() {
		return new ArrayList<GameMap>(arenas);
	}
	
	public static ArrayList<GameMap> getSortedMaps() {
		ArrayList<GameMap> sorted = new ArrayList<GameMap>();
		for (GameMap gMap: arenas) {
			if (gMap.isRegistered()) {
				sorted.add(gMap);
			}
		}
		Collections.sort(sorted, new GameMapComparator());
		return sorted;
	}
	
	public static ArrayList<GameMap> getSortedArenas() {
		ArrayList<GameMap> sorted = new ArrayList<GameMap>();
		for (GameMap gMap: arenas) {
			sorted.add(gMap);
		}
		Collections.sort(sorted, new GameMapComparator());
		return sorted;
	}
	
	public static boolean mapRegistered(String name) {
		for (GameMap gMap: arenas) {
			if (gMap.getName().equalsIgnoreCase(name)) {
				return gMap.isRegistered();
			}
		}
		return false;
	}
	   
	public boolean loadMap() {
			WorldManager wm = SkyWarsReloaded.getWM();
			String mapName = name + "_" + mapUseCount;
			boolean mapExists = false;
	    	File dataDirectory = SkyWarsReloaded.get().getDataFolder();
			File maps = new File (dataDirectory, "maps");
			File source = new File(maps, name);
			String root = SkyWarsReloaded.get().getServer().getWorldContainer().getAbsolutePath();
			File rootDirectory = new File(root);
			File target = new File(rootDirectory, mapName);
			if(target.isDirectory()) {			 
				if(target.list().length > 0) {
		 			mapExists = true;
				}	 
			}
			if (mapExists) {
				SkyWarsReloaded.getWM().deleteWorld(mapName);
			}
			
			wm.copyWorld(source, target);
			
			boolean loaded = SkyWarsReloaded.getWM().loadWorld(mapName);
			
			if (loaded) {
				World world = SkyWarsReloaded.get().getServer().getWorld(mapName);
			    world.setAutoSave(false);
			    world.setThundering(false);
			    world.setStorm(false);
			    world.setDifficulty(Difficulty.NORMAL);
			    world.setSpawnLocation(2000, 0, 2000);
			    world.setTicksPerAnimalSpawns(1);
			    world.setTicksPerMonsterSpawns(1);
		        world.setGameRuleValue("doMobSpawning", "false");
		        world.setGameRuleValue("mobGriefing", "false");
		        world.setGameRuleValue("doFireTick", "false");
		        world.setGameRuleValue("showDeathMessages", "false");
		        createSpawnPlatforms(world);
			}
			return loaded;
	}
	
	public void ChunkIterator() {
		World chunkWorld;
		chunkWorld = SkyWarsReloaded.get().getServer().getWorld(name);
		int mapSize = SkyWarsReloaded.getCfg().getMaxMapSize();
		int max1 = mapSize/2;
		int min1 = -mapSize/2;
		Block min = chunkWorld.getBlockAt(min1, 0, min1);
		Block max = chunkWorld.getBlockAt(max1, 0, max1);
		Chunk cMin = min.getChunk();
		Chunk cMax = max.getChunk();
		int countChests = 0;
		int countDChests = 0;
		
		for(int cx = cMin.getX(); cx < cMax.getX(); cx++) {
			for(int cz = cMin.getZ(); cz < cMax.getZ(); cz++) {
		           Chunk currentChunk = chunkWorld.getChunkAt(cx, cz);
		           currentChunk.load(true);

		           for(BlockState te : currentChunk.getTileEntities()) {
		               	if(te instanceof Beacon){
			                  Beacon beacon = (Beacon) te;
			                  Block block = beacon.getBlock().getRelative(0, -1, 0);
			                  if(!block.getType().equals(Material.GOLD_BLOCK) && !block.getType().equals(Material.IRON_BLOCK) 
			                		  && !block.getType().equals(Material.DIAMOND_BLOCK)&& !block.getType().equals(Material.EMERALD_BLOCK)) {
				                  Location loc = beacon.getLocation();
				                  playerCards.add(new PlayerCard(loc, null, -1, this));
			                  }
			            } else if (te instanceof Chest) {
				                  Chest chest = (Chest) te;
				                  InventoryHolder ih = chest.getInventory().getHolder();
				                  if (ih instanceof DoubleChest){
				                      int x = chest.getX();
					                  int z = chest.getZ();
					                  int y = chest.getY();	
					                  doubleChests.put(countDChests, new EmptyChest(x, y, z));
						              countDChests++;
					              } else {
					                  int x = chest.getX();
					                  int z = chest.getZ();
					                  int y = chest.getY();
				                	  chests.put(countChests, new EmptyChest(x, y, z));
					                  countChests++;
				                  }
			            } 
		           }
		        }
	     }
		
	}
	
	public void refreshMap() {
		for (PlayerCard pCard: playerCards) {
			pCard.reset();
		}
		thunder = false;
		forceStart = false;
		allowRegen = true;
        dead.clear();
        kit = null;
        setDisplayTimer(0);
        restartTimer = -1;
        winner = "";
		if (SkyWarsReloaded.getCfg().kitVotingEnabled()) {
			kitVoteOption.restore();
		}
		healthOption.restore();
        chestOption.restore();
        timeOption.restore();
        weatherOption.restore();
        modifierOption.restore();
        SkyWarsReloaded.getWM().deleteWorld(name + "_" + mapUseCount);
        mapUseCount++;
        this.loadMap();
        final GameMap gMap = this;
        if (SkyWarsReloaded.get().isEnabled()) {
            new BukkitRunnable() {
				@Override
				public void run() {
					matchState = MatchState.WAITINGSTART;
			        getScoreBoard();
			        MatchManager.get().start(gMap);
			        update();
				}
            }.runTaskLater(SkyWarsReloaded.get(), 40);
        }  
	}
	
	/*Inventories*/
	
	public void updateArenaManager() {
		if (SkyWarsReloaded.getIC().has(arenakey)) {
			SkyWarsReloaded.getIC().getMenu(arenakey).update();
		}
	}
	
	public static void openArenasManager(Player player) {
		if (player.hasPermission("sw.arenas")) {
			SkyWarsReloaded.getIC().show(player, "arenasmenu");
		}
	}
	
	public static void updateArenasManager() {
		if (SkyWarsReloaded.getIC().has("arenasmenu")) {
			SkyWarsReloaded.getIC().getMenu("arenasmenu").update();
		}
	}
	
	public void setKitVote(Player player, GameKit kit2) {
		for (PlayerCard pCard: playerCards) {
			if (pCard.getPlayer() != null && pCard.getPlayer().equals(player)) {
				pCard.setKitVote(kit2);
				return;
			}
		}
	}
   
    public GameKit getSelectedKit(Player player) {
    	for (PlayerCard pCard: playerCards) {
    		if (pCard != null) {
    			if (pCard.getPlayer() != null && pCard.getPlayer().equals(player)) {
    				return pCard.getKitVote();
    			}
    		}
    	}
    	return null;
    }
    
	/*Scoreboard Methods*/
	
	public void getScoreBoard() {
		if (scoreboard != null) {
            resetScoreboard();
        }
		ScoreboardManager manager = SkyWarsReloaded.get().getServer().getScoreboardManager();
		scoreboard = manager.getNewScoreboard();
        objective = scoreboard.registerNewObjective("info", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		updateScoreboard();
	}
	
	public void updateScoreboard() {
        if (objective != null) {
            objective.unregister();
        }
        if (scoreboard != null) {
        	objective = scoreboard.registerNewObjective("info", "dummy");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            String sb = "";
            if (matchState.equals(MatchState.WAITINGSTART)) {
            	sb = "scoreboards.waitboard.line";
            } else if (matchState.equals(MatchState.PLAYING) || matchState.equals(MatchState.SUDDENDEATH)) {
            	sb = "scoreboards.playboard.line";
            } else if (matchState.equals(MatchState.ENDING)) {
            	sb = "scoreboards.endboard.line";
            	if (restartTimer == -1) {
            		startRestartTimer();
            	}
            }
            
            for (int i = 1; i < 17; i++) {
            	if (i == 1) {
        	        String title = getScoreboardLine(sb + i);
        	        if (title.length() > 32) {
        	        	title = title.substring(0, 31);
        	        }
        	        objective.setDisplayName(title);
        		} else {
        			String s = "";
        			if (getScoreboardLine(sb + i).length() == 0) {
        				for (int j = 0; j < i; j++) {
        					s = s + " ";
        				}
        			} else {
        				s = getScoreboardLine(sb + i);
        			}
        			if (!s.equalsIgnoreCase("remove")) {
        				if (s.length() > 40) {
        	    	        s = s.substring(0, 39);
        				}
            			Score score = objective.getScore(s);
        				score.setScore(17-i);
        			}
        		}
            }	
        }   
	}
	
	private void startRestartTimer() {
		restartTimer = SkyWarsReloaded.getCfg().getTimeAfterMatch();
		if (SkyWarsReloaded.get().isEnabled()) {
			new BukkitRunnable() {
				@Override
				public void run() {
					restartTimer--;
					if (restartTimer == 0) {
						this.cancel();
					}
					updateScoreboard();
				}
			}.runTaskTimer(SkyWarsReloaded.get(), 0, 20);
		}
	}
	

	
	private String getScoreboardLine(String lineNum) {
		return new Messaging.MessageFormatter()
				.setVariable("mapname", displayName)
				.setVariable("time", "" + Util.get().getFormattedTime(displayTimer))
				.setVariable("players", "" + getAlivePlayers().size())
				.setVariable("maxplayers", "" + playerCards.size())
				.setVariable("winner", winner)
				.setVariable("restarttime", "" + restartTimer)
				.setVariable("chestvote", ChatColor.stripColor(currentChest))
				.setVariable("timevote", ChatColor.stripColor(currentTime))
				.setVariable("healthvote", ChatColor.stripColor(currentHealth))
				.setVariable("weathervote", ChatColor.stripColor(currentWeather))
				.setVariable("modifiervote", ChatColor.stripColor(currentModifier))
				.format(lineNum);
	}

    private void resetScoreboard() {
        if (objective != null) {
            objective.unregister();
        }
        
        if (scoreboard != null) {
            scoreboard = null;
        }
    }

	public Scoreboard getScoreboard() {
		return scoreboard;
	}

	
	
	/*Glass Color Handling Methods*/
	
	public void createSpawnPlatforms(World world) {
    	for(PlayerCard pCard: playerCards) {
            int x = pCard.getSpawn().getBlockX();
            int y = pCard.getSpawn().getBlockY();
            int z = pCard.getSpawn().getBlockZ();

            world.getBlockAt(x, y, z).setType(Material.GLASS);
            world.getBlockAt(x, y + 1, z + 1).setType(Material.GLASS);
            world.getBlockAt(x, y + 1, z - 1).setType(Material.GLASS);
            world.getBlockAt(x + 1, y + 1, z).setType(Material.GLASS);
            world.getBlockAt(x - 1, y + 1, z).setType(Material.GLASS);
            world.getBlockAt(x, y + 2, z + 1).setType(Material.GLASS);
            world.getBlockAt(x, y + 2, z - 1).setType(Material.GLASS);
            world.getBlockAt(x + 1, y + 2, z).setType(Material.GLASS);
            world.getBlockAt(x - 1, y + 2, z).setType(Material.GLASS);
            world.getBlockAt(x, y + 3, z + 1).setType(Material.GLASS);
            world.getBlockAt(x, y + 3, z - 1).setType(Material.GLASS);
            world.getBlockAt(x + 1, y + 3, z).setType(Material.GLASS);
            world.getBlockAt(x - 1, y + 3, z).setType(Material.GLASS);
            world.getBlockAt(x, y + 4, z).setType(Material.GLASS);
    	}
    }
	
	@SuppressWarnings("deprecation")
	public void setGlassColor(Player player, String color) {
		if (matchState == MatchState.WAITINGSTART) {
			PlayerCard pCard = null;
			for (PlayerCard playerCard: playerCards) {
				if (playerCard.getPlayer() != null && playerCard.getPlayer().equals(player)) {
					pCard = playerCard;
					break;
				}
			}
			
			if (pCard != null) {
				World mapWorld;
				mapWorld = SkyWarsReloaded.get().getServer().getWorld(this.getName() + "_" + this.getMapCount());
	            int x = pCard.getSpawn().getBlockX();
	            int y = pCard.getSpawn().getBlockY();
	            int z = pCard.getSpawn().getBlockZ();
	            
				byte cByte = Util.get().getByteFromColor(color.toLowerCase());
				if (cByte <= -1) {
					Material material = Material.GLASS;
					GlassColorOption gColor = (GlassColorOption) GlassColorOption.getPlayerOptionByKey(color.toLowerCase());
					if (gColor != null) {
						material = gColor.getItem().getType();
					}
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
				} else {
		            mapWorld.getBlockAt(x, y, z).setType(Material.STAINED_GLASS);
		            mapWorld.getBlockAt(x, y, z).setData(cByte);
		            mapWorld.getBlockAt(x, y + 1, z + 1).setType(Material.STAINED_GLASS);
		            mapWorld.getBlockAt(x, y + 1, z + 1).setData(cByte);
		            mapWorld.getBlockAt(x, y + 1, z - 1).setType(Material.STAINED_GLASS);
		            mapWorld.getBlockAt(x, y + 1, z - 1).setData(cByte);
		            mapWorld.getBlockAt(x + 1, y + 1, z).setType(Material.STAINED_GLASS);
		            mapWorld.getBlockAt(x + 1, y + 1, z).setData(cByte);
		            mapWorld.getBlockAt(x - 1, y + 1, z).setType(Material.STAINED_GLASS);
		            mapWorld.getBlockAt(x - 1, y + 1, z).setData(cByte);
		            mapWorld.getBlockAt(x, y + 2, z + 1).setType(Material.STAINED_GLASS);
		            mapWorld.getBlockAt(x, y + 2, z + 1).setData(cByte);
		            mapWorld.getBlockAt(x, y + 2, z - 1).setType(Material.STAINED_GLASS);
		            mapWorld.getBlockAt(x, y + 2, z - 1).setData(cByte);
		            mapWorld.getBlockAt(x + 1, y + 2, z).setType(Material.STAINED_GLASS);
		            mapWorld.getBlockAt(x + 1, y + 2, z).setData(cByte);
		            mapWorld.getBlockAt(x - 1, y + 2, z).setType(Material.STAINED_GLASS);
		            mapWorld.getBlockAt(x - 1, y + 2, z).setData(cByte);
		            mapWorld.getBlockAt(x, y + 3, z + 1).setType(Material.STAINED_GLASS);
		            mapWorld.getBlockAt(x, y + 3, z + 1).setData(cByte);
		            mapWorld.getBlockAt(x, y + 3, z - 1).setType(Material.STAINED_GLASS);
		            mapWorld.getBlockAt(x, y + 3, z - 1).setData(cByte);
		            mapWorld.getBlockAt(x + 1, y + 3, z).setType(Material.STAINED_GLASS);
		            mapWorld.getBlockAt(x + 1, y + 3, z).setData(cByte);
		            mapWorld.getBlockAt(x - 1, y + 3, z).setType(Material.STAINED_GLASS);
		            mapWorld.getBlockAt(x - 1, y + 3, z).setData(cByte);
		            mapWorld.getBlockAt(x, y + 4, z).setType(Material.STAINED_GLASS);
		            mapWorld.getBlockAt(x, y + 4, z).setData(cByte);
				}
			}
		}
	}
    
    public void removeSpawnHousing() {
    	World mapWorld;
		mapWorld = SkyWarsReloaded.get().getServer().getWorld(this.getName() + "_" + this.getMapCount());
		final GameMap gMap = this;
		this.allowFallDamage = false;
        new BukkitRunnable() {
			@Override
			public void run() {
				if (kit != null) {
					gMap.allowFallDamage = SkyWarsReloaded.getCfg().allowFallDamage();
				}
			}
        	
        }.runTaskLater(SkyWarsReloaded.get(), 20L);
    	for(PlayerCard pCard: playerCards) {
            int x = pCard.getSpawn().getBlockX();
            int y = pCard.getSpawn().getBlockY();
            int z = pCard.getSpawn().getBlockZ();
            
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
	
    
    
	/*Bungeemode Methods*/
	
	public void sendBungeeUpdate() {
		if (SkyWarsReloaded.getCfg().bungeeMode()) {
			String playerCount = "" + this.getAlivePlayers().size();
			String maxPlayers = "" + this.getMaxPlayers();
			String gameStarted = "" + this.matchState.toString();
			ArrayList<String> messages = new ArrayList<String>();
			messages.add("ServerUpdate");
			messages.add(SkyWarsReloaded.get().getServerName());
			messages.add(playerCount);
			messages.add(maxPlayers);
			messages.add(gameStarted);
			Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
			if (player != null) {
				SkyWarsReloaded.get().sendSWRMessage(player, SkyWarsReloaded.getCfg().getBungeeLobby(), messages);
			}
		}
	}
	
	
	/*Mapdata Methods*/
	
	public static Map<String, MapData> getMapData() {
		return mapData;
	}
	
	public static void loadMapData() {
		mapData.clear();
        File mapFile = new File(SkyWarsReloaded.get().getDataFolder(), "maps.yml");

        if (!mapFile.exists()) {
        	SkyWarsReloaded.get().saveResource("maps.yml", false);
        }
        
        if (mapFile.exists()) {
            FileConfiguration storage = YamlConfiguration.loadConfiguration(mapFile);

            if (storage.getConfigurationSection("maps") != null) {
                for (String key: storage.getConfigurationSection("maps").getKeys(false)) {
                	String name = key;
                	String displayname = storage.getString("maps." + key + ".displayname");
                	int minplayers = storage.getInt("maps." + key + ".minplayers");
                	String creator = storage.getString("maps." + key + ".creator");
                	List<String> signs = storage.getStringList("maps." + key + ".signs");
                	boolean registered = storage.getBoolean("maps." + key + ".registered");
                	mapData.put(key, new MapData(name, displayname, minplayers, creator, signs, registered));
                }
            }
        }
        
        for (GameMap gMap: arenas) {
            gMap.setMapData(mapData.get(gMap.getName()));
        }
	}
	
	private void setMapData(MapData mpdata) {
		this.mp = mpdata;
		
		if (mp == null) {
        	mapData.put(name, new MapData(name, name, ((int) playerCards.size()/2) >= 2 ? (int) playerCards.size()/2 : 2 , "", null, false));
        	saveMapData();
        	mp = mapData.get(name);
        }
        
        this.minPlayers = mp.getMinPlayers() >= 2 ? mp.getMinPlayers() : 2;
        this.displayName = mp.getDisplayName();
        this.designedBy = mp.getCreator();
        this.registered = mp.isRegistered();
        
        if (mp.getSigns() != null) {
        	for (String sign: mp.getSigns()) {
        		signs.add(new SWRSign(this.getName(), Util.get().stringToLocation(sign)));
        	}
        }
	}
	
	private static void saveMapData() {
		try {
			 File mapFile = new File(SkyWarsReloaded.get().getDataFolder(), "maps.yml");

			 if (!mapFile.exists()) {
				 SkyWarsReloaded.get().saveResource("maps.yml", false);
		     }
		        
		     if (mapFile.exists()) {
		         FileConfiguration storage = YamlConfiguration.loadConfiguration(mapFile);
		         storage.set("maps", null);
		         for (GameMap gMap: arenas) {
		        	 storage.set("maps." + gMap.getName() + ".displayname", gMap.getDisplayName());
		        	 storage.set("maps." + gMap.getName() + ".minplayers", gMap.getMinPlayers());
		        	 storage.set("maps." + gMap.getName() + ".creator", gMap.getDesigner());
		        	 storage.set("maps." + gMap.getName() + ".registered", gMap.isRegistered());
		        	 List<String> stringSigns = new ArrayList<String>();
		        	 for (SWRSign s: gMap.getSigns()) {
		        		 stringSigns.add(Util.get().locationToString(s.getLocation()));
		        	 }
		        	 storage.set("maps." + gMap.getName() + ".signs", stringSigns);
		         }
		         storage.save(mapFile);
		     }
        } catch (IOException ioException) {
            System.out.println("Failed to save mapData");
        }
	}
	
	
	
    /*Sign Methods*/
	
	public void updateSigns() {
		for (SWRSign s : signs) {
			s.update();
		}
	}
	
	public List<SWRSign> getSigns() {
		return this.signs;
	}
	

	public boolean hasSign(Location loc) {
		for (SWRSign s: signs) {
			if (s.getLocation().equals(loc)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean removeSign(Location loc) {
		SWRSign sign = null;
		for (SWRSign s: signs) {
			if (s.getLocation().equals(loc)) {
				sign = s;
			}
		}
		if (sign != null) {
			signs.remove(sign);
			saveMapData();
			updateSigns();
			return true;
		}
		return false;
	}
	
	public void addSign(Location loc) {
		signs.add(new SWRSign(name, loc));
		saveMapData();
		updateSigns();
	}
	
	
	
	/*Getter and Setter Methods*/
        
    public String getDisplayName() {
    	return this.displayName;
    }
    
    public String getDesigner() {
    	return this.designedBy;
    }
    
    public int getMapCount() {
    	return mapUseCount;
    }
    
    public Map<Integer, EmptyChest> getChests(){
		return chests;
	}
	
	public Map<Integer, EmptyChest> getDoubleChests(){
		return doubleChests;
	}
	
	public boolean containsSpawns() {
		if (playerCards.size() >= 2) {
			return true;
		}
		return false;
	}
    
    public MatchState getMatchState() {
        return this.matchState;
    }
    
    public void setMatchState(final MatchState state) {
        this.matchState = state;
    }
    
    public int getPlayerCount() {
    	int count = 0;
		for (PlayerCard pCard: playerCards) {
			if (pCard.getPreElo() != -1) {
				count++;
			}
		}
		return count;
    }
    
    public int getMinPlayers() {
    	if (minPlayers == 0) {
    		return playerCards.size();
    	}
    	return minPlayers;
    }
    
    public void setMinPlayers(int x) {
    	minPlayers = x;
    	saveMapData();
    }
        
    public int getTimer() {
        return this.timer;
    }
    
    public void setTimer(final int lenght) {
        this.timer = lenght;
    }
    
    public GameKit getKit() {
    	return kit;
    }
          
    public String getName() {
        return this.name;
    }
       
	public static void shuffle() {
		Collections.shuffle(arenas);
	}
	
	public void setAllowFallDamage(boolean b) {
		allowFallDamage = b;
	}
   
	public boolean allowFallDamage() {
		return allowFallDamage;
	}

	public ArrayList<UUID> getDead() {
		return dead;
	}

	public ArrayList<UUID> getSpectators() {
		return spectators;
	}
	
	public boolean isThunder() {
		return thunder;
	}

	public void setNextStrike(int randomNum) {
		nextStrike = randomNum;
	}
	
	public int getNextStrike() {
		return nextStrike;
	}
	
	public void setStrikeCounter(int num) {
		strikeCounter = num;
	}
	
	public int getStrikeCounter() {
		return strikeCounter;
	}
	
	public int getMaxPlayers() {
		return playerCards.size();
	}
	
	public boolean isMatchStarted() {
		if (this.matchState == MatchState.WAITINGSTART) {
			return false;
		}
		return true;
	}
	
	public void setThunderStorm(boolean b) {
		this.thunder = b;
	}
	

	public ArrayList<PlayerCard> getPlayerCards() {
		return playerCards;
	}
	
	public PlayerCard getPlayerCard(Player player) {
		for (PlayerCard pCard: playerCards) {
			if (pCard.getPlayer() != null && pCard.getPlayer().equals(player)) {
				return pCard;
			}
		}
		return null;
	}
	
	public void setForceStart(boolean state) {
		forceStart = true;
	}
	
	public boolean getForceStart() {
		return forceStart;
	}

	public static GameMap getMapByDisplayName(String name) {
		for (GameMap gMap: arenas) {
			if (ChatColor.stripColor((ChatColor.translateAlternateColorCodes('&', gMap.getDisplayName()))).equalsIgnoreCase(name)) {
				return gMap;
			}
		}
		return null;
	}

	public int getDisplayTimer() {
		return displayTimer;
	}

	public void setDisplayTimer(int displayTimer) {
		this.displayTimer = displayTimer;
	}

	public void setAllowRegen(boolean b) {
		allowRegen = b;
	}

	public boolean allowRegen() {
		return allowRegen;
	}
	
	public void setWinner(String name) {
		winner = name;
	}
	
	public boolean isRegistered() {
		return registered;
	}

	public void setRegistered(boolean b) {
		registered = b;
		saveMapData();
	}

	public void setCreator(String creator) {
		this.designedBy = creator;
		saveMapData();
	}

	public void setDisplayName(String displayName2) {
		this.displayName = displayName2;
		saveMapData();
	}

	public static void editMap(GameMap gMap, Player player) {
		gMap.unregister();
		String worldName = gMap.getName();
		boolean alreadyLoaded = false;
		for (World world: SkyWarsReloaded.get().getServer().getWorlds()) {
			if (world.getName().equalsIgnoreCase(worldName)) {
				alreadyLoaded = true;
				World editWorld = SkyWarsReloaded.get().getServer().getWorld(worldName);
				player.teleport(new Location(editWorld, 0, 95, 0), TeleportCause.PLUGIN);
				player.setGameMode(GameMode.CREATIVE);
				player.setAllowFlight(true);
				player.setFlying(true);
			}
		} 
		
		if (!alreadyLoaded) {
			File dataDirectory = new File(SkyWarsReloaded.get().getDataFolder(), "maps");
			File source = new File (dataDirectory, worldName);
			File target = new File (SkyWarsReloaded.get().getServer().getWorldContainer().getAbsolutePath(), worldName);
			boolean mapExists = false;
			if(target.isDirectory()) {			 
				if(target.list().length > 0) {
		 			mapExists = true;
				}	 
			}
			if (mapExists) {
				SkyWarsReloaded.getWM().deleteWorld(worldName);
			}
			SkyWarsReloaded.getWM().copyWorld(source, target);
			boolean loaded = SkyWarsReloaded.getWM().loadWorld(worldName);
			if (loaded) {
				SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
					public void run() {
						World editWorld = SkyWarsReloaded.get().getServer().getWorld(worldName);
						player.teleport(new Location(editWorld, 0, 95, 0), TeleportCause.PLUGIN);
						player.setGameMode(GameMode.CREATIVE);
						player.setAllowFlight(true);
						player.setFlying(true);
					}
				}, 20);
			} else {
				player.sendMessage(new Messaging.MessageFormatter().format("error.map-fail-load"));
			}
		}
	}

	public String getArenaKey() {
		return arenakey;
	}

	public void setCurrentChest(String voteString) {
		currentChest = voteString;
	}

	public void setCurrentModifier(String voteString) {
		currentModifier = voteString;
	}

	public void setCurrentTime(String voteString) {
		currentTime = voteString;	
	}
	
	public void setCurrentHealth(String voteString) {
		currentHealth = voteString;	
	}

	public void setCurrentWeather(String voteString) {
		currentWeather = voteString;
	}

	public GameOption getChestOption() {
		return chestOption;
	}

	public GameOption getTimeOption() {
		return timeOption;
	}

	public GameOption getWeatherOption() {
		return weatherOption;
	}

	public GameOption getModifierOption() {
		return modifierOption;
	}

	public void setKit(GameKit voted) {
		this.kit = voted;
	}

	public KitVoteOption getKitVoteOption() {
		return kitVoteOption;
	}

	public GameOption getHealthOption() {
		return healthOption;
	}

}
