package me.playgamesgo.inventorydropchance.commands;

import java.util.List;

import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.jorel.commandapi.annotations.*;
import dev.jorel.commandapi.annotations.arguments.ABooleanArgument;
import dev.jorel.commandapi.annotations.arguments.AIntegerArgument;
import me.playgamesgo.inventorydropchance.InventoryDropChance;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Command("makenodrop")
@Permission("inventorydropchance.inventorydropchance")
@Alias("mnd")
public class MakeNoDropCommand {
    @Default
    public static void makeNoDropCommand(CommandSender sender) {
        commandExecute(sender, false, 100);
    }

    @Default
    public static void makeNoDropCommand(CommandSender sender, @ABooleanArgument boolean lore) {
        commandExecute(sender, lore, 100);
    }

    @Default
    public static void makeNoDropCommand(CommandSender sender, @ABooleanArgument boolean lore, @AIntegerArgument int chance) {
        commandExecute(sender, lore, chance);
    }

    public static void commandExecute(CommandSender sender, boolean addLore, int chance) {
        if (sender instanceof Player) {
            Player player = (Player)sender;
            if (player.hasPermission("inventorydropchance.makenodrop")) {
                if (!player.getInventory().getItemInMainHand().getType().isAir()) {
                    ItemStack item = player.getInventory().getItemInMainHand();
                    if (addLore) {
                        ItemMeta meta = item.getItemMeta();
                        List<String> lore = InventoryDropChance.configFile.getStringList("noDropLore");
                        if (chance != 100) {
                            lore = InventoryDropChance.configFile.getStringList("noDropChanceLore");
                        }
                        lore.replaceAll(textToTranslate -> textToTranslate.replaceAll("%chance%", String.valueOf(chance)));
                        lore.replaceAll(textToTranslate -> ChatColor.translateAlternateColorCodes('&', textToTranslate));
                        if (InventoryDropChance.configFile.getBoolean("loreOverwriteMode")) {
                            meta.setLore(lore);
                        } else {
                            List<String> currentLore = meta.getLore();
                            if (currentLore != null) {
                                currentLore.addAll(lore);
                                meta.setLore(currentLore);
                            } else {
                                meta.setLore(lore);
                            }
                        }
                        item.setItemMeta(meta);
                    }
                    NBTItem nbtItem = new NBTItem(item);
                    nbtItem.setBoolean("MAY_NO_DROP", Boolean.TRUE);
                    nbtItem.setInteger("NO_DROP_CHANCE", chance);
                    item = nbtItem.getItem();
                    player.getInventory().setItemInMainHand(item);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', InventoryDropChance.configFile.getString("noDropGiven")));
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', InventoryDropChance.configFile.getString("noItemInHand")));
                }
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', InventoryDropChance.configFile.getString("noPermission")));
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', InventoryDropChance.configFile.getString("onlyPlayer")));
        }
    }
}
