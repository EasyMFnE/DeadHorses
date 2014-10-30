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
import org.bukkit.event.Event.Result;
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

  private static final int BARDING_RAW_INDEX = 1;

  private static final double DIAMOND_BARDING_MODIFIER = 0.56;
  private static final double GOLD_BARDING_MODIFIER = 0.72;
  private static final double IRON_BARDING_MODIFIER = 0.8;
  private static final double NO_BARDING_MODIFIER = 1.0;

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
   * @param item The barding
   * @return The damage multiplier/modifier
   */
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

  /**
   * @param item The item stack
   * @return Whether the item stack is horse barding
   */
  private boolean isBarding(ItemStack item) {
    return item != null
        && (item.getType() == Material.IRON_BARDING || item.getType() == Material.GOLD_BARDING || item
            .getType() == Material.DIAMOND_BARDING);
  }

  /**
   * @param entity The Horse
   * @return Whether or not the Horse is a Skeleton or Undead.
   */
  private boolean isDeadHorse(Entity entity) {
    return entity instanceof Horse
        && (((Horse) entity).getVariant() == Horse.Variant.SKELETON_HORSE || ((Horse) entity)
            .getVariant() == Horse.Variant.UNDEAD_HORSE);
  }

  /**
   * @param item An item stack
   * @return Whether the item stack is empty (null or air)
   */
  private boolean isEmpty(ItemStack item) {
    return item == null || item.getType() == Material.AIR;
  }

  /**
   * Monitor entity damage events for horses taking damage. Modify the damage based on barding.
   * 
   * @param event The damage event
   */
  @EventHandler(ignoreCancelled = true)
  public void onDeadHorseDamage(EntityDamageEvent event) {
    if (isDeadHorse(event.getEntity())
        && isBarding(((Horse) event.getEntity()).getInventory().getArmor())) {
      Horse horse = (Horse) event.getEntity();
      event.setDamage(event.getDamage() * getBardingModifier(horse.getInventory().getArmor()));
    }
  }

  /**
   * Monitor inventory clicking to allow (un)equipping of barding at the horse inventory screen.
   * 
   * @param event The click event
   */
  @SuppressWarnings("deprecation")
  @EventHandler
  public void onDeadHorseInventoryClick(InventoryClickEvent event) {
    InventoryHolder topHolder = event.getView().getTopInventory().getHolder();
    if (topHolder instanceof Horse && isDeadHorse((Horse) topHolder)) {
      Horse horse = (Horse) topHolder;
      if (!plugin.getPluginConfig().isArmorEquippable(horse.getVariant())) {
        return;
      }
      switch (event.getAction()) {
        case PLACE_ALL:
        case PLACE_ONE:
        case PLACE_SOME:
          if (event.getRawSlot() == BARDING_RAW_INDEX && event.getResult() != Result.DENY) {
            if (isEmpty(event.getCurrentItem()) && isBarding(event.getCursor())) {
              /* Placing held barding into horse armor slot: swap cursor and slot. */
              event.setCancelled(true);
              event.setCurrentItem(event.getCursor());
              event.setCursor(new ItemStack(Material.AIR, 0));
              updateInventories(horse, event);
            }
          }
          return;
        case PICKUP_ALL:
        case PICKUP_HALF:
        case PICKUP_ONE:
        case PICKUP_SOME:
        case DROP_ALL_SLOT:
        case DROP_ONE_SLOT:
          if (event.getRawSlot() == BARDING_RAW_INDEX && event.getResult() != Result.DENY
              && isEmpty(event.getCursor()) && !isEmpty(event.getCurrentItem())) {
            /* Dropping barding from horse armor slot: trigger horse metadata packet. */
            updateHorseAfter(horse);
          }
          return;
        case MOVE_TO_OTHER_INVENTORY:
          if (event.getRawSlot() == BARDING_RAW_INDEX && event.getResult() != Result.DENY
              && !isEmpty(event.getCurrentItem())) {
            /* Removing barding from horse via shift click: trigger horse metadata packet. */
            updateHorseAfter(horse);
          } else if (isBarding(event.getCurrentItem()) && event.getResult() != Result.DENY
              && isEmpty(horse.getInventory().getArmor())) {
            /* Equpping barding to horse armor slot via shift click: trigger horse metadata packet. */
            event.setCancelled(true);
            horse.getInventory().setArmor(event.getCurrentItem());
            event.setCurrentItem(new ItemStack(Material.AIR, 0));
            updateInventories(horse, event);
          }
          return;
        case SWAP_WITH_CURSOR:
        case NOTHING:
          if (event.getRawSlot() == BARDING_RAW_INDEX && event.getResult() != Result.DENY
              && isBarding(event.getCursor())) {
            /* Switching held barding with the equipped barding.: swap itemstacks. */
            event.setCancelled(true);
            ItemStack temp = event.getCurrentItem();
            event.setCurrentItem(event.getCursor());
            event.setCursor(temp);
            updateInventories(horse, event);
          }
          return;
        case DROP_ALL_CURSOR:
        case CLONE_STACK:
        case COLLECT_TO_CURSOR:
        case DROP_ONE_CURSOR:
        case HOTBAR_MOVE_AND_READD:
        case HOTBAR_SWAP:
        case UNKNOWN:
        default:
          return;
      }
    }
  }

  /**
   * Cause an updated horse metadata packet to be sent after the inventory has been modified.
   * 
   * @param horse The horse to update
   */
  private void updateHorseAfter(final Horse horse) {
    plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
      @Override
      public void run() {
        plugin.sendMetadataUpdatePacket(horse);
      }
    });
  }

  /**
   * Update the inventories of all inventory viewwers and update the horse metadata afterwards.
   * 
   * @param horse The horse
   * @param event The click event
   */
  @SuppressWarnings("deprecation")
  private void updateInventories(Horse horse, InventoryClickEvent event) {
    updateHorseAfter(horse);
    for (HumanEntity viewer : event.getViewers()) {
      if (viewer.getType() == EntityType.PLAYER) {
        ((Player) viewer).updateInventory();
      }
    }
  }

}
