package com.walrusone.skywars.controllers;

import java.util.Collection;
import java.util.Map;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.collect.Maps;
import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.GamePlayer;

public class PlayerController {

	private final Map<Player, GamePlayer> onlinePlayers = Maps.newHashMap();
	
	public PlayerController() {
		for (Player player : Bukkit.getOnlinePlayers()) {
            addPlayer(player);
        }
	}
	
	public void addPlayer(@Nonnull Player p) {
        GamePlayer gamePlayer = null;

        if (!this.onlinePlayers.containsKey(p)) {
            gamePlayer = new GamePlayer(p);
            onlinePlayers.put(p, gamePlayer);
        }
    }
	
    public void removePlayer(@Nonnull Player p) {
    	SkyWarsReloaded.getDS().savePlayerAsync(onlinePlayers.get(p));
        onlinePlayers.remove(p);
    }

    public GamePlayer getPlayer(@Nonnull Player p) {
        return onlinePlayers.get(p);
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
