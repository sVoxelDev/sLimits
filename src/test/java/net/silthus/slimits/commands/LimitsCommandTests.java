package net.silthus.slimits.commands;

import co.aikar.commands.BaseCommand;
import net.silthus.slimits.testing.TestBase;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LimitsCommandTests extends TestBase {

    @Test
    void create() {
        LimitsCommand command = new LimitsCommand(plugin);

        assertThat(command)
                .isInstanceOf(BaseCommand.class)
                .extracting("plugin")
                .isNotNull()
                .isEqualTo(plugin);
    }
}
