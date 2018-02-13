package com.walrusone.skywarsreloaded.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.Listener;

import com.google.common.collect.Lists;
import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.menus.OptionsSelectionMenu;
import com.walrusone.skywarsreloaded.objects.SWRServer;
import com.walrusone.skywarsreloaded.utilities.Messaging;
import com.walrusone.skywarsreloaded.utilities.Util;

public class LobbyListener implements Listener
{
 
	private static Inventory joinMenu;

	public LobbyListener() {
		ArrayList<SWRServer> servers = SWRServer.getServers();
		
		int menuSize = 81;
		int rowCount = 9;
        while (rowCount < servers.size() && rowCount < menuSize) {
            rowCount += 9;
        }
        joinMenu = Bukkit.createInventory(null, rowCount, new Messaging.MessageFormatter().format("menu.joingame-menu-title"));
		updateJoinMenu();
	}
		
	public static void updateJoinMenu() {
		if (SkyWarsReloaded.getCfg().joinMenuEnabled() && joinMenu != null) {
			ArrayList<SWRServer> servers = SWRServer.getServers();
			
			int menuSize = 81;
			int rowCount = 9;
	        while (rowCount < servers.size() && rowCount < menuSize) {
	            rowCount += 9;
	        }
	        
	        for (int iii = 0; iii < servers.size(); iii++) {
	            if (iii >= joinMenu.getSize()) {
	                break;
	            }

	            SWRServer server = servers.get(iii);
	            	            
	            List<String> loreList = Lists.newLinkedList();
	            if (server.getMatchState() == MatchState.OFFLINE) {
	            	loreList.add((new Messaging.MessageFormatter().format("signs.offline").toUpperCase()));
	        	} else if (server.getMatchState() == MatchState.WAITINGSTART) {
		            loreList.add((new Messaging.MessageFormatter().format("signs.joinable").toUpperCase()));
	            } else if (server.getMatchState().equals(MatchState.PLAYING) || server.getMatchState().equals(MatchState.SUDDENDEATH)) {
	           	loreList.add((new Messaging.MessageFormatter().format("signs.playing").toUpperCase()));
	            }  else if (server.getMatchState().equals(MatchState.ENDING)) {
	            	loreList.add((new Messaging.MessageFormatter().format("signs.ending").toUpperCase()));
	            }
	            loreList.add((new Messaging.MessageFormatter().setVariable("playercount", "" + server.getPlayerCount()).setVariable("maxplayers", "" + server.getMaxPlayers()).format("signs.line4")));
	            
	            double xy = 0;
	            if (server.getMaxPlayers() != 0) {
	            	xy = ((double) (server.getPlayerCount() / server.getMaxPlayers()));
	            }
	            
	            ItemStack gameIcon = SkyWarsReloaded.getNMS().getItemStack(Material.valueOf(SkyWarsReloaded.getCfg().getMaterial("blockwaiting")), loreList, ChatColor.translateAlternateColorCodes('&', server.getDisplayName()));
	            if (server.getMatchState().equals(MatchState.OFFLINE)) {
	            	gameIcon = SkyWarsReloaded.getNMS().getItemStack(Material.valueOf(SkyWarsReloaded.getCfg().getMaterial("blockoffline")), loreList, ChatColor.translateAlternateColorCodes('&', server.getDisplayName()));
	            } else if (server.getMatchState().equals(MatchState.PLAYING) || server.getMatchState().equals(MatchState.SUDDENDEATH)) {
	           	 	gameIcon = SkyWarsReloaded.getNMS().getItemStack(Material.valueOf(SkyWarsReloaded.getCfg().getMaterial("blockplaying")), loreList, ChatColor.translateAlternateColorCodes('&', server.getDisplayName()));
	            } else if (server.getMatchState().equals(MatchState.ENDING)) {
	           	 	gameIcon = SkyWarsReloaded.getNMS().getItemStack(Material.valueOf(SkyWarsReloaded.getCfg().getMaterial("blockending")), loreList, ChatColor.translateAlternateColorCodes('&', server.getDisplayName()));
	            } else if (server.getMatchState() == MatchState.WAITINGSTART) {
	           	 	gameIcon = SkyWarsReloaded.getNMS().getItemStack(Material.valueOf(SkyWarsReloaded.getCfg().getMaterial("almostfull")), loreList, ChatColor.translateAlternateColorCodes('&', server.getDisplayName()));
	    	    	if (xy < 0.75) {
	    	    		gameIcon = SkyWarsReloaded.getNMS().getItemStack(Material.valueOf(SkyWarsReloaded.getCfg().getMaterial("threefull")), loreList, ChatColor.translateAlternateColorCodes('&', server.getDisplayName()));
	    	    	} 
	     	    	if (xy < 0.50) {
	    	    		gameIcon = SkyWarsReloaded.getNMS().getItemStack(Material.valueOf(SkyWarsReloaded.getCfg().getMaterial("halffull")), loreList, ChatColor.translateAlternateColorCodes('&', server.getDisplayName()));
	    	    	} 
	    	    	if (xy < 0.25) {
	    	    		gameIcon = SkyWarsReloaded.getNMS().getItemStack(Material.valueOf(SkyWarsReloaded.getCfg().getMaterial("almostempty")), loreList, ChatColor.translateAlternateColorCodes('&', server.getDisplayName()));
	    	    	} 
	            }  
	            joinMenu.setItem(iii, gameIcon);      
	        }
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onEntityDamage(final EntityDamageByEntityEvent e) {	
		if (SkyWarsReloaded.getCfg().protectLobby() && Util.get().isSpawnWorld(e.getEntity().getWorld())) {
			e.setCancelled(true);
    		if (e.getEntity() instanceof Player || e.getDamager() instanceof Player) {
        		if (((Player)e.getDamager()).hasPermission("sw.alterlobby")) {
        			e.setCancelled(false);
        		}
    		}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onClick(final PlayerInteractEvent e) {
		if (Util.get().isSpawnWorld(e.getPlayer().getWorld())) {
			if (SkyWarsReloaded.getCfg().protectLobby()) {
				e.setCancelled(true);
	    		if (e.getPlayer().hasPermission("sw.alterlobby")) {
	    			e.setCancelled(false);
	    		}
			}
			
			if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
        		if (e.hasItem()) {
                    if (e.getItem().equals(SkyWarsReloaded.getIM().getItem("optionselect"))) {
                    	e.setCancelled(true);
                    	Util.get().playSound(e.getPlayer(), e.getPlayer().getLocation(), SkyWarsReloaded.getCfg().getOpenOptionsMenuSound(), 0.5F, 1);
                    	new OptionsSelectionMenu(e.getPlayer());
                    } else if (e.getItem().equals(SkyWarsReloaded.getIM().getItem("joinselect"))) {
                    	e.setCancelled(true);
                    	Util.get().playSound(e.getPlayer(), e.getPlayer().getLocation(), SkyWarsReloaded.getCfg().getOpenJoinMenuSound(), 1, 1);
                    	e.getPlayer().openInventory(joinMenu);
                    } 
        		}
        	}
		}
    }
	
	 @EventHandler
	    public void onInventoryClick(final InventoryClickEvent e) {
	    	Player player = (Player) e.getWhoClicked();
	    	if (Util.get().isSpawnWorld(player.getWorld())) {
	    		if (SkyWarsReloaded.getCfg().protectLobby()) {
	            	if (!player.hasPermission("sw.alterlobby") && !SkyWarsReloaded.getIC().has(player)) {
	            		e.setCancelled(true);
	            	}
	    		}
	        	int rawSlot = e.getRawSlot();
				if (e.getInventory().equals(joinMenu) && rawSlot < joinMenu.getSize() && rawSlot >= 0) {
					e.setCancelled(true);
					SWRServer server = SWRServer.getServerByDisplayName(ChatColor.stripColor(SkyWarsReloaded.getNMS().getItemName(e.getCurrentItem())));
	                if (server == null) {
	                    return;
	                }
	                
	                if (server.getMatchState() != MatchState.WAITINGSTART) {
	                	Util.get().playSound(player, player.getLocation(), SkyWarsReloaded.getCfg().getErrorSound(), 1, 1);
	                	return;
	                }
	                
	                if (player.hasPermission("sw.join")) {
		                if (player!= null) {
		                	if (server.getMatchState() == MatchState.WAITINGSTART && server.getPlayerCount() < server.getMaxPlayers()) {
			                	player.closeInventory();
			                	server.setPlayerCount(server.getPlayerCount() + 1);
	        			    	server.updateSigns();
	    			    		SkyWarsReloaded.get().sendBungeeMsg(player, "Connect", server.getServerName());
		                	}
		                }
	                }
				} 
	    	}
	    }
    
    @EventHandler
    public void onPlayerDropItem(final PlayerDropItemEvent e) {
    	if (Util.get().isSpawnWorld(e.getPlayer().getWorld())) {
    		if (!e.getPlayer().hasPermission("sw.alterlobby") && !SkyWarsReloaded.getIC().has(e.getPlayer())) {
    			e.setCancelled(true);
    		}
    	}
    }
}
