package net.silthus.slimits.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.io.File;

@Data
@NoArgsConstructor
public class StorageConfig {

    private String blockPlacement = "storage/block_placement/";

    public StorageConfig(ConfigurationSection config) {
        this.blockPlacement = config.getString("block_placement");
    }

    public File getStoragePath(Plugin plugin) {
        File path = new File(blockPlacement);
        if (path.isAbsolute())
            return path;
        return new File(plugin.getDataFolder(), blockPlacement);
    }
}
