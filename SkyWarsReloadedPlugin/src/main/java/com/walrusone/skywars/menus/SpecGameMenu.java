package com.walrusone.skywars.menus;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;
import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.Game;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.game.Game.GameState;
import com.walrusone.skywars.utilities.IconMenu;
import com.walrusone.skywars.utilities.Messaging;

public class SpecGameMenu {

	private static final int menuSlotsPerRow = 9;
	private static final int menuSize = 81;
	private static final String menuName = new Messaging.MessageFormatter().format("menu.spectategame-menu-title");
	
    public SpecGameMenu(final GamePlayer gamePlayer) {
        List<Game> availableGames = SkyWarsReloaded.getGC().getGames();

        int rowCount = menuSlotsPerRow;
        while (rowCount < availableGames.size() && rowCount < menuSize) {
            rowCount += menuSlotsPerRow;
        }

        SkyWarsReloaded.getIC().create(gamePlayer.getP(), menuName, rowCount, new IconMenu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IconMenu.OptionClickEvent event) {
                if (!SkyWarsReloaded.getCfg().spectatingEnabled()) {
                	return;
                }
                
                if (gamePlayer.inGame()) {
                	event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("error.no-spectate-other-worlds"));
                    return;
                }

                Game game = SkyWarsReloaded.getGC().getGame(Integer.parseInt(ChatColor.stripColor(event.getName())));
                if (game == null) {
                    return;
                }

                if (game.getState() == GameState.ENDING) {
                	return;
                }
                
                if (!gamePlayer.getP().hasPermission("swr.spectate")) {
                    event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("error.no-spectate-perm"));
                    return;
                }

                event.setWillClose(true);
                event.setWillDestroy(true);

                if (gamePlayer.getP() != null) {
	                SkyWarsReloaded.getInvC().add(gamePlayer.getP());
	                Location location = game.getSpawn();
	                if (game.getState() == GameState.PREGAME) {
		                gamePlayer.spectateMode(true, game, location.add(0, 4, 0), false);
	                } else if (game.getState() == GameState.PLAYING) {
	                	gamePlayer.spectateMode(true, game, location, false);
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
            loreList.add(ChatColor.RED + "" + ChatColor.BOLD + game.getMapName().toUpperCase());
            loreList.add((new Messaging.MessageFormatter().format("menu.spectate-game-header") + "  " + game.getPlayers().size() + "/" + game.getNumberOfSpawns()));
            for (GamePlayer gPlayer: game.getPlayers()) {
            	if (gPlayer.getP() != null) {
	            	loreList.add(ChatColor.WHITE + gPlayer.getP().getName());
            	}
            }
            
            Material gameIcon = Material.REDSTONE_BLOCK;
            String gameNumber = String.valueOf(game.getGameNumber());
            if (game.getState() == GameState.PREGAME) {
            	gameIcon = Material.EMERALD_BLOCK;
            } else if (game.getState() == GameState.ENDING) {
            	gameIcon = Material.LAPIS_BLOCK;
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
        if (gamePlayer.getP() != null) {
	        SkyWarsReloaded.getIC().show(gamePlayer.getP());
        }
        
        final GamePlayer player = gamePlayer;

        SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
			@Override
			public void run() {
				updateSpectateMenu(player);	
			}
        }, 40);
    }

    public void updateSpectateMenu(final GamePlayer gamePlayer) {
		if (!SkyWarsReloaded.getIC().has(gamePlayer.getP()) || gamePlayer.isSpectating() || !SkyWarsReloaded.getIC().getMenu(gamePlayer.getP()).getName().equalsIgnoreCase(menuName)) {
			return;
		} else {
	        ArrayList<Game> games = SkyWarsReloaded.getGC().getGames();
	        
	        for (int iii = 0; iii < games.size(); iii++) {
	            if (iii >= menuSize) {
	                break;
	            }

	            
	            Game game = games.get(iii);
	            
	            List<String> loreList = Lists.newLinkedList();
	            loreList.add(ChatColor.RED + "" + ChatColor.BOLD + game.getMapName().toUpperCase());
                loreList.add((new Messaging.MessageFormatter().format("menu.spectate-game-header") + "  " + game.getPlayers().size() + "/" + game.getNumberOfSpawns()));
	            for (GamePlayer gPlayer: game.getPlayers()) {
	            	if (gPlayer.getP() != null) {
		            	loreList.add(ChatColor.WHITE + gPlayer.getP().getName());
	            	}
	            }
	            
	            Material gameIcon = Material.REDSTONE_BLOCK;
	            String gameNumber = String.valueOf(game.getGameNumber());
	            if (game.getState() == GameState.PREGAME) {
	            	gameIcon = Material.EMERALD_BLOCK;
	            } else if (game.getState() == GameState.ENDING) {
	            	gameIcon = Material.LAPIS_BLOCK;
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
	        if (gamePlayer.getP() != null) {
		        SkyWarsReloaded.getIC().update(gamePlayer.getP());
	        }
	        
	        final GamePlayer player = gamePlayer;

	        SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
				@Override
				public void run() {
					updateSpectateMenu(player);	
				}
	        }, 40);
		}
    }
    
}
