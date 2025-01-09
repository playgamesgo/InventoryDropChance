package me.playgamesgo.inventorydropchance.configs;

import de.leonhard.storage.Config;
import de.leonhard.storage.internal.settings.ReloadSettings;

import java.util.ArrayList;
import java.util.List;

public class LegacyConfig {
    public LegacyConfig(Config configFile) {
        configFile.setReloadSettings(ReloadSettings.INTELLIGENT);

        configFile.setDefault("onlyPlayer", "&e[&6InventoryDropChance&e] &cOnly players can use this command!");
        configFile.setDefault("noPermission", "&e[&6InventoryDropChance&e] &cYou don't have permission to use this command!");
        configFile.setDefault("noDropGiven", "&e[&6InventoryDropChance&e] &aNow this item is not droppable!");
        configFile.setDefault("noItemInHand", "&e[&6InventoryDropChance&e] &cYou don't have any item in your hand!");
        configFile.setDefault("reloaded", "&e[&6InventoryDropChance&e] &aConfig reloaded!");
        configFile.setDefault("invalidArgument", "&e[&6InventoryDropChance&e] &cInvalid argument! Use /idc help for help!");
        configFile.setDefault("noArgument", "&e[&6InventoryDropChance&e] &cYou need to specify an argument! Use /idc help for help!");
        configFile.setDefault("scrollGiven", "&e[&6InventoryDropChance&e] &aScroll given!");

        List<String> help = new ArrayList<>();
        help.add("&e[&6InventoryDropChance&e] &aCommands:");
        help.add("&a/idc reload - Reloads the config");
        help.add("&a/mnd &7- &eMake the item in your hand not drop on death");
        help.add("&a/mnd <addLore> <chance> &7- &eMake the item in your hand not drop on death with a specific chance");
        help.add("&a/idc help &7- &eShows this help message");
        configFile.setDefault("help", help);

        List<String> noDropLore = new ArrayList<>();
        noDropLore.add("&6This item will not drop on death!");
        configFile.setDefault("noDropLore", noDropLore);

        List<String> noDropChanceLore = new ArrayList<>();
        noDropChanceLore.add("&6This item has a %chance%% chance to not drop on death!");
        configFile.setDefault("noDropChanceLore", noDropChanceLore);

        List<String> ignoredWorlds = new ArrayList<>();
        ignoredWorlds.add("exampleWorld");
        ignoredWorlds.add("exampleWorld2");
        configFile.setDefault("ignoredWorlds", ignoredWorlds);

        configFile.setDefault("skipCurseOfVanishingItems", false);

        configFile.setDefault("loreOverwriteMode", true);

        configFile.setDefault("enableScrolls", true);
        List<String> scrollsLore = new ArrayList<>();
        scrollsLore.add("&6This item is a scroll!");
        scrollsLore.add("It put a %chance% chance to not drop the item on death!");
        configFile.setDefault("scrollsLore", scrollsLore);

        configFile.write();
    }
}
