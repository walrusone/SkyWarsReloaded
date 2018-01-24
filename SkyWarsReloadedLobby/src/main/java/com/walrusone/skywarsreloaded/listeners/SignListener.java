package com.walrusone.skywarsreloaded.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.objects.GameSign;
import com.walrusone.skywarsreloaded.objects.SWRServer;
import com.walrusone.skywarsreloaded.utilities.Messaging;

public class SignListener implements Listener {

    @EventHandler
    public void signPlaced(SignChangeEvent event) {
        String[] lines = event.getLines();
        if (lines[0].equalsIgnoreCase("[sw]") && lines.length >= 2) {
        	if (event.getPlayer().hasPermission("sw.signs")) {
        			Location signLocation = event.getBlock().getLocation();
                    World w = signLocation.getWorld();
                	Block b = w.getBlockAt(signLocation);
                	if(b.getType() == Material.WALL_SIGN || b.getType() == Material.SIGN_POST) {
               			event.setCancelled(true);
               			String serverName = lines[1];
               			SWRServer server = SWRServer.getServer(serverName);
               			if (server != null) {
               				GameSign.addSign(signLocation, server);
                       		event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("signs.added"));
                       	} else {
                       		event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("signs.no-map"));
                       	}
                	}
            	} else {
            		event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("error.signs-no-perm"));
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
	    	Location loc = sign.getLocation();
	    	if (GameSign.hasGameSign(loc)) {
	    		GameSign.removeSign(loc);
	    		event.getPlayer().sendMessage(new Messaging.MessageFormatter().format("signs.remove"));
	    	}
		}
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player player = e.getPlayer();
    	if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
    		 if (e.getClickedBlock().getType() == Material.WALL_SIGN || e.getClickedBlock().getType() == Material.SIGN_POST ) {
    				Sign s = (Sign) e.getClickedBlock().getState();
    			    Location loc = s.getLocation();
    			    if (GameSign.hasGameSign(loc)) {
    			    	e.setCancelled(false);
    			    	SWRServer server = GameSign.getSign(loc);
    			    	if (server.isInitialized() && server.isOnline() && server.getState().equalsIgnoreCase("WAITINGSTART") && server.getPlayerCount() < server.getMaxPlayers()) {
    			    		server.setPlayerCount(server.getPlayerCount() + 1);
        			    	GameSign.updateSigns();
    			    		SkyWarsReloaded.get().sendBungeeMsg(player, "Connect", server.getServerName());
    					}
    			    }
    		 }
    	}
	}
    
}
