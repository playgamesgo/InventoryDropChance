package me.playgamesgo.inventorydropchance.listeners;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryClickListener implements Listener {
    @EventHandler
    public static void inventoryClickListener(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            if (event.getAction() == InventoryAction.SWAP_WITH_CURSOR) {
                ItemStack cursor = event.getCursor();
                NBTItem nbtItem = new NBTItem(cursor);
                if (nbtItem.getBoolean("IS_SCROLL")) {
                    if (event.getCurrentItem() != null) {
                        ItemStack currentItem = event.getCurrentItem();
                        NBTItem currentItemNBT = new NBTItem(currentItem);
                        if (currentItemNBT.hasTag("MAY_NO_DROP") || currentItemNBT.hasTag("IS_SCROLL")) {
                            return;
                        }
                        event.setCancelled(true);
                        currentItemNBT.setBoolean("MAY_NO_DROP", Boolean.TRUE);
                        currentItemNBT.setInteger("NO_DROP_CHANCE", nbtItem.getInteger("SCROLL_NO_DROP_CHANCE"));
                        currentItem = currentItemNBT.getItem();
                        player.getInventory().setItem(event.getSlot(), currentItem);
                        cursor.setAmount(cursor.getAmount() - 1);
                    }
                }
            }
        }
    }
}
