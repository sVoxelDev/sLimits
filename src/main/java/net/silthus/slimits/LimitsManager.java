package net.silthus.slimits;

import lombok.Data;
import net.silthus.slib.config.ConfigUtil;
import net.silthus.slimits.limits.BlockPlacementLimit;
import net.silthus.slimits.limits.BlockPlacementLimitConfig;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Data
public class LimitsManager {

    private final LimitsPlugin plugin;
    private final Map<String, BlockPlacementLimit> loadedLimits = new HashMap<>();

    public void load() {

        ConfigUtil.loadRecursiveConfigs(
                plugin, "limits", BlockPlacementLimitConfig.class, this::loadLimit);
        getPlugin().getLogger().info("Loaded " + loadedLimits.size() + " limit configs.");
    }

    public void loadLimit(String id, File file, BlockPlacementLimitConfig config) {
        if (loadedLimits.containsKey(id)) {
            getPlugin().getLogger().warning("Duplicate config detected: " + id);
            return;
        }

        BlockPlacementLimit limit = new BlockPlacementLimit(id);
        loadedLimits.put(id, limit);

        getPlugin().registerEvents(limit);
        limit.load(config);

        plugin.getLogger().info("Loaded limit config: " + id + " (" + file.getAbsolutePath() + ")");
    }
}
