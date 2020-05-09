package net.silthus.slib.bukkit;

import lombok.Getter;
import net.silthus.slib.components.*;
import net.silthus.slib.components.loader.ClassLoaderComponentLoader;
import net.silthus.slib.components.loader.ClassPathComponentLoader;
import net.silthus.slib.components.loader.ConfigListedComponentLoader;
import net.silthus.slib.components.loader.JarFilesComponentLoader;
import net.silthus.slib.config.Config;
import net.silthus.slib.config.SimpleConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.util.Optional;
import java.util.jar.JarFile;

/**
 * @author Silthus
 */
@Getter
public abstract class BasePlugin extends JavaPlugin implements CommandExecutor, Component {

    private ComponentManager<BukkitComponent> componentManager;

    public BasePlugin() {
    }

    public BasePlugin(
            JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    public final void onEnable() {

        super.onEnable();

        // lets register the plugin as component
        // TODO: do guice dependency injection
        // RaidCraft.registerComponent(getClass(), this);

        // create default folders
        getDataFolder().mkdirs();

        // call the sub plugins to enable
        enable();

        getJarFile().ifPresent(this::setupComponentManager);

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, this::loadDependencyConfigs, 5 * 20L);

        PluginDescriptionFile description = getDescription();
        getLogger().info(description.getName() + "-v" + description.getVersion() + " enabled.");
    }

    public final void onDisable() {

        super.onDisable();

        this.getServer().getScheduler().cancelTasks(this);

        if (componentManager != null) componentManager.unloadComponents();

        // call the sub plugin to disable
        disable();

        // TODO: do guice dependency injection
        // RaidCraft.unregisterComponent(getClass());

        PluginDescriptionFile description = getDescription();
        getLogger().info(description.getName() + "-v" + description.getVersion() + " disabled.");
    }

    public abstract void enable();

    /**
     * Override this method to load your plugins configs that depend on other plugins.
     */
    public void loadDependencyConfigs() {
    }

    public abstract void disable();

    public void reload() {

        disable();
        enable();
    }

    public final <T extends Config> T configure(T config) {

        config.load();
        return config;
    }

    public final SimpleConfiguration<BasePlugin> configure(String configName) {
        return configure(new SimpleConfiguration<>(this, configName));
    }

    public void registerEvents(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    public void unregisterEvents(Listener listener) {
        HandlerList.unregisterAll(listener);
    }

    @SuppressWarnings("unchecked")
    protected void setupComponentManager(JarFile jarFile) {
        componentManager =
                new ComponentManager<>(
                        getLogger(),
                        BukkitComponent.class,
                        configure(new SimpleConfiguration<>(this, "components.yml"))) {
                    @Override
                    protected void setUpComponent(BukkitComponent component) {
                        component.setUp();
                    }
                };

        registerComponentLoaders(jarFile);

        try {
            componentManager.loadComponents(this);
        } catch (Throwable e) {
            getLogger().severe("Unable to load components of: " + getName());
            getLogger().severe(e.getMessage());
        }

        componentManager.enableComponents();
    }

    private void registerComponentLoaders(JarFile jarFile) {
        // -- Component loaders
        final File configDir = new File(getDataFolder(), "config/");

        FileConfiguration globalConfig = getConfig();
        SimpleConfiguration<BasePlugin> config =
                configure(new SimpleConfiguration<BasePlugin>(this, "components.yml"));

        componentManager.addComponentLoader(
                new ConfigListedComponentLoader(getLogger(), globalConfig, config, configDir));

        componentManager.addComponentLoader(
                new ClassPathComponentLoader(getLogger(), configDir, jarFile) {
                    @Override
                    public FileConfiguration createConfigurationNode(File configFile) {
                        return BasePlugin.this.configure(
                                new SimpleConfiguration<>(BasePlugin.this, configFile));
                    }
                });

        for (String dir : config.getStringList("component-class-dirs")) {
            final File classesDir = new File(getDataFolder(), dir);
            if (!classesDir.exists() || !classesDir.isDirectory()) {
                classesDir.mkdirs();
            }
            componentManager.addComponentLoader(
                    new ClassLoaderComponentLoader(getLogger(), classesDir, configDir) {
                        @Override
                        public YamlConfiguration createConfigurationNode(File file) {
                            return BasePlugin.this.configure(new SimpleConfiguration<>(BasePlugin.this, file));
                        }
                    });
        }

        for (String dir : config.getStringList("component-jar-dirs")) {
            final File classesDir = new File(getDataFolder(), dir);
            if (!classesDir.exists() || !classesDir.isDirectory()) {
                classesDir.mkdirs();
            }
            componentManager.addComponentLoader(
                    new JarFilesComponentLoader(getLogger(), classesDir, configDir) {
                        @Override
                        public YamlConfiguration createConfigurationNode(File file) {
                            return BasePlugin.this.configure(new SimpleConfiguration<>(BasePlugin.this, file));
                        }
                    });
        }

        // -- Annotation handlers
        componentManager.registerAnnotationHandler(
                InjectComponent.class, new InjectComponentAnnotationHandler(componentManager));
    }

    public Optional<JarFile> getJarFile() {
        if (getFile() == null) return Optional.empty();
        try {
            return Optional.of(new JarFile(getFile()));
        } catch (Exception e) {
            getLogger()
                    .severe(
                            String.format(
                                    "Unable to load JAR file for plugin {0} from {1}",
                                    getName(), getFile().getAbsolutePath()));
            return Optional.empty();
        }
    }
}
