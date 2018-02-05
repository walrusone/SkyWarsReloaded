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
    	getMatchStartItems();
    	getChestVoteItems();
    	getTimeVoteItems();
    	getWeatherVoteItems();
    	getModifierVoteItems();
    	getLobbyItem();
    	getOptionItems();
    }
    
    private void addItem(String materialref, List<String> lore, String message) {
    	ItemStack addItem = SkyWarsReloaded.getNMS().getItemStack(Material.valueOf(SkyWarsReloaded.getCfg().getMaterial(materialref)), lore, new Messaging.MessageFormatter().format(message));
        gameItems.put(materialref, addItem);
    }
    
    private void getLobbyItem() {
        List<String> lore = new ArrayList<String>();
        lore.add(new Messaging.MessageFormatter().format("items.click-to-open"));
        
        addItem("optionselect", lore, "items.skywars-options"); 
        addItem("joinselect", lore, "items.joinmenu");
        addItem("spectateselect", lore, "items.spectatemenu");
    }

	private void getMatchStartItems() {
        List<String> lore = new ArrayList<String>();
        lore.add(new Messaging.MessageFormatter().format("items.click-to-open"));
        
		if (SkyWarsReloaded.getCfg().kitVotingEnabled()) {
	        addItem("kitvote", lore, "items.kit-vote-item");
		} else {
			addItem("kitvote", lore, "items.kit-select-item");
		}
		addItem("chestvote", lore, "items.chest-item");
		addItem("nopermission", lore, "items.no-perm");
		addItem("timevote", lore, "items.time-item");
		addItem("weathervote", lore, "items.weather-item");
		addItem("modifiervote", lore, "items.modifier-item");
    }
    
    private void getChestVoteItems() {
    	List<String> lore = new ArrayList<String>();
		lore.add(new Messaging.MessageFormatter().format("items.click-to-vote"));
		
		addItem("chestrandom", lore, "items.chest-random");
		addItem("chestbasic", lore, "items.chest-basic");
		addItem("chestnormal", lore, "items.chest-normal");
		addItem("chestop", lore, "items.chest-op");
		addItem("chestscavenger", lore, "items.chest-scavenger");
	}
    
    private void getTimeVoteItems() {
    	List<String> lore = new ArrayList<String>();
		lore.add(new Messaging.MessageFormatter().format("items.click-to-vote"));
		
		addItem("timerandom", lore, "items.time-random");
		addItem("timedawn", lore, "items.time-dawn");
		addItem("timenoon", lore, "items.time-noon");
		addItem("timedusk", lore, "items.time-dusk");
		addItem("timemidnight", lore, "items.time-midnight");
    }
    
    private void getWeatherVoteItems() {
    	List<String> lore = new ArrayList<String>();
		lore.add(new Messaging.MessageFormatter().format("items.click-to-vote"));
		
		addItem("weatherrandom", lore, "items.weather-random");
		addItem("weathersunny", lore, "items.weather-sunny");
		addItem("weatherrain", lore, "items.weather-rain");
		addItem("weatherstorm", lore, "items.weather-storm");
		addItem("weathersnow", lore, "items.weather-snow");
    }

    private void getModifierVoteItems() {
    	List<String> lore = new ArrayList<String>();
		lore.add(new Messaging.MessageFormatter().format("items.click-to-vote"));
		
		addItem("modifierrandom", lore, "items.modifier-random");
		addItem("modifierspeed", lore, "items.modifier-speed");
		addItem("modifierjump", lore, "items.modifier-jump");
		addItem("modifierstrength", lore, "items.modifier-strength");
		addItem("modifiernone", lore, "items.modifier-none");
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