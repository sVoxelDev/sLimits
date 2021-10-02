package net.silthus.slimits.commands;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import co.aikar.commands.BaseCommand;
import net.silthus.slimits.TestBase;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.permissions.PermissionAttachment;
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

    @Test
    void list() {

        loadConfiguredLimits();
        player.setOp(false);
        player.addAttachment(plugin, "slimits.player.list", true);
        performCommand("slimits list");

        assertThat(player.nextMessage())
                .contains(ChatColor.GOLD + "You have the following block placement limits:");
        assertThat(player.nextMessage())
                .contains("  - stone: 0/10");
        assertThat(player.nextMessage())
                .contains("  - bedrock: 0/5");
    }

    @Test
    void list_withNoLimits_showsDifferentMessage() {

        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, "slimits.player.list", true);

        player.performCommand("slimits list");

        assertThat(player.nextMessage())
                .contains(ChatColor.GREEN + "You have no limits.");
    }

    @Test
    void list_canListOtherPlayerLimits() {

        PlayerMock admin = server.addPlayer();
        admin.setOp(true);
        placeBlocks(Material.STONE, 5);

        assertThat(admin.performCommand("slimits list " + player.getName())).isTrue();

        assertThat(admin.nextMessage())
                .contains(ChatColor.AQUA + player.getName()
                        + ChatColor.GOLD + " has the following block placement limits:");
        assertThat(admin.nextMessage())
                .contains("  - stone: 5/10");
        assertThat(admin.nextMessage())
                .contains("  - bedrock: 0/5");
    }

    @Test
    void list_onlyPlayerWithPermissionCanListOtherLimits() {

        PlayerMock admin = server.addPlayer();

        assertThat(admin.performCommand("slimits list " + player.getName())).isTrue();
        assertThat(admin.nextMessage())
                .isEqualTo(ChatColor.RED + "I'm sorry, but you do not have permission to perform this command.");
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
