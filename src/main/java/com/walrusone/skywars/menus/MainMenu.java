package com.walrusone.skywars.menus;

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

    private static final int menuSlotsPerRow = 9;
    private static final int menuSize = 36;
    private static final String optionsMenuName = new Messaging.MessageFormatter().format("menu.options-menu-title");
	private ItemStack opChest;
	private ItemStack timeVote;
	private ItemStack weatherVote;
	private ItemStack jumpVote;
	private ItemStack color;
	private ItemStack particle;
	private ItemStack projEffect;
    
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
		String colorItem = SkyWarsReloaded.get().getConfig().getString("gameItems.glassMenuItem");
		List<String> colorItemData = new LinkedList<String>(Arrays.asList(colorItem.split(" ")));
		color = ItemUtils.parseItem(colorItemData);
		String particleItem = SkyWarsReloaded.get().getConfig().getString("gameItems.particleMenuItem");
		List<String> particleItemData = new LinkedList<String>(Arrays.asList(particleItem.split(" ")));
		particle = ItemUtils.parseItem(particleItemData);
		String projEffectItem = SkyWarsReloaded.get().getConfig().getString("gameItems.trailEffectMenuItem");
		List<String> projEffectItemData = new LinkedList<String>(Arrays.asList(projEffectItem.split(" ")));
		projEffect = ItemUtils.parseItem(projEffectItemData);
		String weatherItem = SkyWarsReloaded.get().getConfig().getString("gameItems.weatherMenuItem");
		List<String> weatherItemData = new LinkedList<String>(Arrays.asList(weatherItem.split(" ")));
		weatherVote = ItemUtils.parseItem(weatherItemData);
	
		int rowCount = menuSlotsPerRow;
        while (rowCount < 36 && rowCount < menuSize) {
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
                } else if (option.equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', new Messaging.MessageFormatter().format("menu.weather-item-name"))))) {
                	if (hasTimePermission(gamePlayer.getP())) {
                    	gamePlayer.getP().closeInventory();
                    	SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
    						@Override
    						public void run() {
    		                	new WeatherMenu(gamePlayer);
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
                } else if (option.equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', new Messaging.MessageFormatter().format("menu.selecteffect-item-name"))))) {
                	if (hasEffectPermission(gamePlayer.getP())) {
                    	gamePlayer.getP().closeInventory();
                    	SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
    						@Override
    						public void run() {
    		                	new UseEffectMenu(gamePlayer);
    						}
                    	}, 2);
                	}
                	 else {
                 		return;
                 	}
                } else if (option.equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', new Messaging.MessageFormatter().format("menu.selectprojeffect-item-name"))))) {
                	if (hasEffectPermission(gamePlayer.getP())) {
                    	gamePlayer.getP().closeInventory();
                    	SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
    						@Override
    						public void run() {
    		                	new UseProjEffectMenu(gamePlayer);
    						}
                    	}, 2);
                	}
                	 else {
                 		return;
                 	}
                } else if (option.equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', new Messaging.MessageFormatter().format("menu.selectcolor-item-name"))))) {
                	if (hasColorPermission(gamePlayer.getP())) {
                    	gamePlayer.getP().closeInventory();
                    	SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
    						@Override
    						public void run() {
    		                	new UseColorMenu(gamePlayer);
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
        List<String> weatherLore = Lists.newLinkedList();
        List<String> jumpLore = Lists.newLinkedList();
        List<String> colorLore = Lists.newLinkedList();
        List<String> effectLore = Lists.newLinkedList();
        List<String> trailLore = Lists.newLinkedList();
        
        if (hasTimePermission(gamePlayer.getP())) {
        } else {
        	timeLore.add(new Messaging.MessageFormatter().format("error.no-permission-time"));
        }
        
        if (hasJumpPermission(gamePlayer.getP())) {
        } else {
        	jumpLore.add(new Messaging.MessageFormatter().format("error.no-permission-jump"));
        }
        
        if (hasChestPermission(gamePlayer.getP())) {
        } else {
        	chestLore.add(new Messaging.MessageFormatter().format("error.no-permission-chest"));
        }
        
        if (hasColorPermission(gamePlayer.getP())) {
        } else {
        	colorLore.add(new Messaging.MessageFormatter().format("error.no-permission-color"));
        }
        
        if (hasEffectPermission(gamePlayer.getP())) {
        } else {
        	effectLore.add(new Messaging.MessageFormatter().format("error.no-permission-effect"));
        }
        
        if (hasProjEffectPermission(gamePlayer.getP())) {
        } else {
        	trailLore.add(new Messaging.MessageFormatter().format("error.no-permission-projeffect"));
        }
        
        if (hasWeatherPermission(gamePlayer.getP())) {
        } else {
        	weatherLore.add(new Messaging.MessageFormatter().format("error.no-permission-weather"));
        }

    	if (gamePlayer.getP() != null && gamePlayer.inGame() && gamePlayer.getGame().getState() == GameState.PREGAME) {
    		boolean opEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.opChestsEnabled");
    		if (opEnabled) {
                SkyWarsReloaded.getIC().setOption(
	                    gamePlayer.getP(),
	                    3,
	                    opChest.clone(),
	                    new Messaging.MessageFormatter().format("menu.chest-item-name"),
	                    chestLore.toArray(new String[chestLore.size()]));
    		}
    		
    		boolean timeEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.timeVoteEnabled");
    		if (timeEnabled) {
                SkyWarsReloaded.getIC().setOption(
	                    gamePlayer.getP(),
	                    5,
	                    timeVote.clone(),
	                    new Messaging.MessageFormatter().format("menu.time-item-name"),
	                    timeLore.toArray(new String[timeLore.size()]));
    		}
            
    		boolean jumpEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.jumpVoteEnabled");
    		if (jumpEnabled) {
    	           SkyWarsReloaded.getIC().setOption(
   	                    gamePlayer.getP(),
   	                    1,
   	                    jumpVote.clone(),
   	                    new Messaging.MessageFormatter().format("menu.jump-item-name"),
   	                    jumpLore.toArray(new String[jumpLore.size()]));
    		}
    		
    		boolean weatherEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.weatherVoteEnabled");
    		if (weatherEnabled) {
                SkyWarsReloaded.getIC().setOption(
	                    gamePlayer.getP(),
	                    7,
	                    weatherVote.clone(),
	                    new Messaging.MessageFormatter().format("menu.weather-item-name"),
	                    weatherLore.toArray(new String[weatherLore.size()]));
    		}
    		
    		boolean colorEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.colorGlassEnabled");
    		if (colorEnabled) {
    	           SkyWarsReloaded.getIC().setOption(
   	                    gamePlayer.getP(),
   	                    20,
   	                    color.clone(),
   	                    new Messaging.MessageFormatter().format("menu.selectcolor-item-name"),
   	                    colorLore.toArray(new String[colorLore.size()]));
    		}
    		
    		boolean particlesEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.particlesEnabled");
    		if (particlesEnabled) {
    	           SkyWarsReloaded.getIC().setOption(
   	                    gamePlayer.getP(),
   	                    22,
   	                    particle.clone(),
   	                    new Messaging.MessageFormatter().format("menu.selecteffect-item-name"),
   	                    effectLore.toArray(new String[effectLore.size()]));
    		}
    		
    		boolean trailsEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.trailEffectsEnabled");
    		if (trailsEnabled) {
    	           SkyWarsReloaded.getIC().setOption(
   	                    gamePlayer.getP(),
   	                    24,
   	                    projEffect.clone(),
   	                    new Messaging.MessageFormatter().format("menu.selectprojeffect-item-name"),
   	                    trailLore.toArray(new String[trailLore.size()]));
    		}
    		
            List<String> loreList5 = Lists.newLinkedList();
            SkyWarsReloaded.getIC().setOption(
    	                    gamePlayer.getP(),
    	                    35,
    	                    new ItemStack(Material.TORCH, 1),
    	                    new Messaging.MessageFormatter().format("menu.exit-menu"),
    	                    loreList5.toArray(new String[loreList5.size()])); 
            
    		
            SkyWarsReloaded.getIC().show(gamePlayer.getP());
    	}
    }
	
    public boolean hasTimePermission(Player player) {
        return player.isOp() || SkyWarsReloaded.perms.has(player, "swr.timevote");
    }
    
    public boolean hasWeatherPermission(Player player) {
        return player.isOp() || SkyWarsReloaded.perms.has(player, "swr.weathervote");
    }
    
    public boolean hasJumpPermission(Player player) {
        return player.isOp() || SkyWarsReloaded.perms.has(player, "swr.jumpvote");
    }
    
    public boolean hasChestPermission(Player player) {
        return player.isOp() || SkyWarsReloaded.perms.has(player, "swr.opchest");
    }
    
    public boolean hasColorPermission(Player player) {
        return player.isOp() || SkyWarsReloaded.perms.has(player, "swr.usecolor");
    }
    
    public boolean hasEffectPermission(Player player) {
        return player.isOp() || SkyWarsReloaded.perms.has(player, "swr.useeffect");
    }
	
    public boolean hasProjEffectPermission(Player player) {
        return player.isOp() || SkyWarsReloaded.perms.has(player, "swr.useprojeffect");
    }
}
