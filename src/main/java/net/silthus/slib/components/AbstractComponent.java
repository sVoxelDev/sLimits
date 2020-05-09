package net.silthus.slib.components;

import lombok.Getter;
import net.silthus.slib.bukkit.BasePlugin;
import net.silthus.slib.components.loader.ComponentLoader;
import net.silthus.slib.config.ConfigurationBase;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author zml2008
 */
@Getter
@TemplateComponent
public abstract class AbstractComponent implements Component {

    /**
     * The raw configuration for this component. This is usually accessed through ConfigurationBase
     * subclasses and #configure()
     */
    private ConfigurationSection rawConfiguration;

    private BasePlugin plugin;

    private ComponentLoader loader;

    private ComponentInformation info;

    private boolean enabled;

    protected void setUp(BasePlugin plugin, ComponentLoader loader, ComponentInformation info) {
        this.plugin = plugin;
        this.loader = loader;
        this.info = info;
    }

    /**
     * This method is called once all of this Component's fields have been set up and all other
     * Component classes have been discovered
     */
    public abstract void enable();

    public void disable() {
    }

    public void reload() {
        if (getConfiguration() != null && getConfiguration() instanceof ConfigurationBase) {
            ((ConfigurationBase) getConfiguration()).reload();
        }
    }

    protected <T extends ConfigurationBase> T configure(T config) {
        return getPlugin().configure(config);
    }

    public boolean isEnabled() {
        return enabled;
    }

    void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ComponentLoader getComponentLoader() {
        return loader;
    }

    public ComponentInformation getInformation() {
        return info;
    }

    public ConfigurationSection getConfiguration() {
        if (rawConfiguration != null) {
            return rawConfiguration;
        } else {
            return rawConfiguration = getComponentLoader().getConfiguration(this);
        }
    }

    public void saveConfig() {
        if (getConfiguration() != null && getConfiguration() instanceof ConfigurationBase) {
            ((ConfigurationBase) getConfiguration()).save();
        }
    }
}
