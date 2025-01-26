package me.playgamesgo.inventorydropchance;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.serdes.commons.SerdesCommons;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;
import me.playgamesgo.inventorydropchance.commands.IDCDebugCommand;
import me.playgamesgo.inventorydropchance.commands.InventoryDropChanceCommand;
import me.playgamesgo.inventorydropchance.commands.MakeNoDropCommand;
import me.playgamesgo.inventorydropchance.commands.ScrollsCommand;
import me.playgamesgo.inventorydropchance.listeners.InventoryClickListener;
import me.playgamesgo.inventorydropchance.listeners.PlayerDeathListener;
import me.playgamesgo.inventorydropchance.configs.Config;
import me.playgamesgo.inventorydropchance.configs.LegacyConfig;
import me.playgamesgo.inventorydropchance.configs.GlobalConfig;
import me.playgamesgo.inventorydropchance.configs.LangConfig;
import me.playgamesgo.plugin.annotation.dependency.SoftDependency;
import me.playgamesgo.plugin.annotation.plugin.ApiVersion;
import me.playgamesgo.plugin.annotation.plugin.Description;
import me.playgamesgo.plugin.annotation.plugin.Plugin;
import me.playgamesgo.plugin.annotation.plugin.author.Author;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

@Plugin(name = "InventoryDropChance", version = "${version}")
@ApiVersion(ApiVersion.Target.v1_16)
@Author("playgamesgo")
@Description("Change the drop rate of items in the inventory on death")
@SoftDependency("ItemsAdder")
public final class InventoryDropChance extends JavaPlugin {
    public static InventoryDropChance instance;
    public static Config config;
    public static LangConfig lang;
    public static GlobalConfig globalConfig;
    public static boolean itemsAdder = false;

    public void onLoad() {
        CommandAPI.onLoad((new CommandAPIBukkitConfig(this)).verboseOutput(false));
    }

    public void onEnable() {
        instance = this;
        PluginManager pluginManager = getServer().getPluginManager();

        boolean migrate = false;
        File configFile = new File(getDataFolder(), "config.yml");
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                StringBuilder sb = new StringBuilder();
                int i;
                while ((i = reader.read()) != -1) {
                    sb.append((char) i);
                    if (i == '\n') {
                        break;
                    }
                }
                String firstLine = sb.toString();
                if (firstLine.startsWith("onlyPlayer:")) { // Legacy config, migrate
                    if (configFile.renameTo(new File(getDataFolder(), "config_old.yml"))) {
                        getLogger().warning("Old config detected, config file will be migrated to new format, old config file is saved as config_old.yml");
                        migrate = true;
                    } else {
                        throw new RuntimeException("Failed to rename old config to config_old.yml");
                    }
                }
            } catch (IOException e) {
                getLogger().severe("Failed to read config.yml");
                throw new RuntimeException(e);
            }
        }

        config = ConfigManager.create(Config.class, (it) -> {
            it.withConfigurer(new YamlBukkitConfigurer(), new SerdesCommons(), new SerdesBukkit());
            it.withBindFile(new File(this.getDataFolder(), "config.yml"));
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });

        lang = ConfigManager.create(LangConfig.class, (it) -> {
            it.withConfigurer(new YamlBukkitConfigurer(), new SerdesCommons(), new SerdesBukkit());
            it.withBindFile(new File(this.getDataFolder(), "lang.yml"));
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });

        globalConfig = ConfigManager.create(GlobalConfig.class, (it) -> {
            it.withConfigurer(new YamlBukkitConfigurer(), new SerdesCommons(), new SerdesBukkit());
            it.withBindFile(new File(this.getDataFolder(), "global.yml"));
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });

        if (migrate) {
            de.leonhard.storage.Config legacyConfig = new de.leonhard.storage.Config("config_old.yml", getDataFolder().toString());
            new LegacyConfig(legacyConfig);
            config.setIgnoredWorlds(legacyConfig.getStringList("ignoredWorlds"));
            config.setSkipCurseOfVanishingItems(legacyConfig.getBoolean("skipCurseOfVanishingItems"));
            config.setOverwriteLore(legacyConfig.getBoolean("loreOverwriteMode"));
            config.setEnableScrolls(legacyConfig.getBoolean("enableScrolls"));
            config.save();
            config.load();

            lang.setNoPermission(legacyConfig.getString("noPermission"));
            lang.setNoDropGiven(legacyConfig.getString("noDropGiven"));
            lang.setNoItemInHand(legacyConfig.getString("noItemInHand"));
            lang.setReloaded(legacyConfig.getString("reloaded"));
            lang.setInvalidArgument(legacyConfig.getString("invalidArgument"));
            lang.setNoArgument(legacyConfig.getString("noArgument"));
            lang.setScrollGiven(legacyConfig.getString("scrollGiven"));
            lang.setHelp(legacyConfig.getStringList("help"));
            lang.setNoDropLore(legacyConfig.getStringList("noDropLore"));
            lang.setNoDropChanceLore(legacyConfig.getStringList("noDropChanceLore"));
            lang.setScrollsLore(legacyConfig.getStringList("scrollsLore"));
            lang.save();
            lang.load();

            getLogger().info("Config migrated successfully");
        }


        pluginManager.registerEvents(new PlayerDeathListener(), this);

        if (false) CommandAPI.registerCommand(IDCDebugCommand.class);
        CommandAPI.registerCommand(InventoryDropChanceCommand.class);
        CommandAPI.registerCommand(MakeNoDropCommand.class);
        if (config.isEnableScrolls()) {
            CommandAPI.registerCommand(ScrollsCommand.class);
            pluginManager.registerEvents(new InventoryClickListener(), this);
        }

        CommandAPI.onEnable();

        getLogger().info("Plugin List: " + Arrays.stream(Bukkit.getPluginManager().getPlugins()).map(org.bukkit.plugin.Plugin::getName).reduce((a, b) -> a + ", " + b).orElse(""));

        if (Arrays.stream(Bukkit.getPluginManager().getPlugins()).toList().stream().anyMatch(plugin -> plugin.getName().equals("ItemsAdder"))) {
            itemsAdder = true;
            getLogger().info("ItemsAdder detected, support for custom items added");
        }
    }

    public void onDisable() {
        CommandAPI.onDisable();
    }
}
