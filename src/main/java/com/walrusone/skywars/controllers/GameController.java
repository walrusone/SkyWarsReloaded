package com.walrusone.skywars.controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.Game;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.game.Game.GameState;
import com.walrusone.skywars.utilities.IconMenu;
import com.walrusone.skywars.utilities.ItemUtils;
import com.walrusone.skywars.utilities.Messaging;

public class GameController {

	private static final int menuSlotsPerRow = 9;
	private static final int menuSize = 54;
	private static final String menuName = new Messaging.MessageFormatter().format("menu.spectategame-menu-title");
	private Map<Integer, Game> gameNumbers = Maps.newHashMap();
	private Map<Integer, GameSign> signJoinGames = Maps.newHashMap();
	private boolean shutdown = false;
	private Queue<GamePlayer> waitingPlayers = new LinkedList<GamePlayer>();
	private ItemStack exit;
	private ItemStack opvote;
	private ItemStack kit;
	private ItemStack jumpVote;
	private ItemStack timeVote;
	private int gameNumber = 0;
	org.bukkit.material.Sign meteSign = new org.bukkit.material.Sign();

	public GameController() {
		String kitItem = SkyWarsReloaded.get().getConfig().getString("gameItems.kitMenuItem");
		List<String> kitItemData = new LinkedList<String>(Arrays.asList(kitItem.split(" ")));
		String kitName = "name:" + new Messaging.MessageFormatter().format("menu.kit-item-name");
		kitItemData.add(kitName);
		kit = ItemUtils.parseItem(kitItemData);
		
		String exitItem = SkyWarsReloaded.get().getConfig().getString("gameItems.exitGameItem");
		List<String> exitItemData = new LinkedList<String>(Arrays.asList(exitItem.split(" ")));
		String exitName = "name:" + new Messaging.MessageFormatter().format("menu.returntospawn-item-name");
		exitItemData.add(exitName);
		exit = ItemUtils.parseItem(exitItemData);
		
		String opVoteItem = SkyWarsReloaded.get().getConfig().getString("gameItems.opChestVoteItem");
		List<String> opVoteItemData = new LinkedList<String>(Arrays.asList(opVoteItem.split(" ")));
		String opVoteName = "name:" + new Messaging.MessageFormatter().format("menu.chest-item-name");
		opVoteItemData.add(opVoteName);
		opvote = ItemUtils.parseItem(opVoteItemData);
		
		String timeVoteItem = SkyWarsReloaded.get().getConfig().getString("gameItems.timeVoteItem");
		List<String> timeVoteItemData = new LinkedList<String>(Arrays.asList(timeVoteItem.split(" ")));
		String timeVoteName = "name:" + new Messaging.MessageFormatter().format("menu.time-item-name");
		timeVoteItemData.add(timeVoteName);
		timeVote = ItemUtils.parseItem(timeVoteItemData);
		
		String jumpVoteItem = SkyWarsReloaded.get().getConfig().getString("gameItems.jumpVoteItem");
		List<String> jumpVoteItemData = new LinkedList<String>(Arrays.asList(jumpVoteItem.split(" ")));
		String jumpVoteName = "name:" + new Messaging.MessageFormatter().format("menu.jump-item-name");
		jumpVoteItemData.add(jumpVoteName);
		jumpVote = ItemUtils.parseItem(jumpVoteItemData);
	}
	
	public Game findGame() {
		ArrayList<Game> games = getGames();
		for (int i = 0; i < getGames().size(); i++) {
			if (!games.get(i).isFull() && games.get(i).getState() == GameState.PREGAME) {
				return games.get(i);
			}
		}
		return createGame();
	}
	
	public Game createGame() {
			gameNumber = findAvailableNumber();
			if (gameNumber != -1) {
				final Game game = new Game(gameNumber, "");
				gameNumbers.put(gameNumber, game);
				if (!waitingPlayers.isEmpty()) {
					while (!game.isFull() && !waitingPlayers.isEmpty()) {
						game.addPlayer(waitingPlayers.remove());
					}
					if (game.isFull()) {
						createGame();
					}
				}
				return game;
			} else {
				return null;
			}
	}
	
