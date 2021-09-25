package net.silthus.slimits;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.bukkit.Material;

import java.util.Map;

@Value
@Builder
@AllArgsConstructor
public class BlockPlacementLimitConfig {

    Material type;
    int limit;

    public BlockPlacementLimitConfig(Map<?, ?> map) {

        this.type = Material.matchMaterial((String) map.get("type"));
        this.limit = (int) map.get("limit");
    }
}
