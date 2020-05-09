package net.silthus.slimits.limits;

import lombok.Data;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;

@Data
public class PlayerBlockPlacementLimit {

    private UUID playerUUID;
    private String playerName;
    private Map<Material, Integer> counts = new HashMap<>();
    private Set<Location> locations = new HashSet<>();

    public PlayerBlockPlacementLimit() {
    }

    public PlayerBlockPlacementLimit(Player player) {
        this.playerUUID = player.getUniqueId();
        this.playerName = player.getName();
    }

    public int addBlock(Block block) {
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
