package net.silthus.slimits;

import lombok.Getter;
import lombok.Setter;
import net.silthus.slib.configlib.annotation.Comment;
import net.silthus.slib.configlib.annotation.ConfigurationElement;
import net.silthus.slib.configlib.configs.yaml.BukkitYamlConfiguration;
import net.silthus.slib.configlib.format.FieldNameFormatters;

import java.nio.file.Path;

@Getter
@Setter
public class LimitsConfig extends BukkitYamlConfiguration {

    @Comment("Supported storage types: FLATFILES")
    private StorageType storage = StorageType.FLATFILES;
    @Comment("Tell the plugin where you want your player data to be stored. Defaults to: storage/ inside the plugin folder.")
    private String storagePath = "storage";
    private BlockPlacementConfig blockConfig = new BlockPlacementConfig();

    public LimitsConfig(Path path) {
        super(path,
                BukkitYamlProperties.builder()
                    .setFormatter(FieldNameFormatters.LOWER_UNDERSCORE)
                    .build()
        );
    }

    @Getter
    @Setter
    @ConfigurationElement
    public static class BlockPlacementConfig {

        @Comment({
                "If set to false, blocks that other players destroy will not be subtracted from the player limit.",
                "Could be used for PvP scenarios.",
                "Defaults to: true"
        })
        private boolean deleteBlocksDestroyedByOthers = true;
        @Comment({
                "If set to true, only the player who placed a block can destroy it.",
                "Defaults to: false"
        })
        private boolean blockLimitedBlockDestruction = false;
    }
}
