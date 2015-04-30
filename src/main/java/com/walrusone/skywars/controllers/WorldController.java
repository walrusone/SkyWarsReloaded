package com.walrusone.skywars.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;

import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.walrusone.skywars.SkyWarsReloaded;

public class WorldController {

	public World createEmptyWorld(String name) {
		for (MultiverseWorld mvworld: SkyWarsReloaded.getMV().getMVWorldManager().getMVWorlds()) {
			if (mvworld.getName().equalsIgnoreCase(name)) {
				return null;
			}
		}
		SkyWarsReloaded.getMV().getMVWorldManager().addWorld(name, Environment.NORMAL, null, null, null, "VoidWorld", false);
		
		World world = SkyWarsReloaded.get().getServer().getWorld(name);
		MultiverseWorld mvworld = SkyWarsReloaded.getMV().getMVWorldManager().getMVWorld(name);
		mvworld.setAllowAnimalSpawn(false);
		mvworld.setAllowMonsterSpawn(false);
		mvworld.setKeepSpawnInMemory(false);
		mvworld.setSpawnLocation(new Location(world, 2000, 0, 2000));
		mvworld.setDifficulty(Difficulty.NORMAL);
		mvworld.setEnableWeather(false);

        world.setAutoSave(false);

        Block b = world.getBlockAt(0, 20, 0);
        b.setType(Material.STONE);
        
        return world;
    }

	public void copyWorld(File source, File target){
	    try {
	        ArrayList<String> ignore = new ArrayList<String>(Arrays.asList("uid.dat", "session.dat"));
	        if(!ignore.contains(source.getName())) {
	            if(source.isDirectory()) {
	                if(!target.exists())
	                target.mkdirs();
	                String files[] = source.list();
	                for (String file : files) {
	                    File srcFile = new File(source, file);
	                    File destFile = new File(target, file);
	                    copyWorld(srcFile, destFile);
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
	 
	    }
	}
	
	public boolean deleteWorld(File path) {
	      if(path.exists()) {
	          File files[] = path.listFiles();
	          for(int i=0; i<files.length; i++) {
	              if(files[i].isDirectory()) {
	                  deleteWorld(files[i]);
	              } else {
	                  files[i].delete();
	              }
	          }
	      }
	      return(path.delete());
	}
	
}
