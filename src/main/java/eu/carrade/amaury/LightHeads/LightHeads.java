/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, you can
 * obtain one at http://mozilla.org/MPL/2.0/.
 */

package eu.carrade.amaury.LightHeads;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public final class LightHeads extends JavaPlugin
{
    public static final String PERM_GET_SELF = "heads.self";
    public static final String PERM_GET_OTHERS = "heads.others";
    public static final String PERM_GIVE = "heads.give";
    public static final String PERM_DEATH_DROP = "heads.deathDrop";

    private static final String[] MHF_HEADS = new String[]{
            "MHF_Alex",
            "MHF_Blaze",
            "MHF_CaveSpider",
            "MHF_Chicken",
            "MHF_Cow",
            "MHF_Creeper",
            "MHF_Enderman",
            "MHF_Ghast",
            "MHF_Golem",
            "MHF_Herobrine",
            "MHF_Lava_slime",
            "MHF_Mushroom_cow",
            "MHF_Ocelot",
            "MHF_Pig",
            "MHF_PigZombie",
            "MHF_Sheep",
            "MHF_Skeleton",
            "MHF_Slime",
            "MHF_Spider",
            "MHF_Squid",
            "MHF_Steve",
            "MHF_Villager",
            "MHF_WSkeleton",
            "MHF_Zombie",
            "MHF_Cactus",
            "MHF_Cake",
            "MHF_Chest",
            "MHF_CoconutB",
            "MHF_CoconutG",
            "MHF_Melon",
            "MHF_OakLog",
            "MHF_Present1",
            "MHF_Present2",
            "MHF_Pumpkin",
            "MHF_TNT",
            "MHF_TNT2",
            "MHF_ArrowUp",
            "MHF_ArrowDown",
            "MHF_ArrowLeft",
            "MHF_ArrowRight",
            "MHF_Exclamation",
            "MHF_Question",
    };

    private double dropOnDeathProbability;
    private boolean pickupSound;

    @Override
    public void onEnable()
    {
        this.saveDefaultConfig();

        pickupSound = getConfig().getBoolean("pickupSound", true);
        dropOnDeathProbability = Math.min(Math.abs(getConfig().getDouble("dropOnDeathProbability", 0.2d)), 1.0d);

        if (dropOnDeathProbability > 0)
        {
            getServer().getPluginManager().registerEvents(new HeadsListener(this), this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (!cmd.getName().equalsIgnoreCase("head")) return false;

        OfflinePlayer owner;
        Player receiver;

        // /head
        if (args.length == 0)
        {
            if (sender instanceof Player)
            {
                if (sender.hasPermission(PERM_GET_SELF))
                {
                    owner = (OfflinePlayer) sender;
                    receiver = (Player) sender;
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + getConfig().getString("i18n.disallowed"));
                    return true;
                }
            }
            else
            {
                sender.sendMessage(ChatColor.RED + getConfig().getString("i18n.onlyAsAPlayer"));
                return true;
            }
        }
        // /head <headOwner>
        else if (args.length == 1)
        {
            if (sender instanceof Player)
            {
                if (sender.hasPermission(PERM_GET_OTHERS))
                {
                    owner = getServer().getOfflinePlayer(args[0]);
                    receiver = (Player) sender;
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + getConfig().getString("i18n.disallowed"));
                    return true;
                }
            }
            else
            {
                sender.sendMessage(ChatColor.RED + getConfig().getString("i18n.onlyAsAPlayer"));
                return true;
            }
        }
        // /head <headOwner> <receiver>
        else
        {
            if (sender.hasPermission(PERM_GIVE))
            {
                owner = getServer().getOfflinePlayer(args[0]);
                receiver = getServer().getPlayer(args[1]);
                if (receiver == null)
                {
                    sender.sendMessage(ChatColor.RED + getConfig().getString("i18n.notOnline").replace("{player}", args[1]));
                    return true;
                }
            }
            else
            {
                sender.sendMessage(ChatColor.RED + getConfig().getString("i18n.disallowed"));
                return true;
            }
        }

        final ItemStack head = getHead(owner, 1);

        if (receiver.getInventory().addItem(head).size() != 0)
        {
            // Inventory was full
            receiver.getWorld().dropItem(receiver.getLocation(), head);
            sender.sendMessage(ChatColor.GRAY + getConfig().getString("i18n.inventoryFull"));
        }
        else
        {
            if (pickupSound)
            {
                receiver.playSound(receiver.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.2f, 1.8f);
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
    {
        if (!command.getName().equalsIgnoreCase("head"))
        {
            return super.onTabComplete(sender, command, alias, args);
        }

        if (!sender.hasPermission(PERM_GET_OTHERS) && !sender.hasPermission(PERM_GIVE)) return null;

        return Stream.concat(
                Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName),
                Arrays.stream(MHF_HEADS)
        ).collect(Collectors.toList());
    }

    /**
     * Returns an ItemStack representing {@code amount} heads of {@code owner}.
     *
     * @param owner  The owner.
     * @param amount The amount.
     * @return The head.
     */
    ItemStack getHead(final OfflinePlayer owner, final int amount)
    {
        final ItemStack head = new ItemStack(Material.PLAYER_HEAD, amount);

        final SkullMeta sm = (SkullMeta) head.getItemMeta();
        sm.setOwningPlayer(owner);
        head.setItemMeta(sm);

        return head;
    }

    /**
     * Returns the probability a head drop when a player die (between 0.0 and 1.0).
     *
     * @return The probability.
     */
    double getDropOnDeathProbability()
    {
        return dropOnDeathProbability;
    }
}
