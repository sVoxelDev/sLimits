package net.silthus.slimits.limits;

import lombok.Getter;
import lombok.Setter;
import net.silthus.slib.config.converter.MaterialMapConverter;
import net.silthus.slib.config.converter.MaterialMapLocationListConverter;
import net.silthus.slib.config.converter.UUIDConverter;
import net.silthus.slib.configlib.annotation.ConfigurationElement;
import net.silthus.slib.configlib.annotation.Convert;
import net.silthus.slib.configlib.annotation.Ignore;
import net.silthus.slimits.Constants;
import net.silthus.slimits.LimitMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@ConfigurationElement
public class PlayerBlockPlacementLimit {

    @Convert(UUIDConverter.class)
    private UUID playerUUID = UUID.randomUUID();
    private String playerName = "UNDEFINED";
    @Ignore
    private Map<String, LimitMode> limitConfigs = new HashMap<>();
    @Ignore
    private Map<Material, Integer> limits = new HashMap<>();
    @Convert(MaterialMapConverter.class)
    private Map<Material, Integer> counts = new HashMap<>();
    @Convert(MaterialMapLocationListConverter.class)
    private Map<Material, List<Location>> blockLocations = new HashMap<>();
    @Ignore
    private Map<Material, Set<String>> blockTypePermissions = new HashMap<>();

    public PlayerBlockPlacementLimit(OfflinePlayer player) {
        this.playerUUID = player.getUniqueId();
        this.playerName = player.getName();
    }

    public PlayerBlockPlacementLimit() {
    }

    public void registerLimitConfig(BlockPlacementLimitConfig config) {
        if (limitConfigs.containsKey(config.getIdentifier())) {
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

        limitConfigs.put(config.getIdentifier(), config.getMode());
        addPermissions(config);
    }

    public void unregisterLimitConfig(BlockPlacementLimitConfig config) {
        LimitMode limitMode = limitConfigs.remove(config.getIdentifier());
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

        removePermissions(config);
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
        addBlockLocation(block);

        return currentCount;
    }

    public int getCount(Material blockType) {
        if (!getCounts().containsKey(blockType)) {
            getCounts().put(blockType, 0);
        }
        return counts.getOrDefault(blockType, 0);
    }

    public boolean hasPlacedBlock(Block block) {

        return getLocations(block.getType()).contains(block.getLocation());
    }

    public List<Location> getLocations(Material material) {
        return blockLocations.getOrDefault(material, new ArrayList<>());
    }

    public int removeBlock(Block block) {

        Material blockType = block.getType();
        int count = getCount(blockType);

        if (removeBlockLocation(block)) {
            count--;
            getCounts().put(blockType, count);
        }

        if (count < 0) {
            count = 0;
            getCounts().remove(blockType);
        }

        return count;
    }

    public boolean isApplicable(Player player, Block block) {

        boolean isLimitedBlock = getLimits().containsKey(block.getType());
        boolean hasPermission = getBlockTypePermissions()
                .getOrDefault(block.getType(), new HashSet<>()).stream()
                .anyMatch(player::hasPermission);
        boolean isExcluded = player.hasPermission(Constants.PERMISSION_EXCLUDE_FROM_LIMITS);

        return (isLimitedBlock && hasPermission) && !isExcluded;
    }

    public boolean hasReachedLimit(Material blockType) {

        Optional<Integer> limit = getLimit(blockType);

        return limit.filter(integer -> getCount(blockType) >= integer).isPresent();
    }

    private void addPermissions(BlockPlacementLimitConfig config) {
        for (Material material : config.getBlocks().keySet()) {
            if (!blockTypePermissions.containsKey(material)) {
                blockTypePermissions.put(material, new HashSet<>());
            }
            blockTypePermissions.get(material).add(config.getPermission());
        }
    }

    private void removePermissions(BlockPlacementLimitConfig config) {
        for (Material material : config.getBlocks().keySet()) {
            if (!blockTypePermissions.containsKey(material)) {
                blockTypePermissions.put(material, new HashSet<>());
            }
            blockTypePermissions.get(material).remove(config.getPermission());
        }
    }

    private void addBlockLocation(Block block) {

        if (!blockLocations.containsKey(block.getType())) {
            blockLocations.put(block.getType(), new ArrayList<>());
        }
        blockLocations.get(block.getType()).add(block.getLocation());
    }

    private boolean removeBlockLocation(Block block) {
        return blockLocations.getOrDefault(block.getType(), new ArrayList<>()).remove(block.getLocation());
    }
}
