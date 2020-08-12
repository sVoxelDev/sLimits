package net.silthus.slimits;

import lombok.Getter;
import net.silthus.slib.config.ConfigUtil;
import net.silthus.slimits.api.LimitsStorage;
import net.silthus.slimits.limits.BlockPlacementLimit;
import net.silthus.slimits.limits.BlockPlacementLimitConfig;
import net.silthus.slimits.limits.PlayerBlockPlacementLimit;
import net.silthus.slimits.storage.FlatFileLimitsStorage;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.util.*;

@Getter
public class LimitsManager implements Listener {

    private final LimitsPlugin plugin;
    private final Map<UUID, PlayerBlockPlacementLimit> playerLimits = new HashMap<>();
    private final Map<String, BlockPlacementLimitConfig> limitConfigs = new HashMap<>();

    private final BlockPlacementLimit blockPlacementLimit;
    private final LimitsConfig pluginConfig;

    private LimitsStorage storage;

    public LimitsManager(LimitsPlugin plugin, LimitsConfig config) {
        this.plugin = plugin;
        this.pluginConfig = config;
        this.blockPlacementLimit = new BlockPlacementLimit(this);

        plugin.getMetrics().ifPresent(metrics -> {
            metrics.addCustomChart(new Metrics.SingleLineChart("loaded_limits", limitConfigs::size));
        });
    }

    public void reload() {
        unload();
        load();
    }

    public void load() {

        this.pluginConfig.loadAndSave();

        initializeStorage();

        ConfigUtil.loadRecursiveConfigs(
                plugin, "limits", BlockPlacementLimitConfig.class, this::loadLimit);
        getPlugin().getLogger().info("Loaded " + limitConfigs.size() + " limit configs.");

        loadAllPlayerLimits();

        getPlugin().registerEvents(this);
        getPlugin().registerEvents(getBlockPlacementLimit());
    }

    public void initializeStorage() {

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
        getPlugin().unregisterEvents(getBlockPlacementLimit());

        if (getStorage() != null) {
            getStorage().store(getPlayerLimits().values().toArray(new PlayerBlockPlacementLimit[0]));
        }

        getPlayerLimits().clear();
        getLimitConfigs().clear();
    }

    public void loadLimit(String id, File file, BlockPlacementLimitConfig config) {
        if (limitConfigs.containsKey(id)) {
            getPlugin().getLogger().warning("Duplicate config detected: " + id);
            return;
        }

        config.setIdentifier(id);
        limitConfigs.put(id, config);

        plugin.getLogger().info("Loaded limit config: " + id + " (" + file.getAbsolutePath() + ")");
    }

    public PlayerBlockPlacementLimit getPlayerLimit(OfflinePlayer player) {

        if (!playerLimits.containsKey(player.getUniqueId())) {
            playerLimits.put(player.getUniqueId(), loadPlayerLimit(player));
        }

        return playerLimits.get(player.getUniqueId());
    }

    public File getStoragePath() {

        return new File(getPlugin().getDataFolder(), getPluginConfig().getStoragePath());
    }

    public void savePlayerLimits(OfflinePlayer player) {
        getStorage().store(getPlayerLimit(player));
    }

    /**
     * Gets the uuid of the player who placed/owns the block.
     *
     * @param block to get owner for
     * @return UUID of the one who placed the block. Empty optional if no owner was found.
     */
    public Optional<UUID> getBlockOwner(Block block) {
        return getPlayerLimits().values().stream()
                .filter(limit -> limit.hasPlacedBlock(block))
                .findFirst()
                .map(PlayerBlockPlacementLimit::getPlayerUUID);
    }

    private void loadAllPlayerLimits() {

        if (getStorage() == null) return;

        for (PlayerBlockPlacementLimit playerBlockPlacementLimit : getStorage().load()) {
            // load only unloaded saves to avoid overwriting stuff
            if (!playerLimits.containsKey(playerBlockPlacementLimit.getPlayerUUID())) {
                playerLimits.put(playerBlockPlacementLimit.getPlayerUUID(), playerBlockPlacementLimit);
            }
        }
    }

    private PlayerBlockPlacementLimit loadPlayerLimit(OfflinePlayer player) {

        PlayerBlockPlacementLimit playerLimit = getStorage().load(player);
        Player onlinePlayer = Bukkit.getPlayer(player.getUniqueId());

        if (onlinePlayer != null && onlinePlayer.isOnline()) {
            getLimitConfigs().values().stream()
                    .filter(config -> onlinePlayer.hasPermission(config.getPermission()))
                    .forEach(playerLimit::registerLimitConfig);
        }

        return playerLimit;
    }

    ///
    /// Events
    ///

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        savePlayerLimits(player);
        playerLimits.remove(player.getUniqueId());
    }
}
