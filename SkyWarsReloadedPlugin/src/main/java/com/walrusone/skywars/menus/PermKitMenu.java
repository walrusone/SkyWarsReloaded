package com.walrusone.skywars.menus;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;
import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.GameKit;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.utilities.IconMenu;
import com.walrusone.skywars.utilities.Messaging;

public class PermKitMenu {

    private static final int menuSlotsPerRow = 9;
    private static final int menuSize = 54;
    private static final String menuName = new Messaging.MessageFormatter().format("menu.permkit-menu-title");
    private static final String premissionPrefix = "swr.kit.";
    
    public PermKitMenu(final GamePlayer gamePlayer) {
        List<GameKit> availableKits = SkyWarsReloaded.getKC().getKits();
        
        int highestSlot = 0;
        for (GameKit kit: availableKits) {
        	if (kit.getPosition() > highestSlot) {
        		highestSlot = kit.getPosition();
        	}
        }

        int rowCount = menuSlotsPerRow;
        while (rowCount < 54 && rowCount < menuSize) {
            rowCount += menuSlotsPerRow;
        }

        SkyWarsReloaded.getIC().create(gamePlayer.getP(), menuName, rowCount, new IconMenu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IconMenu.OptionClickEvent event) {
                if (gamePlayer.inGame()) {
                	gamePlayer.getP().closeInventory();
                    return;
                }

                String name = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', event.getName()));
                if (name.equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', new Messaging.MessageFormatter().format("menu.return-to-lobbymenu"))))) {
                	gamePlayer.getP().closeInventory();
                	if (!gamePlayer.inGame()) {
                    	SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
    						@Override
    						public void run() {
    		                	new LobbyMainMenu(gamePlayer);
    						}
                    	}, 2);
                	}
                	return;
                }
                
                GameKit kit = SkyWarsReloaded.getKC().getByName(ChatColor.stripColor(event.getName()));
                if (kit == null) {
                	gamePlayer.getP().closeInventory();
                    return;
                }

