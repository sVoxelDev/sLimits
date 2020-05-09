package net.silthus.slimits.limits;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.google.common.collect.Maps;
import net.silthus.slimits.Constants;
import net.silthus.slimits.LimitsPlugin;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.junit.jupiter.api.*;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("block placement limit")
class BlockPlacementLimitTest {

    private static ServerMock server;
    private static LimitsPlugin plugin;
    private BlockPlacementLimit limit;
    private PlayerMock player;

    @BeforeAll
    public static void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.loadWith(LimitsPlugin.class, new File("src/test/resources/plugin.yml"));
    }

    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }

    @BeforeEach
    public void beforeEach() {
        Path configPath = Path.of("src/test/resources", "test-limit1.yaml");
        BlockPlacementLimitConfig config = new BlockPlacementLimitConfig(configPath);
        config.load();

        limit = new BlockPlacementLimit("test");
        limit.load(config);
        plugin.registerEvents(limit);

        player = server.addPlayer();
        player.addAttachment(plugin, Constants.PERMISSION_PREFIX + "test", true);
    }

    @Test
    @DisplayName("should increase limit count in memory")
    public void shouldIncreaseLimitCount() {

        World world = server.getWorld("world");
        assertThat(world).isNotNull();

        Block block = world.getBlockAt(0, 0, 0);
        assertThat(block).isNotNull();

        limit.addPlacedBlock(player, block);
        PlayerBlockPlacementLimit playerLimit = limit.getPlayerLimits().get(player.getUniqueId());
        assertThat(playerLimit).isNotNull();

        assertThat(playerLimit.getCount(block.getType())).isEqualTo(1);

        for (int i = 0; i < 10; i++) {
            limit.addPlacedBlock(player, block);
        }

        assertThat(playerLimit.getCount(block.getType())).isEqualTo(11);
    }

    @Test
    @DisplayName("should decrease limit count in memory")
    public void shouldDecreaseLimitCount() {

        World world = server.getWorld("world");
        assertThat(world).isNotNull();

        PlayerBlockPlacementLimit playerLimit = limit.getPlayerLimit(player);
        for (int i = 0; i < 5; i++) {
            playerLimit.addBlock(world.getBlockAt(i, i, i));
        }

        Block block = world.getBlockAt(0, 0, 0);

        assertThat(playerLimit.getCounts()).hasSizeBetween(1, 5);
        assertThat(playerLimit.getLocations()).hasSize(5);

        int count = playerLimit.getCount(block.getType());

        playerLimit.removeBlock(block);

        assertThat(playerLimit.getCount(block.getType())).isEqualTo(count - 1);
    }

    @Test
    @DisplayName("should decrease limit count on block break")
    public void blockBreakEventShouldDecreaseLimitCount() {

        World world = server.getWorld("world");
        assertThat(world).isNotNull();

        Block block = world.getBlockAt(0, 0, 0);

        limit.addPlacedBlock(player, block);
        Material blockType = block.getType();
        assertThat(limit.getPlayerLimit(player).getCount(blockType)).isEqualTo(1);

        player.simulateBlockBreak(block);
        assertThat(limit.getPlayerLimit(player).getCount(blockType)).isEqualTo(0);
    }

    @AfterEach
    public void afterEach() {

        plugin.unregisterEvents(limit);
    }
}
