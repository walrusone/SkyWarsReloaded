package com.walrusone.skywarsreloaded.listeners;

import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Maps;
import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.utilities.Messaging;

public class ChatListener implements Listener {

	private static Map<UUID, Long> chatList = Maps.newHashMap();
	private static Map<UUID, String> toChange = Maps.newHashMap();
	
	@EventHandler
    public void signPlaced(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		UUID uuid = event.getPlayer().getUniqueId();
		if (chatList.containsKey(uuid)) {
			if (Math.abs((System.currentTimeMillis() - chatList.get(uuid))) < 20000) {
				ChatListener.chatList.remove(uuid);
				event.setCancelled(true);
				String[] settings = toChange.get(uuid).split(":");
				GameMap gMap = GameMap.getMap(settings[0]);
				String setting = settings[1];
				String variable = event.getMessage();
				if (setting.equals("display")) {
					gMap.setDisplayName(variable);
					player.sendMessage(new Messaging.MessageFormatter().setVariable("mapname", gMap.getName()).setVariable("displayname", variable).format("maps.name"));
					new BukkitRunnable() {
						@Override
						public void run() {
							gMap.update();
						}
					}.runTask(SkyWarsReloaded.get());
					SkyWarsReloaded.getIC().show(player, gMap.getArenaKey());
				} else if (setting.equalsIgnoreCase("creator")) {
					gMap.setCreator(variable);
					player.sendMessage(new Messaging.MessageFormatter().setVariable("mapname", gMap.getName()).setVariable("creator", variable).format("maps.creator"));
					new BukkitRunnable() {
						@Override
						public void run() {
							gMap.update();
						}
					}.runTask(SkyWarsReloaded.get());
					SkyWarsReloaded.getIC().show(player, gMap.getArenaKey());
				}
				ChatListener.toChange.remove(uuid);
			} else {
				ChatListener.chatList.remove(uuid);
				ChatListener.toChange.remove(uuid);
			}
		}
	}
	
	public static void setTime(UUID uuid, long time) {
		chatList.put(uuid,  time);
	}
	
	public static void setSetting(UUID uuid, String setting) {
		toChange.put(uuid,  setting);
	}
}
