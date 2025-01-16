package me.playgamesgo.inventorydropchance.configs;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Config extends OkaeriConfig {
    private List<String> ignoredWorlds = List.of("exampleWorld", "exampleWorld2");

    @Comment()
    @Comment("If true, a chance to save item will be ignored if item has Curse of Vanishing")
    private boolean skipCurseOfVanishingItems = false;

    @Comment("If true, a drop chance will be applied to the all items in same stack, else each item in one stack will have individual chance")
    private boolean applyChanceToItemStack = true;

    @Comment("If true, the plugin will overwrite the existing lore of the item, else it will append the lore")
    private boolean overwriteLore = true;

    @Comment("If true, the plugin will inverse the chance number that will added to the lore")
    private boolean inverseLoreChance = false;

    public boolean enableScrolls = true;
}
