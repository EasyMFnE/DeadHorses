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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;

/**
 * Main plugin class, responsible for its own setup, logging, reloading, and shutdown operations.
 * Maintains instances of Config, DeadHorsesCommand, and PlayerListener.
 * 
 * @author Eric Hildebrand
 */
public class DeadHorses extends JavaPlugin {

  /* Packet modification constants. */
  private final String PROTOCOL_LIB = "ProtocolLib";
  private final int ENTITY_INDEX = 0;
  private final int WATCHABLE_INDEX = 0;
  private final int ARMOR_INDEX = 22;
  private final int DIAMOND_BARDING_VALUE = 3;
  private final int GOLD_BARDING_VALUE = 2;
  private final int IRON_BARDING_VALUE = 1;
  public int Version;

  private Config config = null;
  private boolean hookedProtocolLib = false;
  private DeadHorsesCommand deadHorsesCommand = null;
  private BardingListener bardingListener = null;
  private PlayerListener playerListener = null;
  public String version;
  public String clazzName;
  public String sendPacket;

  /* Strings for fancyLog() methods */
  private final String logPrefix = ChatColor.DARK_GREEN + "[DeadHorses] ";
  private final String logColor = ChatColor.DARK_GRAY.toString();

  /**
   * Log a message to the console using color, with a specific logging Level. If there is no console
   * open, log the message without any coloration.
   * 
   * @param level Level at which the message should be logged
   * @param message The message to be logged
   */
  protected void fancyLog(Level level, String message) {
    if (getServer().getConsoleSender() != null) {
      getServer().getConsoleSender().sendMessage(logPrefix + logColor + message);
    } else {
      getServer().getLogger().log(level, ChatColor.stripColor(logPrefix + message));
    }
  }

  /**
   * Log a message to the console using color, defaulting to the Info level. If there is no console
   * open, log the message without any coloration.
   * 
   * @param message The message to be logged
   */
  protected void fancyLog(String message) {
    fancyLog(Level.INFO, message);
  }

  /**
   * Get the metadata integer value for a horse's armor.
   * 
   * @param horse The horse
   * @return The armor value
   */
  private int getArmorMetadataValue(Horse horse) {
    if (horse != null && horse.getInventory().getArmor() != null) {
      switch (horse.getInventory().getArmor().getType()) {
        case DIAMOND_BARDING:
          return DIAMOND_BARDING_VALUE;
        case GOLD_BARDING:
          return GOLD_BARDING_VALUE;
        case IRON_BARDING:
          return IRON_BARDING_VALUE;
        default:
      }
    }
    return 0;
  }

  /**
   * @return the configuration helper instance
   */
  public Config getPluginConfig() {
    return config;
  }

  /**
   * Examine an EntityMetadata packet and if it is for a dead horse, modify it to reflect the actual
   * value of the horse's armor.
   * 
   * @param event PacketEvent to examine and potentially modify.
   */
  private void handleMetadataPacket(PacketEvent event) {
    Entity entity = event.getPacket().getEntityModifier(event).read(ENTITY_INDEX);
    if (entity != null && entity.getType() == EntityType.HORSE) {
      switch (((Horse) entity).getVariant()) {
        default:
          return;
        case SKELETON_HORSE:
        case UNDEAD_HORSE:
          List<WrappedWatchableObject> watchables =
              event.getPacket().getWatchableCollectionModifier().read(WATCHABLE_INDEX);
          for (WrappedWatchableObject watchable : watchables) {
            if (watchable.getIndex() == ARMOR_INDEX) {
              int armor = getArmorMetadataValue((Horse) entity);
              // if (!watchable.getValue().equals(armor)) {
              watchable.setValue(armor, true);
              // }
              return;
            }
          }
      }
    }
  }

  /**
   * Close all event handlers and command listeners, then null instances to mark them for garbage
   * collection. Displays elapsed time to console when finished.
   */
  @Override
  public void onDisable() {
    long start = Calendar.getInstance().getTimeInMillis();
    fancyLog("=== DISABLE START ===");
    playerListener.close();
    playerListener = null;
    bardingListener.close();
    bardingListener = null;
    deadHorsesCommand.close();
    deadHorsesCommand = null;
    config = null;
    fancyLog("=== DISABLE COMPLETE (" + (Calendar.getInstance().getTimeInMillis() - start)
        + "ms) ===");
  }

