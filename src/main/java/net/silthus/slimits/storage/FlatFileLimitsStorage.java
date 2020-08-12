package net.silthus.slimits.storage;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.silthus.slib.configlib.configs.yaml.BukkitYamlConfiguration;
import net.silthus.slimits.LimitsManager;
import net.silthus.slimits.api.LimitsStorage;
import net.silthus.slimits.limits.PlayerBlockPlacementLimit;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.nio.file.Path;
import java.util.UUID;
import java.util.logging.Logger;

@Data
public class FlatFileLimitsStorage implements LimitsStorage {

    private final LimitsManager limitsManager;

    @Override
    public void store(PlayerBlockPlacementLimit... limits) {

        for (PlayerBlockPlacementLimit limit : limits) {
            StorageConfig config = new StorageConfig(getStorageFile(limit.getPlayerUUID()).toPath(), limit);
            config.setBlockPlacementLimit(limit);
            config.save();
        }
    }

    @Override
    public PlayerBlockPlacementLimit[] load() {

        File storagePath = getLimitsManager().getStoragePath();

        if (storagePath.mkdirs()) return new PlayerBlockPlacementLimit[0];

        File[] files = storagePath.listFiles();
        PlayerBlockPlacementLimit[] result = new PlayerBlockPlacementLimit[files.length];

        for (int i = 0; i < files.length; i++) {
            PlayerBlockPlacementLimit playerLimit = loadPlayerConfig(files[0]);
            result[i] = playerLimit;
        }

        return result;
    }

    @Override
    public PlayerBlockPlacementLimit load(OfflinePlayer player) {

        File file = getStorageFile(player.getUniqueId());
        if (!file.exists()) {
            return createPlayerConfig(player);
        } else {
            return loadPlayerConfig(file);
        }
    }

    private File getStorageFile(UUID player) {
        return new File(getLimitsManager().getStoragePath(), player.toString() + ".yaml");
    }

    private PlayerBlockPlacementLimit createPlayerConfig(OfflinePlayer player) {
        StorageConfig config = new StorageConfig(getStorageFile(player.getUniqueId()).toPath());
        config.setBlockPlacementLimit(new PlayerBlockPlacementLimit(player));
        config.loadAndSave();
        return config.getBlockPlacementLimit();
    }

    private PlayerBlockPlacementLimit loadPlayerConfig(File file) {
        StorageConfig storageConfig = new StorageConfig(file.toPath());
        try {
            storageConfig.load();
        } catch (Exception e) {
            Logger logger = getLimitsManager().getPlugin().getLogger();
            logger.severe("Failed to load player data from " + file.getAbsolutePath() + ": " + e.getMessage());
            e.printStackTrace();
            File invalidConfig = new File(file, "_INVALID");
            if (file.renameTo(invalidConfig)) {
                storageConfig.loadAndSave();
                logger.info("Renamed invalid config to " + invalidConfig.getName() + " and created a new blank save state.");
            } else {
                logger.warning("Failed to rename invalid config! Please check your file permissions and manually remove or update the config.");
            }
        }
        return storageConfig.getBlockPlacementLimit();
    }

    @Getter
    @Setter
    public static class StorageConfig extends BukkitYamlConfiguration {

        private PlayerBlockPlacementLimit blockPlacementLimit = new PlayerBlockPlacementLimit();

        protected StorageConfig(Path path, PlayerBlockPlacementLimit limit) {
            super(path);
            this.blockPlacementLimit = limit;
        }

        protected StorageConfig(Path path) {
            super(path);
        }
    }
}
