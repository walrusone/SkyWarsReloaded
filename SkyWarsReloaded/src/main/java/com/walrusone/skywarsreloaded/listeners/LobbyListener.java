package com.walrusone.skywarsreloaded.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.Listener;

import com.google.common.collect.Lists;
import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.enums.LeaderType;
import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.menus.OptionsSelectionMenu;
import com.walrusone.skywarsreloaded.objects.GameMap;
import com.walrusone.skywarsreloaded.utilities.Messaging;
import com.walrusone.skywarsreloaded.utilities.Util;

import me.clip.placeholderapi.PlaceholderAPI;

public class LobbyListener implements Listener
{

	private static Inventory joinMenu;
	private static Inventory spectateMenu;

	public LobbyListener() {
		ArrayList<GameMap> games = GameMap.getSortedMaps();
		
		int menuSize = 81;
		int rowCount = 9;
        while (rowCount < games.size() && rowCount < menuSize) {
            rowCount += 9;
        }
        joinMenu = Bukkit.createInventory(null, rowCount, new Messaging.MessageFormatter().format("menu.joingame-menu-title"));
        spectateMenu = Bukkit.createInventory(null, rowCount, new Messaging.MessageFormatter().format("menu.spectategame-menu-title"));
		updateJoinMenu();
	}
		
