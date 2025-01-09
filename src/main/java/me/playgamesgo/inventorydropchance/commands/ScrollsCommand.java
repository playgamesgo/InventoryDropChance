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
    public static void makeNoDropCommand(Player player) {
        makeNoDropCommand(player, false, 100);
    }

    @Default
    public static void makeNoDropCommand(Player player, @ABooleanArgument boolean lore) {
        makeNoDropCommand(player, lore, 100);
    }

    @Default
    public static void makeNoDropCommand(Player player, @ABooleanArgument boolean lore, @AIntegerArgument int chance) {
        if (!player.hasPermission("inventorydropchance.scrolls")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', InventoryDropChance.lang.getNoPermission()));
            return;
        }

        if (player.getInventory().getItemInMainHand().getType().isAir()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', InventoryDropChance.lang.getNoItemInHand()));
            return;
        }

        NBTItem nbtItem = new NBTItem(player.getInventory().getItemInMainHand());
        nbtItem.setBoolean("IS_SCROLL", Boolean.TRUE);
        nbtItem.setInteger("SCROLL_NO_DROP_CHANCE", chance);
        ItemStack item = nbtItem.getItem();

        if (lore) {
            InventoryDropChance.lang.load(true);
            List<String> loreList = InventoryDropChance.lang.getScrollsLore();
            loreList.replaceAll(textToTranslate -> ChatColor.translateAlternateColorCodes('&', textToTranslate));
            loreList.replaceAll(textToTranslate -> textToTranslate.replace("%chance%", chance + "%"));

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
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', InventoryDropChance.lang.getScrollGiven()));
    }
}
