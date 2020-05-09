package net.silthus.slib.bukkit;

import lombok.Getter;
import net.silthus.slib.config.Config;
import net.silthus.slib.config.SimpleConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;

/**
 * @author Silthus
 */
@Getter
public abstract class BasePlugin extends JavaPlugin implements CommandExecutor {

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

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, this::loadDependencyConfigs, 5 * 20L);

        PluginDescriptionFile description = getDescription();
        getLogger().info(description.getName() + "-v" + description.getVersion() + " enabled.");
    }

    public final void onDisable() {

        super.onDisable();

        this.getServer().getScheduler().cancelTasks(this);

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
}
