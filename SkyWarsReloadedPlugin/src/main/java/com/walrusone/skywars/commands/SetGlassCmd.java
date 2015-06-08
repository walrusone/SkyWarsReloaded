package com.walrusone.skywars.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.Game;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.utilities.GlassColor;


public class SetGlassCmd extends BaseCmd { 
	
	public SetGlassCmd() {
		forcePlayer = false;
		cmdName = "setglass";
		argLength = 3; //counting cmdName
		usage = "<player> <glasscolor>";
		desc = ":: Sets a players glass color";

	}

	@Override
	public boolean run() {
		String playerName = args[1];
		String color = args[2];
		Boolean colorChange = false;
		GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayerByName(playerName);
		if (gPlayer != null) {
			if (color.equalsIgnoreCase("normal")) {
				gPlayer.setGlass("normal");
				colorChange = true;
			} else {
				GlassColor glassColor = SkyWarsReloaded.getGLC().getByColor(color);
				if (glassColor != null) {
					gPlayer.setGlass(glassColor.getColor());
					colorChange = true;
				}
			}
			if (gPlayer.inGame() && colorChange) {
				String color1 = gPlayer.getGlass();
				Game game = gPlayer.getGame();
				if (color1 == null) {
					color1 = "normal";
				}
				if (!color1.equalsIgnoreCase("normal")) {
					GlassColor colorGlass = SkyWarsReloaded.getGLC().getByColor(color1);
					if (colorGlass != null) {
						game.setGlass(colorGlass.getMaterial(), colorGlass.getData(), gPlayer);
					} else {
						game.setGlass(Material.GLASS, gPlayer);
					}
				} else {
					game.setGlass(Material.GLASS, gPlayer);
				}
			}
			if (!colorChange) {
				sender.sendMessage(ChatColor.RED + "That is not a valid color. Valid colors are:");
				StringBuilder colors = new StringBuilder();
				for (GlassColor gColor: SkyWarsReloaded.getGLC().getColorItems()) {
					colors.append(gColor.getColor() + ", ");
				}
				colors.append("normal");
				String col = colors.toString();
				sender.sendMessage(ChatColor.RED + col);
			} else {
				sender.sendMessage(ChatColor.GREEN + playerName + "'s Glass Color was set to: " + color);
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Could not find a player named: " + playerName);
		}
		return true;
	}

}
