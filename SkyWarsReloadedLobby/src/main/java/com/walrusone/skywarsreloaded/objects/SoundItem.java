package com.walrusone.skywarsreloaded.objects;

import org.bukkit.Location;
import org.bukkit.Material;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;

public class SoundItem implements Comparable<SoundItem> {

    private int level;
    private String key;
    private String sound;
    private String name;
    private Material icon;
    private float volume;
    private float pitch;
    private boolean customSound;
    
    public SoundItem(String key, String sound, String displayname, int level, float volume, float pitch, Material material, boolean customSound) {
        this.level = level;
        this.key = key;
        this.sound = sound;
        this.name = displayname;
        this.customSound = customSound;
        this.icon = material;
        this.volume = volume;
        this.pitch = pitch;
    }

    public int getLevel() {
        return level;
    }
    
    public String getSound() {
        return sound;
    }
    
    public String getName() {
    	return name;
    }
    
    public Material getIcon() {
    	return icon;
    }
    
    public String getKey() {
    	return key;
    }
    
	@Override
	public int compareTo(SoundItem o) {
		return Integer.compare(this.level, o.level);
	}
	
	public void playSound(Location loc) {
		if (SkyWarsReloaded.get().getConfig().getBoolean("soundsEnabled", true)) {
			SkyWarsReloaded.getNMS().playGameSound(loc, sound, volume, pitch, customSound);
		}
	}
}
