package net.silthus.slimits.limits;

import lombok.Getter;
import lombok.NonNull;
import net.silthus.slimits.config.BlockPlacementLimitConfig;
import net.silthus.slimits.events.LimitReachedEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class BlockPlacementLimit implements Listener {

    @Getter
    private final String key;
    @Getter
    private final Material type;
    @Getter
    private final int limit;
    @Getter
    private final String permission;
    @Getter
    private final List<PlacedBlock> placedBlocks = new ArrayList<>();

    public BlockPlacementLimit(String key, Material type, int limit, String permission) {
        this.key = key;
        this.type = type;
        this.limit = limit;
        this.permission = permission;
    }

    public BlockPlacementLimit(Material type, int limit) {
        this(BlockPlacementLimitConfig.getKey(type, limit), type, limit, null);
    }

    public BlockPlacementLimit(Material type, int limit, String permission) {
        this(BlockPlacementLimitConfig.getKey(type, limit), type, limit, permission);
    }

    public BlockPlacementLimit(BlockPlacementLimitConfig config) {
        this(config.getKey(), config.getType(), config.getLimit(), config.getPermission());
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

        if (isNotApplicable(event))
            return;

        if (hasReachedLimit(event.getPlayer()))
            fireAndHandleLimitReachedEvent(event);
        else
            addPlacedBlock(event.getPlayer(), event.getBlockPlaced());
    }

    private boolean isNotApplicable(BlockPlaceEvent event) {

        boolean playerHasNoPermission = !event.getPlayer().hasPermission(getPermission());

        return isNotSameType(event.getBlock()) || playerHasNoPermission;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {

        removePlacedBlock(event.getBlock());
    }

    @SuppressWarnings("unchecked")
    public void load(@NonNull ConfigurationSection store) {

        placedBlocks.clear();
        this.placedBlocks.addAll(((Collection<PlacedBlock>) store.getList("placed_blocks", new ArrayList<>())).stream()
                .filter(placedBlock -> placedBlock.getType() == getType())
                .collect(Collectors.toList()));
    }

    public void save(@NonNull File storagePath) throws IOException {

        File file = new File(storagePath, getKey() + ".yml");
        YamlConfiguration config = new YamlConfiguration();

        config.set("placed_blocks", getPlacedBlocks());

        config.save(file);
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

        placedBlocks.add(new PlacedBlock(block, player));
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
