package com.walrusone.skywarsreloaded.listeners;

import com.walrusone.skywarsreloaded.enums.GameType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.game.Crate;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.menus.gameoptions.KitSelectionMenu;
import com.walrusone.skywarsreloaded.menus.gameoptions.VotingMenu;
import com.walrusone.skywarsreloaded.menus.gameoptions.objects.CoordLoc;
import com.walrusone.skywarsreloaded.menus.playeroptions.OptionsSelectionMenu;
import com.walrusone.skywarsreloaded.utilities.Messaging;
import com.walrusone.skywarsreloaded.utilities.Party;
import com.walrusone.skywarsreloaded.utilities.Util;

public class PlayerInteractListener implements Listener {
	
    @SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(final PlayerInteractEvent a1) {
    	final GameMap gameMap = MatchManager.get().getPlayerMap(a1.getPlayer());
    	if (gameMap == null) {
    		if (Util.get().isSpawnWorld(a1.getPlayer().getWorld())) {
    			if (SkyWarsReloaded.getCfg().protectLobby()) {
    				a1.setCancelled(true);
    	    		if (a1.getPlayer().hasPermission("sw.alterlobby")) {
    	    			a1.setCancelled(false);
    	    		}
    			}
    			if (a1.getAction() == Action.RIGHT_CLICK_AIR || a1.getAction() == Action.RIGHT_CLICK_BLOCK) {
            		if (a1.hasItem()) {
                        if (a1.getItem().equals(SkyWarsReloaded.getIM().getItem("optionselect"))) {
							a1.setCancelled(true);
							Util.get().playSound(a1.getPlayer(), a1.getPlayer().getLocation(), SkyWarsReloaded.getCfg().getOpenOptionsMenuSound(), 0.5F, 1);
							new OptionsSelectionMenu(a1.getPlayer());
						} else if (a1.getItem().equals(SkyWarsReloaded.getIM().getItem("joinselect"))) {
							a1.setCancelled(true);
							if (SkyWarsReloaded.getIC().has("joinmenu")) {
								Util.get().playSound(a1.getPlayer(), a1.getPlayer().getLocation(), SkyWarsReloaded.getCfg().getOpenJoinMenuSound(), 1, 1);
								if (GameMap.getPlayableArenas(GameType.TEAM).size() == 0) {
									if (!SkyWarsReloaded.getIC().hasViewers("joinsinglemenu")) {
										new BukkitRunnable() {
											@Override
											public void run() {
												SkyWarsReloaded.getIC().getMenu("joinsinglemenu").update();
											}
										}.runTaskLater(SkyWarsReloaded.get(), 5);
									}
									SkyWarsReloaded.getIC().show(a1.getPlayer(), "joinsinglemenu");
									return;
								} else if (GameMap.getPlayableArenas(GameType.SINGLE).size() == 0) {
									if (!SkyWarsReloaded.getIC().hasViewers("jointeammenu")) {
										new BukkitRunnable() {
											@Override
											public void run() {
												SkyWarsReloaded.getIC().getMenu("jointeammenu").update();
											}
										}.runTaskLater(SkyWarsReloaded.get(), 5);
									}
									SkyWarsReloaded.getIC().show(a1.getPlayer(), "jointeammenu");
									return;
								} else {
									SkyWarsReloaded.getIC().show(a1.getPlayer(), "joinmenu");
									return;
								}
							}
						} else if (a1.getItem().equals(SkyWarsReloaded.getIM().getItem("spectateselect"))) {
							a1.setCancelled(true);
							if (GameMap.getPlayableArenas(GameType.TEAM).size() == 0) {
								if (!SkyWarsReloaded.getIC().hasViewers("joinsinglemenu")) {
									new BukkitRunnable() {
										@Override
										public void run() {
											SkyWarsReloaded.getIC().getMenu("joinsinglemenu").update();
										}
									}.runTaskLater(SkyWarsReloaded.get(), 5);
								}
								SkyWarsReloaded.getIC().show(a1.getPlayer(), "spectatesinglemenu");
								return;
							} else if (GameMap.getPlayableArenas(GameType.SINGLE).size() == 0) {
								if (!SkyWarsReloaded.getIC().hasViewers("jointeammenu")) {
									new BukkitRunnable() {
										@Override
										public void run() {
											SkyWarsReloaded.getIC().getMenu("jointeammenu").update();
										}
									}.runTaskLater(SkyWarsReloaded.get(), 5);
								}
								SkyWarsReloaded.getIC().show(a1.getPlayer(), "spectateteammenu");
								return;
							} else {
								SkyWarsReloaded.getIC().show(a1.getPlayer(), "spectatemenu");
								return;
							}
						}
            		}
            	}
    			Player player = a1.getPlayer();
            	if (a1.getAction() == Action.RIGHT_CLICK_BLOCK && (player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR)) {
            		 if (a1.getClickedBlock().getType() == Material.WALL_SIGN || a1.getClickedBlock().getType() == Material.SIGN_POST ) {
            				Sign s = (Sign) a1.getClickedBlock().getState();
            			    Location loc = s.getLocation();
            			    	boolean joined;
            			    	for (GameMap gMap : GameMap.getMaps()) {
                			    	if (gMap.hasSign(loc) && gMap.getMatchState().equals(MatchState.WAITINGSTART)) {
                			    		Party party = Party.getParty(player);
                			    		if (party != null) {
                			    			if (party.getLeader().equals(player.getUniqueId())) {
                    			    			gMap.addPlayers(party);
                			    			} else {
                			    				player.sendMessage(new Messaging.MessageFormatter().format("party.onlyleader"));
                			    			}
                			    		} else {
                    			    		joined = gMap.addPlayers(player);
                    			    		if (!joined) { 
                    			    			player.sendMessage(new Messaging.MessageFormatter().format("error.could-not-join2"));
                    			    		} 
                			    		}
                			    	}
                			    }
            		 }
            	}
    		}
    		return;
    	}
    	if (gameMap.getMatchState() == MatchState.WAITINGSTART) {
        	a1.setCancelled(true);
        	if (a1.getAction() == Action.RIGHT_CLICK_AIR || a1.getAction() == Action.RIGHT_CLICK_BLOCK) {
        		if (a1.hasItem()) {
        			Player player = a1.getPlayer();
                    if (a1.getItem().equals(SkyWarsReloaded.getIM().getItem("kitvote"))) {
                    	if (SkyWarsReloaded.getCfg().kitVotingEnabled()) {
                    		SkyWarsReloaded.getIC().show(player, gameMap.getKitVoteOption().getKey());
                    	} else {
                    		new KitSelectionMenu(a1.getPlayer());
                    	}
                    	Util.get().playSound(player, player.getLocation(), SkyWarsReloaded.getCfg().getOpenKitMenuSound(), 1, 1);
                    	return;
                    } else if (a1.getItem().equals(SkyWarsReloaded.getIM().getItem("votingItem"))) {
                    	if (a1.getPlayer().hasPermission("sw.votemenu")) {
                        	new VotingMenu(a1.getPlayer());
                        	Util.get().playSound(player, player.getLocation(), SkyWarsReloaded.getCfg().getOpenChestMenuSound(), 1, 1);
                    	} else {
                    		player.sendMessage(new Messaging.MessageFormatter().format("error.nopermission"));
                    	}
                    	return;
                    }  else if (a1.getItem().equals(SkyWarsReloaded.getIM().getItem("exitGameItem"))) {
                    	MatchManager.get().playerLeave(player, DamageCause.CUSTOM, true, true);
                    } 
        		}
        	}
        	return;
        }
    	if (gameMap.getMatchState() == MatchState.PLAYING) {
    		if (a1.getAction() == Action.RIGHT_CLICK_BLOCK) {
    			Block block = a1.getClickedBlock();
    			if (block.getType().equals(Material.ENDER_CHEST)) {
    				for (GameMap gMap: GameMap.getPlayableArenas(GameType.ALL)) {
    					for (Crate crate: gMap.getCrates()) {
    						if (crate.getLocation().equals(block.getLocation())) {
    							a1.setCancelled(true);
    							if (SkyWarsReloaded.getNMS().isOnePointEight()) {
    								a1.getPlayer().getWorld().playSound(a1.getPlayer().getLocation(), Sound.valueOf("CHEST_OPEN"), 1, 1);
    							} else {
    								a1.getPlayer().getWorld().playSound(a1.getPlayer().getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);
    							}
    							a1.getPlayer().openInventory(crate.getInventory());
    							SkyWarsReloaded.getNMS().playEnderChestAction(block, true);
    							return;
    						}
    					}
    				}
    			}
    		}
    	}
		if (gameMap.getMatchState() == MatchState.ENDING) {
			a1.setCancelled(true);
		}
    }
    
