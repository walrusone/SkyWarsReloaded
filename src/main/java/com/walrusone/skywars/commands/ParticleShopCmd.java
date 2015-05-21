package com.walrusone.skywars.commands;

import org.bukkit.Location;

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
		if (SkyWarsReloaded.getCfg().purchaseParticlesEnabled()) {
			Location spawn = SkyWarsReloaded.getCfg().getSpawn();
			if (spawn != null) {
				if(SkyWarsReloaded.getCfg().getSpawn().getWorld().getName().equalsIgnoreCase(player.getWorld().getName())) {
					GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(player.getUniqueId());
					if (!gPlayer.inGame()) {
							new BuyEffectMenu(gPlayer);
							return true;
					} else {
						sender.sendMessage(new Messaging.MessageFormatter().format("error.shop-not-available"));
						return true;
					}
				} else {
					sender.sendMessage(new Messaging.MessageFormatter().format("error.lobby-only-command"));	
				}
			} else {
				sender.sendMessage(new Messaging.MessageFormatter().format("error.no-spawn-set"));
			}
		} else {
			sender.sendMessage(new Messaging.MessageFormatter().format("error.particle-shop-disabled"));
			return true;
		}
		return true;
	}

}
