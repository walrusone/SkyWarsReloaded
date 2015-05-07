package com.walrusone.menus;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;
import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.game.Game.GameState;
import com.walrusone.skywars.utilities.IconMenu;
import com.walrusone.skywars.utilities.ItemUtils;
import com.walrusone.skywars.utilities.Messaging;

public class MainMenu {

    private static final int menuSlotsPerRow = 27;
    private static final int menuSize = 27;
    private static final String optionsMenuName = new Messaging.MessageFormatter().format("menu.options-menu-title");
	private ItemStack opChest;
	private ItemStack timeVote;
	private ItemStack jumpVote;
    
	public MainMenu(final GamePlayer gamePlayer) {

		String opVoteItem = SkyWarsReloaded.get().getConfig().getString("gameItems.opChestMenuItem");
		List<String> opVoteItemData = new LinkedList<String>(Arrays.asList(opVoteItem.split(" ")));
		opChest = ItemUtils.parseItem(opVoteItemData);
		String timeVoteItem = SkyWarsReloaded.get().getConfig().getString("gameItems.timeMenuItem");
		List<String> timeVoteItemData = new LinkedList<String>(Arrays.asList(timeVoteItem.split(" ")));
		timeVote = ItemUtils.parseItem(timeVoteItemData);
		String jumpVoteItem = SkyWarsReloaded.get().getConfig().getString("gameItems.jumpMenuItem");
		List<String> jumpVoteItemData = new LinkedList<String>(Arrays.asList(jumpVoteItem.split(" ")));
		jumpVote = ItemUtils.parseItem(jumpVoteItemData);
		
        int rowCount = menuSlotsPerRow;
        while (rowCount < 8 && rowCount < menuSize) {
            rowCount += menuSlotsPerRow;
        }

        SkyWarsReloaded.getIC().create(gamePlayer.getP(), optionsMenuName, rowCount, new IconMenu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IconMenu.OptionClickEvent event) {
            	if (!gamePlayer.inGame() || gamePlayer.getGame().getState() != GameState.PREGAME) {
            		return;
            	}
            	
                String option = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', event.getName()));

                event.setWillClose(false);
                event.setWillDestroy(false);
                
                if (option.equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', new Messaging.MessageFormatter().format("menu.time-item-name"))))) {
                	if (hasTimePermission(gamePlayer.getP())) {
                    	gamePlayer.getP().closeInventory();
                    	SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
    						@Override
    						public void run() {
    		                	new TimeMenu(gamePlayer);
    						}
                    	}, 2);
                	} else {
                		return;
                	}
                } else if (option.equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', new Messaging.MessageFormatter().format("menu.jump-item-name"))))) {
                	if (hasJumpPermission(gamePlayer.getP())) {
                    	gamePlayer.getP().closeInventory();
                    	SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
    						@Override
    						public void run() {
    		                	new JumpMenu(gamePlayer);
    						}
                    	}, 2);
                	}  else {
                		return;
                	}
                } else if (option.equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', new Messaging.MessageFormatter().format("menu.chest-item-name"))))) {
                	if (hasChestPermission(gamePlayer.getP())) {
                    	gamePlayer.getP().closeInventory();
                    	SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
    						@Override
    						public void run() {
    		                	new ChestMenu(gamePlayer);
    						}
                    	}, 2);
                	}
                	 else {
                 		return;
                 	}
                } else if (option.equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', new Messaging.MessageFormatter().format("menu.exit-menu"))))) {
                    	gamePlayer.getP().closeInventory();
                } else {
                	return;
                }
            }
        });

        List<String> chestLore = Lists.newLinkedList();
        List<String> timeLore = Lists.newLinkedList();
        List<String> jumpLore = Lists.newLinkedList();
        
        if (hasTimePermission(gamePlayer.getP())) {
        	timeLore.add("");
        } else {
        	timeLore.add(new Messaging.MessageFormatter().format("error.no-permission-time"));
        }
        
        if (hasJumpPermission(gamePlayer.getP())) {
        	jumpLore.add("");
        } else {
        	jumpLore.add(new Messaging.MessageFormatter().format("error.no-permission-jump"));
        }
        
        if (hasChestPermission(gamePlayer.getP())) {
        	chestLore.add("");
        } else {
        	chestLore.add(new Messaging.MessageFormatter().format("error.no-permission-chest"));
        }

    	if (gamePlayer.getP() != null && gamePlayer.inGame() && gamePlayer.getGame().getState() == GameState.PREGAME) {
    		boolean opEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.opChestsEnabled");
    		if (opEnabled) {
                SkyWarsReloaded.getIC().setOption(
	                    gamePlayer.getP(),
	                    4,
	                    opChest,
	                    new Messaging.MessageFormatter().format("menu.chest-item-name"),
	                    chestLore.toArray(new String[chestLore.size()]));
    		}
    		
    		boolean timeEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.timeVoteEnabled");
    		if (timeEnabled) {
                SkyWarsReloaded.getIC().setOption(
	                    gamePlayer.getP(),
	                    6,
	                    timeVote,
	                    new Messaging.MessageFormatter().format("menu.time-item-name"),
	                    timeLore.toArray(new String[timeLore.size()]));
    		}
            
    		boolean jumpEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.jumpVoteEnabled");
    		if (jumpEnabled) {
    	           SkyWarsReloaded.getIC().setOption(
   	                    gamePlayer.getP(),
   	                    2,
   	                    jumpVote,
   	                    new Messaging.MessageFormatter().format("menu.jump-item-name"),
   	                    jumpLore.toArray(new String[jumpLore.size()]));
    		}
    		
            List<String> loreList5 = Lists.newLinkedList();
            SkyWarsReloaded.getIC().setOption(
    	                    gamePlayer.getP(),
    	                    26,
    	                    new ItemStack(Material.TORCH, 1),
    	                    new Messaging.MessageFormatter().format("menu.exit-menu"),
    	                    loreList5.toArray(new String[loreList5.size()])); 
            
    		
            SkyWarsReloaded.getIC().show(gamePlayer.getP());
    	}
    }
	
    public boolean hasTimePermission(Player player) {
        return player.isOp() || SkyWarsReloaded.perms.has(player, "swr.timevote");
    }
    
    public boolean hasJumpPermission(Player player) {
        return player.isOp() || SkyWarsReloaded.perms.has(player, "swr.jumpvote");
    }
    
    public boolean hasChestPermission(Player player) {
        return player.isOp() || SkyWarsReloaded.perms.has(player, "swr.opchest");
    }
	
}
