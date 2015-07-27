package com.walrusone.skywars.controllers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.utilities.Messaging;
import com.walrusone.skywars.utilities.ParticleItem;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ParticleController {

    private final Map<String, ParticleItem> particleMap = Maps.newHashMap();
    private final List<String> effects = Arrays.asList("water", "flame", "smoke", "critical", "slime", "snow", "magic", 
    		"music", "happy", "angry", "potion", "poison", "alphabet", "lava", "lava_drip", "heart", "redstone", "sparks", "portal", "clouds");
    
    public ParticleController() {
        load();
    }

    public void load() {
        particleMap.clear();
        File particleFile = new File(SkyWarsReloaded.get().getDataFolder(), "particleeffects.yml");

        if (!particleFile.exists()) {
        	SkyWarsReloaded.get().saveResource("particleeffects.yml", false);
        }

        if (particleFile.exists()) {
            FileConfiguration storage = YamlConfiguration.loadConfiguration(particleFile);

            if (storage.contains("effects")) {
                for (String item : storage.getStringList("effects")) {
                    List<String> itemData = new LinkedList<String>(Arrays.asList(item.split(" ")));

                    int cost = Integer.parseInt(itemData.get(1));
        
                    String effect = itemData.get(0).toLowerCase();
                    String name = null;

                    if (effects.contains(effect)) {
                    	name = new Messaging.MessageFormatter().format("effects." + effect);
                    }
                    
                    if (name != null) {
                    	particleMap.put(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', name)), new ParticleItem(effect, name, cost));
                    }
                }
            }
        }
    }
    
 
    public ParticleItem getByName(String name) {
        return particleMap.get(name);
    }
    
    public List<ParticleItem> getParticleItems() {
    	return Lists.newArrayList(particleMap.values());
    }
    
    public ParticleItem getByEffect(String effect) {
    	for (ParticleItem pItem: particleMap.values()) {
    		if (pItem.getEffect().equalsIgnoreCase(effect)) {
    			return pItem;
    		}
    	}
        return null;
    }
    
}
