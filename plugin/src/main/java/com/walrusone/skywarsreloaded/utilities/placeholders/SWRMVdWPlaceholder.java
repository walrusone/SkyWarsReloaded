package com.walrusone.skywarsreloaded.utilities.placeholders;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;

public class SWRMVdWPlaceholder {

	private Plugin plugin;
	
	public SWRMVdWPlaceholder(Plugin p) {
		plugin = p;

		PlaceholderAPI.registerPlaceholder(plugin, "swr_elo", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				Player player = e.getPlayer();
				return "" + ((SkyWarsReloaded) p).getPlayerStat(player).getElo();
			}	
		});
		
		PlaceholderAPI.registerPlaceholder(plugin, "swr_wins", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				Player player = e.getPlayer();
				return "" + ((SkyWarsReloaded) p).getPlayerStat(player).getWins();
			}
		});
		
		PlaceholderAPI.registerPlaceholder(plugin, "swr_losses", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				Player player = e.getPlayer();
				return "" + ((SkyWarsReloaded) p).getPlayerStat(player).getKills();
			}
		});
		
		PlaceholderAPI.registerPlaceholder(plugin, "swr_deaths", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				Player player = e.getPlayer();
				return "" + ((SkyWarsReloaded) p).getPlayerStat(player).getDeaths();
			}
		});
		
		PlaceholderAPI.registerPlaceholder(plugin, "swr_xp", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				Player player = e.getPlayer();
				return "" + ((SkyWarsReloaded) p).getPlayerStat(player).getXp();
			}
		});
		
		PlaceholderAPI.registerPlaceholder(plugin, "swr_games_played", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				Player player = e.getPlayer();
				return "" + (((SkyWarsReloaded) p).getPlayerStat(player).getLosses() + ((SkyWarsReloaded) p).getPlayerStat(player).getWins());
			}
		});
		
		PlaceholderAPI.registerPlaceholder(plugin, "swr_kill_death", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				Player player = e.getPlayer();
				double stat = (double)((double)((SkyWarsReloaded) p).getPlayerStat(player).getKills()/(double)((SkyWarsReloaded) p).getPlayerStat(player).getDeaths());
				String statString = String.format("%1$,.2f", stat);
				return statString;
			}
		});
		
		PlaceholderAPI.registerPlaceholder(plugin, "swr_win_loss", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				Player player = e.getPlayer();
				double stat = (double)((double)((SkyWarsReloaded) p).getPlayerStat(player).getWins()/(double)((SkyWarsReloaded) p).getPlayerStat(player).getLosses());
				String statString = String.format("%1$,.2f", stat);
				return statString;
			}
		});
	}
}
