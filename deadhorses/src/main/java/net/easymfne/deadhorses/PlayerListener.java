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

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;
import net.easymfne.deadhorses.AbstractEffects;

/**
 * The class that monitors and reacts to server events.
 * 
 * @author Eric Hildebrand
 */
public class PlayerListener implements Listener {

  /**
   * Extension of EntityTameEvent used for allowing other plugins to cancel taming of DeadHorses.
   * 
   * @author Eric Hildebrand
   */
  private class DeadEntityTameEvent extends EntityTameEvent {
    public DeadEntityTameEvent(LivingEntity entity, AnimalTamer owner) {
      super(entity, owner);
    }
  }

  /**
   * Extension of VehicleEnterEvent used for allowing other plugins to cancel mounting (to tame) of
   * DeadHorses.
   * 
   * @author Eric Hildebrand
   */
  private class DeadVehicleEnterEvent extends VehicleEnterEvent {
    public DeadVehicleEnterEvent(Vehicle vehicle, Entity entered) {
      super(vehicle, entered);
    }
  }

  /**
   * Extension of PlayerLeashEntityEvent used for allowing other plugins to cancel leashing of
   * DeadHorses.
   * 
   * @author Eric Hildebrand
   */
  private class PlayerLeashDeadEntityEvent extends PlayerLeashEntityEvent {
    public PlayerLeashDeadEntityEvent(Entity what, Entity leashHolder, Player leasher) {
      super(what, leashHolder, leasher);
    }
  }

  private DeadHorses plugin = null;

  private Random random = null;

  /**
   * Instantiate by getting a reference to the plugin instance, instantiating the Random, and
   * registering each of the defined EventHandlers.
   * 
   * @param plugin Reference to DeadHorses plugin instance
   */
  public PlayerListener(DeadHorses plugin) {
    this.plugin = plugin;
    random = new Random();
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  /**
   * Unregister all registered EventHandlers, preventing further reactions, and null the instance of
   * Random.
   */
  public void close() {
    HandlerList.unregisterAll(this);
    random = null;
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
   * @param player Player with item in hand
   * @param variant Type of Horse
   * @return Whether or not the held item is a valid food for the horse
   */
  private boolean isHoldingFood(Player player, Horse.Variant variant) {
    return plugin.getPluginConfig().isFood(variant, player.getItemInHand());
  }

  /**
   * @param player Player with item in hand
   * @return Whether or not the item is a Leash
   */
  private boolean isHoldingLeash(Player player) {
    return player.getItemInHand().isSimilar(new ItemStack(Material.LEASH));
  }

  /**
   * @param player Player with(out) item in hand
   * @return Whether the player is holding nothing
   */
  private boolean isHoldingNothing(Player player) {
    return player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR;
  }

  /**
   * Handle outcomes of a player attempting to leash or feed a Dead Horse.
   * 
   * @param event Caught event
   */
  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
	Class<?> clazz = null;
	try {
        clazz = Class.forName(this.plugin.clazzName);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      return;
    }
	AbstractEffects effect;
	try {
		effect = (AbstractEffects)clazz.asSubclass(clazz).newInstance();
	} catch (InstantiationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return;
	} catch (IllegalAccessException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return;
	}
    if (!isDeadHorse(event.getRightClicked())) {
      return;
    }
    if (!Perms.isUser(event.getPlayer())) {
      return;
    }
    Horse horse = (Horse) event.getRightClicked();
    Player player = event.getPlayer();

    /* User is leashing a DeadHorse */
    if (plugin.getPluginConfig().isLeashingEnabled() && !horse.isLeashed()
        && isHoldingLeash(player) && useItem(player)) {
      event.setCancelled(true);
      PlayerLeashDeadEntityEvent leashEvent = new PlayerLeashDeadEntityEvent(horse, player, player);
      plugin.getServer().getPluginManager().callEvent(leashEvent);
      if (!leashEvent.isCancelled()) {
        horse.setLeashHolder(player);
      }
      return;
    }

    /* User is feeding a DeadHorse */
    if ((plugin.getPluginConfig().isFoodTamingEnabled() || plugin.getPluginConfig()
        .isFoodAgingEnabled()) && isHoldingFood(player, horse.getVariant()) && useItem(player)) {
      event.setCancelled(true);
      boolean success =
          random.nextDouble() * 100.0 < plugin.getPluginConfig().getFoodChance(horse.getVariant(),
              player.getItemInHand());

      /* Successfully tamed a wild, adult horse */
      if (plugin.getPluginConfig().isFoodTamingEnabled() && horse.isAdult() && !horse.isTamed()
          && success) {
        DeadEntityTameEvent tameEvent = new DeadEntityTameEvent(horse, player);
        plugin.getServer().getPluginManager().callEvent(tameEvent);
        if (!tameEvent.isCancelled()) {
          horse.setOwner(player);
          effect.playFeedEffects(horse, true);
          return;
        }
      }

      /* Successfully aged an aging, non-adult horse */
      if (plugin.getPluginConfig().isFoodAgingEnabled() && !horse.isAdult() && !horse.getAgeLock()
          && success) {
        horse.setAdult();
      }

      effect.playFeedEffects(horse, false);
      return;
    }

    /* User is mounting a DeadHorse */
    if (plugin.getPluginConfig().isVanillaTamingEnabled() && horse.isAdult() && !horse.isTamed()
        && horse.isEmpty()) {
      if (!isHoldingNothing(player)) {
    	  effect.playAngryEffects(horse);
        return;
      }
      DeadVehicleEnterEvent mountEvent = new DeadVehicleEnterEvent(horse, player);
      plugin.getServer().getPluginManager().callEvent(mountEvent);
      if (!mountEvent.isCancelled()) {
        horse.setPassenger(player);
      }
      return;
    }
  }

  /**
   * Handle a player being thrown from an untamed Dead Horse.
   * 
   * @param event Caught event
   */
  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onVehicleExitEvent(VehicleExitEvent event) {
	Class<?> clazz = null;
	try {
		clazz = Class.forName(this.plugin.clazzName);
	} catch (ClassNotFoundException e) {
		e.printStackTrace();
	    return;
	}
	AbstractEffects effect;
	try {
		effect = (AbstractEffects)clazz.asSubclass(clazz).newInstance();
	} catch (InstantiationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return;
	} catch (IllegalAccessException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return;
	}
    if (!isDeadHorse(event.getVehicle())) {
      return;
    }
    if (!(event.getExited() instanceof Player) || !Perms.isUser((Player) event.getExited())) {
      return;
    }
    Horse horse = (Horse) event.getVehicle();

    if (plugin.getPluginConfig().isVanillaTamingEnabled() && horse.isAdult() && !horse.isTamed()) {
    	effect.playAngryEffects(horse);
    }
  }

  /**
   * Attempts to remove exactly one of the item currently being held.
   * 
   * TODO: Update player's inventory in a better way, once possible.
   * 
   * @param player The player to modify
   * @return Whether or not one item was taken
   */
  private boolean useItem(Player player) {
    ItemStack item = new ItemStack(player.getItemInHand());
    item.setAmount(1);
    if (player.getInventory().containsAtLeast(item, 1)) {
      player.getInventory().removeItem(item);
      player.updateInventory();
      return true;
    }
    return false;
  }

}
