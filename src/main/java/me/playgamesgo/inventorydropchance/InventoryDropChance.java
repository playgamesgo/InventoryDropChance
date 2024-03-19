package me.playgamesgo.inventorydropchance;

import de.leonhard.storage.Config;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import me.playgamesgo.inventorydropchance.Commands.InventoryDropChanceCommand;
import me.playgamesgo.inventorydropchance.Commands.MakeNoDropCommand;
import me.playgamesgo.inventorydropchance.Listeners.PlayerDeathListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class InventoryDropChance extends JavaPlugin {
    public static Config configFile;

    public void onLoad() {
        CommandAPI.onLoad((new CommandAPIBukkitConfig(this)).verboseOutput(false));
    }

    public void onEnable() {
        PluginManager pluginManager = getServer().getPluginManager();
        configFile = new Config("config.yml", getDataFolder().toString());
        new ConfigManager(configFile);
        CommandAPI.onEnable();
        pluginManager.registerEvents(new PlayerDeathListener(), this);
        CommandAPI.registerCommand(InventoryDropChanceCommand.class);
        CommandAPI.registerCommand(MakeNoDropCommand.class);
    }

    public void onDisable() {
        CommandAPI.onDisable();
    }
}
