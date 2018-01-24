package com.walrusone.skywarsreloaded.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.objects.PlayerStat;

public class PlayerJoinListener implements Listener
{
    @EventHandler
    public void onJoin(final PlayerJoinEvent a1) {
    	new BukkitRunnable() {
			@Override
			public void run() {
				if (SkyWarsReloaded.getCfg().getSpawn() != null) {
			    	a1.getPlayer().teleport(SkyWarsReloaded.getCfg().getSpawn());
			    	if (Bukkit.getOnlinePlayers().size() == 1) {
			    		SkyWarsReloaded.get().updateServers();
			    	}
				}
			}
    	}.runTaskLater(SkyWarsReloaded.get(), 1);

   	 	if (PlayerStat.getPlayerStats(a1.getPlayer()) == null) {
   	 		PlayerStat.getPlayers().add(new PlayerStat(a1.getPlayer()));
   	 	}
    }
}
