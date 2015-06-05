package com.walrusone.skywars.menus;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;
import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.utilities.IconMenu;
import com.walrusone.skywars.utilities.Messaging;
import com.walrusone.skywars.utilities.ShopItem;

public class SpecShopMenu {
    private static final int menuSlotsPerRow = 9;
    private static final int menuSize = 54;
    private static final String menuName2 = new Messaging.MessageFormatter().format("menu.spectateshop-menu-title");
    
    public SpecShopMenu(final GamePlayer gamePlayer) {
        List<ShopItem> availableItems = SkyWarsReloaded.getSC().getSpecShopItems();

        int rowCount = menuSlotsPerRow;
        while (rowCount < availableItems.size() && rowCount < menuSize) {
            rowCount += menuSlotsPerRow;
        }

        SkyWarsReloaded.getIC().create(gamePlayer.getP(), menuName2, rowCount, new IconMenu.OptionClickEventHandler() {
			@Override
            public void onOptionClick(IconMenu.OptionClickEvent event) {
                if (gamePlayer.isSpectating()) {
                    
                	ShopItem shopItem = SkyWarsReloaded.getSC().getBySpecName(ChatColor.stripColor(event.getName()));
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
            boolean canPurchase = false;
            
            if (SkyWarsReloaded.getCfg().usingExternalEcomony()) {
        		loreList.add("\247r\2476Price\247f: \247" + (SkyWarsReloaded.econ.getBalance(gamePlayer.getP()) >= shopItem.getCost() ? 'a' : 'c') + shopItem.getCost());
        	} else {
        		loreList.add("\247r\2476Price\247f: \247" + (gamePlayer.getBalance() >= shopItem.getCost() ? 'a' : 'c') + shopItem.getCost());
        	}
            loreList.add(" ");

            if (canSpecPurchase(gamePlayer, shopItem)) {
                canPurchase = true;
            }

            if (gamePlayer.getP() != null) {
                SkyWarsReloaded.getIC().setOption(
                        gamePlayer.getP(),
                        iii,
                        shopItem.getItem().clone(),
                        "\247r\247" + (canPurchase ? 'a' : 'c') + SkyWarsReloaded.getNMS().getName(shopItem.getItem()),
                        loreList.toArray(new String[loreList.size()]));
            }

        }
        if (gamePlayer.getP() != null) {
            SkyWarsReloaded.getIC().show(gamePlayer.getP());
        }

    }
    
    public boolean hasSpecPermission(Player player) {
        return player.isOp() || player.hasPermission("swr.spectateshop");
    }
    
    public boolean canSpecPurchase(GamePlayer gamePlayer, ShopItem shopItem) {
    	if (SkyWarsReloaded.getCfg().usingExternalEcomony()) {
            return shopItem.getCost() > 0 && (SkyWarsReloaded.econ.getBalance(gamePlayer.getP()) >= shopItem.getCost());
    	} else {
    		return shopItem.getCost() > 0 && (gamePlayer.getBalance() >= shopItem.getCost());
    	}
    }
    
    private void removeBalance(GamePlayer p, int x) {
    	if (SkyWarsReloaded.getCfg().usingExternalEcomony()) {
    		SkyWarsReloaded.econ.withdrawPlayer(p.getP(), x);
    	} else {
    		p.setBalance(p.getBalance() - x);
    	}
    }
}