  /**
   * Set up the plugin by: loading config.yml (creating from default if not existent), then
   * instantiating its own Config, DeadHorsesCommand, and PlayerListener objects. Displays elapsed
   * time to console when finished.
   */
  @Override
  public void onEnable() {
    long start = Calendar.getInstance().getTimeInMillis();
    fancyLog("=== ENABLE START ===");
    File configFile = new File(getDataFolder(), "config.yml");
    if (!configFile.exists()) {
      saveDefaultConfig();
      fancyLog("Saved default config.yml");
    }

    config = new Config(this);
    this.version = getNmsVersion().replace("_", "").toLowerCase();
    if(!checkCompat()){
    	this.setEnabled(false);
    }
    deadHorsesCommand = new DeadHorsesCommand(this);
    bardingListener = new BardingListener(this);
    playerListener = new PlayerListener(this);
    setupPacketModification();
    fancyLog("=== ENABLE COMPLETE (" + (Calendar.getInstance().getTimeInMillis() - start)
        + "ms) ===");
  }

  /**
   * Reload the configuration from disk and perform any necessary functions. Displays elapsed time
   * to console when finished.
   */
  public void reload() {
    long start = Calendar.getInstance().getTimeInMillis();
    fancyLog("=== RELOAD START ===");
    reloadConfig();
    fancyLog("=== RELOAD COMPLETE (" + (Calendar.getInstance().getTimeInMillis() - start)
        + "ms) ===");
  }

  /**
   * 
   */
  public void sendMetadataUpdatePacket(Horse horse) {
    if (hookedProtocolLib) {
      ProtocolManager manager = ProtocolLibrary.getProtocolManager();
      PacketContainer packet = manager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
      packet.getIntegers().write(0, horse.getEntityId());
      List<WrappedWatchableObject> watchables = new ArrayList<WrappedWatchableObject>();
      watchables.add(new WrappedWatchableObject(ARMOR_INDEX, getArmorMetadataValue(horse)));
      packet.getWatchableCollectionModifier().write(WATCHABLE_INDEX, watchables);
      manager.broadcastServerPacket(packet, horse, true);
    }
  }

  /**
   * Set up packet listening for ENTITY_METADATA packets so they can be modified to actually contain
   * the dead horses' equipped armor.
   */
  private void setupPacketModification() {
    if (getServer().getPluginManager().getPlugin(PROTOCOL_LIB) instanceof ProtocolLibrary) {
      fancyLog("ProtocolLib detected, creating hook for entity metadata packets.");
      ProtocolManager manager = ProtocolLibrary.getProtocolManager();
      manager.addPacketListener(new PacketAdapter(new PacketAdapter.AdapterParameteters()
          .plugin(this).serverSide().types(PacketType.Play.Server.ENTITY_METADATA)) {
        @Override
        public void onPacketSending(PacketEvent event) {
          if (config.isPacketModificationEnabled()) {
            handleMetadataPacket(event);
          }
        }
      });
      hookedProtocolLib = true;
      fancyLog("Entity metadata packet hooked.");
    }
  }

  /**
   * If possible, instantiate Metrics and connect with mcstats.org
   */
  private void startMetrics() {
    MetricsLite metrics;
    try {
      metrics = new MetricsLite(this);
      if (metrics.start()) {
        fancyLog("Metrics enabled.");
      }
    } catch (IOException e) {
      fancyLog(Level.WARNING, "Metrics exception: " + e.getMessage());
    }
  }
  
  private String getNmsVersion()
  {
    return Bukkit.getServer().getClass().getPackage().getName().replace("org.bukkit.craftbukkit.", "");
  }
  
  private boolean checkCompat()
  {
	String baseVersion = this.version.substring(1 ,3);
	Version = Integer.parseInt(baseVersion);
	if(this.Version < 16){
		getLogger().log(Level.WARNING, "DeadHorses could not be loaded, Horses did not exist before Minecraft 1.6");
		setEnabled(false);
	    return false;
	}
	if(this.Version <= 18){
		this.version = "older";
		this.clazzName = (getClass().getPackage().getName() + "." + this.version + ".Effects");
	}
	if(this.Version > 18){
		this.clazzName = (getClass().getPackage().getName() + "." + this.version + ".Effects");
		this.sendPacket = (getClass().getPackage().getName() + "." + this.version + ".SendPacketTask");
	}
    
    try {
      Class<?> clazz = Class.forName(this.clazzName);
      Class<?> clazz1 = Class.forName(this.sendPacket);
      if (AbstractEffects.class.isAssignableFrom(clazz) && AbstractMountTask.class.isAssignableFrom(clazz1)) {
        return true;
      }
      getLogger().log(Level.WARNING, "DeadHorses could not be loaded, version {" + this.version + "} is not supported yet!");
      setEnabled(false);
      return false;
    } catch (ClassNotFoundException e) {
      getLogger().log(Level.WARNING, "DeadHorses could not be loaded, version {" + this.version + "} is not supported yet!");
      setEnabled(false);
      return false;
    }
  }

}
