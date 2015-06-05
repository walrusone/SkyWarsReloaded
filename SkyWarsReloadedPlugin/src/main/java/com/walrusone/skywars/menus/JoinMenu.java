package com.walrusone.skywars.menus;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;
import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.Game;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.game.Game.GameState;
import com.walrusone.skywars.utilities.IconMenu;
import com.walrusone.skywars.utilities.Messaging;

public class JoinMenu {

	private static final int menuSlotsPerRow = 9;
	private static final int menuSize = 81;
	private static final String joinName = new Messaging.MessageFormatter().format("menu.joingame-menu-title");
	
    public JoinMenu(final GamePlayer gamePlayer) {
        List<Game> availableGames = SkyWarsReloaded.getGC().getGames();

        int rowCount = menuSlotsPerRow;
        while (rowCount < availableGames.size() && rowCount < menuSize) {
            rowCount += menuSlotsPerRow;
        }

        SkyWarsReloaded.getIC().create(gamePlayer.getP(), joinName, rowCount, new IconMenu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IconMenu.OptionClickEvent event) {
                if (gamePlayer.inGame()) {
                    return;
                }

      
                Game game = SkyWarsReloaded.getGC().getGame(Integer.parseInt(ChatColor.stripColor(event.getName())));
                if (game == null) {
                    return;
                }
                
                if (game.getState() != GameState.PREGAME) {
                	return;
                }

                event.setWillClose(true);
                event.setWillDestroy(true);
                
                if (gamePlayer.getP().hasPermission("swr.play")) {
	                if (gamePlayer.getP() != null) {
	                	if (game.getState() == GameState.PREGAME && !game.isFull()) {
		                	game.addPlayer(gamePlayer);
	                	}
	                }
                }
            }
        });

        ArrayList<Game> games = SkyWarsReloaded.getGC().getGames();
    
        for (int iii = 0; iii < games.size(); iii++) {
            if (iii >= menuSize) {
                break;
            }

            Game game = games.get(iii);
            	            
            List<String> loreList = Lists.newLinkedList();
            if (game.getState() != GameState.PREGAME) {
	            loreList.add((new Messaging.MessageFormatter().format("signJoinSigns.inprogress").toUpperCase()));
            }
            loreList.add(ChatColor.RED + "" + ChatColor.BOLD + game.getMapName().toUpperCase());
            loreList.add((new Messaging.MessageFormatter().format("menu.spectate-game-header") + "  " + game.getPlayers().size() + "/" + game.getNumberOfSpawns()));
            for (GamePlayer gPlayer: game.getPlayers()) {
            	if (gPlayer.getP() != null) {
	            	loreList.add(ChatColor.WHITE + gPlayer.getP().getName());
            	}
            }
            
            double xy = ((double) (game.getPlayers().size() / game.getNumberOfSpawns()));
            
            Material gameIcon = Material.REDSTONE_BLOCK;
            String gameNumber = String.valueOf(game.getGameNumber());
            if (game.getState() == GameState.PREGAME) {
            	gameIcon =  Material.DIAMOND_HELMET;
    	    	if (xy < 0.75) {
    	    		gameIcon = Material.GOLD_HELMET;
    	    	} 
    	    	if (xy < 0.50) {
    	    		gameIcon =  Material.IRON_HELMET;
    	    	} 
    	    	if (xy < 0.25) {
    	    		gameIcon =  Material.LEATHER_HELMET;
    	    	} 
            } 

            
            if (gamePlayer.getP() != null) {
	            SkyWarsReloaded.getIC().setOption(
	                    gamePlayer.getP(),
	                    iii,
	                    new ItemStack(gameIcon, 1),
	                    gameNumber,
	                    loreList.toArray(new String[loreList.size()]));
            }
        }
        
        if (gamePlayer.getP() != null && !gamePlayer.inGame()) {
	        SkyWarsReloaded.getIC().show(gamePlayer.getP());
        }
        final GamePlayer player = gamePlayer;

        SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
			@Override
			public void run() {
				updateJoinMenu(player);	
			}
        }, 40); 
    }
    
    public void updateJoinMenu(final GamePlayer gamePlayer) {
		if (!SkyWarsReloaded.getIC().has(gamePlayer.getP()) || gamePlayer.inGame() || !SkyWarsReloaded.getIC().getMenu(gamePlayer.getP()).getName().equalsIgnoreCase(joinName)) {
			return;
		} else {
	        ArrayList<Game> games = SkyWarsReloaded.getGC().getGames();
	        
	        for (int iii = 0; iii < games.size(); iii++) {
	            if (iii >= menuSize) {
	                break;
	            }

	            Game game = games.get(iii);
	            	            
	            List<String> loreList = Lists.newLinkedList();
	            if (game.getState() != GameState.PREGAME) {
		            loreList.add((new Messaging.MessageFormatter().format("signJoinSigns.inprogress").toUpperCase()));
	            }
	            loreList.add(ChatColor.RED + "" + ChatColor.BOLD + game.getMapName().toUpperCase());
                loreList.add((new Messaging.MessageFormatter().format("menu.spectate-game-header") + "  " + game.getPlayers().size() + "/" + game.getNumberOfSpawns()));
	            for (GamePlayer gPlayer: game.getPlayers()) {
	            	if (gPlayer.getP() != null) {
		            	loreList.add(ChatColor.WHITE + gPlayer.getP().getName());
	            	}
	            }
	            
	            double xy = ((double) (game.getPlayers().size() / game.getNumberOfSpawns()));
	            
	            Material gameIcon = Material.REDSTONE_BLOCK;
	            String gameNumber = String.valueOf(game.getGameNumber());
	            if (game.getState() == GameState.PREGAME) {
	            	gameIcon =  Material.DIAMOND_HELMET;
	    	    	if (xy < 0.75) {
	    	    		gameIcon = Material.GOLD_HELMET;
	    	    	} 
	    	    	if (xy < 0.50) {
	    	    		gameIcon =  Material.IRON_HELMET;
	    	    	} 
	    	    	if (xy < 0.25) {
	    	    		gameIcon =  Material.LEATHER_HELMET;
	    	    	} 
	            }  
	            
	            if (gamePlayer.getP() != null) {
		            SkyWarsReloaded.getIC().setOption(
		                    gamePlayer.getP(),
		                    iii,
		                    new ItemStack(gameIcon, 1),
		                    gameNumber,
		                    loreList.toArray(new String[loreList.size()]));
	            }
            }
	        
	        if (gamePlayer.getP() != null && !gamePlayer.inGame()) {
		        SkyWarsReloaded.getIC().update(gamePlayer.getP());
	        }
	        
	        final GamePlayer player = gamePlayer;

	        SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
				@Override
				public void run() {
					updateJoinMenu(player);	
				}
	        }, 40); 
		}
    }
    
}
