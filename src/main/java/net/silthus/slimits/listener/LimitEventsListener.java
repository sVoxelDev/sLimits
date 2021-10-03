package net.silthus.slimits.listener;

import net.silthus.slimits.SLimitsPlugin;
import net.silthus.slimits.events.DecreaseLimitEvent;
import net.silthus.slimits.events.IncreaseLimitEvent;
import net.silthus.slimits.events.LimitReachedEvent;
import net.silthus.slimits.events.SLimitEvent;
import net.silthus.slimits.limits.BlockPlacementLimit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Comparator;
import java.util.Optional;

public class LimitEventsListener implements Listener {

    private final SLimitsPlugin plugin;

    public LimitEventsListener(SLimitsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onLimitReached(LimitReachedEvent event) {

        if (isHighestLimit(event)) return;

        event.setCancelBlockPlacement(false);
    }

    @EventHandler(ignoreCancelled = true)
    public void onLimitIncrease(IncreaseLimitEvent event) {

        if (isHighestLimit(event)) return;

        event.setSilent(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onLimitDecrease(DecreaseLimitEvent event) {

        if (isHighestLimit(event)) return;

        event.setSilent(true);
    }

    private Boolean isHighestLimit(SLimitEvent event) {

        Optional<BlockPlacementLimit> highestLimit = getHighestBlockPlacementLimit(event.getType(), event.getPlayer());
        return highestLimit.map(limit -> limit.getKey().equals(event.getKey()))
                .orElse(true);
    }

    private Optional<BlockPlacementLimit> getHighestBlockPlacementLimit(Material type, Player player) {

        return plugin.getLimitsService().getBlockPlacementLimits(type)
                .stream().filter(limit -> limit.hasPermission(player))
                .max(Comparator.comparingInt(BlockPlacementLimit::getLimit));
    }
}
