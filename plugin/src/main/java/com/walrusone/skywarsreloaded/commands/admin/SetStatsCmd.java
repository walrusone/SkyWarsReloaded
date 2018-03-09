package com.walrusone.skywarsreloaded.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.database.DataStorage;
import com.walrusone.skywarsreloaded.managers.PlayerStat;
import com.walrusone.skywarsreloaded.menus.playeroptions.ParticleEffectOption;
import com.walrusone.skywarsreloaded.menus.playeroptions.ProjectileEffectOption;
import com.walrusone.skywarsreloaded.utilities.Messaging;
import com.walrusone.skywarsreloaded.utilities.Util;

public class SetStatsCmd extends BaseCmd { 
	
	public SetStatsCmd(String t) {
		type = t;
		forcePlayer = false;
		cmdName = "setstat";
		alias = new String[]{"ss"};
		argLength = 4; //counting cmdName
	}

	@Override
	public boolean run() {
		Player swPlayer = null;
		for (Player playerMatch: Bukkit.getOnlinePlayers()) {
			if (ChatColor.stripColor(playerMatch.getName()).equalsIgnoreCase(ChatColor.stripColor(args[1]))) {
				swPlayer = playerMatch;
			}
		}
		
		if (swPlayer != null) {
			PlayerStat pStat = PlayerStat.getPlayerStats(swPlayer);
			if (pStat != null) {
                if (args[2].equalsIgnoreCase("wins")) {
                    if (Util.get().isInteger(args[3])) {
                        pStat.setWins(Integer.valueOf(args[3]));
                        DataStorage.get().saveStats(pStat);
                        sender.sendMessage(new Messaging.MessageFormatter().setVariable("player", args[1])
                                .setVariable("stat", args[2]).setVariable("amount", args[3]).format("command.setstat"));
                    } else {
                        sender.sendMessage(new Messaging.MessageFormatter().format("command.must-be-int"));
                    }
                } else if (args[2].equalsIgnoreCase("losses")) {
                    if (Util.get().isInteger(args[3])) {
                        pStat.setLosts(Integer.valueOf(args[3]));
                        DataStorage.get().saveStats(pStat);
                        sender.sendMessage(new Messaging.MessageFormatter().setVariable("player", args[1])
                                .setVariable("stat", args[2]).setVariable("amount", args[3]).format("command.setstat"));
                    } else {
                        sender.sendMessage(new Messaging.MessageFormatter().format("command.must-be-int"));
                    }
                } else if (args[2].equalsIgnoreCase("kills")) {
                    if (Util.get().isInteger(args[3])) {
                        pStat.setKills(Integer.valueOf(args[3]));
                        DataStorage.get().saveStats(pStat);
                        sender.sendMessage(new Messaging.MessageFormatter().setVariable("player", args[1])
                                .setVariable("stat", args[2]).setVariable("amount", args[3]).format("command.setstat"));
                    } else {
                        sender.sendMessage(new Messaging.MessageFormatter().format("command.must-be-int"));
                    }
                } else if (args[2].equalsIgnoreCase("deaths")) {
                    if (Util.get().isInteger(args[3])) {
                        pStat.setDeaths(Integer.valueOf(args[3]));
                        DataStorage.get().saveStats(pStat);
                        sender.sendMessage(new Messaging.MessageFormatter().setVariable("player", args[1])
                                .setVariable("stat", args[2]).setVariable("amount", args[3]).format("command.setstat"));
                    } else {
                        sender.sendMessage(new Messaging.MessageFormatter().format("command.must-be-int"));
                    }
                } else if (args[2].equalsIgnoreCase("elo")) {
                    if (Util.get().isInteger(args[3])) {
                        pStat.setElo(Integer.valueOf(args[3]));
                        DataStorage.get().saveStats(pStat);
                        sender.sendMessage(new Messaging.MessageFormatter().setVariable("player", args[1])
                                .setVariable("stat", args[2]).setVariable("amount", args[3]).format("command.setstat"));
                    } else {
                        sender.sendMessage(new Messaging.MessageFormatter().format("command.must-be-int"));
                    }
                } else if (args[2].equalsIgnoreCase("xp")) {
                    if (Util.get().isInteger(args[3])) {
                        pStat.setXp(Integer.valueOf(args[3]));
                        if (SkyWarsReloaded.getCfg().displayPlayerExeperience()) {
                            Util.get().setPlayerExperience(swPlayer, Integer.valueOf(args[3]));
                        }
                        DataStorage.get().saveStats(pStat);
                        sender.sendMessage(new Messaging.MessageFormatter().setVariable("player", args[1])
                                .setVariable("stat", args[2]).setVariable("amount", args[3]).format("command.setstat"));
                    } else {
                        sender.sendMessage(new Messaging.MessageFormatter().format("command.must-be-int"));
                    }
                } else if (args[2].equalsIgnoreCase("pareffect")) {
                    if (ParticleEffectOption.getPlayerOptionByKey(args[3]) != null) {
                        pStat.setParticleEffect(args[3].toLowerCase());
                        DataStorage.get().saveStats(pStat);
                        sender.sendMessage(new Messaging.MessageFormatter().setVariable("player", args[1])
                                .setVariable("stat", args[2]).setVariable("amount", args[3]).format("command.setstat"));
                    } else {
                        sender.sendMessage(new Messaging.MessageFormatter().format("command.invalid-effect"));
                    }
                } else if (args[2].equalsIgnoreCase("proeffect")) {
                    if (ProjectileEffectOption.getPlayerOptionByKey(args[3]) != null) {
                        pStat.setProjectileEffect(args[3].toLowerCase());
                        DataStorage.get().saveStats(pStat);
                        sender.sendMessage(new Messaging.MessageFormatter().setVariable("player", args[1])
                                .setVariable("stat", args[2]).setVariable("amount", args[3]).format("command.setstat"));
                    } else {
                        sender.sendMessage(new Messaging.MessageFormatter().format("command.invalid-effect"));
                    }
                } else if (args[2].equalsIgnoreCase("glasscolor")) {
                    if (Util.get().getByteFromColor(args[3]) != -1) {
                        pStat.setGlassColor(args[3].toLowerCase());
                        DataStorage.get().saveStats(pStat);
                        sender.sendMessage(new Messaging.MessageFormatter().setVariable("player", args[1])
                                .setVariable("stat", args[2]).setVariable("amount", args[3]).format("command.setstat"));
                    } else {
                        sender.sendMessage(new Messaging.MessageFormatter().format("command.invalid-color"));
                    }
                } else if (args[2].equalsIgnoreCase("killsound")) {
                    try {
                        @SuppressWarnings("unused")
                        Sound sound = Sound.valueOf(args[3].toUpperCase());
                        pStat.setKillSound(args[3].toUpperCase());
                        DataStorage.get().saveStats(pStat);
                        sender.sendMessage(new Messaging.MessageFormatter().setVariable("player", args[1])
                                .setVariable("stat", args[2]).setVariable("amount", args[3]).format("command.setstat"));
                    } catch (IllegalArgumentException e) {
                        sender.sendMessage(new Messaging.MessageFormatter().format("command.invalid-sound"));
                    }
                } else if (args[2].equalsIgnoreCase("winsound")) {
                    try {
                        @SuppressWarnings("unused")
                        Sound sound = Sound.valueOf(args[3].toUpperCase());
                        pStat.setWinSound(args[3].toUpperCase());
                        DataStorage.get().saveStats(pStat);
                        sender.sendMessage(new Messaging.MessageFormatter().setVariable("player", args[1])
                                .setVariable("stat", args[2]).setVariable("amount", args[3]).format("command.setstat"));
                    } catch (IllegalArgumentException e) {
                        sender.sendMessage(new Messaging.MessageFormatter().format("command.invalid-sound"));
                    }
                } else {
                    sender.sendMessage(new Messaging.MessageFormatter().format("command.stat-types"));
                }
            }
		} else {
			sender.sendMessage(new Messaging.MessageFormatter().format("command.must-be-online"));
		}

		return true;
	}

}
