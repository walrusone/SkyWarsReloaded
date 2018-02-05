package com.walrusone.skywarsreloaded.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.enums.Vote;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.menus.KitSelectionMenu;
import com.walrusone.skywarsreloaded.objects.GameKit;
import com.walrusone.skywarsreloaded.objects.GameMap;

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
                    if (a1.getItem().getType() == Material.valueOf(SkyWarsReloaded.getCfg().getMaterial("kitvote"))) {
                    	if (SkyWarsReloaded.getCfg().kitVotingEnabled()) {
                        	a1.getPlayer().openInventory(gameMap.getKitsVoteMenu());
                    	} else {
                    		new KitSelectionMenu(a1.getPlayer());
                    	}
                    } else if (a1.getItem().getType() == Material.valueOf(SkyWarsReloaded.getCfg().getMaterial("chestvote")) && a1.getPlayer().hasPermission("sw.chestvote")) {
                    	a1.getPlayer().openInventory(gameMap.getChestVoteMenu());
                    } else if (a1.getItem().getType() == Material.valueOf(SkyWarsReloaded.getCfg().getMaterial("timevote")) && a1.getPlayer().hasPermission("sw.timevote")) {
                    	a1.getPlayer().openInventory(gameMap.getTimeVoteMenu());
                    } else if (a1.getItem().getType() == Material.valueOf(SkyWarsReloaded.getCfg().getMaterial("weathervote")) && a1.getPlayer().hasPermission("sw.weathervote")) {
                    	a1.getPlayer().openInventory(gameMap.getWeatherVoteMenu());
                    } else if (a1.getItem().getType() == Material.valueOf(SkyWarsReloaded.getCfg().getMaterial("modifiervote")) && a1.getPlayer().hasPermission("sw.modifiervote")) {
                    	a1.getPlayer().openInventory(gameMap.getModifierVoteMenu());
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
			
			if (rawSlot < kitMenu.getSize() && rawSlot >= 0 && event.getInventory().equals(kitMenu)) {
				ItemStack kitItem = kitMenu.getItem(rawSlot);
				if (kitItem != null && !kitItem.getType().equals(Material.AIR)) {
					GameKit kit = GameKit.getKit(kitItem.getItemMeta().getDisplayName());
					if (gameMap.isKitLocked(kit)) {
						if (((Player) event.getWhoClicked()).hasPermission("swr.kit." + kit.getFilename())) {
							gameMap.loadKit((Player) event.getWhoClicked(), gameMap, kit);
						} else {
							return;
						}
					}
					gameMap.setKitVote((Player) event.getWhoClicked(), kit);
					gameMap.updateKitVotes();
					((Player) event.getWhoClicked()).closeInventory();
				}
			} else if (rawSlot < chestMenu.getSize() && rawSlot >= 0 && event.getInventory().equals(chestMenu)) {
				Vote cVote = null;
				if (rawSlot == 9) {
					cVote = Vote.CHESTRANDOM;
				} else if (rawSlot == 11) {
					cVote = Vote.CHESTBASIC;
				} else if (rawSlot == 13) {
					cVote = Vote.CHESTNORMAL;
				} else if (rawSlot == 15) {
					cVote = Vote.CHESTOP;
				} else if (rawSlot == 17) {
					cVote = Vote.CHESTSCAVENGER;
				}
				if (cVote != null) {
					gameMap.setVote((Player) event.getWhoClicked(), cVote, "chest");
					gameMap.updateVotes("chest");
					((Player) event.getWhoClicked()).closeInventory();
				}
			} else if (rawSlot < timeMenu.getSize() && rawSlot >= 0 && event.getInventory().equals(timeMenu)) {
				Vote cVote = null;
				if (rawSlot == 9) {
					cVote = Vote.TIMERANDOM;
				} else if (rawSlot == 11) {
					cVote = Vote.TIMEDAWN;
				} else if (rawSlot == 13) {
					cVote = Vote.TIMENOON;
				} else if (rawSlot == 15) {
					cVote = Vote.TIMEDUSK;
				} else if (rawSlot == 17) {
					cVote = Vote.TIMEMIDNIGHT;
				}
				if (cVote != null) {
					gameMap.setVote((Player) event.getWhoClicked(), cVote, "time");
					gameMap.updateVotes("time");
					((Player) event.getWhoClicked()).closeInventory();
				}
			} else if (rawSlot < weatherMenu.getSize() && rawSlot >= 0 && event.getInventory().equals(weatherMenu)) {
				Vote cVote = null;
				if (rawSlot == 9) {
					cVote = Vote.WEATHERRANDOM;
				} else if (rawSlot == 11) {
					cVote = Vote.WEATHERSUN;
				} else if (rawSlot == 13) {
					cVote = Vote.WEATHERRAIN;
				} else if (rawSlot == 15) {
					cVote = Vote.WEATHERTHUNDER;
				} else if (rawSlot == 17) {
					cVote = Vote.WEATHERSNOW;
				}
				if (cVote != null) {
					gameMap.setVote((Player) event.getWhoClicked(), cVote, "weather");
					gameMap.updateVotes("weather");
					((Player) event.getWhoClicked()).closeInventory();
				}
			} else if (rawSlot < modifierMenu.getSize() && rawSlot >= 0 && event.getInventory().equals(modifierMenu)) {
				Vote cVote = null;
				if (rawSlot == 9) {
					cVote = Vote.MODIFIERRANDOM;
				} else if (rawSlot == 11) {
					cVote = Vote.MODIFIERSPEED;
				} else if (rawSlot == 13) {
					cVote = Vote.MODIFIERJUMP;
				} else if (rawSlot == 15) {
					cVote = Vote.MODIFIERSTRENGTH;
				} else if (rawSlot == 17) {
					cVote = Vote.MODIFIERNONE;
				}
				if (cVote != null) {
					gameMap.setVote((Player) event.getWhoClicked(), cVote, "modifier");
					gameMap.updateVotes("modifier");
					((Player) event.getWhoClicked()).closeInventory();
				}
			}
			if (SkyWarsReloaded.getIC().has((Player) event.getWhoClicked())) {
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
    
}
