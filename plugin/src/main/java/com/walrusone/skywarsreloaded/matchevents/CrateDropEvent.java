package com.walrusone.skywarsreloaded.matchevents;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.utilities.Util;

public class CrateDropEvent extends MatchEvent {

	private int maxNumOfCrates;
	private int maxItemsPerCrate;
	private BukkitTask br;
	
	public CrateDropEvent(GameMap map, boolean b) {
		this.gMap = map;
		this.enabled = b;
		File dataDirectory = SkyWarsReloaded.get().getDataFolder();
        File mapDataDirectory = new File(dataDirectory, "mapsData");

        if (!mapDataDirectory.exists() && !mapDataDirectory.mkdirs()) {
        	return;
        }
        
        File mapFile = new File(mapDataDirectory, gMap.getName() + ".yml");
	    if (mapFile.exists()) {
	    	eventName = "CrateDropEvent";
	    	slot = 6;
	    	material = new ItemStack(Material.ENDER_CHEST, 1);
	        FileConfiguration fc = YamlConfiguration.loadConfiguration(mapFile);
	        this.min = fc.getInt("events." + eventName + ".minStart");
	        this.max = fc.getInt("events." + eventName + ".maxStart");
	        this.length = fc.getInt("events." + eventName + ".length");
	        this.chance = fc.getInt("events." + eventName + ".chance");
	        this.title = fc.getString("events." + eventName + ".title");
	        this.subtitle = fc.getString("events." + eventName + ".subtitle");
	        this.startMessage = fc.getString("events." + eventName + ".startMessage");
	        this.endMessage = fc.getString("events." + eventName + ".endMessage");
	        this.announceEvent = fc.getBoolean("events." + eventName + ".announceTimer");
	        this.repeatable = fc.getBoolean("events." + eventName + ".repeatable");
	        this.maxItemsPerCrate = fc.getInt("events." + eventName + ".maxItemsPerCrate");
	        this.maxNumOfCrates = fc.getInt("events." + eventName + ".maxNumOfCrates");
	    }
	}
	
	@Override
	public void doEvent() {
		if (gMap.getMatchState() == MatchState.PLAYING) {
			this.fired = true;
			sendTitle();
			if (addCrates() < 1) {
				new BukkitRunnable() {
					@Override
					public void run() {
						addCrates();
					}
				}.runTaskLater(SkyWarsReloaded.get(), 10L);
			}
			if (length != -1) {
				br = new BukkitRunnable() {
					@Override
					public void run() {
						endEvent(false);
					}
				}.runTaskLater(SkyWarsReloaded.get(), length * 20L);
			}
		}
	}

	@Override
	public void endEvent(boolean force) {
		if (fired) {
			if (force) {
				if (length != -1) {
					br.cancel();
				}
			}
			if (gMap.getMatchState() == MatchState.PLAYING) {
				MatchManager.get().message(gMap, ChatColor.translateAlternateColorCodes('&', endMessage));
			}
			gMap.removeCrates();
			if (repeatable || force) {
				setStartTime();
				this.startTime = this.startTime + gMap.getTimer();
				this.fired = false;
			}
		}
	}
	
	private int addCrates() {
		int spawned = 0;
		World world = gMap.getCurrentWorld();
		int cratesToAdd = Util.get().getRandomNum(0, maxNumOfCrates);
		for (int i = 0; i < cratesToAdd; i++) {
			Location loc = new Location (world, gMap.getSpectateSpawn().getX(), 0, gMap.getSpectateSpawn().getZ());
			Location loc2 = new Location (world, gMap.getPlayerCards().get(0).getSpawn().getX(), gMap.getPlayerCards().get(0).getSpawn().getY(), gMap.getPlayerCards().get(0).getSpawn().getZ());
			int distance = (int) Math.hypot(loc.getX()-loc2.getX(), loc.getZ()-loc2.getZ());
			int y = loc2.getBlockY();
			Location spawn = new Location(world, loc.getBlockX() + Util.get().getRandomNum(-distance, distance), 0, loc.getBlockZ() + Util.get().getRandomNum(-distance, distance));
			Block block = world.getHighestBlockAt(spawn);
			if (block != null && !block.getType().equals(Material.AIR)) {
				spawn = block.getLocation();
			} else {
				int range = Util.get().getRandomNum(y-15, y+15);
				spawn = new Location(world, spawn.getBlockX(), range, spawn.getBlockZ());
				world.getBlockAt(spawn).setType(Material.COBBLESTONE);
			}
			gMap.addCrate(spawn, maxItemsPerCrate);
			spawned++;
		}
		return spawned;
	}
	
	@Override
	public void saveEventData() {
		File dataDirectory = SkyWarsReloaded.get().getDataFolder();
        File mapDataDirectory = new File(dataDirectory, "mapsData");

        if (!mapDataDirectory.exists() && !mapDataDirectory.mkdirs()) {
        	return;
        }
        
        File mapFile = new File(mapDataDirectory, gMap.getName() + ".yml");
	    if (mapFile.exists()) {
	        FileConfiguration fc = YamlConfiguration.loadConfiguration(mapFile);
	        fc.set("events." + eventName + ".enabled", this.enabled);
	        fc.set("events." + eventName + ".minStart", this.min);
	        fc.set("events." + eventName + ".maxStart", this.max);
	        fc.set("events." + eventName + ".length", this.length);
	        fc.set("events." + eventName + ".chance", this.chance);
	        fc.set("events." + eventName + ".title", this.title);
	        fc.set("events." + eventName + ".subtitle", this.subtitle);
	        fc.set("events." + eventName + ".startMessage",  this.startMessage);
	        fc.set("events." + eventName + ".endMessage", this.endMessage);
	        fc.set("events." + eventName + ".announceTimer", this.announceEvent);
	        fc.set("events." + eventName + ".repeatable", this.repeatable);
	        fc.set("events." + eventName + ".maxItemsPerCrate", this.maxItemsPerCrate);
	        fc.set("events." + eventName + ".maxNumOfCrates", this.maxNumOfCrates);
	        try {
				fc.save(mapFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}
}
