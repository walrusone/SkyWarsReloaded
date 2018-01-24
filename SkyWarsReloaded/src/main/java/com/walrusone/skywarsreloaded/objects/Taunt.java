package com.walrusone.skywarsreloaded.objects;

import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.utilities.Messaging;
import com.walrusone.skywarsreloaded.utilities.Util;

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
	
	/**Does the taunt for a player
	 */
	public void performTaunt(Player player) {
		if (!this.getKey().equalsIgnoreCase("none")) {
			SkyWarsReloaded.getNMS().playGameSound(player.getLocation(), sound, volume, pitch, useCustomSound);
			
			doTauntParticles(player.getUniqueId().toString());
		
			if (this.getMessage() != null && this.getMessage().length() != 0) {
				String prefix = new Messaging.MessageFormatter().setVariable("player", player.getDisplayName()).format("taunt.prefix");
				List<Player> players = player.getWorld().getPlayers();
				for (Player p: players) {
					if(p.getLocation().distance(player.getLocation()) < this.getVolume()*15) {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + " " + this.getMessage()));
					}
				}
			}
		}	
	}
	
	/**Creates a sphere of particles around the player
	 */
    public void doTauntParticles(String uuid) {
    	Player player = SkyWarsReloaded.get().getServer().getPlayer(UUID.fromString(uuid));
    	if (player != null) {
    		Util.get().surroundParticles(player, 2, stringParticles, density, speed);
    	}
    }

	@Override
	public int compareTo(Taunt o) {
		return Integer.compare(this.level, o.level);
	}

}

