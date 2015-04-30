package com.walrusone.skywars.game;

import java.io.File;
import java.util.Map;

import org.bukkit.Chunk;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;

import com.google.common.collect.Maps;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.controllers.WorldController;
import com.walrusone.skywars.utilities.EmptyChest;
import com.walrusone.skywars.utilities.VotingSign;

public class GameMap {

	private File source;
	private File rootDirectory;
	private String name;
	private Map<Integer, Location> spawnPoints = Maps.newHashMap();
	private Map<Integer, VotingSign> signs = Maps.newHashMap();
	private Map<Integer, EmptyChest> chests = Maps.newHashMap();
	private Block min;
	private Block max;
	private int minX;
	private int minZ;
	private int minY = 0;
	private int maxX;
	private int maxZ;
	private int maxY = 0;
	
	public GameMap(String name, File filepath) {
		int size = SkyWarsReloaded.get().getConfig().getInt("gameVariables.maxMapSize");
		int max = size/2;
		int min = -size/2;
		minX = min;
		minZ = min;
		maxX = max;
		maxZ = max;		
		this.source = filepath;
		this.name = name.toLowerCase();
		String root = SkyWarsReloaded.get().getServer().getWorldContainer().getAbsolutePath();
		rootDirectory = new File(root);
		ChunkIterator();
	}

	public void addSign(Integer x, VotingSign y) {
		signs.put(x, y);
	}
	
	public String getName() {
		return name;
	}
	
	public boolean containsSpawns() {
		if (!name.equalsIgnoreCase("lobby") && spawnPoints.size() >= 2) {
			return true;
		} else if (name.equalsIgnoreCase("lobby") && spawnPoints.size() >= 1) {
			return true;
		}
		return false;
	}
	
	public boolean containsSigns() {
		if (signs.size() <= 0) {
			return false;
		} 
		return true;
	}
	
	public Map<Integer, Location> getSpawns(){
		return spawnPoints;
	}
	
	public Map<Integer, VotingSign> getSigns(){
		return signs;
	}
	
	public Map<Integer, EmptyChest> getChests(){
		return chests;
	}
	
	public boolean loadMap(int gNumber) {
		WorldController wc = SkyWarsReloaded.getWC();
		String mapName = name + "_" + gNumber;
		boolean mapExists = false;
		for (MultiverseWorld mvworld: SkyWarsReloaded.getMV().getMVWorldManager().getMVWorlds()) {
			if (mvworld.getName().equalsIgnoreCase(mapName)) {
				mapExists = true;
			}
		}
		if (mapExists) {
			SkyWarsReloaded.getMV().getMVWorldManager().deleteWorld(mapName);
		}
		File target = new File(rootDirectory, mapName);
		wc.copyWorld(source, target);
		boolean loaded = SkyWarsReloaded.getMV().getMVWorldManager().addWorld(mapName, Environment.NORMAL, null, null, null, "VoidWorld", false);
		if (loaded) {
			MultiverseWorld mvworld = SkyWarsReloaded.getMV().getMVWorldManager().getMVWorld(mapName);
			World world = SkyWarsReloaded.get().getServer().getWorld(mapName);
		    world.setAutoSave(false);
		    mvworld.setSpawnLocation(new Location(world, 2000, 0, 2000));
			mvworld.setAllowAnimalSpawn(false);
			mvworld.setAllowMonsterSpawn(false);
			mvworld.setKeepSpawnInMemory(false);
			mvworld.setDifficulty(Difficulty.NORMAL);
			mvworld.setEnableWeather(false);
			if (name.equals("lobby")) {
				mvworld.setHunger(false);
				mvworld.setPVPMode(false);
			}
		}
		return loaded;
	}
	
	public void ChunkIterator() {
		World chunkWorld = SkyWarsReloaded.get().getServer().getWorld(name);
		min = chunkWorld.getBlockAt(minX, minY, minZ);
		max = chunkWorld.getBlockAt(maxX, maxY, maxZ);
		Chunk cMin = min.getChunk();
		Chunk cMax = max.getChunk();
		int countSpawns = 1;
		int countSigns = 0;
		int countChests = 0;
		
		for(int cx = cMin.getX(); cx < cMax.getX(); cx++) {
			for(int cz = cMin.getZ(); cz < cMax.getZ(); cz++) {
		           Chunk currentChunk = chunkWorld.getChunkAt(cx, cz);
		           currentChunk.load(true);

		           for(BlockState te : currentChunk.getTileEntities()) {
		               	if(te instanceof Beacon){
			                  Beacon beacon = (Beacon) te;
			                  Location loc = beacon.getLocation();
			                  spawnPoints.put(countSpawns, loc);
			                  countSpawns++;
			               } else if (te instanceof Sign) {
				                  Sign sign = (Sign) te;
				                  int x = sign.getX();
				                  int z = sign.getZ();
				                  int y = sign.getY();
				                  signs.put(countSigns, new VotingSign(x, y, z));
				                  countSigns++;
			               } else if (te instanceof Chest) {
				                  Chest chest = (Chest) te;
				                  int x = chest.getX();
				                  int z = chest.getZ();
				                  int y = chest.getY();
				                  chests.put(countChests, new EmptyChest(x, y, z));
				                  countChests++;
			               }
		           }
		        }
	     }
	}
	
}
