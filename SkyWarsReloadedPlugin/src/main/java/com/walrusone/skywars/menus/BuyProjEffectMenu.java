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
import com.walrusone.skywars.utilities.IconMenu;
import com.walrusone.skywars.utilities.Messaging;
import com.walrusone.skywars.utilities.ParticleItem;

public class BuyProjEffectMenu {

    private static final int menuSlotsPerRow = 9;
    private static final int menuSize = 45;
    private static final String menuName = new Messaging.MessageFormatter().format("menu.buyprojeffect-menu-title");
    private static final String premissionPrefix = "swr.projeffect.";
    
    public BuyProjEffectMenu(final GamePlayer gamePlayer) {
        List<ParticleItem> availableItems = SkyWarsReloaded.getProjC().getParticleItems();

        int rowCount = menuSlotsPerRow;
        while (rowCount < 45 && rowCount < menuSize) {
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
                
            	ParticleItem effect = SkyWarsReloaded.getProjC().getByName(name);

            	if (effect == null) {
                    return;
                }
                
            	if (!canPurchase(gamePlayer, effect)) {
            		event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("error.not-enough-balance-shop"));
            		return;
            	} else {
                	if (!hasEffectPermission(gamePlayer, effect)) {
                    	removeBalance(gamePlayer, effect.getCost());
                    	gamePlayer.addPerm(premissionPrefix + effect.getEffect().toLowerCase());
                    	SkyWarsReloaded.getScore().getScoreboard(event.getPlayer());
                    	if (!gamePlayer.inGame()) {
                        	SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
        						@Override
        						public void run() {
        	                    	updateBuyEffectMenu(gamePlayer);
        						}
                        	}, 2);
                    	}
                	} 
            	}
            }
        });

        ArrayList<Integer> placement = new ArrayList<Integer>(Arrays.asList(0, 2, 4, 6, 8, 9, 11, 13, 15, 17, 18, 20, 22, 24, 26, 27, 29, 31, 33, 35));
		
        for (int iii = 0; iii < availableItems.size(); iii ++) {
            if (iii >= menuSize) {
                break;
            }

            ParticleItem effect = availableItems.get(iii);
            List<String> loreList = Lists.newLinkedList();
            if (effect.getCost() != -1) {
                if (!gamePlayer.inGame()) {
                    if (!hasEffectPermission(gamePlayer, effect)) {
                    	if (SkyWarsReloaded.getCfg().usingExternalEcomony()) {
                    		loreList.add("\247r\2476Price\247f: \247" + (SkyWarsReloaded.econ.getBalance(gamePlayer.getP()) >= effect.getCost() ? 'a' : 'c') + effect.getCost());
                    	} else {
                    		loreList.add("\247r\2476Price\247f: \247" + (gamePlayer.getBalance() >= effect.getCost() ? 'a' : 'c') + effect.getCost());
                    	}
                        loreList.add(" ");
                    } else {
                    	loreList.add(new Messaging.MessageFormatter().format("menu.buyprojeffect-purchased"));
                    }            
                    
                    if (gamePlayer.getP() != null) {
                        SkyWarsReloaded.getIC().setOption(
                                gamePlayer.getP(),
                                placement.get(iii),
                                SkyWarsReloaded.getCfg().getTrailEffectsMenuItem(),
                                effect.getName(),
                                loreList.toArray(new String[loreList.size()]));
                    }
                }
            }
         }
        
        List<String> loreList5 = Lists.newLinkedList();
        SkyWarsReloaded.getIC().setOption(
	                    gamePlayer.getP(),
	                    44,
	                    new ItemStack(Material.TORCH, 1),
	                    new Messaging.MessageFormatter().format("menu.return-to-lobbymenu"),
	                    loreList5.toArray(new String[loreList5.size()])); 
        
        if (!gamePlayer.inGame()) {
            SkyWarsReloaded.getIC().show(gamePlayer.getP());
        }
    }
    
    public void updateBuyEffectMenu(final GamePlayer gamePlayer) {
		if (!SkyWarsReloaded.getIC().has(gamePlayer.getP()) || gamePlayer.inGame() || !SkyWarsReloaded.getIC().getMenu(gamePlayer.getP()).getName().equalsIgnoreCase(menuName)) {
			gamePlayer.getP().closeInventory();
			return;
		} else {
	        List<ParticleItem> availableItems = SkyWarsReloaded.getProjC().getParticleItems();
	        ArrayList<Integer> placement = new ArrayList<Integer>(Arrays.asList(0, 2, 4, 6, 8, 9, 11, 13, 15, 17, 18, 20, 22, 24, 26, 27, 29, 31, 33, 35));
			
	        for (int iii = 0; iii < availableItems.size(); iii ++) {
	            if (iii >= menuSize) {
	                break;
	            }

	            ParticleItem effect = availableItems.get(iii);
	            List<String> loreList = Lists.newLinkedList();
	            if (effect.getCost() != -1) {
	                if (!gamePlayer.inGame()) {
	                    if (!hasEffectPermission(gamePlayer, effect)) {
	                    	if (SkyWarsReloaded.getCfg().usingExternalEcomony()) {
	                    		loreList.add("\247r\2476Price\247f: \247" + (SkyWarsReloaded.econ.getBalance(gamePlayer.getP()) >= effect.getCost() ? 'a' : 'c') + effect.getCost());
	                    	} else {
	                    		loreList.add("\247r\2476Price\247f: \247" + (gamePlayer.getBalance() >= effect.getCost() ? 'a' : 'c') + effect.getCost());
	                    	}
	                        loreList.add(" ");
	                    } else {
	                    	loreList.add(new Messaging.MessageFormatter().format("menu.buyprojeffect-purchased"));
	                    }            
	                    
	                    if (gamePlayer.getP() != null) {
	                        SkyWarsReloaded.getIC().setOption(
	                                gamePlayer.getP(),
	                                placement.get(iii),
	                                SkyWarsReloaded.getCfg().getTrailEffectsMenuItem(),
	                                effect.getName(),
	                                loreList.toArray(new String[loreList.size()]));
	                    }
	                }
	            }
	         }
	        
	        List<String> loreList5 = Lists.newLinkedList();
	        SkyWarsReloaded.getIC().setOption(
		                    gamePlayer.getP(),
		                    44,
		                    new ItemStack(Material.TORCH, 1),
		                    new Messaging.MessageFormatter().format("menu.return-to-lobbymenu"),
		                    loreList5.toArray(new String[loreList5.size()])); 
	        
	        if (!gamePlayer.inGame()) {
	            SkyWarsReloaded.getIC().update(gamePlayer.getP());
	        }
		}
    }
    
    public boolean hasPermission(Player player) {
        return player.isOp() || player.hasPermission("swr.projeffectshop");
    }
    
    public boolean hasEffectPermission(GamePlayer player, ParticleItem effect) {
    	return (player.getP().isOp() || player.getP().hasPermission(premissionPrefix + effect.getEffect().toLowerCase()) || player.hasPerm(premissionPrefix + effect.getEffect().toLowerCase()));
    }
    
    public boolean canPurchase(GamePlayer gamePlayer, ParticleItem effect) {
    	if (SkyWarsReloaded.getCfg().usingExternalEcomony()) {
            return effect.getCost() > 0 && (SkyWarsReloaded.econ.getBalance(gamePlayer.getP()) >= effect.getCost());
    	} else {
    		return effect.getCost() > 0 && (gamePlayer.getBalance() >= effect.getCost());
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
