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
	
	@Override
	public void onEnable() {
		this.saveDefaultConfig();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!cmd.getName().equalsIgnoreCase("head")) return false;
		
		String ownerName = null;
		Player receiver = null;
		
		if(args.length == 0) { // /head
			if(sender instanceof Player) {
				if(((Player) sender).hasPermission("heads.self")) {
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
				if(((Player) sender).hasPermission("heads.others")) {
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
			if(sender.hasPermission("heads.give")) {
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
		
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta sm = (SkullMeta) head.getItemMeta();
		sm.setOwner(ownerName);
		head.setItemMeta(sm);
		
		if(receiver.getInventory().addItem(head).size() != 0) {
			// Inventory was full
			receiver.getWorld().dropItem(receiver.getLocation(), head);
			sender.sendMessage(ChatColor.GRAY + getConfig().getString("i18n.inventoryFull"));
		}
		else {
			if(getConfig().getBoolean("pickupSound")) {
				receiver.playSound(receiver.getLocation(), Sound.ITEM_PICKUP, 0.2f, 1.8f);
			}
		}
		
		return true;
	}
}
