package com.walrusone.skywarsreloaded.matchevents;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.menus.gameoptions.objects.CoordLoc;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GhastEvent extends MatchEvent {

	private ArrayList<Entity> mobsSpawned = new ArrayList<>();
	private BukkitTask br1;
	private BukkitTask br2;

	public GhastEvent(GameMap map, boolean b) {
		this.gMap = map;
		this.enabled = b;
		File dataDirectory = SkyWarsReloaded.get().getDataFolder();
        File mapDataDirectory = new File(dataDirectory, "mapsData");

        if (!mapDataDirectory.exists() && !mapDataDirectory.mkdirs()) {
        	return;
        }
        
        File mapFile = new File(mapDataDirectory, gMap.getName() + ".yml");
	    if (mapFile.exists()) {
	    	eventName = "GhastEvent";
	    	slot = 17;
    		material = new ItemStack(Material.GHAST_TEAR, 1);
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
	    }
	}
	
	@Override
	public void doEvent() {
		if (gMap.getMatchState() == MatchState.PLAYING) {
			this.fired = true;
			sendTitle();
			CoordLoc loc = gMap.getSpectateSpawn();
			World world = gMap.getCurrentWorld();
			Location location = new Location(world, loc.getX(), loc.getY(), loc.getZ());
			br1 = new BukkitRunnable() {
				@Override
				public void run() {
					for (int i = 0; i < gMap.getTeamCards().size(); i++) {
						Location loc = new Location(gMap.getCurrentWorld(), gMap.getTeamCards().get(i).getSpawn().getX(), gMap.getTeamCards().get(i).getSpawn().getY(), gMap.getTeamCards().get(i).getSpawn().getZ());
						Location spawn;
						do {
							spawn = loc.clone().add(0, 10, 0);
						} while (!loc.getBlock().getType().equals(Material.AIR));
						LivingEntity ent = (LivingEntity) world.spawnEntity(spawn, EntityType.GHAST);
						mobsSpawned.add(ent);
					}


				}
			}.runTaskLater(SkyWarsReloaded.get(),3L);
			if (length != -1) {
				br2 = new BukkitRunnable() {
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
				br1.cancel();
				if (length != -1) {
					br2.cancel();
				}
			}
			for (Entity ent: mobsSpawned) {
				if (ent != null && !ent.isDead()) {
					ent.remove();
				}
			}
			mobsSpawned.clear();
			if (gMap.getMatchState() == MatchState.PLAYING) {
				MatchManager.get().message(gMap, ChatColor.translateAlternateColorCodes('&', endMessage));
			}
			if (repeatable || force) {
				setStartTime();
				this.startTime = this.startTime + gMap.getTimer();
				this.fired = false;
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
	        try {
				fc.save(mapFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}

}
