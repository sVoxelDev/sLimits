package net.silthus.slimits;

import net.silthus.slimits.config.LimitsConfig;
import net.silthus.slimits.limits.BlockPlacementLimit;
import net.silthus.slimits.testing.TestBase;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.RegisteredListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class LimitsServiceTests extends TestBase {

    private LimitsService service;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        service = new LimitsService(plugin);
    }

    @Test
    void create() {

        assertThat(service)
                .extracting("plugin")
                .isNotNull()
                .isEqualTo(plugin);
    }

    @Test
    void loadLimits_createsBlockTrackers() {

        loadConfiguredLimits();

        assertThat(service.getBlockPlacementLimits())
                .isNotNull()
                .hasSize(2);
    }

    @Test
    void loadLimits_replacesExistingLimits() {

        loadConfiguredLimits();

        assertThat(service.getBlockPlacementLimits())
                .hasSize(2);

        service.loadLimits(new LimitsConfig());
        assertThat(service.getBlockPlacementLimits())
                .isEmpty();
    }

    @Test
    void loadLimits_registersCounterListeners() {

        loadConfiguredLimits();

        List<Listener> registeredListeners = getBlockPlaceListeners();

        assertThat(registeredListeners)
                .containsAll(service.getBlockPlacementLimits());
    }

    @Test
    void loadLimits_loadsStoredBlocksFromDisk() throws IOException {

        BlockPlacementLimit limit = new BlockPlacementLimit("stones", Material.STONE, 5, "test");
        server.getPluginManager().registerEvents(limit, plugin);
        player.addAttachment(plugin, limit.getPermission(), true);
        placeBlocks(Material.STONE, 5);
        limit.save(tempDir);

        service.loadLimits(plugin.getLimitsConfig());

        Optional<BlockPlacementLimit> stones = service.getBlockPlacementLimits()
                .stream().filter(blockPlacementLimit -> blockPlacementLimit.getKey().equals("stones"))
                .findFirst();
        assertThat(stones)
                .isPresent().get()
                .extracting(BlockPlacementLimit::getPlacedBlocks)
                .asList()
                .hasSize(5);
    }

    @Test
    void loadLimits_unregisterExistingListeners() {

        BlockPlacementLimit limit = new BlockPlacementLimit(Material.DIAMOND_BLOCK, 10);
        service.registerAndLoadLimit(limit);
        assertThat(service.getLimits()).hasSize(1);
        assertThat(getBlockPlaceListeners()).contains(limit);

        service.loadLimits(plugin.getLimitsConfig());

        assertThat(service.getLimits()).hasSize(2);
        assertThat(service.getLimits()).doesNotContain(limit);
        assertThat(getBlockPlaceListeners()).doesNotContain(limit);
    }

    @Test
    void save_storesLimits_toDisk() {

        loadConfiguredLimits();
        placeBlocks(Material.STONE, 2);

        service.saveLimits();

        assertThat(tempDir.list())
                .containsExactly(
                        "bedrock.yml",
                        "stones.yml"
                );
    }

    @Test
    void reload_reloadsLimitsFromDisk() {

        assertThat(service.getLimits()).isEmpty();

        service.reload();

        assertThat(service.getLimits())
                .hasSize(2);
    }

    @Test
    void reload_unregistersOldLimits() {

        BlockPlacementLimit limit = new BlockPlacementLimit(Material.IRON_BLOCK, 10);
        service.registerAndLoadLimit(limit);

        service.reload();

        assertThat(getBlockPlaceListeners()).doesNotContain(limit);
        assertThat(service.getLimits()).doesNotContain(limit);
    }

    @Test
    void reload_savesLimitCacheBeforeReload() {

        loadConfiguredLimits();
        placeBlocks(Material.STONE, 10);
        assertThat(service.getLimits().stream().filter(blockPlacementLimit -> blockPlacementLimit.getType() == Material.STONE).findFirst())
                .isPresent().get()
                .extracting(BlockPlacementLimit::getPlacedBlocks)
                .asList().hasSize(10);

        service.reload();

        assertThat(service.getLimits().stream().filter(blockPlacementLimit -> blockPlacementLimit.getType() == Material.STONE).findFirst())
                .isPresent().get()
                .extracting(BlockPlacementLimit::getPlacedBlocks)
                .asList().hasSize(10);
        assertThat(tempDir.list())
                .contains(
                        "bedrock.yml",
                        "stones.yml"
                );
    }

    private void loadConfiguredLimits() {

        service.loadLimits(plugin.getLimitsConfig());
        for (BlockPlacementLimit limit : service.getBlockPlacementLimits()) {
            player.addAttachment(plugin, limit.getPermission(), true);
        }
    }

    private List<Listener> getBlockPlaceListeners() {
        return Arrays.stream(BlockPlaceEvent.getHandlerList().getRegisteredListeners())
                .map(RegisteredListener::getListener)
                .filter(listener -> listener instanceof BlockPlacementLimit)
                .collect(Collectors.toList());
    }
}
