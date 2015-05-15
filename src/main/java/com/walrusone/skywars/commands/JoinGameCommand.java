package com.walrusone.skywars.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.Game;
import com.walrusone.skywars.game.Game.GameState;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.utilities.Messaging;

public class JoinGameCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	String lobbyWorld = SkyWarsReloaded.get().getConfig().getString("spawn.world");
		if (lobbyWorld != null) {
	  		boolean signJoinMode = SkyWarsReloaded.get().getConfig().getBoolean("signJoinMode");
    		boolean hasPerm = false;
    		if (!(sender instanceof Player)) {
    			sender.sendMessage(new Messaging.MessageFormatter().format("error.must-be-player"));
    		} else if (sender instanceof Player) {
    			Player player = (Player) sender;
    			if (SkyWarsReloaded.perms.has(player, "swr.play")) {
    				hasPerm = true;
    			}
    		} else {
    			sender.sendMessage(new Messaging.MessageFormatter().format("error.cmd-no-perm"));
    		}
    		if (hasPerm) {
	        	String world = ((Player) sender).getWorld().getName();
	    		if (!world.equalsIgnoreCase(lobbyWorld)) {
	    			sender.sendMessage(ChatColor.RED + "YOU CAN ONLY JOIN GAMES IN THE LOBBY WORLD");
	    			return true;
	    		}
    			if (args.length == 1) {
    				if (sender instanceof Player) {
    					Player player = (Player) sender;
    					GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(player.getUniqueId());
    			    	if (!signJoinMode) {
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
    				}
    			} else {
    				sender.sendMessage(ChatColor.RED + "USAGE: /swr join");
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
