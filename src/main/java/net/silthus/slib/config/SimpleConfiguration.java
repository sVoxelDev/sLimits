package net.silthus.slib.config;

import net.silthus.slib.bukkit.BasePlugin;

import java.io.File;

/**
 * @author Silthus
 */
public class SimpleConfiguration<T extends BasePlugin> extends ConfigurationBase<T> {

    public SimpleConfiguration(T plugin, String name) {

        super(plugin, name);
    }

    public SimpleConfiguration(T plugin, File file) {

        super(plugin, file);
    }
}
