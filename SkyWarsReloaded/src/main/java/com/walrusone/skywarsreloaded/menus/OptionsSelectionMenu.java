package com.walrusone.skywarsreloaded.menus;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.menus.IconMenu.OptionClickEvent;
import com.walrusone.skywarsreloaded.utilities.Messaging;
import com.walrusone.skywarsreloaded.utilities.Util;

public class OptionsSelectionMenu {

    private static int menuSize = 27;
    private static final String menuName = new Messaging.MessageFormatter().format("menu.options-menu-title");
    
    public OptionsSelectionMenu(final Player player) {
    	
    	Inventory inv = Bukkit.createInventory(null, menuSize, menuName);

        if (SkyWarsReloaded.getCfg().glassMenuEnabled()) {
        	inv.setItem(9, SkyWarsReloaded.getIM().getItem("glassselect"));
        }
        	
        if (SkyWarsReloaded.getCfg().particleMenuEnabled()) {
        	inv.setItem(11, SkyWarsReloaded.getIM().getItem("particleselect"));
        }
        if (SkyWarsReloaded.getCfg().projectileMenuEnabled()) {
        	inv.setItem(12, SkyWarsReloaded.getIM().getItem("projectileselect"));
        }
        if (SkyWarsReloaded.getCfg().killsoundMenuEnabled()) {
        	inv.setItem(14, SkyWarsReloaded.getIM().getItem("killsoundselect"));
        }
        if (SkyWarsReloaded.getCfg().winsoundMenuEnabled()) {
        	inv.setItem(15, SkyWarsReloaded.getIM().getItem("winsoundselect"));
        }
        if (SkyWarsReloaded.getCfg().tauntsMenuEnabled()) {
        	inv.setItem(17, SkyWarsReloaded.getIM().getItem("tauntselect"));
        }
        
        inv.setItem(menuSize-1, SkyWarsReloaded.getIM().getItem("exitMenuItem"));

        SkyWarsReloaded.getIC().create(player, inv, new IconMenu.OptionClickEventHandler() {
			@Override
            public void onOptionClick(OptionClickEvent event) {
                
                String name = event.getName();

                if (name.equalsIgnoreCase(new Messaging.MessageFormatter().format("items.particle-effect-sel"))) {
                	new EffectSelectionMenu(player);
		            Util.get().playSound(player, player.getLocation(), SkyWarsReloaded.getCfg().getOpenParticleMenuSound(), 1, 1);
                } else if (name.equalsIgnoreCase(new Messaging.MessageFormatter().format("items.projectile-effect-sel"))) {
		            new ProjSelectionMenu(player);
		            Util.get().playSound(player, player.getLocation(), SkyWarsReloaded.getCfg().getOpenProjectileMenuSound(), 1, 1);
                } else if (name.equalsIgnoreCase(new Messaging.MessageFormatter().format("items.killsound-sel"))) {
		            new KillSoundSelectionMenu(player);
		            Util.get().playSound(player, player.getLocation(), SkyWarsReloaded.getCfg().getOpenKillSoundMenuSound(), 1, 1);
                } else if (name.equalsIgnoreCase(new Messaging.MessageFormatter().format("items.winsound-sel"))) {
		            new WinSoundSelectionMenu(player);
		            Util.get().playSound(player, player.getLocation(), SkyWarsReloaded.getCfg().getOpenWinSoundMenuSound(), 1, 1);
                } else if (name.equalsIgnoreCase(new Messaging.MessageFormatter().format("items.glass-sel"))) {
                    new ColorSelectionMenu(player);
		            Util.get().playSound(player, player.getLocation(), SkyWarsReloaded.getCfg().getOpenGlassMenuSound(), 1, 1);
                } else if (name.equalsIgnoreCase(new Messaging.MessageFormatter().format("items.taunt-sel"))) {
		            new TauntSelectionMenu(player);
		            Util.get().playSound(player, player.getLocation(), SkyWarsReloaded.getCfg().getOpenTauntMenuSound(), 1, 1);
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
