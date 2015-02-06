package com.walrusone.skywars.game;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;
import com.walrusone.skywars.utilities.ItemUtils;

import net.milkbowl.vault.item.Items;

public class GameKit {

    private String name;
    private int cost;
    private List<ItemStack> items = Lists.newArrayList();

    private ItemStack icon;
    private int position;
    private List<String> lores;

    public GameKit(String name, FileConfiguration storage) {
        this.name = name;

        List<String> itemDatas = storage.getStringList("items");
        for (String itemData : itemDatas) {
        	
        	List<String> item = Arrays.asList(itemData.split(" "));
            ItemStack itemStack = ItemUtils.parseItem(item);

            if (itemStack != null) {
                items.add(itemStack);
            }
        }

        cost = storage.getInt("cost", 0);
        position = storage.getInt("position", 0);

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
    }
    
    public Collection<ItemStack> getItems() {
        return items;
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

