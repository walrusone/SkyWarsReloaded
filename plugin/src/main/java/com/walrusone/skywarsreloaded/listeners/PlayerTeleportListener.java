package com.walrusone.skywarsreloaded.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.Listener;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.managers.PlayerStat;

public class PlayerTeleportListener implements Listener
{
	
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerTeleport(final PlayerTeleportEvent a1) {
    		Player player = a1.getPlayer();
            final GameMap gameMap = MatchManager.get().getPlayerMap(player);
        	if (gameMap == null) {
        		if (SkyWarsReloaded.getCfg().getSpawn() != null) {
        			if (!a1.getFrom().getWorld().equals(SkyWarsReloaded.getCfg().getSpawn().getWorld()) && a1.getTo().getWorld().equals(SkyWarsReloaded.getCfg().getSpawn().getWorld())) {
                		PlayerStat.updatePlayer(a1.getPlayer().getUniqueId().toString());
                		return;
                    }
                	if (a1.getFrom().getWorld().equals(SkyWarsReloaded.getCfg().getSpawn().getWorld()) && !a1.getTo().getWorld().equals(SkyWarsReloaded.getCfg().getSpawn().getWorld())) {
                		if (SkyWarsReloaded.getCfg().lobbyBoardEnabled()) {
            		        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        		        }
        		        if (SkyWarsReloaded.getCfg().optionsMenuEnabled()) {
        		        	if (player.getInventory().getItem(SkyWarsReloaded.getCfg().getOptionsSlot()) != null) {
            		        	if (player.getInventory().getItem(SkyWarsReloaded.getCfg().getOptionsSlot()).equals(SkyWarsReloaded.getIM().getItem("optionselect"))) {
                    		        player.getInventory().setItem(SkyWarsReloaded.getCfg().getOptionsSlot(), new ItemStack(Material.AIR, 1));
            		        	}
        		        	}
        		        }
        		        if (SkyWarsReloaded.getCfg().joinMenuEnabled() && player.hasPermission("sw.join")) {
        		        	if (player.getInventory().getItem(SkyWarsReloaded.getCfg().getJoinSlot()) != null) {
            		        	if (player.getInventory().getItem(SkyWarsReloaded.getCfg().getJoinSlot()).equals(SkyWarsReloaded.getIM().getItem("joinselect"))) {
            		        		player.getInventory().setItem(SkyWarsReloaded.getCfg().getJoinSlot(),  new ItemStack(Material.AIR, 1));
            		        	}
        		        	}
        		        }
        		        if (SkyWarsReloaded.getCfg().spectateMenuEnabled() && player.hasPermission("sw.spectate")) {
        		        	if (player.getInventory().getItem(SkyWarsReloaded.getCfg().getSpectateSlot()) != null) {
            		        	if (player.getInventory().getItem(SkyWarsReloaded.getCfg().getSpectateSlot()).equals(SkyWarsReloaded.getIM().getItem("spectateselect"))) {
            		        		player.getInventory().setItem(SkyWarsReloaded.getCfg().getSpectateSlot(),  new ItemStack(Material.AIR, 1));
            		        	}
        		        	}
        		        }
                	}
        		}
        	} else {
            	if (a1.getCause().equals(TeleportCause.END_PORTAL) || player.hasPermission("sw.opteleport") || a1.getTo().getWorld().equals(a1.getFrom().getWorld())) {
            		a1.setCancelled(false);
            	} else {
            		if (a1.getCause().equals(TeleportCause.ENDER_PEARL) && gameMap.getMatchState() != MatchState.ENDING && gameMap.getMatchState() != MatchState.WAITINGSTART) {
            			a1.setCancelled(false);
            		} else {
            			a1.setCancelled(true);
            		} 
            	}
        	}
    }
    
    
}
