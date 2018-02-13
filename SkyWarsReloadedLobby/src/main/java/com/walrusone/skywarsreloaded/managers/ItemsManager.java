package com.walrusone.skywarsreloaded.managers;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.utilities.Messaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemsManager {

    private final Map<String, ItemStack> gameItems = new HashMap<String, ItemStack>();

    public ItemsManager() {
    	getLobbyItem();
    	getOptionItems();
    }
    
    private void addItem(String materialref, List<String> lore, String message) {
    	ItemStack addItem = SkyWarsReloaded.getNMS().getItemStack(Material.valueOf(SkyWarsReloaded.getCfg().getMaterial(materialref).toUpperCase()), lore, new Messaging.MessageFormatter().format(message));
        gameItems.put(materialref, addItem);
    }
    
    private void getLobbyItem() {
        List<String> lore = new ArrayList<String>();
        lore.add(new Messaging.MessageFormatter().format("items.click-to-open"));
        
        addItem("joinselect", lore, "items.joinmenu");
        addItem("optionselect", lore, "items.skywars-options"); 
    }
    
    private void getOptionItems() {
        List<String> lore = new ArrayList<String>();
        lore.add(new Messaging.MessageFormatter().format("items.lclick-to-open"));
        
        addItem("particleselect", lore, "items.particle-effect-sel");
        addItem("projectileselect", lore, "items.projectile-effect-sel");
        addItem("killsoundselect", lore, "items.killsound-sel");
        addItem("winsoundselect", lore, "items.winsound-sel");
        addItem("glassselect", lore, "items.glass-sel");
        addItem("tauntselect", lore, "items.taunt-sel");
	}
    
	public ItemStack getItem(String item) {
    	return gameItems.get(item).clone();
    }
}