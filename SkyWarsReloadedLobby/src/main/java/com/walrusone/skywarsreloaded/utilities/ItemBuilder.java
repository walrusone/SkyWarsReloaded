package com.walrusone.skywarsreloaded.utilities;

import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

public class ItemBuilder
{
    CustomEnchantment ench;
    ItemStack item;
    String name;
    List<String> lore;
    public boolean glow;
    
    public ItemBuilder(final ItemStack a4, final String v1, final List<String> v2, final boolean v3) {
        this.ench = new CustomEnchantment(69);
        this.item = ((a4 == null) ? null : a4.clone());
        this.name = v1.replaceAll("&", "§");
        final ArrayList<String> v4 = new ArrayList<String>();
        if (v2 != null) {
            for (final String a6 : v2) {
                v4.add(a6.replaceAll("&", "§"));
            }
        }
        this.lore = v4;
        this.glow = v3;
    }
    
    public ItemBuilder(final ItemStack a1, final String a2) {
        this(a1, a2, null, false);
    }
    
    public ItemBuilder(final ItemStack a1, final String a2, final List<String> a3) {
        this(a1, a2, a3, false);
    }
    
    public ItemBuilder(final ItemStack a1, final String a2, final String a3) {
        this(a1, a2, a3.contains("\n") ? Arrays.<String>asList(a3.split("\n")) : Arrays.<String>asList(a3), false);
    }
    
    public ItemBuilder() {
        this(null, "");
    }
    
    public void addLore(final String a1) {
        (this.lore = new ArrayList<String>(this.lore)).add(/*EL:50*/a1);
    }
    
    public ItemStack buildHead(final String a1) {
        this.item = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
        final SkullMeta v1 = (SkullMeta)this.item.getItemMeta();
        v1.setOwner(a1);
        this.item.setItemMeta((ItemMeta)v1);
        return this.build();
    }
    
    public ItemStack build() {
        final ItemMeta v1 = this.item.getItemMeta();
        if (this.name != null) {
            v1.setDisplayName(this.name);
        }
        if (this.lore != null) {
            v1.setLore(this.lore);
        }
        if (this.glow) {
            v1.addEnchant((Enchantment)this.ench, 1, true);
        }
        this.item.setItemMeta(v1);
        return this.item;
    }
    
    public ItemStack buildEnchant(final Enchantment a1, final int a2) {
        this.item.addUnsafeEnchantment(a1, a2);
        return this.build();
    }
    
    public class CustomEnchantment extends Enchantment
    {
        public CustomEnchantment(final int a1) {
            super(a1);
        }
        
        public boolean canEnchantItem(final ItemStack a1) {
            return true;
        }
        
        public boolean conflictsWith(final Enchantment a1) {
            return false;
        }
        
        public EnchantmentTarget getItemTarget() {
            return null;
        }
        
        public int getMaxLevel() {
            /*SL:102*/return 1;
        }
        
        public String getName() {
            /*SL:107*/return " ";
        }
        
        public int getId() {
            /*SL:113*/return 69;
        }
        
        public int getStartLevel() {
            /*SL:118*/return 1;
        }
    }
}
