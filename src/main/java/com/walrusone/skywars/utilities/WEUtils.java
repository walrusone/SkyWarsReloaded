package com.walrusone.skywars.utilities;

import org.bukkit.Location;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;

@SuppressWarnings("deprecation")
public class WEUtils {

	public static boolean pasteSchematic(Location origin, CuboidClipboard schematic) {
        EditSession editSession = new EditSession(new BukkitWorld(origin.getWorld()), Integer.MAX_VALUE);
        editSession.setFastMode(true);

        try {
            schematic.paste(editSession, new Vector(origin.getBlockX(), origin.getBlockY(), origin.getBlockZ()), true);
        } catch (MaxChangedBlocksException ignored) {
            return false;
        }

        return true;
    }
}
