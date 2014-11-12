/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 */

package eu.carrade.amaury.LightHeads;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

public final class LightHeads extends JavaPlugin {
	
	public static final String PERM_GET_SELF   = "heads.self";
	public static final String PERM_GET_OTHERS = "heads.others";
	public static final String PERM_GIVE       = "heads.give";
	
	double  dropProbabilityOnDeath;
	boolean pickupSound;
	
	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		
		pickupSound = getConfig().getBoolean("pickupSound", true);
		dropProbabilityOnDeath = Math.min(Math.abs(getConfig().getDouble("dropProbabilityOnDeath", 0.2d)), 1.0d);
		
		if(dropProbabilityOnDeath > 0) {
			getServer().getPluginManager().registerEvents(new HeadsListener(this), this);
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!cmd.getName().equalsIgnoreCase("head")) return false;
		
		String ownerName = null;
		Player receiver = null;
		
		if(args.length == 0) { // /head
			if(sender instanceof Player) {
				if(((Player) sender).hasPermission(PERM_GET_SELF)) {
					ownerName = ((Player) sender).getName();
					receiver = (Player) sender;
				}
				else {
					sender.sendMessage(ChatColor.RED + getConfig().getString("i18n.disallowed"));
					return true;
				}
			}
			else {
				sender.sendMessage(ChatColor.RED + getConfig().getString("i18n.onlyAsAPlayer"));
				return true;
			}
		}
		else if(args.length == 1) { // /head <headOwner>
			if(sender instanceof Player) {
				if(((Player) sender).hasPermission(PERM_GET_OTHERS)) {
					ownerName = args[0];
					receiver = (Player) sender;
				}
				else {
					sender.sendMessage(ChatColor.RED + getConfig().getString("i18n.disallowed"));
					return true;
				}
			}
			else {
				sender.sendMessage(ChatColor.RED + getConfig().getString("i18n.onlyAsAPlayer"));
				return true;
			}
		}
		else { // /head <headOwner> <receiver>
			if(sender.hasPermission(PERM_GIVE)) {
				ownerName = args[0];
				receiver = getServer().getPlayer(args[1]);
				if(receiver == null) {
					sender.sendMessage(ChatColor.RED + getConfig().getString("i18n.notOnline").replace("{player}", args[1]));
					return true;
				}
			}
			else {
				sender.sendMessage(ChatColor.RED + getConfig().getString("i18n.disallowed"));
				return true;
			}
		}
		
		ItemStack head = getHead(ownerName, 1);
		
		if(receiver.getInventory().addItem(head).size() != 0) {
			// Inventory was full
			receiver.getWorld().dropItem(receiver.getLocation(), head);
			sender.sendMessage(ChatColor.GRAY + getConfig().getString("i18n.inventoryFull"));
		}
		else {
			if(pickupSound) {
				receiver.playSound(receiver.getLocation(), Sound.ITEM_PICKUP, 0.2f, 1.8f);
			}
		}
		
		return true;
	}
	
	/**
	 * Returns an ItemStack representing {@code amount} heads of {@code owner}.
	 * 
	 * @param owner The owner.
	 * @param amount The amount.
	 * 
	 * @return The head.
	 */
	public ItemStack getHead(String owner, int amount) {
		ItemStack head = new ItemStack(Material.SKULL_ITEM, amount, (short) 3);
		
		SkullMeta sm = (SkullMeta) head.getItemMeta();
		sm.setOwner(owner);
		head.setItemMeta(sm);
		
		return head;
	}
	
	/**
	 * Returns the probability a head drop when a player die (between 0.0 and 1.0).
	 * 
	 * @return The probability.
	 */
	public double getDropProbabilityOnDeath() {
		return dropProbabilityOnDeath;
	}
}
