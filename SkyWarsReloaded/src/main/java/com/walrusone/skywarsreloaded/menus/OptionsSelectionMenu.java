package com.walrusone.skywarsreloaded.menus;

import java.util.List;

import org.bukkit.entity.Player;

import com.google.common.collect.Lists;
import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.utilities.Messaging;

public class OptionsSelectionMenu {

    private static int menuSize = 27;
    private static final String menuName = new Messaging.MessageFormatter().format("menu.usecolor-menu-title");
    
    public OptionsSelectionMenu(final Player player) {

        SkyWarsReloaded.getIC().create(player, menuName, menuSize, new IconMenu.OptionClickEventHandler() {
			@Override
            public void onOptionClick(IconMenu.OptionClickEvent event) {
                
                String name = event.getName();
                event.setWillClose(false);
                event.setWillDestroy(false); 
                if (name.equalsIgnoreCase(new Messaging.MessageFormatter().format("items.particle-effect-sel"))) {
                	new EffectSelectionMenu(player);
                } else if (name.equalsIgnoreCase(new Messaging.MessageFormatter().format("items.projectile-effect-sel"))) {
                	new ProjSelectionMenu(player);
                } else if (name.equalsIgnoreCase(new Messaging.MessageFormatter().format("items.killsound-sel"))) {
                	new KillSoundSelectionMenu(player);
                } else if (name.equalsIgnoreCase(new Messaging.MessageFormatter().format("items.winsound-sel"))) {
                	new WinSoundSelectionMenu(player);
                } else if (name.equalsIgnoreCase(new Messaging.MessageFormatter().format("items.glass-sel"))) {
                	new ColorSelectionMenu(player);
                } else if (name.equalsIgnoreCase(new Messaging.MessageFormatter().format("items.taunt-sel"))) {
                	new TauntSelectionMenu(player);
                } else {
                	return;
                }
            	

            	
            }
        });

        List<String> loreList = Lists.newLinkedList();

        if (SkyWarsReloaded.getCfg().glassMenuEnabled()) {
        	if (player != null) {
                loreList = SkyWarsReloaded.getIM().getItem("glassselect").getItemMeta().getLore();
                SkyWarsReloaded.getIC().setOption(
                        player,
                        9,
                        SkyWarsReloaded.getIM().getItem("glassselect"),
                        SkyWarsReloaded.getNMS().getName(SkyWarsReloaded.getIM().getItem("glassselect")),
                        loreList.toArray(new String[loreList.size()]));
            }
        }
        if (SkyWarsReloaded.getCfg().particleMenuEnabled()) {
        	if (player != null) {
                loreList = SkyWarsReloaded.getIM().getItem("particleselect").getItemMeta().getLore();
                SkyWarsReloaded.getIC().setOption(
                        player,
                        11,
                        SkyWarsReloaded.getIM().getItem("particleselect"),
                        SkyWarsReloaded.getNMS().getName(SkyWarsReloaded.getIM().getItem("particleselect")),
                        loreList.toArray(new String[loreList.size()]));
            }
        }
        if (SkyWarsReloaded.getCfg().projectileMenuEnabled()) {
        	if (player != null) {
                loreList = SkyWarsReloaded.getIM().getItem("projectileselect").getItemMeta().getLore();
                SkyWarsReloaded.getIC().setOption(
                        player,
                        12,
                        SkyWarsReloaded.getIM().getItem("projectileselect"),
                        SkyWarsReloaded.getNMS().getName(SkyWarsReloaded.getIM().getItem("projectileselect")),
                        loreList.toArray(new String[loreList.size()]));
            }
        }
        if (SkyWarsReloaded.getCfg().killsoundMenuEnabled()) {
        	if (player != null) {
                loreList = SkyWarsReloaded.getIM().getItem("killsoundselect").getItemMeta().getLore();
                SkyWarsReloaded.getIC().setOption(
                        player,
                        14,
                        SkyWarsReloaded.getIM().getItem("killsoundselect"),
                        SkyWarsReloaded.getNMS().getName(SkyWarsReloaded.getIM().getItem("killsoundselect")),
                        loreList.toArray(new String[loreList.size()]));
            }
        }
        if (SkyWarsReloaded.getCfg().winsoundMenuEnabled()) {
        	if (player != null) {
                loreList = SkyWarsReloaded.getIM().getItem("winsoundselect").getItemMeta().getLore();
                SkyWarsReloaded.getIC().setOption(
                        player,
                        15,
                        SkyWarsReloaded.getIM().getItem("winsoundselect"),
                        SkyWarsReloaded.getNMS().getName(SkyWarsReloaded.getIM().getItem("winsoundselect")),
                        loreList.toArray(new String[loreList.size()]));
            }
        }
        if (SkyWarsReloaded.getCfg().tauntsMenuEnabled()) {
        	if (player != null) {
                loreList = SkyWarsReloaded.getIM().getItem("tauntselect").getItemMeta().getLore();
                SkyWarsReloaded.getIC().setOption(
                        player,
                        17,
                        SkyWarsReloaded.getIM().getItem("tauntselect"),
                        SkyWarsReloaded.getNMS().getName(SkyWarsReloaded.getIM().getItem("tauntselect")),
                        loreList.toArray(new String[loreList.size()]));
            }
        }
                
        if (player != null) {
            SkyWarsReloaded.getIC().show(player);
        }
    }
}
