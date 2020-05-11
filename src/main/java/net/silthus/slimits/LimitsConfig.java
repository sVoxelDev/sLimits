package net.silthus.slimits;

import de.exlll.configlib.annotation.Comment;
import de.exlll.configlib.annotation.ElementType;
import de.exlll.configlib.configs.yaml.BukkitYamlConfiguration;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.silthus.slimits.limits.BlockPlacementLimitConfig;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class LimitsConfig extends BukkitYamlConfiguration {

    @Comment("Supported storage types: FLATFILES")
    private StorageType storage = StorageType.FLATFILES;
    @Comment("Tell the plugin where you want your player data to be stored. Defaults to: storage/ inside the plugin folder.")
    private String storagePath = "storage";

    public LimitsConfig(Path path, BukkitYamlProperties properties) {
        super(path, properties);
    }

    public LimitsConfig(Path path) {
        super(path);
    }
}
