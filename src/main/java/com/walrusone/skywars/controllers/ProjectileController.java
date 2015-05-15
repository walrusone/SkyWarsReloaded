package com.walrusone.skywars.controllers;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Projectile;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.utilities.Messaging;
import com.walrusone.skywars.utilities.ParticleEffect;
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
        if (effect.equalsIgnoreCase("normal")) {
        } else if (effect.equalsIgnoreCase("flame")) {
            Random random = new Random();
            ParticleEffect.FLAME.display(0, 0, 0, 0, random.nextInt((8 - 4) + 4) + 1, projectile.getLocation(), 300);
        }  else if (effect.equalsIgnoreCase("smoke")) {
            Random random = new Random();
            ParticleEffect.SMOKE_LARGE.display(0, 0, 0, 0, random.nextInt((16 - 8) + 8) + 1, projectile.getLocation(), 300);
        } else if (effect.equalsIgnoreCase("portal")) {
            Random random = new Random();
            ParticleEffect.PORTAL.display(0, 0, 0, 0, random.nextInt((16 - 8) + 8) + 1, projectile.getLocation(), 300);
        } else if (effect.equalsIgnoreCase("heart")) {
            Random random = new Random();
            ParticleEffect.HEART.display(0, 0, 0, 0, random.nextInt((10 - 3) + 3) + 1, projectile.getLocation(), 300);
        } else if (effect.equalsIgnoreCase("critical")) {
            Random random = new Random();
            ParticleEffect.CRIT.display(0, 0, 0, 0, random.nextInt((8 - 5) + 5) + 1, projectile.getLocation(), 300);
        }  else if (effect.equalsIgnoreCase("water")) {
            Random random = new Random();
            ParticleEffect.WATER_SPLASH.display(0, 0, 0, 0, random.nextInt((14 - 8) + 8) + 1, projectile.getLocation(), 300);
        } else if (effect.equalsIgnoreCase("redstone")) {
            Random random = new Random();
            ParticleEffect.REDSTONE.display(0, 0, 0, 0, random.nextInt((10 - 3) + 3) + 1, projectile.getLocation(), 300);
        } else if (effect.equalsIgnoreCase("sparks")) {
            Random random = new Random();
            ParticleEffect.FIREWORKS_SPARK.display(0, 0, 0, 0, random.nextInt((8 - 4) + 4) + 1, projectile.getLocation(), 300);
        } else if (effect.equalsIgnoreCase("lava_drip")) {
            Random random = new Random();
            ParticleEffect.DRIP_LAVA.display(0, 0, 0, 0, random.nextInt((8 - 4) + 4) + 1, projectile.getLocation(), 300);
        } else if (effect.equalsIgnoreCase("lava")) {
            Random random = new Random();
            ParticleEffect.LAVA.display(0, 0, 0, 0, random.nextInt((16 - 4) + 4) + 1, projectile.getLocation(), 300);
        }  else if (effect.equalsIgnoreCase("alphabet")) {
            Random random = new Random();
            ParticleEffect.ENCHANTMENT_TABLE.display(0, 0, 0, 0, random.nextInt((16 - 8) + 8) + 1, projectile.getLocation(), 300);
        } else if (effect.equalsIgnoreCase("happy")) {
            Random random = new Random();
            ParticleEffect.VILLAGER_HAPPY.display(0, 0, 0, 0, random.nextInt((12 - 4) + 4) + 1, projectile.getLocation(), 300);
        } else if (effect.equalsIgnoreCase("magic")) {
            Random random = new Random();
            ParticleEffect.SPELL_WITCH.display(0, 0, 0, 0, random.nextInt((10 - 7) + 7) + 1, projectile.getLocation(), 300);
        } else if (effect.equalsIgnoreCase("music")) {
            Random random = new Random();
            ParticleEffect.NOTE.display(0, 0, 0, 0, random.nextInt((10 - 4) + 4) + 1, projectile.getLocation(), 300);
        } else if (effect.equalsIgnoreCase("angry")) {
            Random random = new Random();
            ParticleEffect.VILLAGER_ANGRY.display(0, 0, 0, 0, random.nextInt((5 - 2) + 2) + 1, projectile.getLocation(), 300);
        } else if (effect.equalsIgnoreCase("clouds")) {
            Random random = new Random();
            ParticleEffect.CLOUD.display(0, 0, 0, 0, random.nextInt((12 - 6) + 6) + 1, projectile.getLocation(), 300);
        } else if (effect.equalsIgnoreCase("potion")) {
            Random random = new Random();
            ParticleEffect.SPELL.display(0, 0, 0, 0, random.nextInt((12 - 6) + 6) + 1, projectile.getLocation(), 300);
        } else if (effect.equalsIgnoreCase("poison")) {
            Random random = new Random();
            ParticleEffect.SPELL_INSTANT.display(0, 0, 0, 0, random.nextInt((12 - 6) + 6) + 1, projectile.getLocation(), 300);
        } else if (effect.equalsIgnoreCase("snow")) {
            Random random = new Random();
            ParticleEffect.SNOWBALL.display(0, 0, 0, 0, random.nextInt((12 - 6) + 6) + 1, projectile.getLocation(), 300);
        } else if (effect.equalsIgnoreCase("slime")) {
            Random random = new Random();
            ParticleEffect.SLIME.display(0, 0, 0, 0, random.nextInt((12 - 6) + 6) + 1, projectile.getLocation(), 300);
        }
	}
	
}
