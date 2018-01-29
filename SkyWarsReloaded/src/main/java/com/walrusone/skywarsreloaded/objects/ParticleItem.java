package com.walrusone.skywarsreloaded.objects;

import org.bukkit.Material;

public class ParticleItem implements Comparable<ParticleItem> {

    private int level;
    private String effect;
    private String name;
    private Material material;
    
    public ParticleItem(String effect, String name, Material material, int cost) {
        this.level = cost;
        this.effect = effect;
        this.name = name;
        this.material = material;
    }

    public int getLevel() {
        return level;
    }
    
    public String getEffect() {
        return effect;
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
}
