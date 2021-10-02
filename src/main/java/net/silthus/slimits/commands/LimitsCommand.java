package net.silthus.slimits.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.MessageKeys;
import co.aikar.commands.MessageType;
import co.aikar.commands.annotation.*;
import co.aikar.locales.MessageKey;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.silthus.slimits.SLimitsPlugin;
import net.silthus.slimits.limits.PlayerLimit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

@CommandAlias("slimits|limits")
public class LimitsCommand extends BaseCommand {

    static MessageKey key(String key) {
        return MessageKey.of("acf-slimits." + key);
    }

    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    private SLimitsPlugin plugin;

    public LimitsCommand(SLimitsPlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    @Subcommand("list")
    @CommandCompletion("@players")
    @CommandPermission("slimits.player.list")
    public void list(@Flags("other,defaultself") @Optional Player player) {

        boolean isNotSamePlayer = !player.equals(getCurrentCommandIssuer().getIssuer());
        if (isNotSamePlayer && !getCurrentCommandIssuer().hasPermission("slimits.admin.list")) {
            getCurrentCommandIssuer().sendMessage(MessageType.ERROR, MessageKeys.PERMISSION_DENIED_PARAMETER, "{param}", "player");
            throw new InvalidCommandArgument(false);
        }

        List<PlayerLimit> limits = plugin.getLimitsService().getPlayerLimits(player);
        if (limits.isEmpty()) {
            info("list-no-limits");
        } else {
            if (isNotSamePlayer)
                info("list-header-other-player",
                        "{player}", player.getName()
                );
            else
                info("list-header");

            for (PlayerLimit limit : limits) {
                getCurrentCommandIssuer().sendMessage(ChatColor.GOLD + "  - "
                        + limit.getType().getKey().getKey() + ": " + limit.getCount() + "/" + limit.getLimit());
            }
        }
    }

    @Subcommand("reload")
    @CommandPermission("slimits.admin.reload")
    public void reload() {

        plugin.reload();
        info("reload-success",
                "{limits}", String.valueOf(plugin.getLimitsService().getLimits().size())
        );
    }

    @Subcommand("save")
    @CommandPermission("slimits.admin.save")
    public void save() {

        plugin.getLimitsService().saveLimits();
        info("save-success");
    }

    protected void info(String key, String... replacements) {
        getCurrentCommandIssuer().sendInfo(key(key), replacements);
    }
}
