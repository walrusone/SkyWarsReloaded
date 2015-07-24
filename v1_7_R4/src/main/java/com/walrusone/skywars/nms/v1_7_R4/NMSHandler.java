package com.walrusone.skywars.nms.v1_7_R4;

import java.util.List;

import net.minecraft.server.v1_7_R4.ChunkCoordIntPair;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_7_R4.PlayerConnection;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.World;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.craftbukkit.v1_7_R4.CraftChunk;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.walrusone.skywars.api.NMS;

public class NMSHandler implements NMS {
	
	public void respawnPlayer(Player player) {
		((CraftServer)Bukkit.getServer()).getHandle().moveToWorld(((CraftPlayer)player).getHandle(), 0, false);
	}
	
	@SuppressWarnings("unchecked")
	public void updateChunks(World world, List<Chunk> chunks) {
		for (Chunk currentChunk: chunks) {
			net.minecraft.server.v1_7_R4.World mcWorld = ((CraftChunk) currentChunk).getHandle().world;
	        for (EntityPlayer ep : (List<EntityPlayer>) mcWorld.players) {
	                ep.chunkCoordIntPairQueue.add(new ChunkCoordIntPair(currentChunk.getX(), currentChunk.getZ()));
	        }
		}
	}
	
	public void sendParticles(World world, String type, float x, float y, float z, float offsetX, float offsetY, float offsetZ, float data, int amount) {
		PacketPlayOutWorldParticles particles = new PacketPlayOutWorldParticles(getEffect(type), x, y, z, offsetX, offsetY, offsetZ, data, amount);
		for (Player player: world.getPlayers()) {
			CraftPlayer start = (CraftPlayer) player; //Replace player with your player.
			EntityPlayer target = start.getHandle();
			PlayerConnection connect = target.playerConnection;
			connect.sendPacket(particles);
		}
	}
	
	public String getName(ItemStack stack) {
		return CraftItemStack.asNMSCopy(stack).getName();
	}
	
	public FireworkEffect getFireworkEffect(Color one, Color two, Color three, Color four, Color five, Type type) {
		return FireworkEffect.builder().flicker(false).withColor(one, two, three, four).withFade(five).with(type).trail(true).build();
	}
	
	public void sendTitle(Player player, int fadein, int stay, int fadeout, String title, String subtitle) {
	}
	
	public String getEffect(String effect) {
		if (effect.equalsIgnoreCase("HUGE_EXPLOSION")) {
			return "hugeexplosion";
		} else if (effect.equalsIgnoreCase("FIREWORKS_SPARK")) {
			return "fireworksSpark";
		} else if (effect.equalsIgnoreCase("BUBBLE")) {
			return "bubble";
		} else if (effect.equalsIgnoreCase("SUSPEND")) {
			return "suspend";
		} else if (effect.equalsIgnoreCase("DEPTH_SUSPEND")) {
			return "depthSuspend";
		} else if (effect.equalsIgnoreCase("TOWN_AURA")) {
			return "townaura";
		} else if (effect.equalsIgnoreCase("CRIT")) {
			return "crit";
		} else if (effect.equalsIgnoreCase("MAGIC_CRIT")) {
			return "magicCrit";
		} else if (effect.equalsIgnoreCase("MOB_SPELL")) {
			return "mobSpell";
		} else if (effect.equalsIgnoreCase("SPELL")) {
			return "spell";
		} else if (effect.equalsIgnoreCase("SPELL_INSTANT")) {
			return "instantSpell";
		} else if (effect.equalsIgnoreCase("SPELL_WITCH")) {
			return "witchMagic";
		} else if (effect.equalsIgnoreCase("NOTE")) {
			return "note";
		} else if (effect.equalsIgnoreCase("PORTAL")) {
			return "portal";
		} else if (effect.equalsIgnoreCase("ENCHANTMENT_TABLE")) {
			return "enchantmenttable";
		} else if (effect.equalsIgnoreCase("EXPLODE")) {
			return "explode";
		} else if (effect.equalsIgnoreCase("FLAME")) {
			return "flame";
		} else if (effect.equalsIgnoreCase("LAVA")) {
			return "lava";
		} else if (effect.equalsIgnoreCase("FOOTSTEP")) {
			return "footstep";
		} else if (effect.equalsIgnoreCase("WATER_SPLASH")) {
			return "splash";
		} else if (effect.equalsIgnoreCase("SMOKE_LARGE")) {
			return "largesmoke";
		} else if (effect.equalsIgnoreCase("CLOUD")) {
			return "cloud";
		} else if (effect.equalsIgnoreCase("REDSTONE")) {
			return "reddust";
		} else if (effect.equalsIgnoreCase("SNOWBALL_POOF")) {
			return"snowballpoof";
		} else if (effect.equalsIgnoreCase("DRIP_WATER")) {
			return "dripWater";
		}  else if (effect.equalsIgnoreCase("DRIP_LAVA")) {
			return "dripLava";
		} else if (effect.equalsIgnoreCase("SNOWBALL")) {
			return "snowshovel";
		} else if (effect.equalsIgnoreCase("SLIME")) {
			return "slime";
		} else if (effect.equalsIgnoreCase("HEART")) {
			return "heart";
		} else if (effect.equalsIgnoreCase("VILLAGER_ANGRY")) {
			return "angryVillager";
		} else if (effect.equalsIgnoreCase("VILLAGER_HAPPY")) {
			return "happerVillager";
		} else {
			return "fireworksSpark";
		}
	}
	
	public boolean isOnePointSeven() {
		return true;
	}
    
}
