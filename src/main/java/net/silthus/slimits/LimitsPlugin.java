package net.silthus.slimits;

import co.aikar.commands.PaperCommandManager;
import kr.entree.spigradle.Plugin;
import lombok.Getter;
import net.silthus.slib.bukkit.BasePlugin;
import net.silthus.slimits.commands.LimitsCommand;
import net.silthus.slimits.ui.LimitsGUI;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;

@Plugin
public class LimitsPlugin extends BasePlugin implements Listener {

    public static String PLUGIN_PATH;

    @Getter
    private LimitsManager limitsManager;
    @Getter
    private LimitsGUI gui;
    private PaperCommandManager commandManager;

    public LimitsPlugin() {
    }

    public LimitsPlugin(
            JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    @Override
    public void enable() {

        PLUGIN_PATH = getDataFolder().getAbsolutePath();

        this.limitsManager = new LimitsManager(this);
        this.gui = new LimitsGUI(this, limitsManager);
        this.commandManager = new PaperCommandManager(this);

        this.commandManager.registerCommand(new LimitsCommand(getLimitsManager(), getGui()));
    }

    @Override
    public void loadDependencyConfigs() {
        load();
    }

    public void load() {
        getLimitsManager().load();
    }

    @Override
    public void reload() {
        getLimitsManager().reload();
    }

    @Override
    public void disable() {
        getLimitsManager().unload();
    }
}
