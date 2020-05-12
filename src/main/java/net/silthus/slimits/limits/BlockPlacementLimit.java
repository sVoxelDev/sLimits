package net.silthus.slimits.limits;

import lombok.Getter;
import net.silthus.slimits.Constants;
import net.silthus.slimits.LimitsManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Optional;

public class BlockPlacementLimit implements Listener {

    @Getter
    private final LimitsManager limitsManager;

    public BlockPlacementLimit(LimitsManager limitsManager) {
        this.limitsManager = limitsManager;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerPlaceBlock(BlockPlaceEvent event) {

        PlayerBlockPlacementLimit playerLimit = getLimitsManager().getPlayerLimit(event.getPlayer());

        if (!playerLimit.isApplicable(event.getPlayer(), event.getBlock())) return;

        Material blockType = event.getBlock().getType();
        if (playerLimit.hasReachedLimit(blockType)) {
            event.getPlayer().sendMessage(
                    ChatColor.DARK_RED
                            + "You reached your limit of "
                            + playerLimit.getLimit(blockType).orElse(0)
                            + " for placing "
                            + blockType.name());
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void afterBlockPlaceEvent(BlockPlaceEvent event) {

        PlayerBlockPlacementLimit playerLimit = getLimitsManager().getPlayerLimit(event.getPlayer());

        if (!playerLimit.isApplicable(event.getPlayer(), event.getBlock())) return;

        playerLimit.getLimit(event.getBlock().getType()).ifPresent(limit -> {
            Block block = event.getBlock();

            Player player = event.getPlayer();
            Material blockType = block.getType();

            int placedBlockAmount = playerLimit.addBlock(block);

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

        PlayerBlockPlacementLimit playerLimit = getLimitsManager().getPlayerLimit(event.getPlayer());

        if (!playerLimit.isApplicable(event.getPlayer(), event.getBlock())) return;

        if (!playerLimit.hasPlacedBlock(event.getBlock())) return;

        playerLimit.getLimit(event.getBlock().getType()).ifPresent(limit -> {
            int newCount = playerLimit.removeBlock(event.getBlock());

            event.getPlayer().sendMessage(ChatColor.AQUA
                    + "You removed a placed block. You placed "
                    + newCount
                    + "/"
                    + limit
                    + " of "
                    + event.getBlock().getType().name());
        });
    }
}
