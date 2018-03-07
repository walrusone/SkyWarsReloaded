package com.walrusone.skywarsreloaded.menus.playeroptions.objects;

import org.bukkit.inventory.ItemStack;

public class GlassColor implements Comparable<GlassColor> {

    private ItemStack item;
    private int level;
    private int cost;
    private String color;
    private String name;
    
    public GlassColor(String color, String name, ItemStack item, int level, int cost) {
        this.item = item;
        this.level = level;
        this.cost = cost;
        this.color = color;
        this.name = name;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getLevel() {
        return level;
    }
    
    public String getKey() {
        return color;
    }
    
    public String getName() {
    	return name;
    }
    
    public int getCost() {
    	return cost;
    }

	@Override
	public int compareTo(GlassColor o) {
		return Integer.compare(this.level, o.level);
	}
}
