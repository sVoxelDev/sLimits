package net.silthus.slimits.limits;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import net.silthus.slib.config.converter.MaterialMapConverter;
import net.silthus.slib.configlib.annotation.Comment;
import net.silthus.slib.configlib.annotation.Convert;
import net.silthus.slib.configlib.annotation.NoConvert;
import net.silthus.slib.configlib.configs.yaml.BukkitYamlConfiguration;
import net.silthus.slimits.Constants;
import net.silthus.slimits.LimitMode;
import org.bukkit.Material;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class BlockPlacementLimitConfig extends BukkitYamlConfiguration {

    @NoConvert
    private String identifier = "";

    @Comment({
            "By default the permission is constructed from the path to the config.",
            "You can override this by setting your custom permission here.",
            "Set it to an empty string to use the default permission."
    })
    private String permission = "";

    @Comment({
            "Here you can configure how this limit config plays with other limits.",
            "Defaults to: ADD",
            "ADD: In this mode duplicate block types from different configs will be added to each other and build a sum.",
            "SUBTRACT: In this mode you block limits will be subtracted from other ADD mode configs with the same block types.",
            "ABSOLUTE: Only one absolute config per block type and player is applied. This type of config applies the absolute values and irgnores all other config types."
    })
    private LimitMode mode = LimitMode.ADD;

    @Comment({
            "Here you can limit the limit to a list of worlds.",
            "The limit is applicable to all worlds if left empty."
    })
    private List<String> worlds = new ArrayList<>();

    @Comment({
            "Define you block type limits in the form of a map.",
            "wood:5",
            "bedrock:100"
    })
    @Convert(MaterialMapConverter.class)
    private Map<Material, Integer> blocks = new HashMap<>();

    public BlockPlacementLimitConfig(Path path, BukkitYamlConfiguration.BukkitYamlProperties properties) {
        super(path, properties);
        setIdentifier(path.getFileName().toString().replace(".yml", "").replace(".yaml", ""));
    }

    public BlockPlacementLimitConfig(Path path) {
        super(path);
        setIdentifier(path.getFileName().toString().replace(".yml", "").replace(".yaml", ""));
    }

    public String getPermission() {
        if (!Strings.isNullOrEmpty(permission)) return permission;

        return Constants.PERMISSION_PREFIX + "." + getIdentifier();
    }
}
