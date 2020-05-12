package net.silthus.slimits.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
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

        playerLimit.getCounts().forEach((material, integer) -> {
            StringBuilder sb = new StringBuilder();
            messages.add(sb.append(ChatColor.BOLD).append(ChatColor.GREEN).append(material.name()).append(": ")
                    .append(ChatColor.RESET).append(ChatColor.AQUA).append("you placed ")
                    .append(integer).append(ChatColor.GREEN).append("/").append(ChatColor.AQUA).append(playerLimit.getLimit(material).orElse(0)).toString());
        });

        player.sendMessage(messages.toArray(new String[0]));
    }
}
