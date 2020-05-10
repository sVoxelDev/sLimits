package net.silthus.slimits.limits;

import de.exlll.configlib.annotation.Convert;
import de.exlll.configlib.annotation.ElementType;
import de.exlll.configlib.annotation.NoConvert;
import de.exlll.configlib.configs.yaml.BukkitYamlConfiguration;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.silthus.slib.config.converter.LocationListConverter;
import net.silthus.slib.config.converter.MaterialMapConverter;
import net.silthus.slib.config.converter.UUIDConverter;
import net.silthus.slimits.LimitsPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.nio.file.Path;
import java.util.*;

@Getter
@Setter
@EqualsAndHashCode(of = {"playerUUID", "counts", "locations"}, callSuper = true)
public class PlayerBlockPlacementLimit extends BukkitYamlConfiguration {

    public static PlayerBlockPlacementLimit create(Player player, String identifier) {

        PlayerBlockPlacementLimit placementLimit = new PlayerBlockPlacementLimit(player, identifier);
        placementLimit.loadAndSave();
        return placementLimit;
    }

    @Convert(UUIDConverter.class)
    private UUID playerUUID;
    private String playerName;
    @Convert(MaterialMapConverter.class)
    private Map<Material, Integer> counts = new HashMap<>();
    @Convert(LocationListConverter.class)
    private List<Location> locations = new ArrayList<>();

    private PlayerBlockPlacementLimit(Player player, String identifier) {
        super(Path.of(LimitsPlugin.PLUGIN_PATH, "storage", identifier + "_" + player.getUniqueId().toString() + ".yaml"));
        this.playerUUID = player.getUniqueId();
        this.playerName = player.getName();
    }

    public int addBlock(Block block) {
        if (hasPlacedBlock(block)) {
            return getCount(block.getType());
        }

        int currentCount = getCount(block.getType());
        currentCount++;

        counts.put(block.getType(), currentCount);
        locations.add(block.getLocation());

        return currentCount;
    }

    public int getCount(Material blockType) {
        if (!getCounts().containsKey(blockType)) {
            getCounts().put(blockType, 0);
        }
        return counts.getOrDefault(blockType, 0);
    }

    public boolean hasPlacedBlock(Block block) {

        return getLocations().contains(block.getLocation());
    }

    public int removeBlock(Block block) {

        Material blockType = block.getType();
        int count = getCount(blockType);

        if (getLocations().remove(block.getLocation())) {
            count--;
            getCounts().put(blockType, count);
        }

        if (count < 0) {
            count = 0;
            getCounts().remove(blockType);
        }

        return count;
    }
}
