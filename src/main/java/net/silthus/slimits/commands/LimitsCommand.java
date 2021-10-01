package net.silthus.slimits.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.locales.MessageKey;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.silthus.slimits.SLimitsPlugin;

@CommandAlias("slimits|limits")
@CommandPermission("slimits.admin")
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

    @Subcommand("reload")
    @CommandPermission("slimits.admin.reload")
    public void reload() {

        plugin.reload();
        info("reload-success");
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
