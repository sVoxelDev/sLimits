package net.silthus.slimits.listener;

import net.silthus.slimits.LimitsService;
import net.silthus.slimits.TestBase;
import net.silthus.slimits.events.DecreaseLimitEvent;
import net.silthus.slimits.events.IncreaseLimitEvent;
import net.silthus.slimits.events.LimitReachedEvent;
import net.silthus.slimits.limits.BlockPlacementLimit;
import org.bukkit.Material;
import org.bukkit.event.HandlerList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LimitEventsListenerTest extends TestBase {

    private LimitsService service;
    private LimitEventsListener listener;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        service = plugin.getLimitsService();
        listener = plugin.getLimitEventsListener();
    }

    @Test
    void create_registersListener() {

        assertThat(plugin.getLimitEventsListener()).isNotNull();
        assertThat(HandlerList.getRegisteredListeners(plugin))
                .anyMatch(registeredListener -> registeredListener.getListener().equals(plugin.getLimitEventsListener()));
    }

    @Test
    void onLimitReached_cancelsBlockPlacement_false_ifHigherLimitExists() {

        loadConfiguredLimits();
        BlockPlacementLimit limit = new BlockPlacementLimit(Material.STONE, 150, "foo");
        service.registerAndLoadLimit(limit);

        LimitReachedEvent event = new LimitReachedEvent(limit, player, 120);
        event.setCancelBlockPlacement(true);
        listener.onLimitReached(event);

        assertThat(event.isCancelBlockPlacement()).isFalse();
    }

    @Test
    void onLimitIncrease_silencesLowerLimit_ifHigherLimitExists() {

        loadConfiguredLimits();
        BlockPlacementLimit limit = new BlockPlacementLimit(Material.STONE, 150, "foo");
        service.registerAndLoadLimit(limit);

        IncreaseLimitEvent event = new IncreaseLimitEvent(limit, player, 5, 6);
        listener.onLimitIncrease(event);

        assertThat(event.isSilent()).isTrue();
    }

    @Test
    void onLimitIncrease_doesNotSilenceHighestLimit() {

        loadConfiguredLimits();

        IncreaseLimitEvent event = new IncreaseLimitEvent(service.getBlockPlacementLimits(Material.STONE).get(0), player, 5, 6);
        listener.onLimitIncrease(event);

        assertThat(event.isSilent()).isFalse();
    }

    @Test
    void onLimitDecrease_silencesLowerLimit_ifHigherLimitExists() {

        loadConfiguredLimits();
        BlockPlacementLimit limit = new BlockPlacementLimit(Material.STONE, 150, "foo");
        service.registerAndLoadLimit(limit);

        DecreaseLimitEvent event = new DecreaseLimitEvent(limit, player, 5, 6);
        listener.onLimitDecrease(event);

        assertThat(event.isSilent()).isTrue();
    }

    @Test
    void onLimitDecrease_doesNotSilenceHighestLimit() {

        loadConfiguredLimits();

        DecreaseLimitEvent event = new DecreaseLimitEvent(service.getBlockPlacementLimits(Material.STONE).get(0), player, 5, 6);
        listener.onLimitDecrease(event);

        assertThat(event.isSilent()).isFalse();
    }
}