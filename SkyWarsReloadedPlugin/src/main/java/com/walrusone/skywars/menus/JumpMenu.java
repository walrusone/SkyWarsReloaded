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

public class JumpMenu {
	
	private static final int menuSlotsPerRow = 9;
    private static final int menuSize = 36;
    private static final String jumpMenuName = new Messaging.MessageFormatter().format("menu.jump-menu-title");
	
    public JumpMenu(final GamePlayer gamePlayer) {
    	
        int rowCount = menuSlotsPerRow;
        while (rowCount < 36 && rowCount < menuSize) {
            rowCount += menuSlotsPerRow;
        }

        SkyWarsReloaded.getIC().create(gamePlayer.getP(), jumpMenuName, rowCount, new IconMenu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IconMenu.OptionClickEvent event) {    
                String vote = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', event.getName()));
                
                if (gamePlayer.getGame().getState() != GameState.PREGAME) {
                	return;
                }
                
            	if (!hasJumpPermission(event.getPlayer())) {
                    return;
            	} 

                event.setWillClose(false);
                event.setWillDestroy(false);
          
                int lastVote = gamePlayer.getJumpVote();
                
                if (vote.equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', new Messaging.MessageFormatter().format("menu.jump-normal"))))) {
                	gamePlayer.setJumpVote(1);
                	gamePlayer.getP().closeInventory();
                	if (gamePlayer.getGame().getState() == GameState.PREGAME) {
                    	SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
    						@Override
    						public void run() {
    		                	new MainMenu(gamePlayer);
    						}
                    	}, 2);
                	}
                } else if (vote.equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', new Messaging.MessageFormatter().format("menu.jump-highjump"))))) {
                	gamePlayer.setJumpVote(2);
                	gamePlayer.getP().closeInventory();
                	if (gamePlayer.getGame().getState() == GameState.PREGAME) {
                    	SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
    						@Override
    						public void run() {
    		                	new MainMenu(gamePlayer);
    						}
                    	}, 2);
                	}
                } else if (vote.equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', new Messaging.MessageFormatter().format("menu.jump-superjump"))))) {
                	gamePlayer.setJumpVote(3);
                	gamePlayer.getP().closeInventory();
                	if (gamePlayer.getGame().getState() == GameState.PREGAME) {
                    	SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
    						@Override
    						public void run() {
    		                	new MainMenu(gamePlayer);
    						}
                    	}, 2);
                	}
                } else if (vote.equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', new Messaging.MessageFormatter().format("menu.jump-godjump"))))) {
                	gamePlayer.setJumpVote(4);
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
                
                if (gamePlayer.getJumpVote() != lastVote) {
                	Game game = gamePlayer.getGame();
                	for (GamePlayer gPlayer: game.getPlayers()) {
                		if (gPlayer.getP() != null) {
                    		gPlayer.getP().sendMessage(new Messaging.MessageFormatter()
        					.withPrefix()
        					.setVariable("player", gamePlayer.getName())
        					.setVariable("jump", vote.toUpperCase())
        					.format("game.voted-for-jump"));
                		}
                	}
                	game.playSound(SkyWarsReloaded.getCfg().getJumpVoteSound());
                }
            }
        });
 


        Game game = gamePlayer.getGame();
        int votesForNormal = 0;
        int votesForHigh = 0;
        int votesForSuper = 0;
        int votesForGod = 0;
    	for (GamePlayer gPlayer: game.getPlayers()) {
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
        
    	if (gamePlayer.getP() != null) {
            List<String> loreList = Lists.newLinkedList();
            SkyWarsReloaded.getIC().setOption(
    	                    gamePlayer.getP(),
    	                    1,
    	                    new ItemStack(Material.DIAMOND_BOOTS, votesForNormal),
    	                    new Messaging.MessageFormatter().format("menu.jump-normal"),
    	                    loreList.toArray(new String[loreList.size()]));
            
            List<String> loreList3 = Lists.newLinkedList();
            SkyWarsReloaded.getIC().setOption(
    	                    gamePlayer.getP(),
    	                    3,
    	                    new ItemStack(Material.DIAMOND_BOOTS, votesForHigh),
    	                    new Messaging.MessageFormatter().format("menu.jump-highjump"),
    	                    loreList3.toArray(new String[loreList3.size()]));
            
            List<String> loreList2 = Lists.newLinkedList();
            SkyWarsReloaded.getIC().setOption(
    	                    gamePlayer.getP(),
    	                    5,
    	                    new ItemStack(Material.DIAMOND_BOOTS, (votesForSuper)),
    	                    new Messaging.MessageFormatter().format("menu.jump-superjump"),
    	                    loreList2.toArray(new String[loreList2.size()]));

            List<String> loreList4 = Lists.newLinkedList();
            SkyWarsReloaded.getIC().setOption(
    	                    gamePlayer.getP(),
    	                    7,
    	                    new ItemStack(Material.DIAMOND_BOOTS, (votesForGod)),
    	                    new Messaging.MessageFormatter().format("menu.jump-godjump"),
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
    
    public boolean hasJumpPermission(Player player) {
        return player.isOp() || player.hasPermission("swr.jumpvote");
    }
        
}
