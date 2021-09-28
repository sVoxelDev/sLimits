package net.silthus.slimits.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;

@Data
@NoArgsConstructor
public class StorageConfig {

    private String blockPlacement = "storage/block_placement/";

    public StorageConfig(ConfigurationSection config) {
        this.blockPlacement = config.getString("block_placement");
    }
}