	private int findAvailableNumber() {
		int maxNum = SkyWarsReloaded.get().getConfig().getInt("gameVariables.maxNumberOfGames");
		if (maxNum == -1) {
			maxNum = Integer.MAX_VALUE;
		}
		int gameNumber = -1;
		
		for (int i = 1; i <= maxNum; i++) {
			if (gameNumbers.get(i) == null) {
				return i;
			}
		}
		return gameNumber;
	}
	
	public void signJoinLoad() {
		 File signJoinFile = new File(SkyWarsReloaded.get().getDataFolder(), "signJoinGames.yml");

	     if (!signJoinFile.exists()) {
	    	 SkyWarsReloaded.get().saveResource("signJoinGames.yml", false);
	     }

	     if (signJoinFile.exists()) {
	    	 FileConfiguration storage = YamlConfiguration.loadConfiguration(signJoinFile);
	    	 try {
		    	 for (String gameNumber : storage.getConfigurationSection("games.").getKeys(false)) {
		    		 String mapName = storage.getString("games." + gameNumber + ".map");
		    		 String world = storage.getString("games." + gameNumber + ".world");
		    		 if (mapName != null && world != null) {
		    			 GameSign gs = new GameSign(storage.getInt("games." + gameNumber + ".x"), storage.getInt("games." + gameNumber + ".y"), storage.getInt("games." + gameNumber + ".z"), world, mapName);
		    			 signJoinGames.put(Integer.valueOf(gameNumber), gs);
		    			 createGame(Integer.valueOf(gameNumber), gs);
		    		 }
		    	 }
	    	 } catch (NullPointerException e) {
	    	 }
	     }
	}
	
