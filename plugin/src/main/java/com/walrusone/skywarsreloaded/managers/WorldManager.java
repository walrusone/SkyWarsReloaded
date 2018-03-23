package com.walrusone.skywarsreloaded.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.*;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;

public class WorldManager {

	public World createEmptyWorld(String name) {
		if (Bukkit.getWorld(name) == null) {
			World world = null;
			boolean loaded = loadWorld(name);
	        if (loaded) {
	        	world = Bukkit.getWorld(name);
	        }
	        if (world != null) {
				world.getBlockAt(0, 75, 0).setType(Material.STONE);
				return world;
			}
		}
		return null;
    }

	public boolean loadWorld(String worldName){
		WorldCreator worldCreator = new WorldCreator(worldName);
        worldCreator.type(WorldType.NORMAL)
                .generateStructures(false)
                .environment(World.Environment.NORMAL)
                .generator(SkyWarsReloaded.getNMS().getChunkGenerator());
		World world = worldCreator.createWorld();
        world.setDifficulty(Difficulty.NORMAL);
        world.setSpawnFlags(true, true);
        world.setPVP(true);
        world.setStorm(false);
        world.setThundering(false);
        world.setWeatherDuration(Integer.MAX_VALUE);
        world.setKeepSpawnInMemory(false);
	    world.setTicksPerAnimalSpawns(1);
	    world.setTicksPerMonsterSpawns(1);
	    world.setAutoSave(false);
        
        world.setGameRuleValue("doMobSpawning", "false");
        world.setGameRuleValue("mobGriefing", "false");
        world.setGameRuleValue("doFireTick", "false");
        world.setGameRuleValue("showDeathMessages", "false");
        
        boolean loaded = false;
        for(World w: SkyWarsReloaded.get().getServer().getWorlds()) {
          if(w.getName().equals(world.getName())) {
            loaded = true;
            break;
          }
        }
        return loaded;
	}
	
	public void unloadWorld(String w) {
	    World world = SkyWarsReloaded.get().getServer().getWorld(w);
	    if(world != null) {
	        SkyWarsReloaded.get().getServer().unloadWorld(world, false);
	    }
	}

	public void copyWorld(File source, File target){
	    try {
	        ArrayList<String> ignore = new ArrayList<>(Arrays.asList("uid.dat", "session.dat"));
	        if(!ignore.contains(source.getName())) {
	            if(source.isDirectory()) {
	                if(!target.exists())
	                if (target.mkdirs()) {
                        String[] files = source.list();
                        if (files != null) {
                            for (String file : files) {
                                File srcFile = new File(source, file);
                                File destFile = new File(target, file);
                                copyWorld(srcFile, destFile);
                            }
                        }
                    }
	            } else {
	                InputStream in = new FileInputStream(source);
	                OutputStream out = new FileOutputStream(target);
	                byte[] buffer = new byte[1024];
	                int length;
	                while ((length = in.read(buffer)) > 0)
	                    out.write(buffer, 0, length);
	                in.close();
	                out.close();
	            }
	        }
	    } catch (IOException e) {
	        SkyWarsReloaded.get().getLogger().info("Failed to copy world as required!");
	    }
	}
	
	public boolean deleteWorld(String name) {
		unloadWorld(name);
		File target = new File (SkyWarsReloaded.get().getServer().getWorldContainer().getAbsolutePath(), name);
		return deleteWorld(target);
	}
	
	@SuppressWarnings("ResultOfMethodCallIgnored")
    public boolean deleteWorld(File path) {
	      if(path.exists()) {
	          File[] files = path.listFiles();
	          if (files != null) {
                  for (File file: files) {
                      if(file.isDirectory()) {
                          deleteWorld(file);
                      } else {
                          file.delete();
                      }
                  }
              }
	      }
	      return(path.delete());
	}
	
}


