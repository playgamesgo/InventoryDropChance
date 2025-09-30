package me.playgamesgo.inventorydropchance.utils;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import lombok.Getter;
import me.playgamesgo.inventorydropchance.InventoryDropChance;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public final class WorldGuardManager {
    @Getter private static boolean enabled = false;
    @Getter private static BooleanFlag IDCDisabled;
    @Getter private static IntegerFlag regionDropChance;

    public static void init() {
        Plugin worldGuard = Bukkit.getPluginManager().getPlugin("WorldGuard");
        if (worldGuard == null) return;

        try {
            FlagRegistry flags = WorldGuard.getInstance().getFlagRegistry();

            IDCDisabled = new BooleanFlag("idc-disabled");
            flags.register(IDCDisabled);

            regionDropChance = new IntegerFlag("idc-region-drop-chance");
            flags.register(regionDropChance);

            enabled = true;
        } catch (FlagConflictException e) {
            InventoryDropChance.instance.getLogger().severe("Could not register WorldGuard flags: " + e.getMessage());
        }
    }

    public static boolean isIDCDisabled(org.bukkit.entity.Player bukkitPlayer) {
        if (!enabled) return false;

        LocalPlayer player = WorldGuardPlugin.inst().wrapPlayer(bukkitPlayer);
        ApplicableRegionSet set = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().getApplicableRegions(player.getLocation());
        Boolean disabled = set.queryValue(player, IDCDisabled);

        if (disabled == null) return false;
        return disabled;
    }
}
