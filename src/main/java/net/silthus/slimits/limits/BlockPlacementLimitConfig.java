package net.silthus.slimits.limits;

import de.exlll.configlib.annotation.Comment;
import de.exlll.configlib.annotation.Convert;
import de.exlll.configlib.configs.yaml.BukkitYamlConfiguration;
import lombok.Getter;
import lombok.Setter;
import net.silthus.slib.config.converter.MaterialMapConverter;
import net.silthus.slimits.LimitMode;
import org.bukkit.Material;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class BlockPlacementLimitConfig extends BukkitYamlConfiguration {

    @Comment({
            "Here you can configure how this limit config plays with other limits.",
            "Defaults to: ADD",
            "ADD: In this mode duplicate block types from different configs will be added to each other and build a sum.",
            "SUBTRACT: In this mode you block limits will be subtracted from other ADD mode configs with the same block types.",
            "ABSOLUTE: Only one absolute config per block type and player is applied. This type of config applies the absolute values and irgnores all other config types."
    })
    private LimitMode mode = LimitMode.ADD;

    @Comment({
            "Define you block type limits in the form of a map.",
            "wood:5",
            "bedrock:100"
    })
    @Convert(MaterialMapConverter.class)
    private Map<Material, Integer> blocks = new HashMap<>();

    public BlockPlacementLimitConfig(Path path, BukkitYamlProperties properties) {
        super(path, properties);
    }

    public BlockPlacementLimitConfig(Path path) {
        super(path);
    }

    public boolean hasLimit(Material blockType) {
        return getBlocks().containsKey(blockType);
    }
}
