package net.silthus.slib.config;

import net.silthus.slib.bukkit.BasePlugin;

public class ComponentsConfig extends ConfigurationBase<BasePlugin> {

    public ComponentsConfig(BasePlugin plugin) {
        super(plugin, "components.yml");
    }
}
