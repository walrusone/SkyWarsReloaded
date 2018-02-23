package com.walrusone.skywarsreloaded.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.menus.gameoptions.KitSelectionMenu;
import com.walrusone.skywarsreloaded.menus.gameoptions.VotingMenu;
import com.walrusone.skywarsreloaded.menus.playeroptions.OptionsSelectionMenu;
import com.walrusone.skywarsreloaded.utilities.Messaging;
import com.walrusone.skywarsreloaded.utilities.Party;
import com.walrusone.skywarsreloaded.utilities.Util;

public class PlayerInteractListener implements Listener
{
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
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
                            	if (!SkyWarsReloaded.getIC().hasViewers("joinmenu")) {
                                	new BukkitRunnable() {
        								@Override
        								public void run() {
        									SkyWarsReloaded.getIC().getMenu("joinmenu").update();
        								}
                                	}.runTaskLater(SkyWarsReloaded.get(), 5);
                            	}
                            	SkyWarsReloaded.getIC().show(a1.getPlayer(), "joinmenu");
                            	return;
                        	}
                        } else if (a1.getItem().equals(SkyWarsReloaded.getIM().getItem("spectateselect"))) {
                        	a1.setCancelled(true);
                        	if (!SkyWarsReloaded.getIC().hasViewers("spectatemenu")) {
                            	Util.get().playSound(a1.getPlayer(), a1.getPlayer().getLocation(), SkyWarsReloaded.getCfg().getOpenSpectateMenuSound(), 1, 1);
                            	if (!SkyWarsReloaded.getIC().hasViewers("spectatemenu") || !SkyWarsReloaded.getIC().hasViewers("joinmenu")) {
                                	new BukkitRunnable() {
        								@Override
        								public void run() {
        									SkyWarsReloaded.getIC().getMenu("joinmenu").update();
        								}
                                	}.runTaskLater(SkyWarsReloaded.get(), 5);
                            	}
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
            			    	boolean joined = false;
            			    	for (GameMap gMap : GameMap.getMaps()) {
                			    	if (gMap.hasSign(loc) && gMap.getMatchState().equals(MatchState.WAITINGSTART)) {
                			    		Party party = Party.getParty(player);
                			    		if (party != null) {
                			    			if (party.getLeader().equals(player.getUniqueId())) {
                    			    			joined = gMap.addParty(party);
                			    			} else {
                			    				player.sendMessage(new Messaging.MessageFormatter().format("party.onlyleader"));
                			    			}
                			    		} else {
                    			    		joined = gMap.addPlayer(player);
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
		if (gameMap.getMatchState() == MatchState.ENDING) {
			a1.setCancelled(true);
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
        			return;
        		}
        		if (event.getClick().equals(ClickType.NUMBER_KEY)) {
        			
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
    	final GameMap gameMap = MatchManager.get().getPlayerMap((Player) event.getPlayer());
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
