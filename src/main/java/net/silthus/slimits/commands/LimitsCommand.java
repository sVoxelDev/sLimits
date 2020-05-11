package net.silthus.slimits.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import lombok.Getter;
import net.silthus.slimits.LimitsManager;
import org.bukkit.entity.Player;

@CommandAlias("slimits|limits|limit|lim")
public class LimitsCommand extends BaseCommand {

    @Getter
    private final LimitsManager limitsManager;

    public LimitsCommand(LimitsManager limitsManager) {
        this.limitsManager = limitsManager;
    }

    @Default
    @Subcommand("list")
    @Description("Lists all of your or another players limits.")
    public void listLimits(Player player, String[] args) {

        StringBuilder sb = new StringBuilder("---=== Your Block Placement Limits ===---");

        getLimitsManager().getPlayerLimit(player).getCounts().forEach((material, integer) -> {

        });
    }
}
