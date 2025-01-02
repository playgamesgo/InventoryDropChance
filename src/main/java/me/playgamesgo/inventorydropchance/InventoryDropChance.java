package me.playgamesgo.inventorydropchance;

import de.leonhard.storage.Config;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import eu.okaeri.configs.serdes.commons.SerdesCommons;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;
import me.playgamesgo.inventorydropchance.commands.InventoryDropChanceCommand;
import me.playgamesgo.inventorydropchance.commands.MakeNoDropCommand;
import me.playgamesgo.inventorydropchance.commands.ScrollsCommand;
import me.playgamesgo.inventorydropchance.listeners.InventoryClickListener;
import me.playgamesgo.inventorydropchance.listeners.PlayerDeathListener;
import me.playgamesgo.inventorydropchance.utils.ConfigManager;
import me.playgamesgo.inventorydropchance.utils.GlobalConfig;
import me.playgamesgo.plugin.annotation.plugin.ApiVersion;
import me.playgamesgo.plugin.annotation.plugin.Description;
import me.playgamesgo.plugin.annotation.plugin.Plugin;
import me.playgamesgo.plugin.annotation.plugin.author.Author;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@Plugin(name = "InventoryDropChance", version = "${version}")
@ApiVersion(ApiVersion.Target.v1_16)
@Author("playgamesgo")
@Description("Change the drop rate of items in the inventory on death")
public final class InventoryDropChance extends JavaPlugin {
    public static Config configFile;
    public static GlobalConfig globalConfig;

    public void onLoad() {
        CommandAPI.onLoad((new CommandAPIBukkitConfig(this)).verboseOutput(false));
    }

    public void onEnable() {
        PluginManager pluginManager = getServer().getPluginManager();
        configFile = new Config("config.yml", getDataFolder().toString());
        new ConfigManager(configFile);

        globalConfig = eu.okaeri.configs.ConfigManager.create(GlobalConfig.class, (it) -> {
            it.withConfigurer(new YamlBukkitConfigurer(), new SerdesCommons(), new SerdesBukkit());
            it.withBindFile(new File(this.getDataFolder(), "global.yml"));
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });

        pluginManager.registerEvents(new PlayerDeathListener(), this);

        CommandAPI.registerCommand(InventoryDropChanceCommand.class);
        CommandAPI.registerCommand(MakeNoDropCommand.class);
        if (configFile.getBoolean("enableScrolls")) {
            CommandAPI.registerCommand(ScrollsCommand.class);
            pluginManager.registerEvents(new InventoryClickListener(), this);
        }

        CommandAPI.onEnable();
    }

    public void onDisable() {
        CommandAPI.onDisable();
    }
}
