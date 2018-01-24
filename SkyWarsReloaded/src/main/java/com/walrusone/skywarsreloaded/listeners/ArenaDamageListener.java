package com.walrusone.skywarsreloaded.listeners;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.objects.GameMap;
import com.walrusone.skywarsreloaded.objects.PlayerData;

public class ArenaDamageListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
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
					return;
				} else {
					if (gameMap.allowPvp()) {
						event.setCancelled(false);
						if (damager instanceof Projectile) {
							Projectile proj = (Projectile) damager;
							if (proj.getShooter() instanceof Player) {
								hitter = (Player) proj.getShooter();
								if (hitter != null) {
									PlayerData pd = PlayerData.getPlayerData(target.getUniqueId());
									if (pd != null) {
										pd.setTaggedBy(hitter);
									}
								}
							} else {
								return;
							}
						} else if (damager instanceof Player) {
							hitter = (Player) damager;
							if (hitter != null) {
								PlayerData pd = PlayerData.getPlayerData(target.getUniqueId());
								if (pd != null) {
									pd.setTaggedBy(hitter);
								}
							}
						} else {
							return;
						}
					} else {
						if (damager instanceof Projectile) {
							event.setCancelled(false);
							Projectile proj = (Projectile) damager;
							if (proj.getShooter() instanceof Player) {
								hitter = (Player) proj.getShooter();
								PlayerData pd = PlayerData.getPlayerData(target.getUniqueId());
								if (pd != null) {
									pd.setTaggedBy(hitter);
								}
							} else {
								return;
							}
						} else if (damager instanceof Player) {
							event.setCancelled(true);
						} else {
							return;
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
			if (MatchManager.get().getPlayerMap(player) != null) {
				GameMap gameMap = MatchManager.get().getPlayerMap(player);
				if (gameMap.getMatchState() == MatchState.ENDING || gameMap.getMatchState() == MatchState.WAITINGSTART) {
					event.setCancelled(true);
					return;
				}
				if (!gameMap.allowFallDamage()) {
					if (event.getCause().equals(DamageCause.FALL)) {
						event.setCancelled(true);
						return;
					}
				}
			}
		} else {
			return;
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
}
