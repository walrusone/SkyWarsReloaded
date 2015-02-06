package com.walrusone.skywars.utilities;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtils {

    public static ItemStack parseItem(List<String> item) {
        if (item.size() < 2) {
            return null;
        }

        ItemStack itemStack = null;

        try {
        	if (item.get(0).contains(":")) {
        		Material material = Material.getMaterial(item.get(0).split(":")[0].toUpperCase());
        		int amount = Integer.parseInt(item.get(1));
        		short data = (short) Integer.parseInt(item.get(0).split(":")[1].toUpperCase());
        		itemStack = new ItemStack(material, amount, data);
        	} else {
                itemStack = new ItemStack(Material.getMaterial(item.get(0).toUpperCase()), Integer.parseInt(item.get(1)));
        	}

        	if (item.size() > 2) {
            	for (int x = 2; x < item.size(); x++) {
            		if (item.get(x).split(":")[0].equalsIgnoreCase("name")) {
            			ItemMeta itemMeta = itemStack.getItemMeta();
            	        itemMeta.setDisplayName(item.get(x).split(":")[1]);
            	        itemStack.setItemMeta(itemMeta);
            		} else {
                        itemStack.addUnsafeEnchantment(getEnchant(item.get(x).split(":")[0]), Integer.parseInt(item.get(x).split(":")[1]));
            		}
            	}

            }

        } catch (Exception ignored) {

        }
        return itemStack;
    }

    private static Enchantment getEnchant(String enchant) {
    	switch (enchant.toLowerCase()) {
    	case "protection": return Enchantment.PROTECTION_ENVIRONMENTAL;
    	case "fireprotection": return Enchantment.PROTECTION_FIRE;
    	case "featherfall": return Enchantment.PROTECTION_FALL;
    	case "blastprotection": return Enchantment.PROTECTION_EXPLOSIONS;
    	case "respiration": return Enchantment.OXYGEN;
    	case "aquaaffinity": return Enchantment.WATER_WORKER;
    	case "sharpness": return Enchantment.DAMAGE_ALL;
    	case "smite": return Enchantment.DAMAGE_UNDEAD;
    	case "baneofarthropods": return Enchantment.DAMAGE_ARTHROPODS;
    	case "knockback": return Enchantment.KNOCKBACK;
    	case "fireaspect": return Enchantment.FIRE_ASPECT;
    	case "looting": return Enchantment.LOOT_BONUS_MOBS;
    	case "power": return Enchantment.ARROW_DAMAGE;
    	case "punch": return Enchantment.ARROW_KNOCKBACK;
    	case "flame": return Enchantment.ARROW_FIRE;
    	case "infinity": return Enchantment.ARROW_INFINITE;
    	case "efficiency": return Enchantment.DIG_SPEED;
    	case "silktouch": return Enchantment.SILK_TOUCH;
    	case "unbreaking": return Enchantment.DURABILITY;
    	case "fortune": return Enchantment.LOOT_BONUS_BLOCKS;
    	case "luckofthesea": return Enchantment.LUCK;
    	case "lure": return Enchantment.LURE;
    	case "thorns": return Enchantment.THORNS;
    	default: return null;	
    	}
    }
    
    public static boolean isEnchanted(ItemStack itemStack) {
    	if (itemStack.containsEnchantment(Enchantment.ARROW_DAMAGE)) {
    		return true;
    	} else if (itemStack.containsEnchantment(Enchantment.ARROW_DAMAGE)) {
    		return true;
    	}  else if (itemStack.containsEnchantment(Enchantment.ARROW_FIRE)) {
    		return true;
    	}  else if (itemStack.containsEnchantment(Enchantment.ARROW_INFINITE)) {
    		return true;
    	}  else if (itemStack.containsEnchantment(Enchantment.ARROW_KNOCKBACK)) {
    		return true;
    	}  else if (itemStack.containsEnchantment(Enchantment.DAMAGE_ALL)) {
    		return true;
    	}  else if (itemStack.containsEnchantment(Enchantment.DAMAGE_ARTHROPODS)) {
    		return true;
    	}  else if (itemStack.containsEnchantment(Enchantment.DAMAGE_UNDEAD)) {
    		return true;
    	}  else if (itemStack.containsEnchantment(Enchantment.DIG_SPEED)) {
    		return true;
    	}  else if (itemStack.containsEnchantment(Enchantment.DURABILITY)) {
    		return true;
    	}  else if (itemStack.containsEnchantment(Enchantment.FIRE_ASPECT)) {
    		return true;
    	}  else if (itemStack.containsEnchantment(Enchantment.KNOCKBACK)) {
    		return true;
    	}  else if (itemStack.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
    		return true;
    	}  else if (itemStack.containsEnchantment(Enchantment.LOOT_BONUS_MOBS)) {
    		return true;
    	}  else if (itemStack.containsEnchantment(Enchantment.LUCK)) {
    		return true;
    	}  else if (itemStack.containsEnchantment(Enchantment.LURE)) {
    		return true;
    	}  else if (itemStack.containsEnchantment(Enchantment.OXYGEN)) {
    		return true;
    	} else if (itemStack.containsEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL)) {
    		return true;
    	} else if (itemStack.containsEnchantment(Enchantment.PROTECTION_EXPLOSIONS)) {
    		return true;
    	} else if (itemStack.containsEnchantment(Enchantment.PROTECTION_FALL)) {
    		return true;
    	} else if (itemStack.containsEnchantment(Enchantment.PROTECTION_FIRE)) {
    		return true;
    	} else if (itemStack.containsEnchantment(Enchantment.PROTECTION_PROJECTILE)) {
    		return true;
    	} else if (itemStack.containsEnchantment(Enchantment.SILK_TOUCH)) {
    		return true;
    	} else if (itemStack.containsEnchantment(Enchantment.THORNS)) {
    		return true;
    	} else if (itemStack.containsEnchantment(Enchantment.WATER_WORKER)) {
    		return true;
    	} 
    	return false;
    }
    
    public static ItemStack name(ItemStack itemStack, String name, String... lores) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (!name.isEmpty()) {
            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        }

        if (lores.length > 0) {
            List<String> loreList = new ArrayList<String>(lores.length);

            for (String lore : lores) {
                loreList.add(ChatColor.translateAlternateColorCodes('&', lore));
            }

            itemMeta.setLore(loreList);
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}
