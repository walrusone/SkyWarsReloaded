package com.walrusone.skywarsreloaded.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.enums.Vote;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.menus.KitSelectionMenu;
import com.walrusone.skywarsreloaded.menus.VotingMenu;
import com.walrusone.skywarsreloaded.objects.GameKit;
import com.walrusone.skywarsreloaded.objects.GameMap;
import com.walrusone.skywarsreloaded.utilities.Messaging;
import com.walrusone.skywarsreloaded.utilities.Util;

public class PlayerInteractListener implements Listener
{
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onClick(final PlayerInteractEvent a1) {
    	final GameMap gameMap = MatchManager.get().getPlayerMap(a1.getPlayer());
    	if (gameMap == null) {
    		return;
    	}
    	if (gameMap.getMatchState() == MatchState.WAITINGSTART) {
        	a1.setCancelled(true);
        	if (a1.getAction() == Action.RIGHT_CLICK_AIR || a1.getAction() == Action.RIGHT_CLICK_BLOCK) {
        		if (a1.hasItem()) {
        			Player player = a1.getPlayer();
                    if (a1.getItem().equals(SkyWarsReloaded.getIM().getItem("kitvote"))) {
                    	if (SkyWarsReloaded.getCfg().kitVotingEnabled()) {
                        	a1.getPlayer().openInventory(gameMap.getKitsVoteMenu());
                    	} else {
                    		new KitSelectionMenu(a1.getPlayer());
                    	}
                    	Util.get().playSound(player, player.getLocation(), SkyWarsReloaded.getCfg().getOpenKitMenuSound(), 1, 1);
                    } else if (a1.getItem().equals(SkyWarsReloaded.getIM().getItem("votingItem")) && a1.getPlayer().hasPermission("sw.chestvote")) {
                    	new VotingMenu(a1.getPlayer());
                    	Util.get().playSound(player, player.getLocation(), SkyWarsReloaded.getCfg().getOpenChestMenuSound(), 1, 1);
                    }  else if (a1.getItem().equals(SkyWarsReloaded.getIM().getItem("exitGameItem"))) {
                    	MatchManager.get().playerLeave(player, DamageCause.CUSTOM, true, true);
                    } 
        		}
        	}
        	return;
        }
		if (gameMap.getMatchState() == MatchState.ENDING) {
			a1.setCancelled(true);
		}
    }
        
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
    	final GameMap gameMap = MatchManager.get().getPlayerMap((Player) event.getWhoClicked());
    	if (gameMap == null) {
    		return;
    	}
		if (gameMap.getMatchState() == MatchState.WAITINGSTART) {
			event.setCancelled(true);
			int rawSlot = event.getRawSlot();
			Inventory kitMenu = gameMap.getKitsVoteMenu();
			Inventory chestMenu = gameMap.getChestVoteMenu();
			Inventory timeMenu = gameMap.getTimeVoteMenu();
			Inventory weatherMenu = gameMap.getWeatherVoteMenu();
			Inventory modifierMenu = gameMap.getModifierVoteMenu();
			
			Player player = (Player) event.getWhoClicked();
			if (rawSlot < kitMenu.getSize() && rawSlot >= 0 && event.getInventory().equals(kitMenu)) {
				ItemStack kitItem = kitMenu.getItem(rawSlot);
				if (kitItem != null && !kitItem.getType().equals(Material.AIR)) {
					GameKit kit = GameKit.getKit(kitItem.getItemMeta().getDisplayName());
					if (gameMap.isKitLocked(kit)) {
						if (((Player) event.getWhoClicked()).hasPermission("sw.kit." + kit.getFilename())) {
							gameMap.loadKit((Player) event.getWhoClicked(), gameMap, kit);
						} else {
							return;
						}
					}
					gameMap.setKitVote((Player) event.getWhoClicked(), kit);
					gameMap.updateKitVotes();
					Util.get().playSound(player, player.getLocation(), SkyWarsReloaded.getCfg().getConfirmeSelctionSound(), 1, 1);
					player.closeInventory();
					MatchManager.get().message(gameMap, new Messaging.MessageFormatter()
							.setVariable("player", player.getName())
							.setVariable("kit", kit.getColorName()).format("game.votekit"));
				}
			} else if (rawSlot < chestMenu.getSize() && rawSlot >= 0 && event.getInventory().equals(chestMenu)) {
				Vote cVote = null;
				String type = "";
				if (rawSlot == 9) {
					cVote = Vote.CHESTRANDOM;
					type = new Messaging.MessageFormatter().format("items.chest-random");
				} else if (rawSlot == 11) {
					cVote = Vote.CHESTBASIC;
					type = new Messaging.MessageFormatter().format("items.chest-basic");
				} else if (rawSlot == 13) {
					cVote = Vote.CHESTNORMAL;
					type = new Messaging.MessageFormatter().format("items.chest-normal");
				} else if (rawSlot == 15) {
					cVote = Vote.CHESTOP;
					type = new Messaging.MessageFormatter().format("items.chest-op");
				} else if (rawSlot == 17) {
					cVote = Vote.CHESTSCAVENGER;
					type = new Messaging.MessageFormatter().format("items.chest-scavenger");
				} else if (rawSlot == 26) {
					if (gameMap.getMatchState().equals(MatchState.WAITINGSTART)) {
						new VotingMenu(player);
					}
				}
				if (cVote != null) {
					gameMap.setVote((Player) event.getWhoClicked(), cVote, "chest");
					gameMap.updateVotes("chest");
					Util.get().playSound(player, player.getLocation(), SkyWarsReloaded.getCfg().getConfirmeSelctionSound(), 1, 1);
					if (gameMap.getMatchState().equals(MatchState.WAITINGSTART)) {
						new VotingMenu(player);
					}
					MatchManager.get().message(gameMap, new Messaging.MessageFormatter()
							.setVariable("player", player.getName())
							.setVariable("chest", type).format("game.votechest"));
				}
			} else if (rawSlot < timeMenu.getSize() && rawSlot >= 0 && event.getInventory().equals(timeMenu)) {
				Vote cVote = null;
				String type = "";
				if (rawSlot == 9) {
					cVote = Vote.TIMERANDOM;
					type = new Messaging.MessageFormatter().format("items.time-random");
				} else if (rawSlot == 11) {
					cVote = Vote.TIMEDAWN;
					type = new Messaging.MessageFormatter().format("items.time-dawn");
				} else if (rawSlot == 13) {
					cVote = Vote.TIMENOON;
					type = new Messaging.MessageFormatter().format("items.time-noon");
				} else if (rawSlot == 15) {
					cVote = Vote.TIMEDUSK;
					type = new Messaging.MessageFormatter().format("items.time-dusk");
				} else if (rawSlot == 17) {
					cVote = Vote.TIMEMIDNIGHT;
					type = new Messaging.MessageFormatter().format("items.time-midnight");
				} else if (rawSlot == 26) {
					if (gameMap.getMatchState().equals(MatchState.WAITINGSTART)) {
						new VotingMenu(player);
					}
				}
				if (cVote != null) {
					gameMap.setVote((Player) event.getWhoClicked(), cVote, "time");
					gameMap.updateVotes("time");
					Util.get().playSound(player, player.getLocation(), SkyWarsReloaded.getCfg().getConfirmeSelctionSound(), 1, 1);
					if (gameMap.getMatchState().equals(MatchState.WAITINGSTART)) {
						new VotingMenu(player);
					} 
					MatchManager.get().message(gameMap, new Messaging.MessageFormatter()
							.setVariable("player", player.getName())
							.setVariable("time", type).format("game.votetime"));
				}
			} else if (rawSlot < weatherMenu.getSize() && rawSlot >= 0 && event.getInventory().equals(weatherMenu)) {
				Vote cVote = null;
				String type = "";
				if (rawSlot == 9) {
					cVote = Vote.WEATHERRANDOM;
					type = new Messaging.MessageFormatter().format("items.weather-random");
				} else if (rawSlot == 11) {
					cVote = Vote.WEATHERSUN;
					type = new Messaging.MessageFormatter().format("items.weather-sunny");
				} else if (rawSlot == 13) {
					cVote = Vote.WEATHERRAIN;
					type = new Messaging.MessageFormatter().format("items.weather-rain");
				} else if (rawSlot == 15) {
					cVote = Vote.WEATHERTHUNDER;
					type = new Messaging.MessageFormatter().format("items.weather-storm");
				} else if (rawSlot == 17) {
					cVote = Vote.WEATHERSNOW;
					type = new Messaging.MessageFormatter().format("items.weather-snow");
				} else if (rawSlot == 26) {
					if (gameMap.getMatchState().equals(MatchState.WAITINGSTART)) {
						new VotingMenu(player);
					}
				}
				if (cVote != null) {
					gameMap.setVote((Player) event.getWhoClicked(), cVote, "weather");
					gameMap.updateVotes("weather");
					Util.get().playSound(player, player.getLocation(), SkyWarsReloaded.getCfg().getConfirmeSelctionSound(), 1, 1);
					if (gameMap.getMatchState().equals(MatchState.WAITINGSTART)) {
						new VotingMenu(player);
					} 
					MatchManager.get().message(gameMap, new Messaging.MessageFormatter()
							.setVariable("player", player.getName())
							.setVariable("weather", type).format("game.voteweather"));
				}
			} else if (rawSlot < modifierMenu.getSize() && rawSlot >= 0 && event.getInventory().equals(modifierMenu)) {
				Vote cVote = null;
				String type = "";
				if (rawSlot == 9) {
					cVote = Vote.MODIFIERRANDOM;
					type = new Messaging.MessageFormatter().format("items.modifier-random");
				} else if (rawSlot == 11) {
					cVote = Vote.MODIFIERSPEED;
					type = new Messaging.MessageFormatter().format("items.modifier-speed");
				} else if (rawSlot == 13) {
					cVote = Vote.MODIFIERJUMP;
					type = new Messaging.MessageFormatter().format("items.modifier-jump");
				} else if (rawSlot == 15) {
					cVote = Vote.MODIFIERSTRENGTH;
					type = new Messaging.MessageFormatter().format("items.modifier-strength");
				} else if (rawSlot == 17) {
					cVote = Vote.MODIFIERNONE;
					type = new Messaging.MessageFormatter().format("items.modifier-none");
				} else if (rawSlot == 26) {
					if (gameMap.getMatchState().equals(MatchState.WAITINGSTART)) {
						new VotingMenu(player);
					}
				}
				if (cVote != null) {
					gameMap.setVote((Player) event.getWhoClicked(), cVote, "modifier");
					gameMap.updateVotes("modifier");
					Util.get().playSound(player, player.getLocation(), SkyWarsReloaded.getCfg().getConfirmeSelctionSound(), 1, 1);
					if (gameMap.getMatchState().equals(MatchState.WAITINGSTART)) {
						new VotingMenu(player);
					}
					MatchManager.get().message(gameMap, new Messaging.MessageFormatter()
							.setVariable("player", player.getName())
							.setVariable("mod", type).format("game.votemodifier"));
				}
			} else if (SkyWarsReloaded.getIC().has((Player) event.getWhoClicked()) && rawSlot < SkyWarsReloaded.getIC().getMenu(player).getInventory().getSize() && rawSlot >= 0) {
					event.setCancelled(false);
			}
		}
    }
    
    @EventHandler
    public void onPlayerDropItem(final PlayerDropItemEvent event) {
    	final GameMap gameMap = MatchManager.get().getPlayerMap((Player) event.getPlayer());
    	if (gameMap == null) {
    		return;
    	}
		if (gameMap.getMatchState() == MatchState.WAITINGSTART) {
			event.setCancelled(true);
		}
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
    	GameMap gMap = MatchManager.get().getPlayerMap(e.getPlayer());
    	if (gMap == null) {
    		return;
    	}
    	if (gMap.getMatchState().equals(MatchState.WAITINGSTART)) {
    			e.setCancelled(true);
    			new BukkitRunnable() {
					@Override
					public void run() {
		    			e.getPlayer().teleport(gMap.getPlayerCard(e.getPlayer()).getSpawn());
					}
    			}.runTaskLater(SkyWarsReloaded.get(), 2);
   		}
    }
    
}
