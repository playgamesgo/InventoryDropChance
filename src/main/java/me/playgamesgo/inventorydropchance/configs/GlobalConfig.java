package me.playgamesgo.inventorydropchance.configs;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import java.util.*;

@Getter
@Setter
public final class GlobalConfig extends OkaeriConfig {
    public enum Order {
        ITEMSADDER,
        WORLDGUARD,
        CUSTOMMODELDATA,
        MATERIAL,
        WORLD,
        DEFAULT
    }

    @Comment("Order of chance calculation, chance given by the command will have the highest priority and will work as FIRST_APPLY for that item")
    @Comment("If element is not present, it will be ignored, possible values: ITEMSADDER, WORLDGUARD, CUSTOMMODELDATA, MATERIAL, WORLD, DEFAULT")
    private LinkedList<Order> chanceOrder = new LinkedList<>(Arrays.asList(
            Order.ITEMSADDER,
            Order.WORLDGUARD,
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
    private Map<String, Integer> worldValues = new HashMap<String, Integer>() {{
        put("exampleWorld", 80);
        put("exampleWorld2", 20);
    }};

    @Comment()
    @Comment("Default drop chance for all items per material, put {} to disable")
    private Map<Material, Integer> globalValues = new HashMap<Material, Integer>() {{
        put(Material.STICK, 50);
        put(Material.STONE, 10);
    }};

    @Comment()
    @Comment("Default drop chance for all items per custom model data, put {} to disable")
    private Map<Integer, Integer> customModelDataValues = new HashMap<Integer, Integer>() {{
        put(1, 40);
        put(2, 10);
    }};

    @Comment()
    @Comment("Default drop chance for all items from ItemsAdder, requires ItemsAdder plugin, put {} to disable")
    private Map<String, Integer> itemsAdderValues = new HashMap<String, Integer>() {{
        put("example_namespace:example_item", 75);
        put("example_namespace:example_item2", 25);
    }};
}
