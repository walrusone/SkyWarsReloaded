package com.walrusone.skywarsreloaded.matchevents;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.managers.MatchManager;

public class ArrowRainEvent extends MatchEvent {

	public int count; 
	public int per2Tick;
	private BukkitTask br;
	
	public ArrowRainEvent(GameMap map, boolean b) {
		this.gMap = map;
		this.enabled = b;
		File dataDirectory = SkyWarsReloaded.get().getDataFolder();
        File mapDataDirectory = new File(dataDirectory, "mapsData");

        if (!mapDataDirectory.exists() && !mapDataDirectory.mkdirs()) {
        	return;
        }
        
        File mapFile = new File(mapDataDirectory, gMap.getName() + ".yml");
	    if (mapFile.exists()) {
	    	eventName = "ArrowRainEvent";
	    	slot = 2;
	    	material = new ItemStack(Material.ARROW, 1);
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
	        this.per2Tick = fc.getInt("events." + eventName + ".spawnPer2Tick");
	       }
	}
	
	@Override
	public void doEvent() {
		if (gMap.getMatchState() == MatchState.PLAYING) {
			this.fired = true;
			sendTitle();
			World world = gMap.getCurrentWorld();
			br = new BukkitRunnable() {
				@Override
				public void run() {
					if (length != -1) {
						if (count/10 >= length) {
							endEvent(false);
						}
					}
					if (gMap.getMatchState().equals(MatchState.PLAYING)) {
						for (Player player: gMap.getAlivePlayers()) {
							if (player != null) {
								Location loc = player.getLocation();
								for (int i = 0; i < per2Tick; i++) {
									world.spawnEntity(loc.add(ThreadLocalRandom.current().nextDouble(-3, 3), ThreadLocalRandom.current().nextDouble(10, 20), ThreadLocalRandom.current().nextDouble(-3, 3)), EntityType.ARROW);
								}
							}
						}
					} else {
						endEvent(false);
					}
					count++;
				}
			}.runTaskTimer(SkyWarsReloaded.get(), 0, 2);
		}
	}

	@Override
	public void endEvent(boolean force) {
		if (fired) {
			br.cancel();
			if (gMap.getMatchState() == MatchState.PLAYING) {
				MatchManager.get().message(gMap, ChatColor.translateAlternateColorCodes('&', endMessage));
			}
			if (repeatable || force) {
				setStartTime();
				this.startTime = this.startTime + gMap.getTimer();
				this.fired = false;
				this.count = 0;
			}
		}
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
	        fc.set("events." + eventName + ".spawnPer2Tick", this.per2Tick);
	        try {
				fc.save(mapFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}
}
