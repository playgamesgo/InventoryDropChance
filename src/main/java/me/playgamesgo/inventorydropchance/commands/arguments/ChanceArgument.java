package me.playgamesgo.inventorydropchance.commands.arguments;

import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import lombok.AllArgsConstructor;
import me.playgamesgo.inventorydropchance.InventoryDropChance;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ChanceArgument extends  ArgumentResolver<CommandSender, ChanceArgument> {
    public int chance;

    @Override
    protected ParseResult<ChanceArgument> parse(Invocation<CommandSender> invocation, Argument<ChanceArgument> context, String argument) {
        int chance;
        try {
            chance = Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            return ParseResult.failure(ChatColor.translateAlternateColorCodes('&', InventoryDropChance.lang.getInvalidUsage()));
        }

        if (chance < 1 || chance > 100) {
            return ParseResult.failure(ChatColor.translateAlternateColorCodes('&', InventoryDropChance.lang.getInvalidUsage()));
        }

        return ParseResult.success(new ChanceArgument(chance));
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<ChanceArgument> argument, SuggestionContext context) {
        List<String> suggestions = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            suggestions.add(i + "");
        }
        return SuggestionResult.of(suggestions);
    }
}
