package com.walrusone.skywars.utilities;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.Game;

public class BungeeUtil {

	public static void connectToServer(Player player, String server) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(stream);
		try {
			out.writeUTF("Connect");
			out.writeUTF(server);
		} catch (IOException e) {
			e.printStackTrace();
		}
		player.sendPluginMessage(SkyWarsReloaded.get(), "BungeeCord", stream.toByteArray());
	}

	public static void sendSignUpdateRequest(Game game) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		String name = SkyWarsReloaded.getCfg().getBungeeServer();
		try {
			out.writeUTF("Forward");
			out.writeUTF("ALL");
			out.writeUTF("SkyWarsReloaded");

			ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
			DataOutputStream msgout = new DataOutputStream(msgbytes);
			msgout.writeUTF(name + ":" + game.getState().toString() + ":" + Integer.toString(game.getPlayers().size()) + ":" + Integer.toString(game.getNumberOfSpawns()) + ":" + game.getMapName());

			out.writeShort(msgbytes.toByteArray().length);
			out.write(msgbytes.toByteArray());

			Bukkit.getServer().sendPluginMessage(SkyWarsReloaded.get(), "BungeeCord", out.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
