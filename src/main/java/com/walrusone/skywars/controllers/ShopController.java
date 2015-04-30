package com.walrusone.skywars.controllers;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.Game.GameState;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.utilities.IconMenu;
import com.walrusone.skywars.utilities.ItemUtils;
import com.walrusone.skywars.utilities.Messaging;

import net.milkbowl.vault.item.Items;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ShopController {

    private static final int menuSlotsPerRow = 9;
    private static final int menuSize = 54;
    private static final String menuName = new Messaging.MessageFormatter().format("menu.shop-menu-title");
    private static final String menuName2 = new Messaging.MessageFormatter().format("menu.spectateshop-menu-title");

    private final Map<String, ShopItem> shopMap = Maps.newHashMap();
    private final Map<String, ShopItem> specShopMap = Maps.newHashMap();

    public ShopController() {
        load();
    }

    public void load() {
        shopMap.clear();
        File shopFile = new File(SkyWarsReloaded.get().getDataFolder(), "shop.yml");

        if (!shopFile.exists()) {
        	SkyWarsReloaded.get().saveResource("shop.yml", false);
        }

        if (shopFile.exists()) {
            FileConfiguration storage = YamlConfiguration.loadConfiguration(shopFile);

            if (storage.contains("items")) {
                for (String item : storage.getStringList("items")) {
                    List<String> itemData = new LinkedList<String>(Arrays.asList(item.split(" ")));

                    int cost = Integer.parseInt(itemData.get(0));
                    itemData.remove(itemData.get(0));
                    
                    ItemStack itemStack = ItemUtils.parseItem(itemData);
                    
                    
                    if (itemStack != null) {
                        shopMap.put(ChatColor.stripColor(Items.itemByStack(itemStack).getName().toString()), new ShopItem(itemStack, cost));
                    }
                }
            }
        }
        
        specShopMap.clear();
        File specShopFile = new File(SkyWarsReloaded.get().getDataFolder(), "spectateshop.yml");

        if (!specShopFile.exists()) {
        	SkyWarsReloaded.get().saveResource("spectateshop.yml", false);
        }

        if (specShopFile.exists()) {
            FileConfiguration storage = YamlConfiguration.loadConfiguration(specShopFile);

            if (storage.contains("items")) {
                for (String item : storage.getStringList("items")) {
                    List<String> itemData = new LinkedList<String>(Arrays.asList(item.split(" ")));

                    int cost = Integer.parseInt(itemData.get(0));
                    itemData.remove(itemData.get(0));
                    
                    ItemStack itemStack = ItemUtils.parseItem(itemData);
                    
                    
                    if (itemStack != null) {
                        specShopMap.put(Items.itemByStack(itemStack).getName().toString(), new ShopItem(itemStack, cost));
                    }
                }
            }
        }
    }
    
    public boolean hasPermission(Player player) {
        return player.isOp() || SkyWarsReloaded.perms.has(player, "swr.shop");
    }
    
    public boolean canPurchase(GamePlayer gamePlayer, ShopItem shopItem) {
    	boolean economy = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.useExternalEconomy");
    	if (economy) {
            return shopItem.getCost() > 0 && (SkyWarsReloaded.econ.getBalance(gamePlayer.getP()) >= shopItem.getCost());
    	} else {
    		return shopItem.getCost() > 0 && (gamePlayer.getBalance() >= shopItem.getCost());
    	}
    }

    public ShopItem getByName(String name) {
        return shopMap.get(name);
    }

    public void openShopMenu(final GamePlayer gamePlayer) {
        List<ShopItem> availableItems = Lists.newArrayList(shopMap.values());

        int rowCount = menuSlotsPerRow;
        while (rowCount < availableItems.size() && rowCount < menuSize) {
            rowCount += menuSlotsPerRow;
        }

        SkyWarsReloaded.getIC().create(gamePlayer.getP(), menuName, rowCount, new IconMenu.OptionClickEventHandler() {
			@Override
            public void onOptionClick(IconMenu.OptionClickEvent event) {
                if (gamePlayer.inGame() && (gamePlayer.getGame().getState() == GameState.PREGAME || gamePlayer.getGame().getState() == GameState.PLAYING)) {
                    
                	ShopItem shopItem = getByName(ChatColor.stripColor(event.getName()));
                    if (shopItem == null) {
                        return;
                    }
                    
                	if (!hasPermission(event.getPlayer())) {
                		event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("error.no-permission-shop"));
                        return;
                	} 
                	if (!canPurchase(gamePlayer, shopItem)) {
                		event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("error.not-enough-balance-shop"));
                		return;
                	}
                	
                	removeBalance(gamePlayer, shopItem.getCost());
      
                    ItemStack itemStack = shopItem.getItem();
                    event.getPlayer().getInventory().addItem(itemStack);
                    event.getPlayer().updateInventory();
                    return;
                    
                } else {
                	event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("error.shop-not-available"));
                	return;
                }
            }
        });

        for (int iii = 0; iii < availableItems.size(); iii ++) {
            if (iii >= menuSize) {
                break;
            }

            ShopItem shopItem = availableItems.get(iii);
            List<String> loreList = Lists.newLinkedList();

            loreList.add("Cost: " + shopItem.getCost());
            
            if (gamePlayer.getP() != null) {
                SkyWarsReloaded.getIC().setOption(
                        gamePlayer.getP(),
                        iii,
                        shopItem.getItem().clone(),
                        Items.itemByStack(shopItem.getItem()).getName(),
                        loreList.toArray(new String[loreList.size()]));
            }
         }
        if (gamePlayer.getP() != null) {
            SkyWarsReloaded.getIC().show(gamePlayer.getP());
        }
    }
    
    public boolean hasSpecPermission(Player player) {
        return player.isOp() || SkyWarsReloaded.perms.has(player, "swr.spectateshop");
    }
    
    public boolean canSpecPurchase(GamePlayer gamePlayer, ShopItem shopItem) {
    	boolean economy = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.useExternalEconomy");
    	if (economy) {
            return shopItem.getCost() > 0 && (SkyWarsReloaded.econ.getBalance(gamePlayer.getP()) >= shopItem.getCost());
    	} else {
    		return shopItem.getCost() > 0 && (gamePlayer.getBalance() >= shopItem.getCost());
    	}
    }

    public ShopItem getBySpecName(String name) {
        return specShopMap.get(name);
    }

    public void openSpecShopMenu(final GamePlayer gamePlayer) {
        List<ShopItem> availableItems = Lists.newArrayList(specShopMap.values());

        int rowCount = menuSlotsPerRow;
        while (rowCount < availableItems.size() && rowCount < menuSize) {
            rowCount += menuSlotsPerRow;
        }

        SkyWarsReloaded.getIC().create(gamePlayer.getP(), menuName2, rowCount, new IconMenu.OptionClickEventHandler() {
			@Override
            public void onOptionClick(IconMenu.OptionClickEvent event) {
                if (gamePlayer.isSpectating()) {
                    
                	ShopItem shopItem = getBySpecName(ChatColor.stripColor(event.getName()));
                    if (shopItem == null) {
                        return;
                    }
                    
                	if (!hasSpecPermission(event.getPlayer())) {
                		event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("error.no-permission-shop"));
                        return;
                	} 
                	if (!canSpecPurchase(gamePlayer, shopItem)) {
                		event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("error.not-enough-balance-shop"));
                		return;
                	}
                	
                	removeBalance(gamePlayer, shopItem.getCost());
      
                    ItemStack itemStack = shopItem.getItem();
                    Location loc = event.getPlayer().getLocation();
                    World world = event.getPlayer().getWorld();
                    world.dropItemNaturally(loc, itemStack);
                    return;
                    
                } else {
                	event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("error.shop-not-available"));
                	return;
                }
            }
        });

        for (int iii = 0; iii < availableItems.size(); iii ++) {
            if (iii >= menuSize) {
                break;
            }

            ShopItem shopItem = availableItems.get(iii);
            List<String> loreList = Lists.newLinkedList();

            loreList.add("Cost: " + shopItem.getCost());

            if (gamePlayer.getP() != null) {
                SkyWarsReloaded.getIC().setOption(
                        gamePlayer.getP(),
                        iii,
                        shopItem.getItem().clone(),
                        Items.itemByStack(shopItem.getItem()).getName(),
                        loreList.toArray(new String[loreList.size()]));
            }

        }
        if (gamePlayer.getP() != null) {
            SkyWarsReloaded.getIC().show(gamePlayer.getP());
        }

    }
    
    private class ShopItem {

        private ItemStack item;
        private int cost;

        public ShopItem(ItemStack item, int cost) {
            this.item = item;
            this.cost = cost;
        }

        public ItemStack getItem() {
            return item;
        }

        public int getCost() {
            return cost;
        }
    }

    private void removeBalance(GamePlayer p, int x) {
    	boolean economy = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.useExternalEconomy");
    	if (economy) {
    		SkyWarsReloaded.econ.withdrawPlayer(p.getP(), x);
    	} else {
    		p.setBalance(p.getBalance() - x);
    	}
    }
    
}
