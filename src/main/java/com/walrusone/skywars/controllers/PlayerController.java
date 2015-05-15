package com.walrusone.skywars.controllers;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.collect.Maps;
import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.GamePlayer;

public class PlayerController {

	private final Map<UUID, GamePlayer> onlinePlayers = Maps.newHashMap();
	
	public PlayerController() {
		for (Player player : Bukkit.getOnlinePlayers()) {
            addPlayer(player.getUniqueId());
        }
	}
	
	public void addPlayer(UUID uuid) {
        if (!this.onlinePlayers.containsKey(uuid)) {
            final GamePlayer gamePlayer = new GamePlayer(uuid);
            onlinePlayers.put(uuid, gamePlayer);
            SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
		        public void run() {
		        	String world = gamePlayer.getP().getWorld().getName();
		        	String lobbyWorld = SkyWarsReloaded.get().getConfig().getString("spawn.world");
		    		if (world.equalsIgnoreCase(lobbyWorld)) {
			        	SkyWarsReloaded.getScore().getScoreboard(gamePlayer.getP());
		    		}
			   }
            }, 5);
        }
    }
	
    public void removePlayer(UUID uuid) {
    	SkyWarsReloaded.getDS().savePlayerAsync(onlinePlayers.get(uuid));
        onlinePlayers.remove(uuid);
    }

    public GamePlayer getPlayer(UUID uuid) {
        return onlinePlayers.get(uuid);
    }

    public Collection<GamePlayer> getAll() {
        return onlinePlayers.values();
    }

    public void shutdown() {
        for (GamePlayer gamePlayer : onlinePlayers.values()) {
            SkyWarsReloaded.getDS().savePlayerSync(gamePlayer);
        }
        onlinePlayers.clear();
    }
    
    public void savePlayersAsync() {
    	for (GamePlayer gamePlayer : onlinePlayers.values()) {
            SkyWarsReloaded.getDS().savePlayerAsync(gamePlayer);
        }
    }
    
}
