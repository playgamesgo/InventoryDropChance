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
        ITEMSADDDER,
        CUSTOMMODELDATA,
        MATERIAL,
        WORLD,
        DEFAULT
    }

    @Comment("Order of chance calculation, chance given by the command will have the highest priority and will work as FIRST_APPLY for that item")
    @Comment("If element is not present, it will be ignored, possible values: ITEMSADDDER, CUSTOMMODELDATA, MATERIAL, WORLD, DEFAULT")
    private LinkedList<Order> chanceOrder = new LinkedList<>(List.of(
            Order.ITEMSADDDER,
            Order.CUSTOMMODELDATA,
            Order.MATERIAL,
            Order.WORLD,
            Order.DEFAULT
    ));

    public enum OrderType {
        FIRST_SUCCESS,
        FIRST_APPLY
    }

    @Comment("By wich rule the chance will be applied")
    @Comment("FIRST_SUCCESS: Each order will be checked; if chance can be applied but didn't happen, the next order will be checked until success or the end of the list.")
    @Comment("FIRST_APPLY: Each order will be checked; if a chance can be applied, it will be applied and break the loop. In any case,")
    @Comment("  even if the chance didn't work out, the next order will be checked until the end of the list.")
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

    @Comment()
    @Comment("Default drop chance for all items per custom model data, put {} to disable")
    private Map<Integer, Integer> customModelDataValues = Map.of(
            1, 40,
            2, 10
    );

    @Comment()
    @Comment("Default drop chance for all items from ItemsAdder, requires ItemsAdder plugin, put {} to disable")
    private Map<String, Integer> itemsAdderValues = Map.of(
            "example_namespace:example_item", 75,
            "example_namespace:example_item2", 25
    );
}
