package net.silthus.slimits;

import de.exlll.configlib.Configuration;
import lombok.Getter;
import net.silthus.slib.config.ConfigUtil;
import net.silthus.slimits.api.LimitsStorage;
import net.silthus.slimits.limits.BlockPlacementLimit;
import net.silthus.slimits.limits.BlockPlacementLimitConfig;
import net.silthus.slimits.limits.PlayerBlockPlacementLimit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LimitsManager {

    @Getter
    private final LimitsPlugin plugin;
    @Getter
    private final Map<String, BlockPlacementLimit> loadedLimits = new HashMap<>();
    @Getter
    private final Map<UUID, PlayerBlockPlacementLimit> playerLimits = new HashMap<>();
    @Getter
    private final Map<String, BlockPlacementLimitConfig> loadedConfigs = new HashMap<>();

    @Getter
    private LimitsConfig limitsConfig;
    private LimitsStorage storage;

    public LimitsManager(LimitsPlugin plugin) {
        this.plugin = plugin;
        this.limitsConfig = new LimitsConfig(new File(plugin.getDataFolder(), "config.yaml").toPath());
    }

    public void load() {

        this.limitsConfig.loadAndSave();

        ConfigUtil.loadRecursiveConfigs(
                plugin, "limits", BlockPlacementLimitConfig.class, this::loadLimit);
        getPlugin().getLogger().info("Loaded " + loadedLimits.size() + " limit configs.");
    }

    public void unload() {

        getPlayerLimits().values().forEach(Configuration::save);
    }

    public void loadLimit(String id, File file, BlockPlacementLimitConfig config) {
        if (loadedLimits.containsKey(id)) {
            getPlugin().getLogger().warning("Duplicate config detected: " + id);
            return;
        }
        loadedConfigs.put(id, config);

        BlockPlacementLimit limit = new BlockPlacementLimit(id, this);
        loadedLimits.put(id, limit);

        getPlugin().registerEvents(limit);
        limit.load(config);

        plugin.getLogger().info("Loaded limit config: " + id + " (" + file.getAbsolutePath() + ")");
    }

    public PlayerBlockPlacementLimit getPlayerLimit(Player player) {

        if (!playerLimits.containsKey(player.getUniqueId())) {
            playerLimits.put(player.getUniqueId(), PlayerBlockPlacementLimit.create(player));
        }

        return playerLimits.get(player.getUniqueId());
    }
}
