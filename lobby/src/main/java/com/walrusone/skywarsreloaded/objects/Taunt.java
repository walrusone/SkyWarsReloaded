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
	private int cost;
	private List<String> stringParticles;
	
	public Taunt(String key, String name, List<String> lore, String message, String sound, boolean useCustomSound, double volume, double pitch, double speed, int density, List<String> particles, Material icon, int level, int cost) {
		this.key = key;
		this.name = name;
		this.lore = lore;
		this.message = message;
		this.useCustomSound = useCustomSound;
		this.setSound(sound);
		this.volume = (float) volume;
		this.pitch = (float) pitch;
		this.density = density;
		this.speed = speed;
		this.icon = icon;
		this.stringParticles = particles;
		this.level = level;	
		this.cost = cost;
	}
	
	public String getName() {
		return this.name;
	}
	
	public List<String> getLore() {
		return this.lore;
	}
	
	public Material getMaterial() {
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
	
	public int getCost() {
		return this.cost;
	}
	
	public String getKey() {
		return this.key;
	}
	
	@Override
	public int compareTo(Taunt o) {
		return Integer.compare(this.level, o.level);
	}

	public String getSound() {
		return sound;
	}

	public void setSound(String sound) {
		this.sound = sound;
	}

}

