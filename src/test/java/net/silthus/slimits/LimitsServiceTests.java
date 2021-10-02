package net.silthus.slimits;

import net.silthus.slimits.config.LimitsConfig;
import net.silthus.slimits.limits.BlockPlacementLimit;
import net.silthus.slimits.limits.PlacedBlock;
import net.silthus.slimits.limits.PlayerLimit;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.RegisteredListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

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

        assertThat(service.getLimits())
                .isNotNull()
                .hasSize(2);
    }

    @Test
    void loadLimits_replacesExistingLimits() {

        loadConfiguredLimits();

        assertThat(service.getLimits())
                .hasSize(2);

        service.loadLimits(new LimitsConfig());
        assertThat(service.getLimits())
                .isEmpty();
    }

    @Test
    void loadLimits_registersCounterListeners() {

        loadConfiguredLimits();

        List<Listener> registeredListeners = getBlockPlaceListeners();

        assertThat(registeredListeners)
                .containsAll(service.getLimits());
    }

    @Test
    void loadLimits_loadsStoredBlocksFromDisk() throws IOException {

        BlockPlacementLimit limit = new BlockPlacementLimit("stones", Material.STONE, 5, "test");
        server.getPluginManager().registerEvents(limit, plugin);
        player.addAttachment(plugin, limit.getPermission(), true);
        placeBlocks(Material.STONE, 5);
        limit.save(storageDir);

        service.loadLimits(plugin.getLimitsConfig());

        Optional<BlockPlacementLimit> stones = service.getLimits()
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

        assertThat(storageDir.list())
                .containsOnly(
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
        assertThat(getPlacedBlocks(Material.STONE))
                .hasSize(10);

        service.reload();

        assertThat(getPlacedBlocks(Material.STONE))
                .hasSize(10);
        assertThat(storageDir.list())
                .contains(
                        "bedrock.yml",
                        "stones.yml"
                );
    }

    @Test
    void reload_limitCountForPlayerIsSameAfterReload() {

        loadConfiguredLimits();
        placeBlocks(Material.STONE, 10);
        assertThat(getLimit(Material.STONE).getCount(player)).isEqualTo(10);

        service.reload();

        assertThat(getLimit(Material.STONE).getCount(player)).isEqualTo(10);
    }

    @Test
    void getPlayerLimits_returnsThePlayersLimits() {

        loadConfiguredLimits();

        List<PlayerLimit> limits = service.getPlayerLimits(player);
        assertThat(limits)
                .isNotNull()
                .hasSize(2);
    }

    @Test
    void save_isCalledPeriodically_byTask() {

        plugin.getLimitsService().schedulePeriodicSaveTask();

        server.getScheduler().performTicks(plugin.getLimitsConfig().getSaveIntervalTicks());

        verify(plugin.getLimitsService()).saveLimits();
    }

    @Test
    void saveTask_notScheduledIfTicksIsBelowZero() {

        plugin.getLimitsConfig().setSaveIntervalTicks(0);
        plugin.getLimitsService().schedulePeriodicSaveTask();

        server.getScheduler().performTicks(2000);

        verify(plugin.getLimitsService(), never()).saveLimits();
    }

    @Test
    void reload_cancelsAndReschedulesSaveTask() {

        plugin.getLimitsConfig().setSaveIntervalTicks(0);
        plugin.getLimitsService().schedulePeriodicSaveTask();

        server.getScheduler().performTicks(10);

        verify(plugin.getLimitsService(), never()).saveLimits();

        plugin.getLimitsConfig().setSaveIntervalTicks(20);

        plugin.getLimitsService().reload();

        server.getScheduler().performTicks(20);
        verify(plugin.getLimitsService(), times(2)).saveLimits();
    }

    @Override
    protected void loadConfiguredLimits() {

        service.loadLimits(plugin.getLimitsConfig());
        for (BlockPlacementLimit limit : service.getLimits()) {
            player.addAttachment(plugin, limit.getPermission(), true);
        }
    }

    private List<Listener> getBlockPlaceListeners() {
        return Arrays.stream(BlockPlaceEvent.getHandlerList().getRegisteredListeners())
                .map(RegisteredListener::getListener)
                .filter(listener -> listener instanceof BlockPlacementLimit)
                .collect(Collectors.toList());
    }

    private BlockPlacementLimit getLimit(Material type) {

        return service.getLimits().stream()
                .filter(blockPlacementLimit -> blockPlacementLimit.getType() == type)
                .findFirst().orElseThrow();
    }

    private List<PlacedBlock> getPlacedBlocks(Material type) {

        return getLimit(type).getPlacedBlocks();
    }
}
