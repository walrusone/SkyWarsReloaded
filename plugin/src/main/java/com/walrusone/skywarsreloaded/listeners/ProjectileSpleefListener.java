package com.walrusone.skywarsreloaded.listeners;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;

public class ProjectileSpleefListener implements Listener {

	@EventHandler
	public void projectileHitEvent(ProjectileHitEvent e) {
		if (!(e.getEntity().getShooter() instanceof Player)) {
			return;
		}
		GameMap gMap = MatchManager.get().getPlayerMap((Player)e.getEntity().getShooter());
		if (gMap != null && gMap.isProjectileSpleefEnabled()) {
			Projectile projectile = e.getEntity();
			if(projectile instanceof EnderPearl){
				return;
			}
			Block block = SkyWarsReloaded.getNMS().getHitBlock(e);
			if(block == null) {
				return;
			}
			block.breakNaturally();
		}
	}

}
