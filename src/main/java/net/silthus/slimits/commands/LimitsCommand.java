package net.silthus.slimits.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.locales.MessageKey;
import lombok.Getter;
import net.silthus.slimits.LimitsManager;
import net.silthus.slimits.limits.PlayerBlockPlacementLimit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        player.sendMessage(ChatColor.BOLD + "" + ChatColor.DARK_PURPLE + "---=== " + ChatColor.YELLOW + "Your Block Placement Limits " + ChatColor.DARK_PURPLE + "===---");

        PlayerBlockPlacementLimit playerLimit = getLimitsManager().getPlayerLimit(player);
        List<String> messages = new ArrayList<>();

        playerLimit.getLimits().forEach((material, limit) -> {
            StringBuilder sb = new StringBuilder();
            messages.add(sb.append(ChatColor.BOLD).append(ChatColor.GREEN).append(material.name()).append(": ")
                    .append(ChatColor.RESET).append(ChatColor.AQUA)
                    .append(playerLimit.getCount(material)).append(ChatColor.GREEN).append("/").append(ChatColor.AQUA).append(limit)
                    .append(ChatColor.YELLOW).append(" blocks placed.")
                    .toString());
        });

        player.sendMessage(messages.toArray(new String[0]));
    }

    @Subcommand("reload")
    @Description("Reloads the plugin fetching updated configs from the disk.")
    @CommandPermission("slimits.admin.reload")
    public void reload() {

        getLimitsManager().reload();
        getCurrentCommandIssuer().sendMessage(ChatColor.YELLOW + "Reloaded all limit configs.");
    }
}
