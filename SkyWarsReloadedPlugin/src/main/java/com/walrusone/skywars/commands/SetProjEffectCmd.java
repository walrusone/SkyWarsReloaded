package com.walrusone.skywars.commands;

import org.bukkit.ChatColor;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.utilities.ParticleItem;


public class SetProjEffectCmd extends BaseCmd { 
	
	public SetProjEffectCmd() {
		forcePlayer = false;
		cmdName = "setprojeffect";
		argLength = 3; //counting cmdName
		usage = "<player> <particleeffect>";
		desc = ":: Sets a players projectile particle effect";

	}

	@Override
	public boolean run() {
		String playerName = args[1];
		String effect = args[2];
		Boolean effectChange = false;
		GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayerByName(playerName);
		if (gPlayer != null) {
			if (effect.equalsIgnoreCase("normal")) {
				gPlayer.setProjEffect("normal");
				effectChange = true;
			} else {
				ParticleItem pEffect = SkyWarsReloaded.getProjC().getByEffect(effect);
				if (pEffect != null) {
					gPlayer.setProjEffect(pEffect.getEffect());
					effectChange = true;
				}
			}
			if (!effectChange) {
				sender.sendMessage(ChatColor.RED + "That is not a valid particle effect. Valid effects are:");
				StringBuilder effects = new StringBuilder();
				for (ParticleItem pItem: SkyWarsReloaded.getProjC().getParticleItems()) {
					effects.append(pItem.getEffect() + ", ");
				}
				effects.append("normal");
				String effectList = effects.toString();
				sender.sendMessage(ChatColor.RED + effectList);
			} else {
				sender.sendMessage(ChatColor.GREEN + playerName + "'s Projectile Effect was set to: " + effect);
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Could not find a player named: " + playerName);
		}
		return true;
	}

}
