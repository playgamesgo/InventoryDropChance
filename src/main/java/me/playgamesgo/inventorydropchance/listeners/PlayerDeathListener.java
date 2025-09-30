package me.playgamesgo.inventorydropchance.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.lone.itemsadder.api.CustomStack;
import me.playgamesgo.inventorydropchance.InventoryDropChance;
import me.playgamesgo.inventorydropchance.configs.GlobalConfig;
import me.playgamesgo.inventorydropchance.utils.WorldGuardManager;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class PlayerDeathListener implements Listener {
    private static final Map<GlobalConfig.Order, Runnable> orders = new HashMap<>();

    public PlayerDeathListener() {
        if (InventoryDropChance.itemsAdder) {
            orders.put(GlobalConfig.Order.ITEMSADDER, (item, player) -> {
                Random random = new Random();

                CustomStack stack = CustomStack.byItemStack(item);
                if (stack != null) {
                    if (InventoryDropChance.globalConfig.getItemsAdderValues().containsKey(stack.getNamespacedID())) {
                        int chance = InventoryDropChance.globalConfig.getItemsAdderValues().get(stack.getNamespacedID());
                        return random.nextInt(100) <= chance;
                    }
                }
                return null;
            });
        }

        if (WorldGuardManager.isEnabled()) {
            orders.put(GlobalConfig.Order.WORLDGUARD, (item, bukkitPlayer) -> {
                LocalPlayer player = WorldGuardPlugin.inst().wrapPlayer(bukkitPlayer);
                ApplicableRegionSet set = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().getApplicableRegions(player.getLocation());
                Integer regionChance = set.queryValue(player, WorldGuardManager.getRegionDropChance());

                if (regionChance == null || regionChance < 0) return null;
                return new Random().nextInt(100) <= regionChance;
            });
        }

        orders.put(GlobalConfig.Order.CUSTOMMODELDATA, (item, player) -> {
            Random random = new Random();

            if (item.getItemMeta().hasCustomModelData() &&
                    InventoryDropChance.globalConfig.getCustomModelDataValues().containsKey(item.getItemMeta().getCustomModelData())) {
                int chance = InventoryDropChance.globalConfig.getCustomModelDataValues().get(item.getItemMeta().getCustomModelData());
                return random.nextInt(100) <= chance;
            } else {
                return null;
            }
        });

        orders.put(GlobalConfig.Order.MATERIAL, (item, player) -> {
            Random random = new Random();

            if (InventoryDropChance.globalConfig.getGlobalValues().containsKey(item.getType())) {
                int chance = InventoryDropChance.globalConfig.getGlobalValues().get(item.getType());
                return random.nextInt(100) <= chance;
            } else {
                return null;
            }
        });

        orders.put(GlobalConfig.Order.WORLD, (item, player) -> {
            Random random = new Random();
            String world = player.getWorld().getName();

            if (InventoryDropChance.globalConfig.getWorldValues().containsKey(world)) {
                int chance = InventoryDropChance.globalConfig.getWorldValues().get(world);
                return random.nextInt(100) <= chance;
            } else {
                return null;
            }
        });

        orders.put(GlobalConfig.Order.DEFAULT, (item, player) ->
                new Random().nextInt(100) <= InventoryDropChance.globalConfig.getDefaultDropChance());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        for (String ignoredWorld : InventoryDropChance.config.getIgnoredWorlds()) {
            if (player.getWorld() == Bukkit.getWorld(ignoredWorld)) return;
        }

        if (WorldGuardManager.isIDCDisabled(event.getEntity())) return;

        int max = 0;
        if (!InventoryDropChance.config.ignorePermissions) {
            for (int x = 100; x > 0; x--) {
                if (player.hasPermission("inventorydropchance." + x)) {
                    max = x;
                    break;
                }
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
                removeCurseOfVanishingItem(playerInventory, item);
                continue;
            }

            Random rand = new Random();
            int n = rand.nextInt(100);
            if (n > max) {

                NBTItem nbtItem = new NBTItem(item);
                if (nbtItem.getBoolean("NO_DROP")) continue; // Legacy support (I think, I don't remember)

                if (nbtItem.getBoolean("MAY_NO_DROP")) {
                    int chance = nbtItem.getInteger("NO_DROP_CHANCE");
                    if (!InventoryDropChance.config.isApplyChanceToItemStack()) {
                        for (int i = 0; i < item.getAmount(); i++) {
                            if (n > chance) {
                                removeItemAmount(player, playerInventory, item);
                            }
                        }
                    } else {
                        if (n > chance) {
                            removeItem(player, playerInventory, item);
                        }
                    }
                } else {
                    if (!InventoryDropChance.config.isApplyChanceToItemStack()) {
                        for (int i = 0; i < item.getAmount(); i++) {
                            if (!trySave(item, player)) {
                                removeItemAmount(player, playerInventory, item);
                            }
                        }
                    } else {
                        if (!trySave(item, player)) {
                            removeItem(player, playerInventory, item);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public static void onPlayerRespawn(PlayerRespawnEvent event) {
        NBT.modify(event.getPlayer(), readWriteNBT -> {
            readWriteNBT.setInteger("Score", 0);
        });
    }

    private boolean trySave(ItemStack item, Player player) {
        for (GlobalConfig.Order order : InventoryDropChance.globalConfig.getChanceOrder()) {
            if (!orders.containsKey(order)) continue;
            Boolean result = orders.get(order).run(item, player);
            if (InventoryDropChance.globalConfig.getOrderType() == GlobalConfig.OrderType.FIRST_SUCCESS) {
                if (result != null && result) return true;
            } else if (InventoryDropChance.globalConfig.getOrderType() == GlobalConfig.OrderType.FIRST_APPLY) {
                if (result != null) return result;
            }
        }
        return false;
    }

    private void removeItem(Player player, PlayerInventory playerInventory, ItemStack item) {
        if (item.getEnchantments().containsKey(Enchantment.VANISHING_CURSE)) {
            removeCurseOfVanishingItem(playerInventory, item);
            return;
        }
        player.getWorld().dropItemNaturally(player.getLocation(), item);
        item.setAmount(0);
    }

    private void removeItemAmount(Player player, PlayerInventory playerInventory, ItemStack item) {
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

        player.getWorld().dropItemNaturally(player.getLocation(), singleItem);
        item.setAmount(item.getAmount() - 1);
    }

    private void removeCurseOfVanishingItem(PlayerInventory playerInventory, ItemStack item) {
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

    @FunctionalInterface
    private interface Runnable {
        Boolean run(ItemStack itemStack, Player player);
    }
}
