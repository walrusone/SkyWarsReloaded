package com.walrusone.skywarsreloaded.managers;

import com.google.common.collect.Lists;
import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.enums.ChestType;
import com.walrusone.skywarsreloaded.enums.Vote;
import com.walrusone.skywarsreloaded.menus.gameoptions.objects.ChestItem;
import com.walrusone.skywarsreloaded.utilities.Util;

import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ChestManager {

    private final List<ChestItem> chestItemList = Lists.newArrayList();
    private final List<ChestItem> opChestItemList = Lists.newArrayList();
    private final List<ChestItem> basicChestItemList = Lists.newArrayList();
    private final Random random = new Random();
   
    private List<Integer> randomLoc = new ArrayList<Integer>();
    private List<Integer> randomDLoc = new ArrayList<Integer>();


    public ChestManager() {
        load(chestItemList, "chest.yml");
        load(opChestItemList, "opchest.yml");
        load(basicChestItemList, "basicchest.yml");
        for (int i = 0; i < 27; i++) {
        	randomLoc.add(i);
        }
        for (int i = 0; i < 54; i++) {
        	randomDLoc.add(i);
        }
    }
    
    public void addItems(List<ItemStack> items, ChestType ct, int percent) {
    	List<ChestItem> toAddTo = null;
    	if (ct == ChestType.BASIC) {
    		toAddTo = basicChestItemList;
    	} else if (ct == ChestType.OP) {
    		toAddTo = opChestItemList;
    	} else if (ct == ChestType.NORMAL) {
    		toAddTo = chestItemList;
    	}
    	for (ItemStack item: items) {
    		toAddTo.add(new ChestItem(item, percent));
    	}
    	Collections.shuffle(toAddTo);
    	save(toAddTo, ct);
    }

	@SuppressWarnings("unchecked")
	public void load(List<ChestItem> itemList, String fileName) {
        itemList.clear();
        File chestFile = new File(SkyWarsReloaded.get().getDataFolder(), fileName);

        if (!chestFile.exists()) {
        	SkyWarsReloaded.get().saveResource(fileName, false);
        }

        if (chestFile.exists()) {
            FileConfiguration storage = YamlConfiguration.loadConfiguration(chestFile);

            if (storage.getConfigurationSection("chestItems") != null) {
            	for (String key: storage.getConfigurationSection("chestItems").getKeys(false)) {
            		if (Util.get().isInteger(key)) {
                		int percent = Integer.valueOf(key);
                    	List<ItemStack> items = (List<ItemStack>) storage.getList("chestItems." + key + ".items");
                    	for (ItemStack iStack: items) {
                    		itemList.add(new ChestItem(iStack, percent));
                    	}
            		}
                }
            }
            Collections.shuffle(itemList);
        }
    }
	
    private void save(List<ChestItem> chestList, ChestType ct) {
		String fileName = "";
    	if (ct == ChestType.BASIC) {
    		fileName = "basicchest.yml";
    	} else if (ct == ChestType.OP) {
    		fileName =  "opchest.yml";
    	} else if (ct == ChestType.NORMAL) {
    		fileName =  "chest.yml";
    	}
    	
    	List<ChestItem> toSave = new ArrayList<ChestItem>();
    	toSave.addAll(chestList);
    	
    	File chestFile = new File(SkyWarsReloaded.get().getDataFolder(), fileName);

        if (!chestFile.exists()) {
        	SkyWarsReloaded.get().saveResource(fileName, false);
        }

        if (chestFile.exists()) {
        	try {
        		FileConfiguration storage = YamlConfiguration.loadConfiguration(chestFile);
                Collections.<ChestItem>sort(toSave);
                int percent = 0;
                List<ItemStack> items = new ArrayList<ItemStack>();
                for (int j = 0; j <= toSave.size(); j++) {
                	ChestItem cItem = null;
                	if (j < toSave.size()) {
                		cItem = toSave.get(j);
                	}
                	if (percent == 0 && cItem != null) {
                		percent = cItem.getChance();
                		items.add(cItem.getItem());
                	} else {
                		if (cItem != null && percent == cItem.getChance()) {
                			items.add(cItem.getItem());
                		} else {
                			ItemStack[] itemsToAdd = new ItemStack[items.size()];
                			for (int i = 0; i < items.size(); i++) {
                				itemsToAdd[i] = items.get(i);
                			}
                			storage.set("chestItems." + percent + ".items", itemsToAdd);
                			items.clear();
                			if (cItem != null) {
                    			percent = cItem.getChance();
                    			items.add(cItem.getItem());
                			}
                		}
                	}
                }
                storage.save(chestFile);
        	} catch (IOException ioException) {
	            System.out.println("Failed to save chestfile " + fileName + ": " + ioException.getMessage());
	        }
            
        }	
	}
	
    public void populateChest(Object chest, Vote cVote) {
    	if (cVote == Vote.CHESTOP) {
    		fillChest(chest, opChestItemList);
    	} else if (cVote == Vote.CHESTBASIC) {
    		fillChest(chest, basicChestItemList);
    	} else if (cVote == Vote.CHESTNORMAL) {
    		fillChest(chest, chestItemList);
    	} else {
    		Inventory inventory = null;
    		if (chest instanceof DoubleChest) {
    			inventory = ((DoubleChest) chest).getInventory();
    		} else if (chest instanceof Chest) {
    			inventory = ((Chest)chest).getInventory();
    		}
    		if (inventory != null) {
        		inventory.clear();
    		}
    	}
    }
    
    private void fillChest(Object chest, List<ChestItem> fill) {
    	Inventory inventory = null;
    	if (chest instanceof Chest) {
    		inventory = ((Chest) chest).getInventory();
    	} else if (chest instanceof DoubleChest) {
    		inventory = ((DoubleChest) chest).getInventory();
    	}
    	if (inventory != null) {
    		inventory.clear();
            int added = 0;
            Collections.shuffle(randomLoc);
            Collections.shuffle(randomDLoc);

            for (ChestItem chestItem : fill) {
            	if (chest instanceof Chest) {
                    if (random.nextInt(100) + 1 <= chestItem.getChance()) {
                        inventory.setItem(randomLoc.get(added), chestItem.getItem());
                        if (added++ >= inventory.getSize() - 1) {
                            break;
                        }
                    }
            	}
                if (chest instanceof DoubleChest) {
                    if (random.nextInt(100) + 1 <= chestItem.getChance()) {
                        inventory.setItem(randomDLoc.get(added), chestItem.getItem());
                        if (added++ >= inventory.getSize() - 1) {
                            break;
                        }
                    }
                }
            }	
    	}
    }
}