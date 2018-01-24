package com.walrusone.skywarsreloaded.listeners;

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
				if (SkyWarsReloaded.getCfg().getSpawn() != null && SkyWarsReloaded.getCfg().teleportOnJoin()) {
			    	a1.getPlayer().teleport(SkyWarsReloaded.getCfg().getSpawn());
				} else if (SkyWarsReloaded.getCfg().getSpawn() != null && SkyWarsReloaded.getCfg().teleportOnWorldEnter()) {
					if (a1.getPlayer().getWorld().equals(SkyWarsReloaded.getCfg().getSpawn())) {
						a1.getPlayer().teleport(SkyWarsReloaded.getCfg().getSpawn());
					}
				}
			}
    	}.runTaskLater(SkyWarsReloaded.get(), 1);

   	 	if (PlayerStat.getPlayerStats(a1.getPlayer()) == null) {
   	 		PlayerStat.getPlayers().add(new PlayerStat(a1.getPlayer()));
   	 	}
    }
}
