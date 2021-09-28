package net.silthus.slimits.config;

import org.bukkit.Material;
import org.bukkit.configuration.MemoryConfiguration;
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
                        BlockPlacementLimitConfig::getLimit,
                        BlockPlacementLimitConfig::getPermission,
                        BlockPlacementLimitConfig::getKey
                ).contains(
                        Material.BEDROCK,
                        20,
                        "slimits.limits.block_placement.bedrock.20",
                        "bedrock-20"
                );
    }

    @Test
    void create_allowsOverride_Permission() {

        BlockPlacementLimitConfig config = new BlockPlacementLimitConfig(Map.of(
                "type", "diamond_block",
                "limit", 3,
                "permission", "diamonds"
        ));

        assertThat(config.getPermission())
                .isEqualTo("diamonds");
    }

    @Test
    void create_withConfiguration_andKey() {

        MemoryConfiguration config = new MemoryConfiguration();
        config.set("type", "minecraft:cobblestone");
        config.set("limit", 10);

        BlockPlacementLimitConfig limitConfig = new BlockPlacementLimitConfig("test", config);
        assertThat(limitConfig)
                .extracting(
                        BlockPlacementLimitConfig::getType,
                        BlockPlacementLimitConfig::getLimit,
                        BlockPlacementLimitConfig::getPermission,
                        BlockPlacementLimitConfig::getKey
                ).contains(
                        Material.COBBLESTONE,
                        10,
                        "slimits.limits.block_placement.test",
                        "test"
                );
    }

    @Test
    void create_withConfiguration_andKey_allowsOverriding_Permission() {

        MemoryConfiguration config = new MemoryConfiguration();
        config.set("type", "minecraft:cobblestone");
        config.set("limit", 10);
        config.set("permission", "my-test");

        BlockPlacementLimitConfig limitConfig = new BlockPlacementLimitConfig("test", config);

        assertThat(limitConfig.getPermission())
                .isEqualTo("my-test");
    }

    @Test
    void getKey_returnsValidKey() {

        assertThat(BlockPlacementLimitConfig.getKey(Material.STONE, 20))
                .isEqualTo("stone-20");
    }
}