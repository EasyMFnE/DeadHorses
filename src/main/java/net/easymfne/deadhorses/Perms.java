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

import org.bukkit.command.CommandSender;

/**
 * This method provides a static way to check user permissions.
 * 
 * @author Eric Hildebrand
 */
public class Perms {
    
    /**
     * @param sender
     *            User
     * @return Whether user has admin privileges or not
     */
    public static boolean isAdmin(CommandSender sender) {
        return sender.hasPermission("deadhorses.admin");
    }
    
    /**
     * @param sender
     *            User
     * @return Whether user has user privileges or not
     */
    public static boolean isUser(CommandSender sender) {
        return sender.hasPermission("deadhorses.user");
    }
    
}
