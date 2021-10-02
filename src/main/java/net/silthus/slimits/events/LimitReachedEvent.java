package net.silthus.slimits.events;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.silthus.slimits.limits.LimitType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@Setter
public class LimitReachedEvent extends Event implements Cancellable {

    private final Player player;
    private final Material type;
    private final int limit;
    private final int count;
    private final String permission;
    private boolean cancelBlockPlacement = true;
    private boolean cancelled = false;

    public LimitReachedEvent(Player player, Material type, int limit, int count, String permission) {
        this.player = player;
        this.type = type;
        this.limit = limit;
        this.count = count;
        this.permission = permission;
    }

    public LimitType getLimitType() {
        return LimitType.BLOCK_PLACEMENT;
    }

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    @Override
    @NonNull
    public HandlerList getHandlers() {
        return handlerList;
    }
}
