package net.silthus.slib.config;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

/**
 * @author Silthus
 */
public class YamlDataMap extends DataMap {

    private final ConfigurationBase config;
    private final String path;

    public YamlDataMap(ConfigurationSection data, ConfigurationBase config) {

        super(data);
        this.config = config;
        this.path = data.getCurrentPath();
    }

    public void save() {

        if (path == null || path.equals("")) {
            for (Map.Entry<String, Object> entry : getValues(true).entrySet()) {
                config.set(entry.getKey(), entry.getValue());
            }
        } else {
            config.set(path, getValues(true));
        }
        config.save();
    }
}
