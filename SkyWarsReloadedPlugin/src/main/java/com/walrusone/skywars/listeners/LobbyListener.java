package com.walrusone.skywars.listeners;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import com.walrusone.skywars.SkyWarsReloaded;

public class LobbyListener implements Listener {
		
	@EventHandler
	public void onFoodChange(FoodLevelChangeEvent e) {
		Entity ent = e.getEntity();
		if(ent instanceof Player) {
			if (inLobbyWorld((Player) ent) && SkyWarsReloaded.getCfg().hungerDisabled()) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent e) {
		if (SkyWarsReloaded.getCfg().weatherDisabled()) {
			Location spawn = SkyWarsReloaded.getCfg().getSpawn();
			if (spawn != null) {
				if (spawn.getWorld().equals(e.getWorld())) {
					e.setCancelled(true);
				} 
			}
		}
	}
	
	@EventHandler
	public void onEntityDamageEntity(EntityDamageByEntityEvent e) {
		 Entity ent = e.getEntity();
		 Entity damager = e.getDamager();
		 if(ent instanceof Player && damager instanceof Player) {
				if (!hasIgnorePermission((Player) damager)) {
					 if (inLobbyWorld((Player) ent) && SkyWarsReloaded.getCfg().pvpDisabled()) {
						 e.setCancelled(true); 
					 }
				}
		 }
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		Entity ent = e.getEntity();
		 if(ent instanceof Player) {
			 if (!hasIgnorePermission((Player) ent)) {
				 if (inLobbyWorld((Player) ent) && SkyWarsReloaded.getCfg().LobbyFallDamageDisabled()) {
						if (e.getCause().equals(DamageCause.FALL)) {
							e.setCancelled(true);
						}
				 }
				 if (inLobbyWorld((Player) ent) && SkyWarsReloaded.getCfg().damageDisabled()) {
						if (!e.getCause().equals(DamageCause.VOID)) {
							e.setCancelled(true);
						}
				 }
			 }
		 }
	}
	
	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent e) {
		Player player = e.getPlayer();
		if (!hasIgnorePermission(player)) {
			 if (inLobbyWorld(player) && SkyWarsReloaded.getCfg().playerBuildDisabled()) {
					e.setCancelled(true);
			 }
		}

	}
	
	@EventHandler
	public void onBlockPlaceEvent(BlockPlaceEvent e) {
		Player player = e.getPlayer();
		if (!hasIgnorePermission(player)) {
			 if (inLobbyWorld(player) && SkyWarsReloaded.getCfg().playerBuildDisabled()) {
					e.setCancelled(true);
			 }
		}
	}
	
	@EventHandler 
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (!hasIgnorePermission(player)) {
   		 	if (inLobbyWorld(player) && SkyWarsReloaded.getCfg().playerInteractDisabled()) {
   		 		e.setCancelled(true);
   		 	}
        }
    }
        
	@EventHandler
	public void onDropItem(PlayerDropItemEvent e) {
		Player player = e.getPlayer();
		if (!hasIgnorePermission(player)) {
			 if (inLobbyWorld(player) && SkyWarsReloaded.getCfg().playerInteractDisabled()) {
					e.setCancelled(true);
			}	
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		if (!hasIgnorePermission(player)) {
			 if (inLobbyWorld(player) && SkyWarsReloaded.getCfg().playerInteractDisabled()) {
					e.setCancelled(true);
			 }
		}
	}
	
	@EventHandler
	public void onTeleportEvent(PlayerTeleportEvent e) {
		final Player player = e.getPlayer();
		if (teleportBetweenWorlds(e.getTo().getWorld(), e.getFrom().getWorld()) && teleportToLobby(e.getTo().getWorld())) {
			SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
				public void run() {
					if (player != null) {
						givePlayerItems(player);
					}
				}
			}, 8);
		} else if (teleportBetweenWorlds(e.getTo().getWorld(), e.getFrom().getWorld()) && !teleportToLobby(e.getTo().getWorld())) {
			SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
				public void run() {
					if (player != null) {
						removePlayerItems(player);
					}
				}
			}, 8);
		}
	}
	
	public void givePlayerItems(Player p) {
		if (SkyWarsReloaded.getCfg().giveSpectateItem()) {
			if (p.hasPermission("swr.spectate")) {
				p.getInventory().setItem(SkyWarsReloaded.getCfg().getSpectateItemSlot(), SkyWarsReloaded.getCfg().getSpectateItem());
			}
		}
		if (SkyWarsReloaded.getCfg().giveJoinMenuItem()) {
				p.getInventory().setItem(SkyWarsReloaded.getCfg().getJoinMenuSlot(), SkyWarsReloaded.getCfg().getJoinItem());
		}
		if (SkyWarsReloaded.getCfg().giveLobbyMenuItem()) {
			p.getInventory().setItem(SkyWarsReloaded.getCfg().getLobbyMenuSlot(), SkyWarsReloaded.getCfg().getLobbyMenuItem());
		}
		SkyWarsReloaded.getScore().getScoreboard(p);
	}
	
	public void removePlayerItems(Player player) {
		if (player != null) {
			if (SkyWarsReloaded.getPC().getPlayer(player.getUniqueId()) != null) {
				if (!SkyWarsReloaded.getPC().getPlayer(player.getUniqueId()).inGame()) {
					player.getInventory().remove(SkyWarsReloaded.getCfg().getSpectateItem());
					player.getInventory().remove(SkyWarsReloaded.getCfg().getJoinItem());
					player.getInventory().remove(SkyWarsReloaded.getCfg().getLobbyMenuItem());
					if (!SkyWarsReloaded.getPC().getPlayer(player.getUniqueId()).isSpectating()) {
						player.setScoreboard(SkyWarsReloaded.get().getServer().getScoreboardManager().getNewScoreboard());
					}
				}
			}
		}
	}
	
	public boolean inLobbyWorld(Player p) {
		Location spawn = SkyWarsReloaded.getCfg().getSpawn();
		if (spawn != null) {
			if (spawn.getWorld().equals(p.getWorld())) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public boolean teleportBetweenWorlds(World to, World from) {
		if (to.equals(from)) {
			return false;
		}
		return true;
	}
	
	public boolean teleportToLobby(World to) {
		Location spawn = SkyWarsReloaded.getCfg().getSpawn();
		if (spawn != null) {
			if (spawn.getWorld().equals(to)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public boolean hasIgnorePermission(Player player) {
		return player.isOp() || player.hasPermission("swr.ignoreLobbyGuard");
	}
    
}
