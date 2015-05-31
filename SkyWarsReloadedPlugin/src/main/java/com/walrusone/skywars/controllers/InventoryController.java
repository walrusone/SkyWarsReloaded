package com.walrusone.skywars.controllers;

import com.walrusone.skywars.SkyWarsReloaded;

import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryController {

    private Map<String, pInventory> inventories = new HashMap<String, pInventory>();
    
	public InventoryController() {
        load();
    }

    @SuppressWarnings("unchecked")
	public void load() {
        inventories = new HashMap<String, pInventory>();
        try {
        	 File dataDirectory = SkyWarsReloaded.get().getDataFolder();
             File playerDataDirectory = new File(dataDirectory, "player_data");

             if (!playerDataDirectory.exists() && !playerDataDirectory.mkdirs()) {
                 System.out.println("Could not get Inventories File");
                 return;
             }

             File playerInventories = new File(playerDataDirectory, "inventories.yml");

             if (playerInventories.exists()) {
                 FileConfiguration storage = YamlConfiguration.loadConfiguration(playerInventories);

                 if (storage.isSet("inventories")) {
         			for (String uuid : storage.getConfigurationSection("inventories.").getKeys(false)) {
						ItemStack[] content = ((List<ItemStack>) storage.get("inventories." + uuid + ".contents")).toArray(new ItemStack[0]);
						ItemStack[] armor = ((List<ItemStack>) storage.get("inventories." + uuid + ".armor")).toArray(new ItemStack[0]);
						int level = storage.getInt("inventories." + uuid + ".level");
						float exp = (float) storage.getDouble("inventories." + uuid + ".exp");
						String gameMode = storage.getString("inventories." + uuid + ".gameMode");
						GameMode gamemode = GameMode.valueOf(gameMode);
						inventories.put(uuid, new pInventory(content, armor, level, exp, gamemode));
         			}
         		}
                 playerInventories.delete();
             } else {
            	 
            	 File playerInventoriesNew = new File(dataDirectory, "inventories.yml");
            	 
            	 if (!playerInventoriesNew.exists() && !playerInventoriesNew.createNewFile()) {
                  	System.out.println("Could not get Inventories File");
                      return;
                  }

                  if (playerInventoriesNew.exists()) {
                      FileConfiguration storage = YamlConfiguration.loadConfiguration(playerInventoriesNew);

                      if (storage.isSet("inventories")) {
              			for (String uuid : storage.getConfigurationSection("inventories.").getKeys(false)) {
     						ItemStack[] content = ((List<ItemStack>) storage.get("inventories." + uuid + ".contents")).toArray(new ItemStack[0]);
     						ItemStack[] armor = ((List<ItemStack>) storage.get("inventories." + uuid + ".armor")).toArray(new ItemStack[0]);
     						int level = storage.getInt("inventories." + uuid + ".level");
     						float exp = (float) storage.getDouble("inventories." + uuid + ".exp");
     						String gameMode = storage.getString("inventories." + uuid + ".gameMode");
     						GameMode gamemode = GameMode.valueOf(gameMode);
     						inventories.put(uuid, new pInventory(content, armor, level, exp, gamemode));
              			}
              		}
                  }
             }
        } catch (IOException ioException) {
            System.out.println("Failed to load inventories file: " + ioException.getMessage());
        }
       
    }
    
    public void save() {
        try {
        	 File dataDirectory = SkyWarsReloaded.get().getDataFolder();

             File playerInventories = new File(dataDirectory, "inventories.yml");

             if (!playerInventories.exists() && !playerInventories.createNewFile()) {
             	System.out.println("Could not get Inventories File");
                 return;
             }

             if (playerInventories.exists()) {
                 FileConfiguration storage = YamlConfiguration.loadConfiguration(playerInventories);
                 
                 storage.set("inventories", null);
                
                 for (String uuid: inventories.keySet()) {
                	 storage.set("inventories." + uuid + ".contents", inventories.get(uuid).getContent());
                	 storage.set("inventories." + uuid + ".armor", inventories.get(uuid).getArmor());
                	 storage.set("inventories." + uuid + ".level", inventories.get(uuid).getLevel());
                	 storage.set("inventories." + uuid + ".exp", inventories.get(uuid).getExp());
                	 storage.set("inventories." + uuid + ".gameMode", inventories.get(uuid).getGameMode().toString());
                 }
                 storage.save(playerInventories);
             }
        } catch (IOException ioException) {
            System.out.println("Failed to load inventories file: " + ioException.getMessage());
        }
       
    }
    
    public void add(Player p) {
    	inventories.put(p.getUniqueId().toString(), new pInventory(p.getInventory().getContents(), p.getInventory().getArmorContents(), p.getLevel(), p.getExp(), p.getGameMode()));
    }
    
    public boolean playerExists(String uuid) {
    	if (inventories.containsKey(uuid)) {
    		return true;
    	}
    	return false;
    }
    
    public void restoreInventory(Player player) {
    	if (player != null) {
        	if (playerExists(player.getUniqueId().toString())) {
            	player.getInventory().clear();
    			player.getInventory().setHelmet(null);
    		    player.getInventory().setChestplate(null);
    		    player.getInventory().setLeggings(null);
    		    player.getInventory().setBoots(null);
				for (PotionEffect effect : player.getActivePotionEffects()) {
			        player.removePotionEffect(effect.getType());
				}
            	player.getInventory().setContents(inventories.get(player.getUniqueId().toString()).getContent());
            	player.getInventory().setArmorContents(inventories.get(player.getUniqueId().toString()).getArmor());
            	player.setLevel(inventories.get(player.getUniqueId().toString()).getLevel());
            	player.setExp(inventories.get(player.getUniqueId().toString()).getExp());
            	player.setGameMode(inventories.get(player.getUniqueId().toString()).getGameMode());
            	inventories.remove(player.getUniqueId().toString());
        	}
    	}
    }

    private class pInventory {
    	
    	private ItemStack[] content;
    	private ItemStack[] armor;
    	private float exp;
    	private GameMode gameMode;
    	private int level;
    	
    	public pInventory(ItemStack[] content, ItemStack[] armor, int l, float f, GameMode gameMode) {
    		this.content = content;
    		this.armor = armor;
    		level = l;
    		exp = f;
    		this.gameMode = gameMode;
    	}
    	
    	public ItemStack[] getContent() {
    		return content;
    	}
    	
    	public ItemStack[] getArmor() {
    		return armor;
    	}
    	
    	public int getLevel() {
    		return level;
    	}
    	
    	public float getExp() {
    		return exp;
    	}
    	
    	public GameMode getGameMode() {
    		return gameMode;
    	}
    }
}