package net.silthus.slimits.events;

import net.silthus.slimits.limits.BlockPlacementLimit;
import net.silthus.slimits.limits.LimitType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public abstract class SLimitEvent extends Event {

    protected final BlockPlacementLimit limit;
    protected final Player player;
    protected final int count;

    public SLimitEvent(BlockPlacementLimit limit, Player player, int count) {
        this.limit = limit;
        this.player = player;
        this.count = count;
    }

    public String getKey() {
        return limit.getKey();
    }

    public Material getType() {
        return limit.getType();
    }

    public String getPermission() {
        return limit.getPermission();
    }

    public int getLimit() {
        return limit.getLimit();
    }

    public LimitType getLimitType() {
        return LimitType.BLOCK_PLACEMENT;
    }

    public org.bukkit.entity.Player getPlayer() {
        return this.player;
    }

    public int getCount() {
        return this.count;
    }
}
