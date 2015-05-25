package com.walrusone.skywars.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.Game;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.game.Game.GameState;
import com.walrusone.skywars.utilities.Messaging;


public class JoinGameCmd extends BaseCmd { 

	public JoinGameCmd() {
		forcePlayer = true;
		cmdName = "join";
		argLength = 1; //counting cmdName
		usage = "";
		desc = ":: Allows a player to join a game";

	}

	@Override
	public boolean run() {
		Location spawn = SkyWarsReloaded.getCfg().getSpawn();
		if (spawn == null) {
			sender.sendMessage(ChatColor.RED + "YOU MUST SET SPAWN BEFORE YOU CAN JOIN A GAME");
			return true;
		}
		String lobbyWorld = spawn.getWorld().getName();
		if (lobbyWorld != null) {
			String world = ((Player) sender).getWorld().getName();
			if (!world.equalsIgnoreCase(lobbyWorld)) {
				sender.sendMessage(ChatColor.RED + "YOU CAN ONLY JOIN GAMES IN THE LOBBY WORLD");
				return true;
			}
			GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(player.getUniqueId());
			if (!SkyWarsReloaded.getCfg().signJoinMode()) {
				if (!gPlayer.inGame()) {
					Game game = SkyWarsReloaded.getGC().findGame();
					if (game != null) {
						game.addPlayer(gPlayer);
					} else {
						SkyWarsReloaded.getGC().addToQueue(gPlayer);
						gPlayer.getP().sendMessage(new Messaging.MessageFormatter().format("game.no-game-available"));
					} 
				} else {
					sender.sendMessage(new Messaging.MessageFormatter().format("error.no-perm-in-game"));
				}
			} else {
				Game game = findGame();
				int i = 0;
				while (i < 3) {
					if (game != null && game.getState() == GameState.PREGAME && !game.isFull()) {
						game.addPlayer(gPlayer);
						break;
					} else {
						i++;
						game = findGame();
					}
				}
			}
			return true;
		} else {
			sender.sendMessage(ChatColor.RED + "YOU MUST SET SPAWN IN THE LOBBY WORLD WITH /SWR SETSPAWN BEFORE STARTING A GAME");
			SkyWarsReloaded.get().getLogger().info("YOU MUST SET SPAWN IN THE LOBBY WORLD WITH /SWR SETSPAWN BEFORE STARTING A GAME");
			return true;
		}
	}

	private Game findGame() {
		Game game = null;
		int highest = 0;
		for (Game g: SkyWarsReloaded.getGC().getGames()) {
			if (highest <= g.getPlayers().size() && g.getState() == GameState.PREGAME && !g.isFull()) {
				highest = g.getPlayers().size();
				game = g;
			}
		}
		return game;
	}
}
