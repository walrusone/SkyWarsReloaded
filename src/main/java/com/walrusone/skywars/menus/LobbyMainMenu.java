package com.walrusone.skywars.menus;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;
import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.utilities.IconMenu;
import com.walrusone.skywars.utilities.ItemUtils;
import com.walrusone.skywars.utilities.Messaging;

public class LobbyMainMenu {

    private static final int menuSlotsPerRow = 9;
    private static final int menuSize = 36;
    private static final String optionsMenuName = new Messaging.MessageFormatter().format("menu.lobbymenu-menu-title");
    private ItemStack kit;
	private ItemStack color;
    
	public LobbyMainMenu(final GamePlayer gamePlayer) {

		String kitItem = SkyWarsReloaded.get().getConfig().getString("gameItems.kitPermMenuItem");
		List<String> kitItemData = new LinkedList<String>(Arrays.asList(kitItem.split(" ")));
		kit = ItemUtils.parseItem(kitItemData);
		String colorItem = SkyWarsReloaded.get().getConfig().getString("gameItems.glassMenuItem");
		List<String> colorItemData = new LinkedList<String>(Arrays.asList(colorItem.split(" ")));
		color = ItemUtils.parseItem(colorItemData);
		
        int rowCount = menuSlotsPerRow;
        while (rowCount < 36 && rowCount < menuSize) {
            rowCount += menuSlotsPerRow;
        }

        SkyWarsReloaded.getIC().create(gamePlayer.getP(), optionsMenuName, rowCount, new IconMenu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IconMenu.OptionClickEvent event) {
            	if (gamePlayer.inGame()) {
            		gamePlayer.getP().closeInventory();
            		return;
            	}
            	
                String option = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', event.getName()));

                event.setWillClose(false);
                event.setWillDestroy(false);
                
                if (option.equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', new Messaging.MessageFormatter().format("menu.permkit-menu-title"))))) {
                	if (hasPermKitPermission(gamePlayer.getP())) {
                    	gamePlayer.getP().closeInventory();
                    	SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
    						@Override
    						public void run() {
    		                	new PermKitMenu(gamePlayer);
    						}
                    	}, 2);
                	} else {
                		return;
                	}
                } else if (option.equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', new Messaging.MessageFormatter().format("menu.buycolor-item-name"))))) {
                	if (hasColorPermission(gamePlayer.getP())) {
                    	gamePlayer.getP().closeInventory();
                    	SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
    						@Override
    						public void run() {
    		                	new BuyColorMenu(gamePlayer);
    						}
                    	}, 2);
                	}  else {
                		return;
                	}
                } else if (option.equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', new Messaging.MessageFormatter().format("menu.exit-lobby-menu"))))) {
                    	gamePlayer.getP().closeInventory();
                } else {
                	return;
                }
            }
        });

        List<String> kitLore = Lists.newLinkedList();
        if (hasPermKitPermission(gamePlayer.getP())) {
        } else {
        	kitLore.add(new Messaging.MessageFormatter().format("error.no-permission-permkits"));
        }
        
        List<String> colorLore = Lists.newLinkedList();
        if (hasColorPermission(gamePlayer.getP())) {
        } else {
        	colorLore.add(new Messaging.MessageFormatter().format("error.no-permission-colorshop"));
        }
        
    	if (gamePlayer.getP() != null && !gamePlayer.inGame()) {
    		boolean opEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.purchasePermanentKitsEnabled");
    		if (opEnabled) {
                SkyWarsReloaded.getIC().setOption(
	                    gamePlayer.getP(),
	                    2,
	                    kit,
	                    new Messaging.MessageFormatter().format("menu.permkit-item-name"),
	                    kitLore.toArray(new String[kitLore.size()]));
    		}
    		
    		boolean timeEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.purchaseColorGlassEnabled");
    		if (timeEnabled) {
                SkyWarsReloaded.getIC().setOption(
	                    gamePlayer.getP(),
	                    6,
	                    color,
	                    new Messaging.MessageFormatter().format("menu.buycolor-item-name"),
	                    colorLore.toArray(new String[colorLore.size()]));
    		}
            
            List<String> loreList5 = Lists.newLinkedList();
            SkyWarsReloaded.getIC().setOption(
    	                    gamePlayer.getP(),
    	                    35,
    	                    new ItemStack(Material.TORCH, 1),
    	                    new Messaging.MessageFormatter().format("menu.exit-lobby-menu"),
    	                    loreList5.toArray(new String[loreList5.size()])); 
            
    		
            if (!gamePlayer.inGame()) {
                SkyWarsReloaded.getIC().show(gamePlayer.getP());
            }
    	}
    }
	
    public boolean hasColorPermission(Player player) {
        return player.isOp() || SkyWarsReloaded.perms.has(player, "swr.colorshop");
    }
    
    public boolean hasPermKitPermission(Player player) {
        return player.isOp() || SkyWarsReloaded.perms.has(player, "swr.permkits");
    }
 
}
