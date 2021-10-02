package net.silthus.slimits;

import lombok.Getter;
import net.silthus.slimits.config.LimitsConfig;
import net.silthus.slimits.events.LimitReachedEvent;
import net.silthus.slimits.limits.BlockPlacementLimit;
import net.silthus.slimits.limits.PlayerLimit;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LimitsService implements Listener {

    private final SLimitsPlugin plugin;

    @Getter
    private final List<BlockPlacementLimit> limits = new ArrayList<>();
    private BukkitTask saveTask;

    public LimitsService(SLimitsPlugin plugin) {
        this.plugin = plugin;
    }

    public void schedulePeriodicSaveTask() {
        if (saveTask != null)
            saveTask.cancel();

        long ticks = plugin.getLimitsConfig().getSaveIntervalTicks();
        if (ticks > 0)
            saveTask = plugin.getServer().getScheduler()
                .runTaskTimer(plugin, this::saveLimits, ticks, ticks);
    }

    public void reload() {

        loadLimits(plugin.getLimitsConfig());
        schedulePeriodicSaveTask();
    }

    public void loadLimits(LimitsConfig config) {

        unregisterAllLimits();

        config.getLimits().stream()
                .map(BlockPlacementLimit::new)
                .forEach(this::registerAndLoadLimit);
        plugin.getLogger().info("loaded " + limits.size() + " limits!");
    }

    public void saveLimits() {

        File storagePath = prepareAndGetStoragePath();

        for (BlockPlacementLimit limit : getLimits()) {
            try {
                limit.save(storagePath);
            } catch (IOException e) {
                plugin.getLogger().severe("Unable to save limit store of \"" + limit.getKey() + "\": " + e.getMessage());
            }
        }
    }

    public List<BlockPlacementLimit> getBlockPlacementLimits(Material type) {
        return getLimits().stream()
                .filter(limit -> limit.getType() == type)
                .collect(Collectors.toList());
    }

    public List<PlayerLimit> getPlayerLimits(OfflinePlayer player) {

        Player onlinePlayer = Bukkit.getPlayer(player.getUniqueId());
        return getLimits().stream()
                .filter(limit -> limit.hasPermission(onlinePlayer))
                .map(limit -> limit.asPlayerLimit(player))
                .collect(Collectors.toList());
    }

    public void registerAndLoadLimit(BlockPlacementLimit limit) {

        try {
            limit.load(getLimitStore(limit));

            Bukkit.getPluginManager().registerEvents(limit, plugin);
            limits.add(limit);
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().severe("Unable to load limit storage for block_placement limit \"" + limit.getKey() + "\": " + e.getMessage());
            e.printStackTrace();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onLimitReached(LimitReachedEvent event) {

        Optional<BlockPlacementLimit> highestLimit = getHighestBlockPlacementLimit(event.getType(), event.getPlayer());
        if (highestLimit.map(limit -> !limit.getPermission().equals(event.getPermission())).orElse(false)) {
            event.setCancelled(true);
        }
    }

    private Optional<BlockPlacementLimit> getHighestBlockPlacementLimit(Material type, Player player) {
        return getBlockPlacementLimits(type)
                .stream().filter(limit -> limit.hasPermission(player))
                .max(Comparator.comparingInt(BlockPlacementLimit::getLimit));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File prepareAndGetStoragePath() {
        File storage = plugin.getLimitsConfig().getStorage().getStoragePath(plugin);
        storage.mkdirs();
        return storage;
    }

    private YamlConfiguration getLimitStore(BlockPlacementLimit limit) throws IOException, InvalidConfigurationException {
        YamlConfiguration cfg = new YamlConfiguration();
        File file = new File(prepareAndGetStoragePath(), limit.getKey() + ".yml");
        if (file.exists())
            cfg.load(file);
        return cfg;
    }

    private void unregisterAllLimits() {

        saveLimits();

        limits.forEach(HandlerList::unregisterAll);
        limits.clear();
    }
}
