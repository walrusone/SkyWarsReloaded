package com.walrusone.skywars.menus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;
import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.Game.GameState;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.utilities.IconMenu;
import com.walrusone.skywars.utilities.Messaging;
import com.walrusone.skywars.utilities.ParticleItem;

public class UseEffectMenu {

    private static final int menuSlotsPerRow = 9;
    private static final int menuSize = 45;
    private static final String menuName = new Messaging.MessageFormatter().format("menu.useeffect-menu-title");
    private static final String premissionPrefix = "swr.effect.";
    
    public UseEffectMenu(final GamePlayer gamePlayer) {
        List<ParticleItem> availableItems = SkyWarsReloaded.getPEC().getParticleItems();

        int rowCount = menuSlotsPerRow;
        while (rowCount < 45 && rowCount < menuSize) {
            rowCount += menuSlotsPerRow;
        }

        SkyWarsReloaded.getIC().create(gamePlayer.getP(), menuName, rowCount, new IconMenu.OptionClickEventHandler() {
			@Override
            public void onOptionClick(IconMenu.OptionClickEvent event) {
                if (!gamePlayer.inGame()) {
                	gamePlayer.getP().closeInventory();
                	return;
                }
                
                if (gamePlayer.getGame().getState() != GameState.PREGAME) {
                	gamePlayer.getP().closeInventory();
                	return;
                }
                
                String name = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', event.getName()));
                if (name.equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', new Messaging.MessageFormatter().format("menu.return-to-options"))))) {
                	gamePlayer.getP().closeInventory();
                	if (gamePlayer.getGame().getState() == GameState.PREGAME) {
                    	SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
    						@Override
    						public void run() {
    		                	new MainMenu(gamePlayer);
    						}
                    	}, 2);
                	}
                	return;
                }
                
                if (name.equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', new Messaging.MessageFormatter().format("effects.no-effect"))))) {
                	gamePlayer.getP().closeInventory();
            		gamePlayer.setEffect("normal");
                	if (gamePlayer.getGame().getState() == GameState.PREGAME) {
                    	SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
    						@Override
    						public void run() {
    		                	new MainMenu(gamePlayer);
    						}
                    	}, 2);
                	}
                	return;
                }
                
            	ParticleItem effect = SkyWarsReloaded.getPEC().getByName(name);
                if (effect == null) {
                    return;
                }
                
            	if (!hasEffectPermission(gamePlayer, effect)) {
            		gamePlayer.getP().closeInventory();
                    return;
            	} else {
            		gamePlayer.setEffect(effect.getEffect());
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
                return;   
            }
        });

        ArrayList<Integer> placement = new ArrayList<Integer>(Arrays.asList(0, 2, 4, 6, 8, 9, 11, 13, 15, 17, 18, 20, 22, 24, 26, 27, 29, 31, 33, 35));
		
        for (int iii = 0; iii < availableItems.size(); iii ++) {
            if (iii >= menuSize) {
                break;
            }

            ParticleItem effect = availableItems.get(iii);
            List<String> loreList = Lists.newLinkedList();
            
            if (hasEffectPermission(gamePlayer, effect)) {
            	loreList.add(new Messaging.MessageFormatter().format("menu.useeffect-seteffect"));
            } else {
            	loreList.add(new Messaging.MessageFormatter().format("menu.useeffect-no-use"));
            }
  
            if (gamePlayer.getP() != null) {
                SkyWarsReloaded.getIC().setOption(
                        gamePlayer.getP(),
                        placement.get(iii),
                        SkyWarsReloaded.getCfg().getparticleMenuItem(),
                        effect.getName(),
                        loreList.toArray(new String[loreList.size()]));
            }
         }
        
        List<String> loreList2 = Lists.newLinkedList();
        loreList2.add(new Messaging.MessageFormatter().format("menu.useeffect-seteffect"));
        SkyWarsReloaded.getIC().setOption(
	                    gamePlayer.getP(),
	                    36,
	                    new ItemStack(Material.GLASS, 1),
	                    new Messaging.MessageFormatter().format("effects.no-effect"),
	                    loreList2.toArray(new String[loreList2.size()])); 
        
        List<String> loreList5 = Lists.newLinkedList();
        SkyWarsReloaded.getIC().setOption(
	                    gamePlayer.getP(),
	                    44,
	                    new ItemStack(Material.TORCH, 1),
	                    new Messaging.MessageFormatter().format("menu.return-to-options"),
	                    loreList5.toArray(new String[loreList5.size()])); 
        
        if (gamePlayer.getP() != null) {
            SkyWarsReloaded.getIC().show(gamePlayer.getP());
        }
    }
    
    public boolean hasEffectPermission(GamePlayer player, ParticleItem effect) {
    	return (player.getP().isOp() || player.getP().hasPermission(premissionPrefix + effect.getEffect().toLowerCase()) || player.hasPerm(premissionPrefix + effect.getEffect().toLowerCase()));
    }
}
