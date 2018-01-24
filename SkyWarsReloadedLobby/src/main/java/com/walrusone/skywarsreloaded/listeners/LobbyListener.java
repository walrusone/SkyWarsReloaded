package com.walrusone.skywarsreloaded.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.Listener;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.menus.ColorSelectionMenu;
import com.walrusone.skywarsreloaded.menus.EffectSelectionMenu;
import com.walrusone.skywarsreloaded.menus.KillSoundSelectionMenu;
import com.walrusone.skywarsreloaded.menus.ProjSelectionMenu;
import com.walrusone.skywarsreloaded.menus.WinSoundSelectionMenu;

public class LobbyListener implements Listener
{
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onClick(final PlayerInteractEvent a1) {
    		a1.setCancelled(true);
    		if (a1.getPlayer().hasPermission("sw.alterlobby")) {
    			a1.setCancelled(false);
    		}
        	if (a1.getAction() == Action.RIGHT_CLICK_AIR || a1.getAction() == Action.RIGHT_CLICK_BLOCK) {
        		if (a1.hasItem()) {
                    if (a1.getItem().getType() == Material.STAINED_GLASS) {
                    	new ColorSelectionMenu(a1.getPlayer());
                    } else if (a1.getItem().getType() == Material.BLAZE_POWDER) {
                    	new EffectSelectionMenu(a1.getPlayer());
                    } else if (a1.getItem().getType() == Material.ARROW) {
                    	new ProjSelectionMenu(a1.getPlayer());
                    } else if (a1.getItem().getType() == Material.DIAMOND_SWORD) {
                    	new KillSoundSelectionMenu(a1.getPlayer());
                    } else if (a1.getItem().getType() == Material.DRAGON_EGG) {
                    	new WinSoundSelectionMenu(a1.getPlayer());
                    }
        		}
        	}
        	return;
    }
    
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
    	Player player = (Player) event.getWhoClicked();
    	if (!player.hasPermission("sw.alterlobby") && !SkyWarsReloaded.getIC().has(player)) {
    		event.setCancelled(true);
    	}
    }
    
    @EventHandler
    public void onPlayerDropItem(final PlayerDropItemEvent event) {
    	if (!event.getPlayer().hasPermission("sw.alterlobby") && !SkyWarsReloaded.getIC().has(event.getPlayer())) {
    		event.setCancelled(true);
    	}
    }	
}
