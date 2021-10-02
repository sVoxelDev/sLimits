package net.silthus.slimits.commands;

import co.aikar.commands.BaseCommand;
import net.silthus.slimits.TestBase;
import org.bukkit.ChatColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class LimitsCommandTests extends TestBase {

    private LimitsCommand command;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        player.setOp(true);
        command = prepareLimitsCommand();
    }

    @Test
    void create() {
        LimitsCommand command = new LimitsCommand(plugin);

        assertThat(command)
                .isInstanceOf(BaseCommand.class)
                .extracting("plugin")
                .isNotNull()
                .isEqualTo(plugin);
    }

    @Test
    void reload() {

        performCommand("slimits reload");

        verify(command.getPlugin(), times(1)).reload();
        assertThat(player.nextMessage()).contains("sLimits successfully reloaded. Loaded 2 limits.");
    }

    @Test
    void save() {

        performCommand("slimits save");

        verify(command.getPlugin().getLimitsService()).saveLimits();
        assertThat(player.nextMessage()).contains("All limits saved successfully.");
    }

    private void performCommand(String command) {
        assertThat(player.performCommand(command)).isTrue();
    }

    private LimitsCommand prepareLimitsCommand() {
        LimitsCommand command = plugin.getCommandManager().getRegisteredRootCommands()
                .stream().filter(rootCommand -> rootCommand.getDefCommand() instanceof LimitsCommand)
                .findFirst()
                .map(rootCommand -> (LimitsCommand) rootCommand.getDefCommand())
                .orElse(new LimitsCommand(plugin));
        command.setPlugin(spy(plugin));
        return command;
    }
}
