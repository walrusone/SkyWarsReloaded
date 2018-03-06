package com.walrusone.skywarsreloaded.listeners;

import org.bukkit.event.EventHandler;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.objects.PlayerStat;

import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerQuitListener implements Listener
{
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent a1) {
    	final String id = a1.getPlayer().getUniqueId().toString();        
   		if (PlayerStat.getPlayerStats(id) != null) {
   			new BukkitRunnable() {
				@Override
				public void run() {
			   		PlayerStat.removePlayer(id);
				}
   			}.runTaskLater(SkyWarsReloaded.get(), 20);
   		}
    }
}
