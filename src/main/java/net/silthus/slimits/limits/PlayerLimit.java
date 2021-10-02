package net.silthus.slimits.limits;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

@Value
@AllArgsConstructor
public class PlayerLimit {

    OfflinePlayer player;
    LimitType limitType;
    Material type;
    int limit;
    int count;

    public PlayerLimit(OfflinePlayer player, BlockPlacementLimit limit) {
        this.player = player;
        this.limitType = LimitType.BLOCK_PLACEMENT;
        this.type = limit.getType();
        this.limit = limit.getLimit();
        this.count = limit.getCount(player);
    }
}
