package com.walrusone.skywars.controllers;

import com.google.common.collect.Lists;
import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.utilities.ItemUtils;

import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ChestController {

    private final List<ChestItem> chestItemList = Lists.newArrayList();
    private final Random random = new Random();

    public ChestController() {
        load();
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
    }

    public void populateChest(Chest chest) {
        Inventory inventory = chest.getBlockInventory();
        int added = 0;

        for (ChestItem chestItem : chestItemList) {
            if (random.nextInt(100) + 1 <= chestItem.getChance()) {
                inventory.addItem(chestItem.getItem());

                if (added++ > inventory.getSize()) {
                    break;
                }
            }
        }
    }

    public class ChestItem {

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