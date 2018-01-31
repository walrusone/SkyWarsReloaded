package com.walrusone.skywarsreloaded.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.Listener;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.enums.LeaderType;
import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.menus.JoinMenu;
import com.walrusone.skywarsreloaded.menus.OptionsSelectionMenu;
import com.walrusone.skywarsreloaded.menus.SpectateMenu;
import com.walrusone.skywarsreloaded.objects.GameMap;
import com.walrusone.skywarsreloaded.utilities.Messaging;
import com.walrusone.skywarsreloaded.utilities.Util;

public class LobbyListener implements Listener
{

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onEntityDamage(final EntityDamageByEntityEvent e) {	
		if (e.getEntity().getWorld().equals(SkyWarsReloaded.getCfg().getSpawn().getWorld())) {
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
    	if (e.getPlayer().getWorld().equals(SkyWarsReloaded.getCfg().getSpawn().getWorld())) {
    		e.setCancelled(true);
    		if (e.getPlayer().hasPermission("sw.alterlobby")) {
    			e.setCancelled(false);
    		}
        	if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
        		if (e.hasItem()) {
                    if (e.getItem().equals(SkyWarsReloaded.getIM().getItem("optionselect"))) {
                    	e.setCancelled(true);
                    	new OptionsSelectionMenu(e.getPlayer());
                    } else if (e.getItem().equals(SkyWarsReloaded.getIM().getItem("joinselect"))) {
                    	e.setCancelled(true);
                    	new JoinMenu(e.getPlayer());
                    } else if (e.getItem().equals(SkyWarsReloaded.getIM().getItem("spectateselect"))) {
                    	e.setCancelled(true);
                    	new SpectateMenu(e.getPlayer());
                    }
        		}
        	}
        	
        	Player player = e.getPlayer();
        	if (e.getAction() == Action.RIGHT_CLICK_BLOCK && (player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR)) {
        		 if (e.getClickedBlock().getType() == Material.WALL_SIGN || e.getClickedBlock().getType() == Material.SIGN_POST ) {
        				Sign s = (Sign) e.getClickedBlock().getState();
        			    Location loc = s.getLocation();
        			    if (loc.getWorld().equals(SkyWarsReloaded.getCfg().getSpawn().getWorld())) {
        			    	boolean joined = false;
        			    	for (GameMap gMap : GameMap.getMaps()) {
            			    	if (gMap.hasSign(loc) && gMap.getMatchState().equals(MatchState.WAITINGSTART)) {
            			    		joined = gMap.addPlayer(player);
            			    	}
            			    }
    			    		if (!joined) { 
    			    			player.sendMessage(new Messaging.MessageFormatter().format("error.could-not-join2"));
    			    		}
        			    }    
        		 }
        	}
    	}
    }
    
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
    	Player player = (Player) e.getWhoClicked();
    	if (player.getWorld().equals(SkyWarsReloaded.getCfg().getSpawn().getWorld())) {
        	if (!player.hasPermission("sw.alterlobby") && !SkyWarsReloaded.getIC().has(player)) {
        		e.setCancelled(true);
        	}
    	}
    }
    
    @EventHandler
    public void onPlayerDropItem(final PlayerDropItemEvent e) {
    	if (e.getPlayer().getWorld().equals(SkyWarsReloaded.getCfg().getSpawn().getWorld())) {
    		if (!e.getPlayer().hasPermission("sw.alterlobby") && !SkyWarsReloaded.getIC().has(e.getPlayer())) {
    			e.setCancelled(true);
    		}
    	}
    }
    
    @EventHandler
    public void signPlaced(SignChangeEvent event) {
        String[] lines = event.getLines();
        if (lines[0].equalsIgnoreCase("[sw]") && lines.length >= 2) {
        	if (event.getPlayer().hasPermission("sw.signs")) {
        			Location signLocation = event.getBlock().getLocation();
                    World w = signLocation.getWorld();
                	Block b = w.getBlockAt(signLocation);
                	if(b.getType() == Material.WALL_SIGN || b.getType() == Material.SIGN_POST) {
               			event.setCancelled(true);
               			String arenaName = lines[1];
               			GameMap gMap = GameMap.getMap(arenaName);
               			if (gMap != null) {
               				gMap.addSign(signLocation);
                       		event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("signs.added"));
                       	} else {
                       		event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("signs.no-map"));
                       	}
                	}
            	} else {
            		event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("error.signs-no-perm"));
        			event.setCancelled(true);
            } 
       } else if (lines[0].equalsIgnoreCase("[swl]") && lines.length >= 3) {
    	   if (event.getPlayer().hasPermission("sw.signs")) {
   				Location signLocation = event.getBlock().getLocation();
   				World w = signLocation.getWorld();
               	Block b = w.getBlockAt(signLocation);
           		if(b.getType() == Material.WALL_SIGN || b.getType() == Material.SIGN_POST) {
          			event.setCancelled(true);
          			if (SkyWarsReloaded.getUseable().contains(lines[1].toUpperCase())) {
          				LeaderType type = LeaderType.valueOf(lines[1].toUpperCase());
          				if (Util.get().isInteger(lines[2])) {
          					if (Integer.valueOf(lines[2]) <= SkyWarsReloaded.getCfg().getLeaderSize()) {
                  				SkyWarsReloaded.getLB().addLeaderSign(Integer.valueOf(lines[2]), type, signLocation);
                          		event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("signs.addedleader"));
          					} else {
          						event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("signs.invalid-range"));
          					}
          				} else {
          					event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("error.position"));
          				}
                  	} else {
                  		event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("signs.invalid-type"));
                  	}
           		}
       		} else {
       			event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("error.signs-no-perm"));
       			event.setCancelled(true);
       		} 
       }
    }
    
    @EventHandler
    public void signRemoved(BlockBreakEvent event) {
        Location blockLocation = event.getBlock().getLocation();
        World w = blockLocation.getWorld();
    	Block b = w.getBlockAt(blockLocation);
		if(b.getType() == Material.WALL_SIGN || b.getType() == Material.SIGN_POST){
	    	Sign sign = (Sign) b.getState();
	    	Location loc = sign.getLocation();
	    	boolean removed = false;
	    	for (GameMap gMap : GameMap.getMaps()) {
	    		if (!removed) {
		    		removed = gMap.removeSign(loc);
	    		}
	    	}
	    	if (!removed) {
	    		removed = SkyWarsReloaded.getLB().removeLeaderSign(loc);
	    	}
	    	if (removed) {
		    	event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("signs.remove"));
	    	}
		}
    }    

    @EventHandler
    public void onPressurePlate(final PlayerInteractEvent e) {
    	Player player = e.getPlayer();
    	GameMap gMap = MatchManager.get().getPlayerMap(player);
    	if (gMap == null) {
    		if (e.getAction() == Action.PHYSICAL && e.getClickedBlock().getType() == Material.STONE_PLATE) {
        		if (SkyWarsReloaded.getCfg().pressurePlateJoin()) {
        			Location spawn = SkyWarsReloaded.getCfg().getSpawn();
        			if (spawn != null) {
        				boolean joined = MatchManager.get().joinGame(player);
        				int count = 0;
        				while (count < 4 && !joined) {
        					joined = MatchManager.get().joinGame(player);
        					count++;
        				}
        				if (!joined) {
        					player.sendMessage(new Messaging.MessageFormatter().format("error.could-not-join"));
        				}
           	        } else {
           				e.getPlayer().sendMessage(ChatColor.RED + "YOU MUST SET SPAWN IN THE LOBBY WORLD WITH /SWR SETSPAWN BEFORE STARTING A GAME");
           				SkyWarsReloaded.get().getLogger().info("YOU MUST SET SPAWN IN THE LOBBY WORLD WITH /SWR SETSPAWN BEFORE STARTING A GAME");
           			}
             	} 
    		}
    	} 
    }
}
