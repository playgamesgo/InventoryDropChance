package me.playgamesgo.inventorydropchance.commands;

import java.util.List;

import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.jorel.commandapi.annotations.*;
import dev.jorel.commandapi.annotations.arguments.ABooleanArgument;
import dev.jorel.commandapi.annotations.arguments.AIntegerArgument;
import me.playgamesgo.inventorydropchance.InventoryDropChance;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Command("makenodrop")
@Permission("inventorydropchance.inventorydropchance")
@Alias("mnd")
public class MakeNoDropCommand {
    @Default
    public static void makeNoDropCommand(Player player) {
        makeNoDropCommand(player, false, 100);
    }

    @Default
    public static void makeNoDropCommand(Player player, @ABooleanArgument boolean lore) {
        makeNoDropCommand(player, lore, 100);
    }

    @Default
    public static void makeNoDropCommand(Player player, @ABooleanArgument boolean lore, @AIntegerArgument int chance) {
        if (!player.hasPermission("inventorydropchance.makenodrop")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', InventoryDropChance.lang.getNoPermission()));
            return;
        }

        if (player.getInventory().getItemInMainHand().getType().isAir()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', InventoryDropChance.lang.getNoItemInHand()));
            return;
        }

        NBTItem nbtItem = new NBTItem(player.getInventory().getItemInMainHand());
        nbtItem.setBoolean("MAY_NO_DROP", Boolean.TRUE);
        nbtItem.setInteger("NO_DROP_CHANCE", chance);
        ItemStack item = nbtItem.getItem();

        if (lore) {
            InventoryDropChance.lang.load(true);
            List<String> loreList = chance == 100 ? InventoryDropChance.lang.getNoDropLore() : InventoryDropChance.lang.getNoDropChanceLore();

            if (InventoryDropChance.config.isInverseLoreChance()) {
                chance = 100 - chance;
            }

            int finalChance = chance;
            loreList.replaceAll(textToTranslate -> textToTranslate.replaceAll("%chance%", finalChance + ""));
            loreList.replaceAll(textToTranslate -> ChatColor.translateAlternateColorCodes('&', textToTranslate));


            ItemMeta meta = item.getItemMeta();
            List<String> currentLore = meta.getLore();
            if (InventoryDropChance.config.isOverwriteLore()) {
                meta.setLore(loreList);
            } else {
                if (currentLore != null) {
                    currentLore.addAll(loreList);
                    meta.setLore(currentLore);
                } else {
                    meta.setLore(loreList);
                }
            }
            item.setItemMeta(meta);
        }

        player.getInventory().setItemInMainHand(item);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', InventoryDropChance.lang.getNoDropGiven()));
    }
}