	public boolean addSignJoinGame(Location loc, String mapName) {
		if (SkyWarsReloaded.getMC().mapRegistered(mapName)) {
			String world = loc.getWorld().getName().toString();
			int x = loc.getBlockX();
			int y = loc.getBlockY();
			int z = loc.getBlockZ();
			GameSign gs = new GameSign(x, y, z, world, mapName);
			gameNumber = -1;
			File signJoinFile = new File(SkyWarsReloaded.get().getDataFolder(), "signJoinGames.yml");
			if (!signJoinFile.exists()) {
				SkyWarsReloaded.get().saveResource("signJoinGames.yml", false);
			}
			if (signJoinFile.exists()) {
				FileConfiguration storage = YamlConfiguration.loadConfiguration(signJoinFile);
				for (int i = 1; i < 1000; i++) {
					if (storage.getString("games." + i + ".map") == null) {
						gameNumber = i;
						break;
					}
				}
				storage.set("games." + gameNumber + ".x", x);
				storage.set("games." + gameNumber + ".y", y);
				storage.set("games." + gameNumber + ".z", z);
				storage.set("games." + gameNumber + ".world", world);
				storage.set("games." + gameNumber + ".map", mapName);
				try {
					storage.save(signJoinFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				signJoinGames.put(gameNumber, gs);
				createGame(gameNumber, gs);
				return true;
			} else {
				return false;
			}
			}
		return false;
	}

	public void removeSignJoinGame(String gameNumber) {
		File signJoinFile = new File(SkyWarsReloaded.get().getDataFolder(), "signJoinGames.yml");
		FileConfiguration storage = YamlConfiguration.loadConfiguration(signJoinFile);
  		storage.set("games." + gameNumber, null);
  		try {
			storage.save(signJoinFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
        signJoinGames.remove(Integer.valueOf(gameNumber));
        Game game = getGame(Integer.valueOf(gameNumber));
        if (game.getState() != GameState.PLAYING) {
        	game.endGame();
        }
	}

	
	protected void createGame(int gameNumber, GameSign gs) {
		 Game game = new Game(gameNumber, gs.getName());
		 gameNumbers.put(gameNumber, game);
		 updateSign(Integer.valueOf(gameNumber));
	}
	
	public void updateSign(int gameNumber) {
		GameSign gameSign = signJoinGames.get(gameNumber);
		World world = SkyWarsReloaded.get().getServer().getWorld(gameSign.getWorld());
		if (world != null) {
			Block b = world.getBlockAt(gameSign.getX(), gameSign.getY(), gameSign.getZ());
			if(b.getType() == Material.WALL_SIGN || b.getType() == Material.SIGN_POST) {
				Sign s = (Sign) b.getState();
				meteSign = (org.bukkit.material.Sign) b.getState().getData();
				Block attachedBlock = b.getRelative(meteSign.getAttachedFace());
				String state = getStatusName(getGame(gameNumber));
				setMaterial(getStatus(getGame(gameNumber)), attachedBlock);
				int max = getGame(gameNumber).getNumberOfSpawns();
				int count = getGame(gameNumber).getPlayers().size();
				if (s != null) {
					s.getBlock().getChunk().load();
					s.setLine(0, new Messaging.MessageFormatter().format("signJoinSigns.line1"));
					s.setLine(1, new Messaging.MessageFormatter().setVariable("mapName", gameSign.getName().toUpperCase()).format("signJoinSigns.line2"));
					s.setLine(2, new Messaging.MessageFormatter().setVariable("gameStatus", state).format("signJoinSigns.line3"));
					s.setLine(3, new Messaging.MessageFormatter().setVariable("count", "" + count).setVariable("max", "" + max).format("signJoinSigns.line4"));
					s.update();
				}
			}
		}

	}
	
	public enum GameStatus {
	    JOINABLE,
	    FULL,
	    INPROGRESS,
	    RESTARTING,
	}

	public GameStatus getStatus(Game game) {
		if (game.getState() == GameState.PREGAME && !game.isFull()) {
			return GameStatus.JOINABLE;
		} else if (game.getState() == GameState.PREGAME && game.isFull()) {
			return GameStatus.FULL;
		} else if (game.getState() == GameState.PLAYING) {
			return GameStatus.INPROGRESS;
		} else {
			return GameStatus.RESTARTING;
		}
	}
	
	public String getStatusName(Game game) {
		if (game.getState() == GameState.PREGAME && !game.isFull()) {
			return new Messaging.MessageFormatter().format("signJoinSigns.joinable");
		} else if (game.getState() == GameState.PREGAME && game.isFull()) {
			return new Messaging.MessageFormatter().format("signJoinSigns.full");
		} else if (game.getState() == GameState.PLAYING) {
			return new Messaging.MessageFormatter().format("signJoinSigns.inprogress");
		} else {
			return new Messaging.MessageFormatter().format("signJoinSigns.restarting");
		}
	}
	
	@SuppressWarnings("deprecation")
	private void setMaterial(GameStatus gs, Block attachedBlock) {
		if (gs == GameStatus.JOINABLE) {
			attachedBlock.setType(Material.WOOL);
			attachedBlock.setData((byte) 5);
		} else if (gs == GameStatus.FULL || gs == GameStatus.INPROGRESS) {
			attachedBlock.setType(Material.WOOL);
			attachedBlock.setData((byte) 14);
		} else if (gs == GameStatus.RESTARTING) {
			attachedBlock.setType(Material.WOOL);
			attachedBlock.setData((byte) 11);
		}
	}
	
	public void deleteGame(final int gameNumber) {
		final GameSign gs = signJoinGames.get(gameNumber);
		gameNumbers.remove(gameNumber);
		if (SkyWarsReloaded.get().getConfig().getBoolean("bungeeMode.enabled")  && !shutdown) {
			SkyWarsReloaded.get().getServer().getScheduler().runTaskLater(SkyWarsReloaded.get(), new Runnable() {
				public void run() {
					createGame();
				  }
			}, 40);
		}
		if (SkyWarsReloaded.get().getConfig().getBoolean("signJoinMode")  && !shutdown && gs != null) {
			SkyWarsReloaded.get().getServer().getScheduler().runTaskLater(SkyWarsReloaded.get(), new Runnable() {
				public void run() {
					createGame(gameNumber, gs);
				  }
			}, 40);
		}
	}
	
	public void shutdown() {
		 shutdown = true;
		 for (Game game : getGames()) {
			 game.shutdown();
			 game.endGame();
	     }
	 }

	 public ArrayList<Game> getGames() {
			ArrayList<Game> games = new ArrayList<Game>();
			for (Game g: gameNumbers.values()) {
				games.add(g);
			}
			return games;
	 }
	 
	 public Game getGame(int gameNumber) {
		 return gameNumbers.get(gameNumber);
	 }
	 
	 public boolean inGame(GamePlayer gPlayer) {
		 for (Game game: getGames()) {
			 for (GamePlayer gamePlayer: game.getPlayers()) {
				 if (gamePlayer == gPlayer) {
					 return true;
				 }
			 }
		 }
		 return false;
	 }
	
	    public void openGameMenu(final GamePlayer gamePlayer) {
	        List<Game> availableGames = Lists.newArrayList(getGames());

	        int rowCount = menuSlotsPerRow;
	        while (rowCount < availableGames.size() && rowCount < menuSize) {
	            rowCount += menuSlotsPerRow;
	        }

	        SkyWarsReloaded.getIC().create(gamePlayer.getP(), menuName, rowCount, new IconMenu.OptionClickEventHandler() {
	            @Override
	            public void onOptionClick(IconMenu.OptionClickEvent event) {
	            	boolean allowSpectating = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.allowSpectating");
	                if (!allowSpectating) {
	                	return;
	                }
	                
	                if (gamePlayer.inGame()) {
	                	event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("error.no-spectate-other-worlds"));
	                    return;
	                }

	                Game game = getGame(Integer.parseInt(ChatColor.stripColor(event.getName())));
	                if (game == null) {
	                    return;
	                }

	                if (!SkyWarsReloaded.perms.has(gamePlayer.getP(), "swr.spectate")) {
	                    event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("error.no-spectate-perm"));
	                    return;
	                }

	                event.setWillClose(true);
	                event.setWillDestroy(true);

	                gamePlayer.saveGameMode();
	                gamePlayer.saveInventory();
	                if (gamePlayer.getP() != null) {
		                gamePlayer.setRespawn(gamePlayer.getP().getLocation());
		                gamePlayer.spectateMode(true, game);
		                Location location = game.getSpawn();
		                gamePlayer.getP().teleport(location);
						gamePlayer.getP().setAllowFlight(true);
						gamePlayer.getP().setFlying(true);
	                }
	            }
	        });

	        ArrayList<Game> games = SkyWarsReloaded.getGC().getGames();
	        
	        for (int iii = 0; iii < games.size(); iii++) {
	            if (iii >= menuSize) {
	                break;
	            }

	            Game game = games.get(iii);
	            	            
	            if (game.getState() == GameState.PREGAME || game.getState() == GameState.PLAYING) {
		            List<String> loreList = Lists.newLinkedList();
	                loreList.add(new Messaging.MessageFormatter().format("menu.spectate-game-header"));
		            for (GamePlayer gPlayer: game.getPlayers()) {
		            	if (gPlayer.getP() != null) {
			            	loreList.add(ChatColor.WHITE + gPlayer.getP().getName());
		            	}
		            }
		            if (gamePlayer.getP() != null) {
			            SkyWarsReloaded.getIC().setOption(
			                    gamePlayer.getP(),
			                    iii,
			                    new ItemStack(Material.DIAMOND_HELMET, 1),
			                    String.valueOf(game.getGameNumber()),
			                    loreList.toArray(new String[loreList.size()]));
		            }
	            }
	        }
	        if (gamePlayer.getP() != null) {
		        SkyWarsReloaded.getIC().show(gamePlayer.getP());
	        }
	    }
	    
		public void addToQueue(GamePlayer gPlayer) {
			if (!waitingPlayers.contains(gPlayer)) {
				waitingPlayers.add(gPlayer);
			}
		}
		
		public ItemStack getKitItem() {
			return kit;
		}
		
		public ItemStack getExitItem() {
			return exit;
		}
		
		public ItemStack getOpVoteItem() {
			return opvote;
		}

		public ItemStack getMapVoteItem() {
			return jumpVote;
		}

		public ItemStack getTimeVoteItem() {
			return timeVote;
		}
		
		public ItemStack getJumpVoteItem() {
			return jumpVote;
		}
	
	private class GameSign {
		private int x;
		private int y;
		private int z;
		private String mapName;
		private String world;
		
		GameSign(int x, int y, int z, String world, String mapName) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.world = world;
			this.mapName = mapName;
		}
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}
		
		public int getZ() {
			return z;
		}
		
		public String getWorld() {
			return world;
		}
		
		public String getName() {
			return mapName;
		}
	}
}


