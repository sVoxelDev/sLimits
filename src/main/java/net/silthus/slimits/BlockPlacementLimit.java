package net.silthus.slimits;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.*;
import java.util.stream.Collectors;

public class BlockPlacementLimit implements Listener {

    @Getter
    private final Material type;
    @Getter
    private final int limit;
    private final List<PlacedBlock> placedBlocks = new ArrayList<>();

    public BlockPlacementLimit(Material material, int limit) {
        this.type = material;
        this.limit = limit;
    }

    public BlockPlacementLimit(BlockPlacementLimitConfig config) {
        this(config.getType(), config.getLimit());
    }

    public int getCount(Player player) {
        return getPlacedBlocks(player).size();
    }

    public List<PlacedBlock> getPlacedBlocks(Player player) {

        return placedBlocks.stream()
                .filter(placedBlock -> placedBlock.isOwner(player))
                .collect(Collectors.toList());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {

        if (hasReachedLimit(event.getPlayer()))
            fireAndHandleLimitReachedEvent(event);
        else
            addPlacedBlock(event.getPlayer(), event.getBlockPlaced());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {

        removePlacedBlock(event.getBlock());
    }

    private void fireAndHandleLimitReachedEvent(BlockPlaceEvent event) {

        LimitReachedEvent limitReachedEvent = fireLimitReachedEvent(event);
        if (limitReachedEvent.isCancelBlockPlacement())
            event.setCancelled(true);
        else
            addPlacedBlock(event.getPlayer(), event.getBlockPlaced());
    }

    private LimitReachedEvent fireLimitReachedEvent(BlockPlaceEvent blockPlaceEvent) {

        Player player = blockPlaceEvent.getPlayer();
        LimitReachedEvent event = new LimitReachedEvent(player, getLimit(), getCount(player));
        Bukkit.getPluginManager().callEvent(event);

        return event;
    }

    private void addPlacedBlock(Player player, Block block) {

        if (isNotSameType(block))
            return;

        placedBlocks.add(new PlacedBlock(player, block));
    }

    private boolean hasReachedLimit(Player player) {
        return getCount(player) >= getLimit();
    }

    private void removePlacedBlock(Block block) {

        if (isNotSameType(block))
            return;

        placedBlocks.remove(new PlacedBlock(block));
    }

    private boolean isNotSameType(Block block) {

        return block.getType() != getType();
    }
}
