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

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

/**
 * The class that handles the "/deadhorses" command for the plugin.
 * 
 * @author Eric Hildebrand
 */
public class DeadHorsesCommand implements CommandExecutor {
    
    private DeadHorses plugin = null;
    
    /**
     * Instantiate by getting a reference to the plugin instance and registering
     * this class to handle the '/deadhorses' command.
     * 
     * @param plugin
     *            Reference to DeadHorses plugin instance
     */
    public DeadHorsesCommand(DeadHorses plugin) {
        this.plugin = plugin;
        plugin.getCommand("deadhorses").setExecutor(this);
    }
    
    /**
     * Release the '/deadhorses' command from its ties to this class.
     */
    public void close() {
        plugin.getCommand("deadhorses").setExecutor(null);
    }
    
    /**
     * This method handles user commands. Usage: "/deadhorses <reload,summon>"
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command,
            String label, String[] args) {
        /* "/deadhorses reload" - Reload configuration */
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            plugin.reload();
            sender.sendMessage("Configuration has been reloaded.");
            return true;
        }
        
        /* "/deadhorses summon" - Summon DeadHorses for testing */
        if (args.length == 1 && args[0].equalsIgnoreCase("summon")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("This command does not support console use.");
                return true;
            }
            Player player = (Player) sender;
            for (Horse.Variant variant : new Horse.Variant[] {
                    Horse.Variant.SKELETON_HORSE, Horse.Variant.UNDEAD_HORSE }) {
                Horse horse = (Horse) player.getWorld().spawnEntity(
                        player.getLocation(), EntityType.HORSE);
                horse.setVariant(variant);
                horse.setBaby();
            }
            sender.sendMessage("Dead horses have been summoned.");
            return true;
        }
        
        /* Return false to trigger display of usage from plugin.yml */
        return false;
    }
    
}
