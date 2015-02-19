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
    private static final String menuName = "Shop Menu";

    private final Map<String, ShopItem> shopMap = Maps.newHashMap();

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
                        shopMap.put(Items.itemByStack(itemStack).getName().toString(), new ShopItem(itemStack, cost));
                    }
                }
            }
        }
    }
    
    public boolean hasPermission(Player player) {
        return player.isOp() || SkyWarsReloaded.perms.has(player, "swr.shop");
    }
    
    public boolean canPurchase(Player player, ShopItem shopItem) {
        return shopItem.getCost() > 0 && (SkyWarsReloaded.econ.getBalance(player) >= shopItem.getCost());
    }

    public ShopItem getByName(String name) {
        return shopMap.get(name);
    }

    public void openKitMenu(final GamePlayer gamePlayer) {
        List<ShopItem> availableItems = Lists.newArrayList(shopMap.values());

        int rowCount = menuSlotsPerRow;
        while (rowCount < availableItems.size() && rowCount < menuSize) {
            rowCount += menuSlotsPerRow;
        }

        SkyWarsReloaded.getIC().create(gamePlayer.getP(), menuName, rowCount, new IconMenu.OptionClickEventHandler() {
            @SuppressWarnings("deprecation")
			@Override
            public void onOptionClick(IconMenu.OptionClickEvent event) {
                if (gamePlayer.getGame() != null && (gamePlayer.getGame().getState() == GameState.PREGAME || gamePlayer.getGame().getState() == GameState.PLAYING)) {
                    
                	ShopItem shopItem = getByName(ChatColor.stripColor(event.getName()));
                    if (shopItem == null) {
                        return;
                    }
                    
                	if (!hasPermission(event.getPlayer())) {
                		event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("error.no-permission-shop"));
                        return;
                	} 
                	if (!canPurchase(event.getPlayer(), shopItem)) {
                		event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("error.not-enough-balance-shop"));
                		return;
                	}
                	
                    SkyWarsReloaded.econ.withdrawPlayer(gamePlayer.getP(), shopItem.getCost());
                    
      
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

            SkyWarsReloaded.getIC().setOption(
                    gamePlayer.getP(),
                    iii,
                    shopItem.getItem().clone(),
                    Items.itemByStack(shopItem.getItem()).getName(),
                    loreList.toArray(new String[loreList.size()]));
        }

        SkyWarsReloaded.getIC().show(gamePlayer.getP());
    }
    
    public class ShopItem {

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
}
