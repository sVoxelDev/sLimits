package net.silthus.slimits.limits;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import net.silthus.slimits.LimitMode;
import net.silthus.slimits.LimitsPlugin;
import org.bukkit.Material;
import org.junit.jupiter.api.*;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("block placement config")
public class BlockPlacementLimitConfigTests {

    private static ServerMock server;
    private static LimitsPlugin plugin;
    private BlockPlacementLimitConfig config;

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
        config = new BlockPlacementLimitConfig(configPath);
        config.load();
    }

    @Test
    @DisplayName("should load limit mode")
    public void shouldLoadLimitMode() {
        assertThat(config.getMode()).isEqualTo(LimitMode.BLACKLIST);
    }

    @Test
    @DisplayName("should load block map with limits")
    public void shouldLoadBlockMapLimits() {
        Map<Material, Integer> blocks = config.getBlocks();

        assertThat(blocks)
                .isNotNull()
                .isNotEmpty()
                .hasSize(2)
                .containsEntry(Material.BEDROCK, 5)
                .containsEntry(Material.DIRT, 2);
    }

    @Test
    @DisplayName("should get empty optional limit if not configured")
    public void shouldGetEmptyLimitIfNotConfigured() {

        assertThat(config.getLimit(Material.AIR)).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("should load empty config with defaults")
    public void shouldSetConfigDefaults() {

        Path path = Path.of("src/test/resources", "test-limit2.yaml");
        BlockPlacementLimitConfig config = new BlockPlacementLimitConfig(path);
        config.load();

        assertThat(config.getMode()).isEqualTo(LimitMode.WHITELIST);

        assertThat(config.getBlocks()).isNotNull().hasSize(2);
    }
}
