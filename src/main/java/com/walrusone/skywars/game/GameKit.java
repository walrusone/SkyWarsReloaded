package com.walrusone.skywars.game;

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

import net.milkbowl.vault.item.Items;

public class GameKit {

    private String name;
    private int cost;
    private List<ItemStack> items = Lists.newArrayList();
    private List<PotionEffect> potionEffects = Lists.newArrayList();

    private ItemStack icon;
    private int position;
    private List<String> lores;

    public GameKit(String name, FileConfiguration storage) {
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

            lores.add(ChatColor.DARK_BLUE + "Contents");
            for (ItemStack itemStack : items) {
            	String enchanted = "";
            	if (ItemUtils.isEnchanted(itemStack)) {
            		enchanted = "Enchanted ";
            	}
    			lores.add(ChatColor.YELLOW + enchanted + ChatColor.WHITE + "" + Items.itemByStack(itemStack).getName());
            }
            lores.add(ChatColor.DARK_BLUE + " ");
            lores.add(ChatColor.DARK_BLUE + "Potion Effects");
            for (PotionEffect potionEffect: potionEffects) {
                lores.add(ChatColor.WHITE + "" + potionEffect.getType().getName() + ", " + potionEffect.getDuration() + ", " + potionEffect.getAmplifier());
            }
        } catch (NullPointerException e) {
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

    public int getCost() {
        return cost;
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

