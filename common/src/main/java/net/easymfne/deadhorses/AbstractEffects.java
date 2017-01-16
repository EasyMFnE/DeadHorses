package net.easymfne.deadhorses;

import org.bukkit.entity.Horse;

public interface AbstractEffects{
	
	/**
	 * Play effects triggered by making a horse angry.
	 * 
	 * @param horse The horse
	 */
	public void playAngryEffects(Horse horse);
	
	/**
	 * Play effects triggered by an eating horse.
	 * 
	 * @param horse The horse
	 * @param happy Show hearts?
	 */
	public void playFeedEffects(Horse horse, boolean happy);
}