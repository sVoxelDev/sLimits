package net.silthus.slimits;

import org.bukkit.Material;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class BlockPlacementLimitConfigTest {

    @Test
    void create_withMap_mapsProperties() {

        BlockPlacementLimitConfig config = new BlockPlacementLimitConfig(Map.of(
                "type", "bedrock",
                "limit", 20
        ));

        assertThat(config)
                .extracting(
                        BlockPlacementLimitConfig::getType,
                        BlockPlacementLimitConfig::getLimit
                ).contains(
                        Material.BEDROCK,
                        20
                );
    }
}