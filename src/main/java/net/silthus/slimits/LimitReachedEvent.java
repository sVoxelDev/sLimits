package net.silthus.slimits;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@Setter
public class LimitReachedEvent extends Event {

    private final Player player;
    private final int limit;
    private final int count;
    private boolean cancelBlockPlacement = true;

    public LimitReachedEvent(Player player, int limit, int count) {
        this.player = player;
        this.limit = limit;
        this.count = count;
    }

    public LimitType getType() {
        return LimitType.BLOCK_PLACEMENT;
    }

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
