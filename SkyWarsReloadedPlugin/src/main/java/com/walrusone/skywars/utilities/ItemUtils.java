package com.walrusone.skywars.utilities;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
        		if (amount < 1) {
        			return null;
        		}
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
            		} else if (item.get(x).split(":")[0].equalsIgnoreCase("color")) {
            			if (itemStack.getType().equals(Material.LEATHER_BOOTS) || itemStack.getType().equals(Material.LEATHER_LEGGINGS) || itemStack.getType().equals(Material.LEATHER_HELMET) || itemStack.getType().equals(Material.LEATHER_CHESTPLATE)) {
            				LeatherArmorMeta itemMeta = (LeatherArmorMeta) itemStack.getItemMeta();
            				itemMeta.setColor(getColor(item.get(x).split(":")[1]));
            				itemStack.setItemMeta(itemMeta);
            			}
            		} else {
                        itemStack.addUnsafeEnchantment(getEnchant(item.get(x).split(":")[0]), Integer.parseInt(item.get(x).split(":")[1]));
            		}
            	}

            }

        } catch (Exception ignored) {

        }
        return itemStack;
    }
    
    public static PotionEffect parseEffect(List<String> effect) {
        if (effect.size() < 2) {
            return null;
        }

        PotionEffect potionEffect = null;

        try {
        	int length;
        	int level;
        	PotionEffectType pType = getPotionType(effect.get(0));
           	if (Integer.parseInt(effect.get(1)) == -1) {
        	   length = Integer.MAX_VALUE;
           	} else {
        	   length = 20 * Integer.parseInt(effect.get(1));
           	}
           	level = Integer.parseInt(effect.get(2));
           	
           	potionEffect = new PotionEffect(pType, length, level);

        } catch (Exception ignored) {

        }
        return potionEffect;
    }

    
    private static PotionEffectType getPotionType(String type) {
    	switch (type.toLowerCase()) {
    	case "speed": return PotionEffectType.SPEED;
    	case "slowness": return PotionEffectType.SLOW;
    	case "haste": return PotionEffectType.FAST_DIGGING;
    	case "miningfatique": return PotionEffectType.SLOW_DIGGING;
    	case "strength": return PotionEffectType.INCREASE_DAMAGE;
    	case "instanthealth": return PotionEffectType.HEAL;
    	case "instantdamage": return PotionEffectType.HARM;
    	case "jumpboost": return PotionEffectType.JUMP;
    	case "nausea": return PotionEffectType.CONFUSION;
    	case "regeneration": return PotionEffectType.REGENERATION;
    	case "resistance": return PotionEffectType.DAMAGE_RESISTANCE;
    	case "fireresistance": return PotionEffectType.FIRE_RESISTANCE;
    	case "waterbreathing": return PotionEffectType.WATER_BREATHING;
    	case "invisibility": return PotionEffectType.INVISIBILITY;
    	case "blindness": return PotionEffectType.BLINDNESS;
    	case "nightvision": return PotionEffectType.NIGHT_VISION;
    	case "hunger": return PotionEffectType.HUNGER;
    	case "weakness": return PotionEffectType.WEAKNESS;
    	case "poison": return PotionEffectType.POISON;
    	case "wither": return PotionEffectType.WITHER;
    	case "healthboost": return PotionEffectType.HEALTH_BOOST;
    	case "absorption": return PotionEffectType.ABSORPTION;
    	case "saturation": return PotionEffectType.SATURATION;
    	default: return null;	
    	}
    }

    private static Enchantment getEnchant(String enchant) {
    	switch (enchant.toLowerCase()) {
    	case "protection": return Enchantment.PROTECTION_ENVIRONMENTAL;
    	case "projectileprotection": return Enchantment.PROTECTION_PROJECTILE;
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
    	case "depthstrider": return Enchantment.DEPTH_STRIDER;
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
    	case "luck": return Enchantment.LUCK;
    	case "lure": return Enchantment.LURE;
    	case "thorns": return Enchantment.THORNS;
    	default: return null;	
    	}
    }
    
    public static boolean isEnchanted(ItemStack itemStack) {
     	return !itemStack.getEnchantments().isEmpty();
    }
    
	public static Color getColor(String c) {
		switch (c) {
		case "aqua": return Color.AQUA;
		case "black": return Color.BLACK;
		case "blue": return Color.BLUE;
		case "fuschia": return Color.FUCHSIA;
		case "gray": return Color.GRAY;
		case "green": return Color.GREEN;
		case "lime": return Color.LIME;
		case "maroon": return Color.MAROON;
		case "navy": return Color.NAVY;
		case "olvie": return Color.OLIVE;
		case "orange": return Color.ORANGE;
		case "purple": return Color.PURPLE;
		case "red": return Color.RED;
		case "silver": return Color.SILVER;
		case "teal": return Color.TEAL;
		case "white": return Color.WHITE;
		case "yellow": return Color.YELLOW;
		default: return Color.NAVY;
		}
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
