package net.silthus.slimits;

import lombok.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class LimitsConfig {

    public static LimitsConfig loadFromFile(FileConfiguration config) {
        return new LimitsConfig().load(config);
    }

    List<BlockPlacementLimitConfig> configs;

    public LimitsConfig() {
        this.configs = new ArrayList<>();
    }

    public List<BlockPlacementLimitConfig> getLimits() {
        return configs;
    }

    public LimitsConfig load(ConfigurationSection config) {

        ConfigurationSection limits = config.getConfigurationSection("limits");
        if (limits == null)
            return this;

        configs = limits.getMapList(LimitType.BLOCK_PLACEMENT.getConfigKey()).stream()
                .map(BlockPlacementLimitConfig::new)
                .collect(Collectors.toList());

        return this;
    }
}
