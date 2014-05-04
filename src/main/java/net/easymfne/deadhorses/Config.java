/*
 * This file is part of the DeadHorses plugin by EasyMFnE.
 * 
 * DeadHorses is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or any later version.
 * 
 * DeadHorses is distributed in the hope that it will be useful, but without any
 * warranty; without even the implied warranty of merchantability or fitness for
 * a particular purpose. See the GNU General Public License for details.
 * 
 * You should have received a copy of the GNU General Public License v3 along
 * with DeadHorses. If not, see <http://www.gnu.org/licenses/>.
 */
package net.easymfne.deadhorses;

import org.bukkit.entity.Horse;
import org.bukkit.inventory.ItemStack;

/**
 * Configuration helper class, with methods for accessing the configuration.
 * 
 * @author Eric Hildebrand
 */
public class Config {
    
    private DeadHorses plugin = null;
    
    /**
     * Instantiate the class and give it a reference back to the plugin itself.
     * 
     * @param plugin
     *            The DeadHorses plugin
     */
    public Config(DeadHorses plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Examine an itemstack and determine the probability that a given horse
     * variant will become tamed upon consuming one item.
     * 
     * @param variant
     *            The type of Horse
     * @param item
     *            The ItemStack that may be edible
     * @return The chance of becoming tamed
     */
    public double getFoodChance(Horse.Variant variant, ItemStack item) {
        String node = variant.name().toLowerCase() + "."
                + item.getType().name().toLowerCase();
        /* Check to see if the food is defined without a data value */
        if (plugin.getConfig().contains(node)) {
            return plugin.getConfig().getDouble(node, 0.0);
        }
        /* Check to see if the food is defined with a data value */
        if (plugin.getConfig().contains(node + ":" + item.getDurability())) {
            return plugin.getConfig().getDouble(
                    node + ":" + item.getDurability(), 0.0);
        }
        return 0.0;
    }
    
    /**
     * Examine an itemstack and determine if a specific horse Variant can eat it
     * 
     * @param variant
     *            The type of Horse
     * @param item
     *            The ItemStack that may be edible
     * @return Whether the Horse can eat the item or not
     */
    public boolean isFood(Horse.Variant variant, ItemStack item) {
        String node = variant.name().toLowerCase() + "."
                + item.getType().name().toLowerCase();
        /* Check to see if the food is defined without a data value */
        if (plugin.getConfig().contains(node)
                && plugin.getConfig().getDouble(node, 0.0) > 0.0) {
            return true;
        }
        /* Check to see if the food is defined with a data value */
        if (plugin.getConfig().contains(node + ":" + item.getDurability())
                && plugin.getConfig().getDouble(
                        node + ":" + item.getDurability(), 0.0) > 0.0) {
            return true;
        }
        return false;
    }
    
    /**
     * @return Whether users can make baby dead horses age by feeding them.
     */
    public boolean isFoodAgingEnabled() {
        return plugin.getConfig().getBoolean("feeding-can-age", false);
    }
    
    /**
     * @return Whether users can attempt to tame dead horses by feeding them.
     */
    public boolean isFoodTamingEnabled() {
        return plugin.getConfig().getBoolean("taming.food-based", false);
    }
    
    /**
     * @return Whether users can attach leashes to dead horses.
     */
    public boolean isLeashingEnabled() {
        return plugin.getConfig().getBoolean("leashing", false);
    }
    
    /**
     * @return Whether users can attempt to tame dead horses by riding them,
     *         like in vanilla Minecraft.
     */
    public boolean isVanillaTamingEnabled() {
        return plugin.getConfig().getBoolean("taming.vanilla-like", false);
    }
    
}
