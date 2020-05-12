package net.silthus.slimits;

/**
 * Configures how multiple limit configurations are handled.
 */
public enum LimitMode {
    /**
     * If a player has a config with an absolute limit,
     * that config will define the total limit of the player
     * for all blocks configured in that config.
     *
     * Only one absolute config can be configured at a time.
     */
    ABSOLUTE,
    /**
     * Adds up the limits from all configs that apply to the player.
     */
    ADD,
    /**
     * Subtracts the limits in this config from the total limit of the player.
     * Limits may never fall under zero. Defining a subtract config without a
     * additive config will result in denied block placements.
     */
    SUBTRACT
}
