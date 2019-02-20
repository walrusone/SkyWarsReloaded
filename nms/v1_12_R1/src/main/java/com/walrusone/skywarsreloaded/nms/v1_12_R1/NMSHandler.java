package com.walrusone.skywarsreloaded.nms.v1_12_R1;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftFallingBlock;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.entity.Entity;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;

import com.walrusone.skywarsreloaded.api.NMS;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class NMSHandler implements NMS {
	
	public void respawnPlayer(Player player) {
		((CraftServer)Bukkit.getServer()).getHandle().moveToWorld(((CraftPlayer)player).getHandle(), 0, false);
	}
	
	public void sendParticles(World world, String type, float x, float y, float z, float offsetX, float offsetY, float offsetZ, float data, int amount) {
		EnumParticle particle = EnumParticle.valueOf(type);
		PacketPlayOutWorldParticles particles = new PacketPlayOutWorldParticles(particle, true, x, y, z, offsetX, offsetY, offsetZ, data, amount, 1);
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
		PacketPlayOutTitle pTitleInfo = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, null, fadein, stay, fadeout);
		pConn.sendPacket(pTitleInfo);
		if (subtitle != null) {
			subtitle = subtitle.replaceAll("%player%", player.getDisplayName());
			subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
			IChatBaseComponent iComp = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + subtitle + "\"}");
			PacketPlayOutTitle pSubtitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, iComp);
			pConn.sendPacket(pSubtitle);
		}
		if (title != null) {
			title = title.replaceAll("%player%", player.getDisplayName());
			title = ChatColor.translateAlternateColorCodes('&', title);
			IChatBaseComponent iComp = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + title + "\"}");
			PacketPlayOutTitle pTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, iComp);
			pConn.sendPacket(pTitle);
		}
	}
	
    public void sendActionBar(Player p, String msg) {
        String s = ChatColor.translateAlternateColorCodes('&', msg);
        IChatBaseComponent icbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + s + "\"}");
        PacketPlayOutChat bar = new PacketPlayOutChat(icbc);
        ((CraftPlayer)p).getHandle().playerConnection.sendPacket(bar);
    }
    
	public String getItemName(ItemStack item){
        return CraftItemStack.asNMSCopy(item).getName();
    }

	public void playGameSound(Location loc, String sound, float volume, float pitch, boolean customSound) {
		if (customSound) {
			loc.getWorld().playSound(loc, sound, volume, pitch);
		} else {
			loc.getWorld().playSound(loc, Sound.valueOf(sound), volume, pitch);
		}
	} 
	
	public ItemStack getMainHandItem(Player player) {
        return player.getInventory().getItemInMainHand();

	}
	
	public ItemStack getOffHandItem(Player player) {
		return player.getInventory().getItemInOffHand();
	}

	public ItemStack getItemStack(Material material, List<String> lore, String message) {
    	ItemStack addItem = new ItemStack(material, 1);
        ItemMeta addItemMeta = addItem.getItemMeta();
        addItemMeta.setDisplayName(message);
        addItemMeta.setLore(lore);
        addItemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        addItemMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        addItemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        addItemMeta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        addItemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        addItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        addItem.setItemMeta(addItemMeta);
        return addItem;
	}

	public ItemStack getItemStack(ItemStack item, List<String> lore, String message) {
		ItemStack addItem = item.clone();
        ItemMeta addItemMeta = addItem.getItemMeta();
        addItemMeta.setDisplayName(message);
        addItemMeta.setLore(lore);
        addItemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        addItemMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        addItemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        addItemMeta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        addItemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        addItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        addItem.setItemMeta(addItemMeta);
        return addItem;
	}	
	
	public boolean isValueParticle(String string) {
		try {
			Particle.valueOf(string);
		} catch (IllegalArgumentException e) {
			return false;
		}
		return true;
	}
	
	public void updateSkull(Skull skull, UUID uuid) {
		skull.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
	}
	
	public void setMaxHealth(Player player, int health) {
		player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
	}

	public void spawnDragon(World world, Location loc) {
		WorldServer w = ((CraftWorld) world).getHandle();
		EntityEnderDragon dragon = new EntityEnderDragon(w);
		dragon.getDragonControllerManager().setControllerPhase(DragonControllerPhase.c);
		dragon.setLocation(loc.getX(), loc.getY(), loc.getZ(), w.random.nextFloat() * 360.0F, 0.0F);
		w.addEntity(dragon);
	}
	
	public Entity spawnFallingBlock(Location loc, Material mat, boolean damage) {
		FallingBlock block = loc.getWorld().spawnFallingBlock(loc, new MaterialData(mat));
		block.setDropItem(false);
		EntityFallingBlock fb = ((CraftFallingBlock) block).getHandle();
		fb.a(damage);
		return block;
	}
	
	public void playEnderChestAction(Block block, boolean open) {
        Location location = block.getLocation();
        WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
        BlockPosition position = new BlockPosition(location.getX(), location.getY(), location.getZ());
        TileEntityEnderChest ec = (TileEntityEnderChest) world.getTileEntity(position);
		if (ec != null) {
			world.playBlockAction(position, ec.getBlock(), 1, open ? 1 : 0);
		}
	}
	
	public void setEntityTarget(Entity ent, Player player) {
		EntityCreature entity = (EntityCreature) ((CraftEntity) ent).getHandle();
		entity.setGoalTarget(((CraftPlayer) player).getHandle());
	}

	@SuppressWarnings("deprecation")
	public void updateSkull(SkullMeta meta1, Player player) {meta1.setOwner(player.getName());
	}

	@Override
	public ChunkGenerator getChunkGenerator() {
		return new ChunkGenerator() {
			@Override
			public final ChunkGenerator.ChunkData generateChunkData(final World world, final Random random, final int x, final int z, final ChunkGenerator.BiomeGrid chunkGererator) {
				final ChunkGenerator.ChunkData chunkData = this.createChunkData(world);
				for (int i = 0; i < 16; i++) {
					for (int j = 0; j < 16; j++) {
						chunkGererator.setBiome(i, j, Biome.VOID);
					}
				}
				return chunkData;
			}
		};
	}

	@Override
	public boolean checkMaterial(FallingBlock fb, Material mat) {
		return fb.getMaterial().equals(mat);
	}

	@Override
	public Objective getNewObjective(Scoreboard scoreboard, String criteria, String DisplayName) {
		return scoreboard.registerNewObjective(DisplayName, criteria);
	}

	@Override
	public void setGameRule(World world, String rule, String bool) {world.setGameRuleValue(rule, bool);
	}

	@Override
	public boolean headCheck(Block h1) {
		return h1.getType() == Material.SKULL;
	}

	@Override
	public ItemStack getBlankPlayerHead() {
		return new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
	}

	@Override
	public int getVersion() {
		return 12;
	}

	@Override
	public ItemStack getMaterial(String item) {
		if (item.equalsIgnoreCase("SKULL_ITEM")) {
			return new ItemStack(Material.SKULL_ITEM, 1, (short) 1);
		} else {
			return new ItemStack(Material.valueOf(item), 1);
		}
	}

	@Override
	public ItemStack getColorItem(String mat, byte color) {
		if (mat.equalsIgnoreCase("wool")) {
			return new ItemStack(Material.WOOL, 1, (short) color);
		} else if (mat.equalsIgnoreCase("glass")) {
			return new ItemStack(Material.STAINED_GLASS, 1, (short) color);
		} else if (mat.equalsIgnoreCase("banner")) {
			return new ItemStack(Material.BANNER, 1, (short) color);
		} else {
			return new ItemStack(Material.STAINED_GLASS, 1, (short) color);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setBlockWithColor(World world, int x, int y, int z, Material mat, byte cByte) {
		world.getBlockAt(x, y, z).setType(mat);
		world.getBlockAt(x, y, z).setData(cByte);
	}

	@Override
	public void deleteCache() {
		RegionFileCache.a();
	}
}
