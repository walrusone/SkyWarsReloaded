package com.walrusone.skywars.controllers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.utilities.Messaging;
import com.walrusone.skywars.utilities.ParticleItem;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ParticleController {

    private final Map<String, ParticleItem> particleMap = Maps.newHashMap();

    public ParticleController() {
        load();
    }

    public void load() {
        particleMap.clear();
        File particleFile = new File(SkyWarsReloaded.get().getDataFolder(), "particleeffects.yml");

        if (!particleFile.exists()) {
        	SkyWarsReloaded.get().saveResource("particleeffects.yml", false);
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
                    	particleMap.put(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', name)), new ParticleItem(effect, name, cost));
                    }
                }
            }
        }
    }
    
 
    public ParticleItem getByName(String name) {
        return particleMap.get(name);
    }
    
    public List<ParticleItem> getParticleItems() {
    	return Lists.newArrayList(particleMap.values());
    }
    
    public ParticleItem getByEffect(String effect) {
    	for (ParticleItem pItem: particleMap.values()) {
    		if (pItem.getEffect().equalsIgnoreCase(effect)) {
    			return pItem;
    		}
    	}
        return null;
    }
    
}
