package net.silthus.slimits;

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

        List<Listener> registeredListeners = Arrays.stream(BlockPlaceEvent.getHandlerList().getRegisteredListeners())
                .map(RegisteredListener::getListener)
                .filter(listener -> listener instanceof BlockPlacementLimit)
                .collect(Collectors.toList());

        assertThat(registeredListeners)
                .containsAll(service.getBlockPlacementLimits());
    }

    @Test
    void loadLimits_loadsStoredBlocksFromDisk(@TempDir File temp) throws IOException {

        plugin.getLimitsConfig().getStorage().setBlockPlacement(temp.getAbsolutePath());

        BlockPlacementLimit limit = new BlockPlacementLimit("stones", Material.STONE, 5, "test");
        server.getPluginManager().registerEvents(limit, plugin);
        player.addAttachment(plugin, limit.getPermission(), true);
        placeBlocks(Material.STONE, 5);
        limit.save(temp);

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
    void save_storesLimits_toDisk(@TempDir File temp) {

        loadConfiguredLimits();
        placeBlocks(Material.STONE, 2);

        plugin.getLimitsConfig().getStorage().setBlockPlacement(temp.getAbsolutePath());

        service.saveLimits();

        assertThat(temp.list())
                .containsExactly(
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
}
