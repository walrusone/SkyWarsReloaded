package com.walrusone.skywars.game;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.google.common.collect.Lists;
import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.utilities.ItemUtils;
import com.walrusone.skywars.utilities.Messaging;

public class GameKit {

    private String name;
    private String kitName;
    private int permCost;
    private int cost;
    private List<ItemStack> items = Lists.newArrayList();
    private List<PotionEffect> potionEffects = Lists.newArrayList();

    private ItemStack icon;
    private int position;
    private List<String> lores;

    public GameKit(String name, FileConfiguration storage, File kit) {
        try {
        	this.name = name;

            List<String> itemDatas = storage.getStringList("items");
            for (String itemData : itemDatas) {
            	
            	List<String> item = Arrays.asList(itemData.split(" "));
                ItemStack itemStack = ItemUtils.parseItem(item);

                if (itemStack != null) {
                    items.add(itemStack);
                }
            }
            
            List<String> effects = storage.getStringList("potionEffects");
            for (String effect : effects) {
            	
            	List<String> effectDetails = Arrays.asList(effect.split(" "));
                PotionEffect potionEffect = ItemUtils.parseEffect(effectDetails);

                if (potionEffect != null) {
                    potionEffects.add(potionEffect);
                }
            }

           if (storage.getString("kitName") != null) {
           	 	kitName = storage.getString("kitName");
           } else {
           		kitName = name;
           		storage.set("kitName", name);
           	try {
					storage.save(kit);
				} catch (IOException e) {
				}
           }
           
           if (storage.getString("permCost") != null) {
          	 	permCost = storage.getInt("permCost");
          } else {
          		permCost = 1000000;
          		storage.set("permCost", permCost);
          	try {
					storage.save(kit);
				} catch (IOException e) {
				}
          }
            
            cost = storage.getInt("cost", 0);
            position = storage.getInt("menuPostion");

            String icon = storage.getString("icon").toUpperCase();
            Material material;
            material = Material.getMaterial(icon);

            if (material == null) {
                material = Material.STONE;
            }

            this.icon = new ItemStack(material, 1);

            lores = Lists.newLinkedList();
            if (storage.contains("details")) {
                for (String string : storage.getStringList("details")) {
                    lores.add(ChatColor.translateAlternateColorCodes('&', string));
                }
            }
            
            if (SkyWarsReloaded.getCfg().showKitItemsandPotionEffects()) {
                String contents = new Messaging.MessageFormatter().format("kits.contents");
                lores.add(contents);
                for (ItemStack itemStack : items) {
                	String enchanted = "";
                	if (ItemUtils.isEnchanted(itemStack)) {
                		enchanted = "Enchanted ";
                	}
        			lores.add(ChatColor.YELLOW + enchanted + ChatColor.WHITE + "" + SkyWarsReloaded.getNMS().getName(itemStack));
                }
                lores.add(ChatColor.DARK_BLUE + " ");
                String potions = new Messaging.MessageFormatter().format("kits.potion-effects");
                if (potionEffects.size() > 0) {
                    lores.add(potions);
                    for (PotionEffect potionEffect: potionEffects) {
                        lores.add(ChatColor.WHITE + "" + potionEffect.getType().getName() + ", " + potionEffect.getDuration() + ", " + potionEffect.getAmplifier());
                    }
                }
            }
        } catch (NullPointerException e) {
        	e.printStackTrace();
        	SkyWarsReloaded.get().getLogger().info("There is an error in the kit: " + ChatColor.RED + name); 
        }
    	
    }
    
    public Collection<ItemStack> getItems() {
        return items;
    }
    
    public Collection<PotionEffect> getPotionEffects() {
        return potionEffects;
    }

    public String getName() {
        return name;
    }
    
    public String getKitName() {
    	return kitName;
    }

    public int getCost() {
        return cost;
    }
    
    public int getPermCost() {
    	return permCost;
    }

    public int getPosition() {
        return position;
    }
    
    public ItemStack getIcon() {
        return icon;
    }

    public List<String> getLores() {
        return lores;
    }
}

