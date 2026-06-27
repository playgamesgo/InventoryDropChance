package me.playgamesgo.inventorydropchance;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.serdes.commons.SerdesCommons;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;
import me.playgamesgo.inventorydropchance.commands.InventoryDropChanceCommand;
import me.playgamesgo.inventorydropchance.commands.MakeNoDropCommand;
import me.playgamesgo.inventorydropchance.commands.ScrollsCommand;
import me.playgamesgo.inventorydropchance.commands.arguments.ChanceArgument;
import me.playgamesgo.inventorydropchance.listeners.InventoryClickListener;
import me.playgamesgo.inventorydropchance.listeners.PlayerDeathListener;
import me.playgamesgo.inventorydropchance.configs.Config;
import me.playgamesgo.inventorydropchance.configs.GlobalConfig;
import me.playgamesgo.inventorydropchance.configs.LangConfig;
import me.playgamesgo.inventorydropchance.utils.AxGravesIntegration;
import me.playgamesgo.inventorydropchance.utils.WorldGuardManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;

public final class InventoryDropChance extends JavaPlugin {
    public static InventoryDropChance instance;
    public static LiteCommands<CommandSender> liteCommands;
    public static Config config;
    public static LangConfig lang;
    public static GlobalConfig globalConfig;
    public static boolean worldGuard = false;
    public static boolean itemsAdder = false;
    public static boolean axgraves = false;

    @Override
    public void onLoad() {
        org.bukkit.plugin.Plugin worldGuard = Bukkit.getPluginManager().getPlugin("WorldGuard");
        if (worldGuard != null) {
            InventoryDropChance.worldGuard = true;
            WorldGuardManager.init();
        }
    }

    @Override
    public void onEnable() {
        instance = this;
        PluginManager pluginManager = getServer().getPluginManager();

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

        liteCommands = LiteBukkitFactory.builder("inventorydropchance", this)
                .commands(
                        //new IDCDebugCommand(),
                        new InventoryDropChanceCommand(),
                        new MakeNoDropCommand(),
                        new ScrollsCommand()
                )
                .argument(ChanceArgument.class, new ChanceArgument(100))
                .missingPermission((invocation, missingPermissions, chain) ->
                        invocation.sender().sendMessage(ChatColor.translateAlternateColorCodes('&', lang.getNoPermission())))
                .invalidUsage((invocation, invalidUsage, chain) ->
                        invocation.sender().sendMessage(ChatColor.translateAlternateColorCodes('&', lang.getInvalidUsage())))
                .build();

        if (Arrays.stream(Bukkit.getPluginManager().getPlugins()).toList().stream().anyMatch(plugin -> plugin.getName().equals("ItemsAdder"))) {
            itemsAdder = true;
            getLogger().info("ItemsAdder detected, support for custom items added");
        }

        pluginManager.registerEvents(new PlayerDeathListener(), this);
        pluginManager.registerEvents(new InventoryClickListener(), this);

        if (InventoryDropChance.config.isEnableAxGravesIntegration()) {
            if (Arrays.stream(Bukkit.getPluginManager().getPlugins()).toList().stream().anyMatch(plugin -> plugin.getName().equals("AxGraves"))) {
                axgraves = true;
                InventoryDropChance.instance.getServer().getPluginManager().registerEvents(new AxGravesIntegration(), InventoryDropChance.instance);
            } else {
                InventoryDropChance.instance.getLogger().warning("AxGraves integration is enabled in the config, but AxGraves plugin is not installed!");
            }
        }
    }

    public void onDisable() {
        if (liteCommands != null) {
            liteCommands.unregister();
        }
    }
}
