package com.walrusone.skywars.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Gate;
import org.bukkit.material.TrapDoor;
import org.bukkit.util.Vector;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.Game;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.menus.SpecPlayerMenu;
import com.walrusone.skywars.menus.SpecShopMenu;
import com.walrusone.skywars.utilities.Messaging;

public class SpectatorListener implements Listener {

	@EventHandler(ignoreCancelled = true, priority=EventPriority.LOW)
	protected void onChatSend(AsyncPlayerChatEvent e) {
		GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(e.getPlayer().getUniqueId());
		if (gPlayer.isSpectating()) {
				e.setCancelled(true);
				gPlayer.getSpecGame().sendSpectatorMessage(e.getPlayer(), e.getMessage());
		}
	}
	
	@EventHandler
	protected void onBlockCanBuild(BlockCanBuildEvent e) {
		if (!e.isBuildable()) {
			Location blockL = e.getBlock().getLocation(); 
			
			boolean allowed = false; 
			
			for (Player target : SkyWarsReloaded.get().getServer().getOnlinePlayers()) {
				if (target.getWorld().equals(e.getBlock().getWorld()) && SkyWarsReloaded.getPC().getPlayer(target.getUniqueId()).isSpectating()) { 
					Location playerL = target.getLocation();
					
					if (playerL.getX() > blockL.getBlockX()-1 && playerL.getX() < blockL.getBlockX()+1) {
						if (playerL.getZ() > blockL.getBlockZ()-1 && playerL.getZ() < blockL.getBlockZ()+1) { 
							if (playerL.getY() > blockL.getBlockY()-2 && playerL.getY() < blockL.getBlockY()+1) {
								if (SkyWarsReloaded.getPC().getPlayer(target.getUniqueId()).isSpectating()) {
									allowed = true;
									target.teleport(e.getBlock().getLocation().add(0, 5, 0), TeleportCause.PLUGIN);
									target.sendMessage(new Messaging.MessageFormatter().withPrefix().format("spectate.teleport-from-block"));
								} else {
									allowed = false;
									break;
								}
							}
						}
					}
					
				}
			}
			e.setBuildable(allowed);
		}
	}
	
	@EventHandler
	protected void onEntityDamageEvent(final EntityDamageByEntityEvent e) {		
		if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
			if ((!e.getDamager().hasMetadata("NPC") && SkyWarsReloaded.getPC().getPlayer(((Player) e.getDamager()).getUniqueId()).isSpectating()) || (!e.getEntity().hasMetadata("NPC") && SkyWarsReloaded.getPC().getPlayer(((Player) e.getEntity()).getUniqueId()).isSpectating())) {
				e.setCancelled(true);
			}
		} else if (!(e.getEntity() instanceof Player) && e.getDamager() instanceof Player) {
			if (!e.getDamager().hasMetadata("NPC") && SkyWarsReloaded.getPC().getPlayer(((Player) e.getDamager()).getUniqueId()).isSpectating()) {
				e.setCancelled(true);
			}
		} else if (e.getEntity() instanceof Player && !(e.getDamager() instanceof Player)) {
			if (!e.getEntity().hasMetadata("NPC") && SkyWarsReloaded.getPC().getPlayer(((Player) e.getEntity()).getUniqueId()).isSpectating()) {
				e.setCancelled(true);
			}
		}
		
