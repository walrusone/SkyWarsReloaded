package com.walrusone.skywarsreloaded.menus;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.objects.GameMap;
import com.walrusone.skywarsreloaded.utilities.Messaging;
import com.walrusone.skywarsreloaded.utilities.Util;

public class VotingMenu {
	
    private static int menuSize = 27;
    private static final String menuName = new Messaging.MessageFormatter().format("menu.options-menu-title");
    
    public VotingMenu(final Player player) {
    	GameMap gMap = MatchManager.get().getPlayerMap(player);
    	if (gMap != null) {
    		Inventory inv = Bukkit.createInventory(null, menuSize, menuName);

            if (SkyWarsReloaded.getCfg().isChestVoteEnabled()) {
            	if (player != null) {
            		if (player.hasPermission("sw.chestvote")) {
            			inv.setItem(SkyWarsReloaded.getCfg().getChestVotePos(), SkyWarsReloaded.getIM().getItem("chestvote"));
                    } else {
                    	inv.setItem(SkyWarsReloaded.getCfg().getChestVotePos(), SkyWarsReloaded.getIM().getItem("nopermission"));
                    }
            	}
            }
            if (SkyWarsReloaded.getCfg().isTimeVoteEnabled()) {
            	if (player != null) {
            		if (player.hasPermission("sw.timevote")) {
            			inv.setItem(SkyWarsReloaded.getCfg().getTimeVotePos(), SkyWarsReloaded.getIM().getItem("timevote"));
                    } else {
                    	inv.setItem(SkyWarsReloaded.getCfg().getTimeVotePos(), SkyWarsReloaded.getIM().getItem("nopermission"));
                    }
            	}
            }
            if (SkyWarsReloaded.getCfg().isWeatherVoteEnabled()) {
            	if (player != null) {
            		if (player.hasPermission("sw.weathervote")) {
            			inv.setItem(SkyWarsReloaded.getCfg().getWeatherVotePos(), SkyWarsReloaded.getIM().getItem("weathervote"));
                    } else {
                    	inv.setItem(SkyWarsReloaded.getCfg().getWeatherVotePos(), SkyWarsReloaded.getIM().getItem("nopermission"));
                    }
            	}
            }
            if (SkyWarsReloaded.getCfg().isModifierVoteEnabled()) {
            	if (player != null) {
            		if (player.hasPermission("sw.modifiervote")) {
            			inv.setItem(SkyWarsReloaded.getCfg().getModifierVotePos(), SkyWarsReloaded.getIM().getItem("modifiervote"));
                    } else {
                    	inv.setItem(SkyWarsReloaded.getCfg().getModifierVotePos(), SkyWarsReloaded.getIM().getItem("nopermission"));
                    }
            	}
            }
            
            inv.setItem(menuSize-1, SkyWarsReloaded.getIM().getItem("exitMenuItem"));
      	
        	
            SkyWarsReloaded.getIC().create(player, inv, new IconMenu.OptionClickEventHandler() {
    			@Override
                public void onOptionClick(IconMenu.OptionClickEvent event) {
                    String name = event.getName();

                    if (name.equalsIgnoreCase(new Messaging.MessageFormatter().format("items.chest-item"))) {
	                	player.openInventory(gMap.getChestVoteMenu());
	                	Util.get().playSound(player, player.getLocation(), SkyWarsReloaded.getCfg().getOpenChestMenuSound(), 1, 1);
                    } else if (name.equalsIgnoreCase(new Messaging.MessageFormatter().format("items.time-item"))) {
	                	player.openInventory(gMap.getTimeVoteMenu());
	                	Util.get().playSound(player, player.getLocation(), SkyWarsReloaded.getCfg().getOpenTimeMenuSound(), 1, 1);
                    } else if (name.equalsIgnoreCase(new Messaging.MessageFormatter().format("items.weather-item"))) {
	                	player.openInventory(gMap.getWeatherVoteMenu());
	                	Util.get().playSound(player, player.getLocation(), SkyWarsReloaded.getCfg().getOpenWeatherMenuSound(), 1, 1);
                    } else if (name.equalsIgnoreCase(new Messaging.MessageFormatter().format("items.modifier-item"))) {
	                	player.openInventory(gMap.getModifierVoteMenu());
	                	Util.get().playSound(player, player.getLocation(), SkyWarsReloaded.getCfg().getOpenModifierMenuSound(), 1, 1);
                    } else if (name.equalsIgnoreCase(new Messaging.MessageFormatter().format("items.exit-menu-item"))) {
                    	player.closeInventory();
                    } else {
                    	return;
                    }
                }
            });
            
            if (player != null) {
                SkyWarsReloaded.getIC().show(player);
            }
    	}
    	
    }
}
