/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 */

package eu.carrade.amaury.LightHeads;

import java.util.Random;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public final class HeadsListener implements Listener {
	
	private LightHeads p;
	private Random random;
	
	public HeadsListener(LightHeads lightHeads) {
		p = lightHeads;
		random = new Random();
	}
	
	/**
	 * Used to drop the head, following the probability set in the config.
	 * 
	 * @param ev
	 */
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent ev) {
		if(random.nextDouble() <= p.getDropOnDeathProbability() && ev.getEntity().hasPermission(LightHeads.PERM_DEATH_DROP)) {
			ev.getEntity().getWorld().dropItem(ev.getEntity().getLocation(), p.getHead(ev.getEntity().getName(), 1));
		}
	}
}
