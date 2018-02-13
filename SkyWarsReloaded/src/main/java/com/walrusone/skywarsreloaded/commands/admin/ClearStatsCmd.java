package com.walrusone.skywarsreloaded.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.database.DataStorage;
import com.walrusone.skywarsreloaded.objects.PlayerStat;
import com.walrusone.skywarsreloaded.utilities.Messaging;

public class ClearStatsCmd extends BaseCmd { 
	
	public ClearStatsCmd(String t) {
		type = t;
		forcePlayer = false;
		cmdName = "clearstats";
		alias = new String[]{"cs"};
		argLength = 2; //counting cmdName
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
			pStat.clear();
			DataStorage.get().saveStats(pStat);
			player.sendMessage(new Messaging.MessageFormatter().setVariable("kit", args[1]).format("command.stats-cleared"));
			return true;
		} else {
			new BukkitRunnable() {
				@Override
				public void run() {
					OfflinePlayer offlinePlayer = null;
					for (OfflinePlayer playerMatch: Bukkit.getOfflinePlayers()) {
						if (ChatColor.stripColor(playerMatch.getName()).equalsIgnoreCase(ChatColor.stripColor(args[1]))) {
							offlinePlayer = playerMatch;
						}
					}
					if (offlinePlayer != null) {
						final String uuid = offlinePlayer.getUniqueId().toString();
						new BukkitRunnable() {
							@Override
							public void run() {
									DataStorage.get().removePlayerData(uuid);
									player.sendMessage(new Messaging.MessageFormatter().setVariable("kit", args[1]).format("command.stats-cleared"));
							}
						}.runTask(SkyWarsReloaded.get());
					} else {
						new BukkitRunnable() {
							@Override
							public void run() {
								player.sendMessage(new Messaging.MessageFormatter().format("command.player-not-found"));
							}
						}.runTask(SkyWarsReloaded.get());
					}
				}
			}.runTaskAsynchronously(SkyWarsReloaded.get());
		}
		return true;
	}

}
