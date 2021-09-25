package net.silthus.slimits;

import net.silthus.slimits.testing.TestBase;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.RegisteredListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
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

        assertThat(service.getBlockPlacementCounter())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void loadLimits_replacesExistingLimits() {

        loadConfiguredLimits();

        assertThat(service.getBlockPlacementCounter())
                .hasSize(1);

        service.loadLimits(new LimitsConfig());
        assertThat(service.getBlockPlacementCounter())
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
                .containsAll(service.getBlockPlacementCounter());
    }

    private void loadConfiguredLimits() {
        service.loadLimits(new LimitsConfig().load(plugin.getConfig()));
    }
}
