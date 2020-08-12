package net.silthus.slimits;

import co.aikar.commands.PaperCommandManager;
import kr.entree.spigradle.annotations.Plugin;
import lombok.Getter;
import net.silthus.slib.bukkit.BasePlugin;
import net.silthus.slimits.commands.LimitsCommand;
import net.silthus.slimits.ui.LimitsGUI;
import org.bstats.bukkit.Metrics;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.util.Optional;

@Plugin
public class LimitsPlugin extends BasePlugin implements Listener {

    private static final int BSTATS_ID = 7979;

    public static String PLUGIN_PATH;

    @Getter
    private LimitsManager limitsManager;
    @Getter
    private LimitsGUI gui;
    private PaperCommandManager commandManager;
    private Metrics metrics;

    public LimitsPlugin() {
        metrics = new Metrics(this, BSTATS_ID);
    }

    public LimitsPlugin(
            JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    public Optional<Metrics> getMetrics() {

        return Optional.ofNullable(metrics);
    }

    @Override
    public void enable() {

        PLUGIN_PATH = getDataFolder().getAbsolutePath();

        this.limitsManager = new LimitsManager(this, new LimitsConfig(new File(getDataFolder(), "config.yaml").toPath()));
        this.gui = new LimitsGUI(this, limitsManager);
        this.commandManager = new PaperCommandManager(this);

        this.commandManager.registerCommand(new LimitsCommand(getLimitsManager(), getGui()));

        getLimitsManager().load();
    }

    @Override
    public void disable() {
        getLimitsManager().unload();
    }
}
