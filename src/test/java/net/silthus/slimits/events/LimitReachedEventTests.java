package net.silthus.slimits.events;

import lombok.Value;
import net.silthus.slimits.limits.BlockPlacementLimit;
import net.silthus.slimits.limits.LimitType;
import net.silthus.slimits.TestBase;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class LimitReachedEventTests extends TestBase {

    private BlockPlacementLimit limit;
    private LimitEventListener listener;
    private ArgumentCaptor<LimitReachedEvent> eventCaptor;
    private PermissionAttachment permission;

    @BeforeEach
    public void setUp() {
        super.setUp();

        eventCaptor = ArgumentCaptor.forClass(LimitReachedEvent.class);

        limit = new BlockPlacementLimit(Material.STONE, 3, "test");
        server.getPluginManager().registerEvents(limit, plugin);

        listener = spy(new LimitEventListener());
        server.getPluginManager().registerEvents(listener, plugin);

        permission = player.addAttachment(plugin, limit.getPermission(), true);
    }

    @Test
    void placeBlock_firesLimitReachedEvent_ifLimitIsReached() {

        triggerLimitReachedEvent();
    }

    @Test
    void placeBlockOnce_doesNotFire_LimitReachedEvent() {

        placeBlock(Material.STONE, 1, 2, 3);

        verify(listener, never()).onLimitReached(any());
    }

    @Test
    void limitReachedEvent_isOfType_blockPlacement() {

        LimitReachedEvent event = triggerLimitReachedEvent();

        assertThat(event)
                .extracting(LimitReachedEvent::getLimitType)
                .isEqualTo(LimitType.BLOCK_PLACEMENT);
    }

    @Test
    void limitReachedEvent_containsProperties() {

        LimitReachedEvent event = triggerLimitReachedEvent();

        assertThat(event)
                .isInstanceOf(Event.class)
                .extracting(
                        LimitReachedEvent::getLimit,
                        LimitReachedEvent::getCount,
                        LimitReachedEvent::getPlayer
                ).contains(
                        3,
                        3,
                        player
                );
    }

    @Test
    void limitReachedEvent_twiceInARow() {

        triggerLimitReachedEvent();
        placeBlock(Material.STONE, 10, 20, 30);

        verify(listener, times(2))
                .onLimitReached(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getCount())
                .isEqualTo(4);
    }

    @Test
    void limitReachedEvent_cancelsBlockPlaceEvent() {

        LimitEvents events = triggerCancelledLimitReachedEvent();

        assertThat(events.limitReachedEvent.isCancelBlockPlacement())
                .isTrue();
        assertThat(events.blockPlaceEvent.isCancelled())
                .isTrue();
    }

    @Test
    void uncancelledLimitReached_countsBlockNormally() {

        triggerLimitReachedEvent();
        assertThat(limit.getCount(player)).isEqualTo(4);
    }

    @Test
    void placeBlock_doesNotCount_cancelledBlockPlacement() {

        LimitEvents events = triggerCancelledLimitReachedEvent();

        assertThat(events.limitReachedEvent.isCancelBlockPlacement())
                .isTrue();
        assertThat(limit.getCount(player))
                .isEqualTo(3);
    }

    @Test
    void placeBlock_doesNotFireEvent_closeToLimit() {

        placeBlocks(Material.STONE, 3);

        verify(listener, never())
                .onLimitReached(any());
    }

    @Test
    void placeBlock_doesNotFireEvent_ifWrongTypeIsPlaced() {

        placeBlocks(Material.BEDROCK, 5);

        verify(listener, never())
                .onLimitReached(any());
    }

    @Test
    void event_onlyFires_ifPlayerHasThePermission() {

        player.removeAttachment(permission);
        assertThat(player.hasPermission(limit.getPermission()))
                .isFalse();

        placeBlocks(Material.STONE, 4);

        verify(listener, never())
                .onLimitReached(any());
    }

    private LimitReachedEvent triggerLimitReachedEvent() {

        placeBlocks(Material.STONE, 4);
        verify(listener, atLeastOnce())
                .onLimitReached(eventCaptor.capture());
        return eventCaptor.getValue();
    }

    private LimitEvents triggerCancelledLimitReachedEvent() {

        HandlerList.unregisterAll(this.listener);

        LimitCancelPlacementListener listener = spy(new LimitCancelPlacementListener());
        server.getPluginManager().registerEvents(listener, plugin);

        placeBlocks(Material.STONE, 3);
        BlockPlaceEvent blockPlaceEvent = createBlockPlaceEvent(createBlock(Material.STONE, 10, 20, 30));
        callEvent(blockPlaceEvent);

        verify(listener, atLeastOnce())
                .onLimitReached(eventCaptor.capture());
        return new LimitEvents(blockPlaceEvent, eventCaptor.getValue());
    }

    static class LimitEventListener implements Listener {
        @EventHandler
        public void onLimitReached(LimitReachedEvent event) {
            event.setCancelBlockPlacement(false);
        }
    }

    static class LimitCancelPlacementListener implements Listener {
        @EventHandler
        public void onLimitReached(LimitReachedEvent event) {
            event.setCancelBlockPlacement(true);
        }
    }

    @Value
    static class LimitEvents {
        BlockPlaceEvent blockPlaceEvent;
        LimitReachedEvent limitReachedEvent;
    }
}
