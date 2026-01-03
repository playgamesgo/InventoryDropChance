package me.playgamesgo.inventorydropchance.utils;

import com.artillexstudios.axgraves.AxGraves;
import com.artillexstudios.axgraves.api.events.GravePreSpawnEvent;
import com.artillexstudios.axgraves.api.events.GraveSpawnEvent;
import com.artillexstudios.axgraves.grave.Grave;
import com.artillexstudios.axgraves.grave.SpawnedGraves;
import com.artillexstudios.axgraves.utils.ExperienceUtils;
import me.playgamesgo.inventorydropchance.InventoryDropChance;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public final class AxGravesIntegration implements Listener {
    public static HashMap<Player, List<ItemStack>> graveItems = new HashMap<>();

    public static void addGraveItems(Player player, ItemStack... items) {
        List<ItemStack> playerItems = graveItems.getOrDefault(player, new ArrayList<>());
        playerItems.addAll(Arrays.asList(items));
        graveItems.put(player, playerItems);
    }

    public static void summonGrave(Player player, PlayerDeathEvent event) {
        List<ItemStack> playerItems = graveItems.getOrDefault(player, new ArrayList<>());
        if (playerItems.isEmpty()) return;

        int xp = 0;
        boolean storeXp = AxGraves.CONFIG.getBoolean("store-xp", true);
        if (storeXp) {
            xp = Math.round(ExperienceUtils.getExp(player) * AxGraves.CONFIG.getFloat("xp-keep-percentage", 1f));
        }

        Location location = player.getLocation();
        location.add(0, -0.5, 0);

        if (storeXp) event.setDroppedExp(0);
        Grave grave = new Grave(location, player, playerItems, xp, System.currentTimeMillis());
        SpawnedGraves.addGrave(grave);

        final GraveSpawnEvent graveSpawnEvent = new GraveSpawnEvent(player, grave);
        Bukkit.getPluginManager().callEvent(graveSpawnEvent);
        graveItems.remove(player);
    }

    @EventHandler
    public void onPreGraveSpawn(GravePreSpawnEvent event) {
        if (InventoryDropChance.axgraves) event.setCancelled(true);
    }
}
