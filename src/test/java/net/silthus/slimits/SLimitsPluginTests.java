package net.silthus.slimits;

import net.silthus.slimits.config.LimitsConfig;
import net.silthus.slimits.testing.TestBase;
import org.junit.jupiter.api.Test;

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
    void onDisable_savesAllLimits() {

        LimitsService service = spy(plugin.getLimitsService());
        plugin.setLimitsService(service);

        plugin.onDisable();

        verify(service, times(1))
                .saveLimits();
    }
}
