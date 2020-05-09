package net.silthus.slimits.limits;

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
import java.util.Optional;

@Getter
@Setter
public class BlockPlacementLimitConfig extends BukkitYamlConfiguration {

    private LimitMode mode = LimitMode.WHITELIST;

    @Convert(MaterialMapConverter.class)
    private Map<Material, Integer> limits = new HashMap<>();

    public BlockPlacementLimitConfig(Path path, BukkitYamlProperties properties) {
        super(path, properties);
    }

    public BlockPlacementLimitConfig(Path path) {
        super(path);
    }

    public Optional<Integer> getLimit(Material blockType) {
        return Optional.ofNullable(getLimits().get(blockType));
    }
}
