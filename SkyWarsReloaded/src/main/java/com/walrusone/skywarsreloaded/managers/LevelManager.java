package com.walrusone.skywarsreloaded.managers;

import com.google.common.collect.Maps;
import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.objects.GlassColor;
import com.walrusone.skywarsreloaded.objects.ParticleItem;
import com.walrusone.skywarsreloaded.objects.SoundItem;
import com.walrusone.skywarsreloaded.objects.Taunt;
import com.walrusone.skywarsreloaded.utilities.Util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class LevelManager {

    private final ArrayList<GlassColor> colorList = new ArrayList<GlassColor>();
    private final ArrayList<Taunt> tauntList = new ArrayList<Taunt>();
	private final Map<Projectile, String> projectileMap = Maps.newConcurrentMap();
    private final ArrayList<ParticleItem> particleList = new ArrayList<ParticleItem>();
	private final ArrayList<ParticleItem> projEffectList = new ArrayList<ParticleItem>();
	private final ArrayList<SoundItem> killSoundList = new ArrayList<SoundItem>();
	private final ArrayList<SoundItem> winSoundList = new ArrayList<SoundItem>();
    private final List<String> effects = Arrays.asList("water", "flame", "smoke", "critical", "slime", "snow", "magic", 
    		"music", "happy", "angry", "potion", "poison", "alphabet", "lava", "lava_drip", "heart", "redstone", "sparks", "portal", "clouds", "none");
    
    public LevelManager() {
    	if (SkyWarsReloaded.getCfg().particlesEnabled()) {
            SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncRepeatingTask(SkyWarsReloaded.get(), new Runnable() {
    			public void run() {
    				for (Projectile projectile: projectileMap.keySet()) {
    					if (projectile.isDead()) {
    						projectileMap.remove(projectile);
    					} else {
    						String effect = projectileMap.get(projectile);
    						doEffect(projectile, effect);
    					}
    				}
    			}
    		}, 2, 2); 
    	}
        loadGlassColors();
        loadParticleEffects();
        loadProjEffects();
        loadKillSounds();
        loadWinSounds();
        loadTaunts();
    }
    
	/*Glass color methods*/
    
    public void loadGlassColors() {
        colorList.clear();
        File glassFile = new File(SkyWarsReloaded.get().getDataFolder(), "glasscolors.yml");

        if (!glassFile.exists()) {
        	SkyWarsReloaded.get().saveResource("glasscolors.yml", false);
        }

        if (glassFile.exists()) {
            FileConfiguration storage = YamlConfiguration.loadConfiguration(glassFile);

            if (storage.getConfigurationSection("colors") != null) {
            	for (String key: storage.getConfigurationSection("colors").getKeys(false)) {
            		String color = key;
                	String name = storage.getString("colors." + key + ".displayname");
                	String material = storage.getString("colors." + key + ".material");
                	int level = storage.getInt("colors." + key + ".level");
                	int data = storage.getInt("colors." + key + ".datavalue");
                	                	
                	Material mat = Material.matchMaterial(material);
                	if (mat != null) {
                		ItemStack itemStack = null;
                		if (data == -1) {
                			itemStack = new ItemStack(mat, 1);
                		} else {
                			itemStack = new ItemStack(mat, 1, (short) data);
                		}
                		
                        if (itemStack != null) {
                            colorList.add(new GlassColor(color, name, itemStack, level));
                        }
                	}
            	}
            }
        }
        
        Collections.<GlassColor>sort(colorList);
    }
       
    public GlassColor getGlassByName(String color) {
    	for (GlassColor glassColor: colorList) {
    		if (glassColor.getName().equalsIgnoreCase(color)) {
    			return glassColor;
    		}
    	}
        return null;
    }
    
    public GlassColor getGlassByColor(String color) {
    	for (GlassColor glassColor: colorList) {
    		if (glassColor.getColor().equalsIgnoreCase(color)) {
    			return glassColor;
    		}
    	}
        return null;
    }
    
    public ArrayList<GlassColor> getColorItems() {
    	return colorList;
    }
    
    /*Particle Effect Methods*/
    
    public void loadParticleEffects() {
        particleList.clear();
        File particleFile = new File(SkyWarsReloaded.get().getDataFolder(), "particleeffects.yml");

        if (!particleFile.exists()) {
        	SkyWarsReloaded.get().saveResource("particleeffects.yml", false);
        }

        if (particleFile.exists()) {
            FileConfiguration storage = YamlConfiguration.loadConfiguration(particleFile);

            if (storage.getConfigurationSection("effects") != null) {
            	for (String key: storage.getConfigurationSection("effects").getKeys(false)) {
            		String effect = key;
                	String name = storage.getString("effects." + key + ".displayname");
                	String material = storage.getString("effects." + key + ".icon");
                	int level = storage.getInt("effects." + key + ".level");
                	
                	Material mat = Material.matchMaterial(material);
                	if (mat != null) {
                		if (effects.contains(effect)) {
                            particleList.add(new ParticleItem(effect, name, mat, level));
                		}
                    }
            	}
            }
        }
        
        Collections.<ParticleItem>sort(particleList);
    }
    
 
    public ParticleItem getParticleByName(String name) {
    	for (ParticleItem pi: particleList) {
    		if (pi.getName().equalsIgnoreCase(name)) {
    			return pi;
    		}
    	}
    	return null;
    }
    
    public ArrayList<ParticleItem> getParticleItems() {
    	return particleList;
    }
    
    public ParticleItem getParticleByEffect(String effect) {
    	for (ParticleItem pItem: particleList) {
    		if (pItem.getEffect().equalsIgnoreCase(effect)) {
    			return pItem;
    		}
    	}
        return null;
    }
    
    
    /*Particle Effect Methods*/
    
    public void loadProjEffects() {
        projEffectList.clear();
        File particleFile = new File(SkyWarsReloaded.get().getDataFolder(), "projectileeffects.yml");

        if (!particleFile.exists()) {
        	SkyWarsReloaded.get().saveResource("projectileeffects.yml", false);
        }

        if (particleFile.exists()) {
            FileConfiguration storage = YamlConfiguration.loadConfiguration(particleFile);

            if (storage.getConfigurationSection("effects") != null) {
            	for (String key: storage.getConfigurationSection("effects").getKeys(false)) {
            		String effect = key;
                	String name = storage.getString("effects." + key + ".displayname");
                	String material = storage.getString("effects." + key + ".icon");
                	int level = storage.getInt("effects." + key + ".level");
                	
                	Material mat = Material.matchMaterial(material);
                	if (mat != null) {
                		if (effects.contains(effect)) {
                    		projEffectList.add(new ParticleItem(effect, name, mat, level));
                		}
                    }
            	}
            }
        }
        
        Collections.<ParticleItem>sort(projEffectList);
    }
    
    public ParticleItem getProjByName(String name) {
    	for (ParticleItem pi: projEffectList) {
    		if (pi.getName().equalsIgnoreCase(name)) {
    			return pi;
    		}
    	}
        return null;
    }
    
    public ArrayList<ParticleItem> getProjParticleItems() {
    	return projEffectList;
    }
    
    public ParticleItem getProjByEffect(String effect) {
    	for (ParticleItem pItem: projEffectList) {
    		if (pItem.getEffect().equalsIgnoreCase(effect)) {
    			return pItem;
    		}
    	}
        return null;
    }  
    
       
    public void loadKillSounds() {
        killSoundList.clear();
        File soundFile = new File(SkyWarsReloaded.get().getDataFolder(), "killsounds.yml");

        if (!soundFile.exists()) {
        	SkyWarsReloaded.get().saveResource("killsounds.yml", false);
        }

        if (soundFile.exists()) {
            FileConfiguration storage = YamlConfiguration.loadConfiguration(soundFile);

            if (storage.getConfigurationSection("sounds") != null) {
            	for (String key: storage.getConfigurationSection("sounds").getKeys(false)) {
            		String sound = storage.getString("sounds." + key + ".sound");
                	String name = storage.getString("sounds." + key + ".displayName");
                	int volume = storage.getInt("sounds." + key + ".volume");
                	int pitch = storage.getInt("sounds." + key + ".pitch");
                	String material = storage.getString("sounds." + key + ".icon");
                	int level = storage.getInt("sounds." + key + ".level");
                	boolean isCustom = storage.getBoolean("sounds." + key + ".isCustomSound");
                	
                	Material mat = Material.matchMaterial(material);
                	if (mat != null) {
                		if (!isCustom) {
                			try {
                				Sound s = Sound.valueOf(sound);
                				if (s != null) {
                					killSoundList.add(new SoundItem(key, sound, name, level, volume, pitch, mat, isCustom));
                				}
                			} catch (IllegalArgumentException e) {
                				SkyWarsReloaded.get().getServer().getLogger().info(sound + " is not a valid sound in killsounds.yml");
                			}
                		} else {
                			killSoundList.add(new SoundItem(key, sound, name, level, volume, pitch, mat, isCustom));
                		}
                			
                    } else {
                    	SkyWarsReloaded.get().getServer().getLogger().info(mat + " is not a valid Material in killsounds.yml");
                    }
            	}
            }
        }
        
        Collections.<SoundItem>sort(killSoundList);
    }
    
    public SoundItem getKillSoundByName(String name) {
    	for (SoundItem pi: killSoundList) {
    		if (pi.getName().equalsIgnoreCase(name)) {
    			return pi;
    		}
    	}
        return null;
    }
    
    public ArrayList<SoundItem> getKillSoundItems() {
    	return killSoundList;
    }
    
    public SoundItem getKillSoundByKey(String key) {
    	for (SoundItem pItem: killSoundList) {
    		if (pItem.getKey().equalsIgnoreCase(key)) {
    			return pItem;
    		}
    	}
        return null;
    }
    
    public void loadWinSounds() {
        winSoundList.clear();
        File soundFile = new File(SkyWarsReloaded.get().getDataFolder(), "winsounds.yml");

        if (!soundFile.exists()) {
        	SkyWarsReloaded.get().saveResource("winsounds.yml", false);
        }

        if (soundFile.exists()) {
            FileConfiguration storage = YamlConfiguration.loadConfiguration(soundFile);

            if (storage.getConfigurationSection("sounds") != null) {
            	for (String key: storage.getConfigurationSection("sounds").getKeys(false)) {
            		String sound = storage.getString("sounds." + key + ".sound");
                	String name = storage.getString("sounds." + key + ".displayName");
                	int volume = storage.getInt("sounds." + key + ".volume");
                	int pitch = storage.getInt("sounds." + key + ".pitch");
                	String material = storage.getString("sounds." + key + ".icon");
                	int level = storage.getInt("sounds." + key + ".level");
                	boolean isCustom = storage.getBoolean("sounds." + key + ".isCustomSound");
                	
                	Material mat = Material.matchMaterial(material);
                	if (mat != null) {
                		if (!isCustom) {
                			try {
                				Sound s = Sound.valueOf(sound);
                				if (s != null) {
                    				winSoundList.add(new SoundItem(key, sound, name, level, volume, pitch, mat, isCustom));
                				}
                			} catch (IllegalArgumentException e) {
                				SkyWarsReloaded.get().getServer().getLogger().info(sound + " is not a valid sound in winsounds.yml");
                			}
                		} else {
                			winSoundList.add(new SoundItem(key, sound, name, level, volume, pitch, mat, isCustom));
                		}
                			
                    } else {
                    	SkyWarsReloaded.get().getServer().getLogger().info(mat + " is not a valid Material in winsounds.yml");
                    }
            	}
            }
        }
        Collections.<SoundItem>sort(winSoundList);
    }
    
    public SoundItem getWinSoundByName(String name) {
    	for (SoundItem pi: winSoundList) {
    		if (pi.getName().equalsIgnoreCase(name)) {
    			return pi;
    		}
    	}
        return null;
    }
    
    public ArrayList<SoundItem> getWinSoundItems() {
    	return winSoundList;
    }
    
    public SoundItem getWinSoundBySound(String effect) {
    	for (SoundItem pItem: winSoundList) {
    		if (pItem.getSound().equalsIgnoreCase(effect)) {
    			return pItem;
    		}
    	}
        return null;
    }
    
	/*Loads tuants from taunts.yml configuation.
	 */
	private void loadTaunts() {
		tauntList.clear();
        File tauntFile = new File(SkyWarsReloaded.get().getDataFolder(), "taunts.yml");

        if (!tauntFile.exists()) {
        	SkyWarsReloaded.get().saveResource("taunts.yml", false);
        }
        
        if (tauntFile.exists()) {
            FileConfiguration storage = YamlConfiguration.loadConfiguration(tauntFile);

            if (storage.getConfigurationSection("taunts") != null) {
            	for (String key: storage.getConfigurationSection("taunts").getKeys(false)) {
                	String name = storage.getString("taunts." + key + ".name");
                	List<String> lore = storage.getStringList("taunts." + key + ".lore");
                	int level = storage.getInt("taunts." + key + ".level");
                	String message = storage.getString("taunts." + key + ".message");
                	String sound = storage.getString("taunts." + key + ".sound");
                	boolean useCustomSound = storage.getBoolean("taunts." + key + ".useCustomSound", false);
                	double volume = storage.getDouble("taunts." + key + ".volume");
                	double pitch = storage.getDouble("taunts." + key + ".pitch");
                	double speed = storage.getDouble("taunts." + key + ".particleSpeed");
                	int density = storage.getInt("taunts." + key + ".particleDensity");
                	List<String> particles = storage.getStringList("taunts." + key + ".particles");
                	Material icon = Material.valueOf(storage.getString("taunts." + key + ".icon", "DIAMOND"));
                	tauntList.add(new Taunt(key, name, lore, message, sound, useCustomSound, volume, pitch, speed, density, particles, icon, level));
                }
            } 
        }
        Collections.<Taunt>sort(tauntList);
	}
	    
    /**Returns a list of tuants that are currently loaded.
	 */
    public ArrayList<Taunt> getTaunts() {
    	return tauntList;
    }
    
	public Taunt getTauntFromName(String name) {
		for (Taunt taunt: tauntList) {
			if (taunt.getName().equalsIgnoreCase(name)) {
				return taunt;
			}
		}
		return null;
	}
	
	public Taunt getTauntFromKey(String key) {
		for (Taunt taunt: tauntList) {
			if (taunt.getKey().equalsIgnoreCase(key)) {
				return taunt;
			}
		}
		return null;
	}
    
    /*Handles projectile effects*/
  
    public void addProjectile(Projectile p, String e) {
		projectileMap.put(p,  e);
	}
	
	private void doEffect(Projectile projectile, String effect) {
		World world = projectile.getWorld();
		Location location = projectile.getLocation();
		if (effect.equalsIgnoreCase("none")) {
        } else if (effect.equalsIgnoreCase("flame")) {
        	Util.get().sendParticles(world, "FLAME", (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 2);
        }  else if (effect.equalsIgnoreCase("smoke")) {
        	Util.get().sendParticles(world, "SMOKE_LARGE", (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 2);
        } else if (effect.equalsIgnoreCase("portal")) {
        	Util.get().sendParticles(world, "PORTAL", (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 2);
        } else if (effect.equalsIgnoreCase("heart")) {
        	Util.get().sendParticles(world, "HEART", (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 2);
        } else if (effect.equalsIgnoreCase("critical")) {
        	Util.get().sendParticles(world, "CRIT", (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 2);
        }  else if (effect.equalsIgnoreCase("water")) {
        	Util.get().sendParticles(world, "WATER_SPLASH", (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 2);
        } else if (effect.equalsIgnoreCase("redstone")) {
        	Util.get().sendParticles(world, "REDSTONE", (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 2);
        } else if (effect.equalsIgnoreCase("sparks")) {
        	Util.get().sendParticles(world, "FIREWORKS_SPARK", (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 2);
        } else if (effect.equalsIgnoreCase("lava_drip")) {
        	Util.get().sendParticles(world, "DRIP_LAVA", (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 2);
        } else if (effect.equalsIgnoreCase("lava")) {
        	Util.get().sendParticles(world, "LAVA", (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 2);
        }  else if (effect.equalsIgnoreCase("alphabet")) {
        	Util.get().sendParticles(world, "ENCHANTMENT_TABLE", (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 2);
        } else if (effect.equalsIgnoreCase("happy")) {
        	Util.get().sendParticles(world, "VILLAGER_HAPPY", (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 2);
        } else if (effect.equalsIgnoreCase("magic")) {
        	Util.get().sendParticles(world, "SPELL_WITCH", (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 2);
       	} else if (effect.equalsIgnoreCase("music")) {
       		Util.get().sendParticles(world, "NOTE", (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 2);
       	} else if (effect.equalsIgnoreCase("angry")) {
       		Util.get().sendParticles(world, "VILLAGER_ANGRY", (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 2);
       	} else if (effect.equalsIgnoreCase("clouds")) {
       		Util.get().sendParticles(world, "CLOUD", (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 2);
       	} else if (effect.equalsIgnoreCase("potion")) {
       		Util.get().sendParticles(world, "SPELL", (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 2);
        } else if (effect.equalsIgnoreCase("poison")) {
        	Util.get().sendParticles(world, "SPELL_INSTANT", (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 2);
        } else if (effect.equalsIgnoreCase("snow")) {
        	Util.get().sendParticles(world, "SNOWBALL", (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 2);
        } else if (effect.equalsIgnoreCase("slime")) {
        	Util.get().sendParticles(world, "SLIME", (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 2);
        }
	}
    
}
