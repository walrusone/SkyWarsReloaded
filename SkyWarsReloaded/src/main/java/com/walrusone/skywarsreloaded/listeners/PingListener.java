package com.walrusone.skywarsreloaded.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.objects.GameMap;

public class PingListener implements Listener {

    @EventHandler
    public void onPing(final ServerListPingEvent serverListPingEvent) {
        if (SkyWarsReloaded.get().serverLoaded()) {
            if (GameMap.getMaps().size() > 0) {
                GameMap game = GameMap.getMaps().get(0);
                serverListPingEvent.setMotd(game.getMatchState().toString() + ":" + game.getPlayerCount() + ":" + game.getMaxPlayers() + ":" + game.getDisplayName());
            } else {
                serverListPingEvent.setMotd(MatchState.ENDING.toString() + ":" + 0 + ":" + 0 + ":" + "null");
            }
        } else {
            serverListPingEvent.setMotd(MatchState.ENDING.toString() + ":" + 0 + ":" + 0 + ":" + "null");
        }
    }

}