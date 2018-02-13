package com.walrusone.skywarsreloaded.nms.v1_8_R1;

import java.util.List;
import java.util.UUID;

import net.minecraft.server.v1_8_R1.ChatSerializer;
import net.minecraft.server.v1_8_R1.ChunkCoordIntPair;
import net.minecraft.server.v1_8_R1.EntityPlayer;
import net.minecraft.server.v1_8_R1.EnumParticle;
import net.minecraft.server.v1_8_R1.EnumTitleAction;
import net.minecraft.server.v1_8_R1.IChatBaseComponent;
import net.minecraft.server.v1_8_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R1.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_8_R1.PlayerConnection;
import net.minecraft.server.v1_8_R1.PacketPlayOutChat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Skull;
import org.bukkit.craftbukkit.v1_8_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_8_R1.CraftServer;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.walrusone.skywarsreloaded.api.NMS;

public class NMSHandler implements NMS {
	
	public void respawnPlayer(Player player) {
		((CraftServer)Bukkit.getServer()).getHandle().moveToWorld(((CraftPlayer)player).getHandle(), 0, false);
	}
	
	@SuppressWarnings("unchecked")
	public void updateChunks(World world, List<Chunk> chunks) {
		for (Chunk currentChunk: chunks) {
			net.minecraft.server.v1_8_R1.World mcWorld = ((CraftChunk) currentChunk).getHandle().world;
	        for (EntityPlayer ep : (List<EntityPlayer>) mcWorld.players) {
	                ep.chunkCoordIntPairQueue.add(new ChunkCoordIntPair(currentChunk.getX(), currentChunk.getZ()));
	        }
		}
	}
	
	public void sendParticles(World world, String type, float x, float y, float z, float offsetX, float offsetY, float offsetZ, float data, int amount) {
		EnumParticle particle = EnumParticle.valueOf(type);
		PacketPlayOutWorldParticles particles = new PacketPlayOutWorldParticles(particle, false, x, y, z, offsetX, offsetY, offsetZ, data, amount, 1);
		for (Player player: world.getPlayers()) {
			CraftPlayer start = (CraftPlayer) player; //Replace player with your player.
			EntityPlayer target = start.getHandle();
			PlayerConnection connect = target.playerConnection;
			connect.sendPacket(particles);
		}
	}

	public FireworkEffect getFireworkEffect(Color one, Color two, Color three, Color four, Color five, Type type) {
		return FireworkEffect.builder().flicker(false).withColor(one, two, three, four).withFade(five).with(type).trail(true).build();
	}
	
	public void sendTitle(Player player, int fadein, int stay, int fadeout, String title, String subtitle) {
		PlayerConnection pConn = ((CraftPlayer) player).getHandle().playerConnection;
		PacketPlayOutTitle pTitleInfo = new PacketPlayOutTitle(EnumTitleAction.TIMES, (IChatBaseComponent) null, (int) fadein, (int) stay, (int) fadeout);
		pConn.sendPacket(pTitleInfo);
		if (subtitle != null) {
			subtitle = subtitle.replaceAll("%player%", player.getDisplayName());
			subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
			IChatBaseComponent iComp = ChatSerializer.a("{\"text\": \"" + subtitle + "\"}");
			PacketPlayOutTitle pSubtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, iComp);
			pConn.sendPacket(pSubtitle);
		}
		if (title != null) {
			title = title.replaceAll("%player%", player.getDisplayName());
			title = ChatColor.translateAlternateColorCodes('&', title);
			IChatBaseComponent iComp = ChatSerializer.a("{\"text\": \"" + title + "\"}");
			PacketPlayOutTitle pTitle = new PacketPlayOutTitle(EnumTitleAction.TITLE, iComp);
			pConn.sendPacket(pTitle);
		}
	}
	
	public boolean isOnePointEight() {
		return true;
	} 
	
	public void playGameSound(Location loc, String sound, float volume, float pitch, boolean customSound) {
		if (customSound) {
		} else {
			loc.getWorld().playSound(loc, Sound.valueOf(sound), volume, pitch);
		}
	}

	public void sendActionBar(Player p, String msg) {
        String s = ChatColor.translateAlternateColorCodes('&', msg);
        IChatBaseComponent icbc = ChatSerializer.a("{\"text\": \"" + s + "\"}");
        PacketPlayOutChat bar = new PacketPlayOutChat(icbc, (byte)2);
        ((CraftPlayer)p).getHandle().playerConnection.sendPacket(bar);		
	}

	public String getItemName(ItemStack item) {
		return CraftItemStack.asNMSCopy(item).getName();
	}
	
	public ItemStack getMainHandItem(Player player) {
        return player.getInventory().getItemInHand();
	}
	
	public ItemStack getOffHandItem(Player player) {
		return null;
	}
	
	public ItemStack getItemStack(Material material, List<String> lore, String message) {
    	ItemStack addItem = new ItemStack(material, 1);
        ItemMeta addItemMeta = addItem.getItemMeta();
        addItemMeta.setDisplayName(message);
        addItemMeta.setLore(lore);
        addItem.setItemMeta(addItemMeta);
        return addItem;
	}
	
	@Override
	public ItemStack getItemStack(ItemStack item, List<String> lore, String message) {
		ItemStack addItem = item.clone();
        ItemMeta addItemMeta = addItem.getItemMeta();
        addItemMeta.setDisplayName(message);
        addItemMeta.setLore(lore);
        addItem.setItemMeta(addItemMeta);
        return addItem;
	}

	@Override
	public boolean isValueParticle(String string) {
		return true;
	}
	
	@Override 
	public void updateSkull(Skull skull, UUID uuid) {
		skull.setOwner(Bukkit.getOfflinePlayer(uuid).getName());
	}
}
