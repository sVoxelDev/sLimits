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

        if (!hasPermission(event.getPlayer())) return;

        getConfig()
                .ifPresent(
                        config -> {
                            Material blockType = event.getBlock().getType();
                            if (hasReachedLimit(event.getPlayer(), blockType)) {
                                event
                                        .getPlayer()
                                        .sendMessage(
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

        if (!hasPermission(event.getPlayer())) return;

        getConfig()
                .flatMap(config -> config.getLimit(event.getBlock().getType()))
                .ifPresent(
                        limit -> {
                            Block block = event.getBlock();

                            Player player = event.getPlayer();
                            Material blockType = block.getType();

                            int placedBlockAmount = addPlacedBlock(player, event.getBlock());

                            player.sendMessage(
                                    ChatColor.AQUA
                                            + "You placed "
                                            + placedBlockAmount
                                            + "/"
                                            + limit
                                            + " of "
                                            + blockType.name());
                        });
    }

    public int addPlacedBlock(Player player, Block block) {

        if (!playerLimits.containsKey(player.getUniqueId())) {
            playerLimits.put(player.getUniqueId(), new PlayerBlockPlacementLimit(player));
        }

        PlayerBlockPlacementLimit limit = playerLimits.get(player.getUniqueId());
        return limit.addBlock(block);
    }

    public int getPlacedBlockAmount(Player player, Material blockType) {

        return playerLimits.getOrDefault(player.getUniqueId(), createLimit(player)).getCount(blockType);
    }

    public boolean hasReachedLimit(Player player, Material blockType) {

        Optional<Integer> limit = getConfig().flatMap(config -> config.getLimit(blockType));
        return limit.isPresent()
                && playerLimits.getOrDefault(player.getUniqueId(), createLimit(player)).getCount(blockType)
                >= limit.get();
    }

    private PlayerBlockPlacementLimit createLimit(Player player) {
        return new PlayerBlockPlacementLimit(player);
    }

    public boolean hasPermission(Player player) {
        return player.hasPermission(getPermission())
                && !player.hasPermission(Constants.PERMISSION_EXCLUDE_FROM_LIMITS);
    }

    private String getPermission() {
        return Constants.PERMISSION_PREFIX + getIdentifier();
    }
}
