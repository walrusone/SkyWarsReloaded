package com.walrusone.skywars.controllers;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.utilities.GlassColor;
import com.walrusone.skywars.utilities.Messaging;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GlassController {

    private final Map<String, GlassColor> colorMap = Maps.newHashMap();

    public GlassController() {
        load();
    }

    public void load() {
        colorMap.clear();
        File glassFile = new File(SkyWarsReloaded.get().getDataFolder(), "glasscolors.yml");

        if (!glassFile.exists()) {
        	SkyWarsReloaded.get().saveResource("glasscolors.yml", false);
        }

        if (glassFile.exists()) {
            FileConfiguration storage = YamlConfiguration.loadConfiguration(glassFile);

            if (storage.contains("colors")) {
                for (String item : storage.getStringList("colors")) {
                    List<String> itemData = new LinkedList<String>(Arrays.asList(item.split(" ")));

                    int cost = Integer.parseInt(itemData.get(1));
        
                    String color = itemData.get(0).toLowerCase();
                    ItemStack itemStack = new ItemStack(Material.GLASS, 1);
                    String name = "glass";
                    
                    switch (color) {
                    case "white": itemStack = new ItemStack(Material.STAINED_GLASS, 1, (short)0);
                    name = new Messaging.MessageFormatter().format("colors.white");
                    break;
                    case "orange": itemStack = new ItemStack(Material.STAINED_GLASS, 1, (short)1);
                    name = new Messaging.MessageFormatter().format("colors.orange");
      			  	break;
                    case "magenta": itemStack = new ItemStack(Material.STAINED_GLASS, 1, (short)2);
                    name = new Messaging.MessageFormatter().format("colors.magenta");
      			  	break;
                    case "lightblue": itemStack = new ItemStack(Material.STAINED_GLASS, 1, (short)3);
                    name = new Messaging.MessageFormatter().format("colors.lightblue");
      			  	break;
                    case "yellow": itemStack = new ItemStack(Material.STAINED_GLASS, 1, (short)4);
                    name = new Messaging.MessageFormatter().format("colors.yellow");
      			  	break;
                    case "lime": itemStack = new ItemStack(Material.STAINED_GLASS, 1, (short)5);
                    name = new Messaging.MessageFormatter().format("colors.lime");
      			  	break;
                    case "pink": itemStack = new ItemStack(Material.STAINED_GLASS, 1, (short)6);
                    name = new Messaging.MessageFormatter().format("colors.pink");
      			  	break;
                    case "gray": itemStack = new ItemStack(Material.STAINED_GLASS, 1, (short)7);
                    name = new Messaging.MessageFormatter().format("colors.gray");
      			  	break;
                    case "lightgray": itemStack = new ItemStack(Material.STAINED_GLASS, 1, (short)8);
                    name = new Messaging.MessageFormatter().format("colors.lightgray");
      			  	break;
                    case "cyan": itemStack = new ItemStack(Material.STAINED_GLASS, 1, (short)9);
                    name = new Messaging.MessageFormatter().format("colors.cyan");
      			  	break;
                    case "purple": itemStack = new ItemStack(Material.STAINED_GLASS, 1, (short)10);
                    name = new Messaging.MessageFormatter().format("colors.purple");
      			  	break;
                    case "blue": itemStack = new ItemStack(Material.STAINED_GLASS, 1, (short)11);
                    name = new Messaging.MessageFormatter().format("colors.blue");
      			  	break;
                    case "brown": itemStack = new ItemStack(Material.STAINED_GLASS, 1, (short)12);
                    name = new Messaging.MessageFormatter().format("colors.brown");
      			  	break;
                    case "green": itemStack = new ItemStack(Material.STAINED_GLASS, 1, (short)13);
                    name = new Messaging.MessageFormatter().format("colors.green");
      			  	break;
                    case "red": itemStack = new ItemStack(Material.STAINED_GLASS, 1, (short)14);
                    name = new Messaging.MessageFormatter().format("colors.red");
      			  	break;
                    case "black": itemStack = new ItemStack(Material.STAINED_GLASS, 1, (short)15);
                    name = new Messaging.MessageFormatter().format("colors.black");
      			  	break;
      			    }  
                    
                    if (itemStack != null) {
                        colorMap.put(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', name)), new GlassColor(color, name, itemStack, cost));
                    }
                }
            }
        }
    }
    
 
    public GlassColor getByName(String name) {
        return colorMap.get(name);
    }
    
    public GlassColor getByColor(String color) {
    	for (GlassColor glassColor: colorMap.values()) {
    		if (glassColor.getColor().equalsIgnoreCase(color)) {
    			return glassColor;
    		}
    	}
        return null;
    }
    
    public List<GlassColor> getColorItems() {
    	return Lists.newArrayList(colorMap.values());
    }
    
}
