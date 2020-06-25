package net.silthus.slimits.limits;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import lombok.Getter;
import lombok.SneakyThrows;
import net.silthus.slimits.LimitMode;
import net.silthus.slimits.LimitsPlugin;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.junit.jupiter.api.*;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("player block placement limit")
public class PlayerBlockPlacementLimitTest {

    @Getter
    private static ServerMock server;
    @Getter
    private PlayerMock player;
    @Getter
    private PlayerBlockPlacementLimit playerLimit;

    @BeforeAll
    public static void setUp() {
        server = MockBukkit.mock();
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

        playerLimit = new PlayerBlockPlacementLimit(player);
    }

    @AfterEach
    public void afterEach() {
        server.setPlayers(0);
        playerLimit = null;
    }

    @Test
    @DisplayName("should set player uuid and name in config")
    public void shouldSetPlayerData() {

        assertThat(getPlayerLimit().getPlayerUUID())
                .isEqualTo(player.getUniqueId());
        assertThat(getPlayerLimit().getPlayerName())
                .isEqualTo(player.getName());
    }

    @Test
    @DisplayName("addBlock() should increase block type count")
    public void shouldIncreaseBlockTypeCount() {

        assertThat(getPlayerLimit().getCounts()).isEmpty();

        Block block = getBlock();
        getPlayerLimit().addBlock(block);

        assertThat(getPlayerLimit().getCounts())
                .hasSize(1)
                .containsEntry(block.getType(), 1);
    }

    @Test
    @DisplayName("addBlock() should add block to location list")
    public void shouldAddBlockLocation() {

        assertThat(getPlayerLimit().getLocations(Material.AIR)).isEmpty();


        Block block = getBlock();
        getPlayerLimit().addBlock(block);

        assertThat(getPlayerLimit().getLocations(block.getType()))
                .hasSize(1)
                .contains(block.getLocation());
    }

    @Test
    @DisplayName("getCount() should get correct count")
    public void shouldGetCorrectCountForType() {

        Block block = getBlock();
        assertThat(getPlayerLimit().getCount(block.getType()))
                .isEqualTo(0);

        getPlayerLimit().addBlock(block);

        assertThat(getPlayerLimit().getCount(block.getType()))
                .isEqualTo(1);
    }

    @Test
    @DisplayName("addBlock() should not count already placed blocks")
    public void shouldNotAddDuplicateBlocks() {

        Block block = getBlock();
        assertThat(getPlayerLimit().getCount(block.getType()))
                .isEqualTo(0);

        getPlayerLimit().addBlock(block);
        assertThat(getPlayerLimit().getCount(block.getType()))
                .isEqualTo(1);

        getPlayerLimit().addBlock(block);
        assertThat(getPlayerLimit().getCount(block.getType()))
                .isEqualTo(1);
    }

    @Test
    @DisplayName("hasPlacedBlock() should check placed blocks")
    public void testHasPlacedBlock() {

        Block block = getBlock();

        assertThat(getPlayerLimit().hasPlacedBlock(block))
                .isFalse();

        getPlayerLimit().addBlock(block);
        assertThat(getPlayerLimit().hasPlacedBlock(block))
                .isTrue();

        getPlayerLimit().removeBlock(block);
        assertThat(getPlayerLimit().hasPlacedBlock(block))
                .isFalse();
    }

    @Test
    @DisplayName("removeBlock() should decrease counter")
    public void shouldRemoveBlockFromCount() {

        assertThat(getPlayerLimit().getCounts())
                .isEmpty();

        for (int i = 0; i < 10; i++) {
            getPlayerLimit().addBlock(getBlock());
        }

        Block block = getBlock();
        getPlayerLimit().addBlock(block);
        assertThat(getPlayerLimit().getCounts())
                .hasSizeBetween(1, 11)
                .containsKeys(block.getType());

        int count = getPlayerLimit().getCount(block.getType());

        getPlayerLimit().removeBlock(block);
        assertThat(getPlayerLimit().getCount(block.getType()))
                .isEqualTo(count - 1);
    }

    @Test
    @DisplayName("removeBlock() should remove block from locations")
    public void shouldRemoveBlockFromLocations() {

        assertThat(getPlayerLimit().getLocations(Material.AIR))
                .isEmpty();


        Block block = getBlock();
        getPlayerLimit().getLocations(block.getType()).add(block.getLocation());

        getPlayerLimit().removeBlock(block);
        assertThat(getPlayerLimit().getLocations(block.getType()))
                .isEmpty();
    }

    private Block getBlock() {
        World world = server.getWorld("world");
        assertThat(world).isNotNull();

        return world.getBlockAt(RandomUtils.nextInt(256), RandomUtils.nextInt(128), RandomUtils.nextInt(256));
    }

    @Nested
    @DisplayName("config modes")
    public class ConfigModes {

        private final BlockPlacementLimitConfig CONFIG1 = new BlockPlacementLimitConfig(
                new File("src/test/resources/", "absolute_config1.yaml").toPath());
        private final BlockPlacementLimitConfig CONFIG2 = new BlockPlacementLimitConfig(
                new File("src/test/resources/", "absolute_config2.yaml").toPath());

        @BeforeEach
        public void beforeEach() {
            CONFIG1.load();
            CONFIG2.load();
        }

        @Test
        @DisplayName("ABSOLUTE: should only apply one absolute config")
        public void shouldDiscardMultipleAbsoluteConfigs() {

            assertThat(getPlayerLimit().getLimits()).isEmpty();

            getPlayerLimit().registerLimitConfig(CONFIG1);

            assertThat(playerLimit.getLimits())
                    .hasSize(2)
                    .containsEntry(Material.BEDROCK, 5)
                    .containsEntry(Material.DIRT, 2);

            getPlayerLimit().registerLimitConfig(CONFIG2);

            assertThat(playerLimit.getLimits())
                    .hasSize(2)
                    .containsEntry(Material.BEDROCK, 10)
                    .containsEntry(Material.DIRT, 10);
        }

        @Test
        @DisplayName("ADD: should add up multiple configs")
        public void shouldAddMultipleConfigLimits() {

            CONFIG1.setMode(LimitMode.ADD);
            CONFIG2.setMode(LimitMode.ADD);

            assertThat(getPlayerLimit().getLimits()).isEmpty();

            getPlayerLimit().registerLimitConfig(CONFIG1);
            getPlayerLimit().registerLimitConfig(CONFIG2);

            assertThat(getPlayerLimit().getLimits())
                    .hasSize(2)
                    .containsEntry(Material.DIRT, 12)
                    .containsEntry(Material.BEDROCK, 15);
        }

        public void afterEach() {

        }
    }
}