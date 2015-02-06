package com.walrusone.skywars.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.Game;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.game.Game.GameState;

public class SignListener implements Listener {

    @EventHandler
    public void signPlaced(SignChangeEvent event) {
        String[] lines = event.getLines();
        if (lines[0].equalsIgnoreCase("[swr]")) {
        	if (SkyWarsReloaded.perms.has(event.getPlayer(), "swr.signs")) {
                Location signLocation = event.getBlock().getLocation();
                World w = signLocation.getWorld();
            	Block b = w.getBlockAt(signLocation);
            	if(b.getType() == Material.WALL_SIGN || b.getType() == Material.SIGN_POST){
            		if(lines[1].equalsIgnoreCase("join")) {
            			event.setCancelled(true);
            			Sign sign = (Sign) b.getState();
            			sign.setLine(0, ChatColor.RED + "" + ChatColor.MAGIC + "bounce1234567");
            			sign.setLine(1, ChatColor.GREEN + "SkyWars");
            			sign.setLine(2, ChatColor.BLUE + "Join");
            			sign.setLine(3, ChatColor.RED + "" + ChatColor.MAGIC + "ljsdaklfjlsdjfljsdlfsdlk");
            			sign.update();
            		}
            	}  
            } else {
            		event.getPlayer().sendMessage(ChatColor.RED + "YOU DO NOT HAVE PERMISSION TO CREATE SWR SIGNS");
        			event.setCancelled(true);
        } 
       }
    }
    
    @EventHandler
    public void signRemoved(BlockBreakEvent event) {
        Location blockLocation = event.getBlock().getLocation();
        World w = blockLocation.getWorld();
    	Block b = w.getBlockAt(blockLocation);
		if(b.getType() == Material.WALL_SIGN || b.getType() == Material.SIGN_POST){
	    	Sign sign = (Sign) b.getState();
	    	String line1 = ChatColor.stripColor(sign.getLine(0));
			String line3 = ChatColor.stripColor(sign.getLine(2));
			 if  (line1.equalsIgnoreCase("bounce1234567") && line3.equalsIgnoreCase("join") && !(SkyWarsReloaded.perms.has(event.getPlayer(), "swr.signs"))) {
         		event.getPlayer().sendMessage(ChatColor.RED + "YOU DO NOT HAVE PERMISSION TO DESTROY SWR SIGNS");
     			event.setCancelled(true);
             }
		}
    }
    
    @EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(player);
    	if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
    		 if (e.getClickedBlock().getType() == Material.WALL_SIGN || e.getClickedBlock().getType() == Material.SIGN_POST ) {
    			 if (gPlayer.getGame() != null) {
    				 Game game = gPlayer.getGame();
    				 if (game.getState() == GameState.INLOBBY && !game.votingStatus()) {
     					Sign s = (Sign) e.getClickedBlock().getState();
     					String mapName = s.getLine(0);
     					gPlayer.setVoted(mapName);
     					game.updateSigns();
     				}
    			 } else {
    				 Sign s = (Sign) e.getClickedBlock().getState();
    				 String line1 = ChatColor.stripColor(s.getLine(0));
    				 String line3 = ChatColor.stripColor(s.getLine(2));
    				 if  (line1.equalsIgnoreCase("bounce1234567") && line3.equalsIgnoreCase("join") && (SkyWarsReloaded.perms.has(e.getPlayer(), "swr.play"))) {
    					 Game game = SkyWarsReloaded.getGC().findGame(); 
    					 game.addPlayer(gPlayer);
    				 }
    			 }
    		 }
    	}
	}
    
}
