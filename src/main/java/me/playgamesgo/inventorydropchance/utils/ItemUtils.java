package me.playgamesgo.inventorydropchance.utils;

import me.playgamesgo.inventorydropchance.InventoryDropChance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class ItemUtils {
    public static void removeItem(Player player, PlayerInventory playerInventory, ItemStack item) {
        if (item.getEnchantments().containsKey(Enchantment.VANISHING_CURSE)) {
            removeCurseOfVanishingItem(playerInventory, item);
            return;
        }

        if (InventoryDropChance.axgraves) AxGravesIntegration.addGraveItems(player, item.clone());
        else player.getWorld().dropItemNaturally(player.getLocation(), item);
        item.setAmount(0);
    }

    public static void removeItemAmount(Player player, PlayerInventory playerInventory, ItemStack item) {
        if (item.getEnchantments().containsKey(Enchantment.VANISHING_CURSE)) {
            if (playerInventory.getItemInOffHand().equals(item)) {
                if (item.getAmount() <= 1) item = null;
                else item.setAmount(item.getAmount() - 1);

                playerInventory.setItemInOffHand(item);
                return;
            }
            if (playerInventory.getHelmet() != null && playerInventory.getHelmet().equals(item)) {
                if (item.getAmount() <= 1) item = null;
                else item.setAmount(item.getAmount() - 1);

                playerInventory.setHelmet(item);
                return;
            }
            if (playerInventory.getChestplate() != null && playerInventory.getChestplate().equals(item)) {
                if (item.getAmount() <= 1) item = null;
                else item.setAmount(item.getAmount() - 1);

                playerInventory.setChestplate(item);
                return;
            }
            if (playerInventory.getLeggings() != null && playerInventory.getLeggings().equals(item)) {
                if (item.getAmount() <= 1) item = null;
                else item.setAmount(item.getAmount() - 1);

                playerInventory.setLeggings(item);
                return;
            }
            if (playerInventory.getBoots() != null && playerInventory.getBoots().equals(item)) {
                if (item.getAmount() <= 1) item = null;
                else item.setAmount(item.getAmount() - 1);

                playerInventory.setBoots(item);
                return;
            }

            if (item.getAmount() == 1) playerInventory.remove(item);
            else item.setAmount(item.getAmount() - 1);
            return;
        }
        ItemStack singleItem = item.clone();
        singleItem.setAmount(1);

        if (InventoryDropChance.axgraves) AxGravesIntegration.addGraveItems(player, singleItem.clone());
        else player.getWorld().dropItemNaturally(player.getLocation(), singleItem);
        item.setAmount(item.getAmount() - 1);
    }

    public static void removeCurseOfVanishingItem(PlayerInventory playerInventory, ItemStack item) {
        if (playerInventory.getItemInOffHand().equals(item)) {
            playerInventory.setItemInOffHand(null);
            return;
        }
        if (playerInventory.getHelmet() != null && playerInventory.getHelmet().equals(item)) {
            playerInventory.setHelmet(null);
            return;
        }
        if (playerInventory.getChestplate() != null && playerInventory.getChestplate().equals(item)) {
            playerInventory.setChestplate(null);
            return;
        }
        if (playerInventory.getLeggings() != null && playerInventory.getLeggings().equals(item)) {
            playerInventory.setLeggings(null);
            return;
        }
        if (playerInventory.getBoots() != null && playerInventory.getBoots().equals(item)) {
            playerInventory.setBoots(null);
            return;
        }
        playerInventory.remove(item);
    }

    public static float calculateDifference(ItemStack[] originalItems, ItemStack[] modifiedItems) {
        int totalOriginalAmount = 0;
        int totalModifiedAmount = 0;

        for (ItemStack item : originalItems) {
            if (item != null) {
                totalOriginalAmount += item.getAmount();
            }
        }

        for (ItemStack item : modifiedItems) {
            if (item != null) {
                totalModifiedAmount += item.getAmount();
            }
        }

        if (totalOriginalAmount == 0) {
            return 0.0f;
        }

        return (float) (totalOriginalAmount - totalModifiedAmount) / totalOriginalAmount;
    }
}
