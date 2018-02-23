package com.walrusone.skywarsreloaded.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.Listener;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.utilities.Messaging;

public class PlayerCommandPrepocessListener implements Listener
{
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommandPrepocess(final PlayerCommandPreprocessEvent e) {
    	GameMap gMap = MatchManager.get().getSpectatorMap(e.getPlayer());
    	String[] splited = e.getMessage().split("\\s+");
    	if (gMap != null) {
    		if (splited[0].equalsIgnoreCase("/spawn")) {
            	e.setCancelled(true);
            	gMap.getSpectators().remove(e.getPlayer().getUniqueId());
            	MatchManager.get().removeSpectator(gMap, e.getPlayer());
            	return;
    		}
        	if (SkyWarsReloaded.getCfg().disableCommandsSpectate()) {
            	if (gMap != null) {
                	if (e.getPlayer().hasPermission("sw.allowcommands")) {
                		return;
                	}
                    for (final String a1 : SkyWarsReloaded.getCfg().getEnabledCommandsSpectate()) {
                    	if (splited.length == 1) {
                        	if (splited[0].equalsIgnoreCase("/" + a1)) {
                                return;
                            }
                    	} else if (splited.length > 1) {
                        	if (splited[0].equalsIgnoreCase("/" + a1) || (splited[0] + " " + splited[1]).equalsIgnoreCase("/" + a1)) {
                                return;
                            }
                    	}
                    }
            		e.getPlayer().sendMessage(new Messaging.MessageFormatter().format("game.command-disabled-spec"));
            		e.setCancelled(true);
            		return;
                }
        	}
    	}
    		  	
    	if (MatchManager.get().getPlayerMap(e.getPlayer()) == null) {
            return;
        } else {
        	if (e.getPlayer().hasPermission("sw.allowcommands")) {
        		return;
        	}
            for (final String a1 : SkyWarsReloaded.getCfg().getEnabledCommands()) {
            	if (splited.length == 1) {
                	if (splited[0].equalsIgnoreCase("/" + a1)) {
                        return;
                    }
            	} else if (splited.length > 1) {
                	if (splited[0].equalsIgnoreCase("/" + a1) || (splited[0] + " " + splited[1]).equalsIgnoreCase("/" + a1)) {
                        return;
                    }
            	}
            }
            e.getPlayer().sendMessage(new Messaging.MessageFormatter().format("game.command-disabled"));
            e.setCancelled(true);
        }
    }
}
