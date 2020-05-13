package net.silthus.slimits.ui;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import net.silthus.slimits.LimitsManager;
import net.silthus.slimits.LimitsPlugin;
import net.silthus.slimits.limits.PlayerBlockPlacementLimit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LimitsGUI {

    private final LimitsPlugin plugin;
    private final LimitsManager limitsManager;

    public LimitsGUI(LimitsPlugin plugin, LimitsManager limitsManager) {
        this.plugin = plugin;
        this.limitsManager = limitsManager;
    }

    public Gui showLimits(Player player) {

        PlayerBlockPlacementLimit playerLimit = limitsManager.getPlayerLimit(player);
        Map<Material, Integer> limits = playerLimit.getLimits();

        List<ItemStack> itemList = new ArrayList<>();

        limits.forEach((material, limit) -> {
            StringBuilder sb = new StringBuilder();
            ItemStack item = new ItemStack(material, limit);

            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(sb.append(ChatColor.BOLD).append(ChatColor.GREEN).append(material.name()).append(": ")
                    .append(ChatColor.RESET).append(ChatColor.AQUA)
                    .append(playerLimit.getCount(material)).append(ChatColor.GREEN).append("/").append(ChatColor.AQUA).append(limit)
                    .append(ChatColor.YELLOW).append(" blocks placed.")
                    .toString());
            item.setItemMeta(itemMeta);
            itemList.add(item);
        });

        List<GuiItem> guiItems = itemList.stream()
                .sorted(Comparator.comparing(ItemStack::getType))
                .map(itemStack -> new GuiItem(itemStack, inventoryClickEvent -> inventoryClickEvent.setCancelled(true)))
                .collect(Collectors.toList());

        Gui gui = new Gui(plugin, 6, "Your Limits");

        PaginatedPane page = new PaginatedPane(0, 0, 9, 5);

        page.populateWithGuiItems(guiItems);

        gui.addPane(page);

        gui.show(player);

        return gui;
    }
}
