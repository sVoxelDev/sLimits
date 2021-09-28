package net.silthus.slimits;

import com.google.common.base.Strings;
import lombok.Data;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.Objects;

@Data
public class BlockPlacementLimitConfig {

    public static String getKey(Material material, int limit) {
        return material.getKey().getKey() + "-" + limit;
    }

    private final String key;
    private Material type;
    private int limit;

    private String permission;

    public BlockPlacementLimitConfig(Map<?, ?> map) {
        this(
                Objects.requireNonNull(Material.matchMaterial((String) map.get("type"))),
                (int) map.get("limit"),
                (String) map.get("permission")
        );
    }

    public BlockPlacementLimitConfig(@NonNull Material type, int limit, String permission) {
        this.type = type;
        this.limit = limit;
        this.key = getKey(type, limit);
        this.permission = Strings.isNullOrEmpty(permission) ? getPermission(getPermissionSuffix(type, limit)) : permission;
    }

    public BlockPlacementLimitConfig(@NonNull Material type, int limit) {
        this.type = type;
        this.limit = limit;
        this.key = getKey(type, limit);
        this.permission = getPermission(getPermissionSuffix(type, limit));
    }

    public BlockPlacementLimitConfig(@NonNull String key, @NonNull ConfigurationSection config) {
        this.key = key;
        String typeString = config.getString("type");
        Material type = Material.matchMaterial(Objects.requireNonNull(typeString, "\"type\" must not be empty and set to a valid block type."));
        this.type = Objects.requireNonNull(type, typeString + " is not a valid block type.");
        this.limit = config.getInt("limit", 1);
        this.permission = config.getString("permission", getPermission(key));
    }

    private static String getPermission(String suffix) {
        return LimitType.BLOCK_PLACEMENT.getPermissionPrefix() + "."
                + suffix;
    }

    private static String getPermissionSuffix(Material type, int limit) {
        return type.getKey().getKey() + "." + limit;
    }
}
