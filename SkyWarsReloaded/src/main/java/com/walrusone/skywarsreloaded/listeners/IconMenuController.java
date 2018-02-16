package com.walrusone.skywarsreloaded.listeners;

import com.google.common.collect.Maps;
import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.menus.IconMenu;
import com.walrusone.skywarsreloaded.menus.IconMenu.OptionClickEventHandler;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Map;

public class IconMenuController implements Listener {

    private final Map<Player, IconMenu> menu = Maps.newHashMap();

    public IconMenuController() {
    }

    public void create(Player player, Inventory inv, OptionClickEventHandler optionClickEventHandler) {
        if (player != null) {
            menu.put(player, new IconMenu(inv, optionClickEventHandler));
        }
    }

    public IconMenu getMenu(Player player) {
    	return menu.get(player);
    }
    
    public void show(Player player) {
        if (menu.containsKey(player)) {
            player.openInventory(menu.get(player).getInventory());
        }
    }

    public void destroy(Player player) {
        if (menu.containsKey(player)) {
            menu.remove(player);
        }
    }

    public void destroyAll() {
        for (Player player : new HashSet<Player>(menu.keySet())) {
            destroy(player);
        }
    }

    public boolean has(Player player) {
        return menu.containsKey(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player && menu.containsKey(event.getWhoClicked())) {
            menu.get(event.getWhoClicked()).onInventoryClick(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player && menu.containsKey(event.getPlayer())) {
        	new BukkitRunnable() {
				@Override
				public void run() {
					if (event.getPlayer().getOpenInventory().equals(menu.get(event.getPlayer()).getInventory())) {
						destroy((Player) event.getPlayer());
					}
				}
        	}.runTaskLater(SkyWarsReloaded.get(), 5);
        }
    }
}