		if(e.getDamager() instanceof Projectile
				&& !(e.getDamager() instanceof ThrownPotion)
				&& e.getEntity() instanceof Player
				&& !e.getEntity().hasMetadata("NPC")
				&& SkyWarsReloaded.getPC().getPlayer(((Player) e.getEntity()).getUniqueId()).isSpectating()) {
			
			e.setCancelled(true);
			e.getDamager().remove();
			final Player spectatorInvolved = (Player) e.getEntity();
			final boolean wasFlying = spectatorInvolved.isFlying();
			final Location initialSpectatorLocation = spectatorInvolved.getLocation();
			
			final Vector initialProjectileVelocity = e.getDamager().getVelocity();
			final Location initialProjectileLocation = e.getDamager().getLocation();
			final Projectile proj = (Projectile) e.getDamager(); 
						
			
			
			if (spectatorInvolved != proj.getShooter()) {
				spectatorInvolved.setAllowFlight(true);
				spectatorInvolved.setFlying(true);
				spectatorInvolved.teleport(initialSpectatorLocation.clone().add(0, 6, 0), TeleportCause.PLUGIN);
				
				Bukkit.getScheduler().runTaskLater(SkyWarsReloaded.get(), new Runnable() {
					@Override
					public void run() {
						if (proj instanceof Arrow) {
							Arrow arrow = initialProjectileLocation.getWorld().spawn(initialProjectileLocation, Arrow.class);
							arrow.setBounce(false);
							arrow.setVelocity(initialProjectileVelocity);
							arrow.setShooter(proj.getShooter());
						} else if (proj instanceof Snowball) {
							Snowball snowball = initialProjectileLocation.getWorld().spawn(initialProjectileLocation, Snowball.class);
							snowball.setVelocity(initialProjectileVelocity); 
							snowball.setShooter(proj.getShooter());
						} else if (proj instanceof Egg) {
							Egg egg = initialProjectileLocation.getWorld().spawn(initialProjectileLocation, Egg.class);
							egg.setVelocity(initialProjectileVelocity); 
							egg.setShooter(proj.getShooter());
						} else if (proj instanceof EnderPearl) {
							Player p = (Player) proj.getShooter();
							p.launchProjectile(EnderPearl.class, initialProjectileVelocity);
						}
					}
				}, 1L);
				
				Bukkit.getScheduler().runTaskLater(SkyWarsReloaded.get(), new Runnable() {
					@Override
					public void run() {
						spectatorInvolved.teleport(new Location(initialSpectatorLocation.getWorld(), initialSpectatorLocation.getX(), initialSpectatorLocation.getY(), initialSpectatorLocation.getZ(), spectatorInvolved.getLocation().getYaw(), spectatorInvolved.getLocation().getPitch()), TeleportCause.PLUGIN);
						spectatorInvolved.setAllowFlight(true);
						spectatorInvolved.setFlying(wasFlying);
					}
				}, 5L);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	protected void onPotionSplash(final PotionSplashEvent e) {
		
		final ArrayList<UUID> spectatorsAffected = new ArrayList<UUID>();
		
		for(LivingEntity player : e.getAffectedEntities()) {
			if(player instanceof Player && !player.hasMetadata("NPC") && SkyWarsReloaded.getPC().getPlayer(((Player) player).getUniqueId()).isSpectating()) {
				spectatorsAffected.add(player.getUniqueId());
			}
		}

		if(!spectatorsAffected.isEmpty()) {
			
			Boolean teleportationNeeded = false;
			
			for(Entity entity : e.getEntity().getNearbyEntities(2, 2, 2)) {
				if(entity instanceof Player && !entity.hasMetadata("NPC") && SkyWarsReloaded.getPC().getPlayer(((Player) entity).getUniqueId()).isSpectating()) {
					teleportationNeeded = true;
				}
			}
			
			final HashMap<UUID,Boolean> oldFlyMode = new HashMap<UUID,Boolean>(); 
			
			for(UUID spectatorUUID : spectatorsAffected) {
				
				Player spectator = SkyWarsReloaded.get().getServer().getPlayer(spectatorUUID);
				
				e.setIntensity(spectator, 0);
				
				if(teleportationNeeded) {
					oldFlyMode.put(spectator.getUniqueId(), spectator.isFlying());
					spectator.setAllowFlight(true);
					spectator.setFlying(true);

					spectator.teleport(spectator.getLocation().add(0, 10, 0), TeleportCause.PLUGIN);
				}
			}
			
			if(teleportationNeeded) {
				
				final Location initialProjectileLocation = e.getEntity().getLocation();
				final Vector initialProjectileVelocity = e.getEntity().getVelocity();
				
				SkyWarsReloaded.get().getServer().getScheduler().runTaskLater(SkyWarsReloaded.get(), new Runnable() {
					@Override
					public void run() {
						ThrownPotion clonedEntity = (ThrownPotion) e.getEntity().getWorld().spawnEntity(initialProjectileLocation, e.getEntity().getType()); 
						clonedEntity.setShooter(e.getEntity().getShooter());
						clonedEntity.setTicksLived(e.getEntity().getTicksLived());
						clonedEntity.setFallDistance(e.getEntity().getFallDistance());
						clonedEntity.setBounce(e.getEntity().doesBounce());
						if(e.getEntity().getPassenger() != null) {
							clonedEntity.setPassenger(e.getEntity().getPassenger()); // hey, why not
						}
						
						// Clones the effects
						clonedEntity.setItem(e.getEntity().getItem());
						
						// Clones the speed/direction
						clonedEntity.setVelocity(initialProjectileVelocity);
						
						// Just in case
						e.getEntity().remove();
					}
				}, 1L);
				
				SkyWarsReloaded.get().getServer().getScheduler().runTaskLater(SkyWarsReloaded.get(), new Runnable() {
					@Override
					public void run() {
						for(UUID spectatorUUID : spectatorsAffected) {
							Player spectator = SkyWarsReloaded.get().getServer().getPlayer(spectatorUUID);
							
							spectator.teleport(spectator.getLocation().add(0, -10, 0), TeleportCause.PLUGIN);
							spectator.setAllowFlight(true);
							spectator.setFlying(oldFlyMode.get(spectatorUUID));
						}
					}
				}, 5L);
				
				e.setCancelled(true);
			}	
		}
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	protected void onGamemodeChange(PlayerGameModeChangeEvent e) {
		if (SkyWarsReloaded.getPC().getPlayer(e.getPlayer().getUniqueId()) != null
				&& SkyWarsReloaded.getPC().getPlayer(e.getPlayer().getUniqueId()).isSpectating()
				&& !e.getNewGameMode().equals(GameMode.ADVENTURE)
				&& SkyWarsReloaded.getPC().getPlayer(e.getPlayer().getUniqueId()).gamemodeChangeAllowed()) {
			
			e.setCancelled(true);
			e.getPlayer().setAllowFlight(true);
		}
	}
	
	
	@EventHandler
	protected void onPlayerDropItem(PlayerDropItemEvent e) {
		// On player drop item - Cancel if the player is a spectator
		if (SkyWarsReloaded.getPC().getPlayer(e.getPlayer().getUniqueId()).isSpectating()) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	protected void onPlayerPickupItem(PlayerPickupItemEvent e) {
		if (SkyWarsReloaded.getPC().getPlayer(e.getPlayer().getUniqueId()).isSpectating()) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	protected void onEntityTarget(EntityTargetEvent e) {
		// On entity target - Stop mobs targeting spectators
		// Check to make sure it isn't an NPC (Citizens NPC's will be detectable using 'entity.hasMetadata("NPC")')
		if (e.getTarget() != null && e.getTarget() instanceof Player && !e.getTarget().hasMetadata("NPC") && SkyWarsReloaded.getPC().getPlayer(((Player) e.getTarget()).getUniqueId()).isSpectating()) {
			e.setCancelled(true);
		}
		if (e.getTarget() instanceof Player && e.getTarget() != null && SkyWarsReloaded.getPC().getPlayer(((Player) e.getTarget()).getUniqueId()).isSpectating()) {
			if (e.getEntity() instanceof ExperienceOrb) {
				repellExpOrb((Player) e.getTarget(), (ExperienceOrb) e.getEntity());
				e.setCancelled(true);
				e.setTarget(null);
			}
		}
	}
	
	@EventHandler
	protected void onBlockDamage(BlockDamageEvent e) {
		if (SkyWarsReloaded.getPC().getPlayer(e.getPlayer().getUniqueId()).isSpectating()) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	protected void onEntityDamage(EntityDamageEvent e) {
		// On entity damage - Stops users hitting players and mobs while spectating
		// Check to make sure it isn't an NPC (Citizens NPC's will be detectable using 'entity.hasMetadata("NPC")')
		if (e.getEntity() instanceof Player && !e.getEntity().hasMetadata("NPC") && SkyWarsReloaded.getPC().getPlayer(((Player) e.getEntity()).getUniqueId()).isSpectating()) {
			e.setCancelled(true);
			e.getEntity().setFireTicks(0);
		}
	}
	
	@EventHandler
	protected void onFoodLevelChange(FoodLevelChangeEvent e) {
		if (e.getEntity() instanceof Player && !e.getEntity().hasMetadata("NPC") && SkyWarsReloaded.getPC().getPlayer(((Player) e.getEntity()).getUniqueId()).isSpectating()) {
			e.setCancelled(true);
			((Player) e.getEntity()).setFoodLevel(20);
			((Player) e.getEntity()).setSaturation(20);
		}
	}
	
	
	@EventHandler(priority=EventPriority.HIGH)
	protected void onPlayerInteract(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		if (player != null) {
			ItemStack item = player.getItemInHand();
			if (item != null) {
				if (SkyWarsReloaded.getPC().getPlayer(e.getPlayer().getUniqueId()).isSpectating() && ((e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && (item.getData().getItemType().equals(SkyWarsReloaded.getCfg().getSpectateItem().getData().getItemType()) &&  item.getEnchantments().keySet().equals(SkyWarsReloaded.getCfg().getSpectateItem().getEnchantments().keySet())))) {
					e.setCancelled(true);
					new SpecPlayerMenu(SkyWarsReloaded.getPC().getPlayer(e.getPlayer().getUniqueId()));
				}
				if (SkyWarsReloaded.getPC().getPlayer(e.getPlayer().getUniqueId()).isSpectating() && ((e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && (item.getData().getItemType().equals(SkyWarsReloaded.getCfg().getSpecStoreMenuItem().getData().getItemType()) &&  item.getEnchantments().keySet().equals(SkyWarsReloaded.getCfg().getSpecStoreMenuItem().getEnchantments().keySet())))) {
					e.setCancelled(true);
					new SpecShopMenu(SkyWarsReloaded.getPC().getPlayer(e.getPlayer().getUniqueId()));
				}
				if (SkyWarsReloaded.getPC().getPlayer(e.getPlayer().getUniqueId()).isSpectating() && ((e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && (item.getData().getItemType().equals(SkyWarsReloaded.getCfg().getExitGameItem().getData().getItemType()) &&  item.getEnchantments().keySet().equals(SkyWarsReloaded.getCfg().getExitGameItem().getEnchantments().keySet())))) {
					e.setCancelled(true);
					GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(e.getPlayer().getUniqueId());
					if (gPlayer.inGame()) {
						Game game = gPlayer.getGame();
						game.deletePlayer(gPlayer, false, false);
					} else {
						Game game = gPlayer.getSpecGame();
						game.removeSpectator(gPlayer);
					}
				}
			}
			
			if (SkyWarsReloaded.getPC().getPlayer(e.getPlayer().getUniqueId()).isSpectating()) {
				e.setCancelled(true);
				
				if(e.hasBlock()) {
					if(e.getClickedBlock().getState() instanceof InventoryHolder) {
							Inventory original = ((InventoryHolder) e.getClickedBlock().getState()).getInventory();
							Inventory copy = null;
							
							if(original.getType().equals(InventoryType.CHEST) && original.getSize() > 27) {
								String title;
								if (original.getTitle().startsWith("container.")) title = WordUtils.capitalizeFully(original.getType().toString());
								else title = original.getTitle();
								
								copy = SkyWarsReloaded.get().getServer().createInventory(e.getPlayer(), original.getSize(), title);
							}
							else {
								String title;
								if (original.getTitle().startsWith("container.")) title = WordUtils.capitalizeFully(original.getType().toString());
								else title = original.getTitle();
								
								copy = SkyWarsReloaded.get().getServer().createInventory(e.getPlayer(), original.getType(), title);
							}
							
							copy.setContents(original.getContents());
							e.getPlayer().openInventory(copy);
					}
					
					else if(e.getClickedBlock().getType() == Material.WOODEN_DOOR
							|| e.getClickedBlock().getType() == Material.IRON_DOOR_BLOCK
							|| e.getClickedBlock().getType() == Material.FENCE_GATE) {
						
						Player spectator = e.getPlayer();
						Location doorLocation = e.getClickedBlock()
						                             .getLocation()
						                             .setDirection(spectator.getLocation().getDirection());
						
						int relativeHeight = 0;
						if(e.getClickedBlock().getType() == Material.WOODEN_DOOR
								|| e.getClickedBlock().getType() == Material.IRON_DOOR_BLOCK) {
							
							Material belowBlockType = e.getClickedBlock()
							                               .getLocation().add(0, -1, 0)
							                               .getBlock().getType();
							
							if(belowBlockType == Material.WOODEN_DOOR || belowBlockType == Material.IRON_DOOR_BLOCK) {
								relativeHeight = -1;
							}
						}
						switch(e.getBlockFace()) {
							case EAST:
								spectator.teleport(doorLocation.add(-0.5, relativeHeight, 0.5), TeleportCause.PLUGIN);
								break;
							case NORTH:
								spectator.teleport(doorLocation.add(0.5, relativeHeight, 1.5), TeleportCause.PLUGIN);
								break;
							case SOUTH:
								spectator.teleport(doorLocation.add(0.5, relativeHeight, -0.5), TeleportCause.PLUGIN);
								break;
							case WEST:
								spectator.teleport(doorLocation.add(1.5, relativeHeight, 0.5), TeleportCause.PLUGIN);
								break;
							case UP:
								if(e.getClickedBlock().getState().getData() instanceof Gate) {
									Gate fenceGate = (Gate) e.getClickedBlock().getState().getData();
									switch(fenceGate.getFacing()) {
										case NORTH:
										case SOUTH:
											if(spectator.getLocation().getX() > doorLocation.getX()) {
												spectator.teleport(doorLocation.add(-0.5, relativeHeight, 0.5), TeleportCause.PLUGIN);
											}
											else {
												spectator.teleport(doorLocation.add(1.5, relativeHeight, 0.5), TeleportCause.PLUGIN);
											}
											break;
										case EAST:
										case WEST:
											if(spectator.getLocation().getZ() > doorLocation.getZ()) {
												spectator.teleport(doorLocation.add(0.5, relativeHeight, -0.5), TeleportCause.PLUGIN);
											}
											else {
												spectator.teleport(doorLocation.add(0.5, relativeHeight, 1.5), TeleportCause.PLUGIN);
											}
											break;
										default:
											break;
									}
								}
								break;
								
							default:
								break;
						}
						
					}
					
					else if(e.getClickedBlock().getType() == Material.TRAP_DOOR) {
						if(!((TrapDoor) e.getClickedBlock().getState().getData()).isOpen()) {
							Player spectator = e.getPlayer();
							Location doorLocation = e.getClickedBlock()
							                             .getLocation()
							                             .setDirection(spectator.getLocation().getDirection());
							
							switch(e.getBlockFace()) {
								case UP:
									spectator.teleport(doorLocation.add(0.5, -1, 0.5), TeleportCause.PLUGIN);
									break;
								
								case DOWN:
									spectator.teleport(doorLocation.add(0.5, 1, 0.5), TeleportCause.PLUGIN);
									break;
								
								default:
									break;
							}
						}
					}
				}
			}
		}

		
	}
	

	@EventHandler
	public void onInventoryDrag(InventoryDragEvent e) {
		if (SkyWarsReloaded.getPC().getPlayer(((Player) e.getWhoClicked()).getUniqueId()).isSpectating()) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onVehicleEnter(VehicleEnterEvent e) {
		if (e.getEntered() instanceof Player && SkyWarsReloaded.getPC().getPlayer(((Player) e.getEntered()).getUniqueId()).isSpectating()) {
			e.setCancelled(true);
		}
	}
	
    final void repellExpOrb(final Player player, final ExperienceOrb orb) {
		final Location pLoc = player.getLocation();
		final Location oLoc = orb.getLocation();
		final Vector dir = oLoc.toVector().subtract(pLoc.toVector());
		final double dx = Math.abs(dir.getX());
		final double dz = Math.abs(dir.getZ());
		if ( (dx == 0.0) && (dz == 0.0)){
			// Special case probably never happens
			dir.setX(0.001);
		}
		if ((dx < 3.0) && (dz < 3.0)){
			final Vector nDir = dir.normalize();
			final Vector newV = nDir.clone().multiply(0.3);
			newV.setY(0);
			orb.setVelocity(newV);
			if ((dx < 1.0) && (dz < 1.0)){
				// maybe oLoc
				orb.teleport(oLoc.clone().add(nDir.multiply(1.0)), TeleportCause.PLUGIN);
			} 
			if ((dx < 0.5) && (dz < 0.5)){
				orb.remove();
			} 
		} 
	}
	
}
