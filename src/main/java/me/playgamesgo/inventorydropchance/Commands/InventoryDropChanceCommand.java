package me.playgamesgo.inventorydropchance.Commands;

import java.util.List;

import dev.jorel.commandapi.annotations.*;
import dev.jorel.commandapi.annotations.arguments.ABooleanArgument;
import dev.jorel.commandapi.annotations.arguments.AIntegerArgument;
import me.playgamesgo.inventorydropchance.InventoryDropChance;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

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
        InventoryDropChance.configFile.forceReload();
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', InventoryDropChance.configFile.getString("reloaded")));
    }

    @Subcommand("help")
    public static void helpCommand(CommandSender sender) {
        List<String> help = InventoryDropChance.configFile.getStringList("help");
        for (String line : help)
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
    }

    @Subcommand("makenodrop")
    @Permission("inventorydropchance.makenodrop")
    public static void makeNoDropCommand(CommandSender sender, @ABooleanArgument boolean lore, @AIntegerArgument int chance) {
        MakeNoDropCommand.makeNoDropCommand(sender, lore, chance);
    }

    @Subcommand("makenodrop")
    @Permission("inventorydropchance.makenodrop")
    public static void makeNoDropCommand(CommandSender sender, @ABooleanArgument boolean lore) {
        MakeNoDropCommand.makeNoDropCommand(sender, lore);
    }

    @Subcommand("makenodrop")
    @Permission("inventorydropchance.makenodrop")
    public static void makeNoDropCommand(CommandSender sender) {
        MakeNoDropCommand.makeNoDropCommand(sender);
    }
}
