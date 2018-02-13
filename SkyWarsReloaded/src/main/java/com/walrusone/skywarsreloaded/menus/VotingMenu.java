package com.walrusone.skywarsreloaded.menus;

import java.util.List;

import org.bukkit.entity.Player;

import com.google.common.collect.Lists;
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

        SkyWarsReloaded.getIC().create(player, menuName, menuSize, new IconMenu.OptionClickEventHandler() {
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
                event.setWillClose(false);
                event.setWillDestroy(true); 
            }
        });
        
        List<String> loreList = Lists.newLinkedList();

        if (SkyWarsReloaded.getCfg().isChestVoteEnabled()) {
        	if (player != null) {
        		if (player.hasPermission("sw.chestvote")) {
        			loreList = SkyWarsReloaded.getIM().getItem("chestvote").getItemMeta().getLore();
                    SkyWarsReloaded.getIC().setOption(
                            player,
                            SkyWarsReloaded.getCfg().getChestVotePos(),
                            SkyWarsReloaded.getIM().getItem("chestvote"),
                            SkyWarsReloaded.getNMS().getItemName(SkyWarsReloaded.getIM().getItem("chestvote")),
                            loreList.toArray(new String[loreList.size()]));
                } else {
                	 loreList = SkyWarsReloaded.getIM().getItem("nopermission").getItemMeta().getLore();
                     SkyWarsReloaded.getIC().setOption(
                             player,
                             SkyWarsReloaded.getCfg().getChestVotePos(),
                             SkyWarsReloaded.getIM().getItem("nopermission"),
                             SkyWarsReloaded.getNMS().getItemName(SkyWarsReloaded.getIM().getItem("nopermission")),
                             loreList.toArray(new String[loreList.size()]));
                }
        	}
        }
        if (SkyWarsReloaded.getCfg().isTimeVoteEnabled()) {
        	if (player != null) {
        		if (player.hasPermission("sw.timevote")) {
        			loreList = SkyWarsReloaded.getIM().getItem("timevote").getItemMeta().getLore();
                    SkyWarsReloaded.getIC().setOption(
                            player,
                            SkyWarsReloaded.getCfg().getTimeVotePos(),
                            SkyWarsReloaded.getIM().getItem("timevote"),
                            SkyWarsReloaded.getNMS().getItemName(SkyWarsReloaded.getIM().getItem("timevote")),
                            loreList.toArray(new String[loreList.size()]));
                } else {
                	 loreList = SkyWarsReloaded.getIM().getItem("nopermission").getItemMeta().getLore();
                     SkyWarsReloaded.getIC().setOption(
                             player,
                             SkyWarsReloaded.getCfg().getTimeVotePos(),
                             SkyWarsReloaded.getIM().getItem("nopermission"),
                             SkyWarsReloaded.getNMS().getItemName(SkyWarsReloaded.getIM().getItem("nopermission")),
                             loreList.toArray(new String[loreList.size()]));
            	}
            }
        }
        if (SkyWarsReloaded.getCfg().isWeatherVoteEnabled()) {
        	if (player != null) {
        		if (player.hasPermission("sw.weathervote")) {
        			loreList = SkyWarsReloaded.getIM().getItem("weathervote").getItemMeta().getLore();
                    SkyWarsReloaded.getIC().setOption(
                            player,
                            SkyWarsReloaded.getCfg().getWeatherVotePos(),
                            SkyWarsReloaded.getIM().getItem("weathervote"),
                            SkyWarsReloaded.getNMS().getItemName(SkyWarsReloaded.getIM().getItem("weathervote")),
                            loreList.toArray(new String[loreList.size()]));
                } else {
                	 loreList = SkyWarsReloaded.getIM().getItem("nopermission").getItemMeta().getLore();
                     SkyWarsReloaded.getIC().setOption(
                             player,
                             SkyWarsReloaded.getCfg().getWeatherVotePos(),
                             SkyWarsReloaded.getIM().getItem("nopermission"),
                             SkyWarsReloaded.getNMS().getItemName(SkyWarsReloaded.getIM().getItem("nopermission")),
                             loreList.toArray(new String[loreList.size()]));
                }
        	}
        }
        if (SkyWarsReloaded.getCfg().isModifierVoteEnabled()) {
        	if (player != null) {
        		if (player.hasPermission("sw.modifiervote")) {
        			loreList = SkyWarsReloaded.getIM().getItem("modifiervote").getItemMeta().getLore();
                    SkyWarsReloaded.getIC().setOption(
                            player,
                            SkyWarsReloaded.getCfg().getModifierVotePos(),
                            SkyWarsReloaded.getIM().getItem("modifiervote"),
                            SkyWarsReloaded.getNMS().getItemName(SkyWarsReloaded.getIM().getItem("modifiervote")),
                            loreList.toArray(new String[loreList.size()]));
                } else {
                	 loreList = SkyWarsReloaded.getIM().getItem("nopermission").getItemMeta().getLore();
                     SkyWarsReloaded.getIC().setOption(
                             player,
                             SkyWarsReloaded.getCfg().getModifierVotePos(),
                             SkyWarsReloaded.getIM().getItem("nopermission"),
                             SkyWarsReloaded.getNMS().getItemName(SkyWarsReloaded.getIM().getItem("nopermission")),
                             loreList.toArray(new String[loreList.size()]));
                }
        	}
        }
        
        loreList = SkyWarsReloaded.getIM().getItem("exitMenuItem").getItemMeta().getLore();
        SkyWarsReloaded.getIC().setOption(
                player,
                menuSize-1,
                SkyWarsReloaded.getIM().getItem("exitMenuItem"),
                SkyWarsReloaded.getNMS().getItemName(SkyWarsReloaded.getIM().getItem("exitMenuItem")),
                loreList.toArray(new String[loreList.size()]));
        
        if (player != null) {
            SkyWarsReloaded.getIC().show(player);
        }
    }
}
