package net.silthus.slimits;

import de.exlll.configlib.Configuration;
import lombok.Data;
import net.silthus.slib.config.ConfigUtil;
import net.silthus.slimits.limits.BlockPlacementLimit;
import net.silthus.slimits.limits.BlockPlacementLimitConfig;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Data
public class LimitsManager {

    private final LimitsPlugin plugin;
    private final Map<String, BlockPlacementLimit> loadedLimits = new HashMap<>();

    private LimitsConfig config;

    public LimitsManager(LimitsPlugin plugin) {
        this.plugin = plugin;
        this.config = new LimitsConfig(Path.of(plugin.getDataFolder().getAbsolutePath(), "config.yaml"));
    }

    public void load() {

        this.config.loadAndSave();

        ConfigUtil.loadRecursiveConfigs(
                plugin, "limits", BlockPlacementLimitConfig.class, this::loadLimit);
        getPlugin().getLogger().info("Loaded " + loadedLimits.size() + " limit configs.");
    }

    public void unload() {

        getLoadedLimits().values().forEach(blockPlacementLimit -> blockPlacementLimit.getPlayerLimits().values().forEach(Configuration::save));
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
