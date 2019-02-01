package com.walrusone.skywarsreloaded.nms.v1_13_R2;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.server.v1_13_R2.*;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.craftbukkit.v1_13_R2.CraftServer;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftFallingBlock;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
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
		((CraftServer)Bukkit.getServer()).getHandle().moveToWorld(((CraftPlayer)player).getHandle(), ((CraftPlayer)player).getHandle().dimension, false);
	}
	
	public void sendParticles(World world, String type, float x, float y, float z, float offsetX, float offsetY, float offsetZ, float data, int amount) {
		Particle particle = Particle.valueOf(type);
	    for (Player player: world.getPlayers()) {
		    player.spawnParticle(particle, x, y, z, amount, offsetX, offsetY, offsetZ, data);
		}
	}
	
	public FireworkEffect getFireworkEffect(Color one, Color two, Color three, Color four, Color five, Type type) {
		return FireworkEffect.builder().flicker(false).withColor(one, two, three, four).withFade(five).with(type).trail(true).build();
	}
	
	public void sendTitle(Player player, int fadein, int stay, int fadeout, String title, String subtitle) {
		PlayerConnection pConn = ((CraftPlayer) player).getHandle().playerConnection;
		PacketPlayOutTitle pTitleInfo = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, (IChatBaseComponent) null, (int) fadein, (int) stay, (int) fadeout);
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
        return item.getItemMeta().getDisplayName();
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

	@Override
	public void updateSkull(Skull skull, UUID uuid) {
		skull.setType(Material.PLAYER_WALL_HEAD);
		skull.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
	}

	public void setMaxHealth(Player player, int health) {
		player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
	}

	public void spawnDragon(World world, Location loc) {
		WorldServer w = ((CraftWorld) world).getHandle();
		EntityEnderDragon dragon = new EntityEnderDragon(w);
		dragon.getDragonControllerManager().setControllerPhase(DragonControllerPhase.CHARGING_PLAYER);
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
        world.playBlockAction(position, ec.getBlock().getBlock(), 1, open ? 1 : 0);
    }
	
	public void setEntityTarget(Entity ent, Player player) {
		EntityCreature entity = (EntityCreature) ((CraftEntity) ent).getHandle();
		entity.setGoalTarget(((EntityLiving) ((CraftPlayer) player).getHandle()), null, false);
	}

	public void updateSkull(SkullMeta meta1, Player player) {
		meta1.setOwningPlayer(player);
	}

	@Override
	public ChunkGenerator getChunkGenerator() {
		return new ChunkGenerator() {
			@Override
			public final ChunkGenerator.ChunkData generateChunkData(final World world, final Random random, final int x, final int z, final ChunkGenerator.BiomeGrid chunkGererator) {
				final ChunkGenerator.ChunkData chunkData = this.createChunkData(world);
				for (int i = 0; i < 16; i++) {
					for (int j = 0; j < 16; j++) {
						chunkGererator.setBiome(i, j, Biome.THE_VOID);
					}
				}
				return chunkData;
			}
		};
	}

	@Override
	public boolean checkMaterial(FallingBlock fb, Material mat) {
		if (fb.getBlockData().getMaterial().equals(mat)) {
			return true;
		}
		return false;
	}

	@Override
	public Objective getNewObjective(Scoreboard scoreboard, String criteria, String DisplayName) {
		return scoreboard.registerNewObjective(DisplayName, criteria, DisplayName);
	}

	@Override
	public void setGameRule(World world, String rule, String bool) {
		if (rule.equalsIgnoreCase("doMobSpawning")) {
			world.setGameRule(GameRule.DO_MOB_SPAWNING, Boolean.valueOf(bool.toUpperCase()));
		} else if (rule.equalsIgnoreCase("mobGriefing")) {
			world.setGameRule(GameRule.MOB_GRIEFING, Boolean.valueOf(bool.toUpperCase()));
		} else if (rule.equalsIgnoreCase("doFireTick")) {
			world.setGameRule(GameRule.DO_FIRE_TICK, Boolean.valueOf(bool.toUpperCase()));
		} else if (rule.equalsIgnoreCase("showDeathMessages")) {
			world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, Boolean.valueOf(bool.toUpperCase()));
		} else if (rule.equalsIgnoreCase("announceAdvancements")) {
			world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, Boolean.valueOf(bool.toUpperCase()));
		}
	}

	@Override
	public boolean headCheck(Block h1) {
		return (h1.getType() == Material.PLAYER_WALL_HEAD || h1.getType() == Material.SKELETON_SKULL);
	}

	@Override
	public ItemStack getBlankPlayerHead() {
		return new ItemStack(Material.PLAYER_HEAD, 1);
	}

	@Override
	public int getVersion() {
		return 13;
	}

	@Override
	public ItemStack getMaterial(String item) {
		if (item.equalsIgnoreCase("SKULL_ITEM")) {
			return new ItemStack(Material.SKELETON_SKULL, 1);
		} else if (item.equalsIgnoreCase("ENDER_PORTAL_FRAME")) {
			return new ItemStack(Material.END_PORTAL_FRAME, 1);
		} else if (item.equalsIgnoreCase("WORKBENCH")) {
			return new ItemStack(Material.CRAFTING_TABLE, 1);
		} else if (item.equalsIgnoreCase("IRON_FENCE")) {
			return new ItemStack(Material.IRON_BARS, 1);
		} else if (item.equalsIgnoreCase("REDSTONE_COMPARATOR")) {
			return new ItemStack(Material.COMPARATOR);
		} else if (item.equalsIgnoreCase("SIGN_POST")) {
			return new ItemStack(Material.SIGN);
		} else if (item.equalsIgnoreCase("STONE_PLATE")) {
			return new ItemStack(Material.STONE_PRESSURE_PLATE);
		} else if (item.equalsIgnoreCase("IRON_PLATE")) {
			return new ItemStack(Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
		} else if (item.equalsIgnoreCase("GOLD_PLATE")) {
			return new ItemStack(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
		} else if (item.equalsIgnoreCase("MOB_SPAWNER")) {
			return new ItemStack(Material.SPAWNER);
		} else {
			return new ItemStack(Material.AIR, 1);
		}
	}

	@Override
	public ItemStack getColorItem(String mat, byte color) {
		String col = getColorFromByte(color);
		if (mat.equalsIgnoreCase("wool")) {
			return new ItemStack(Material.valueOf(col + "_WOOL"), 1);
		} else if (mat.equalsIgnoreCase("stained_glass")) {
			return new ItemStack(Material.valueOf(col + "_STAINED_GLASS"), 1);
		} else if (mat.equalsIgnoreCase("banner")) {
			return new ItemStack(Material.valueOf(col + "_BANNER"), 1);
		} else {
			return new ItemStack(Material.valueOf(col + "_GLASS"), 1);
		}
	}

	private String getColorFromByte(byte color) {
		switch (color) {
			case 0: return "WHITE";
			case 1: return "ORANGE";
			case 2: return "MAGENTA";
			case 3: return "LIGHT_BLUE";
			case 4: return "YELLOW";
			case 5: return "LIME";
			case 6: return "PINK";
			case 7: return "GRAY";
			case 8: return "LIGHT_GRAY";
			case 9: return "CYAN";
			case 10: return "PURPLE";
			case 11: return "BLUE";
			case 12: return "BROWN";
			case 13: return "GREEN";
			case 14: return "RED";
			case 15: return "BLACK";
			default: return "WHITE";
		}
	}

	@Override
	public void setBlockWithColor(World world, int x, int y, int z, Material mat, byte cByte) {
		world.getBlockAt(x, y, z).setType(mat);
	}

	@Override
	public void deleteCache() {
		RegionFileCache.a();
	}
}
