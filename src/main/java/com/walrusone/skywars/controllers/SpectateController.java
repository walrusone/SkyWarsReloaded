package com.walrusone.skywars.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.Game;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.utilities.IconMenu;
import com.walrusone.skywars.utilities.Messaging;

public class SpectateController {

private static final int menuSlotsPerRow = 9;
private static final int menuSize = 54;
private static final String menuName = new Messaging.MessageFormatter().format("menu.spectate-menu-title");
private static final Map<String, UUID> players = new HashMap<String, UUID>();


	    public void openPlayerMenu(final GamePlayer gamePlayer) {
	        List<Player> availablePlayers = new ArrayList<Player>();
	        Game game;
	        
	        if (gamePlayer.getSpecGame() == null) {
	        	game = gamePlayer.getGame();
	        } else {
	        	game = gamePlayer.getSpecGame();
	        }
	        
	        for (GamePlayer gPlayer: game.getPlayers()) {
	        	if (gPlayer.getP() != null) {
	        		availablePlayers.add(gPlayer.getP());
	        	}
	        }

	        int rowCount = menuSlotsPerRow;
	        while (rowCount < availablePlayers.size() && rowCount < menuSize) {
	            rowCount += menuSlotsPerRow;
	        }

	        SkyWarsReloaded.getIC().create(gamePlayer.getP(), menuName, rowCount, new IconMenu.OptionClickEventHandler() {
	            @Override
	            public void onOptionClick(IconMenu.OptionClickEvent event) {
	            	String name = ChatColor.stripColor(event.getName());
	                Player player = SkyWarsReloaded.get().getServer().getPlayer(players.get(name));
	                if (player == null) {
	                    return;
	                }
	                
	                if (!SkyWarsReloaded.perms.has(gamePlayer.getP(), "swr.spectate")) {
	                    event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("error.no-spectate-perm"));
	                    return;
	                }

	                event.setWillClose(true);
	                event.setWillDestroy(true);
	                
	                if (player != null) {
		                if (SkyWarsReloaded.getPC().getPlayer(player.getUniqueId()).inGame()) {
			                gamePlayer.getP().teleport(player.getLocation());
		                } else {
		                	gamePlayer.getP().sendMessage(new Messaging.MessageFormatter().format("error.player-not-playing"));
		                }
	                }
	                SkyWarsReloaded.getIC().destroy(gamePlayer.getP());

	            }
	        });

	        for (int iii = 0; iii < availablePlayers.size(); iii ++) {
	            if (iii >= menuSize) {
	                break;
	            }

	            Player player = availablePlayers.get(iii);
	            if (player != null) {
		            players.put(player.getName(), player.getUniqueId());
		            ItemStack playerhead = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		    		SkullMeta meta = (SkullMeta)playerhead.getItemMeta();
		    		meta.setOwner(player.getName());
		    		playerhead.setItemMeta(meta);
		            
		    		SkyWarsReloaded.getIC().setOption(
		            		gamePlayer.getP(),
			                iii,
			                playerhead,
			                ChatColor.AQUA + String.valueOf(player.getName()),
			                "");
		            }
	            }
	        if (gamePlayer.getP() != null) {
		        SkyWarsReloaded.getIC().show(gamePlayer.getP());
	        }
	    }
 
}
