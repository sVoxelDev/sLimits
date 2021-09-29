package net.silthus.slimits.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.silthus.slimits.limits.LimitType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
public class LimitsConfig {

    public static LimitsConfig loadFromFile(FileConfiguration config) {
        return new LimitsConfig().load(config);
    }

    private StorageConfig storage = new StorageConfig();
    private List<BlockPlacementLimitConfig> limits = new ArrayList<>();

    public LimitsConfig load(ConfigurationSection config) {

        loadStorageConfig(config);
        loadLimitsFromConfig(config);

        return this;
    }

    private void loadStorageConfig(ConfigurationSection config) {

        ConfigurationSection storage = config.getConfigurationSection("storage");
        if (storage == null)
            return;

        this.storage = new StorageConfig(storage);
    }

    private void loadLimitsFromConfig(ConfigurationSection config) {

        ConfigurationSection limits = config.getConfigurationSection("limits");
        if (limits == null)
            return;

        this.limits = loadLimitsFromKeyedConfig(limits, LimitType.BLOCK_PLACEMENT.getConfigKey());
    }

    private List<BlockPlacementLimitConfig> loadLimitsFromKeyedConfig(ConfigurationSection limits, String configKey) {

        ConfigurationSection limitsSection = limits.getConfigurationSection(configKey);
        if (limitsSection == null) return new ArrayList<>();

        ArrayList<BlockPlacementLimitConfig> configs = new ArrayList<>();
        for (String key : limitsSection.getKeys(false)) {
            ConfigurationSection config = Objects.requireNonNull(
                    limitsSection.getConfigurationSection(key),
                    "Limit config must not be empty, but \"" + configKey + "." + key + "\" was."
            );
            configs.add(new BlockPlacementLimitConfig(key, config));
        }

        return configs;
    }
}
