package me.playgamesgo.inventorydropchance.Listeners;

import java.util.Random;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.playgamesgo.inventorydropchance.InventoryDropChance;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerDeathListener implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        for (String ignoredWorld : InventoryDropChance.configFile.getStringList("ignoredWorlds")) {
            if (player.getWorld() == Bukkit.getWorld(ignoredWorld))
                return;
        }
        int max = 0;
        for (int x = 100; x > 0; x--) {
            if (player.hasPermission("inventorydropchance." + x)) {
                max = x;
                break;
            }
        }
        event.setKeepInventory(true);
        event.getDrops().clear();
        PlayerInventory playerInventory = player.getInventory();
        ItemStack[] items = playerInventory.getContents();
        for (ItemStack item : items) {
            if (item != null) {
                Random rand = new Random();
                int n = rand.nextInt(100);
                if (n > max) {
                    NBTItem nbtItem = new NBTItem(item);
                    if (!nbtItem.getBoolean("NO_DROP"))
                        if (!nbtItem.getBoolean("MAY_NO_DROP")) {
                            player.getWorld().dropItemNaturally(player.getLocation(), item);
                            item.setAmount(0);
                        } else {
                            int chance = nbtItem.getInteger("NO_DROP_CHANCE");
                            if (n > chance) {
                                player.getWorld().dropItemNaturally(player.getLocation(), item);
                                item.setAmount(0);
                            }
                        }
                }
            }
        }
    }
}
