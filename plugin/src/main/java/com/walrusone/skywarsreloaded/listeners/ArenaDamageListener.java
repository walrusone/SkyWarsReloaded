package com.walrusone.skywarsreloaded.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.game.Crate;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.game.PlayerData;
import com.walrusone.skywarsreloaded.managers.MatchManager;

public class ArenaDamageListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerDamagedByAlly(EntityDamageByEntityEvent event) {
		Player target;
		Player hitter;
		Entity damager = event.getDamager();
		if (event.getEntity() instanceof Player) {
			target = (Player) event.getEntity();
			GameMap gameMap = MatchManager.get().getPlayerMap(target);
			if (gameMap != null) {
				if (gameMap.getMatchState() == MatchState.ENDING || gameMap.getMatchState() == MatchState.WAITINGSTART) {
					event.setCancelled(true);
				} else {
						event.setCancelled(false);
						if (damager instanceof Projectile) {
							Projectile proj = (Projectile) damager;
							if (damager instanceof Snowball) {
								event.setDamage(SkyWarsReloaded.getCfg().getSnowDamage());
							}
							if (damager instanceof Egg) {
								event.setDamage(SkyWarsReloaded.getCfg().getEggDamage());
							}
							if (proj.getShooter() instanceof Player) {
								hitter = (Player) proj.getShooter();
								if (hitter != null) {
									PlayerData pd = PlayerData.getPlayerData(target.getUniqueId());
									if (pd != null) {
										pd.setTaggedBy(hitter);
									}
								}
							}
						} else if (damager instanceof Player) {
							hitter = (Player) damager;
							PlayerData pd = PlayerData.getPlayerData(target.getUniqueId());
							if (pd != null) {
								pd.setTaggedBy(hitter);
							}
						}
				}

			}
		}		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void playerDamaged(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			GameMap gameMap = MatchManager.get().getPlayerMap(player);
			if (gameMap != null) {
				if (gameMap.getMatchState() == MatchState.ENDING || gameMap.getMatchState() == MatchState.WAITINGSTART) {
					event.setCancelled(true);
					return;
				}
				if (!gameMap.allowFallDamage()) {
					if (event.getCause().equals(DamageCause.FALL)) {
						event.setCancelled(true);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void satLoss(FoodLevelChangeEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			GameMap gameMap = MatchManager.get().getPlayerMap(player);
			if (gameMap != null) {
				if (gameMap.getMatchState() == MatchState.WAITINGSTART) {
					event.setCancelled(true);
				}
			}		
		}
	}
	
	@EventHandler
	public void regen(EntityRegainHealthEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		Player player = (Player) event.getEntity();
		GameMap gameMap = MatchManager.get().getPlayerMap(player);
		if (gameMap != null) {
			if (!gameMap.allowRegen()) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void bowEvent(EntityShootBowEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		Player player = (Player) event.getEntity();
		GameMap gameMap = MatchManager.get().getPlayerMap(player);
		if (gameMap != null) {
			if (gameMap.getMatchState() == MatchState.WAITINGSTART || gameMap.getMatchState() == MatchState.ENDING) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void arrowEvent(ProjectileHitEvent event) {
		if (event.getEntity() instanceof Arrow) {
			final Arrow arrow = (Arrow) event.getEntity();
			if (arrow.getShooter() instanceof Player) {
				Player player = (Player) arrow.getShooter();
				GameMap gameMap = MatchManager.get().getPlayerMap(player);
				if (gameMap != null) {
						arrow.remove();
				}
			}
		}
	}
	
	@EventHandler
	public void onAnvilLand(EntityChangeBlockEvent event) {
		if (event.getEntity() instanceof FallingBlock) {
			FallingBlock fb = (FallingBlock) event.getEntity();
			if (fb.getMaterial().equals(Material.ANVIL)) {
				for (GameMap gMap: GameMap.getPlayableArenas()) {
					if (gMap.getAnvils().contains(event.getEntity().getUniqueId().toString())) {
						event.setCancelled(true);
						gMap.getAnvils().remove(event.getEntity().getUniqueId().toString());
						return;
					}
				}
			} else if (fb.getMaterial().equals(Material.SAND)) {
				for (GameMap gMap: GameMap.getPlayableArenas()) {
					for (Crate crate: gMap.getCrates()) {
						if (fb.equals(crate.getEntity())) {
							event.setCancelled(true);
							fb.setDropItem(false);
							fb.getWorld().getBlockAt(fb.getLocation()).setType(Material.ENDER_CHEST);
							crate.setLocation(fb.getWorld().getBlockAt(fb.getLocation()));
							fb.remove();
						}
					}

				}
			}
        }
	}
}
