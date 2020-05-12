package net.silthus.slimits.limits;

import de.exlll.configlib.annotation.ConfigurationElement;
import de.exlll.configlib.annotation.Convert;
import lombok.Getter;
import lombok.Setter;
import net.silthus.slib.config.converter.LocationListConverter;
import net.silthus.slib.config.converter.MaterialMapConverter;
import net.silthus.slib.config.converter.UUIDConverter;
import net.silthus.slimits.LimitMode;
import net.silthus.slimits.config.LimitModeMapConverter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;

import java.util.*;

@Getter
@Setter
@ConfigurationElement
public class PlayerBlockPlacementLimit {

    @Convert(UUIDConverter.class)
    private UUID playerUUID = UUID.randomUUID();
    private String playerName = "UNDEFINED";
    @Convert(LimitModeMapConverter.class)
    private Map<String, LimitMode> limitConfigs = new HashMap<>();
    @Convert(MaterialMapConverter.class)
    private Map<Material, Integer> limits = new HashMap<>();
    @Convert(MaterialMapConverter.class)
    private Map<Material, Integer> counts = new HashMap<>();
    @Convert(LocationListConverter.class)
    private List<Location> locations = new ArrayList<>();

    public PlayerBlockPlacementLimit(OfflinePlayer player) {
        this.playerUUID = player.getUniqueId();
        this.playerName = player.getName();
    }

    public PlayerBlockPlacementLimit() {
    }

    public void registerLimitConfig(String identifier, BlockPlacementLimitConfig config) {
        if (limitConfigs.containsKey(identifier)) {
            return;
        }

        switch (config.getMode()) {
            case ABSOLUTE:
                // override any limits that are set with the absolute values
                limits.putAll(config.getBlocks());
                break;
            case SUBTRACT:
                // we don't want to modify the absolute configs
                if (limitConfigs.containsValue(LimitMode.ABSOLUTE)) return;

                config.getBlocks().forEach((key, value) -> {
                    int newLimit = limits.getOrDefault(key, 0) - value;
                    if (newLimit < 0) newLimit = 0;
                    limits.put(key, newLimit);
                });
                break;
            case ADD:
            default:
                // we don't want to modify the absolute configs
                if (limitConfigs.containsValue(LimitMode.ABSOLUTE)) return;

                config.getBlocks().forEach((key, value) -> limits.put(key, limits.getOrDefault(key, 0) + value));
                break;
        }

        limitConfigs.put(identifier, config.getMode());
    }

    public void unregisterLimitConfig(String identifier, BlockPlacementLimitConfig config) {
        LimitMode limitMode = limitConfigs.remove(identifier);
        if (limitMode == null) return;

        // invert the limit change
        switch (limitMode) {
            case ABSOLUTE:
                limitConfigs.clear();
                limits.clear();
                break;
            case SUBTRACT:
                config.getBlocks().forEach((key, value) -> limits.put(key, limits.getOrDefault(key, 0) + value));
                break;
            default:
            case ADD:
                config.getBlocks().forEach((key, value) -> {
                    int newLimit = limits.getOrDefault(key, 0) - value;
                    if (newLimit < 0) newLimit = 0;
                    limits.put(key, newLimit);
                });
                break;
        }
    }

    public Optional<Integer> getLimit(Material blockType) {
        return Optional.ofNullable(getLimits().get(blockType));
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
