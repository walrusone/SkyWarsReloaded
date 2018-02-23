package com.walrusone.skywarsreloaded.menus.playeroptions.objects;

import java.util.List;

import org.bukkit.Material;

public class ParticleItem implements Comparable<ParticleItem> {

    private int level;
    private int cost;
    private List<ParticleEffect> effects;
    private String key;
    private String name;
    private Material material;
    
    public ParticleItem(String key, List<ParticleEffect> effects, String name, Material material, int level, int cost) {
        this.key = key;
    	this.level = level;
    	this.cost = cost;
        this.effects = effects;
        this.name = name;
        this.material = material;
    }

    public int getLevel() {
        return level;
    }
    
    public int getCost() {
        return cost;
    }
    
    public List<ParticleEffect> getEffects() {
        return effects;
    }
    
    public String getName() {
    	return name;
    }
    
    public Material getMaterial() {
    	return material;
    }
    
	@Override
	public int compareTo(ParticleItem o) {
		return Integer.compare(this.level, o.level);
	}

	public String getKey() {
		return key;
	}
}