	public static void updateJoinMenu() {
		if ((SkyWarsReloaded.getCfg().joinMenuEnabled() || SkyWarsReloaded.getCfg().spectateMenuEnabled()) && joinMenu != null) {
			ArrayList<GameMap> games = GameMap.getSortedMaps();
			
			int menuSize = 81;
			int rowCount = 9;
	        while (rowCount < games.size() && rowCount < menuSize) {
	            rowCount += 9;
	        }
	        
	        for (int iii = 0; iii < games.size(); iii++) {
	            if (iii >= joinMenu.getSize()) {
	                break;
	            }

	            GameMap gMap = games.get(iii);
	            	            
	            List<String> loreList = Lists.newLinkedList();
	            if (gMap.getMatchState() == MatchState.WAITINGSTART) {
		            loreList.add((new Messaging.MessageFormatter().format("signs.joinable").toUpperCase()));
	            } else if (gMap.getMatchState().equals(MatchState.PLAYING) || gMap.getMatchState().equals(MatchState.SUDDENDEATH)) {
	           	loreList.add((new Messaging.MessageFormatter().format("signs.playing").toUpperCase()));
	            }  else if (gMap.getMatchState().equals(MatchState.ENDING)) {
	            	loreList.add((new Messaging.MessageFormatter().format("signs.ending").toUpperCase()));
	            }
	            loreList.add((new Messaging.MessageFormatter().setVariable("playercount", "" + gMap.getAlivePlayers().size()).setVariable("maxplayers", "" + gMap.getMaxPlayers()).format("signs.line4")));
	            for (Player p: gMap.getAllPlayers()) {
	            	if (p != null) {
	            		if (gMap.getAlivePlayers().contains(p)) {
	            			loreList.add(ChatColor.GREEN + p.getName());
	            		} else {
	            			loreList.add(ChatColor.RED + p.getName());
	            		}
	            	}
	            }
	            
	            double xy = ((double) (gMap.getAlivePlayers().size() / gMap.getMaxPlayers()));
	            
	            ItemStack gameIcon = SkyWarsReloaded.getNMS().getItemStack(Material.valueOf(SkyWarsReloaded.getCfg().getMaterial("blockwaiting")), loreList, ChatColor.translateAlternateColorCodes('&', gMap.getDisplayName()));
	            if (gMap.getMatchState().equals(MatchState.PLAYING) || gMap.getMatchState().equals(MatchState.SUDDENDEATH)) {
	           	 gameIcon = SkyWarsReloaded.getNMS().getItemStack(Material.valueOf(SkyWarsReloaded.getCfg().getMaterial("blockplaying")), loreList, ChatColor.translateAlternateColorCodes('&', gMap.getDisplayName()));
	            } else if (gMap.getMatchState().equals(MatchState.ENDING)) {
	           	 gameIcon = SkyWarsReloaded.getNMS().getItemStack(Material.valueOf(SkyWarsReloaded.getCfg().getMaterial("blockending")), loreList, ChatColor.translateAlternateColorCodes('&', gMap.getDisplayName()));
	            } else if (gMap.getMatchState() == MatchState.WAITINGSTART) {
	           	 gameIcon = SkyWarsReloaded.getNMS().getItemStack(Material.valueOf(SkyWarsReloaded.getCfg().getMaterial("almostfull")), loreList, ChatColor.translateAlternateColorCodes('&', gMap.getDisplayName()));
	    	    	if (xy < 0.75) {
	    	    		gameIcon = SkyWarsReloaded.getNMS().getItemStack(Material.valueOf(SkyWarsReloaded.getCfg().getMaterial("threefull")), loreList, ChatColor.translateAlternateColorCodes('&', gMap.getDisplayName()));
	    	    	} 
	     	    	if (xy < 0.50) {
	    	    		gameIcon = SkyWarsReloaded.getNMS().getItemStack(Material.valueOf(SkyWarsReloaded.getCfg().getMaterial("halffull")), loreList, ChatColor.translateAlternateColorCodes('&', gMap.getDisplayName()));
	    	    	} 
	    	    	if (xy < 0.25) {
	    	    		gameIcon = SkyWarsReloaded.getNMS().getItemStack(Material.valueOf(SkyWarsReloaded.getCfg().getMaterial("almostempty")), loreList, ChatColor.translateAlternateColorCodes('&', gMap.getDisplayName()));
	    	    	} 
	            }  
	            joinMenu.setItem(iii, gameIcon);      
	        }
	        if (SkyWarsReloaded.getCfg().spectateMenuEnabled()) {
		        spectateMenu.setContents(joinMenu.getContents());
	        }
		}
	}
	 
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
                    	Util.get().playSound(e.getPlayer(), e.getPlayer().getLocation(), SkyWarsReloaded.getCfg().getOpenOptionsMenuSound(), 0.5F, 1);
                    	new OptionsSelectionMenu(e.getPlayer());
                    } else if (e.getItem().equals(SkyWarsReloaded.getIM().getItem("joinselect"))) {
                    	e.setCancelled(true);
                    	Util.get().playSound(e.getPlayer(), e.getPlayer().getLocation(), SkyWarsReloaded.getCfg().getOpenJoinMenuSound(), 1, 1);
                    	e.getPlayer().openInventory(joinMenu);
                    } else if (e.getItem().equals(SkyWarsReloaded.getIM().getItem("spectateselect"))) {
                    	e.setCancelled(true);
                    	Util.get().playSound(e.getPlayer(), e.getPlayer().getLocation(), SkyWarsReloaded.getCfg().getOpenSpectateMenuSound(), 1, 1);
                    	e.getPlayer().openInventory(spectateMenu);
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
        	int rawSlot = e.getRawSlot();
			if (rawSlot < joinMenu.getSize() && rawSlot >= 0 && e.getInventory().equals(joinMenu)) {
				e.setCancelled(true);
				GameMap gMap = MatchManager.get().getPlayerMap(player);
                if (gMap != null) {
                    return;
                }

                gMap = GameMap.getMapByDisplayName(ChatColor.stripColor(SkyWarsReloaded.getNMS().getItemName(e.getCurrentItem())));
                if (gMap == null) {
                    return;
                }
                
                if (gMap.getMatchState() != MatchState.WAITINGSTART) {
                	Util.get().playSound(player, player.getLocation(), SkyWarsReloaded.getCfg().getErrorSound(), 1, 1);
                	return;
                }
                
                if (player.hasPermission("sw.join")) {
	                if (player!= null) {
	                	if (gMap.getMatchState() == MatchState.WAITINGSTART && gMap.canAddPlayer()) {
		                	player.closeInventory();
	                		gMap.addPlayer(player);
	                	}
	                }
                }
			} else if (rawSlot < spectateMenu.getSize() && rawSlot >= 0 && e.getInventory().equals(spectateMenu)) {
				e.setCancelled(true);
				GameMap gMap = MatchManager.get().getPlayerMap(player);
                if (gMap != null) {
                    return;
                }

                gMap = GameMap.getMapByDisplayName(ChatColor.stripColor(SkyWarsReloaded.getNMS().getItemName(e.getCurrentItem())));
                if (gMap == null) {
                    return;
                }
                  
                if (player.hasPermission("sw.spectate")) {
	                if (player!= null) {
		                	player.closeInventory();
		                	MatchManager.get().addSpectator(gMap, player);
	                }
                }
				
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
