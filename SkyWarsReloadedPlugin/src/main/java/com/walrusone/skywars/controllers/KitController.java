package com.walrusone.skywars.controllers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.GameKit;
import com.walrusone.skywars.game.GamePlayer;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class KitController {

    private final Map<String, GameKit> kitMap = Maps.newHashMap();

    public KitController() {
        load();
    }

    public void load() {
        kitMap.clear();
        File dataDirectory = SkyWarsReloaded.get().getDataFolder();
        File kitsDirectory = new File(dataDirectory, "kits");

        if (!kitsDirectory.exists()) {
            if (!kitsDirectory.mkdirs())  {
                return;
            }
            SkyWarsReloaded.get().saveResource("example.yml", true);
            copyFiles(new File(dataDirectory, "example.yml"), new File(kitsDirectory, "example.yml"));
            File delete = new File(dataDirectory, "example.yml");
            delete.delete();
        }

        File[] kits = kitsDirectory.listFiles();
        if (kits == null) {
            return;
        }

        for (File kit : kits) {
            if (!kit.getName().endsWith(".yml")) {
                continue;
            }

            String name = kit.getName().replace(".yml", "");

            if (!name.isEmpty() && !kitMap.containsKey(name)) {
                kitMap.put(name, new GameKit(name, YamlConfiguration.loadConfiguration(kit), kit));
            }
        }
    }
    
    public void copyFiles(File source, File target){
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
	                    copyFiles(srcFile, destFile);
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
    
    public void populateInventory(Inventory inventory, GameKit kit) {
        for (ItemStack itemStack : kit.getItems()) {
        	try {
                inventory.addItem(itemStack);
        	} catch(NullPointerException e) {
        	}

        }
    }
    
    public void givePotionEffects(GamePlayer gamePlayer, GameKit kit) {
    	for (PotionEffect pEffect: kit.getPotionEffects()) {
    		if (gamePlayer.getP() != null) {
        		gamePlayer.getP().addPotionEffect(pEffect);
    		}
    	}
    }

    public GameKit getByName(String name) {
    	for (String kitName: kitMap.keySet()) {
    		if (kitMap.get(kitName).getKitName().equalsIgnoreCase(name)) {
    			return kitMap.get(kitName);
    		}
    	}
		return null;
    }

    public List<GameKit> getKits() {
    	return Lists.newArrayList(kitMap.values());
    }

}
