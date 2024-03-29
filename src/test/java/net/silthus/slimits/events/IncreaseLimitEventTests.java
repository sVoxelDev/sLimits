package net.silthus.slimits.events;

import net.silthus.slimits.TestBase;
import net.silthus.slimits.limits.BlockPlacementLimit;
import net.silthus.slimits.limits.LimitType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class IncreaseLimitEventTests extends TestBase {

    private BlockPlacementLimit limit;
    private IncreaseLimitEventTests.LimitEventListener listener;
    private ArgumentCaptor<IncreaseLimitEvent> captor;

    @BeforeEach
    public void setUp() {
        super.setUp();

        captor = ArgumentCaptor.forClass(IncreaseLimitEvent.class);

        limit = new BlockPlacementLimit(Material.STONE, 5, "foo");
        server.getPluginManager().registerEvents(limit, plugin);
        player.addAttachment(plugin, limit.getPermission(), true);

        listener = spy(new LimitEventListener());
        server.getPluginManager().registerEvents(listener, plugin);
    }

    @Test
    void limitIncreases_isFiredOnBlockPlace() {

        placeBlock(Material.STONE, 1, 2, 3);

        assertThat(limit.getCount(player)).isOne();
        verify(listener).onIncreaseLimit(captor.capture());

        IncreaseLimitEvent event = captor.getValue();
        assertThat(event)
                .isInstanceOf(Cancellable.class)
                .isInstanceOf(Event.class)
                .extracting(
                        IncreaseLimitEvent::getLimitType,
                        IncreaseLimitEvent::getType,
                        IncreaseLimitEvent::getPlayer,
                        IncreaseLimitEvent::getLimit,
                        IncreaseLimitEvent::getCount,
                        IncreaseLimitEvent::getNewCount,
                        IncreaseLimitEvent::getPermission,
                        IncreaseLimitEvent::isCancelled
                ).contains(
                        LimitType.BLOCK_PLACEMENT,
                        Material.STONE,
                        player,
                        5,
                        0,
                        1,
                        "foo"
                );
        assertThat(player.nextMessage())
                .isEqualTo(ChatColor.GRAY + "Your limit for placing stone "
                        + ChatColor.RED + "increased"
                        + ChatColor.GRAY + ": 1/5."
                );
    }

    @Test
    void cancelEvent_doesNotIncreaseCounter() {

        listener.cancel = true;

        placeBlock(Material.STONE, 1, 2, 3);

        verify(listener).onIncreaseLimit(captor.capture());
        assertThat(limit.getCount(player)).isZero();

        assertThat(captor.getValue())
                .extracting(
                        IncreaseLimitEvent::getCount,
                        IncreaseLimitEvent::getNewCount,
                        IncreaseLimitEvent::isCancelled
                ).contains(
                        0,
                        1,
                        true
                );
    }

    @Test
    void silent_doesNotPrintMessage_toPlayer() {

        listener.silent = true;

        placeBlock(Material.STONE, 1, 2, 3);

        assertThat(limit.getCount(player)).isOne();
        verify(listener).onIncreaseLimit(captor.capture());

        assertThat(player.nextMessage()).isNull();
    }

    static class LimitEventListener implements Listener {
        boolean cancel = false;
        boolean silent = false;

        @EventHandler
        public void onIncreaseLimit(IncreaseLimitEvent event) {
            event.setCancelled(cancel);
            event.setSilent(silent);
        }
    }
}
