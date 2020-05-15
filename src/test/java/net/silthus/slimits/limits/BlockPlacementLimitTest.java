package net.silthus.slimits.limits;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.google.common.collect.Maps;
import net.silthus.slimits.Constants;
import net.silthus.slimits.LimitsManager;
import net.silthus.slimits.LimitsPlugin;
import org.apache.commons.lang.math.RandomUtils;
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
    private static LimitsManager limitsManager;
    private BlockPlacementLimit limit;
    private PlayerMock player;

    @BeforeAll
    public static void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.loadWith(LimitsPlugin.class, new File("src/test/resources/plugin.yml"));
        limitsManager = new LimitsManager(plugin);
        limitsManager.initializeStorage();
    }

    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }

    @BeforeEach
    public void beforeEach() {
        Path configPath = new File("src/test/resources", "test-limit1.yaml").toPath();
        BlockPlacementLimitConfig config = new BlockPlacementLimitConfig(configPath);
        config.load();

        limit = new BlockPlacementLimit(limitsManager);
        plugin.registerEvents(limit);

        player = server.addPlayer();
        player.addAttachment(plugin, Constants.PERMISSION_PREFIX + "test", true);
    }

    @Test
    @DisplayName("should increase limit count in memory")
    public void shouldIncreaseLimitCount() {

        Block block = getBlock();

        PlayerBlockPlacementLimit playerLimit = limitsManager.getPlayerLimit(player);
        playerLimit.addBlock(block);
        assertThat(playerLimit).isNotNull();

        assertThat(playerLimit.getCount(block.getType())).isEqualTo(1);

        for (int i = 0; i < 10; i++) {
            playerLimit.addBlock(getBlock());
        }

        assertThat(playerLimit.getCount(block.getType()))
                .isBetween(1, 11);
    }

    @Test
    @DisplayName("should decrease limit count in memory")
    public void shouldDecreaseLimitCount() {

        World world = server.getWorld("world");
        assertThat(world).isNotNull();

        PlayerBlockPlacementLimit playerLimit = limitsManager.getPlayerLimit(player);
        for (int i = 0; i < 5; i++) {
            playerLimit.addBlock(world.getBlockAt(i, i, i));
        }

        Block block = world.getBlockAt(0, 0, 0);

        assertThat(playerLimit.getCounts()).hasSizeBetween(1, 5);
        assertThat(playerLimit.getLocations(block.getType())).hasSize(5);

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

        PlayerBlockPlacementLimit playerLimit = limitsManager.getPlayerLimit(player);
        playerLimit.addBlock(block);
        Material blockType = block.getType();
        assertThat(playerLimit.getCount(blockType)).isEqualTo(1);

        player.simulateBlockBreak(block);
        assertThat(playerLimit.getCount(blockType)).isEqualTo(0);
    }

    private Block getBlock() {
        World world = server.getWorld("world");
        assertThat(world).isNotNull();

        return world.getBlockAt(RandomUtils.nextInt(256), RandomUtils.nextInt(128), RandomUtils.nextInt(256));
    }

    @AfterEach
    public void afterEach() {

        plugin.unregisterEvents(limit);
    }
}