                if (!hasPermission(event.getPlayer(), kit) && !hasFreePermission(gamePlayer, kit)) {
                	event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("error.no-permission-kit"));
                	return;
                }

                event.setWillClose(false);
                event.setWillDestroy(false);
                
                if (!hasFreePermission(gamePlayer, kit)) {
                	if (isPurchaseAble(kit)) {
                		if (!canPurchase(gamePlayer, kit)) {
                    		event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("error.not-enough-balance"));
                            return;
                		} else {
                			removeBalance(gamePlayer, kit.getPermCost());
                			gamePlayer.addPerm(premissionPrefix + "free." + kit.getName().toLowerCase());
                        	if (!gamePlayer.inGame()) {
                            	SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
            						@Override
            						public void run() {
            	                    	updatePermKitMenu(gamePlayer);
            						}
                            	}, 2);
                        	}
                		} 
                	}
                } else {
                	return;
                }
            }
        });

        for (int iii = 0; iii < availableKits.size(); iii ++) {
            if (iii >= menuSize) {
                break;
            }

            GameKit kit = availableKits.get(iii);
            List<String> loreList = Lists.newLinkedList();
            boolean canPurchase = false;

            if (!gamePlayer.inGame()) {
                if (!hasFreePermission(gamePlayer, kit)) {
                    if (!hasPermission(gamePlayer.getP(), kit)) {
                        loreList.add(new Messaging.MessageFormatter().format("menu.no-permission"));
                        loreList.add(" ");
                    } else {
                        if (isPurchaseAble(kit)) {
                        	if (SkyWarsReloaded.getCfg().usingExternalEcomony()) {
                        		loreList.add("\247r\2476Price\247f: \247" + (SkyWarsReloaded.econ.getBalance(gamePlayer.getP()) >= kit.getPermCost() ? 'a' : 'c') + kit.getPermCost());
                        	} else {
                        		loreList.add("\247r\2476Price\247f: \247" + (gamePlayer.getBalance() >= kit.getPermCost() ? 'a' : 'c') + kit.getPermCost());
                        	}
                            loreList.add(" ");

                            if (canPurchase(gamePlayer, kit)) {
                                canPurchase = true;
                            }

                        }
                    }
                } else {
                    loreList.add(new Messaging.MessageFormatter().format("menu.kit-purchased"));
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
        }
        
        List<String> loreList5 = Lists.newLinkedList();
        SkyWarsReloaded.getIC().setOption(
	                    gamePlayer.getP(),
	                    53,
	                    new ItemStack(Material.TORCH, 1),
	                    new Messaging.MessageFormatter().format("menu.return-to-lobbymenu"),
	                    loreList5.toArray(new String[loreList5.size()])); 
        
        if (!gamePlayer.inGame()) {
            SkyWarsReloaded.getIC().show(gamePlayer.getP());
        }
    }
    
    
    public void updatePermKitMenu(final GamePlayer gamePlayer) {
		if (!SkyWarsReloaded.getIC().has(gamePlayer.getP()) || gamePlayer.inGame() || !SkyWarsReloaded.getIC().getMenu(gamePlayer.getP()).getName().equalsIgnoreCase(menuName)) {
			gamePlayer.getP().closeInventory();
			return;
		} else {
			List<GameKit> availableKits = SkyWarsReloaded.getKC().getKits();
	        for (int iii = 0; iii < availableKits.size(); iii ++) {
	            if (iii >= menuSize) {
	                break;
	            }

	            GameKit kit = availableKits.get(iii);
	            List<String> loreList = Lists.newLinkedList();
	            boolean canPurchase = false;

	            if (!gamePlayer.inGame()) {
	                if (!hasFreePermission(gamePlayer, kit)) {
	                    if (!hasPermission(gamePlayer.getP(), kit)) {
	                        loreList.add(new Messaging.MessageFormatter().format("menu.no-permission"));
	                        loreList.add(" ");
	                    } else {
	                        if (isPurchaseAble(kit)) {
	                        	if (SkyWarsReloaded.getCfg().usingExternalEcomony()) {
	                        		loreList.add("\247r\2476Price\247f: \247" + (SkyWarsReloaded.econ.getBalance(gamePlayer.getP()) >= kit.getPermCost() ? 'a' : 'c') + kit.getPermCost());
	                        	} else {
	                        		loreList.add("\247r\2476Price\247f: \247" + (gamePlayer.getBalance() >= kit.getPermCost() ? 'a' : 'c') + kit.getPermCost());
	                        	}
	                            loreList.add(" ");

	                            if (canPurchase(gamePlayer, kit)) {
	                                canPurchase = true;
	                            }

	                        }
	                    }
	                } else {
	                    loreList.add(new Messaging.MessageFormatter().format("menu.kit-purchased"));
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
	            
	            List<String> loreList5 = Lists.newLinkedList();
	            SkyWarsReloaded.getIC().setOption(
	    	                    gamePlayer.getP(),
	    	                    53,
	    	                    new ItemStack(Material.TORCH, 1),
	    	                    new Messaging.MessageFormatter().format("menu.return-to-lobbymenu"),
	    	                    loreList5.toArray(new String[loreList5.size()]));  
	        }
	        
	        if (gamePlayer.getP() != null) {
		        SkyWarsReloaded.getIC().update(gamePlayer.getP());
	        }
		}
    }
    
    
    public boolean hasPermission(Player player, GameKit kit) {
        return player.isOp() || player.hasPermission(premissionPrefix + kit.getName().toLowerCase());
    }
    
    public boolean hasFreePermission(GamePlayer player, GameKit kit) {
        return (player.getP().isOp() || player.getP().hasPermission(premissionPrefix + "free." + kit.getName().toLowerCase()) || player.hasPerm(premissionPrefix + "free." + kit.getName().toLowerCase()));
    }
    
    public boolean isPurchaseAble(GameKit kit) {
        if (kit.getPermCost() > 0) {
        	return true;
        }
        return false;
    }

    public boolean canPurchase(GamePlayer gamePlayer, GameKit kit) {
    	if (SkyWarsReloaded.getCfg().usingExternalEcomony()) {
            return (SkyWarsReloaded.econ.getBalance(gamePlayer.getP()) >= kit.getPermCost());
    	} else {
    		return (gamePlayer.getBalance() >= kit.getPermCost());
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
