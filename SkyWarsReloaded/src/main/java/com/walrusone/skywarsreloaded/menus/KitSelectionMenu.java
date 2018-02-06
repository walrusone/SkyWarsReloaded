package com.walrusone.skywarsreloaded.menus;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;
import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.objects.GameKit;
import com.walrusone.skywarsreloaded.objects.GameMap;
import com.walrusone.skywarsreloaded.utilities.Messaging;
import com.walrusone.skywarsreloaded.utilities.Util;

public class KitSelectionMenu {

    private static int menuSize = 45;
    private static final String menuName = new Messaging.MessageFormatter().format("menu.kit-section-mnenu");
    
    public KitSelectionMenu(final Player player) {
    	GameMap gMap = MatchManager.get().getPlayerMap(player);
        List<GameKit> availableItems = GameKit.getAvailableKits();
        
        if (gMap != null) {
        	SkyWarsReloaded.getIC().create(player, menuName, menuSize, new IconMenu.OptionClickEventHandler() {
    			@Override
                public void onOptionClick(IconMenu.OptionClickEvent event) {
                    String name = event.getName();
                	
                    GameKit kit = GameKit.getKit(name);
                    if (kit == null) {
                        return;
                    }           
                    
                	if (kit.needPermission()) {
                		if (!player.hasPermission("swr.kit." + kit.getFilename())) {
                			Util.get().playSound(player, player.getLocation(), SkyWarsReloaded.getCfg().getErrorSound(), 1, 1);
                			return;
                		}
                	}
                	
                    event.setWillClose(true);
                    event.setWillDestroy(true);
                    Util.get().playSound(player, player.getLocation(), SkyWarsReloaded.getCfg().getConfirmeSelctionSound(), 1, 1);
                    gMap.setKitVote(player, kit);
                }
            });  
        } 
        
        for (int iii = 0; iii < availableItems.size(); iii ++) {
            if (iii >= menuSize || iii > 21) {
                break;
            }

            GameKit kit = availableItems.get(iii);
            List<String> loreList = Lists.newLinkedList();
            ItemStack item = kit.getLIcon();
            
            boolean hasPermission = true;
            if (kit.needPermission()) {
            	if (!player.hasPermission("swr.kit." + kit.getFilename())) {
        			loreList.add(kit.getColoredLockedLore());
        			hasPermission = false;
        		}
            }
            
            if (hasPermission) {
            	loreList.addAll(kit.getColorLores());
            	item = kit.getIcon();
            }
                 
            if (player != null) {
                SkyWarsReloaded.getIC().setOption(
                        player,
                        kit.getPosition(),
                        item,
                        kit.getName(),
                        loreList.toArray(new String[loreList.size()]));
            }
         }
                
        if (player != null) {
            SkyWarsReloaded.getIC().show(player);
        }
    }
}