    @EventHandler 
    public void onInventoryClose(InventoryCloseEvent e) {
    	Inventory inv = e.getInventory();
    	if (inv.getTitle().equals(new Messaging.MessageFormatter().format("event.crateInv"))) {
    		for (GameMap gMap: GameMap.getPlayableArenas(GameType.ALL)) {
    			for (Crate crate: gMap.getCrates()) {
    				if(crate.getInventory().equals(inv) && inv.getViewers().size() <= 1) {
						if (SkyWarsReloaded.getNMS().isOnePointEight()) {
							e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), Sound.valueOf("CHEST_CLOSE"), 1, 1);
						} else {
							e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), Sound.BLOCK_CHEST_CLOSE, 1, 1);
						}
    					SkyWarsReloaded.getNMS().playEnderChestAction(e.getPlayer().getWorld().getBlockAt(crate.getLocation()), false);
    					return;
    				}
    			}
    		}
    	}
    	
    }
    
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
    	if (event.getWhoClicked() instanceof Player) {
        	GameMap gMap = MatchManager.get().getPlayerMap((Player) event.getWhoClicked());
        	if (gMap == null) {
        		ItemStack item;
        		ItemStack item2;
        		if (event.getClick().equals(ClickType.NUMBER_KEY)) {
        			item = event.getWhoClicked().getInventory().getItem(event.getHotbarButton());
        			item2 = event.getCurrentItem();
        		} else {
        			item = event.getCurrentItem();
        			item2 = event.getCurrentItem();
        		}
        		
        		if (item != null && (item.equals(SkyWarsReloaded.getIM().getItem("optionselect")) 
        				|| item.equals(SkyWarsReloaded.getIM().getItem("joinselect")) 
        				|| item.equals(SkyWarsReloaded.getIM().getItem("spectateselect"))) 
        				|| item2 != null && (item2.equals(SkyWarsReloaded.getIM().getItem("optionselect")) 
                		|| item2.equals(SkyWarsReloaded.getIM().getItem("joinselect")) 
                		|| item2.equals(SkyWarsReloaded.getIM().getItem("spectateselect"))) ) {
        			event.setCancelled(true);
        		}
        	} else {
        		if (gMap.getMatchState().equals(MatchState.WAITINGSTART) || gMap.getMatchState().equals(MatchState.ENDING)) {
        			event.setCancelled(true);
        		}
        	}
    	}

    }
        
    @EventHandler
    public void onPlayerDropItem(final PlayerDropItemEvent event) {
    	final GameMap gameMap = MatchManager.get().getPlayerMap(event.getPlayer());
    	if (gameMap == null) {
    		return;
    	}
		if (gameMap.getMatchState() == MatchState.WAITINGSTART || gameMap.getMatchState() == MatchState.ENDING) {
			event.setCancelled(true);
		}
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
		GameMap gMap = MatchManager.get().getPlayerMap(e.getPlayer());
		if (gMap == null) {
			if(e.getBlock().getType().equals(Material.CHEST) || e.getBlock().getType().equals(Material.TRAPPED_CHEST) || e.getBlock().getType().equals(Material.DIAMOND_BLOCK) || e.getBlock().getType().equals(Material.EMERALD_BLOCK)) {
				GameMap map = GameMap.getMap(e.getPlayer().getWorld().getName());
				if (map == null) {
					return;
				}
				if (map.isEditing()) {
					if (e.getBlock().getType().equals(Material.CHEST) || e.getBlock().getType().equals(Material.TRAPPED_CHEST)) {
						Chest chest = (Chest) e.getBlock().getState();
						map.removeChest(chest);
						InventoryHolder ih = chest.getInventory().getHolder();
						if (ih instanceof DoubleChest) {
							DoubleChest dc = (DoubleChest) ih;
							Chest left = (Chest) dc.getLeftSide();
							Chest right = (Chest) dc.getRightSide();
							Location locLeft = left.getLocation();
							Location locRight = right.getLocation();
							World world = e.getBlock().getWorld();
							new BukkitRunnable() {
								@Override
								public void run() {
									world.getBlockAt(locLeft).setType(Material.AIR);
									world.getBlockAt(locRight).setType(Material.AIR);
								}
							}.runTaskLater(SkyWarsReloaded.get(), 2L);
						}
						e.getPlayer().sendMessage(new Messaging.MessageFormatter().setVariable("mapname", map.getDisplayName()).format("maps.removeChest"));
					} else if (e.getBlock().getType().equals(Material.DIAMOND_BLOCK)) {
						boolean result = map.removeTeamCard(e.getBlock().getLocation());
						if (result) {
							e.getPlayer().sendMessage(new Messaging.MessageFormatter().setVariable("num", "" + (map.getMaxPlayers() + 1)).setVariable("mapname", map.getDisplayName()).format("maps.spawnRemoved"));
						}
					} else if (e.getBlock().getType().equals(Material.EMERALD_BLOCK)) {
						boolean result = map.removeDeathMatchSpawn(e.getBlock().getLocation());
						if (result) {
							e.getPlayer().sendMessage(new Messaging.MessageFormatter().setVariable("num", "" + (map.getDeathMatchSpawns().size() + 1)).setVariable("mapname", map.getDisplayName()).format("maps.deathSpawnRemoved"));
						}
					}
				}
			}
			return;
		}
		if (gMap.getMatchState().equals(MatchState.WAITINGSTART)) {
			e.setCancelled(true);
			new BukkitRunnable() {
				@Override
				public void run() {
					CoordLoc spawn = gMap.getPlayerCard(e.getPlayer()).getTeamCard().getSpawn();
					e.getPlayer().teleport(new Location(gMap.getCurrentWorld(), spawn.getX() + 0.5, spawn.getY() + 1, spawn.getZ() + 0.5));
				}
			}.runTaskLater(SkyWarsReloaded.get(), 2);
		}
		if (gMap.getMatchState().equals(MatchState.PLAYING)) {
			Block block = e.getBlock();
			if (block.getType().equals(Material.ENDER_CHEST)) {
				for (Crate crate: gMap.getCrates()) {
					if (crate.getLocation().equals(block.getLocation())) {
						e.setCancelled(true);
						return;
					}
				}
			}
		}
    }
    
    @EventHandler
    public void onBlockPlaced(BlockPlaceEvent e) {
		GameMap gMap = MatchManager.get().getPlayerMap(e.getPlayer());
		if (gMap == null) {
			if (e.getBlockPlaced().getType().equals(Material.CHEST) || e.getBlock().getType().equals(Material.TRAPPED_CHEST)) {
				GameMap map = GameMap.getMap(e.getPlayer().getWorld().getName());
				if (map == null) {
					return;
				}
				if (map.isEditing()) {
					Location loc = e.getBlock().getLocation();
					Player player = e.getPlayer();
					new BukkitRunnable() {
						@Override
						public void run() {
							map.addChest((Chest) loc.getBlock().getState());
							player.sendMessage(new Messaging.MessageFormatter().setVariable("mapname", map.getDisplayName()).format("maps.addChest"));
						}
					}.runTaskLater(SkyWarsReloaded.get(), 2L);
				}
			}
		}
    }
    
    @EventHandler
    public void onPlayerWalk(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        for (GameMap gMap: GameMap.getPlayableArenas(GameType.ALL)) {
        	 if (gMap.getDeathMatchWaiters().contains(player.getUniqueId().toString())) {
                 if (event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
                     event.setCancelled(true);
                 }
             }
        } 
    }
    
}
