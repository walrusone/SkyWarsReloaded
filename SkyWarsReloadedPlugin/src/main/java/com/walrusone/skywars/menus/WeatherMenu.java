package com.walrusone.skywars.menus;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;
import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.Game;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.game.Game.GameState;
import com.walrusone.skywars.utilities.IconMenu;
import com.walrusone.skywars.utilities.Messaging;

public class WeatherMenu {

	private static final int menuSlotsPerRow = 9;
    private static final int menuSize = 36;
    private static final String weatherMenuName = new Messaging.MessageFormatter().format("menu.weather-menu-title");
    
    public WeatherMenu(final GamePlayer gamePlayer) {
    	
        int rowCount = menuSlotsPerRow;
        while (rowCount < 36 && rowCount < menuSize) {
            rowCount += menuSlotsPerRow;
        }

        SkyWarsReloaded.getIC().create(gamePlayer.getP(), weatherMenuName, rowCount, new IconMenu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IconMenu.OptionClickEvent event) {    
                String vote = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', event.getName()));

                if (gamePlayer.getGame().getState() != GameState.PREGAME) {
                	return;
                }
                
            	if (!hasWeatherPermission(event.getPlayer())) {
                    return;
            	}
            	
                event.setWillClose(false);
                event.setWillDestroy(false);
                                
                int lastVote = gamePlayer.getTimeVote();
                
                if (vote.equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', new Messaging.MessageFormatter().format("menu.weather-sunny"))))) {
                	gamePlayer.setWeatherVote(1);
                	gamePlayer.getP().closeInventory();
                	if (gamePlayer.getGame().getState() == GameState.PREGAME) {
                    	SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
    						@Override
    						public void run() {
    		                	new MainMenu(gamePlayer);
    						}
                    	}, 2);
                	}
                } else if (vote.equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', new Messaging.MessageFormatter().format("menu.weather-rain"))))) {
                	gamePlayer.setWeatherVote(2);
                	gamePlayer.getP().closeInventory();
                	if (gamePlayer.getGame().getState() == GameState.PREGAME) {
                    	SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
    						@Override
    						public void run() {
    		                	new MainMenu(gamePlayer);
    						}
                    	}, 2);
                	}
                } else if (vote.equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', new Messaging.MessageFormatter().format("menu.weather-thunder"))))) {
                	gamePlayer.setWeatherVote(3);
                	gamePlayer.getP().closeInventory();
                	if (gamePlayer.getGame().getState() == GameState.PREGAME) {
                    	SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
    						@Override
    						public void run() {
    		                	new MainMenu(gamePlayer);
    						}
                    	}, 2);
                	}
                } else if (vote.equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', new Messaging.MessageFormatter().format("menu.weather-snow"))))) {
                	gamePlayer.setWeatherVote(4);
                	gamePlayer.getP().closeInventory();
                	if (gamePlayer.getGame().getState() == GameState.PREGAME) {
                    	SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
    						@Override
    						public void run() {
    		                	new MainMenu(gamePlayer);
    						}
                    	}, 2);
                	}
                } else if (vote.equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', new Messaging.MessageFormatter().format("menu.return-to-options"))))) {
                	gamePlayer.getP().closeInventory();
                	if (gamePlayer.getGame().getState() == GameState.PREGAME) {
                    	SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
    						@Override
    						public void run() {
    		                	new MainMenu(gamePlayer);
    						}
                    	}, 2);
                	}
                }
                
                if (gamePlayer.getWeatherVote() != lastVote) {
                	Game game = gamePlayer.getGame();
                	for (GamePlayer gPlayer: game.getPlayers()) {
                		if (gPlayer.getP() != null) {
                    		gPlayer.getP().sendMessage(new Messaging.MessageFormatter()
        					.withPrefix()
        					.setVariable("player", gamePlayer.getName())
        					.setVariable("time", vote.toUpperCase())
        					.format("game.voted-for-time"));
                		}
                	}
                	gamePlayer.getGame().playSound(SkyWarsReloaded.getCfg().getWeatherVoteSound());
                }
            }
        });

        Game game = gamePlayer.getGame();
        int votesForSunny = 0;
        int votesForRain = 0;
        int votesForThunder = 0;
        int votesForSnow = 0;
    	for (GamePlayer gPlayer: game.getPlayers()) {
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
        
    	if (gamePlayer.getP() != null) {
            List<String> loreList = Lists.newLinkedList();
            SkyWarsReloaded.getIC().setOption(
    	                    gamePlayer.getP(),
    	                    1,
    	                    new ItemStack(Material.FIREWORK_CHARGE, votesForSunny),
    	                    new Messaging.MessageFormatter().format("menu.weather-sunny"),
    	                    loreList.toArray(new String[loreList.size()]));
            
            List<String> loreList3 = Lists.newLinkedList();
            SkyWarsReloaded.getIC().setOption(
    	                    gamePlayer.getP(),
    	                    3,
    	                    new ItemStack(Material.MELON_SEEDS, votesForRain),
    	                    new Messaging.MessageFormatter().format("menu.weather-rain"),
    	                    loreList3.toArray(new String[loreList3.size()]));
            
            List<String> loreList2 = Lists.newLinkedList();
            SkyWarsReloaded.getIC().setOption(
    	                    gamePlayer.getP(),
    	                    5,
    	                    new ItemStack(Material.BLAZE_ROD, (votesForThunder)),
    	                    new Messaging.MessageFormatter().format("menu.weather-thunder"),
    	                    loreList2.toArray(new String[loreList2.size()]));

            List<String> loreList4 = Lists.newLinkedList();
            SkyWarsReloaded.getIC().setOption(
    	                    gamePlayer.getP(),
    	                    7,
    	                    new ItemStack(Material.SNOW_BLOCK, (votesForSnow)),
    	                    new Messaging.MessageFormatter().format("menu.weather-snow"),
    	                    loreList4.toArray(new String[loreList4.size()]));
            
            List<String> loreList5 = Lists.newLinkedList();
            SkyWarsReloaded.getIC().setOption(
    	                    gamePlayer.getP(),
    	                    35,
    	                    new ItemStack(Material.TORCH, 1),
    	                    new Messaging.MessageFormatter().format("menu.return-to-options"),
    	                    loreList5.toArray(new String[loreList5.size()])); 
            
            SkyWarsReloaded.getIC().show(gamePlayer.getP());
    	}
    }
    
    public boolean hasWeatherPermission(Player player) {
        return player.isOp() || player.hasPermission("swr.weathervote");
    }
    
}
