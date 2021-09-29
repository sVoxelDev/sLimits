package net.silthus.slimits;

import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.RootCommand;
import net.silthus.slimits.commands.LimitsCommand;
import net.silthus.slimits.config.LimitsConfig;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class SLimitsPluginTests extends TestBase {

    @Test
    void onEnable_loadsLimitsFromConfig() {

        assertThat(plugin.getLimitsService())
                .isNotNull()
                .extracting(LimitsService::getLimits)
                .asList()
                .isNotEmpty();
    }

    @Test
    void onEnable_loadsAndSetsConfig() {

        assertThat(plugin.getLimitsConfig())
                .isNotNull()
                .extracting(LimitsConfig::getLimits)
                .asList()
                .isNotEmpty();
    }

    @Test
    void onEnable_savesLanguageConfigToDisk() {

        assertThat(new File(plugin.getDataFolder(), "lang_en.yaml").exists()).isTrue();
    }

    @Test
    void onDisable_savesAllLimits() {

        LimitsService service = spy(plugin.getLimitsService());
        plugin.setLimitsService(service);

        plugin.onDisable();

        verify(service, times(1))
                .saveLimits();
    }

    @Test
    void reload_reloadsConfig_fromDisk() {

        SLimitsPlugin plugin = spy(this.plugin);

        plugin.reload();

        verify(plugin, times(1)).reloadConfig();
    }

    @Test
    void reload_reloadsConfig_andThen_LimitsService() {

        SLimitsPlugin plugin = spy(this.plugin);
        plugin.setLimitsService(spy(plugin.getLimitsService()));

        plugin.reload();

        InOrder inOrder = inOrder(plugin, plugin.getLimitsService());
        inOrder.verify(plugin, times(1)).reloadConfig();
        inOrder.verify(plugin.getLimitsService(), times(1)).reload();
    }

    @Test
    void onEnable_registersLimitsCommand() {

        assertThat(plugin)
                .extracting(SLimitsPlugin::getCommandManager)
                .isNotNull()
                .isInstanceOf(PaperCommandManager.class);

        assertThat(plugin.getCommandManager().getRegisteredRootCommands()
                .stream().filter(rootCommand -> rootCommand.getDefCommand() instanceof LimitsCommand).findFirst())
                .isPresent().get()
                .extracting(RootCommand::getCommandName)
                .isEqualTo("slimits");
        assertThat(plugin.getCommandManager().hasRegisteredCommands()).isTrue();
    }
}
