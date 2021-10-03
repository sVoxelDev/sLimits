package net.silthus.slimits.events;

import net.silthus.slimits.TestBase;
import net.silthus.slimits.limits.BlockPlacementLimit;
import net.silthus.slimits.limits.LimitType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class DecreaseLimitEventTests extends TestBase {

    private BlockPlacementLimit limit;
    private LimitEventListener listener;
    private ArgumentCaptor<DecreaseLimitEvent> captor;

    @BeforeEach
    public void setUp() {
        super.setUp();

        captor = ArgumentCaptor.forClass(DecreaseLimitEvent.class);

        limit = new BlockPlacementLimit(Material.STONE, 5, "foo");
        server.getPluginManager().registerEvents(limit, plugin);
        player.addAttachment(plugin, limit.getPermission(), true);

        listener = spy(new LimitEventListener());
        server.getPluginManager().registerEvents(listener, plugin);
    }

    @Test
    void limitDecreases_isFiredOnBlockBreak() {

        placeBlock(Material.STONE, 1, 2, 3);
        assertThat(limit.getCount(player)).isOne();

        breakBlock(Material.STONE, 1, 2, 3);
        assertThat(limit.getCount(player)).isZero();
        verify(listener).onDecreaseLimit(captor.capture());

        DecreaseLimitEvent event = captor.getValue();
        assertThat(event)
                .isInstanceOf(Cancellable.class)
                .isInstanceOf(Event.class)
                .extracting(
                        DecreaseLimitEvent::getLimitType,
                        DecreaseLimitEvent::getType,
                        DecreaseLimitEvent::getPlayer,
                        DecreaseLimitEvent::getLimit,
                        DecreaseLimitEvent::getCount,
                        DecreaseLimitEvent::getNewCount,
                        DecreaseLimitEvent::getPermission,
                        DecreaseLimitEvent::isCancelled
                ).contains(
                        LimitType.BLOCK_PLACEMENT,
                        Material.STONE,
                        player,
                        5,
                        1,
                        0,
                        "foo"
                );

        player.nextMessage();
        assertThat(player.nextMessage())
                .isEqualTo(ChatColor.GRAY + "Your limit for placing stone "
                        + ChatColor.GREEN + "decreased"
                        + ChatColor.GRAY + ": 0/5."
                );
    }

    @Test
    void cancelEvent_doesNotDecreaseCounter() {

        listener.cancel = true;

        placeBlock(Material.STONE, 1, 2, 3);
        breakBlock(Material.STONE, 1, 2, 3);

        verify(listener).onDecreaseLimit(captor.capture());
        assertThat(limit.getCount(player)).isOne();

        assertThat(captor.getValue())
                .extracting(
                        DecreaseLimitEvent::getCount,
                        DecreaseLimitEvent::getNewCount,
                        DecreaseLimitEvent::isCancelled
                ).contains(
                        1,
                        0,
                        true
                );
    }

    @Test
    void silent_doesNotPrintMessage_toPlayer() {

        listener.silent = true;

        placeBlock(Material.STONE, 1, 2, 3);
        breakBlock(Material.STONE, 1, 2, 3);

        assertThat(limit.getCount(player)).isZero();
        verify(listener).onDecreaseLimit(captor.capture());

        player.nextMessage();
        assertThat(player.nextMessage()).isNull();
    }

    static class LimitEventListener implements Listener {
        boolean cancel = false;
        boolean silent = false;

        @EventHandler
        public void onDecreaseLimit(DecreaseLimitEvent event) {
            event.setCancelled(cancel);
            event.setSilent(silent);
        }
    }
}
