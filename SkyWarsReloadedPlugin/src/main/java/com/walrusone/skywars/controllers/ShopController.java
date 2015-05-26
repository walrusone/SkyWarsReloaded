package com.walrusone.skywars.controllers;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.utilities.ItemUtils;
import com.walrusone.skywars.utilities.ShopItem;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ShopController {

    private final Map<String, ShopItem> shopMap = Maps.newHashMap();
    private final Map<String, ShopItem> specShopMap = Maps.newHashMap();

    public ShopController() {
        load();
    }

    public void load() {
        shopMap.clear();
        File shopFile = new File(SkyWarsReloaded.get().getDataFolder(), "shop.yml");

        if (!shopFile.exists()) {
        	SkyWarsReloaded.get().saveResource("shop.yml", false);
        }

        if (shopFile.exists()) {
            FileConfiguration storage = YamlConfiguration.loadConfiguration(shopFile);

            if (storage.contains("items")) {
                for (String item : storage.getStringList("items")) {
                    List<String> itemData = new LinkedList<String>(Arrays.asList(item.split(" ")));

                    int cost = Integer.parseInt(itemData.get(0));
                    itemData.remove(itemData.get(0));
                    
                    ItemStack itemStack = ItemUtils.parseItem(itemData);
                    
                    
                    if (itemStack != null) {
                        shopMap.put(ChatColor.stripColor(SkyWarsReloaded.getNMS().getName(itemStack)), new ShopItem(itemStack, cost));
                    }
                }
            }
        }
        
        specShopMap.clear();
        File specShopFile = new File(SkyWarsReloaded.get().getDataFolder(), "spectateshop.yml");

        if (!specShopFile.exists()) {
        	SkyWarsReloaded.get().saveResource("spectateshop.yml", false);
        }

        if (specShopFile.exists()) {
            FileConfiguration storage = YamlConfiguration.loadConfiguration(specShopFile);

            if (storage.contains("items")) {
                for (String item : storage.getStringList("items")) {
                    List<String> itemData = new LinkedList<String>(Arrays.asList(item.split(" ")));

                    int cost = Integer.parseInt(itemData.get(0));
                    itemData.remove(itemData.get(0));
                    
                    ItemStack itemStack = ItemUtils.parseItem(itemData);
                    
                    
                    if (itemStack != null) {
                        specShopMap.put(SkyWarsReloaded.getNMS().getName(itemStack), new ShopItem(itemStack, cost));
                    }
                }
            }
        }
    }
    
 

    public ShopItem getByName(String name) {
        return shopMap.get(name);
    }
    
    public ShopItem getBySpecName(String name) {
        return specShopMap.get(name);
    }

    public List<ShopItem> getShopItems() {
    	return Lists.newArrayList(shopMap.values());
    }

    public List<ShopItem> getSpecShopItems() {
    	return Lists.newArrayList(specShopMap.values());
    }
    

    
}
