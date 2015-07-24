package com.walrusone.skywars.controllers;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
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
            if (SkyWarsReloaded.getCfg().LobbyScoreboardEnabeld()) {
                SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
    		        public void run() {
    		        	if (gamePlayer.getP() != null) {
        		        	String world = gamePlayer.getP().getWorld().getName();
        		        	Location spawn = SkyWarsReloaded.getCfg().getSpawn();
        		        	if (spawn != null) {
            		        	String lobbyWorld = spawn.getWorld().getName();
            		    		if (world.equalsIgnoreCase(lobbyWorld)) {
            			        	SkyWarsReloaded.getScore().getScoreboard(gamePlayer.getP());
            		    		}
        		        	}
    		        	}
    			   }
                }, 5);
            }
        }
    }

    public void removePlayer(UUID uuid) {
    	GamePlayer save = getPlayer(uuid);
    	String glassColor;
    	String effect;
    	String projEffect;
    	if (save.getGlass() != null) {
    		glassColor = save.getGlass();
    	} else {
    		glassColor = "normal";
    	}
    	if (save.getEffect() != null) {
    		effect = save.getEffect();
    	} else {
    		effect = "normal";
    	}
    	if (save.getProjEffect() != null) {
    		projEffect = save.getProjEffect();
    	} else {
    		projEffect = "normal";
    	}
    	SkyWarsReloaded.getDS().savePlayerAsync(uuid, save.getName(), save.getWins(), save.getKills(), save.getDeaths(), save.getGamesPlayed(), save.getScore(), save.getBalance(), save.getBlocks(), save.getPerms(), save.getNewPerms(), glassColor, effect, projEffect);
        onlinePlayers.remove(uuid);
    }

    public GamePlayer getPlayer(UUID uuid) {
        return onlinePlayers.get(uuid);
    }
    
    public GamePlayer getPlayerByName(String name) {
    	for (GamePlayer gPlayer: onlinePlayers.values()) {
    		if (gPlayer.getName().equals(name)) {
    			return gPlayer;
    		}
    	} 
    	return null;    	
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
    	for (GamePlayer save : onlinePlayers.values()) {
        	SkyWarsReloaded.getDS().savePlayerAsync(save.getUUID(), save.getName(), save.getWins(), save.getKills(), save.getDeaths(), save.getGamesPlayed(), save.getScore(), save.getBalance(), save.getBlocks(), save.getPerms(), save.getNewPerms(), save.getGlass(), save.getEffect(), save.getProjEffect());
        }
    }
    
}
