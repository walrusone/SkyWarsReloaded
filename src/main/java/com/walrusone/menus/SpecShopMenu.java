package com.walrusone.menus;

import java.util.List;

import net.milkbowl.vault.item.Items;

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
    
    private void removeBalance(GamePlayer p, int x) {
    	boolean economy = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.useExternalEconomy");
    	if (economy) {
    		SkyWarsReloaded.econ.withdrawPlayer(p.getP(), x);
    	} else {
    		p.setBalance(p.getBalance() - x);
    	}
    }
}
