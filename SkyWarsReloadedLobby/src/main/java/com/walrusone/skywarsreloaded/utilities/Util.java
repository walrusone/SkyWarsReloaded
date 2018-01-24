package com.walrusone.skywarsreloaded.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import com.walrusone.skywarsreloaded.utilities.Util;
import com.walrusone.skywarsreloaded.SkyWarsReloaded;

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
	
	public int getMultiplier(Player player) {
		if (player.hasPermission("sw.multi10")) {
			return 10;
		} else if (player.hasPermission("sw.multi5")) {
			return 5;
		} else if (player.hasPermission("sw.multi4")) {
			return 4;
		} else if (player.hasPermission("sw.multi3")) {
			return 3;
		} else if (player.hasPermission("sw.multi2")) {
			return 2;
		} else {
			return 1;
		}
	}
	
	public boolean isInteger(String s) {
	    return isInteger(s,10);
	}

	public boolean isInteger(String s, int radix) {
	    if(s.isEmpty()) return false;
	    for(int i = 0; i < s.length(); i++) {
	        if(i == 0 && s.charAt(i) == '-') {
	            if(s.length() == 1) return false;
	            else continue;
	        }
	        if(Character.digit(s.charAt(i),radix) < 0) return false;
	    }
	    return true;
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
        
    public void fireworks(final Player player, final int length, final int fireworksPer5Tick) {
        final List<FireworkEffect.Type> type = new ArrayList<FireworkEffect.Type>(Arrays.<FireworkEffect.Type>asList(FireworkEffect.Type.BALL, FireworkEffect.Type.BALL_LARGE, FireworkEffect.Type.BURST, FireworkEffect.Type.STAR, FireworkEffect.Type.CREEPER));
        final List<Color> colors = new ArrayList<Color>(Arrays.<Color>asList(Color.AQUA, Color.BLACK, Color.BLUE, Color.FUCHSIA, Color.GRAY, Color.GREEN, Color.LIME, Color.MAROON, Color.NAVY, Color.OLIVE, Color.ORANGE, Color.PURPLE, Color.RED, Color.SILVER, Color.TEAL, Color.WHITE, Color.YELLOW));
        final long currentTime = System.currentTimeMillis();
        if (SkyWarsReloaded.get().isEnabled()) {
            new BukkitRunnable() {
                public void run() {
                    if (System.currentTimeMillis() >= currentTime + length * 1000 || SkyWarsReloaded.get().getServer().getPlayer(player.getUniqueId()) == null) {
                        this.cancel();
                    }
                    else {
                        for (int i = 0; i < fireworksPer5Tick; ++i) {
                            final Location loc = player.getLocation();
                            @SuppressWarnings({ "unchecked", "rawtypes" })
							final Firework firework = (Firework)player.getLocation().getWorld().spawn(loc, (Class)Firework.class);
                            final FireworkMeta fMeta = firework.getFireworkMeta();
                            fMeta.addEffects(new FireworkEffect[] { FireworkEffect.builder().withColor(colors.get(new Random().nextInt(17))).withColor(colors.get(new Random().nextInt(17))).withColor(colors.get(new Random().nextInt(17))).with((FireworkEffect.Type)type.get(new Random().nextInt(5))).trail(new Random().nextBoolean()).flicker(new Random().nextBoolean()).build() });
                            fMeta.setPower(new Random().nextInt(2) + 2);
                            firework.setFireworkMeta(fMeta);
                        }
                    }
                }
            }.runTaskTimer(SkyWarsReloaded.get(), 0L, 5L);
        }
    }
       
    public List<Block> getBlocks(Location center, int radius,
            boolean hollow, boolean sphere) {
        List<Location> locs = circle(center, radius, radius, hollow, sphere, 0);
        List<Block> blocks = new ArrayList<Block>();
 
        for (Location loc : locs) {
            blocks.add(loc.getBlock());
        }
 
        return blocks;
    }
 
    public List<Location> circle(Location loc, int radius, int height,
            boolean hollow, boolean sphere, int plusY) {
        List<Location> circleblocks = new ArrayList<Location>();
        int cx = loc.getBlockX();
        int cy = loc.getBlockY();
        int cz = loc.getBlockZ();
 
        for (int x = cx - radius; x <= cx + radius; x++) {
            for (int z = cz - radius; z <= cz + radius; z++) {
                for (int y = (sphere ? cy - radius : cy); y < (sphere ? cy
                        + radius : cy + height); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z)
                            + (sphere ? (cy - y) * (cy - y) : 0);
 
                    if (dist < radius * radius
                            && !(hollow && dist < (radius - 1) * (radius - 1))) {
                        Location l = new Location(loc.getWorld(), x, y + plusY,
                                z);
                        circleblocks.add(l);
                    }
                }
            }
        }
 
        return circleblocks;
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
	
	public String formatScore(int score) {
        return formatScore(score, "");
    }

    public String formatScore(int score, String note) {
        char color = '7';

        if (score > 0) {
            color = 'a';
        } else if (score < 0) {
            color = 'c';
        }

        return "\247" + color + "(" + (score > 0 ? "+" : "") + score + " Elo" + ")";
    }
    
    public void logToFile(String message) {
    	ConsoleCommandSender console = SkyWarsReloaded.get().getServer().getConsoleSender();
    	console.sendMessage(message);

        try {
            File dataFolder = SkyWarsReloaded.get().getDataFolder();
            if(!dataFolder.exists()) {
                dataFolder.mkdir();
            }
 
            File saveTo = new File(dataFolder, "DebugLog.txt");
            if (!saveTo.exists()) {
                saveTo.createNewFile();
            }
            FileWriter fw = new FileWriter(saveTo, true);
            PrintWriter pw = new PrintWriter(fw);
 
            pw.println(ChatColor.stripColor(message));
 
            pw.flush();
 
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
	public boolean isValidEffect(String string) {
		String effect = string.toLowerCase();
		
		switch(effect) {
		case "flame":
            return true;
		case "smoke":
            return true;
		case "portal":
            return true;
		case "heart":
            return true;
        case "critical":
            return true;
		case "water":
            return true;
		case "redstone":
            return true;
		case "sparks":
            return true;
		case "lava_drip":
            return true;
		case "lava":
            return true;
		case "alphabet":
            return true;
		case "happy":
            return true;
		case "magic":
            return true;
		case "music":
            return true;
		case "angry":
            return true;
		case "clouds":
            return true;
		case "potion":
            return true;
		case "poison":
            return true;
		case "snow":
            return true;
		case "slime":
            return true;
		default:
			return false;
		}
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
	
	public static double quadraticEquationRoot(double a, double b, double c) {    
	    double root1, root2;
	    root1 = (-b + Math.sqrt(Math.pow(b, 2) - 4*a*c)) / (2*a);
	    root2 = (-b - Math.sqrt(Math.pow(b, 2) - 4*a*c)) / (2*a);
	    return Math.max(root1, root2);  
	}
	

}
