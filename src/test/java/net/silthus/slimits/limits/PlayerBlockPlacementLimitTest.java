package net.silthus.slimits.limits;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import lombok.SneakyThrows;
import net.silthus.slimits.LimitsPlugin;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.assertj.core.groups.Tuple;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.junit.jupiter.api.*;

import java.io.File;
import java.nio.file.Path;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.byLessThan;

@DisplayName("player block placement limit")
public class PlayerBlockPlacementLimitTest {

    private static ServerMock server;
    private static LimitsPlugin plugin;
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

    @SneakyThrows
    @BeforeEach
    public void beforeEach() {
        File storage = new File(LimitsPlugin.PLUGIN_PATH, "storage");
        FileUtils.deleteDirectory(storage);

        player = server.addPlayer();
    }

    @Test
    @DisplayName("should create storage file with uuid and identifier")
    public void shouldCreateStorageFile() {
        Path storage = new File(new File(LimitsPlugin.PLUGIN_PATH, "storage"), player.getUniqueId().toString() + ".yaml").toPath();

        assertThat(storage.toFile()).doesNotExist();

        PlayerBlockPlacementLimit.create(player);

        assertThat(storage.toFile())
                .exists()
                .canRead()
                .canWrite()
                .isNotEmpty();
    }

    @Test
    @DisplayName("should set player uuid and name in config")
    public void shouldSetPlayerData() {

        PlayerBlockPlacementLimit limit = PlayerBlockPlacementLimit.create(player);

        assertThat(limit.getPlayerUUID())
                .isEqualTo(player.getUniqueId());
        assertThat(limit.getPlayerName())
                .isEqualTo(player.getName());
    }

    @Test
    @DisplayName("addBlock() should increase block type count")
    public void shouldIncreaseBlockTypeCount() {

        PlayerBlockPlacementLimit limit = PlayerBlockPlacementLimit.create(player);

        assertThat(limit.getCounts()).isEmpty();

        Block block = getBlock();
        limit.addBlock(block);

        assertThat(limit.getCounts())
                .hasSize(1)
                .containsEntry(block.getType(), 1);
    }

    @Test
    @DisplayName("addBlock() should add block to location list")
    public void shouldAddBlockLocation() {

        PlayerBlockPlacementLimit limit = PlayerBlockPlacementLimit.create(player);

        assertThat(limit.getLocations()).isEmpty();


        Block block = getBlock();
        limit.addBlock(block);

        assertThat(limit.getLocations())
                .hasSize(1)
                .contains(block.getLocation());
    }

    @Test
    @DisplayName("getCount() should get correct count")
    public void shouldGetCorrectCountForType() {

        PlayerBlockPlacementLimit limit = PlayerBlockPlacementLimit.create(player);

        Block block = getBlock();
        assertThat(limit.getCount(block.getType()))
                .isEqualTo(0);

        limit.addBlock(block);

        assertThat(limit.getCount(block.getType()))
                .isEqualTo(1);
    }

    @Test
    @DisplayName("addBlock() should not count already placed blocks")
    public void shouldNotAddDuplicateBlocks() {

        PlayerBlockPlacementLimit limit = PlayerBlockPlacementLimit.create(player);

        Block block = getBlock();
        assertThat(limit.getCount(block.getType()))
                .isEqualTo(0);

        limit.addBlock(block);
        assertThat(limit.getCount(block.getType()))
                .isEqualTo(1);

        limit.addBlock(block);
        assertThat(limit.getCount(block.getType()))
                .isEqualTo(1);
    }

    @Test
    @DisplayName("hasPlacedBlock() should check placed blocks")
    public void testHasPlacedBlock() {

        PlayerBlockPlacementLimit limit = PlayerBlockPlacementLimit.create(player);

        Block block = getBlock();

        assertThat(limit.hasPlacedBlock(block))
                .isFalse();

        limit.addBlock(block);
        assertThat(limit.hasPlacedBlock(block))
                .isTrue();

        limit.removeBlock(block);
        assertThat(limit.hasPlacedBlock(block))
                .isFalse();
    }

    @Test
    @DisplayName("removeBlock() should decrease counter")
    public void shouldRemoveBlockFromCount() {

        PlayerBlockPlacementLimit limit = PlayerBlockPlacementLimit.create(player);
        assertThat(limit.getCounts())
                .isEmpty();

        for (int i = 0; i < 10; i++) {
            limit.addBlock(getBlock());
        }

        Block block = getBlock();
        limit.addBlock(block);
        assertThat(limit.getCounts())
                .hasSizeBetween(1, 11)
                .containsKeys(block.getType());

        int count = limit.getCount(block.getType());

        limit.removeBlock(block);
        assertThat(limit.getCount(block.getType()))
                .isEqualTo(count - 1);
    }

    @Test
    @DisplayName("removeBlock() should remove block from locations")
    public void shouldRemoveBlockFromLocations() {

        PlayerBlockPlacementLimit limit = PlayerBlockPlacementLimit.create(player);
        assertThat(limit.getLocations())
                .isEmpty();


        Block block = getBlock();
        limit.getLocations().add(block.getLocation());

        limit.removeBlock(block);
        assertThat(limit.getLocations())
                .isEmpty();
    }

    private Block getBlock() {
        World world = server.getWorld("world");
        assertThat(world).isNotNull();

        return world.getBlockAt(RandomUtils.nextInt(256), RandomUtils.nextInt(128), RandomUtils.nextInt(256));
    }
}