package com.walrusone.skywarsreloaded.menus;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.game.SWRSign;
import com.walrusone.skywarsreloaded.listeners.ChatListener;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.utilities.Messaging;

import me.rayzr522.jsonmessage.JSONMessage;

public class ArenaMenu {
	
    private static int menuSize = 27;
    private static final String menuName = ChatColor.DARK_PURPLE + "Arena Manager: ";
    
    public ArenaMenu(String key, GameMap gMap) {	
    	Inventory menu = Bukkit.createInventory(null, menuSize + 9, menuName + gMap.getName());
    	ArrayList<Inventory> invs = new ArrayList<Inventory>();
    	invs.add(menu);
    	
    	Runnable update = new Runnable() {
			@Override
			public void run() {
				if (SkyWarsReloaded.getIC().hasViewers(key)) {				
						List<String> lores = new ArrayList<String>();
						lores.add(ChatColor.GOLD + "" + gMap.getMinPlayers());
						lores.add(ChatColor.AQUA + "Left Click to Increase.");
						lores.add(ChatColor.AQUA + "Right Click to Decrease.");
						ItemStack min = SkyWarsReloaded.getNMS().getItemStack(new ItemStack(Material.DIAMOND_HELMET, 1), lores, "Minimun Players");
						
						
						lores.clear();
						if (gMap.isRegistered()) {
							lores.add(ChatColor.GREEN + "REGISTERED");
							lores.add(ChatColor.GOLD + gMap.getMatchState().toString().toUpperCase());
							lores.add(ChatColor.GREEN + "" + gMap.getAlivePlayers().size() + " of " + gMap.getMaxPlayers() + " Players");
							lores.add(" ");
							lores.add(ChatColor.RED + "Shift Left Click to Unregister.");
							lores.add(ChatColor.RED + "Unregistering will end the match!");
						} else {
							lores.add(ChatColor.RED + "UNREGISTERED");
							lores.add(" ");
							lores.add(ChatColor.AQUA + "Shift Left Click to");
							lores.add(ChatColor.AQUA + "Attempt Registration!");
						}
						ItemStack status = SkyWarsReloaded.getNMS().getItemStack(new ItemStack(Material.MAP, 1), lores, "Arena Status");
						
						lores.clear();
						lores.add(ChatColor.translateAlternateColorCodes('&', gMap.getDisplayName()));
						lores.add(" ");
						lores.add(ChatColor.AQUA + "Left Click to Change");
						lores.add(ChatColor.AQUA + "the Display Name.");
						ItemStack display = SkyWarsReloaded.getNMS().getItemStack(new ItemStack(Material.NAME_TAG, 1), lores, "Display Name");
						
						lores.clear();
						lores.add(ChatColor.translateAlternateColorCodes('&', gMap.getDesigner()));
						lores.add(" ");
						lores.add(ChatColor.AQUA + "Left Click to Change");
						lores.add(ChatColor.AQUA + "the Creator.");
						ItemStack creator = SkyWarsReloaded.getNMS().getItemStack(new ItemStack(Material.SKULL_ITEM, 1, (short) 3), lores, "Map Creator");
						
						lores.clear();
						lores.add(ChatColor.GOLD + "" + gMap.getSigns().size() + " Sign Available!");
						lores.add(" ");
						lores.add(ChatColor.AQUA + "Left Click for a list");
						lores.add(ChatColor.AQUA + "of Sign Locations.");
						ItemStack signs = SkyWarsReloaded.getNMS().getItemStack(new ItemStack(Material.SIGN, 1), lores, "Join Signs");
						
						lores.clear();
						if (gMap.isRegistered()) {
							lores.add(ChatColor.AQUA + "Left Click to spectate!");
						} else {
							lores.add(ChatColor.RED + "Spectate Currently Unavailable!");
						}
						ItemStack spectate = SkyWarsReloaded.getNMS().getItemStack(new ItemStack(Material.COMPASS, 1), lores, "Spectate");
						
						lores.clear();
						lores.add(ChatColor.AQUA + "Shift Left Click to");
						lores.add(ChatColor.AQUA + "End Current Match!");
						ItemStack end = SkyWarsReloaded.getNMS().getItemStack(new ItemStack(Material.ENDER_PORTAL_FRAME, 1), lores, "End Match");
						
						lores.clear();
						lores.add(ChatColor.AQUA + "Shift Left Click to Edit Map!");
						if (gMap.isRegistered()) {
							lores.add(ChatColor.RED + "Editing will End Match");
							lores.add(ChatColor.RED + "and unregister the map!");
						}
						ItemStack edit = SkyWarsReloaded.getNMS().getItemStack(new ItemStack(Material.WORKBENCH, 1), lores, "Edit Map");
						
						menu.setItem(0, status);
						menu.setItem(2, display);
						menu.setItem(4, creator);
						menu.setItem(6, min);
						menu.setItem(8, signs);
						menu.setItem(10, spectate);
						menu.setItem(12, end);
						menu.setItem(14, edit);		
				}
			}   		
    	};
  
        SkyWarsReloaded.getIC().create(key, invs, new IconMenu.OptionClickEventHandler() {
			@Override
            public void onOptionClick(IconMenu.OptionClickEvent event) {
				Player player = event.getPlayer();
				String name = event.getName();
	            if (name.equalsIgnoreCase(SkyWarsReloaded.getNMS().getItemName(SkyWarsReloaded.getIM().getItem("exitMenuItem")))) {
	            	SkyWarsReloaded.getIC().show(player, "arenasmenu");
	            	new BukkitRunnable() {
						@Override
						public void run() {
							GameMap.updateArenasManager();
						}
					}.runTaskLater(SkyWarsReloaded.get(), 2);
	            	return;
	            }
				if(player.hasPermission("sw.map.arenas")) {       			       		
	       			if(event.getClick().equals(ClickType.SHIFT_LEFT) && event.getSlot() == 0) {
	       				if (gMap.isRegistered()) {
	       					gMap.setRegistered(false);
	       					gMap.unregister();
	       				} else {
	       					gMap.setRegistered(true);
	       					gMap.registerMap();
	       				}
	       				gMap.update();
	       			} else if (event.getClick().equals(ClickType.LEFT) && event.getSlot() == 2) {
	       				player.closeInventory();
	       				ChatListener.setTime(player.getUniqueId(), System.currentTimeMillis());
	       				ChatListener.setSetting(player.getUniqueId(), gMap.getName() + ":display");
	       				player.sendMessage(new Messaging.MessageFormatter().format("maps.changename"));
	       			} else if (event.getClick().equals(ClickType.LEFT) && event.getSlot() == 4) {
	       				player.closeInventory();
	       				ChatListener.setTime(player.getUniqueId(), System.currentTimeMillis());
	       				ChatListener.setSetting(player.getUniqueId(), gMap.getName() + ":creator");
	       				player.sendMessage(new Messaging.MessageFormatter().format("maps.changecreator"));
	       			} else if (event.getClick().equals(ClickType.LEFT) && event.getSlot() == 6) {
	       				if (gMap.isRegistered()) {
	       					if (gMap.getMinPlayers() < gMap.getMaxPlayers()) {
	               				gMap.setMinPlayers(gMap.getMinPlayers() + 1);
	               				gMap.update();
	       					}
	       				}
	       			} else if (event.getClick().equals(ClickType.RIGHT) && event.getSlot() == 6) {
	       				if (gMap.isRegistered()) {
	       					if (gMap.getMinPlayers() > 2) {
	               				gMap.setMinPlayers(gMap.getMinPlayers() - 1);
	               				gMap.update();
	       					}
	       				}
	       			} else if (event.getClick().equals(ClickType.LEFT) && event.getSlot() == 8) {
	       				player.closeInventory();
	       				for (int i = 1; i <= gMap.getSigns().size(); i++) {
	       					SWRSign swSign = gMap.getSigns().get(i-1);
	       					BlockState bs = swSign.getLocation().getBlock().getState();
	       					Sign sign = null;
	       					if (bs instanceof Sign) {
	       						sign = (Sign) bs;
	       						Block block = sign.getBlock();
	       						org.bukkit.material.Sign meteSign = new org.bukkit.material.Sign();
	       						meteSign = (org.bukkit.material.Sign) block.getState().getData();
	       						BlockFace facing = meteSign.getFacing(); 						
	       						Location loc = block.getLocation();
	       						
	       						switch(facing) {
	       					    case NORTH:
	       					        loc.setX(loc.getX() -1);
	       					        break;
	       					    case SOUTH:
	       					        loc.setX(loc.getX() + 1);
	       					        break;
	       					    case EAST:
	       					        loc.setZ(loc.getZ() + 1);
	       					        break;
	       					    case WEST:
	       					        loc.setZ(loc.getZ() + 1);
	       					        break;
								default:
									break;
	       					    }
	       						
	           					World world = swSign.getLocation().getWorld();
	           					int x = loc.getBlockX();
	           					int y = loc.getBlockY();
	           					int z = loc.getBlockZ();
	           					
	           					JSONMessage.create("Sign " + i + ": " + world.getName() + " - " + block.getLocation().getBlockX() + ", " + block.getLocation().getBlockY() + ", " + block.getLocation().getBlockZ())
	           						.color(ChatColor.GOLD)
	           						.tooltip("Click to teleport")
	           						.runCommand("/teleport " + x + " " + y + " " + z)
	           						.color(ChatColor.GOLD).send(player);
	       					}

	       				}
	       			} else if (event.getClick().equals(ClickType.LEFT) && event.getSlot() == 10) {
	       				if (gMap.isRegistered()) {
	           				player.closeInventory();
	           				MatchManager.get().addSpectator(gMap, player);
	       				}
	       			} else if (event.getClick().equals(ClickType.LEFT) && event.getSlot() == 12) {
	       				if (gMap.isRegistered() && !gMap.getMatchState().equals(MatchState.OFFLINE) && !gMap.getMatchState().equals(MatchState.ENDING)) {
	           				gMap.stopGameInProgress();
	           				gMap.refreshMap();
	       				}
	       			} else if (event.getClick().equals(ClickType.SHIFT_LEFT) && event.getSlot() == 14) {
	       				player.closeInventory();
	       				GameMap.editMap(gMap, player);
	       			}
	       		
	       		}
			}
        });
        
        
        SkyWarsReloaded.getIC().getMenu(key).setUpdate(update);
    }

}
