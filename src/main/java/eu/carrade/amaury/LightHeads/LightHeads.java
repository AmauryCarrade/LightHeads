/**
 *  Plugin UltraHardcore (UHPlugin)
 *  Copyright (C) 2013 azenet
 *  Copyright (C) 2014 Amaury Carrade
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see [http://www.gnu.org/licenses/].
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
					sender.sendMessage(ChatColor.RED + "You are not allowed to do that.");
					return true;
				}
			}
			else {
				sender.sendMessage(ChatColor.RED + "This can only be executed as a player.");
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
					sender.sendMessage(ChatColor.RED + "You are not allowed to do that.");
					return true;
				}
			}
			else {
				sender.sendMessage(ChatColor.RED + "This can only be executed as a player.");
				return true;
			}
		}
		else { // /head <headOwner> <receiver>
			if(sender.hasPermission("heads.give")) {
				ownerName = args[0];
				receiver = getServer().getPlayer(args[1]);
				if(receiver == null) {
					sender.sendMessage(ChatColor.RED + "The player " + args[1] + " is not online.");
					return true;
				}
			}
			else {
				sender.sendMessage(ChatColor.RED + "You are not allowed to do that.");
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
			sender.sendMessage(ChatColor.GRAY + "Your inventory is full: the head was dropped at your feets.");
		}
		else {
			receiver.playSound(receiver.getLocation(), Sound.ITEM_PICKUP, 0.2f, 1.8f);
		}
		
		return true;
	}
}
