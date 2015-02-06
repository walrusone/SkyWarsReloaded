package com.walrusone.skywars.utilities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.walrusone.skywars.SkyWarsReloaded;

public class SaveDefaultMaps {

	public static void saveDefaultMaps() {
		ArrayList<String> maps = new ArrayList<String>();
        maps.add("lobby.zip");
        maps.add("caves.zip");
        maps.add("hotandcold.zip");
        maps.add("mushrooms.zip");
        maps.add("nightmare.zip");
        maps.add("planetarium.zip");
        for (String map: maps) {
        	SkyWarsReloaded.get().saveResource(map, true);
            String input = new File(SkyWarsReloaded.get().getDataFolder(), map).toString();
            String output = new File(SkyWarsReloaded.get().getDataFolder(), "maps").toString();
            UnZip unzipper = new UnZip();
            try {
    			unzipper.unzip(input, output);
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
            File delete = new File(input);
            delete.delete();
        }
	}
}
