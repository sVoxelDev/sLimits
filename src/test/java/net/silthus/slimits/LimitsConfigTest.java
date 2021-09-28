package net.silthus.slimits;

import net.silthus.slimits.testing.TestBase;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class LimitsConfigTest extends TestBase {

    @Test
    void createFromFile() {

        LimitsConfig config = new LimitsConfig().load(plugin.getConfig());

        assertThat(config.getLimits())
                .isNotNull()
                .hasSize(2)
                .first()
                .extracting(
                        BlockPlacementLimitConfig::getType,
                        BlockPlacementLimitConfig::getLimit
                ).contains(
                        Material.STONE,
                        10
                );
        assertThat(config.getStorage())
                .extracting(StorageConfig::getBlockPlacement)
                .isEqualTo("storage/block_placement/");
    }

    @Test
    void loadConfig_withEmptyConfig_doesNotThrow() {

        LimitsConfig config = new LimitsConfig().load(new MemoryConfiguration());

        assertThat(config.getLimits())
                .isEmpty();
        assertThat(config.getStorage())
                .isNotNull()
                .extracting(StorageConfig::getBlockPlacement)
                .isEqualTo("storage/block_placement/");
    }

    @Test
    void getLimits_unconfigured_returnsEmpty() {

        LimitsConfig config = new LimitsConfig();
        assertThat(config.getLimits())
                .isEmpty();
        assertThat(config.getStorage())
                .isNotNull()
                .extracting(StorageConfig::getBlockPlacement)
                .isEqualTo("storage/block_placement/");
    }

    @Test
    void loadConfig_fromKeyedConfig_loadsLimits_withKeys() throws IOException, InvalidConfigurationException {

        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.load(new File("src/test/resources/config_keyed.yml"));

        LimitsConfig config = new LimitsConfig().load(yamlConfiguration);

        assertThat(config.getLimits())
                .hasSize(1)
                .first()
                .extracting(
                        BlockPlacementLimitConfig::getType,
                        BlockPlacementLimitConfig::getLimit,
                        BlockPlacementLimitConfig::getKey,
                        BlockPlacementLimitConfig::getPermission
                ).contains(
                        Material.BEDROCK,
                        20,
                        "bedrocks",
                        "slimits.limits.block_placement.bedrocks"
                );
    }
}