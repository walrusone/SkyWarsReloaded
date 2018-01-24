package com.walrusone.skywarsreloaded.objects;

import org.bukkit.inventory.ItemStack;

public class ChestItem implements Comparable<ChestItem> {

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
    
	@Override
	public int compareTo(ChestItem o) {
		return Integer.compare(this.chance, o.chance);
	}
}
