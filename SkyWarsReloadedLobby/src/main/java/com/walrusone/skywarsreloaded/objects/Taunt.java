package com.walrusone.skywarsreloaded.objects;

import java.util.List;

import org.bukkit.Material;

public class Taunt implements Comparable<Taunt>{
	private String key;
	private String name;
	private Material icon;
	private List<String> lore;
	private String message;
	private String sound;
	private boolean useCustomSound;
	private float volume;
	private float pitch;
	private double speed;
	private int density;
	private int level;
	private List<String> stringParticles;
	
	public Taunt(String key, String name, List<String> lore, String message, String sound, boolean useCustomSound, double volume, double pitch, double speed, int density, List<String> particles, Material icon, int level) {
		this.key = key;
		this.name = name;
		this.lore = lore;
		this.message = message;
		this.useCustomSound = useCustomSound;
		this.sound = sound;
		this.volume = (float) volume;
		this.pitch = (float) pitch;
		this.density = density;
		this.speed = speed;
		this.icon = icon;
		this.stringParticles = particles;
		this.level = level;		
	}
	
	public String getName() {
		return this.name;
	}
	
	public List<String> getLore() {
		return this.lore;
	}
	
	public Material getIcon() {
		return this.icon;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public boolean useCustomSound() {
		return this.useCustomSound;
	}
	
	public float getVolume() {
		return this.volume;
	}
	
	public float getPitch() {
		return this.pitch;
	}
	
	public double getSpeed() {
		return speed;
	}
	
	public int getDensity() {
		return this.density;
	}
	
	public List<String> getStringParticles() {
		return this.stringParticles;
	}
	
	public int getLevel() {
		return this.level;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public String getSound() {
		return sound;
	}
		
	@Override
	public int compareTo(Taunt o) {
		return Integer.compare(this.level, o.level);
	}

}

