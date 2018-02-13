package com.walrusone.skywarsreloaded.commands.maps;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.utilities.Messaging;

public class SaveCmd extends BaseCmd { 
	
	public SaveCmd(String t) {
		type = t;
		forcePlayer = false;
		cmdName = "save";
		alias = new String[]{"s"};
		argLength = 2; //counting cmdName

	}

	@Override
	public boolean run() {
			String worldName = args[1].toLowerCase();
	        Location respawn = SkyWarsReloaded.getCfg().getSpawn();
			for (World world: SkyWarsReloaded.get().getServer().getWorlds()) {
				if (world.getName().equalsIgnoreCase(worldName)) {
					World editWorld = SkyWarsReloaded.get().getServer().getWorld(worldName);
					for (Player player: editWorld.getPlayers()) {
						player.teleport(respawn, TeleportCause.PLUGIN);
					}					
					editWorld.save();
					SkyWarsReloaded.getWM().unloadWorld(worldName);
					File dataDirectory = new File (SkyWarsReloaded.get().getDataFolder(), "maps");
					File target = new File (dataDirectory, worldName);
					SkyWarsReloaded.getWM().deleteWorld(target);
					File source = new File (SkyWarsReloaded.get().getServer().getWorldContainer().getAbsolutePath(), worldName);
					SkyWarsReloaded.getWM().copyWorld(source, target);
					SkyWarsReloaded.getWM().deleteWorld(source);
					sender.sendMessage(new Messaging.MessageFormatter().setVariable("mapname", worldName).format("maps.saved"));
					sender.sendMessage(new Messaging.MessageFormatter().format("maps.register-reminder"));
					return true;
				} 	
			}
			sender.sendMessage(new Messaging.MessageFormatter().setVariable("mapname", worldName).format("error.map-not-in-edit"));
			return false;
	}
}
