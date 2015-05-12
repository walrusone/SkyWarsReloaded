package com.walrusone.skywars.utilities;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class GlassColor {

    private ItemStack item;
    private int cost;
    private String color;
    private String name;
    private byte data;
    private Material material;
    
    public GlassColor(String color, String name, ItemStack item, int cost) {
        this.item = item;
        this.cost = cost;
        this.color = color;
        this.name = name;
        material = Material.STAINED_GLASS;
        data = 0;
        
        switch (color) {
        case "white": data = (byte) 0;
        break;
        case "orange": data = (byte) 1;
	  	break;
        case "magenta": data = (byte) 2;
	  	break;
        case "lightblue": data = (byte) 3;
	  	break;
        case "yellow": data = (byte) 4;
	  	break;
        case "lime": data = (byte) 5;
	  	break;
        case "pink": data = (byte) 6;
	  	break;
        case "gray": data = (byte) 7;
	  	break;
        case "lightgray": data = (byte) 8;
	  	break;
        case "cyan": data = (byte) 9;
	  	break;
        case "purple": data = (byte) 10;
	  	break;
        case "blue": data = (byte) 11;
	  	break;
        case "brown": data = (byte) 12;
	  	break;
        case "green": data = (byte) 13;
	  	break;
        case "red": data = (byte) 14;
	  	break;
        case "black": data = (byte) 15;
	  	break;
		}
    }

    public ItemStack getItem() {
        return item;
    }

    public int getCost() {
        return cost;
    }
    
    public String getColor() {
        return color;
    }
    
    public String getName() {
    	return name;
    }
    
    public Material getMaterial() {
    	return material;
    }
    public byte getData() {
    	return data;
    }

}
