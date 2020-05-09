package net.silthus.slimits;

import kr.entree.spigradle.Plugin;
import lombok.Getter;
import net.silthus.slib.bukkit.BasePlugin;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;

@Plugin
public class LimitsPlugin extends BasePlugin implements Listener {

    @Getter
    private LimitsManager limitsManager;

    public LimitsPlugin() {
    }

    public LimitsPlugin(
            JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    @Override
    public void enable() {

        this.limitsManager = new LimitsManager(this);

        load();
    }

    public void load() {
        getLimitsManager().load();
    }

    @Override
    public void disable() {
    }
}
