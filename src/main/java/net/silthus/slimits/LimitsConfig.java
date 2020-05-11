package net.silthus.slimits;

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

    private StorageType storage = StorageType.FLATFILES;
    private String storagePath = "storage";

    public LimitsConfig(Path path, BukkitYamlProperties properties) {
        super(path, properties);
    }

    public LimitsConfig(Path path) {
        super(path);
    }
}
