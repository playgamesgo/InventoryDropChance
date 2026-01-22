package me.playgamesgo.inventorydropchance.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.BiFunction;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.lone.itemsadder.api.CustomStack;
import me.playgamesgo.inventorydropchance.InventoryDropChance;
import me.playgamesgo.inventorydropchance.configs.GlobalConfig;
import me.playgamesgo.inventorydropchance.utils.AxGravesIntegration;
import me.playgamesgo.inventorydropchance.utils.ItemUtils;
import me.playgamesgo.inventorydropchance.utils.WorldGuardManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class PlayerDeathListener implements Listener {
    public static final Random random = new Random();
    private static final Map<GlobalConfig.Order, BiFunction<ItemStack, Player, Boolean>> orders = new HashMap<>();
    private static final Map<Player, Float> pendingSavedAmounts = new HashMap<>();

    public PlayerDeathListener() {
        if (InventoryDropChance.itemsAdder) {
            orders.put(GlobalConfig.Order.ITEMSADDER, (item, player) -> {
                CustomStack stack = CustomStack.byItemStack(item);
                if (stack != null) {
                    if (InventoryDropChance.globalConfig.getItemsAdderValues().containsKey(stack.getNamespacedID())) {
                        int chance = InventoryDropChance.globalConfig.getItemsAdderValues().get(stack.getNamespacedID());
                        return random.nextInt(100) < chance;
                    }
                }
                return null;
            });
        }

        if (InventoryDropChance.worldGuard) {
            orders.put(GlobalConfig.Order.WORLDGUARD, WorldGuardManager::rollRegionDropChance);
        }

        orders.put(GlobalConfig.Order.CUSTOMMODELDATA, (item, player) -> {
            if (item.getItemMeta().hasCustomModelData() &&
                    InventoryDropChance.globalConfig.getCustomModelDataValues().containsKey(item.getItemMeta().getCustomModelData())) {
                int chance = InventoryDropChance.globalConfig.getCustomModelDataValues().get(item.getItemMeta().getCustomModelData());
                return random.nextInt(100) < chance;
            } else {
                return null;
            }
        });

        orders.put(GlobalConfig.Order.MATERIAL, (item, player) -> {
            if (InventoryDropChance.globalConfig.getGlobalValues().containsKey(item.getType())) {
                int chance = InventoryDropChance.globalConfig.getGlobalValues().get(item.getType());
                return random.nextInt(100) < chance;
            } else {
                return null;
            }
        });

        orders.put(GlobalConfig.Order.WORLD, (item, player) -> {
            String world = player.getWorld().getName();

            if (InventoryDropChance.globalConfig.getWorldValues().containsKey(world)) {
                int chance = InventoryDropChance.globalConfig.getWorldValues().get(world);
                return random.nextInt(100) < chance;
            } else {
                return null;
            }
        });

        orders.put(GlobalConfig.Order.DEFAULT, (item, player) ->
                random.nextInt(100) < InventoryDropChance.globalConfig.getDefaultDropChance());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        for (String ignoredWorld : InventoryDropChance.config.getIgnoredWorlds()) {
            if (player.getWorld() == Bukkit.getWorld(ignoredWorld)) return;
        }

        if (InventoryDropChance.worldGuard && WorldGuardManager.isIDCDisabled(event.getEntity())) return;

        int permissionChance = 0;
        if (!InventoryDropChance.config.ignorePermissions) {
            for (int x = 100; x > 0; x--) {
                if (player.hasPermission("inventorydropchance." + x)) {
                    permissionChance = x;
                    break;
                }
            }
        }

        event.setKeepInventory(true);
        event.getDrops().clear();
        PlayerInventory playerInventory = player.getInventory();
        ItemStack[] items = playerInventory.getContents();
        ItemStack[] originalItems = new ItemStack[items.length];
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) originalItems[i] = items[i].clone();
        }

        for (ItemStack item : items) {
            if (item == null) continue;

            if (item.getEnchantments().containsKey(Enchantment.VANISHING_CURSE) &&
                    InventoryDropChance.config.isSkipCurseOfVanishingItems()) {
                ItemUtils.removeCurseOfVanishingItem(playerInventory, item);
                continue;
            }

            if (random.nextInt(100) < permissionChance) continue;

            NBTItem nbtItem = new NBTItem(item);
            if (nbtItem.getBoolean("NO_DROP")) continue; // Legacy support (I think, I don't remember)

            if (nbtItem.getBoolean("MAY_NO_DROP")) {
                int chance = nbtItem.getInteger("NO_DROP_CHANCE");
                if (InventoryDropChance.config.isApplyChanceToItemStack()) {
                    if (random.nextInt(100) < chance) ItemUtils.removeItem(player, playerInventory, item);
                } else {
                    for (int i = 0; i < item.getAmount(); i++) {
                        if (random.nextInt(100) < chance) ItemUtils.removeItemAmount(player, playerInventory, item);
                    }
                }

                continue;
            }

            if (InventoryDropChance.config.isApplyChanceToItemStack()) {
                if (!trySave(item, player)) ItemUtils.removeItem(player, playerInventory, item);
            } else {
                for (int i = 0; i < item.getAmount(); i++) {
                    if (!trySave(item, player)) ItemUtils.removeItemAmount(player, playerInventory, item);
                }
            }
        }

        if (InventoryDropChance.axgraves) AxGravesIntegration.summonGrave(player, event);
        pendingSavedAmounts.put(player, ItemUtils.calculateDifference(originalItems, items));
    }

    @EventHandler
    public static void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        NBT.modify(player, readWriteNBT -> {
            readWriteNBT.setInteger("Score", 0);
        });

        if (InventoryDropChance.lang.isShowTitleOnDeath()) {
            if (pendingSavedAmounts.containsKey(player)) {
                float savedAmount = pendingSavedAmounts.get(player);
                int savedAmound = Math.abs(Math.round(savedAmount * 100) - 100);

                String title = InventoryDropChance.lang.getDeathTitle()
                        .replace("%amount%", Math.round(savedAmount * 100) + "")
                        .replace("%saved_amount%", savedAmound + "");
                String subtitle = InventoryDropChance.lang.getDeathSubTitle()
                        .replace("%amount%", Math.round(savedAmount * 100) + "")
                        .replace("%saved_amount%", savedAmound + "");
                player.sendTitle(ChatColor.translateAlternateColorCodes('&', title),
                        ChatColor.translateAlternateColorCodes('&', subtitle),
                        InventoryDropChance.lang.getFadeIn(), InventoryDropChance.lang.getStay(), InventoryDropChance.lang.getFadeOut());
            }
        }

        if (InventoryDropChance.lang.isSendChatMessageOnDeath()) {
            if (pendingSavedAmounts.containsKey(player)) {
                float savedAmount = pendingSavedAmounts.get(player);
                int savedAmound = Math.abs(Math.round(savedAmount * 100) - 100);

                for (String line : InventoryDropChance.lang.getDeathMessage()) {
                    String message = line
                            .replace("%amount%", Math.round(savedAmount * 100) + "")
                            .replace("%saved_amount%", savedAmound + "");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                }
            }
        }

        pendingSavedAmounts.remove(player);
    }

    private boolean trySave(ItemStack item, Player player) {
        for (GlobalConfig.Order order : InventoryDropChance.globalConfig.getChanceOrder()) {
            if (!orders.containsKey(order)) continue;
            Boolean result = orders.get(order).apply(item, player);
            if (InventoryDropChance.globalConfig.getOrderType() == GlobalConfig.OrderType.FIRST_SUCCESS) {
                if (result != null && result) return true;
            } else if (InventoryDropChance.globalConfig.getOrderType() == GlobalConfig.OrderType.FIRST_APPLY) {
                if (result != null) return result;
            }
        }
        return false;
    }
}
