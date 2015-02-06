package com.walrusone.skywars.game;

import java.io.File;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;

import com.google.common.collect.Maps;
import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.controllers.WorldController;
import com.walrusone.skywars.utilities.EmptyChest;
import com.walrusone.skywars.utilities.SpawnPoint;
import com.walrusone.skywars.utilities.VotingSign;

public class GameMap {

	private File source;
	private File rootDirectory;
	private String name;
	private Map<Integer, SpawnPoint> spawnPoints = Maps.newHashMap();
	private Map<Integer, VotingSign> signs = Maps.newHashMap();
	private Map<Integer, EmptyChest> chests = Maps.newHashMap();
	private World world;
	private Block min;
	private Block max;
	private int minX = -200;
	private int minZ = -200;
	private int minY = 0;
	private int maxX = 200;
	private int maxZ = 200;
	private int maxY = 0;
	
	public GameMap(String name, File filepath) {
		this.source = filepath;
		this.name = name.toLowerCase();
		String root = SkyWarsReloaded.get().getServer().getWorldContainer().getAbsolutePath();
		rootDirectory = new File(root);
		ChunkIterator();
	}

	public void addSpawn(Integer x, SpawnPoint y) {
		spawnPoints.put(x, y);
	}
	
	public void addSign(Integer x, VotingSign y) {
		signs.put(x, y);
	}
	
	public boolean containsSpawns() {
		if ((!name.equalsIgnoreCase("lobby") && spawnPoints.size() < SkyWarsReloaded.get().getConfig().getInt("gameVariables.numberOfSpawns"))) {
			return false;
		} else if (name.equalsIgnoreCase("lobby") && spawnPoints.size() < 1) {
			return false;
		}
		return true;
	}
	
	public boolean containsSigns() {
		if (signs.size() <= 0) {
			return false;
		} 
		return true;
	}
	
	public Map<Integer, SpawnPoint> getSpawns(){
		return spawnPoints;
	}
	
	public Map<Integer, VotingSign> getSigns(){
		return signs;
	}
	
	public boolean loadMap(int gNumber) {
		WorldController wc = SkyWarsReloaded.getWC();
		String mapName = name + "_" + gNumber;
		File target = new File(rootDirectory, mapName);
		wc.copyWorld(source, target);
		wc.loadWorld(mapName);
		for(int cNum: chests.keySet()) {
			int x = chests.get(cNum).getX();
			int y = chests.get(cNum).getY();
			int z = chests.get(cNum).getZ();
			Chest chest = (Chest) SkyWarsReloaded.get().getServer().getWorld(mapName).getBlockAt(x, y, z).getState();
			SkyWarsReloaded.getCC().populateChest(chest);
		}
		for(World w: Bukkit.getServer().getWorlds()) {
		  if(w.getName().equals(mapName)) {
			  world = Bukkit.getServer().getWorld(mapName);
			  return true;
		  }
		}
		return false;
	}
	
	public void deleteMap(int gNumber) {
		WorldController wc = SkyWarsReloaded.getWC();
		String mapName = name + "_" + gNumber;
		File target = new File(rootDirectory, mapName);
		wc.unloadWorld(mapName);
		wc.deleteWorld(target);
	}

	public World getWorld() {
		return world;
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
			                  double x = beacon.getBlock().getX();
			                  double z = beacon.getBlock().getZ();
			                  double y = beacon.getBlock().getY();
			                  spawnPoints.put(countSpawns, new SpawnPoint(x, y, z));
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
