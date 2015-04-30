package com.walrusone.skywars.controllers;

import com.google.common.collect.Lists;
import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.Game;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.utilities.IconMenu;
import com.walrusone.skywars.utilities.ItemUtils;
import com.walrusone.skywars.utilities.Messaging;

import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ChestController {

    private final List<ChestItem> chestItemList = Lists.newArrayList();
    private final List<ChestItem> opChestItemList = Lists.newArrayList();
    private final List<ChestItem> basicChestItemList = Lists.newArrayList();
    private final Random random = new Random();
    private static final int menuSlotsPerRow = 9;
    private static final int menuSize = 54;
    private static final String menuName = new Messaging.MessageFormatter().format("menu.chest-menu-title");
    private List<Integer> randomLoc = new ArrayList<Integer>();
    private String opVoteSound;

    public ChestController() {
        load();
        for (int i = 0; i < 27; i++) {
        	randomLoc.add(i);
        }
        opVoteSound = SkyWarsReloaded.get().getConfig().getString("gameSounds.opChestVote");
    }

    public void load() {
        chestItemList.clear();
        File chestFile = new File(SkyWarsReloaded.get().getDataFolder(), "chest.yml");

        if (!chestFile.exists()) {
        	SkyWarsReloaded.get().saveResource("chest.yml", false);
        }

        if (chestFile.exists()) {
            FileConfiguration storage = YamlConfiguration.loadConfiguration(chestFile);

            if (storage.contains("items")) {
                for (String item : storage.getStringList("items")) {
                    List<String> itemData = new LinkedList<String>(Arrays.asList(item.split(" ")));

                    int chance = Integer.parseInt(itemData.get(0));
                    itemData.remove(itemData.get(0));
                    
                    ItemStack itemStack = ItemUtils.parseItem(itemData);
                    
                    
                    if (itemStack != null) {
                        chestItemList.add(new ChestItem(itemStack, chance));
                    }
                }
            }
        }
        
        opChestItemList.clear();
        File opChestFile = new File(SkyWarsReloaded.get().getDataFolder(), "opchest.yml");

        if (!opChestFile.exists()) {
        	SkyWarsReloaded.get().saveResource("opchest.yml", false);
        }

        if (opChestFile.exists()) {
            FileConfiguration storage = YamlConfiguration.loadConfiguration(opChestFile);

            if (storage.contains("items")) {
                for (String item : storage.getStringList("items")) {
                    List<String> itemData = new LinkedList<String>(Arrays.asList(item.split(" ")));

                    int chance = Integer.parseInt(itemData.get(0));
                    itemData.remove(itemData.get(0));
                    
                    ItemStack itemStack = ItemUtils.parseItem(itemData);
                    
                    
                    if (itemStack != null) {
                        opChestItemList.add(new ChestItem(itemStack, chance));
                    }
                }
            }
        }
        
        basicChestItemList.clear();
        File basicChestFile = new File(SkyWarsReloaded.get().getDataFolder(), "basicchest.yml");

        if (!basicChestFile.exists()) {
        	SkyWarsReloaded.get().saveResource("basicchest.yml", false);
        }

        if (basicChestFile.exists()) {
            FileConfiguration storage = YamlConfiguration.loadConfiguration(basicChestFile);

            if (storage.contains("items")) {
                for (String item : storage.getStringList("items")) {
                    List<String> itemData = new LinkedList<String>(Arrays.asList(item.split(" ")));

                    int chance = Integer.parseInt(itemData.get(0));
                    itemData.remove(itemData.get(0));
                    
                    ItemStack itemStack = ItemUtils.parseItem(itemData);
                    
                    
                    if (itemStack != null) {
                        basicChestItemList.add(new ChestItem(itemStack, chance));
                    }
                }
            }
        }
        
    }

    public void populateChest(Chest chest, String chestfile) {
    	if (chestfile.equalsIgnoreCase("op")) {
    		Inventory inventory = chest.getBlockInventory();
    		inventory.clear();
            int added = 0;
            Collections.shuffle(randomLoc);

            for (ChestItem chestItem : opChestItemList) {
                if (random.nextInt(100) + 1 <= chestItem.getChance()) {
                    inventory.setItem(randomLoc.get(added), chestItem.getItem());
                    if (added++ >= inventory.getSize()-1) {
                        break;
                    }
                }
            }
    	} else if (chestfile.equalsIgnoreCase("basic")) {
    		Inventory inventory = chest.getBlockInventory();
    		inventory.clear();
            int added = 0;
            Collections.shuffle(randomLoc);

            for (ChestItem chestItem : basicChestItemList) {
                if (random.nextInt(100) + 1 <= chestItem.getChance()) {
                    inventory.setItem(randomLoc.get(added), chestItem.getItem());
                    if (added++ >= inventory.getSize()-1) {
                        break;
                    }
                }
            }
    	} else {
            Inventory inventory = chest.getBlockInventory();
    		inventory.clear();
            int added = 0;
            Collections.shuffle(randomLoc);

            for (ChestItem chestItem : chestItemList) {
                if (random.nextInt(100) + 1 <= chestItem.getChance()) {
                    inventory.setItem(randomLoc.get(added), chestItem.getItem());
                    if (added++ >= inventory.getSize()-1) {
                        break;
                    }
                }
            }
    	}
    }

    private class ChestItem {

        private ItemStack item;
        private int chance;

        public ChestItem(ItemStack item, int chance) {
            this.item = item;
            this.chance = chance;
        }

        public ItemStack getItem() {
            return item;
        }

        public int getChance() {
            return chance;
        }
    }
    
    public boolean hasChestPermission(Player player) {
        return player.isOp() || SkyWarsReloaded.perms.has(player, "swr.opchest");
    }
    
    public void openVotingMenu(final GamePlayer gamePlayer) {

        int rowCount = menuSlotsPerRow;
        while (rowCount < 8 && rowCount < menuSize) {
            rowCount += menuSlotsPerRow;
        }

        SkyWarsReloaded.getIC().create(gamePlayer.getP(), menuName, rowCount, new IconMenu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IconMenu.OptionClickEvent event) {    
            	String vote = event.getName();

                event.setWillClose(true);
                event.setWillDestroy(true);
                
            	if (!hasChestPermission(event.getPlayer())) {
            		event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("error.no-permission-chest"));
                    return;
            	} 
            	
                int lastVote = gamePlayer.getTimeVote();
                
                if (vote.equalsIgnoreCase("basic")) {
                	gamePlayer.setOpVote(1);
                } else if (vote.equalsIgnoreCase("normal")) {
                	gamePlayer.setOpVote(2);
                } else if (vote.equalsIgnoreCase("op")) {
                	gamePlayer.setOpVote(3);
                } 
                
                if (gamePlayer.getOpVote() != lastVote) {
                	Game game = gamePlayer.getGame();
                	for (GamePlayer gPlayer: game.getPlayers()) {
                		if (gPlayer.getP() != null) {
                    		gPlayer.getP().sendMessage(new Messaging.MessageFormatter()
        					.withPrefix()
        					.setVariable("player", gamePlayer.getName())
        					.setVariable("type", vote.toUpperCase())
        					.format("game.voted-for-chests"));
                		}
                	}
                	game.playSound(opVoteSound);
                } 
            }
        });

        Game game = gamePlayer.getGame();
        int votesForBasic = 0;
        int votesForNormal = 0;
        int votesForOP = 0;

    	for (GamePlayer gPlayer: game.getPlayers()) {
    		if (gPlayer.getOpVote() == 1) {
    			votesForBasic++;
    		} else if (gPlayer.getOpVote() == 2) {
    			votesForNormal++;
    		} else if (gPlayer.getOpVote() == 3) {
    			votesForOP++;
    		} 
    	}
        
    	if (gamePlayer.getP() != null) {
            List<String> loreList = Lists.newLinkedList();
            loreList.add(new Messaging.MessageFormatter().format("menu.chest-basic"));
            SkyWarsReloaded.getIC().setOption(
    	                    gamePlayer.getP(),
    	                    1,
    	                    new ItemStack(Material.CHEST, votesForBasic),
    	                    "BASIC",
    	                    loreList.toArray(new String[loreList.size()]));
            
            List<String> loreList3 = Lists.newLinkedList();
            loreList3.add(new Messaging.MessageFormatter().format("menu.chest-normal"));
            SkyWarsReloaded.getIC().setOption(
    	                    gamePlayer.getP(),
    	                    4,
    	                    new ItemStack(Material.CHEST, votesForNormal),
    	                    "NORMAL",
    	                    loreList3.toArray(new String[loreList3.size()]));
            
            List<String> loreList2 = Lists.newLinkedList();
            loreList2.add(new Messaging.MessageFormatter().format("menu.chest-op"));
            SkyWarsReloaded.getIC().setOption(
    	                    gamePlayer.getP(),
    	                    7,
    	                    new ItemStack(Material.CHEST, (votesForOP)),
    	                    "OP",
    	                    loreList2.toArray(new String[loreList2.size()]));

            SkyWarsReloaded.getIC().show(gamePlayer.getP());
    	}
    }
}