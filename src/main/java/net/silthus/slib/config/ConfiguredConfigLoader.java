package net.silthus.slib.config;

import de.exlll.configlib.configs.yaml.YamlConfiguration;
import lombok.Data;

import java.io.File;

/**
 * Handles the loading of the given file type. The loader will be called for each file depending on
 * the suffix match.
 */
@Data
public abstract class ConfiguredConfigLoader<TConfig extends YamlConfiguration>
        implements ConfigLoader<TConfig>, Comparable<ConfiguredConfigLoader<TConfig>> {

    private final String suffix;
    private int priority = 1;
    private String path;
    private boolean generateId = false;

    public ConfiguredConfigLoader() {
        this(".yml");
    }

    public ConfiguredConfigLoader(String suffix) {
        this.suffix = ("." + suffix + ".yml").toLowerCase();
    }

    public ConfiguredConfigLoader(String suffix, int priority) {
        this(suffix);
        this.priority = priority;
    }

    /**
     * Override this method and unload the loaded config. This will be called to reload or unload
     * configurations.
     *
     * @param id of the config
     */
    public void unloadConfig(String id) {
    }

    /**
     * Tests if the loader suffix matches the suffix of the given file.
     *
     * @param file to match against
     * @return true if the loader matches and {@link #loadConfig(String, File, YamlConfiguration)}
     * should be called.
     */
    public boolean matches(File file) {
        return file.getName().toLowerCase().endsWith(getSuffix());
    }

    /**
     * Gets called when all files with this suffix are loaded. Can be used in config loaders that
     * depend on configs loaded by itself.
     */
    public void onLoadingComplete() {
    }
}
