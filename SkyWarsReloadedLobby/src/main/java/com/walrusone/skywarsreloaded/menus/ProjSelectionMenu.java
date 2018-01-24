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

public class ProjSelectionMenu {

    private static final int menuSlotsPerRow = 9;
    private static final int menuSize = 45;
    private static final String menuName = new Messaging.MessageFormatter().format("menu.useprojeffect-menu-title");
    
    public ProjSelectionMenu(final Player player) {
        List<ParticleItem> availableItems = SkyWarsReloaded.getLM().getProjParticleItems();

        int rowCount = menuSlotsPerRow;
        while (rowCount < 45 && rowCount < menuSize) {
            rowCount += menuSlotsPerRow;
        }

        SkyWarsReloaded.getIC().create(player, menuName, rowCount, new IconMenu.OptionClickEventHandler() {
			@Override
            public void onOptionClick(IconMenu.OptionClickEvent event) {
                
                String name = event.getName();
            	
                ParticleItem effect = SkyWarsReloaded.getLM().getProjByName(name);
                if (effect == null) {
                    return;
                }           
                
            	if (player.getLevel() < effect.getLevel() && !player.hasPermission("sw.proeffect." + effect.getEffect())) {
                    return;
            	} else {
                    event.setWillClose(true);
                    event.setWillDestroy(true);
            
            		PlayerStat ps = PlayerStat.getPlayerStats(player);
                	ps.setProjectileEffect(effect.getEffect());
                	DataStorage.get().saveStats(ps);
                	player.sendMessage(new Messaging.MessageFormatter().setVariable("effect", effect.getName()).format("menu.useeffect-playermsg"));
            	}   
            	
            }
        });

        ArrayList<Integer> placement = new ArrayList<Integer>(Arrays.asList(44, 0, 2, 4, 6, 8, 9, 11, 13, 15, 17, 18, 20, 22, 24, 26, 27, 29, 31, 33, 35));
        
        for (int iii = 0; iii < availableItems.size(); iii ++) {
        	if (iii >= menuSize || iii > 21) {
                break;
            }

            ParticleItem effect = availableItems.get(iii);
            List<String> loreList = Lists.newLinkedList();
            ItemStack item = new ItemStack(Material.valueOf(SkyWarsReloaded.getCfg().getMaterial("nopermission")), 1);
            
            if (player.getLevel() >= effect.getLevel() || player.hasPermission("sw.proeffect." + effect.getEffect())) {
            	loreList.add(new Messaging.MessageFormatter().format("menu.useprojeffect-seteffect"));
            	item = new ItemStack(effect.getMaterial(), 1);
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
