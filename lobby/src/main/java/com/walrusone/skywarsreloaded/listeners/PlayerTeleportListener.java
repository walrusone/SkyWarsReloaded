package com.walrusone.skywarsreloaded.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.Listener;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.objects.PlayerStat;

public class PlayerTeleportListener implements Listener
{
	
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onPlayerTeleport(final PlayerTeleportEvent a1) {
    		Player player = a1.getPlayer();
        		if (SkyWarsReloaded.getCfg().getSpawn() != null) {
        			if (!a1.getFrom().getWorld().equals(SkyWarsReloaded.getCfg().getSpawn().getWorld()) && a1.getTo().getWorld().equals(SkyWarsReloaded.getCfg().getSpawn().getWorld())) {
                		PlayerStat.updatePlayer(a1.getPlayer().getUniqueId().toString());
                		return;
                    }
                	if (a1.getFrom().getWorld().equals(SkyWarsReloaded.getCfg().getSpawn().getWorld()) && !a1.getTo().getWorld().equals(SkyWarsReloaded.getCfg().getSpawn().getWorld())) {
                		if (SkyWarsReloaded.getCfg().lobbyBoardEnabled()) {
            		        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        		        }
        		        if (SkyWarsReloaded.getCfg().joinMenuEnabled() && player.hasPermission("sw.join")) {
        		        	if (player.getInventory().getItem(SkyWarsReloaded.getCfg().getJoinSlot()) != null) {
            		        	if (player.getInventory().getItem(SkyWarsReloaded.getCfg().getJoinSlot()).equals(SkyWarsReloaded.getIM().getItem("joinselect"))) {
            		        		player.getInventory().setItem(SkyWarsReloaded.getCfg().getJoinSlot(),  new ItemStack(Material.AIR, 1));
            		        	}
        		        	}
        		        }
        		        if (SkyWarsReloaded.getCfg().optionsMenuEnabled()) {
        		        	if (player.getInventory().getItem(SkyWarsReloaded.getCfg().getOptionsSlot()) != null) {
            		        	if (player.getInventory().getItem(SkyWarsReloaded.getCfg().getOptionsSlot()).equals(SkyWarsReloaded.getIM().getItem("optionselect"))) {
                    		        player.getInventory().setItem(SkyWarsReloaded.getCfg().getOptionsSlot(), new ItemStack(Material.AIR, 1));
            		        	}
        		        	}
        		        }
                	}
                    return;
        		}
    }
    
    
}
