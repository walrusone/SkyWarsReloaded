package com.walrusone.skywarsreloaded.utilities;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class VaultUtils {

	private static VaultUtils instance;
	private Economy econ = null;
	
	private VaultUtils() {
		if (!setupEconomy()) {
			SkyWarsReloaded.get().getLogger().info("ERROR: Vault Dependency was not found. Install Vault or turn off Economy in the Config!");
		}
	}
	
	public static VaultUtils get() {
        if (VaultUtils.instance == null) {
            VaultUtils.instance = new VaultUtils();
        }
        return VaultUtils.instance;
	}

	private boolean setupEconomy() {
		if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
	}
	
	public boolean canBuy(Player player, double cost) {
		if (econ != null) {
			if (econ.getBalance(player) >= cost) {
				return true;
			}
		}
		return false;
	}
	public boolean payCost(Player player, double cost) {
		if (econ != null) {
			EconomyResponse rp = econ.withdrawPlayer(player, cost);
			if (rp.transactionSuccess()) {
				return true;
			}
		}
		return false;
	}
	
	public double getBalance(Player player) {
		if (econ != null) {
			return econ.getBalance(player);
		}
		return 0;
	}

	public void give(Player win, int i) {
		if (econ != null) {
			econ.depositPlayer(win, i);
		}
	}
	
}
