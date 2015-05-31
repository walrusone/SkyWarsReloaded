package com.walrusone.skywars.controllers;

import com.google.common.collect.Lists;
import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.utilities.ItemUtils;

import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ChestController {

    private final List<ChestItem> chestItemList = Lists.newArrayList();
    private final List<ChestItem> opChestItemList = Lists.newArrayList();
    private final List<ChestItem> basicChestItemList = Lists.newArrayList();
    private final Random random = new Random();
   
    private List<Integer> randomLoc = new ArrayList<Integer>();
    private List<Integer> randomDLoc = new ArrayList<Integer>();


    public ChestController() {
        load();
        for (int i = 0; i < 27; i++) {
        	randomLoc.add(i);
        }
        for (int i = 0; i < 54; i++) {
        	randomDLoc.add(i);
        }
    }

    public void load() {
        chestItemList.clear();
        File chestFile = new File(SkyWarsReloaded.get().getDataFolder(), "chest.yml");

        if (!chestFile.exists()) {
        	SkyWarsReloaded.get().saveResource("chest.yml", false);
        }

        if (chestFile.exists()) {
            FileConfiguration storage = YamlConfiguration.loadConfiguration(chestFile);

            if (storage.contains("items")) {
                for (String item : storage.getStringList("items")) {
                    List<String> itemData = new LinkedList<String>(Arrays.asList(item.split(" ")));

                    int chance = Integer.parseInt(itemData.get(0));
                    itemData.remove(itemData.get(0));
                    
                    ItemStack itemStack = ItemUtils.parseItem(itemData);
                    
                    
                    if (itemStack != null) {
                        chestItemList.add(new ChestItem(itemStack, chance));
                    }
                }
            }
        }
        
        opChestItemList.clear();
        File opChestFile = new File(SkyWarsReloaded.get().getDataFolder(), "opchest.yml");

        if (!opChestFile.exists()) {
        	SkyWarsReloaded.get().saveResource("opchest.yml", false);
        }

        if (opChestFile.exists()) {
            FileConfiguration storage = YamlConfiguration.loadConfiguration(opChestFile);

            if (storage.contains("items")) {
                for (String item : storage.getStringList("items")) {
                    List<String> itemData = new LinkedList<String>(Arrays.asList(item.split(" ")));

                    int chance = Integer.parseInt(itemData.get(0));
                    itemData.remove(itemData.get(0));
                    
                    ItemStack itemStack = ItemUtils.parseItem(itemData);
                    
                    
                    if (itemStack != null) {
                        opChestItemList.add(new ChestItem(itemStack, chance));
                    }
                }
            }
        }
        
        basicChestItemList.clear();
        File basicChestFile = new File(SkyWarsReloaded.get().getDataFolder(), "basicchest.yml");

        if (!basicChestFile.exists()) {
        	SkyWarsReloaded.get().saveResource("basicchest.yml", false);
        }

        if (basicChestFile.exists()) {
            FileConfiguration storage = YamlConfiguration.loadConfiguration(basicChestFile);

            if (storage.contains("items")) {
                for (String item : storage.getStringList("items")) {
                    List<String> itemData = new LinkedList<String>(Arrays.asList(item.split(" ")));

                    int chance = Integer.parseInt(itemData.get(0));
                    itemData.remove(itemData.get(0));
                    
                    ItemStack itemStack = ItemUtils.parseItem(itemData);
                    
                    
                    if (itemStack != null) {
                        basicChestItemList.add(new ChestItem(itemStack, chance));
                    }
                }
            }
        }
        
    }

    public void populateChest(Chest chest, String chestfile) {
    	if (chestfile.equalsIgnoreCase("op")) {
    		Inventory inventory = chest.getBlockInventory();
    		inventory.clear();
            int added = 0;
            Collections.shuffle(randomLoc);

            for (ChestItem chestItem : opChestItemList) {
                if (random.nextInt(100) + 1 <= chestItem.getChance()) {
                    inventory.setItem(randomLoc.get(added), chestItem.getItem());
                    if (added++ >= inventory.getSize()-1) {
                        break;
                    }
                }
            }
    	} else if (chestfile.equalsIgnoreCase("basic")) {
    		Inventory inventory = chest.getBlockInventory();
    		inventory.clear();
            int added = 0;
            Collections.shuffle(randomLoc);

            for (ChestItem chestItem : basicChestItemList) {
                if (random.nextInt(100) + 1 <= chestItem.getChance()) {
                    inventory.setItem(randomLoc.get(added), chestItem.getItem());
                    if (added++ >= inventory.getSize()-1) {
                        break;
                    }
                }
            }
    	} else {
            Inventory inventory = chest.getBlockInventory();
    		inventory.clear();
            int added = 0;
            Collections.shuffle(randomLoc);

            for (ChestItem chestItem : chestItemList) {
                if (random.nextInt(100) + 1 <= chestItem.getChance()) {
                    inventory.setItem(randomLoc.get(added), chestItem.getItem());
                    if (added++ >= inventory.getSize()-1) {
                        break;
                    }
                }
            }
    	}
    }
    
    public void populateDoubleChest(DoubleChest chest, String chestfile) {
    	String type = chestfile;
    	if (SkyWarsReloaded.getCfg().doubleChestAlwaysOP()) {
    		type = "op";
    	}
    	if (type.equalsIgnoreCase("op")) {
    		Inventory inventory = chest.getInventory();
    		inventory.clear();
            int added = 0;
            Collections.shuffle(randomDLoc);

            for (ChestItem chestItem : opChestItemList) {
                if (random.nextInt(100) + 1 <= chestItem.getChance()) {
                    inventory.setItem(randomDLoc.get(added), chestItem.getItem());
                    if (added++ >= inventory.getSize()-1) {
                        break;
                    }
                }
                if (random.nextInt(100) + 1 <= chestItem.getChance()) {
                    inventory.setItem(randomDLoc.get(added), chestItem.getItem());
                    if (added++ >= inventory.getSize()-1) {
                        break;
                    }
                }
            }
    	} else if (type.equalsIgnoreCase("basic")) {
    		Inventory inventory = chest.getInventory();
    		inventory.clear();
            int added = 0;
            Collections.shuffle(randomDLoc);

            for (ChestItem chestItem : basicChestItemList) {
                if (random.nextInt(100) + 1 <= chestItem.getChance()) {
                    inventory.setItem(randomDLoc.get(added), chestItem.getItem());
                    if (added++ >= inventory.getSize()-1) {
                        break;
                    }
                }
            }
    	} else {
            Inventory inventory = chest.getInventory();
    		inventory.clear();
            int added = 0;
            Collections.shuffle(randomDLoc);

            for (ChestItem chestItem : chestItemList) {
                if (random.nextInt(100) + 1 <= chestItem.getChance()) {
                    inventory.setItem(randomDLoc.get(added), chestItem.getItem());
                    if (added++ >= inventory.getSize()-1) {
                        break;
                    }
                }
            }
    	}
    }

    private class ChestItem {

        private ItemStack item;
        private int chance;

        public ChestItem(ItemStack item, int chance) {
            this.item = item;
            this.chance = chance;
        }

        public ItemStack getItem() {
            return item;
        }

        public int getChance() {
            return chance;
        }
    }
    

}