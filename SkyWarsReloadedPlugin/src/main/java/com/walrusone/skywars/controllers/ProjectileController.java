package com.walrusone.skywars.controllers;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Projectile;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.utilities.Messaging;
import com.walrusone.skywars.utilities.ParticleItem;

public class ProjectileController {

	private final Map<Projectile, String> projectileMap = Maps.newConcurrentMap();
	private final Map<String, ParticleItem> projEffectMap = Maps.newHashMap();
	
	public ProjectileController() {
		load();
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
	
	public void load() {
        projEffectMap.clear();
        File particleFile = new File(SkyWarsReloaded.get().getDataFolder(), "projectileeffects.yml");

        if (!particleFile.exists()) {
        	SkyWarsReloaded.get().saveResource("projectileeffects.yml", false);
        }

        if (particleFile.exists()) {
            FileConfiguration storage = YamlConfiguration.loadConfiguration(particleFile);

            if (storage.contains("effects")) {
                for (String item : storage.getStringList("effects")) {
                    List<String> itemData = new LinkedList<String>(Arrays.asList(item.split(" ")));

                    int cost = Integer.parseInt(itemData.get(1));
        
                    String effect = itemData.get(0).toLowerCase();
                    String name = null;
                    
                    switch (effect) {
                    case "water": {
                    	name = new Messaging.MessageFormatter().format("effects.water");
                        break;
                    }
                    case "flame": {
                    	name = new Messaging.MessageFormatter().format("effects.flame");
                    	break;
                    }
                    case "smoke": {
                    	name = new Messaging.MessageFormatter().format("effects.smoke");
                    	break;
                    }
                    case "critical": {
                    	name = new Messaging.MessageFormatter().format("effects.critical");
                    	break;
                    }
                    case "slime": {
                    	name = new Messaging.MessageFormatter().format("effects.slime");
      			  		break;
                	}
                    case "snow": {
                    	name = new Messaging.MessageFormatter().format("effects.snow");
      			  		break;
            		}
                    case "magic": {
                    	name = new Messaging.MessageFormatter().format("effects.magic");
                    	break;
                    }
                    case "music": {
                    	name = new Messaging.MessageFormatter().format("effects.music");
                    	break;
                    }
                    case "happy": {
                    	name = new Messaging.MessageFormatter().format("effects.happy");
      			  		break;
                    }
                    case "angry": {
                    	name = new Messaging.MessageFormatter().format("effects.angry");
      			  		break;
                	}
                    case "potion": {
                    	name = new Messaging.MessageFormatter().format("effects.potion");
      			  		break;
            		}
                    case "poison": {
                    	name = new Messaging.MessageFormatter().format("effects.poison");
      			  		break;
        			}
                    case "alphabet": {
                    	name = new Messaging.MessageFormatter().format("effects.alphabet");
                    	break;
    				}
                    case "lava": {
                    	name = new Messaging.MessageFormatter().format("effects.lava");
      			  		break;
					}
                    case "lava_drip": {
                    	name = new Messaging.MessageFormatter().format("effects.lava_drip");
      			  		break;
                    }
                    case "heart": {
                    	name = new Messaging.MessageFormatter().format("effects.heart");
      			  		break;
                	}
                    case "redstone": {
                    	name = new Messaging.MessageFormatter().format("effects.redstone");
      			  		break;
            		}
                    case "sparks": {
                    	name = new Messaging.MessageFormatter().format("effects.sparks");
      			  		break;
        			}
                    case "portal": {
                    	name = new Messaging.MessageFormatter().format("effects.portal");
      			  		break;
    				}
                    case "clouds": {
                    	name = new Messaging.MessageFormatter().format("effects.clouds");
          			  	break;
                    }
      			    }
                    
                    if (name != null) {
                    	projEffectMap.put(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', name)), new ParticleItem(effect, name, cost));
                    }
                }
            }
        }
    }
	
    public ParticleItem getByName(String name) {
        return projEffectMap.get(name);
    }
    
    public List<ParticleItem> getParticleItems() {
    	return Lists.newArrayList(projEffectMap.values());
    }
    
	public void addProjectile(Projectile p, String e) {
		projectileMap.put(p,  e);
	}
	
	private void doEffect(Projectile projectile, String effect) {
		World world = projectile.getWorld();
		Location location = projectile.getLocation();
		if (effect.equalsIgnoreCase("normal")) {
        } else if (effect.equalsIgnoreCase("flame")) {
            SkyWarsReloaded.getNMS().sendParticles(world, "FLAME", (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 2);
        }  else if (effect.equalsIgnoreCase("smoke")) {
            SkyWarsReloaded.getNMS().sendParticles(world, "SMOKE_LARGE", (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 2);
        } else if (effect.equalsIgnoreCase("portal")) {
            SkyWarsReloaded.getNMS().sendParticles(world, "PORTAL", (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 2);
        } else if (effect.equalsIgnoreCase("heart")) {
            SkyWarsReloaded.getNMS().sendParticles(world, "HEART", (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 2);
        } else if (effect.equalsIgnoreCase("critical")) {
            SkyWarsReloaded.getNMS().sendParticles(world, "CRIT", (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 2);
        }  else if (effect.equalsIgnoreCase("water")) {
            SkyWarsReloaded.getNMS().sendParticles(world, "WATER_SPLASH", (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 2);
        } else if (effect.equalsIgnoreCase("redstone")) {
            SkyWarsReloaded.getNMS().sendParticles(world, "REDSTONE", (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 2);
        } else if (effect.equalsIgnoreCase("sparks")) {
            SkyWarsReloaded.getNMS().sendParticles(world, "FIREWORKS_SPARK", (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 2);
        } else if (effect.equalsIgnoreCase("lava_drip")) {
            SkyWarsReloaded.getNMS().sendParticles(world, "DRIP_LAVA", (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 2);
        } else if (effect.equalsIgnoreCase("lava")) {
            SkyWarsReloaded.getNMS().sendParticles(world, "LAVA", (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 2);
        }  else if (effect.equalsIgnoreCase("alphabet")) {
            SkyWarsReloaded.getNMS().sendParticles(world, "ENCHANTMENT_TABLE", (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 2);
        } else if (effect.equalsIgnoreCase("happy")) {
            SkyWarsReloaded.getNMS().sendParticles(world, "VILLAGER_HAPPY", (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 2);
        } else if (effect.equalsIgnoreCase("magic")) {
            SkyWarsReloaded.getNMS().sendParticles(world, "SPELL_WITCH", (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 2);
       	} else if (effect.equalsIgnoreCase("music")) {
            SkyWarsReloaded.getNMS().sendParticles(world, "NOTE", (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 2);
       	} else if (effect.equalsIgnoreCase("angry")) {
            SkyWarsReloaded.getNMS().sendParticles(world, "VILLAGER_ANGRY", (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 2);
       	} else if (effect.equalsIgnoreCase("clouds")) {
            SkyWarsReloaded.getNMS().sendParticles(world, "CLOUD", (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 2);
       	} else if (effect.equalsIgnoreCase("potion")) {
            SkyWarsReloaded.getNMS().sendParticles(world, "SPELL", (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 2);
        } else if (effect.equalsIgnoreCase("poison")) {
            SkyWarsReloaded.getNMS().sendParticles(world, "SPELL_INSTANT", (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 2);
        } else if (effect.equalsIgnoreCase("snow")) {
            SkyWarsReloaded.getNMS().sendParticles(world, "SNOWBALL", (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 2);
        } else if (effect.equalsIgnoreCase("slime")) {
            SkyWarsReloaded.getNMS().sendParticles(world, "SLIME", (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 2);
        }
	}
	
}
