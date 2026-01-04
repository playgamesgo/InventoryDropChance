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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public final class WorldGuardManager {
    @Getter private static BooleanFlag IDCDisabled;
    @Getter private static IntegerFlag regionDropChance;

    public static void init() {
        try {
            FlagRegistry flags = WorldGuard.getInstance().getFlagRegistry();

            IDCDisabled = new BooleanFlag("idc-disabled");
            flags.register(IDCDisabled);

            regionDropChance = new IntegerFlag("idc-region-drop-chance");
            flags.register(regionDropChance);
        } catch (FlagConflictException e) {
            InventoryDropChance.instance.getLogger().severe("Could not register WorldGuard flags: " + e.getMessage());
        }
    }

    public static boolean isIDCDisabled(Player bukkitPlayer) {
        LocalPlayer player = WorldGuardPlugin.inst().wrapPlayer(bukkitPlayer);
        ApplicableRegionSet set = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().getApplicableRegions(player.getLocation());
        Boolean disabled = set.queryValue(player, IDCDisabled);

        if (disabled == null) return false;
        return disabled;
    }

    public static Boolean rollRegionDropChance(ItemStack itemStack, Player bukkitPlayer) {
        LocalPlayer player = WorldGuardPlugin.inst().wrapPlayer(bukkitPlayer);
        ApplicableRegionSet set = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().getApplicableRegions(player.getLocation());
        Integer regionChance = set.queryValue(player, WorldGuardManager.getRegionDropChance());

        if (regionChance == null || regionChance < 0) return null;
        return new Random().nextInt(100) <= regionChance;
    }
}
