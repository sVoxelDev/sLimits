package net.silthus.slimits.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.locales.MessageKey;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.silthus.slimits.SLimitsPlugin;

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

    @Subcommand("reload")
    public void reload() {

        plugin.reload();
        info("reload-success");
    }

    protected void info(String key, String... replacements) {
        getCurrentCommandIssuer().sendInfo(key(key), replacements);
    }
}
