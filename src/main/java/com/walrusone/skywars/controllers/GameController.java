package com.walrusone.skywars.controllers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.bungee.BungeeSocket;
import com.walrusone.skywars.game.Game;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.game.Game.GameState;
import com.walrusone.skywars.utilities.IconMenu;
import com.walrusone.skywars.utilities.Messaging;

public class GameController {

private CopyOnWriteArrayList<Game> games = new CopyOnWriteArrayList<Game>();
private static final int menuSlotsPerRow = 9;
private static final int menuSize = 54;
private static final String menuName = "Spectate Game Menu";
private final Map<Integer, Game> gameNumbers = Maps.newHashMap();
private boolean shutdown = false;
private Queue<GamePlayer> waitingPlayers = new LinkedList<GamePlayer>();
private ItemStack door = new ItemStack(Material.IRON_DOOR, 1);

	int gameNumber = 0;

	public Game findGame() {
		for (int i = 0; i < games.size(); i++) {
			if (!games.get(i).isFull() && (games.get(i).getState() == GameState.WAITING || games.get(i).getState() == GameState.INLOBBY)) {
				return games.get(i);
			}
		}
		return createGame();
	}
	
	public Game createGame() {
			gameNumber = findAvailableNumber();
			if (gameNumber != -1) {
				final Game game = new Game(gameNumber);
				games.add(game);
				gameNumbers.put(gameNumber, game);
				if (SkyWarsReloaded.get().getConfig().getBoolean("bungeeMode.enabled")) {
					BungeeSocket.sendSignUpdate(game);
					SkyWarsReloaded.get().getServer().getScheduler().runTaskLater(SkyWarsReloaded.get(), new Runnable() {
						public void run() {
							BungeeSocket.sendSignUpdate(game);
						}
					}, 40);
				}
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
	
	public void deleteGame(Game game) {
		int gameNumber = game.getGameNumber();
		gameNumbers.remove(gameNumber);
		games.remove(game);
		if (SkyWarsReloaded.get().getConfig().getBoolean("bungeeMode.enabled")  && !shutdown) {
			SkyWarsReloaded.get().getServer().getScheduler().runTaskLater(SkyWarsReloaded.get(), new Runnable() {
				public void run() {
					createGame();
				  }
			}, 40);
		}
	}
	
	 public void shutdown() {
		 shutdown = true;
		 for (Game game : games) {
			 game.shutdown();
			 game.endGame();
	     }
	 }

	 public CopyOnWriteArrayList<Game> getGames() {
	        return games;
	 }
	 
	 public Game getGame(int gameNumber) {
		 return gameNumbers.get(gameNumber);
	 }
	 
	 public Game getGameByName(String name) {
		 for (Game game: games) {
			 if (game.getBungeeName().equalsIgnoreCase(name)) {
				 return game;
			 }
		 }
		 return null;
	 }
	 
	 public boolean inGame(GamePlayer gPlayer) {
		 for (Game game: games) {
			 for (GamePlayer gamePlayer: game.getPlayers()) {
				 if (gamePlayer == gPlayer) {
					 return true;
				 }
			 }
		 }
		 return false;
	 }
	
	    public void openGameMenu(final GamePlayer gamePlayer) {
	        List<Game> availableGames = Lists.newArrayList(games);

	        int rowCount = menuSlotsPerRow;
	        while (rowCount < availableGames.size() && rowCount < menuSize) {
	            rowCount += menuSlotsPerRow;
	        }

	        SkyWarsReloaded.getIC().create(gamePlayer.getP(), menuName, rowCount, new IconMenu.OptionClickEventHandler() {
	            @Override
	            public void onOptionClick(IconMenu.OptionClickEvent event) {
	                if (gamePlayer.isPlaying()) {
	                	event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("error.no-spectate-other-worlds"));
	                    return;
	                }

	                if (gamePlayer.getGame() != null) {
	                	event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("error.no-spectate-other-worlds"));
	                    return;
	                }

	                Game game = SkyWarsReloaded.getGC().getGame(Integer.parseInt(ChatColor.stripColor(event.getName())));
	                if (game == null) {
	                    return;
	                }

	                if (!SkyWarsReloaded.perms.has(gamePlayer.getP(), "swr.spectate")) {
	                    event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("error.no-spectate-perm"));
	                    return;
	                }

	                event.setWillClose(true);
	                event.setWillDestroy(true);

	                
					Location location = game.getSpawn();
	                gamePlayer.getP().teleport(location);
	                SkyWarsReloaded.getSpectate().setSpectating(gamePlayer.getP(), true, true);
	                SkyWarsReloaded.getSpectate().setArenaForPlayer(gamePlayer.getP(), SkyWarsReloaded.getSpectate().getArenasManager().getArena(game.getBungeeName()), false);
	                game.addSpectator(gamePlayer);
	                gamePlayer.setSpecGame(game);
	                ItemMeta meta1 = door.getItemMeta();
	        		meta1.setDisplayName("Return to Spawn");
	        		door.setItemMeta(meta1);
	        		door.addUnsafeEnchantment(Enchantment.LUCK, 1);
	        		gamePlayer.getP().getInventory().setItem(8, door);
	            }
	        });

