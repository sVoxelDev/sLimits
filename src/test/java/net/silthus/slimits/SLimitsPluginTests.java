package net.silthus.slimits;

import net.silthus.slimits.testing.TestBase;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SLimitsPluginTests extends TestBase {

    @Test
    void onEnable_loadsLimitsFromConfig() {

        assertThat(plugin.getLimitsService())
                .isNotNull()
                .extracting(LimitsService::getLimits)
                .asList()
                .isNotEmpty();
    }
}
