package me.playgamesgo.inventorydropchance.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.lone.itemsadder.api.CustomStack;
import me.playgamesgo.inventorydropchance.InventoryDropChance;
import me.playgamesgo.inventorydropchance.configs.GlobalConfig;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerDeathListener implements Listener {
    private final Map<GlobalConfig.Order, Runnable> orders = new HashMap<>();

    public PlayerDeathListener() {
        if (InventoryDropChance.itemsAdder) {
            orders.put(GlobalConfig.Order.ITEMSADDDER, (item, player) -> {
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
        } else
            InventoryDropChance.instance.getLogger().warning("ItemsAdder is not installed, but ITEMSADDER order is presented, ignoring");

        orders.put(GlobalConfig.Order.CUSTOMMODELDATA, (item, player) -> {
            Random random = new Random();

            if (InventoryDropChance.globalConfig.getCustomModelDataValues().containsKey(item.getItemMeta().getCustomModelData())) {
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
            if (player.getWorld() == Bukkit.getWorld(ignoredWorld))
                return;
        }

        int max = 0;
        if (InventoryDropChance.config.ignorePermissions) {
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

    private void removeItemAmount(Player player, PlayerInventory playerInventory, ItemStack item) {
        if (item.getEnchantments().containsKey(Enchantment.VANISHING_CURSE)) {
            if (playerInventory.getItemInOffHand().equals(item)) {
                if (item.getAmount() <= 1) item = null;
                else item.setAmount(item.getAmount() - 1);

                playerInventory.setItemInOffHand(item);
            } else {
                if (item.getAmount() == 1) playerInventory.remove(item);
                else item.setAmount(item.getAmount() - 1);
            }
            return;
        }
        ItemStack singleItem = item.clone();
        singleItem.setAmount(1);

        player.getWorld().dropItemNaturally(player.getLocation(), singleItem);
        item.setAmount(item.getAmount() - 1);
    }

    @FunctionalInterface
    private interface Runnable {
        Boolean run(ItemStack itemStack, Player player);
    }
}
