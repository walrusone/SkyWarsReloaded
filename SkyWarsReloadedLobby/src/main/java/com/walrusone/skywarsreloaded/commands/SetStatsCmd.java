package com.walrusone.skywarsreloaded.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.database.DataStorage;
import com.walrusone.skywarsreloaded.objects.PlayerStat;
import com.walrusone.skywarsreloaded.utilities.Messaging;
import com.walrusone.skywarsreloaded.utilities.Util;

public class SetStatsCmd extends BaseCmd { 
	
	public SetStatsCmd() {
		forcePlayer = true;
		cmdName = "setstat";
		alias = new String[]{"ss", "sstats"};
		argLength = 4; //counting cmdName
	}

	@Override
	public boolean run() {
		Player bouncewarssPlayer = null;
		for (Player playerMatch: Bukkit.getOnlinePlayers()) {
			if (ChatColor.stripColor(playerMatch.getName()).equalsIgnoreCase(ChatColor.stripColor(args[1]))) {
				bouncewarssPlayer = playerMatch;
			}
		}
		
		if (bouncewarssPlayer != null) {
			PlayerStat pStat = PlayerStat.getPlayerStats(bouncewarssPlayer);
			if (args[2].equalsIgnoreCase("wins")) {
				if (Util.get().isInteger(args[3])) {
					pStat.setWins(Integer.valueOf(args[3]));
					DataStorage.get().saveStats(pStat);
					player.sendMessage(new Messaging.MessageFormatter().setVariable("player", args[1])
							.setVariable("stat", args[2]).setVariable("ammount", args[3]).format("command.setstat"));
				} else {
					player.sendMessage(new Messaging.MessageFormatter().format("command.must-be-int"));
				}
			} else if (args[2].equalsIgnoreCase("losses")) {
				if (Util.get().isInteger(args[3])) {
					pStat.setLosts(Integer.valueOf(args[3]));
					DataStorage.get().saveStats(pStat);
					player.sendMessage(new Messaging.MessageFormatter().setVariable("player", args[1])
							.setVariable("stat", args[2]).setVariable("ammount", args[3]).format("command.setstat"));
				} else {
					player.sendMessage(new Messaging.MessageFormatter().format("command.must-be-int"));
				}
			} else if (args[2].equalsIgnoreCase("kills")) {
				if (Util.get().isInteger(args[3])) {
					pStat.setKills(Integer.valueOf(args[3]));
					DataStorage.get().saveStats(pStat);
					player.sendMessage(new Messaging.MessageFormatter().setVariable("player", args[1])
							.setVariable("stat", args[2]).setVariable("ammount", args[3]).format("command.setstat"));
				} else {
					player.sendMessage(new Messaging.MessageFormatter().format("command.must-be-int"));
				}
			} else if (args[2].equalsIgnoreCase("deaths")) {
				if (Util.get().isInteger(args[3])) {
					pStat.setDeaths(Integer.valueOf(args[3]));
					DataStorage.get().saveStats(pStat);
					player.sendMessage(new Messaging.MessageFormatter().setVariable("player", args[1])
							.setVariable("stat", args[2]).setVariable("ammount", args[3]).format("command.setstat"));
				} else {
					player.sendMessage(new Messaging.MessageFormatter().format("command.must-be-int"));
				}
			} else if (args[2].equalsIgnoreCase("elo")) {
				if (Util.get().isInteger(args[3])) {
					pStat.setElo(Integer.valueOf(args[3]));
					DataStorage.get().saveStats(pStat);
					player.sendMessage(new Messaging.MessageFormatter().setVariable("player", args[1])
							.setVariable("stat", args[2]).setVariable("ammount", args[3]).format("command.setstat"));
				} else {
					player.sendMessage(new Messaging.MessageFormatter().format("command.must-be-int"));
				}
			}  else if (args[2].equalsIgnoreCase("xp")) {
				if (Util.get().isInteger(args[3])) {
					pStat.setXp(Integer.valueOf(args[3]));
					Util.get().setPlayerExperience(bouncewarssPlayer, Integer.valueOf(args[3]));
					DataStorage.get().saveStats(pStat);
					player.sendMessage(new Messaging.MessageFormatter().setVariable("player", args[1])
							.setVariable("stat", args[2]).setVariable("ammount", args[3]).format("command.setstat"));
				} else {
					player.sendMessage(new Messaging.MessageFormatter().format("command.must-be-int"));
				}
			} else if (args[2].equalsIgnoreCase("pareffect")) {
				if (SkyWarsReloaded.getNMS().isValueParticle(args[3])) {
					pStat.setParticleEffect(args[3].toLowerCase());
					DataStorage.get().saveStats(pStat);
					player.sendMessage(new Messaging.MessageFormatter().setVariable("player", args[1])
							.setVariable("stat", args[2]).setVariable("ammount", args[3]).format("command.setstat"));
				} else {
					player.sendMessage(new Messaging.MessageFormatter().format("command.invalid-effect"));
				}
			} else if (args[2].equalsIgnoreCase("proeffect")) {
				if (SkyWarsReloaded.getNMS().isValueParticle(args[3])) {
					pStat.setProjectileEffect(args[3].toLowerCase());
					DataStorage.get().saveStats(pStat);
					player.sendMessage(new Messaging.MessageFormatter().setVariable("player", args[1])
							.setVariable("stat", args[2]).setVariable("ammount", args[3]).format("command.setstat"));
				} else {
					player.sendMessage(new Messaging.MessageFormatter().format("command.invalid-effect"));
				}
			} else if (args[2].equalsIgnoreCase("glasscolor")) {
				if (Util.get().getByteFromColor(args[3]) != -1) {
					pStat.setGlassColor(args[3].toLowerCase());
					DataStorage.get().saveStats(pStat);
					player.sendMessage(new Messaging.MessageFormatter().setVariable("player", args[1])
							.setVariable("stat", args[2]).setVariable("ammount", args[3]).format("command.setstat"));
				} else {
					player.sendMessage(new Messaging.MessageFormatter().format("command.invalid-color"));
				}
			} else if (args[2].equalsIgnoreCase("killsound")) {
				try {
					@SuppressWarnings("unused")
					Sound sound = Sound.valueOf(args[3].toUpperCase());
					pStat.setKillSound(args[3].toUpperCase());
					DataStorage.get().saveStats(pStat);
					player.sendMessage(new Messaging.MessageFormatter().setVariable("player", args[1])
							.setVariable("stat", args[2]).setVariable("ammount", args[3]).format("command.setstat"));
				} catch(IllegalArgumentException e) {
					player.sendMessage(new Messaging.MessageFormatter().format("command.invalid-sound"));
				}
			} else if (args[2].equalsIgnoreCase("winsound")) {
				try {
					@SuppressWarnings("unused")
					Sound sound = Sound.valueOf(args[3].toUpperCase());
					pStat.setWinSound(args[3].toUpperCase());
					DataStorage.get().saveStats(pStat);
					player.sendMessage(new Messaging.MessageFormatter().setVariable("player", args[1])
							.setVariable("stat", args[2]).setVariable("ammount", args[3]).format("command.setstat"));
				} catch(IllegalArgumentException e) {
					player.sendMessage(new Messaging.MessageFormatter().format("command.invalid-sound"));
				}
			} else {
				player.sendMessage(new Messaging.MessageFormatter().format("command.stat-types"));
			}
		} else {
			player.sendMessage(new Messaging.MessageFormatter().format("command.must-be-online"));
		}

		return true;
	}

}
