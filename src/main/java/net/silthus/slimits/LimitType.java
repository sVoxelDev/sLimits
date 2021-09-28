package net.silthus.slimits;

import lombok.Getter;

public enum LimitType {

    BLOCK_PLACEMENT(SLimitsPlugin.PERMISSION_LIMITS_PREFIX + ".block_placement");

    @Getter
    private final String permissionPrefix;

    LimitType(String permissionPrefix) {
        this.permissionPrefix = permissionPrefix;
    }

    public String getConfigKey() {
        return name().toLowerCase();
    }

    public static LimitType fromString(String name) {

        for (LimitType value : LimitType.values()) {
            if (value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }

        return null;
    }
}
