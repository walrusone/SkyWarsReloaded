package com.walrusone.skywarsreloaded.menus;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;
import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.objects.GameMap;
import com.walrusone.skywarsreloaded.utilities.Messaging;

public class JoinMenu {

	private static final int menuSlotsPerRow = 9;
	private static final int menuSize = 81;
	private static final String joinName = new Messaging.MessageFormatter().format("menu.joingame-menu-title");
	
    public JoinMenu(final Player player) {
        List<GameMap> availableGames = GameMap.getMaps();

        int rowCount = menuSlotsPerRow;
        while (rowCount < availableGames.size() && rowCount < menuSize) {
            rowCount += menuSlotsPerRow;
        }

        SkyWarsReloaded.getIC().create(player, joinName, rowCount, new IconMenu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IconMenu.OptionClickEvent event) {
            	GameMap gMap = MatchManager.get().getPlayerMap(player);
                if (gMap != null) {
                    return;
                }

      
                gMap = GameMap.getMapByDisplayName(ChatColor.stripColor(event.getName()));
                if (gMap == null) {
                    return;
                }
                
                if (gMap.getMatchState() != MatchState.WAITINGSTART) {
                	return;
                }

                event.setWillClose(true);
                event.setWillDestroy(true);
                
                if (player.hasPermission("sw.join")) {
	                if (player!= null) {
	                	if (gMap.getMatchState() == MatchState.WAITINGSTART && gMap.canAddPlayer()) {
		                	gMap.addPlayer(player);
	                	}
	                }
                }
            }
        });

        updateMenu(player);
        
        GameMap gMap = MatchManager.get().getPlayerMap(player);
        if (player != null && gMap == null) {
	        SkyWarsReloaded.getIC().show(player);
        }

        SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
			@Override
			public void run() {
				updateJoinMenu(player);	
			}
        }, 40); 
    }
    
    public void updateJoinMenu(final Player player) {
    	GameMap gMap = MatchManager.get().getPlayerMap(player);
		if (!SkyWarsReloaded.getIC().has(player) || gMap != null || !SkyWarsReloaded.getIC().getMenu(player).getName().equalsIgnoreCase(joinName)) {
			return;
		} else {
			updateMenu(player);
	        SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
				@Override
				public void run() {
					updateJoinMenu(player);	
					GameMap gMap = MatchManager.get().getPlayerMap(player);
		            if (player != null && gMap == null) {
		     	       SkyWarsReloaded.getIC().update(player);
		            }
				}
	        }, 40); 
		}
    }
    
    private void updateMenu(Player player) {
    	 ArrayList<GameMap> games = GameMap.getMaps();
    	    
         for (int iii = 0; iii < games.size(); iii++) {
             if (iii >= menuSize) {
                 break;
             }

             GameMap gMap = games.get(iii);
             	            
             List<String> loreList = Lists.newLinkedList();
             if (gMap.getMatchState() == MatchState.WAITINGSTART) {
 	            loreList.add((new Messaging.MessageFormatter().format("signs.joinable").toUpperCase()));
             } else if (gMap.getMatchState().equals(MatchState.PLAYING) || gMap.getMatchState().equals(MatchState.SUDDENDEATH)) {
            	loreList.add((new Messaging.MessageFormatter().format("signs.playing").toUpperCase()));
             }  else if (gMap.getMatchState().equals(MatchState.ENDING)) {
             	loreList.add((new Messaging.MessageFormatter().format("signs.ending").toUpperCase()));
             }
             loreList.add((new Messaging.MessageFormatter().setVariable("playercount", "" + gMap.getAlivePlayers().size()).setVariable("maxplayers", "" + gMap.getMaxPlayers()).format("signs.line4")));
             for (Player p: gMap.getAllPlayers()) {
             	if (p != null) {
             		if (gMap.getAlivePlayers().contains(p)) {
             			loreList.add(ChatColor.GREEN + p.getName());
             		} else {
             			loreList.add(ChatColor.RED + p.getName());
             		}
             	}
             }
             
             double xy = ((double) (gMap.getAlivePlayers().size() / gMap.getMaxPlayers()));
             
             ItemStack gameIcon = SkyWarsReloaded.getNMS().getItemStack(Material.valueOf(SkyWarsReloaded.getCfg().getMaterial("blockwaiting")), new ArrayList<String>() ,"");
             if (gMap.getMatchState().equals(MatchState.PLAYING) || gMap.getMatchState().equals(MatchState.SUDDENDEATH)) {
            	 gameIcon = SkyWarsReloaded.getNMS().getItemStack(Material.valueOf(SkyWarsReloaded.getCfg().getMaterial("blockplaying")), new ArrayList<String>() ,"");
             } else if (gMap.getMatchState().equals(MatchState.ENDING)) {
            	 gameIcon = SkyWarsReloaded.getNMS().getItemStack(Material.valueOf(SkyWarsReloaded.getCfg().getMaterial("blockending")), new ArrayList<String>() ,"");
             } else if (gMap.getMatchState() == MatchState.WAITINGSTART) {
            	 gameIcon = SkyWarsReloaded.getNMS().getItemStack(Material.valueOf(SkyWarsReloaded.getCfg().getMaterial("almostfull")), new ArrayList<String>() ,"");
     	    	if (xy < 0.75) {
     	    		gameIcon = SkyWarsReloaded.getNMS().getItemStack(Material.valueOf(SkyWarsReloaded.getCfg().getMaterial("threefull")), new ArrayList<String>() ,"");
     	    	} 
     	    	if (xy < 0.50) {
     	    		gameIcon = SkyWarsReloaded.getNMS().getItemStack(Material.valueOf(SkyWarsReloaded.getCfg().getMaterial("halffull")), new ArrayList<String>() ,"");
     	    	} 
     	    	if (xy < 0.25) {
     	    		gameIcon = SkyWarsReloaded.getNMS().getItemStack(Material.valueOf(SkyWarsReloaded.getCfg().getMaterial("almostempty")), new ArrayList<String>() ,"");
     	    	} 
             }

             if (player != null) {
 	            SkyWarsReloaded.getIC().setOption(
 	            		player,
 	                    iii,
 	                    gameIcon,
 	                    ChatColor.translateAlternateColorCodes('&', gMap.getDisplayName()),
 	                    loreList.toArray(new String[loreList.size()]));
             }
         }
    }
    
}
