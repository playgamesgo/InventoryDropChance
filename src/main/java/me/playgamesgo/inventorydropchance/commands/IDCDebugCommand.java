package me.playgamesgo.inventorydropchance.commands;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import me.playgamesgo.inventorydropchance.commands.arguments.ChanceArgument;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

@Command(name = "idcdebug")
public class IDCDebugCommand {
    @Execute(name = "givedefault")
    public void giveDefaultCommand(@Context Player player) {
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
                MakeNoDropCommand.makeNoDropCommand(player, true, new ChanceArgument(50));
                addItems.add(player.getInventory().getItemInMainHand());
                player.getInventory().clear();
            }
            addItems.forEach(player.getInventory()::addItem);
            player.setOp(false);
        } catch (Exception e) {
            giveDefaultCommand(player);
        }
    }

    @Execute(name = "givecurse")
    public void giveCurseCommand(@Context Player player) {
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
                MakeNoDropCommand.makeNoDropCommand(player, true, new ChanceArgument(50));
                addItems.add(player.getInventory().getItemInMainHand());
                player.getInventory().clear();
            }
            addItems.forEach(player.getInventory()::addItem);
            player.setOp(false);
        } catch (Exception e) {
            giveCurseCommand(player);
        }
    }

    @Execute(aliases = "givecursearmor")
    public static void giveCurseArmorCommand(@Context Player player) {
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
            MakeNoDropCommand.makeNoDropCommand(player, true, new ChanceArgument(50));
            addItems.add(player.getInventory().getItemInMainHand());
            player.getInventory().clear();
        }
        addItems.forEach(player.getInventory()::addItem);
        player.setOp(false);
    }
}
