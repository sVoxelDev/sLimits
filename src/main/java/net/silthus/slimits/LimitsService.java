package net.silthus.slimits;

import lombok.Getter;
import org.bukkit.Bukkit;

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
                .forEach(this::registerLimit);
        plugin.getLogger().info("loaded " + limits.size() + " limits!");
    }

    public List<BlockPlacementLimit> getBlockPlacementCounter() {
        return limits;
    }

    private void registerLimit(BlockPlacementLimit blockPlacementLimit) {
        Bukkit.getPluginManager().registerEvents(blockPlacementLimit, plugin);
        limits.add(blockPlacementLimit);
    }
}
