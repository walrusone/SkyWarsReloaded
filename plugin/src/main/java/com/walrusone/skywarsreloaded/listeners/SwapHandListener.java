package com.walrusone.skywarsreloaded.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.managers.MatchManager;

public class SwapHandListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerSwapHand(PlayerSwapHandItemsEvent	event) {
		GameMap gMap = MatchManager.get().getPlayerMap(event.getPlayer());
    	if (gMap == null) {
    		ItemStack item = event.getOffHandItem();
    		if (item.equals(SkyWarsReloaded.getIM().getItem("optionselect")) 
        			|| item.equals(SkyWarsReloaded.getIM().getItem("joinselect")) 
        			|| item.equals(SkyWarsReloaded.getIM().getItem("spectateselect"))) {
        		event.setCancelled(true);
  			}
    	} else {
    		if (gMap.getMatchState().equals(MatchState.WAITINGSTART) || gMap.getMatchState().equals(MatchState.ENDING)) {
    			event.setCancelled(true);
    		}
    	}
	}

}
