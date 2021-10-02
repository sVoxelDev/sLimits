package net.silthus.slimits.limits;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.silthus.slimits.SLimitsPlugin;
import net.silthus.slimits.config.BlockPlacementLimitConfig;
import net.silthus.slimits.events.LimitReachedEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
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
    private final boolean denyBreakingByOthers;
    @Getter
    private final List<PlacedBlock> placedBlocks = new ArrayList<>();

    public BlockPlacementLimit(Material type, int limit) {
        this(BlockPlacementLimitConfig.getKey(type, limit), type, limit, null);
    }

    public BlockPlacementLimit(Material type, int limit, String permission) {
        this(BlockPlacementLimitConfig.getKey(type, limit), type, limit, permission);
    }

    public BlockPlacementLimit(String key, Material type, int limit, String permission) {
        this(key, type, limit, permission, false);
    }

    public BlockPlacementLimit(BlockPlacementLimitConfig config) {
        this(config.getKey(), config.getType(), config.getLimit(), config.getPermission(), config.isDenyBreakingByOthers());
    }

    public PlayerLimit asPlayerLimit(OfflinePlayer player) {
        return new PlayerLimit(player, this);
    }

    public int getCount(OfflinePlayer player) {
        return getPlacedBlocks(player).size();
    }

    public List<PlacedBlock> getPlacedBlocks(OfflinePlayer player) {

        return placedBlocks.stream()
                .filter(placedBlock -> placedBlock.isOwner(player.getUniqueId()))
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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {

        removePlacedBlock(event);
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

    public boolean hasPermission(Player player) {

        if (player == null)
            return false;

        boolean isNotIgnored = !player.hasPermission(SLimitsPlugin.PERMISSION_IGNORE_LIMITS);

        return isNotIgnored && player.hasPermission(getPermission());
    }

    private boolean isNotApplicable(BlockPlaceEvent event) {

        return isNotSameType(event.getBlock()) || !hasPermission(event.getPlayer());
    }

    private void fireAndHandleLimitReachedEvent(BlockPlaceEvent event) {

        LimitReachedEvent limitReachedEvent = fireLimitReachedEvent(event);
        if (limitReachedEvent.isCancelBlockPlacement())
            cancelBlockPlacement(event);
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
        player.sendMessage(ChatColor.GRAY + "Your limit for placing "
                + getType().getKey().getKey() + " " + ChatColor.RED + "increased"
                + ChatColor.GRAY + ": " + getCount(player) + "/" + getLimit() + "."
        );
    }

    private boolean hasReachedLimit(Player player) {
        return getCount(player) >= getLimit();
    }

    private void removePlacedBlock(BlockBreakEvent event) {

        if (isNotSameType(event.getBlock()))
            return;

        if (checkAndDenyBlockBreaking(event))
            return;

        if (placedBlocks.remove(new PlacedBlock(event.getBlock()))) {
            event.getPlayer().sendMessage(ChatColor.GRAY + "Your limit for placing "
                    + getType().getKey().getKey() + " " + ChatColor.GREEN + "decreased"
                    + ChatColor.GRAY + ": " + getCount(event.getPlayer()) + "/" + getLimit() + "."
            );
        }
    }

    private boolean checkAndDenyBlockBreaking(BlockBreakEvent event) {

        if (!isDenyBreakingByOthers()) return false;

        Optional<PlacedBlock> optionalExistingBlock = getPlacedBlock(event.getBlock());
        if (optionalExistingBlock.isEmpty()) return false;

        PlacedBlock existingBlock = optionalExistingBlock.get();
        if (existingBlock.isOwner(event.getPlayer().getUniqueId())) return false;

        cancelBlockBreaking(event, existingBlock);
        return true;
    }

    private Optional<PlacedBlock> getPlacedBlock(Block block) {
        return getPlacedBlocks().stream()
                .filter(placedBlock -> placedBlock.equals(new PlacedBlock(block)))
                .findFirst();
    }

    private boolean isNotSameType(Block block) {

        return block.getType() != getType();
    }

    private void cancelBlockBreaking(BlockBreakEvent event, PlacedBlock existingBlock) {
        event.setCancelled(true);
        event.getPlayer().sendMessage(ChatColor.RED + "This is a limited block placed by "
                + ChatColor.AQUA + existingBlock.getOwnerName()
                + ChatColor.RED + ". You cannot break it.");
    }

    private void cancelBlockPlacement(BlockPlaceEvent event) {
        event.setCancelled(true);
        event.getPlayer().sendMessage(ChatColor.RED + "You reached your limit for placing "
                + getType().getKey().getKey() + ": " + getCount(event.getPlayer()) + "/" + getLimit() + ".");
    }
}
