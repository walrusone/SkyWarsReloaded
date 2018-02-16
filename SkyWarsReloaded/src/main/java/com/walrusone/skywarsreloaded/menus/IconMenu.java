package com.walrusone.skywarsreloaded.menus;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;

// https://forums.bukkit.org/threads/icon-menu.108342/
public class IconMenu {

	private Inventory inv;
    private OptionClickEventHandler handler;

    public IconMenu(Inventory inv, OptionClickEventHandler optionClickEventHandler) {
        this.inv = inv;
        this.handler = optionClickEventHandler;
    }

    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(inv)) {
            return;
        }

        event.setCancelled(true);

        int slot = event.getRawSlot();
 
        try {
            if (!(slot >= 0 && slot < inv.getSize())) {
                return;
            }
        } catch (NullPointerException e) {
        	return;
        }

        String name = "uselessName";
        if (event.getInventory().getItem(slot) != null && !event.getInventory().getItem(slot).getType().equals(Material.AIR)) {
            name = SkyWarsReloaded.getNMS().getItemName(event.getCurrentItem());
        }

        if (!name.equalsIgnoreCase("uselessName")) {
            OptionClickEvent clickEvent = new OptionClickEvent((Player) event.getWhoClicked(), name);
            handler.onOptionClick(clickEvent);
        }
    }
    
	public Inventory getInventory() {
		return inv;
	}

    public static class OptionClickEvent {

        private Player player;
        private String name;

        public OptionClickEvent(Player player, String name) {
            this.player = player;
            this.name = name;
        }

        public Player getPlayer() {
            return this.player;
        }

        public String getName() {
            return this.name;
        }
        
    }
    

	public interface OptionClickEventHandler {
		void onOptionClick(OptionClickEvent clickEvent);
	}

}