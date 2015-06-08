package com.walrusone.skywars.commands;

import org.bukkit.ChatColor;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.utilities.ParticleItem;


public class SetEffectCmd extends BaseCmd { 
	
	public SetEffectCmd() {
		forcePlayer = false;
		cmdName = "seteffect";
		argLength = 3; //counting cmdName
		usage = "<player> <particleeffect>";
		desc = ":: Sets a players particle effect";

	}

	@Override
	public boolean run() {
		String playerName = args[1];
		String effect = args[2];
		Boolean effectChange = false;
		GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayerByName(playerName);
		if (gPlayer != null) {
			if (effect.equalsIgnoreCase("normal")) {
				gPlayer.setEffect("normal");
				effectChange = true;
			} else {
				ParticleItem pEffect = SkyWarsReloaded.getPEC().getByEffect(effect);
				if (pEffect != null) {
					gPlayer.setEffect(pEffect.getEffect());
					effectChange = true;
				}
			}
			if (!effectChange) {
				sender.sendMessage(ChatColor.RED + "That is not a valid particle effect. Valid effects are:");
				StringBuilder effects = new StringBuilder();
				for (ParticleItem pItem: SkyWarsReloaded.getPEC().getParticleItems()) {
					effects.append(pItem.getEffect() + ", ");
				}
				effects.append("normal");
				String effectList = effects.toString();
				sender.sendMessage(ChatColor.RED + effectList);
			} else {
				sender.sendMessage(ChatColor.GREEN + playerName + "'s Particle Effect was set to: " + effect);
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Could not find a player named: " + playerName);
		}
		return true;
	}

}
