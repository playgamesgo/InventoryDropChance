package me.playgamesgo.inventorydropchance.commands;

import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import dev.jorel.commandapi.annotations.Permission;
import dev.jorel.commandapi.annotations.arguments.ABooleanArgument;
import dev.jorel.commandapi.annotations.arguments.AIntegerArgument;
import me.playgamesgo.inventorydropchance.InventoryDropChance;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

@Command("scrolls")
@Permission("inventorydropchance.scrolls")
public class ScrollsCommand {
    @Default
    public static void makeNoDropCommand(Player sender) {
        commandExecute(sender, false, 100);
    }

    @Default
    public static void makeNoDropCommand(Player sender, @ABooleanArgument boolean lore) {
        commandExecute(sender, lore, 100);
    }

    @Default
    public static void makeNoDropCommand(Player sender, @ABooleanArgument boolean lore, @AIntegerArgument int chance) {
        commandExecute(sender, lore, chance);
    }

    public static void commandExecute(Player sender, boolean addLore, int chance) {
        if (sender.hasPermission("inventorydropchance.scrolls")) {
            if (!sender.getInventory().getItemInMainHand().getType().isAir()) {
                ItemStack item = sender.getInventory().getItemInMainHand();
                if (addLore) {
                    ItemMeta meta = item.getItemMeta();
                    List<String> lore = InventoryDropChance.configFile.getStringList("scrollsLore");
                    lore.replaceAll(textToTranslate -> ChatColor.translateAlternateColorCodes('&', textToTranslate));
                    lore.replaceAll(textToTranslate -> textToTranslate.replace("%chance%", chance + "%"));
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
                nbtItem.setBoolean("IS_SCROLL", Boolean.TRUE);
                nbtItem.setInteger("SCROLL_NO_DROP_CHANCE", chance);
                item = nbtItem.getItem();
                sender.getInventory().setItemInMainHand(item);

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', InventoryDropChance.configFile.getString("scrollGiven")));
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', InventoryDropChance.configFile.getString("noItemInHand")));
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', InventoryDropChance.configFile.getString("noPermission")));
        }
    }
}
