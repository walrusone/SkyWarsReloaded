package com.walrusone.skywars.controllers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.Game.GameState;
import com.walrusone.skywars.game.GameKit;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.utilities.IconMenu;
import com.walrusone.skywars.utilities.Messaging;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class KitController {

    private static final String premissionPrefix = "swr.kit.";
    private static final int menuSlotsPerRow = 9;
    private static final int menuSize = 54;
    private static final String menuName = new Messaging.MessageFormatter().format("menu.kit-menu-title");

    private final Map<String, GameKit> kitMap = Maps.newHashMap();

    public KitController() {
        load();
    }

    public void load() {
        kitMap.clear();
        File dataDirectory = SkyWarsReloaded.get().getDataFolder();
        File kitsDirectory = new File(dataDirectory, "kits");

        if (!kitsDirectory.exists()) {
            if (!kitsDirectory.mkdirs())  {
                return;
            }
            SkyWarsReloaded.get().saveResource("example.yml", true);
            copyFiles(new File(dataDirectory, "example.yml"), new File(kitsDirectory, "example.yml"));
            File delete = new File(dataDirectory, "example.yml");
            delete.delete();
        }

        File[] kits = kitsDirectory.listFiles();
        if (kits == null) {
            return;
        }

        for (File kit : kits) {
            if (!kit.getName().endsWith(".yml")) {
                continue;
            }

            String name = kit.getName().replace(".yml", "");

            if (!name.isEmpty() && !kitMap.containsKey(name)) {
                kitMap.put(name, new GameKit(name, YamlConfiguration.loadConfiguration(kit), kit));
            }
        }
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
    
    public boolean hasPermission(Player player, GameKit kit) {
        return player.isOp() || SkyWarsReloaded.perms.has(player, premissionPrefix + kit.getName().toLowerCase());
    }
    
    public boolean hasFreePermission(Player player, GameKit kit) {
        return player.isOp() || SkyWarsReloaded.perms.has(player, premissionPrefix + "free." + kit.getName().toLowerCase());
    }

    public boolean isPurchaseAble(GameKit kit) {
        if (kit.getCost() > 0) {
        	return true;
        }
        return false;
    }

    public boolean canPurchase(GamePlayer gamePlayer, GameKit kit) {
    	boolean economy = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.useExternalEconomy");
    	if (economy) {
            return kit.getCost() > 0 && (SkyWarsReloaded.econ.getBalance(gamePlayer.getP()) >= kit.getCost());
    	} else {
    		return kit.getCost() > 0 && (gamePlayer.getBalance() >= kit.getCost());
    	}
    }

    public void populateInventory(Inventory inventory, GameKit kit) {
        for (ItemStack itemStack : kit.getItems()) {
        	try {
                inventory.addItem(itemStack);
        	} catch(NullPointerException e) {
        	}

        }
    }
    
    public void givePotionEffects(GamePlayer gamePlayer, GameKit kit) {
    	for (PotionEffect pEffect: kit.getPotionEffects()) {
    		if (gamePlayer.getP() != null) {
        		gamePlayer.getP().addPotionEffect(pEffect);
    		}
    	}
    }

    public GameKit getByName(String name) {
    	for (String kitName: kitMap.keySet()) {
    		if (kitMap.get(kitName).getKitName().equalsIgnoreCase(name)) {
    			return kitMap.get(kitName);
    		}
    	}
		return null;
    }

    public void openKitMenu(final GamePlayer gamePlayer) {
        List<GameKit> availableKits = Lists.newArrayList(kitMap.values());

        int rowCount = menuSlotsPerRow;
        while (rowCount < availableKits.size() && rowCount < menuSize) {
            rowCount += menuSlotsPerRow;
        }

        SkyWarsReloaded.getIC().create(gamePlayer.getP(), menuName, rowCount, new IconMenu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IconMenu.OptionClickEvent event) {
                if (gamePlayer.inGame() && gamePlayer.getGame().getState() == GameState.PLAYING) {
                	event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("error.can-not-pick-kit"));
                    return;
                }

                if (gamePlayer.getGame().getState() != GameState.PREGAME) {
                	event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("error.can-not-pick-kit"));
                    return;
                }

                GameKit kit = getByName(ChatColor.stripColor(event.getName()));
                if (kit == null) {
                    return;
                }

                if (!hasPermission(event.getPlayer(), kit) && !hasFreePermission(event.getPlayer(), kit)) {
                	event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("error.no-permission-kit"));
                	return;
                }

                if (!hasFreePermission(event.getPlayer(), kit)) {
                	if (isPurchaseAble(kit)) {
                		if (!canPurchase(gamePlayer, kit)) {
                    		event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("error.not-enough-balance"));
                            return;
                		} else {
                			removeBalance(gamePlayer, kit.getCost());
                		} 
                	}
                }    

                event.setWillClose(true);
                event.setWillDestroy(true);

                gamePlayer.setSelectedKit(kit);
                gamePlayer.setKitSelected(true);
                
                event.getPlayer().sendMessage(new Messaging.MessageFormatter().setVariable("kit", kit.getKitName()).format("game.enjoy-kit"));
            }
        });

        for (int iii = 0; iii < availableKits.size(); iii ++) {
            if (iii >= menuSize) {
                break;
            }

            GameKit kit = availableKits.get(iii);
            List<String> loreList = Lists.newLinkedList();
            boolean canPurchase = false;

            if (isPurchaseAble(kit)) {
            	if (SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.useExternalEconomy")) {
            		loreList.add("\247r\2476Price\247f: \247" + (SkyWarsReloaded.econ.getBalance(gamePlayer.getP()) >= kit.getCost() ? 'a' : 'c') + kit.getCost());
            	} else {
            		loreList.add("\247r\2476Price\247f: \247" + (gamePlayer.getBalance() >= kit.getCost() ? 'a' : 'c') + kit.getCost());
            	}
                loreList.add(" ");

                if (canPurchase(gamePlayer, kit)) {
                    canPurchase = true;
                }

            } else if (!hasPermission(gamePlayer.getP(), kit)) {
                loreList.add("No permission");
                loreList.add(" ");

            } else {
                canPurchase = true;
            }

            loreList.addAll(kit.getLores());

            SkyWarsReloaded.getIC().setOption(
                    gamePlayer.getP(),
                    kit.getPosition(),
                    kit.getIcon(),
                    "\247r\247" + (canPurchase ? 'a' : 'c') + kit.getKitName(),
                    loreList.toArray(new String[loreList.size()]));
        }

        SkyWarsReloaded.getIC().show(gamePlayer.getP());
    }
    
    private void removeBalance(GamePlayer p, int x) {
    	boolean economy = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.useExternalEconomy");
    	if (economy) {
    		SkyWarsReloaded.econ.withdrawPlayer(SkyWarsReloaded.get().getServer().getOfflinePlayer(p.getUUID()), x);
    	} else {
    		p.setBalance(p.getBalance() - x);
    	}
    }
}
