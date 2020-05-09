package net.silthus.slib.components.loader;

import net.silthus.slib.components.AbstractComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author zml2008
 */
public class ConfigListedComponentLoader extends AbstractComponentLoader {
    private final ConfigurationSection jarComponentAliases, globalConfig;

    public ConfigListedComponentLoader(
            Logger logger,
            ConfigurationSection globalConfig,
            ConfigurationSection aliasList,
            File configDir) {
        super(logger, configDir);
        this.jarComponentAliases = aliasList;
        this.globalConfig = globalConfig;
    }

    public Collection<AbstractComponent> loadComponents() {
        List<AbstractComponent> components = new ArrayList<AbstractComponent>();
        // The lists of components to load.
        Set<String> disabledComponents =
                new LinkedHashSet<String>(globalConfig.getStringList("components.disabled"));
        Set<String> stagedEnabled =
                new LinkedHashSet<String>(globalConfig.getStringList("components.enabled"));
        for (String key : jarComponentAliases.getKeys(false)) { // Load the component aliases
            if (!stagedEnabled.contains(key)
                    && !stagedEnabled.contains(
                    jarComponentAliases.getString(
                            key + ".class", key))) { // Not already in the enabled list.
                if (jarComponentAliases.getBoolean(key + ".default")) { // Enabled by default
                    stagedEnabled.add(key);
                } else {
                    disabledComponents.add(key);
                }
            }
        }
        stagedEnabled.removeAll(disabledComponents);

        // And go through the enabled components from the configuration
        for (Iterator<String> i = stagedEnabled.iterator(); i.hasNext(); ) {
            String nextName = i.next();
            nextName = jarComponentAliases.getString(nextName + ".class", nextName);
            Class<?> clazz = null;
            try {
                clazz = Class.forName(nextName);
            } catch (ClassNotFoundException ignore) {
            }

            if (!isComponentClass(clazz)) {
                getLogger()
                        .warning(
                                "Invalid or unknown class found in enabled components: "
                                        + nextName
                                        + ". Moving to disabled components list.");
                i.remove();
                disabledComponents.add(nextName);
                continue;
            }

            try {
                components.add(instantiateComponent(clazz));
            } catch (Throwable t) {
                getLogger().warning("Error initializing component " + clazz + ": " + t.getMessage());
                t.printStackTrace();
            }
        }

        // And update the configuration now that we're done loading from this loader
        globalConfig.set("components.disabled", new ArrayList<>(disabledComponents));
        globalConfig.set("components.enabled", new ArrayList<>(stagedEnabled));
        return components;
    }

    @Override
    public ConfigurationSection getConfiguration(AbstractComponent component) {
        return globalConfig.getConfigurationSection("component." + toFileName(component));
    }

    @Override
    public YamlConfiguration createConfigurationNode(File configFile) {
        throw new UnsupportedOperationException("This is not used in ConfigListedComponentLoader!");
    }
}
