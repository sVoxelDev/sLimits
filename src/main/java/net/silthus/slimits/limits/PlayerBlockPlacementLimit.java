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
        int currentCount = counts.getOrDefault(block.getType(), 0);
        currentCount++;

        counts.put(block.getType(), currentCount);
        locations.add(block.getLocation());

        return currentCount;
    }

    public int getCount(Material blockType) {
        return counts.getOrDefault(blockType, 0);
    }
}
