package net.easymfne.deadhorses.v19r1;

import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import net.easymfne.deadhorses.AbstractMountTask;
import org.bukkit.scheduler.BukkitRunnable;

import net.minecraft.server.v1_9_R1.PacketPlayOutMount;

public class SendPacketTask implements AbstractMountTask{
		
	@Override
	public void sendPacket(Player player) {
		PacketPlayOutMount packet = new PacketPlayOutMount(((CraftPlayer) player).getHandle());
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}
	
}