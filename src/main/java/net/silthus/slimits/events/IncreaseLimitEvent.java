package net.silthus.slimits.events;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.silthus.slimits.limits.BlockPlacementLimit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

@Getter
@Setter
public class IncreaseLimitEvent extends SLimitEvent implements Cancellable {

    private int newCount;
    private boolean cancelled = false;
    private boolean silent = false;

    public IncreaseLimitEvent(BlockPlacementLimit limit, Player player, int count, int newCount) {
        super(limit, player, count);
        this.newCount = newCount;
    }

    public boolean isNotSilent() {
        return !isSilent();
    }

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    @Override
    @NonNull
    public HandlerList getHandlers() {
        return handlerList;
    }
}
