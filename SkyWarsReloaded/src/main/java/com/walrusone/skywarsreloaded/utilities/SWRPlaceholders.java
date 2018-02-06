package com.walrusone.skywarsreloaded.utilities;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.objects.PlayerStat;

import me.clip.placeholderapi.external.EZPlaceholderHook;

public class SWRPlaceholders extends EZPlaceholderHook {

	@SuppressWarnings("unused")
	private SkyWarsReloaded swr;
	
	public SWRPlaceholders(Plugin plugin) {
		super(plugin, "swr");
		this.swr = (SkyWarsReloaded) plugin;
	}

	@Override
	public String onPlaceholderRequest(Player player, String identifier) {
		if (identifier.equals("elo")) {
			return "" + PlayerStat.getPlayerStats(player).getElo();
		}
		
		if (identifier.equals("wins")) {
			return "" + PlayerStat.getPlayerStats(player).getWins();
		}
		
		if (identifier.equals("losses")) {
			return "" + PlayerStat.getPlayerStats(player).getLosses();
		}
		
		if (identifier.equals("kills")) {
			return "" + PlayerStat.getPlayerStats(player).getKills();
		}
		
		if (identifier.equals("deaths")) {
			return "" + PlayerStat.getPlayerStats(player).getDeaths();
		}
		
		if (identifier.equals("xp")) {
			return "" + PlayerStat.getPlayerStats(player).getXp();
		}
		
		if (identifier.equals("games_played")) {
			return "" + (PlayerStat.getPlayerStats(player).getLosses() + PlayerStat.getPlayerStats(player).getWins());
		}
		
		if (identifier.equals("kill_death")) {
			String.format("%1$,.2f", ((double)((double) PlayerStat.getPlayerStats(player).getKills() / (double) PlayerStat.getPlayerStats(player).getDeaths())));
		}
		
		if (identifier.equals("win_loss")) {
			String.format("%1$,.2f", ((double)((double) PlayerStat.getPlayerStats(player).getWins() / (double) PlayerStat.getPlayerStats(player).getLosses())));
		}
		
		return null;
	}

}
