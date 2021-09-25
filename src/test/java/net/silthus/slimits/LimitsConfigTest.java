package net.silthus.slimits;

import net.silthus.slimits.testing.TestBase;
import org.bukkit.Material;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LimitsConfigTest extends TestBase {

    @Test
    void createFromFile() {

        LimitsConfig config = new LimitsConfig().load(plugin.getConfig());
        assertThat(config.getLimits())
                .isNotNull()
                .hasSize(1)
                .first()
                .extracting(
                        BlockPlacementLimitConfig::getType,
                        BlockPlacementLimitConfig::getLimit
                ).contains(
                        Material.STONE,
                        10
                );
    }

    @Test
    void loadConfig_withEmptyConfig_doesNotThrow() {

        LimitsConfig config = new LimitsConfig().load(new MemoryConfiguration());

        assertThat(config.getLimits())
                .isEmpty();
    }

    @Test
    void getLimits_unconfigured_returnsEmpty() {

        assertThat(new LimitsConfig().getLimits())
                .isEmpty();
    }
}