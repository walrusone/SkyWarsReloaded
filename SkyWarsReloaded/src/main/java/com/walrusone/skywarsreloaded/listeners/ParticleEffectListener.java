package com.walrusone.skywarsreloaded.listeners;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.objects.GameMap;
import com.walrusone.skywarsreloaded.objects.PlayerStat;
import com.walrusone.skywarsreloaded.utilities.Util;

public class ParticleEffectListener implements Listener{

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		GameMap gameMap = MatchManager.get().getPlayerMap(e.getPlayer());
		if (gameMap != null) {
			if (gameMap.getMatchState() == MatchState.WAITINGSTART) {
				if (e.getTo().getBlockX() == e.getFrom().getBlockX() && e.getTo().getBlockY() == e.getFrom().getBlockY() && e.getTo().getBlockZ() == e.getFrom().getBlockZ()) {
					return;
				} else {
					e.setCancelled(true);
				}
			}
        	String effect = PlayerStat.getPlayerStats(e.getPlayer()).getParticleEffect().toLowerCase();
        	if (effect == null || effect.equalsIgnoreCase("none")) {
        	    return;
        	}
        	World world = e.getPlayer().getWorld();
        	Location location = e.getPlayer().getLocation();
        	Random random = new Random();
        		switch(effect) {
        		case "flame":
                    Util.get().sendParticles(world, "FLAME", (float) location.getX(), (float) location.getY(), (float) location.getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((8 - 4) + 4) + 1);
                    break;
        		case "smoke":
        			Util.get().sendParticles(world, "SMOKE_LARGE", (float) location.getX(), (float) location.getY(), (float) location.getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((16 - 8) + 8) + 1);
                    break;
        		case "portal":
        			Util.get().sendParticles(world, "PORTAL", (float) location.getX(), (float) location.getY(), (float) location.getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((140 - 80) + 80) + 1);
                    break;
        		case "heart":
        			Util.get().sendParticles(world, "HEART", (float) location.getX(), (float) location.getY(), (float) location.getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((10 - 3) + 3) + 1);
                    break;
        		case "critical":
        			Util.get().sendParticles(world, "CRIT", (float) location.getX(), (float) location.getY(), (float) location.getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((8 - 5) + 5) + 1);
                    break;
        		case "water":
        			Util.get().sendParticles(world, "WATER_SPLASH", (float) location.getX(), (float) location.getY(), (float) location.getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((14 - 8) + 8) + 1);
                    break;
        		case "redstone":
        			Util.get().sendParticles(world, "REDSTONE", (float) location.getX(), (float) location.getY(), (float) location.getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((10 - 3) + 3) + 1);
        			break;
        		case "sparks":
        			Util.get().sendParticles(world, "FIREWORKS_SPARK", (float) location.getX(), (float) location.getY(), (float) location.getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((8 - 4) + 4) + 1);
        			break;
        		case "lava_drip":
        			Util.get().sendParticles(world, "DRIP_LAVA", (float) location.getX(), (float) location.getY(), (float) location.getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((8 - 4) + 4) + 1);
        			break;
        		case "lava":
        			Util.get().sendParticles(world, "LAVA", (float) location.getX(), (float) location.getY(), (float) location.getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((16 - 4) + 4) + 1);
        			break;
        		case "alphabet":
        			Util.get().sendParticles(world, "ENCHANTMENT_TABLE", (float) location.getX(), (float) location.getY(), (float) location.getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((16 - 8) + 8) + 1);
        			break;
        		case "happy":
        			Util.get().sendParticles(world, "VILLAGER_HAPPY", (float) location.getX(), (float) location.getY(), (float) location.getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((12 - 4) + 4) + 1);
        			break;
        		case "magic":
        			Util.get().sendParticles(world, "SPELL_WITCH", (float) location.getX(), (float) location.getY(), (float) location.getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((10 - 7) + 7) + 1);
        			break;
        		case "music":
        			Util.get().sendParticles(world, "NOTE", (float) location.getX(), (float) location.getY(), (float) location.getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((10 - 4) + 4) + 1);
        			break;
        		case "angry":
        			Util.get().sendParticles(world, "VILLAGER_ANGRY", (float) location.getX(), (float) location.getY(), (float) location.getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((5 - 2) + 2) + 1);
        			break;
        		case "clouds":
        			Util.get().sendParticles(world, "CLOUD", (float) location.getX(), (float) location.getY(), (float) location.getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((12 - 6) + 6) + 1);
        			break;
        		case "potion":
        			Util.get().sendParticles(world, "SPELL", (float) location.getX(), (float) location.getY(), (float) location.getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((12 - 6) + 6) + 1);
        			break;
        		case "poison":
        			Util.get().sendParticles(world, "SPELL_INSTANT", (float) location.getX(), (float) location.getY(), (float) location.getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((12 - 6) + 6) + 1);
        			break;
        		case "snow":
        			Util.get().sendParticles(world, "SNOWBALL", (float) location.getX(), (float) location.getY(), (float) location.getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((12 - 6) + 6) + 1);
        			break;
        		case "slime":
        			Util.get().sendParticles(world, "SLIME", (float) location.getX(), (float) location.getY(), (float) location.getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((12 - 6) + 6) + 1);
        			break;
        		default: break;
        	}
        }
	}
	
	@EventHandler
	public void projectileLaunch(ProjectileLaunchEvent e) {
		 Projectile projectile = e.getEntity();
		 if (projectile instanceof Snowball || projectile instanceof Egg || projectile instanceof Arrow) {
			 if (projectile.getShooter() instanceof Player) {
				 Player player = (Player) projectile.getShooter();
				 GameMap gMap = MatchManager.get().getPlayerMap(player);
				 if (gMap != null) {
					 String effect = PlayerStat.getPlayerStats(player.getUniqueId()).getProjectileEffect();
					 if (effect != null) {
						 if (!effect.equalsIgnoreCase("none")) {
							 SkyWarsReloaded.getLM().addProjectile(projectile, effect);
						 }
					 } 
				 }
			 }
		 }
	}  
}
