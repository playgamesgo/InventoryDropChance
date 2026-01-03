package me.playgamesgo.inventorydropchance.commands;

import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import me.playgamesgo.inventorydropchance.InventoryDropChance;
import me.playgamesgo.inventorydropchance.commands.arguments.ChanceArgument;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

@Command(name = "scrolls")
@Permission("inventorydropchance.scrolls")
public class ScrollsCommand {
    @Execute
    public void scrollCommand(@Context Player player) {
        scrollCommand(player, false, new ChanceArgument(100));
    }

    @Execute
    public void scrollCommand(@Context Player player, @Arg boolean lore) {
        scrollCommand(player, lore, new ChanceArgument(100));
    }

    @Execute
    public void scrollCommand(@Context Player player, @Arg boolean lore, @Arg ChanceArgument chance) {
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
        nbtItem.setInteger("SCROLL_NO_DROP_CHANCE", chance.chance);
        ItemStack item = nbtItem.getItem();

        if (lore) {
            InventoryDropChance.lang.load(true);
            List<String> loreList = InventoryDropChance.lang.getScrollsLore();

            if (InventoryDropChance.config.isInverseLoreChance()) {
                chance.chance = 100 - chance.chance;
            }

            int finalChance = chance.chance;
            loreList.replaceAll(textToTranslate -> textToTranslate.replaceAll("%chance%", finalChance + "%"));
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
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', InventoryDropChance.lang.getScrollGiven()));
    }
}
