package me.playgamesgo.inventorydropchance.commands;

import java.util.List;

import dev.jorel.commandapi.annotations.*;
import dev.jorel.commandapi.annotations.arguments.ABooleanArgument;
import dev.jorel.commandapi.annotations.arguments.AIntegerArgument;
import me.playgamesgo.inventorydropchance.InventoryDropChance;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command("inventorydropchance")
@Alias("idc")
@Permission("inventorydropchance.inventorydropchance")
public class InventoryDropChanceCommand {
    @Default
    public static void inventoryDropChanceCommand(CommandSender sender) {
        helpCommand(sender);
    }

    @Subcommand("reload")
    @Permission("inventorydropchance.reload")
    public static void reloadCommand(CommandSender sender) {
        InventoryDropChance.config.load(true);
        InventoryDropChance.lang.load(true);
        InventoryDropChance.globalConfig.load(true);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', InventoryDropChance.lang.getReloaded()));
    }

    @Subcommand("help")
    public static void helpCommand(CommandSender sender) {
        List<String> help = InventoryDropChance.lang.getHelp();
        for (String line : help)
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
    }

    @Subcommand("makenodrop")
    @Permission("inventorydropchance.makenodrop")
    public static void makeNoDropCommand(Player player, @ABooleanArgument boolean lore, @AIntegerArgument int chance) {
        MakeNoDropCommand.makeNoDropCommand(player, lore, chance);
    }

    @Subcommand("makenodrop")
    @Permission("inventorydropchance.makenodrop")
    public static void makeNoDropCommand(Player player, @ABooleanArgument boolean lore) {
        MakeNoDropCommand.makeNoDropCommand(player, lore);
    }

    @Subcommand("makenodrop")
    @Permission("inventorydropchance.makenodrop")
    public static void makeNoDropCommand(Player player) {
        MakeNoDropCommand.makeNoDropCommand(player);
    }
}
