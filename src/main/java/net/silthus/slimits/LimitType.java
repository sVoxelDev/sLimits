package net.silthus.slimits;

public enum LimitType {

    BLOCK_PLACEMENT;

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
