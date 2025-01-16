package me.playgamesgo.inventorydropchance.configs;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class GlobalConfig extends OkaeriConfig {
    public enum Order {
        MATERIAL,
        WORLD,
        DEFAULT
    }

    @Comment("Order of chance calculation, chance given by the command will have the highest priority and will work as FIRST_APPLY for that item")
    @Comment("If element is not present, it will be ignored, possible values: MATERIAL, WORLD, DEFAULT")
    private LinkedList<Order> chanceOrder = new LinkedList<>(List.of(
            Order.MATERIAL,
            Order.WORLD,
            Order.DEFAULT
    ));

    public enum OrderType {
        FIRST_SUCCESS,
        FIRST_APPLY
    }

    @Comment("By wich rule the chance will be applied")
    @Comment("FIRST_SUCCESS - each order will be checked, if chance can be applied, but didn't happen, next order will be checked until success or end of the list")
    @Comment("FIRST_APPLY - each order will be checked, if chance can be applied, it will be apply and break the loop, in any case,")
    @Comment("  even if chance can't be applied, next order will be checked until end of the list")
    private OrderType orderType = OrderType.FIRST_SUCCESS;

    @Comment()
    @Comment("Default drop chance for all items, put 0 to disable")
    private int defaultDropChance = 10;

    @Comment()
    @Comment("Default drop chance for all items per world, put {} to disable")
    private Map<String, Integer> worldValues = Map.of(
            "exampleWorld", 80,
            "exampleWorld2", 20
    );

    @Comment()
    @Comment("Default drop chance for all items per material, put {} to disable")
    private Map<Material, Integer> globalValues = Map.of(
            Material.STICK, 50,
            Material.STONE, 10
    );
}
