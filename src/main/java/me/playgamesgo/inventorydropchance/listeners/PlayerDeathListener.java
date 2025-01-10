package me.playgamesgo.inventorydropchance.listeners;

import java.util.Random;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.playgamesgo.inventorydropchance.InventoryDropChance;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
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

        for (String ignoredWorld : InventoryDropChance.config.getIgnoredWorlds()) {
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
            if (item == null) continue;

            if (item.getEnchantments().containsKey(Enchantment.VANISHING_CURSE) &&
                    InventoryDropChance.config.isSkipCurseOfVanishingItems()) {
                if (playerInventory.getItemInOffHand().equals(item)) {
                    playerInventory.setItemInOffHand(null);
                    continue;
                }
                playerInventory.remove(item);
                continue;
            }

            Random rand = new Random();
            int n = rand.nextInt(100);
            if (n > max) {
                NBTItem nbtItem = new NBTItem(item);
                if (nbtItem.getBoolean("NO_DROP")) continue; // Legacy support (I think, I don't remember)

                if (nbtItem.getBoolean("MAY_NO_DROP")) {
                    int chance = nbtItem.getInteger("NO_DROP_CHANCE");
                    if (n > chance) {
                        removeItem(player, playerInventory, item);
                    }
                } else {
                    if (InventoryDropChance.globalConfig.getGlobalValues().containsKey(item.getType())) {
                        int chance = InventoryDropChance.globalConfig.getGlobalValues().get(item.getType());
                        if (n > chance) {
                            removeItem(player, playerInventory, item);
                        }
                    } else {
                        removeItem(player, playerInventory, item);
                    }
                }
            }

        }
    }

    private void removeItem(Player player, PlayerInventory playerInventory, ItemStack item) {
        if (item.getEnchantments().containsKey(Enchantment.VANISHING_CURSE)) {
            if (playerInventory.getItemInOffHand().equals(item)) {
                playerInventory.setItemInOffHand(null);
            } else {
                playerInventory.remove(item);
            }
            return;
        }
        player.getWorld().dropItemNaturally(player.getLocation(), item);
        item.setAmount(0);
    }
}
