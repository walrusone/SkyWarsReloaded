package com.walrusone.skywarsreloaded.menus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;
import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.database.DataStorage;
import com.walrusone.skywarsreloaded.objects.PlayerStat;
import com.walrusone.skywarsreloaded.objects.Taunt;
import com.walrusone.skywarsreloaded.utilities.Messaging;
import com.walrusone.skywarsreloaded.utilities.Util;
import com.walrusone.skywarsreloaded.utilities.VaultUtils;

public class TauntSelectionMenu {

    private static final int menuSlotsPerRow = 9;
    private static int menuSize = 45;
    private static final String menuName = new Messaging.MessageFormatter().format("menu.usetaunt-menu-title");
    
    public TauntSelectionMenu(final Player player) {
    	List<Taunt> availableItems = SkyWarsReloaded.getLM().getTaunts();

        int rowCount = menuSlotsPerRow;
        double numRows = availableItems.size() / 5.0;
        for (int i = 0; i < Math.ceil(numRows)-1; i++) {
        	rowCount += menuSlotsPerRow;
        }
        menuSize = rowCount;
        int level = Util.get().getPlayerLevel(player);
        SkyWarsReloaded.getIC().create(player, menuName, rowCount, new IconMenu.OptionClickEventHandler() {
			@Override
            public void onOptionClick(IconMenu.OptionClickEvent event) {
                           	
				String tauntName = event.getName();
				Taunt taunt = SkyWarsReloaded.getLM().getTauntFromName(tauntName);
	            if (taunt == null) {
	              	return;
	            }           
	            
	            if (SkyWarsReloaded.getCfg().economyEnabled()) {
               	 	if (level < taunt.getLevel() && !player.hasPermission("sw.taunt." + taunt.getKey())) {
                 		Util.get().playSound(player, player.getLocation(), SkyWarsReloaded.getCfg().getErrorSound(), 1, 1);
                 		return;
               	 	} else if (level >= taunt.getLevel() && !player.hasPermission("sw.taunt."+ taunt.getKey()) && !VaultUtils.get().canBuy(player, taunt.getCost())) {
               	 		Util.get().playSound(player, player.getLocation(), SkyWarsReloaded.getCfg().getErrorSound(), 1, 1);
               	 		player.sendMessage(new Messaging.MessageFormatter().format("menu.insufficientfunds"));
                        event.setWillClose(true);
                        event.setWillDestroy(true);
                      	return;
                    }
                } else {
               		if (level < taunt.getLevel() && !player.hasPermission("sw.taunt." + taunt.getKey())) {
               		Util.get().playSound(player, player.getLocation(), SkyWarsReloaded.getCfg().getErrorSound(), 1, 1);
                    return;
               		}
                }
       
                if (SkyWarsReloaded.getCfg().economyEnabled() && !player.hasPermission("sw.taunt." + taunt.getKey())) {
               		boolean result = VaultUtils.get().payCost(player, taunt.getCost());
               		if (!result) {
               			return;
               		} else {
               			PlayerStat ps = PlayerStat.getPlayerStats(player);
               			ps.addPerm("sw.taunt." + taunt.getKey(), true);
               			player.sendMessage(new Messaging.MessageFormatter().setVariable("cost", "" + taunt.getCost())
               					.setVariable("item", taunt.getName()).format("menu.purchase-taunt"));
               		}
                }
                
                event.setWillClose(true);
                event.setWillDestroy(true);
            
           		PlayerStat ps = PlayerStat.getPlayerStats(player);
               	ps.setTaunt(taunt.getKey());
               	DataStorage.get().saveStats(ps);
               	Util.get().playSound(player, player.getLocation(), SkyWarsReloaded.getCfg().getConfirmeSelctionSound(), 1, 1);
               	player.sendMessage(new Messaging.MessageFormatter().setVariable("taunt", taunt.getName()).format("menu.usetaunt-playermsg"));
            }
        });

        ArrayList<Integer> placement = new ArrayList<Integer>(Arrays.asList(menuSize-1, 0, 2, 4, 6, 8, 9, 11, 13, 15, 17, 18, 20, 22, 24, 26, 27, 29, 31, 33, 35, 
        		36, 38, 40, 42, 44, 45, 47, 49, 51, 53));
        
        for (int iii = 0; iii < availableItems.size(); iii ++) {
        	if (iii >= menuSize || iii > 21) {
                break;
            }

            Taunt taunt = availableItems.get(iii);
            List<String> loreList = Lists.newLinkedList();
            ItemStack item = new ItemStack(Material.valueOf(SkyWarsReloaded.getCfg().getMaterial("nopermission")), 1);
            
            if (level >= taunt.getLevel() || player.hasPermission("sw.taunt." + taunt.getKey())) {
            	if (SkyWarsReloaded.getCfg().economyEnabled()) {
            		if (player.hasPermission("sw.taunt." + taunt.getKey()) || taunt.getCost() == 0) {
            			loreList.add(new Messaging.MessageFormatter().format("menu.usetaunt-settaunt"));
            			item = new ItemStack(taunt.getMaterial(), 1);
            		} else {
            			loreList.add(new Messaging.MessageFormatter().setVariable("cost", "" + taunt.getCost()).format("menu.cost"));
            			item = new ItemStack(taunt.getMaterial(), 1);
            		}
            	} else {
                	loreList.add(new Messaging.MessageFormatter().format("menu.usetaunt-settaunt"));
                	item = new ItemStack(taunt.getMaterial(), 1);
            	}
            } else {
            	loreList.add(new Messaging.MessageFormatter().setVariable("level", "" + taunt.getLevel()).format("menu.no-use"));
            }
  
            if (player != null) {
                SkyWarsReloaded.getIC().setOption(
                        player,
                        placement.get(iii),
                        item,
                        taunt.getName(),
                        loreList.toArray(new String[loreList.size()]));
            }
         }
                
        if (player != null) {
            SkyWarsReloaded.getIC().show(player);
        }
    }
}
