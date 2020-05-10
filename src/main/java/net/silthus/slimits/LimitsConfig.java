package net.silthus.slimits;

import de.exlll.configlib.annotation.ElementType;
import de.exlll.configlib.configs.yaml.BukkitYamlConfiguration;
import net.silthus.slimits.limits.BlockPlacementLimitConfig;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LimitsConfig extends BukkitYamlConfiguration {

    private StorageType storage = StorageType.FLATFILES;

    public LimitsConfig(Path path, BukkitYamlProperties properties) {
        super(path, properties);
    }

    public LimitsConfig(Path path) {
        super(path);
    }
}
