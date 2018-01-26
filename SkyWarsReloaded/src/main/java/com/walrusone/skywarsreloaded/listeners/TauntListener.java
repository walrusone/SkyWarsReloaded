package com.walrusone.skywarsreloaded.listeners;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.objects.GameMap;
import com.walrusone.skywarsreloaded.objects.PlayerStat;
import com.walrusone.skywarsreloaded.objects.Taunt;
import com.walrusone.skywarsreloaded.utilities.Messaging;

public class TauntListener implements Listener {

	private final HashMap<String, Long> lastHandSwap = new HashMap<String, Long>();
	private final HashMap<String, Long> lastTaunt = new HashMap<String, Long>();
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void pressedTauntKey(PlayerToggleSneakEvent e) {
		Player player = e.getPlayer();
        GameMap gameMap = MatchManager.get().getPlayerMap(player);
        if (gameMap == null) {
        	return;
        }
		String uuid = e.getPlayer().getUniqueId().toString();
		if (!player.isSneaking()) {
			if (lastHandSwap.containsKey(uuid)) {
				if (System.currentTimeMillis() - lastHandSwap.get(uuid) < 500) {
					if (lastTaunt.containsKey(uuid)) {
						if (System.currentTimeMillis() - lastTaunt.get(uuid) < SkyWarsReloaded.getCfg().getCooldown()*1000) {
							int cooldown = (int) ((SkyWarsReloaded.getCfg().getCooldown()*1000 - (System.currentTimeMillis() - lastTaunt.get(uuid)))/1000);
							int seconds = (cooldown % 60) + 1;
							int minutes = (cooldown-(seconds - 1)) / 60;
							String cooldownText = "";
							if (minutes > 0) {
								cooldownText = cooldownText + minutes + " Minutes ";
							} 
							if (seconds > 0) {
								cooldownText = cooldownText + seconds + " Seconds";
							}
							
							e.getPlayer().sendMessage(new Messaging.MessageFormatter().setVariable("timeleft", cooldownText).format("error.cooldown"));
							return;
						} else {
							lastTaunt.remove(uuid);
						}
					}
					if (e.getPlayer().hasPermission("taunts.use")) {
						PlayerStat ps = PlayerStat.getPlayerStats(e.getPlayer());
						String tauntName = ps.getTaunt();
						Taunt taunt = SkyWarsReloaded.getLM().getTauntFromKey(tauntName);
						if (taunt != null) {
							taunt.performTaunt(e.getPlayer());
						}
						lastHandSwap.remove(uuid);
						lastTaunt.put(uuid, System.currentTimeMillis());
					} else {
						e.getPlayer().sendMessage(new Messaging.MessageFormatter().format("error.taunt-no-perm"));
					}
				} else {
					lastHandSwap.put(uuid, System.currentTimeMillis());
				}
			} else {
				lastHandSwap.put(uuid, System.currentTimeMillis());
			}
		}	
	}	
}
