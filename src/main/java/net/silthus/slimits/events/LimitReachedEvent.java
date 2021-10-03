package net.silthus.slimits.events;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.silthus.slimits.limits.BlockPlacementLimit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

@Getter
@Setter
public class LimitReachedEvent extends SLimitEvent {

    private boolean cancelBlockPlacement = true;

    public LimitReachedEvent(BlockPlacementLimit limit, Player player, int count) {
        super(limit, player, count);
    }

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    @Override
    @NonNull
    public HandlerList getHandlers() {
        return handlerList;
    }
}
