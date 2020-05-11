package net.silthus.slimits.storage;

import de.exlll.configlib.configs.yaml.BukkitYamlConfiguration;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.silthus.slimits.LimitsConfig;
import net.silthus.slimits.api.LimitsStorage;
import net.silthus.slimits.limits.PlayerBlockPlacementLimit;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.file.Path;

@Data
public class FlatFileLimitsStorage implements LimitsStorage {

    private final LimitsConfig config;

    @Override
    public void store(PlayerBlockPlacementLimit... limits) {

        for (PlayerBlockPlacementLimit limit : limits) {
            StorageConfig config = new StorageConfig(new File(getConfig().getStoragePath(), limit.getPlayerUUID().toString() + ".yaml").toPath(), limit);
            config.save();
        }
    }

    @Override
    public PlayerBlockPlacementLimit[] load() {
        return new PlayerBlockPlacementLimit[0];
    }

    @Override
    public PlayerBlockPlacementLimit load(Player player) {
        return null;
    }

    @Getter
    @Setter
    public static class StorageConfig extends BukkitYamlConfiguration {

        private PlayerBlockPlacementLimit blockPlacementLimit;

        protected StorageConfig(Path path, PlayerBlockPlacementLimit limit) {
            super(path);
            this.blockPlacementLimit = limit;
        }
    }
}
