package net.silthus.slimits.limits;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.silthus.slimits.LimitsPlugin;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.junit.jupiter.api.*;

import java.io.File;
import java.nio.file.Path;

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
        plugin.registerEvents(limit);

        player = server.addPlayer();
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
    }

    @AfterEach
    public void afterEach() {

        player.remove();
        plugin.unregisterEvents(limit);
    }
}
