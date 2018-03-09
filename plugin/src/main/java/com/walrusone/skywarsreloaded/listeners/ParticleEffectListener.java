package com.walrusone.skywarsreloaded.listeners;

import java.util.List;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.managers.PlayerStat;
import com.walrusone.skywarsreloaded.menus.playeroptions.ProjectileEffectOption;
import com.walrusone.skywarsreloaded.menus.playeroptions.objects.ParticleEffect;

public class ParticleEffectListener implements Listener{

	@EventHandler
	public void projectileLaunch(ProjectileLaunchEvent e) {
		 Projectile projectile = e.getEntity();
		 if (projectile instanceof Snowball || projectile instanceof Egg || projectile instanceof Arrow) {
			 if (projectile.getShooter() instanceof Player) {
				 Player player = (Player) projectile.getShooter();
				 GameMap gMap = MatchManager.get().getPlayerMap(player);
				 if (gMap != null) {
				 	PlayerStat ps = PlayerStat.getPlayerStats(player.getUniqueId());
				 	if (ps != null) {
                        String key = ps.getProjectileEffect();
                        ProjectileEffectOption peo = (ProjectileEffectOption) ProjectileEffectOption.getPlayerOptionByKey(key);
                        if (peo != null) {
                            List<ParticleEffect> effects = peo.getEffects();
                            if (key != null && effects != null) {
                                if (!key.equalsIgnoreCase("none")) {
                                    SkyWarsReloaded.getOM().addProjectile(projectile, effects);
                                }
                            }
                        }
                    }
				 }
			 }
		 }
	}  
}
