package net.silthus.slib.config;

import de.exlll.configlib.configs.yaml.YamlConfiguration;

import java.io.File;

@FunctionalInterface
public interface ConfigLoader<TConfig extends YamlConfiguration> {

    /**
     * Implement your custom config loading in this function. This will be called for every file that
     * matches the given suffix.
     *
     * @param id     unique id of the config based on its path and file displayName without the suffix.
     *               Subfolders will be separated by a colon, e.g. subfolder1.subfolder2.my-config
     * @param file   the config file that should be loaded
     * @param config loaded config file not null
     */
    void loadConfig(String id, File file, TConfig config);
}
