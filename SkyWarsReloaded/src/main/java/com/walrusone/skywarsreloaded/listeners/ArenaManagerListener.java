package com.walrusone.skywarsreloaded.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.objects.GameMap;
import com.walrusone.skywarsreloaded.objects.SWRSign;
import com.walrusone.skywarsreloaded.utilities.Messaging;

import me.rayzr522.jsonmessage.JSONMessage;

public class ArenaManagerListener implements Listener
{
   
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
    	Player player = (Player) e.getWhoClicked();
       	int rawSlot = e.getRawSlot();
       	if (e.getInventory().equals(GameMap.getArenasManager()) && player.hasPermission("sw.map.arenas")) {
       		e.setCancelled(true);
       		if (rawSlot < e.getInventory().getSize() && rawSlot >= 0) {
       			if (e.getClick().equals(ClickType.LEFT)) {
       				ItemStack item = e.getCurrentItem();
       				if (item != null) {
       					if (item.getType().equals(Material.WOOL)) {
           					String name = SkyWarsReloaded.getNMS().getItemName(item);
           					GameMap gMap = GameMap.getMap(name);
           					if (gMap != null) {
           						gMap.openArenaManager(player);
           						gMap.updateArenaManager();
           					}
       					} else if (e.getClick().equals(ClickType.LEFT) && rawSlot == e.getInventory().getSize()-1) {
       	       				player.closeInventory();
       	       			}
       				}
       			} 
       		}
       	}
       	if(e.getInventory().getName().contains("Arena Manager") && player.hasPermission("sw.map.arenas")) {
       		e.setCancelled(true);
       		String[] name = e.getInventory().getName().split(": ");
       		GameMap gMap = GameMap.getMap(name[1]);
       		
       		if (gMap == null) {
       			return;
       		}
       		
       		if (rawSlot < e.getInventory().getSize() && rawSlot >= 0) {
       			if(e.getClick().equals(ClickType.SHIFT_LEFT) && rawSlot == 0) {
       				if (gMap.isRegistered()) {
       					gMap.setRegistered(false);
       					gMap.unregister();
       				} else {
       					gMap.setRegistered(true);
       					gMap.attemptRegistration();
       				}
       				gMap.update();
       			} else if (e.getClick().equals(ClickType.LEFT) && rawSlot == 2) {
       				player.closeInventory();
       				ChatListener.setTime(player.getUniqueId(), System.currentTimeMillis());
       				ChatListener.setSetting(player.getUniqueId(), gMap.getName() + ":display");
       				player.sendMessage(new Messaging.MessageFormatter().format("maps.changename"));
       			} else if (e.getClick().equals(ClickType.LEFT) && rawSlot == 4) {
       				player.closeInventory();
       				ChatListener.setTime(player.getUniqueId(), System.currentTimeMillis());
       				ChatListener.setSetting(player.getUniqueId(), gMap.getName() + ":creator");
       				player.sendMessage(new Messaging.MessageFormatter().format("maps.changecreator"));
       			} else if (e.getClick().equals(ClickType.LEFT) && rawSlot == 6) {
       				if (gMap.isRegistered()) {
       					if (gMap.getMinPlayers() < gMap.getMaxPlayers()) {
               				gMap.setMinPlayers(gMap.getMinPlayers() + 1);
               				gMap.update();
       					}
       				}
       			} else if (e.getClick().equals(ClickType.RIGHT) && rawSlot == 6) {
       				if (gMap.isRegistered()) {
       					if (gMap.getMinPlayers() > 2) {
               				gMap.setMinPlayers(gMap.getMinPlayers() - 1);
               				gMap.update();
       					}
       				}
       			} else if (e.getClick().equals(ClickType.LEFT) && rawSlot == 8) {
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
       			} else if (e.getClick().equals(ClickType.LEFT) && rawSlot == 10) {
       				if (gMap.isRegistered()) {
           				player.closeInventory();
           				MatchManager.get().addSpectator(gMap, player);
       				}
       			} else if (e.getClick().equals(ClickType.LEFT) && rawSlot == 12) {
       				if (gMap.isRegistered() && !gMap.getMatchState().equals(MatchState.OFFLINE) && !gMap.getMatchState().equals(MatchState.ENDING)) {
           				gMap.stopGameInProgress();
           				gMap.refreshMap();
       				}
       			} else if (e.getClick().equals(ClickType.LEFT) && rawSlot == 14) {
       				player.closeInventory();
       				GameMap.editMap(gMap, player);
       			} else if (e.getClick().equals(ClickType.LEFT) && rawSlot == e.getInventory().getSize()-1) {
       				player.closeInventory();
       				GameMap.openArenasManager(player);
       			}
       		}       		
       	}   	
    }
}
