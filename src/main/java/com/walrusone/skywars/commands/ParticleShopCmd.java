package com.walrusone.skywars.commands;

import org.bukkit.ChatColor;
import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.menus.BuyEffectMenu;
import com.walrusone.skywars.utilities.Messaging;

public class ParticleShopCmd extends BaseCmd { 	
	
	public ParticleShopCmd() {
		forcePlayer = true;
		cmdName = "particleshop";
		argLength = 1; //counting cmdName
		usage = "";
		desc = ":: Opens the particle effects shop";

	}

	@Override
	public boolean run() {
		boolean enabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.purchaseParticlesEnabled");
		if (enabled) {
			GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(player.getUniqueId());
			if (!gPlayer.inGame()) {
					new BuyEffectMenu(gPlayer);
					return true;
			} else {
				sender.sendMessage(new Messaging.MessageFormatter().format("error.shop-not-available"));
				return true;
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Particle Shop is disabled!");
			return true;
		}
	}

}
