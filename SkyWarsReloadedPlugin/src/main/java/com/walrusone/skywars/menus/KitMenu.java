package com.walrusone.skywars.menus;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;
import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.GameKit;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.game.Game.GameState;
import com.walrusone.skywars.utilities.IconMenu;
import com.walrusone.skywars.utilities.Messaging;

public class KitMenu {

    private static final int menuSlotsPerRow = 9;
    private static final int menuSize = 54;
    private static final String menuName = new Messaging.MessageFormatter().format("menu.kit-menu-title");
    private static final String premissionPrefix = "swr.kit.";
    
    public KitMenu(final GamePlayer gamePlayer) {
        List<GameKit> availableKits = SkyWarsReloaded.getKC().getKits();
        
        int highestSlot = 0;
        for (GameKit kit: availableKits) {
        	if (kit.getPosition() > highestSlot) {
        		highestSlot = kit.getPosition();
        	}
        }

        int rowCount = menuSlotsPerRow;
        while (rowCount < (highestSlot + 1) && rowCount < menuSize) {
            rowCount += menuSlotsPerRow;
        }

        SkyWarsReloaded.getIC().create(gamePlayer.getP(), menuName, rowCount, new IconMenu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IconMenu.OptionClickEvent event) {
                if (!gamePlayer.inGame()) {
                	event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("error.can-not-pick-kit"));
                    return;
                }

                if (gamePlayer.getGame().getState() != GameState.PREGAME) {
                	event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("error.can-not-pick-kit"));
                    return;
                }

                GameKit kit = SkyWarsReloaded.getKC().getByName(ChatColor.stripColor(event.getName()));
                if (kit == null) {
                    return;
                }

                if (!hasPermission(event.getPlayer(), kit) && !hasFreePermission(gamePlayer, kit)) {
                	event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("error.no-permission-kit"));
                	return;
                }

                if (!hasFreePermission(gamePlayer, kit)) {
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

            if (!hasFreePermission(gamePlayer, kit)) {
                if (!hasPermission(gamePlayer.getP(), kit)) {
                    loreList.add(new Messaging.MessageFormatter().format("menu.no-permission"));
                    loreList.add(" ");
                } else {
                    if (isPurchaseAble(kit)) {
                    	if (SkyWarsReloaded.getCfg().usingExternalEcomony()) {
                    		loreList.add("\247r" + new Messaging.MessageFormatter().format("menu.kit-price") + "\247f: \247" + (SkyWarsReloaded.econ.getBalance(gamePlayer.getP()) >= kit.getCost() ? 'a' : 'c') + kit.getCost());
                    	} else {
                    		loreList.add("\247r" + new Messaging.MessageFormatter().format("menu.kit-price") + "\247f: \247" + (gamePlayer.getBalance() >= kit.getCost() ? 'a' : 'c') + kit.getCost());
                    	}
                        loreList.add(" ");

                        if (canPurchase(gamePlayer, kit)) {
                            canPurchase = true;
                        }

                    }
                }
            } else {
                loreList.add(new Messaging.MessageFormatter().format("menu.kit-free"));
                loreList.add(" ");
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
    
    public boolean hasPermission(Player player, GameKit kit) {
        return player.isOp() || player.hasPermission(premissionPrefix + kit.getName().toLowerCase());
    }
    
    public boolean hasFreePermission(GamePlayer player, GameKit kit) {
        return (player.getP().isOp() || player.getP().hasPermission(premissionPrefix + "free." + kit.getName().toLowerCase()) || player.hasPerm(premissionPrefix + "free." + kit.getName().toLowerCase()));
    }
    
    public boolean isPurchaseAble(GameKit kit) {
        if (kit.getCost() > 0) {
        	return true;
        }
        return false;
    }

    public boolean canPurchase(GamePlayer gamePlayer, GameKit kit) {
    	if (SkyWarsReloaded.getCfg().usingExternalEcomony()) {
            return (SkyWarsReloaded.econ.getBalance(gamePlayer.getP()) >= kit.getCost());
    	} else {
    		return (gamePlayer.getBalance() >= kit.getCost());
    	}
    }
    
    private void removeBalance(GamePlayer p, int x) {
    	if (SkyWarsReloaded.getCfg().usingExternalEcomony()) {
    		SkyWarsReloaded.econ.withdrawPlayer(SkyWarsReloaded.get().getServer().getOfflinePlayer(p.getUUID()), x);
    	} else {
    		p.setBalance(p.getBalance() - x);
    	}
    }
}
