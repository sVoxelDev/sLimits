package net.silthus.slimits;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LimitsService {

    private final SLimitsPlugin plugin;
    @Getter
    private final List<BlockPlacementLimit> limits = new ArrayList<>();

    public LimitsService(SLimitsPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadLimits(LimitsConfig config) {

        limits.clear();
        config.getLimits().stream()
                .map(BlockPlacementLimit::new)
                .forEach(this::registerAndLoadLimit);
        plugin.getLogger().info("loaded " + limits.size() + " limits!");
    }

    public List<BlockPlacementLimit> getBlockPlacementLimits() {
        return limits;
    }

    public void saveLimits() {

        File storagePath = prepareAndGetStoragePath();

        for (BlockPlacementLimit limit : getBlockPlacementLimits()) {
            try {
                limit.save(storagePath);
            } catch (IOException e) {
                plugin.getLogger().severe("Unable to save limit store of \"" + limit.getKey() + "\": " + e.getMessage());
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File prepareAndGetStoragePath() {
        File storage = new File(plugin.getLimitsConfig().getStorage().getBlockPlacement());
        storage.mkdirs();
        return storage;
    }

    private void registerAndLoadLimit(BlockPlacementLimit limit) {

        try {
            limit.load(getLimitStore(limit));

            Bukkit.getPluginManager().registerEvents(limit, plugin);
            limits.add(limit);
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().severe("Unable to load limit storage for block_placement limit \"" + limit.getKey() + "\": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private YamlConfiguration getLimitStore(BlockPlacementLimit limit) throws IOException, InvalidConfigurationException {
        YamlConfiguration cfg = new YamlConfiguration();
        File file = new File(prepareAndGetStoragePath(), limit.getKey() + ".yml");
        if (!file.createNewFile())
            cfg.load(file);
        return cfg;
    }
}
