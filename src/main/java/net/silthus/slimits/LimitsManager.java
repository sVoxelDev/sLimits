package net.silthus.slimits;

import lombok.Getter;
import net.silthus.slib.config.ConfigUtil;
import net.silthus.slimits.api.LimitsStorage;
import net.silthus.slimits.limits.BlockPlacementLimit;
import net.silthus.slimits.limits.BlockPlacementLimitConfig;
import net.silthus.slimits.limits.PlayerBlockPlacementLimit;
import net.silthus.slimits.storage.FlatFileLimitsStorage;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.graalvm.compiler.lir.alloc.lsra.LinearScan;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class LimitsManager implements Listener {

    private final LimitsPlugin plugin;
    private final Map<String, BlockPlacementLimit> loadedLimitListeners = new HashMap<>();
    private final Map<UUID, PlayerBlockPlacementLimit> playerLimits = new HashMap<>();
    private final Map<String, BlockPlacementLimitConfig> limitConfigs = new HashMap<>();

    private LimitsConfig pluginConfig;
    private LimitsStorage storage;

    public LimitsManager(LimitsPlugin plugin) {
        this.plugin = plugin;
        this.pluginConfig = new LimitsConfig(new File(plugin.getDataFolder(), "config.yaml").toPath());
    }

    public void load() {

        this.pluginConfig.loadAndSave();

        initializeStorage();

        ConfigUtil.loadRecursiveConfigs(
                plugin, "limits", BlockPlacementLimitConfig.class, this::loadLimit);
        getPlugin().getLogger().info("Loaded " + loadedLimitListeners.size() + " limit configs.");

        getPlugin().registerEvents(this);
    }

    private void initializeStorage() {

        switch (getPluginConfig().getStorage()) {
            case FLATFILES:
            default:
                storage = new FlatFileLimitsStorage(this);
                getPlugin().getLogger().info("[Storage]: FLATFILES saved to " + getPluginConfig().getStoragePath());
                break;
        }
    }

    public void unload() {

        getPlugin().unregisterEvents(this);

        getStorage().store(getPlayerLimits().values().toArray(new PlayerBlockPlacementLimit[0]));
        getPlayerLimits().clear();

        getLoadedLimitListeners().values().forEach(limit -> getPlugin().unregisterEvents(limit));
        getLoadedLimitListeners().clear();

        getLimitConfigs().clear();
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        savePlayerLimits(player);
        playerLimits.remove(player.getUniqueId());
    }

    public void loadLimit(String id, File file, BlockPlacementLimitConfig config) {
        if (loadedLimitListeners.containsKey(id)) {
            getPlugin().getLogger().warning("Duplicate config detected: " + id);
            return;
        }
        limitConfigs.put(id, config);

        BlockPlacementLimit limit = new BlockPlacementLimit(id, this);
        loadedLimitListeners.put(id, limit);

        getPlugin().registerEvents(limit);
        limit.load(config);

        plugin.getLogger().info("Loaded limit config: " + id + " (" + file.getAbsolutePath() + ")");
    }

    public PlayerBlockPlacementLimit getPlayerLimit(OfflinePlayer player) {

        if (!playerLimits.containsKey(player.getUniqueId())) {
            playerLimits.put(player.getUniqueId(), getStorage().load(player));
        }

        return playerLimits.get(player.getUniqueId());
    }

    public File getStoragePath() {

        return new File(getPlugin().getDataFolder(), getPluginConfig().getStoragePath());
    }

    public void savePlayerLimits(Player player) {
        getStorage().store(getPlayerLimit(player));
    }
}
