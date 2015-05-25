package com.walrusone.skywars.utilities;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import com.walrusone.skywars.SkyWarsReloaded;

// https://forums.bukkit.org/threads/icon-menu.108342/
public class IconMenu {

    private String name;
    private int size;
    private OptionClickEventHandler handler;
    private String[] optionNames;
    private ItemStack[] optionIcons;

    public IconMenu(String name, int size, OptionClickEventHandler handler) {
        this.name = name;
        this.size = size;
        this.handler = handler;
        this.optionNames = new String[size];
        this.optionIcons = new ItemStack[size];
    }

    public IconMenu setOption(int position, ItemStack icon, String name, String[] info) {
        this.optionNames[position] = name;
        this.optionIcons[position] = ItemUtils.name(icon, name, info);
        return this;
    }

    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(player, this.size, this.name);
        for (int iii = 0; iii < this.optionIcons.length; iii++) {
            if (this.optionIcons[iii] != null) {
                inventory.setItem(iii, this.optionIcons[iii]);
            }
        }
        player.openInventory(inventory);
    }
    
    @SuppressWarnings("deprecation")
	public void update(Player player) {
    	InventoryView inventory = player.getOpenInventory();
    	if (inventory != null) {
            for (int iii = 0; iii < this.optionIcons.length; iii++) {
                if (this.optionIcons[iii] != null) {
                    inventory.setItem(iii, this.optionIcons[iii]);
                }
            }
            player.updateInventory();
    	}
    }

    public void destroy() {
        this.handler = null;
        this.optionNames = null;
        this.optionIcons = null;
    }

    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getInventory().getTitle().equals(name)) {
            return;
        }

        event.setCancelled(true);

        int slot = event.getRawSlot();
 
        try {
            if (!(slot >= 0 && slot < size && optionNames[slot] != null)) {
                return;
            }
        } catch (NullPointerException e) {
        	return;
        }


        OptionClickEvent clickEvent = new OptionClickEvent((Player) event.getWhoClicked(), slot, optionNames[slot]);
        handler.onOptionClick(clickEvent);

        if (clickEvent.willClose()) {
            final Player player = (Player) event.getWhoClicked();

            Bukkit.getScheduler().runTaskLater(SkyWarsReloaded.get(), new Runnable() {
                @Override
                public void run() {
                    player.closeInventory();
                }
            }, 1L);
        }

        if (clickEvent.willDestroy()) {
            destroy();
        }
    }

    public String getName() {
        return this.name;
    }
    
    public String[] getOptions() {
    	return optionNames;
    }

    public static class OptionClickEvent {

        private Player player;
        private int position;
        private String name;
        private boolean close;
        private boolean destroy;

        public OptionClickEvent(Player player, int position, String name) {
            this.player = player;
            this.position = position;
            this.name = name;
            this.close = false;
            this.destroy = false;
        }

        public Player getPlayer() {
            return this.player;
        }

        public int getPosition() {
            return this.position;
        }

        public String getName() {
            return this.name;
        }

        public boolean willClose() {
            return this.close;
        }

        public boolean willDestroy() {
            return this.destroy;
        }

        public void setWillClose(boolean close) {
            this.close = close;
        }

        public void setWillDestroy(boolean destroy) {
            this.destroy = destroy;
        }
    }

    public static abstract interface OptionClickEventHandler {

        public abstract void onOptionClick(IconMenu.OptionClickEvent event);
    }
}