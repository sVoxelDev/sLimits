package net.silthus.slimits;

import co.aikar.commands.PaperCommandManager;
import kr.entree.spigradle.annotations.PluginMain;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.silthus.slimits.commands.LimitsCommand;
import net.silthus.slimits.config.LimitsConfig;
import net.silthus.slimits.limits.PlacedBlock;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

@PluginMain
public class SLimitsPlugin extends JavaPlugin {

    public static final String PERMISSION_PREFIX = "slimits";
    public static final String PERMISSION_LIMITS_PREFIX = PERMISSION_PREFIX + ".limits";
    public static final String PERMISSION_IGNORE_LIMITS = PERMISSION_LIMITS_PREFIX + ".ignore";

    @Getter
    @Setter(AccessLevel.PACKAGE)
    private LimitsService limitsService;
    @Getter
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

        setupAndLoadConfigs();

        setupLimitsService();

        setupCommands();
    }

    private void setupAndLoadConfigs() {

        ConfigurationSerialization.registerClass(PlacedBlock.class);

        saveDefaultConfig();
        saveResource("lang_en.yaml", true);

        limitsConfig = LimitsConfig.loadFromFile(getConfig());
    }

    private void setupLimitsService() {

        this.limitsService = new LimitsService(this);
        limitsService.loadLimits(limitsConfig);
        Bukkit.getPluginManager().registerEvents(limitsService, this);
    }

    private void setupCommands() {

        this.commandManager = new PaperCommandManager(this);

        loadCommandLocales();
        registerCommands();
    }

    private void registerCommands() {
        commandManager.registerCommand(new LimitsCommand(this));
    }

    private void loadCommandLocales() {
        try {
            commandManager.getLocales().loadYamlLanguageFile("lang_en.yaml", Locale.ENGLISH);
        } catch (IOException | InvalidConfigurationException e) {
            getLogger().severe("Failed to load language config: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {

        limitsService.saveLimits();
    }
}
