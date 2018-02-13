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
import com.walrusone.skywarsreloaded.objects.ParticleItem;
import com.walrusone.skywarsreloaded.objects.PlayerStat;
import com.walrusone.skywarsreloaded.utilities.Messaging;
import com.walrusone.skywarsreloaded.utilities.Util;
import com.walrusone.skywarsreloaded.utilities.VaultUtils;

public class EffectSelectionMenu {

    private static final int menuSlotsPerRow = 9;
    private static int menuSize = 45;
    private static final String menuName = new Messaging.MessageFormatter().format("menu.useeffect-menu-title");
    
    public EffectSelectionMenu(final Player player) {
        List<ParticleItem> availableItems = SkyWarsReloaded.getLM().getParticleItems();

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
                
                String name = event.getName();
            	
                ParticleItem effect = SkyWarsReloaded.getLM().getParticleByName(name);
                if (effect == null) {
                    return;
                }   
                
                if (SkyWarsReloaded.getCfg().economyEnabled()) {
                	 if (level < effect.getLevel() && !player.hasPermission("sw.pareffect." + effect.getKey())) {
                  		Util.get().playSound(player, player.getLocation(), SkyWarsReloaded.getCfg().getErrorSound(), 1, 1);
                        return;
                     } else if (level >= effect.getLevel() && !player.hasPermission("sw.pareffect." + effect.getKey()) && !VaultUtils.get().canBuy(player, effect.getCost())) {
                    	Util.get().playSound(player, player.getLocation(), SkyWarsReloaded.getCfg().getErrorSound(), 1, 1);
               	 		player.sendMessage(new Messaging.MessageFormatter().format("menu.insufficientfunds"));
                        event.setWillClose(true);
                        event.setWillDestroy(true);
                        return;
                     }
                } else {
                	if (level  < effect.getLevel() && !player.hasPermission("sw.pareffect." + effect.getKey())) {
                		Util.get().playSound(player, player.getLocation(), SkyWarsReloaded.getCfg().getErrorSound(), 1, 1);
                        return;
                	}
                }
        
                if (SkyWarsReloaded.getCfg().economyEnabled() && !player.hasPermission("sw.pareffect." + effect.getKey())) {
                	boolean result = VaultUtils.get().payCost(player, effect.getCost());
                	if (!result) {
                		return;
                	} else {
                		PlayerStat ps = PlayerStat.getPlayerStats(player);
                		ps.addPerm("sw.pareffect." + effect.getKey(), true);
               			player.sendMessage(new Messaging.MessageFormatter().setVariable("cost", "" + effect.getCost())
               					.setVariable("item", effect.getName()).format("menu.purchase-effect"));
                	}
                }
                event.setWillClose(true);
                event.setWillDestroy(true);
        
        		PlayerStat ps = PlayerStat.getPlayerStats(player);
            	ps.setParticleEffect(effect.getKey());
            	DataStorage.get().saveStats(ps);
            	Util.get().playSound(player, player.getLocation(), SkyWarsReloaded.getCfg().getConfirmeSelctionSound(), 1, 1);
            	player.sendMessage(new Messaging.MessageFormatter().setVariable("effect", effect.getName()).format("menu.useeffect-playermsg"));
            }
        });

        ArrayList<Integer> placement = new ArrayList<Integer>(Arrays.asList(menuSize-1, 0, 2, 4, 6, 8, 9, 11, 13, 15, 17, 18, 20, 22, 24, 26, 27, 29, 31, 33, 35, 
        		36, 38, 40, 42, 44, 45, 47, 49, 51, 53));
        
        for (int iii = 0; iii < availableItems.size(); iii ++) {
        	if (iii >= menuSize || iii > 21) {
                break;
            }

            ParticleItem effect = availableItems.get(iii);
            List<String> loreList = Lists.newLinkedList();
            ItemStack item = new ItemStack(Material.valueOf(SkyWarsReloaded.getCfg().getMaterial("nopermission")), 1);
            
            if (level  >= effect.getLevel() || player.hasPermission("sw.pareffect." + effect.getKey())) {
            	if (SkyWarsReloaded.getCfg().economyEnabled()) {
            		if (player.hasPermission("sw.pareffect." + effect.getKey()) || effect.getCost() == 0) {
            			loreList.add(new Messaging.MessageFormatter().format("menu.useeffect-seteffect"));
            			item = new ItemStack(effect.getMaterial(), 1);
            		} else {
            			loreList.add(new Messaging.MessageFormatter().setVariable("cost", "" + effect.getCost()).format("menu.cost"));
            			item = new ItemStack(effect.getMaterial(), 1);
            		}
            	} else {
                	loreList.add(new Messaging.MessageFormatter().format("menu.useeffect-seteffect"));
                	item = new ItemStack(effect.getMaterial(), 1);
            	}
            } else {
            	loreList.add(new Messaging.MessageFormatter().setVariable("level", "" + effect.getLevel()).format("menu.no-use"));
            }
  
            if (player != null) {
                SkyWarsReloaded.getIC().setOption(
                        player,
                        placement.get(iii),
                        item,
                        effect.getName(),
                        loreList.toArray(new String[loreList.size()]));
            }
         }
                
        if (player != null) {
            SkyWarsReloaded.getIC().show(player);
        }
    }
}
