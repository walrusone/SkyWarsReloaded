package com.walrusone.skywars.menus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;
import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.utilities.GlassColor;
import com.walrusone.skywars.utilities.IconMenu;
import com.walrusone.skywars.utilities.Messaging;

public class BuyColorMenu {

    private static final int menuSlotsPerRow = 9;
    private static final int menuSize = 36;
    private static final String menuName = new Messaging.MessageFormatter().format("menu.buycolor-menu-title");
    private static final String premissionPrefix = "swr.colorglass.";
    
    public BuyColorMenu(final GamePlayer gamePlayer) {
        List<GlassColor> availableItems = SkyWarsReloaded.getGLC().getColorItems();

        int rowCount = menuSlotsPerRow;
        while (rowCount < 36 && rowCount < menuSize) {
            rowCount += menuSlotsPerRow;
        }

        SkyWarsReloaded.getIC().create(gamePlayer.getP(), menuName, rowCount, new IconMenu.OptionClickEventHandler() {
			@Override
            public void onOptionClick(IconMenu.OptionClickEvent event) {
                if (gamePlayer.inGame()) {
                	gamePlayer.getP().closeInventory();
                	return;
                }
                   
                event.setWillClose(false);
                event.setWillDestroy(false);
                
                String name = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', event.getName()));
                if (name.equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', new Messaging.MessageFormatter().format("menu.return-to-lobbymenu"))))) {
                	gamePlayer.getP().closeInventory();
                	if (!gamePlayer.inGame()) {
                    	SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
    						@Override
    						public void run() {
    		                	new LobbyMainMenu(gamePlayer);
    						}
                    	}, 2);
                	}
                	return;
                }
                
            	GlassColor glass = SkyWarsReloaded.getGLC().getByName(name);

            	if (glass == null) {
                    return;
                }
                
            	if (!canPurchase(gamePlayer, glass)) {
            		event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("error.not-enough-balance-shop"));
            		return;
            	} else {
                	if (!hasColorPermission(gamePlayer, glass)) {
                    	removeBalance(gamePlayer, glass.getCost());
                    	gamePlayer.addPerm(premissionPrefix + glass.getColor().toLowerCase());
                    	SkyWarsReloaded.getScore().getScoreboard(event.getPlayer());
                    	if (!gamePlayer.inGame()) {
                        	SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
        						@Override
        						public void run() {
        	                    	updateBuyColorMenu(gamePlayer);
        						}
                        	}, 2);
                    	}
                	} 
            	}
            }
        });

        ArrayList<Integer> placement = new ArrayList<Integer>(Arrays.asList(0, 2, 4, 6, 8, 9, 11, 13, 15, 17, 18, 20, 22, 24, 26, 27));
        
        for (int iii = 0; iii < availableItems.size(); iii ++) {
            if (iii >= menuSize) {
                break;
            }

            GlassColor glass = availableItems.get(iii);
            List<String> loreList = Lists.newLinkedList();
            if (glass.getCost() != -1) {
                if (!gamePlayer.inGame()) {
                    if (!hasColorPermission(gamePlayer, glass)) {
                    	if (SkyWarsReloaded.getCfg().usingExternalEcomony()) {
                    		loreList.add("\247r\2476Price\247f: \247" + (SkyWarsReloaded.econ.getBalance(gamePlayer.getP()) >= glass.getCost() ? 'a' : 'c') + glass.getCost());
                    	} else {
                    		loreList.add("\247r\2476Price\247f: \247" + (gamePlayer.getBalance() >= glass.getCost() ? 'a' : 'c') + glass.getCost());
                    	}
                        loreList.add(" ");
                    } else {
                    	loreList.add(new Messaging.MessageFormatter().format("menu.buycolor-purchased"));
                    }            
                    
                    if (gamePlayer.getP() != null) {
                        SkyWarsReloaded.getIC().setOption(
                                gamePlayer.getP(),
                                placement.get(iii),
                                glass.getItem().clone(),
                                glass.getName(),
                                loreList.toArray(new String[loreList.size()]));
                    }
                }
            }
         }
        
        List<String> loreList5 = Lists.newLinkedList();
        SkyWarsReloaded.getIC().setOption(
	                    gamePlayer.getP(),
	                    35,
	                    new ItemStack(Material.TORCH, 1),
	                    new Messaging.MessageFormatter().format("menu.return-to-lobbymenu"),
	                    loreList5.toArray(new String[loreList5.size()])); 
        
        if (!gamePlayer.inGame()) {
            SkyWarsReloaded.getIC().show(gamePlayer.getP());
        }
    }
    
    public void updateBuyColorMenu(final GamePlayer gamePlayer) {
		if (!SkyWarsReloaded.getIC().has(gamePlayer.getP()) || gamePlayer.inGame() || !SkyWarsReloaded.getIC().getMenu(gamePlayer.getP()).getName().equalsIgnoreCase(menuName)) {
			gamePlayer.getP().closeInventory();
			return;
		} else {
	        List<GlassColor> availableItems = SkyWarsReloaded.getGLC().getColorItems();
	        ArrayList<Integer> placement = new ArrayList<Integer>(Arrays.asList(0, 2, 4, 6, 8, 9, 11, 13, 15, 17, 18, 20, 22, 24, 26, 27));
	        
	        for (int iii = 0; iii < availableItems.size(); iii ++) {
	            if (iii >= menuSize) {
	                break;
	            }

	            GlassColor glass = availableItems.get(iii);
	            List<String> loreList = Lists.newLinkedList();
	            if (glass.getCost() != -1) {
	                if (!gamePlayer.inGame()) {
	                    if (!hasColorPermission(gamePlayer, glass)) {
	                    	if (SkyWarsReloaded.getCfg().usingExternalEcomony()) {
	                    		loreList.add("\247r\2476Price\247f: \247" + (SkyWarsReloaded.econ.getBalance(gamePlayer.getP()) >= glass.getCost() ? 'a' : 'c') + glass.getCost());
	                    	} else {
	                    		loreList.add("\247r\2476Price\247f: \247" + (gamePlayer.getBalance() >= glass.getCost() ? 'a' : 'c') + glass.getCost());
	                    	}
	                        loreList.add(" ");
	                    } else {
	                    	loreList.add(new Messaging.MessageFormatter().format("menu.buycolor-purchased"));
	                    }            
	                    
	                    if (gamePlayer.getP() != null) {
	                        SkyWarsReloaded.getIC().setOption(
	                                gamePlayer.getP(),
	                                placement.get(iii),
	                                glass.getItem().clone(),
	                                glass.getName(),
	                                loreList.toArray(new String[loreList.size()]));
	                    }
	                }
	            }
	         }
	        
	        List<String> loreList5 = Lists.newLinkedList();
	        SkyWarsReloaded.getIC().setOption(
		                    gamePlayer.getP(),
		                    35,
		                    new ItemStack(Material.TORCH, 1),
		                    new Messaging.MessageFormatter().format("menu.return-to-lobbymenu"),
		                    loreList5.toArray(new String[loreList5.size()])); 
	        if (gamePlayer.getP() != null) {
		        SkyWarsReloaded.getIC().update(gamePlayer.getP());
	        }
		}
    }
    
    public boolean hasPermission(Player player) {
        return player.isOp() || player.hasPermission("swr.colorshop");
    }
    
    public boolean hasColorPermission(GamePlayer player, GlassColor color) {
    	return (player.getP().isOp() || player.getP().hasPermission(premissionPrefix + color.getColor()) || player.hasPerm(premissionPrefix + color.getColor().toLowerCase()));
    }
    
    public boolean canPurchase(GamePlayer gamePlayer, GlassColor gColor) {
    	if (SkyWarsReloaded.getCfg().usingExternalEcomony()) {
            return gColor.getCost() > 0 && (SkyWarsReloaded.econ.getBalance(gamePlayer.getP()) >= gColor.getCost());
    	} else {
    		return gColor.getCost() > 0 && (gamePlayer.getBalance() >= gColor.getCost());
    	}
    }
    
    private void removeBalance(GamePlayer p, int x) {
    	if (SkyWarsReloaded.getCfg().usingExternalEcomony()) {
    		SkyWarsReloaded.econ.withdrawPlayer(p.getP(), x);
    	} else {
    		p.setBalance(p.getBalance() - x);
    	}
    }
}
