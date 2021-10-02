package net.silthus.slimits.events;

import net.silthus.slimits.TestBase;
import net.silthus.slimits.limits.BlockPlacementLimit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
    }

    static class LimitEventListener implements Listener {
        @EventHandler
        public void onIncreaseLimit(IncreaseLimitEvent event) {
        }
    }
}
