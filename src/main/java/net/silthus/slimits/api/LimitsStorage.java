package net.silthus.slimits.api;

import net.silthus.slimits.limits.PlayerBlockPlacementLimit;
import org.bukkit.entity.Player;

/**
 * Provides loading and saving of player limits in
 * a custom data store.
 *
 * Data stores could be flat files, a database or any other implementation.
 */
public interface LimitsStorage {

    /**
     * Stores the given limits in the implemented data store.
     * @param limits to store
     */
    void store(PlayerBlockPlacementLimit... limits);

    /**
     * Loads all stored limits from the data store
     * @return all limits
     */
    PlayerBlockPlacementLimit[] load();

    /**
     * Loads a single player limit from the data store.
     * @param player to load the limits for
     * @return limits of a single player
     */
    PlayerBlockPlacementLimit load(Player player);
}
