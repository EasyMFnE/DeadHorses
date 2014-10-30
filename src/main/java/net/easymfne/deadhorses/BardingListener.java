/*
 * This file is part of the DeadHorses plugin by EasyMFnE.
 * 
 * DeadHorses is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * 
 * DeadHorses is distributed in the hope that it will be useful, but without any warranty; without
 * even the implied warranty of merchantability or fitness for a particular purpose. See the GNU
 * General Public License for details.
 * 
 * You should have received a copy of the GNU General Public License v3 along with DeadHorses. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package net.easymfne.deadhorses;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * The class that monitors and reacts to horse inventory events.
 * 
 * @author Eric Hildebrand
 */
public class BardingListener implements Listener {

  public static final double DIAMOND_BARDING_MODIFIER = 0.56;
  public static final double GOLD_BARDING_MODIFIER = 0.72;
  public static final double IRON_BARDING_MODIFIER = 0.8;
  public static final double NO_BARDING_MODIFIER = 1.0;

  private DeadHorses plugin = null;

  /**
   * Instantiate by getting a reference to the plugin instance and registering each of the defined
   * EventHandlers.
   * 
   * @param plugin Reference to DeadHorses plugin instance
   */
  public BardingListener(DeadHorses plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  /**
   * Unregister all registered EventHandlers, preventing further reactions.
   */
  public void close() {
    HandlerList.unregisterAll(this);
  }

  /**
   * @param entity The Horse
   * @return Whether or not the Horse is a Skeleton or Undead.
   */
  private static boolean isDeadHorse(Entity entity) {
    return entity instanceof Horse
        && (((Horse) entity).getVariant() == Horse.Variant.SKELETON_HORSE || ((Horse) entity)
            .getVariant() == Horse.Variant.UNDEAD_HORSE);
  }

  private boolean isBarding(ItemStack item) {
    return item != null
        && (item.getType() == Material.IRON_BARDING || item.getType() == Material.GOLD_BARDING || item
            .getType() == Material.DIAMOND_BARDING);
  }

  private double getBardingModifier(ItemStack item) {
    if (item != null) {
      switch (item.getType()) {
        case DIAMOND_BARDING:
          return DIAMOND_BARDING_MODIFIER;
        case GOLD_BARDING:
          return GOLD_BARDING_MODIFIER;
        case IRON_BARDING:
          return IRON_BARDING_MODIFIER;
        default:
      }
    }
    return NO_BARDING_MODIFIER;
  }

  private boolean isEmpty(ItemStack item) {
    return item == null || item.getType() == Material.AIR;
  }

  @EventHandler(ignoreCancelled = true)
  public void onDeadHorseDamage(EntityDamageEvent event) {
    if (isDeadHorse(event.getEntity())
        && isBarding(((Horse) event.getEntity()).getInventory().getArmor())) {
      Horse horse = (Horse) event.getEntity();
      event.setDamage(event.getDamage() * getBardingModifier(horse.getInventory().getArmor()));
    }
  }

  @SuppressWarnings("deprecation") // TODO: Remove suppression... never?
  @EventHandler
  public void onDeadHorseInventoryClick(InventoryClickEvent event) {
    InventoryHolder topHolder = event.getView().getTopInventory().getHolder();
    if (topHolder instanceof Horse && isDeadHorse((Horse) topHolder)) {
      Horse horse = (Horse) topHolder;
      if (!plugin.getPluginConfig().isArmorEquippable(horse.getVariant())) {
        return;
      }
      boolean needUpdate = false;

      // Shift-click Barding item in bottom inventory.
      if (event.getClick().isShiftClick() && isBarding(event.getCurrentItem())
          && isEmpty(horse.getInventory().getArmor())) {
        plugin.fancyLog("Shift-click barding in inventory.");
        event.setCancelled(true);
        horse.getInventory().setArmor(event.getCurrentItem());
        event.setCurrentItem(new ItemStack(Material.AIR));
        needUpdate = true;
      }
      // Click empty horse armor slot with cursor holding Barding
      else if (event.getInventory() == event.getView().getTopInventory() && event.getRawSlot() == 1
          && isEmpty(event.getCurrentItem()) && isBarding(event.getCursor())) {
        plugin.fancyLog("Click empty armor slot while holding barding.");
        event.setCancelled(true);
        horse.getInventory().setArmor(event.getCursor());
        event.setCursor(new ItemStack(Material.AIR));
        needUpdate = true;
      }

      if (needUpdate) {
        for (HumanEntity viewer : event.getViewers()) {
          if (viewer.getType() == EntityType.PLAYER) {
            ((Player) viewer).updateInventory();
          }
        }
      }
    }
  }

}
