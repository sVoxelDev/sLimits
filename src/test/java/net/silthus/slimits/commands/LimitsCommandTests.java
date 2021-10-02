package net.silthus.slimits.commands;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import co.aikar.commands.BaseCommand;
import net.silthus.slimits.TestBase;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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

        PlayerMock admin = server.addPlayer();
        admin.addAttachment(plugin, "slimits.admin.reload", true);
        assertThat(admin.performCommand("slimits reload")).isTrue();

        verify(command.getPlugin(), times(1)).reload();
        assertThat(admin.nextMessage()).contains("sLimits successfully reloaded. Loaded 2 limits.");
    }

    @Test
    void save() {

        PlayerMock admin = server.addPlayer();
        admin.addAttachment(plugin, "slimits.admin.save", true);
        assertThat(admin.performCommand("slimits save")).isTrue();

        verify(command.getPlugin().getLimitsService()).saveLimits();
        assertThat(admin.nextMessage()).contains("All limits saved successfully.");
    }

    @Test
    void list() {

        loadConfiguredLimits();
        player.addAttachment(plugin, "slimits.player.list", true);
        assertThat(player.performCommand("limits")).isTrue();

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

        loadConfiguredLimits();

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
    void list_canListOtherPlayerLimits_whenPlayerHasNoLimits() {

        PlayerMock admin = server.addPlayer();
        admin.setOp(true);
        PlayerMock otherPlayer = server.addPlayer();

        assertThat(admin.performCommand("slimits list " + otherPlayer.getName())).isTrue();

        assertThat(admin.nextMessage())
                .contains(ChatColor.AQUA + otherPlayer.getName()
                        + ChatColor.GOLD + " has no limits.");
    }

    @Test
    void list_onlyPlayerWithPermissionCanListOtherLimits() {

        PlayerMock admin = server.addPlayer();

        assertThat(admin.performCommand("slimits list " + player.getName())).isTrue();
        assertThat(admin.nextMessage())
                .isEqualTo(ChatColor.RED + "I'm sorry, but you do not have permission to perform this command.");
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
