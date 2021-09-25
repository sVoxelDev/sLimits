package net.silthus.slimits;

import kr.entree.spigradle.annotations.PluginMain;
import lombok.Getter;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;

@PluginMain
public class SLimitsPlugin extends JavaPlugin {

    @Getter
    private LimitsService limitsService;

    public SLimitsPlugin() {
    }

    public SLimitsPlugin(
            JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    @Override
    public void onEnable() {

        saveDefaultConfig();

        this.limitsService = new LimitsService(this);
        limitsService.loadLimits(LimitsConfig.loadFromFile(getConfig()));
    }
}
