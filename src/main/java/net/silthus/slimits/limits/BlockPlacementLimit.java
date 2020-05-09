package net.silthus.slimits.limits;

import lombok.Data;
import net.silthus.slimits.Constants;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Data
public class BlockPlacementLimit implements Listener {

    static final String TYPE_ALIAS = "block-placement";

    private final String identifier;
    private Map<UUID, PlayerBlockPlacementLimit> playerLimits = new HashMap<>();
    private BlockPlacementLimitConfig config;

    public Optional<BlockPlacementLimitConfig> getConfig() {
        return Optional.ofNullable(this.config);
    }

    public void load(BlockPlacementLimitConfig config) {

        this.config = config;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerPlaceBlock(BlockPlaceEvent event) {

        if (!isApplicable(event.getPlayer(), event.getBlock())) return;

        getConfig().ifPresent(config -> {
            Material blockType = event.getBlock().getType();
            if (hasReachedLimit(event.getPlayer(), blockType)) {
                event.getPlayer().sendMessage(
                        ChatColor.DARK_RED
                                + "You reached your limit of "
                                + config.getLimit(blockType).orElse(0)
                                + " for placing "
                                + blockType.name());
                event.setCancelled(true);
                return;
            }
        });
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void afterBlockPlaceEvent(BlockPlaceEvent event) {

        if (!isApplicable(event.getPlayer(), event.getBlock())) return;

        getConfig().flatMap(config -> config.getLimit(event.getBlock().getType()))
                .ifPresent(limit -> {
                    Block block = event.getBlock();

                    Player player = event.getPlayer();
                    Material blockType = block.getType();

                    int placedBlockAmount = addPlacedBlock(player, event.getBlock());

                    player.sendMessage(ChatColor.AQUA
                            + "You placed "
                            + placedBlockAmount
                            + "/"
                            + limit
                            + " of "
                            + blockType.name());
                });
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {

        if (!isApplicable(event.getPlayer(), event.getBlock())) return;

        if (!hasPlacedBlock(event.getPlayer(), event.getBlock())) return;

        getConfig().flatMap(config -> config.getLimit(event.getBlock().getType()))
                .ifPresent(limit -> {
                    int newCount = removePlacedBlock(event.getPlayer(), event.getBlock());

                    event.getPlayer().sendMessage(ChatColor.AQUA
                            + "You removed a placed block. You placed "
                            + newCount
                            + "/"
                            + limit
                            + " of "
                            + event.getBlock().getType().name());
                });
    }

    public int addPlacedBlock(Player player, Block block) {

        return getPlayerLimit(player).addBlock(block);
    }

    public int removePlacedBlock(Player player, Block block) {

        return getPlayerLimit(player).removeBlock(block);
    }

    public int getPlacedBlockAmount(Player player, Material blockType) {

        return getPlayerLimit(player).getCount(blockType);
    }

    public boolean hasReachedLimit(Player player, Material blockType) {

        Optional<Integer> limit = getConfig().flatMap(config -> config.getLimit(blockType));
        return limit.isPresent()
                && playerLimits.getOrDefault(player.getUniqueId(), createLimit(player)).getCount(blockType)
                >= limit.get();
    }

    public boolean hasPlacedBlock(Player player, Block block) {
        return getPlayerLimit(player).hasPlacedBlock(block);
    }

    public PlayerBlockPlacementLimit getPlayerLimit(Player player) {
        if (!getPlayerLimits().containsKey(player.getUniqueId())) {
            playerLimits.put(player.getUniqueId(), createLimit(player));
        }
        return getPlayerLimits().get(player.getUniqueId());
    }

    private PlayerBlockPlacementLimit createLimit(Player player) {
        return new PlayerBlockPlacementLimit(player);
    }

    public boolean isApplicable(Player player, Block block) {
        boolean isLimitedBlock = getConfig().map(config -> config.hasLimit(block.getType())).orElse(false);
        boolean hasPermission = player.hasPermission(getPermission());
        boolean isExcluded = player.hasPermission(Constants.PERMISSION_EXCLUDE_FROM_LIMITS);

        return isLimitedBlock
                && hasPermission
                && !isExcluded;
    }

    private String getPermission() {
        return Constants.PERMISSION_PREFIX + getIdentifier();
    }
}
