package com.walrusone.skywarsreloaded.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.objects.GameMap;

import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SoupListener implements Listener
{
	@EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
        GameMap gameMap = MatchManager.get().getPlayerMap(player);
        if (gameMap == null) {
        	return;
        }
        
        if (!gameMap.soupPvp()) return;
        
        ItemStack item1 = SkyWarsReloaded.getNMS().getMainHandItem(player);
        ItemStack item2 = SkyWarsReloaded.getNMS().getOffHandItem(player);
        if (item1.getType().compareTo(Material.MUSHROOM_SOUP) == 0) {
        	doMushroomPower(item1, event, player);
        } else if (item2 != null && item2.getType().compareTo(Material.MUSHROOM_SOUP) == 0) {
        	doMushroomPower(item2, event, player);
        }
	}
	
	private void doMushroomPower(ItemStack item, PlayerInteractEvent event, Player player) {
		if (event.getAction().compareTo(Action.RIGHT_CLICK_AIR) == 0 || event.getAction().compareTo(Action.RIGHT_CLICK_BLOCK) == 0) {
            double h = player.getHealth();
            double mh = player.getMaxHealth();
            int f = player.getFoodLevel();
            
            if (h == mh) {
                if (f != 20) {
                    int fToAdd = f + 7;
                    if (fToAdd > 20) fToAdd = 20;
                    player.setFoodLevel(fToAdd);
                    item.setType(Material.BOWL);
                }
            }
            else {
                double hToAdd = h + 7;
                if (hToAdd > mh) hToAdd = mh;
                player.setHealth(hToAdd);
                item.setType(Material.BOWL);
            }
        }
	}
}
