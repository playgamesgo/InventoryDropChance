package me.playgamesgo.inventorydropchance.commands;

import java.util.List;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import me.playgamesgo.inventorydropchance.InventoryDropChance;
import me.playgamesgo.inventorydropchance.commands.arguments.ChanceArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(name = "inventorydropchance", aliases = "idc")
@Permission("inventorydropchance.inventorydropchance")
public class InventoryDropChanceCommand {
    @Execute
    public void inventoryDropChanceCommand(@Context CommandSender sender) {
        helpCommand(sender);
    }

    @Execute(name = "reload")
    @Permission("inventorydropchance.reload")
    public void reloadCommand(@Context CommandSender sender) {
        InventoryDropChance.config.load(true);
        InventoryDropChance.lang.load(true);
        InventoryDropChance.globalConfig.load(true);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', InventoryDropChance.lang.getReloaded()));
    }

    @Execute(name = "help")
    public void helpCommand(@Context CommandSender sender) {
        List<String> help = InventoryDropChance.lang.getHelp();
        for (String line : help)
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
    }

    @Execute(name = "makenodrop")
    @Permission("inventorydropchance.makenodrop")
    public void makeNoDropCommand(@Context Player player, @Arg boolean lore, @Arg ChanceArgument chance) {
        MakeNoDropCommand.makeNoDropCommand(player, lore, chance);
    }

    @Execute(name = "makenodrop")
    @Permission("inventorydropchance.makenodrop")
    public void makeNoDropCommand(@Context Player player, @Arg boolean lore) {
        MakeNoDropCommand.makeNoDropCommand(player, lore);
    }

    @Execute(name = "makenodrop")
    @Permission("inventorydropchance.makenodrop")
    public void makeNoDropCommand(@Context Player player) {
        MakeNoDropCommand.makeNoDropCommand(player);
    }
}
