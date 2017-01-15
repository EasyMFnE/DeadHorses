package net.easymfne.deadhorses.v19plus;

import org.bukkit.EntityEffect;
import org.bukkit.Sound;
import org.bukkit.entity.Horse;
import net.easymfne.deadhorses.AbstractEffects;


public class Effects implements AbstractEffects{
	
	public void playAngryEffects(Horse horse) {
	  horse.getWorld().playSound(horse.getLocation(), Sound.ENTITY_HORSE_ANGRY, 1.0f, 0.75f);
	  horse.playEffect(EntityEffect.WOLF_SMOKE);
	}
	
	/**
	 * Play effects triggered by an eating horse.
	 * 
	 * @param horse The horse
	 * @param happy Show hearts?
	 */
	public void playFeedEffects(Horse horse, boolean happy) {
	  horse.getWorld().playSound(horse.getLocation(), Sound.ENTITY_HORSE_EAT, 1.0f, 0.75f);
	  if (happy) {
	    horse.playEffect(EntityEffect.WOLF_HEARTS);
	  }
	}
}