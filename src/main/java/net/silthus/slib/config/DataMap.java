package net.silthus.slib.config;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.util.Map;

/**
 * @author Silthus
 */
public abstract class DataMap extends MemoryConfiguration {

    public DataMap(Map<?, ?> data) {

        super();
        if (data != null) {
            convertMapsToSections(data, this);
        }
    }

    public DataMap(ConfigurationSection data) {

        super();
        if (data != null) {
            convertMapsToSections(data.getValues(true), this);
        }
    }

    protected void convertMapsToSections(Map<?, ?> input, ConfigurationSection section) {

        for (Map.Entry<?, ?> entry : input.entrySet()) {
            // always lower case the keys
            String key = entry.getKey().toString().toLowerCase();
            Object value = entry.getValue();

            if (value instanceof Map) {
                ConfigurationSection subSection = section.getConfigurationSection(key);
                if (subSection == null) subSection = section.createSection(key);
                convertMapsToSections((Map<?, ?>) value, subSection);
            } else if (value instanceof ConfigurationSection) {
                ConfigurationSection subSection = section.getConfigurationSection(key);
                if (subSection == null) subSection = section.createSection(key);
                convertMapsToSections(((ConfigurationSection) value).getValues(true), subSection);
            } else {
                section.set(key, value);
            }
        }
    }

    /**
     * Will merge the given map with this map. The given map will override values if defined. You also
     * need to make sure that the sections match up with the defined keys.
     *
     * @param section to merge
     */
    public void merge(ConfigurationSection section) {

        // we want to merge so that this current map gets overriden
        convertMapsToSections(section.getValues(true), this);
    }

    public ConfigurationSection getSafeConfigSection(String path) {

        ConfigurationSection configurationSection = getConfigurationSection(path);
        if (configurationSection == null) {
            configurationSection = createSection(path);
        }
        return configurationSection;
    }

    @Override
    public Object get(String path, Object def) {

        if (!isSet(path)) {
            set(path, def);
            return def;
        } else {
            return super.get(path, def);
        }
    }

    @Override
    public boolean isSet(String path) {

        Configuration root = getRoot();
        if (root == null) {
            return false;
        }
        if (root.options().copyDefaults()) {
            return contains(path);
        }
        return super.get(path, null) != null;
    }
}
