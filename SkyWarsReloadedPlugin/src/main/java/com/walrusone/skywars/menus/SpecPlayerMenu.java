package com.walrusone.skywars.menus;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.Game;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.utilities.IconMenu;
import com.walrusone.skywars.utilities.Messaging;

public class SpecPlayerMenu {

	private static final int menuSlotsPerRow = 9;
	private static final int menuSize = 54;
	private static final String menuName = new Messaging.MessageFormatter().format("menu.spectate-menu-title");

	public SpecPlayerMenu(final GamePlayer gamePlayer) {
        List<Player> availablePlayers = new ArrayList<Player>();
        Game game;
        
    	game = gamePlayer.getSpecGame();
        
        for (GamePlayer gPlayer: game.getPlayers()) {
        	if (gPlayer.getP() != null) {
        		availablePlayers.add(gPlayer.getP());
        	}
        }

        int rowCount = menuSlotsPerRow;
        while (rowCount < game.getNumberOfSpawns() && rowCount < menuSize) {
            rowCount += menuSlotsPerRow;
        }

        SkyWarsReloaded.getIC().create(gamePlayer.getP(), menuName, rowCount, new IconMenu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IconMenu.OptionClickEvent event) {
            	String name = ChatColor.stripColor(event.getName());
                @SuppressWarnings("deprecation")
				Player player = SkyWarsReloaded.get().getServer().getPlayer(name);
                if (player == null) {
                    return;
                }
                
                event.setWillClose(true);
                event.setWillDestroy(true);
                
                if (player != null) {
	                if (SkyWarsReloaded.getPC().getPlayer(player.getUniqueId()).inGame()) {
	                	if (gamePlayer.getP().getWorld().getName().equalsIgnoreCase(player.getWorld().getName())) {
			                gamePlayer.getP().teleport(player.getLocation(), TeleportCause.PLUGIN);
	                	} else {
		                	gamePlayer.getP().sendMessage(new Messaging.MessageFormatter().format("error.player-not-playing"));
	                	}
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
