package net.silthus.slib.config;

import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public interface Config extends ConfigurationSection {

    void reload();

    void load();

    void save();
}
