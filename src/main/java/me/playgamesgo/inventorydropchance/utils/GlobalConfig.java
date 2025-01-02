package me.playgamesgo.inventorydropchance.utils;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import java.util.Map;

@Getter
@Setter
public class GlobalConfig extends OkaeriConfig {
    @Comment("\"globalValues: {}\" to disable")
    private Map<Material, Integer> globalValues = Map.of(
            Material.STICK, 50,
            Material.STONE, 10
    );
}
