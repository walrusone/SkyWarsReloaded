package com.walrusone.skywarsreloaded.listeners;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.utilities.Messaging;
import com.walrusone.skywarsreloaded.utilities.Util;

public class SpectateListener implements Listener{
	
	private HashMap<String, BukkitTask> teleportRequests = new HashMap<>();
		
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		final Player player = e.getPlayer();
		final GameMap gameMap = MatchManager.get().getSpectatorMap(player);
		if (gameMap == null) {
			return;
		}
		if (e.getCause() != TeleportCause.END_PORTAL) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onSpectatorDamaged(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			final Player player = (Player) e.getEntity();
			final GameMap gameMap = MatchManager.get().getSpectatorMap(player);
			if (gameMap == null) {
				return;
			}
			e.setCancelled(true);
			if (e.getCause() == DamageCause.VOID) {
				World world = gameMap.getCurrentWorld();
				Location spectateSpawn = new Location(world, 0, 0, 0);
				player.teleport(spectateSpawn);
			}
		}
		
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		final Player player = e.getPlayer();
		final GameMap gameMap = MatchManager.get().getSpectatorMap(player);
		if (gameMap == null) {
			return;
		}
		gameMap.getSpectators().remove(player.getUniqueId());
		MatchManager.get().removeSpectator(player);
	}
		
	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryClick(InventoryClickEvent e) {
		final Player player = (Player) e.getWhoClicked();
		final GameMap gameMap = MatchManager.get().getSpectatorMap(player);
		if (gameMap == null) {
			return;
		}
		int slot = e.getSlot();
		if (slot == 8) {
			player.closeInventory();
			gameMap.getSpectators().remove(player.getUniqueId());
			MatchManager.get().removeSpectator(player);
		} else if (slot >= 9 && slot <= 35) {
			player.closeInventory();
			ItemStack item = e.getCurrentItem();
			if (item != null && !item.getType().equals(Material.AIR)) {
				String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());
				Player toSpec = SkyWarsReloaded.get().getServer().getPlayer(name);
	            if (toSpec != null) {
    				if (!gameMap.mapContainsDead(toSpec.getUniqueId())) {
    					player.teleport(toSpec.getLocation(), TeleportCause.END_PORTAL);
    				}
	            }
			}
		}

	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerMove(PlayerMoveEvent e) {
		if(teleportRequests.containsKey(e.getPlayer().getUniqueId().toString())) {
			if (!(e.getTo().getBlockX() == e.getFrom().getBlockX() && e.getTo().getBlockY() == e.getFrom().getBlockY() && e.getTo().getBlockZ() == e.getFrom().getBlockZ())) {
				e.getPlayer().sendMessage(new Messaging.MessageFormatter().format("error.spectate-cancelled"));
				teleportRequests.get(e.getPlayer().getUniqueId().toString()).cancel();
				teleportRequests.remove(e.getPlayer().getUniqueId().toString());
			}
		}
	}
}
