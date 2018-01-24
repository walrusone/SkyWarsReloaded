package com.walrusone.skywarsreloaded.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.Listener;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.objects.GameMap;
import com.walrusone.skywarsreloaded.objects.PlayerData;
import com.walrusone.skywarsreloaded.objects.PlayerStat;

public class PlayerTeleportListener implements Listener
{
	
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onPlayerTeleport(final PlayerTeleportEvent a1) {
    		Player player = a1.getPlayer();
            final GameMap gameMap = MatchManager.get().getPlayerMap(player);
        	if (gameMap == null) {
            	if (PlayerData.getPlayerData(a1.getPlayer().getUniqueId()) != null && a1.isCancelled()) {
                    a1.setCancelled(false);
                    if (SkyWarsReloaded.getCfg().getSpawn() != null) {
                        if (a1.getTo().getWorld().equals(SkyWarsReloaded.getCfg().getSpawn().getWorld())) {
                        	if (a1.getTo().getWorld().equals(a1.getFrom().getWorld())) {
                        	} else {
                         		if (SkyWarsReloaded.getCfg().teleportOnWorldEnter()) {
                        			a1.setTo(SkyWarsReloaded.getCfg().getSpawn());
                        		}
                        	}
                        }
                        return;
                    }
                }
        	} else {
            	if (a1.getCause().equals(TeleportCause.END_PORTAL)) {
            		a1.setCancelled(false);
            	} else {
            		if (a1.getCause().equals(TeleportCause.ENDER_PEARL) && gameMap.getMatchState() != MatchState.ENDING && gameMap.getMatchState() != MatchState.WAITINGSTART) {
            			a1.setCancelled(false);
            		} else {
            			a1.setCancelled(true);
            		} 
            	}
        	}
       	 if (SkyWarsReloaded.getCfg().getSpawn() != null) {
             if (a1.getTo().getWorld().equals(SkyWarsReloaded.getCfg().getSpawn().getWorld())) {
             	if (a1.getTo().getWorld().equals(a1.getFrom().getWorld())) {
             	} else {
             		PlayerStat.updatePlayer(player.getUniqueId().toString());
             	}
             }
             return;
         }
    }
    
    
}