	        for (int iii = 0; iii < games.size(); iii ++) {
	            if (iii >= menuSize) {
	                break;
	            }

	            Game game = games.get(iii);
	            
	            
	            if (game.getState() == GameState.PREGAME || game.getState() == GameState.PLAYING) {
		            List<String> loreList = Lists.newLinkedList();
		            loreList.add(" ");
	                loreList.add(ChatColor.AQUA + "" + ChatColor.UNDERLINE + "Players");
		            for (GamePlayer gPlayer: game.getPlayers()) {
		            	loreList.add(ChatColor.WHITE + gPlayer.getP().getName());
		            }
		            SkyWarsReloaded.getIC().setOption(
		                    gamePlayer.getP(),
		                    iii,
		                    new ItemStack(Material.DIAMOND_HELMET, 1),
		                    String.valueOf(game.getGameNumber()),
		                    loreList.toArray(new String[loreList.size()]));
	            }
	        }

	        SkyWarsReloaded.getIC().show(gamePlayer.getP());
	    }

	    public void openPlayerMenu(final GamePlayer gamePlayer) {
	    	int menuSlotsPerRow = 9;
	    	int menuSize = 54;
	    	String menuName = "Spectate Player";
	        List<Player> availablePlayers = new ArrayList<Player>();
	        Game game;
	        
	        if (gamePlayer.getSpecGame() == null) {
	        	game = gamePlayer.getGame();
	        } else {
	        	game = gamePlayer.getSpecGame();
	        }
	        
	        for (GamePlayer gPlayer: game.getPlayers()) {
	        	if (gPlayer.isPlaying()) {
	        		availablePlayers.add(gPlayer.getP());
	        	}
	        }

	        int rowCount = menuSlotsPerRow;
	        while (rowCount < availablePlayers.size() && rowCount < menuSize) {
	            rowCount += menuSlotsPerRow;
	        }

	        SkyWarsReloaded.getIC().create(gamePlayer.getP(), menuName, rowCount, new IconMenu.OptionClickEventHandler() {
	            @Override
	            public void onOptionClick(IconMenu.OptionClickEvent event) {
	            	UUID uid = UUID.fromString(event.getName());
	                Player player = SkyWarsReloaded.get().getServer().getPlayer(uid);
	                if (player == null) {
	                    return;
	                }

	                if (!SkyWarsReloaded.perms.has(gamePlayer.getP(), "swr.spectate")) {
	                    event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("error.no-spectate-perm"));
	                    return;
	                }

	                event.setWillClose(true);
	                event.setWillDestroy(true);

	                

	                SkyWarsReloaded.getSpectate().teleportSpectatorToPlayer(gamePlayer.getP(), player);
	            }
	        });

	        for (int iii = 0; iii < availablePlayers.size(); iii ++) {
	            if (iii >= menuSize) {
	                break;
	            }

	            Player player = availablePlayers.get(iii);
	            
	            List<String> loreList = Lists.newLinkedList();
                loreList.add(ChatColor.AQUA + player.getName());
	            SkyWarsReloaded.getIC().setOption(
	            		gamePlayer.getP(),
		                iii,
		                new ItemStack(Material.DIAMOND_HELMET, 1),
		                String.valueOf(player.getUniqueId()),
		                loreList.toArray(new String[loreList.size()]));
	            }

	        SkyWarsReloaded.getIC().show(gamePlayer.getP());
	    }
	    
		public void addToQueue(GamePlayer gPlayer) {
			if (!waitingPlayers.contains(gPlayer)) {
				waitingPlayers.add(gPlayer);
			}
		}
	 
}
