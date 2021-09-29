package net.silthus.slimits;

import co.aikar.commands.PaperCommandManager;
import kr.entree.spigradle.annotations.PluginMain;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.silthus.slimits.commands.LimitsCommand;
import net.silthus.slimits.config.LimitsConfig;
import net.silthus.slimits.limits.PlacedBlock;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;

@PluginMain
public class SLimitsPlugin extends JavaPlugin {

    public static final String PERMISSION_PREFIX = "slimits";
    public static final String PERMISSION_LIMITS_PREFIX = PERMISSION_PREFIX + ".limits";

    @Getter
    @Setter(AccessLevel.PACKAGE)
    private LimitsService limitsService;
    @Getter
    @Setter(AccessLevel.PACKAGE)
    private PaperCommandManager commandManager;
    @Getter
    private LimitsConfig limitsConfig = new LimitsConfig();

    public SLimitsPlugin() {
    }

    public SLimitsPlugin(
            JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    public void reload() {

        reloadConfig();
        limitsService.reload();
    }

    @Override
    public void onEnable() {

        ConfigurationSerialization.registerClass(PlacedBlock.class);

        saveDefaultConfig();
        limitsConfig = LimitsConfig.loadFromFile(getConfig());

        this.limitsService = new LimitsService(this);
        limitsService.loadLimits(limitsConfig);

        this.commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new LimitsCommand(this));
    }

    @Override
    public void onDisable() {

        limitsService.saveLimits();
    }
}
