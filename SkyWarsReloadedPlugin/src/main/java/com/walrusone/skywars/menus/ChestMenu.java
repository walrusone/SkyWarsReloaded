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

public class ChestMenu {

	 private static final int menuSlotsPerRow = 9;
	 private static final int menuSize = 36;
	 private static final String menuName = new Messaging.MessageFormatter().format("menu.chest-menu-title");
  
    public ChestMenu(final GamePlayer gamePlayer) {

    	int rowCount = menuSlotsPerRow;
        while (rowCount < 36 && rowCount < menuSize) {
            rowCount += menuSlotsPerRow;
        }

        SkyWarsReloaded.getIC().create(gamePlayer.getP(), menuName, rowCount, new IconMenu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IconMenu.OptionClickEvent event) {    
            	String vote = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', event.getName()));

                event.setWillClose(false);
                event.setWillDestroy(false);
                
                if (gamePlayer.getGame().getState() != GameState.PREGAME) {
                	return;
                }
                
            	if (!hasChestPermission(event.getPlayer())) {
                    return;
            	} 
            	
                int lastVote = gamePlayer.getOpVote();
                
                if (vote.equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', new Messaging.MessageFormatter().format("menu.chest-basic"))))) {
                 	gamePlayer.setOpVote(1);
                	gamePlayer.getP().closeInventory();
                	if (gamePlayer.getGame().getState() == GameState.PREGAME) {
                    	SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
    						@Override
    						public void run() {
    		                	new MainMenu(gamePlayer);
    						}
                    	}, 2);
                	}
                } else if (vote.equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', new Messaging.MessageFormatter().format("menu.chest-normal"))))) {
                	gamePlayer.setOpVote(2);
                	gamePlayer.getP().closeInventory();
                	if (gamePlayer.getGame().getState() == GameState.PREGAME) {
                    	SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
    						@Override
    						public void run() {
    		                	new MainMenu(gamePlayer);
    						}
                    	}, 2);
                	}
                } else if (vote.equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', new Messaging.MessageFormatter().format("menu.chest-op"))))) {
                	gamePlayer.setOpVote(3);
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
                
                if (gamePlayer.getOpVote() != lastVote) {
                	Game game = gamePlayer.getGame();
                	for (GamePlayer gPlayer: game.getPlayers()) {
                		if (gPlayer.getP() != null) {
                    		gPlayer.getP().sendMessage(new Messaging.MessageFormatter()
        					.withPrefix()
        					.setVariable("player", gamePlayer.getName())
        					.setVariable("type", vote.toUpperCase())
        					.format("game.voted-for-chests"));
                		}
                	}
                	game.playSound(SkyWarsReloaded.getCfg().getChestVoteSound());
                } 
            }
        });

        Game game = gamePlayer.getGame();
        int votesForBasic = 0;
        int votesForNormal = 0;
        int votesForOP = 0;

    	for (GamePlayer gPlayer: game.getPlayers()) {
    		if (gPlayer.getOpVote() == 1) {
    			votesForBasic++;
    		} else if (gPlayer.getOpVote() == 2) {
    			votesForNormal++;
    		} else if (gPlayer.getOpVote() == 3) {
    			votesForOP++;
    		} 
    	}
        
    	if (gamePlayer.getP() != null) {
            List<String> loreList = Lists.newLinkedList();
            SkyWarsReloaded.getIC().setOption(
    	                    gamePlayer.getP(),
    	                    1,
    	                    new ItemStack(Material.CHEST, votesForBasic),
    	                    new Messaging.MessageFormatter().format("menu.chest-basic"),
    	                    loreList.toArray(new String[loreList.size()]));
            
            List<String> loreList3 = Lists.newLinkedList();
            SkyWarsReloaded.getIC().setOption(
    	                    gamePlayer.getP(),
    	                    4,
    	                    new ItemStack(Material.CHEST, votesForNormal),
    	                    new Messaging.MessageFormatter().format("menu.chest-normal"),
    	                    loreList3.toArray(new String[loreList3.size()]));
            
            List<String> loreList2 = Lists.newLinkedList();
            SkyWarsReloaded.getIC().setOption(
    	                    gamePlayer.getP(),
    	                    7,
    	                    new ItemStack(Material.CHEST, (votesForOP)),
    	                    new Messaging.MessageFormatter().format("menu.chest-op"),
    	                    loreList2.toArray(new String[loreList2.size()]));

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
    
    public boolean hasChestPermission(Player player) {
        return player.isOp() || player.hasPermission("swr.opchest");
    }
}
