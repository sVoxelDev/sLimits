package net.silthus.slimits.ui;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import net.silthus.slimits.LimitsManager;
import net.silthus.slimits.LimitsPlugin;
import net.silthus.slimits.limits.PlayerBlockPlacementLimit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class LimitsGUI {

    private final LimitsPlugin plugin;
    private final LimitsManager limitsManager;

    public LimitsGUI(LimitsPlugin plugin, LimitsManager limitsManager) {
        this.plugin = plugin;
        this.limitsManager = limitsManager;
    }

    public Gui showLimits(Player player) {

        Gui gui = new Gui(plugin, 6, "Your Limits");
        PaginatedPane page = new PaginatedPane(0, 0, 9, 5);

        PlayerBlockPlacementLimit playerLimit = limitsManager.getPlayerLimit(player);
        Map<Material, Integer> limits = playerLimit.getLimits();

        List<GuiItem> itemList = new ArrayList<>();

        limits.forEach((material, limit) -> {
            StringBuilder sb = new StringBuilder();
            ItemStack item = new ItemStack(material);

            ItemMeta itemMeta = item.getItemMeta();
            int count = playerLimit.getCount(material);
            double usage = count * 100.0 / limit * 100.0;

            ChatColor color = ChatColor.GREEN;
            if (usage >= 95) {
                color = ChatColor.RED;
            } else if (usage > 80) {
                color = ChatColor.YELLOW;
            }

            itemMeta.setDisplayName(sb.append(ChatColor.BOLD).append(color).append(material.name()).append(": ")
                    .append(ChatColor.RESET).append(ChatColor.AQUA)
                    .append(count).append(ChatColor.GREEN).append("/").append(ChatColor.AQUA).append(limit)
                    .append(ChatColor.YELLOW).append(" blocks placed.")
                    .toString());
            itemMeta.setLore(Arrays.asList(ChatColor.GRAY + "" + ChatColor.BOLD + "Click: " + ChatColor.RESET + ChatColor.GRAY + "show placed block locations."));
            item.setItemMeta(itemMeta);

            GuiItem guiItem = new GuiItem(item, click -> {
                click.setCancelled(true);

                PaginatedPane locationsPane = new PaginatedPane(0, 0, 9, 5, Pane.Priority.HIGH);
                locationsPane.addPane(0, getBackButton(gui, locationsPane, page));

                ArrayList<ItemStack> locationItems = new ArrayList<>();

                for (Location location : playerLimit.getLocations(material)) {
                    ItemStack locationItem = new ItemStack(material, 1);
                    ItemMeta locationItemMeta = locationItem.getItemMeta();

                    locationItemMeta.setDisplayName(ChatColor.YELLOW + material.name());
                    locationItemMeta.setLore(Arrays.asList(
                            ChatColor.GRAY + "x: " + ChatColor.BOLD + location.getBlockX(),
                            ChatColor.GRAY + "y: " + ChatColor.BOLD + location.getBlockY(),
                            ChatColor.GRAY + "z: " + ChatColor.BOLD + location.getBlockZ(),
                            ChatColor.GRAY + "world: " + ChatColor.BOLD + location.getWorld().getName()
                    ));

                    locationItem.setItemMeta(locationItemMeta);
                    locationItems.add(locationItem);
                }

                List<GuiItem> guiItems = locationItems.stream()
                        .map(itemStack -> new GuiItem(itemStack, inventoryClickEvent -> inventoryClickEvent.setCancelled(true)))
                        .collect(Collectors.toList());

                locationsPane.populateWithGuiItems(guiItems);
                page.setVisible(false);
                locationsPane.setVisible(true);
                gui.addPane(locationsPane);
                gui.update();
            });

            itemList.add(guiItem);
        });

        List<GuiItem> guiItems = itemList.stream()
                .sorted(Comparator.comparing(o -> o.getItem().getType()))
                .collect(Collectors.toList());

        page.populateWithGuiItems(guiItems);

        gui.addPane(page);

        gui.show(player);

        return gui;
    }

    public static StaticPane getBackButton(Gui gui, Pane current, Pane parent) {
        StaticPane back = new StaticPane(0, 5, 1, 1);
        ItemStack itemStack = new ItemStack(Material.BARRIER);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + "Go Back");
        itemStack.setItemMeta(itemMeta);
        back.addItem(new GuiItem(itemStack, click -> {
            click.setCancelled(true);
            current.setVisible(false);
            parent.setVisible(true);
            gui.update();
        }), 0, 0);

        return back;
    }
}
