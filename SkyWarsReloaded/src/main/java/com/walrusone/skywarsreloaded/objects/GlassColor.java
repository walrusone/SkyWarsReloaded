package com.walrusone.skywarsreloaded.objects;

import org.bukkit.inventory.ItemStack;

public class GlassColor implements Comparable<GlassColor> {

    private ItemStack item;
    private int level;
    private String color;
    private String name;
    
    public GlassColor(String color, String name, ItemStack item, int level) {
        this.item = item;
        this.level = level;
        this.color = color;
        this.name = name;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getLevel() {
        return level;
    }
    
    public String getColor() {
        return color;
    }
    
    public String getName() {
    	return name;
    }

	@Override
	public int compareTo(GlassColor o) {
		return Integer.compare(this.level, o.level);
	}
}
