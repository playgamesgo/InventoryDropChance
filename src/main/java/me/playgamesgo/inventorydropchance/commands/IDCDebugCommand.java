package me.playgamesgo.inventorydropchance.commands;

import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Subcommand;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

@Command("idcdebug")
public class IDCDebugCommand {
    @Subcommand("givedefault")
    public static void giveDefaultCommand(Player player) {
        try {
            player.getInventory().clear();
            player.setOp(true);
            player.getInventory().setHeldItemSlot(0);
            List<Material> materials = new ArrayList<>(Arrays.stream(Material.values()).toList());
            List<ItemStack> addItems = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                Collections.shuffle(materials);
                ItemStack item = new ItemStack(materials.get(0));
                player.getInventory().setItemInMainHand(item);
                MakeNoDropCommand.makeNoDropCommand(player, true, 50);
                addItems.add(player.getInventory().getItemInMainHand());
                player.getInventory().clear();
            }
            addItems.forEach(player.getInventory()::addItem);
            player.setOp(false);
        } catch (Exception e) {
            giveDefaultCommand(player);
        }
    }

    @Subcommand("givecurse")
    public static void giveCurseCommand(Player player) {
        try {
            player.getInventory().clear();
            player.setOp(true);
            player.getInventory().setHeldItemSlot(0);
            List<Material> materials = new ArrayList<>(Arrays.stream(Material.values()).toList());
            List<ItemStack> addItems = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                Collections.shuffle(materials);
                ItemStack item = new ItemStack(materials.get(0));
                item.addUnsafeEnchantments(Map.of(Enchantment.VANISHING_CURSE, 1));
                player.getInventory().setItemInMainHand(item);
                MakeNoDropCommand.makeNoDropCommand(player, true, 50);
                addItems.add(player.getInventory().getItemInMainHand());
                player.getInventory().clear();
            }
            addItems.forEach(player.getInventory()::addItem);
            player.setOp(false);
        } catch (Exception e) {
            giveCurseCommand(player);
        }
    }

    @Subcommand("givecursearmor")
    public static void giveCurseArmorCommand(Player player) {
        player.getInventory().clear();
        player.setOp(true);
        player.getInventory().setHeldItemSlot(0);
        List<Material> materials = new ArrayList<>(Arrays.stream(Material.values()).toList());
        materials = materials.stream().filter(material -> {
            final String typeNameString = material.name();
            return typeNameString.endsWith("_HELMET")
                    || typeNameString.endsWith("_CHESTPLATE")
                    || typeNameString.endsWith("_LEGGINGS")
                    || typeNameString.endsWith("_BOOTS");
        }).collect(Collectors.toList());
        List<ItemStack> addItems = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            Collections.shuffle(materials);
            ItemStack item = new ItemStack(materials.get(0));
            item.addUnsafeEnchantments(Map.of(Enchantment.VANISHING_CURSE, 1));
            player.getInventory().setItemInMainHand(item);
            MakeNoDropCommand.makeNoDropCommand(player, true, 50);
            addItems.add(player.getInventory().getItemInMainHand());
            player.getInventory().clear();
        }
        addItems.forEach(player.getInventory()::addItem);
        player.setOp(false);
    }
}
