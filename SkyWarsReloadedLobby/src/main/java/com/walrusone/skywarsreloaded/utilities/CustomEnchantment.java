package com.walrusone.skywarsreloaded.utilities;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

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
