package com.walrusone.skywarsreloaded.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import com.walrusone.skywarsreloaded.utilities.Util;
import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.objects.PlayerStat;

public class Util {

	private static Util instance;
	
	public static Util get() {
        if (Util.instance == null) {
            Util.instance = new Util();
        }
        return Util.instance;
	}
	
	public boolean hp(CommandSender sender, String s) {
		if (sender.hasPermission("sw." + s)) {
			return true;
		}
		return false;
	}
	
	public void playSound(Player player, Location location, String sound, float volume, float pitch) {
		if (SkyWarsReloaded.getCfg().soundsEnabled()) {
			try {
				if (player != null) {
					player.playSound(location, Sound.valueOf(sound), volume, pitch);
				}
			} catch (IllegalArgumentException | NullPointerException e) {
				SkyWarsReloaded.get().getLogger().info("ERROR: " + sound + " is not a valid bukkit sound. Please check your configs");
			}
		}
	}
	
	public int getRandomNum(int max, int min) {
		Random rand = new Random();
	    int ii = min + rand.nextInt(((max - (min)) + 1));
	    return ii;
	}
	
	public boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException | NullPointerException e) {
			return false;
		}
	}
	
	public boolean isFloat(String s) {
		try {
			Float.parseFloat(s);
			return true;
		} catch (NumberFormatException | NullPointerException e) {
			return false;
		}
	}
	
    public Location stringToLocation(final String location) {
    	if (location != null) {
        	final String[] locationParts = location.split(":");
            if (locationParts.length != 6) {
            	return null;
            } else {
            	 return new Location(SkyWarsReloaded.get().getServer().getWorld(locationParts[0]), Double.parseDouble(locationParts[1]), Double.parseDouble(locationParts[2]), Double.parseDouble(locationParts[3]), Float.parseFloat(locationParts[4]), Float.parseFloat(locationParts[5]));   
            }
    	}
    	return null;
    }
    
    public String locationToString(final Location location) {
        return location.getWorld().getName() + ":" + location.getX() + ":" + location.getY() + ":" + location.getZ() + ":" + location.getYaw() + ":" + location.getPitch();
    }
	
    public void copyFiles(File source, File target){
	    try {
	        ArrayList<String> ignore = new ArrayList<String>(Arrays.asList("uid.dat", "session.dat"));
	        if(!ignore.contains(source.getName())) {
	            if(source.isDirectory()) {
	                if(!target.exists())
	                target.mkdirs();
	                String files[] = source.list();
	                for (String file : files) {
	                    File srcFile = new File(source, file);
	                    File destFile = new File(target, file);
	                    copyFiles(srcFile, destFile);
	                }
	            } else {
	                InputStream in = new FileInputStream(source);
	                OutputStream out = new FileOutputStream(target);
	                byte[] buffer = new byte[1024];
	                int length;
	                while ((length = in.read(buffer)) > 0)
	                    out.write(buffer, 0, length);
	                in.close();
	                out.close();
	            }
	        }
	    } catch (IOException e) {
	 
	    }
	}
      
    public void clear(final Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents((ItemStack[])null);
        for (final PotionEffect a1 : player.getActivePotionEffects()) {
        	player.removePotionEffect(a1.getType());
        }
    }

     
	public void respawnPlayer(Player player) {
		SkyWarsReloaded.getNMS().respawnPlayer(player);
	}
	
	public ItemStack name(ItemStack itemStack, String name, String... lores) {
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
	
	public double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	public byte getByteFromColor(String color) {
		switch (color) {
			case "white": return (byte) 0;
			case "orange": return (byte) 1;
			case "magenta": return (byte) 2;
			case "lightblue": return (byte) 3;
			case "yellow": return (byte) 4;
			case "lime": return (byte) 5;
			case "pink": return (byte) 6;
			case "gray": return (byte) 7;
			case "lightgray": return (byte) 8;
			case "cyan": return (byte) 9;
			case "purple": return (byte) 10;
			case "blue": return (byte) 11;
			case "brown": return (byte) 12;
			case "green": return (byte) 13;
			case "red": return (byte) 14;
			case "black": return (byte) 15;
			case "none": return (byte) -2;
			case "lapis": return (byte) -3;
			case "redstone": return (byte) -4;
			case "emerald": return (byte) -5;
			case "diamond": return (byte) -6;
			default: return (byte) -1;
		}
	}
	
	public void setPlayerExperience(Player player, int amount) {
		if (amount <= 352) {
			int level = (int) Math.floor(quadraticEquationRoot(1, 6, 0-amount));
			double nextLevel = 2 * level + 7;
			double levelExp = (int) ((level * level) + 6 * level);
			double leftOver = amount - levelExp;
			player.setLevel(level);
			player.setExp((float) (leftOver/nextLevel));
		} else if (amount <= 1507) {
			int level = (int) Math.floor(quadraticEquationRoot(2.5, -40.5, 360-amount));
			double nextLevel = 5 * level - 38;
			double levelExp = (int) (2.5 * (level * level) - 40.5 * level + 360);
			double leftOver = amount - levelExp;
			player.setLevel(level);
			player.setExp((float) (leftOver/nextLevel));
		} else {
			int level = (int) Math.floor(quadraticEquationRoot(4.5, -162.5, 2220-amount));
			double nextLevel = 9 * level - 158;
			double levelExp = (int) (4.5 * (level * level) - 162.5 * level + 2220);
			double leftOver = amount - levelExp;
			player.setLevel(level);
			player.setExp((float) (leftOver/nextLevel));
		}
	}
	
	public int getPlayerLevel(Player player) {
	    if (SkyWarsReloaded.getCfg().displayPlayerExeperience()) {
	    	return player.getLevel();
	    } else {
	    	PlayerStat ps = PlayerStat.getPlayerStats(player);
	    	if (ps != null) {
	    		int amount = ps.getXp();
	    		if (amount <= 352) {
	    			return (int) Math.floor(quadraticEquationRoot(1, 6, 0-amount));
	    		} else if (amount <= 1507) {
	    			return (int) Math.floor(quadraticEquationRoot(2.5, -40.5, 360-amount));
	    		} else {
	    			return (int) Math.floor(quadraticEquationRoot(4.5, -162.5, 2220-amount));
	    		}
	    	} else {
	        	return 0;
	    	}
	    }
	}
		
	public static double quadraticEquationRoot(double a, double b, double c) {    
	    double root1, root2;
	    root1 = (-b + Math.sqrt(Math.pow(b, 2) - 4*a*c)) / (2*a);
	    root2 = (-b - Math.sqrt(Math.pow(b, 2) - 4*a*c)) / (2*a);
	    return Math.max(root1, root2);  
	}
	
	public boolean isSpawnWorld(World world) {
		if (SkyWarsReloaded.getCfg().getSpawn() != null) {
			if (world.equals(SkyWarsReloaded.getCfg().getSpawn().getWorld())) {
				return true;
			}
		}
		return false;
	}
}
