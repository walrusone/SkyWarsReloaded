package com.walrusone.skywars.listeners;

import com.google.common.collect.Maps;
import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.utilities.IconMenu;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

import java.util.HashSet;
import java.util.Map;

//https://github.com/Wietje/SkyWars/blob/master/src/main/java/vc/pvp/skywars/controllers/IconMenuController.java/
public class IconMenuController implements Listener {

    private final Map<Player, IconMenu> menu = Maps.newHashMap();

    public IconMenuController() {
    }

    public void create(Player player, String name, int size, IconMenu.OptionClickEventHandler handler) {
        destroy(player);
        menu.put(player, new IconMenu(name, size, handler, SkyWarsReloaded.get()));
    }

    public void show(@Nonnull Player player) {
        if (menu.containsKey(player)) {
            menu.get(player).open(player);
        }
    }

    public void setOption(Player player, int position, ItemStack icon, String name, String... info) {
        if (menu.containsKey(player)) {
            menu.get(player).setOption(position, icon, name, info);
        }
    }

    public void destroy(Player player) {
        if (menu.containsKey(player)) {
            menu.remove(player).destroy();
            player.getOpenInventory().close();
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
            destroy((Player) event.getPlayer());
        }
    }
}