package net.silthus.slimits.limits;

import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Value
@SerializableAs("PlacedBlock")
@EqualsAndHashCode(of = {"type", "worldId", "x", "y", "z"})
public class PlacedBlock implements ConfigurationSerializable {

    public static PlacedBlock deserialize(Map<String, Object> map) {
        return new PlacedBlock(map);
    }

    public static PlacedBlock valueOf(Map<String, Object> map) {
        return new PlacedBlock(map);
    }

    Material type;
    UUID worldId;
    String world;
    int x;
    int y;

    int z;
    UUID ownerId;

    String owner;

    public PlacedBlock(@NonNull Material material, @NonNull Location location, OfflinePlayer owner) {

        this.type = material;

        if (location.getWorld() == null)
            throw new IllegalArgumentException("World must not be null!");

        this.worldId = location.getWorld().getUID();
        this.world = location.getWorld().getName();
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();

        if (owner != null) {
            this.ownerId = owner.getUniqueId();
            this.owner = owner.getName();
        } else {
            this.ownerId = null;
            this.owner = null;
        }
    }

    public PlacedBlock(Map<String, Object> map) {

        this.type = Material.matchMaterial((String) map.get("type"));

        this.worldId = UUID.fromString((String) map.get("worldId"));
        this.world = (String) map.get("world");
        this.x = (int) map.get("x");
        this.y = (int) map.get("y");
        this.z = (int) map.get("z");

        this.ownerId = UUID.fromString((String) map.get("ownerId"));
        this.owner = (String) map.get("owner");
    }

    public PlacedBlock(Block block) {
        this(block.getType(), block.getLocation(), null);
    }

    public PlacedBlock(Block block, OfflinePlayer player) {
        this(block.getType(), block.getLocation(), player);
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(worldId), x, y, z);
    }

    public Block toBlock() {
        return getLocation().getBlock();
    }

    public PlacedBlock withOwner(OfflinePlayer player) {
        return new PlacedBlock(getType(), getLocation(), player);
    }

    public Optional<Player> getOwner() {

        if (getOwnerId() == null)
            return Optional.empty();

        return Optional.ofNullable(Bukkit.getPlayer(getOwnerId()));
    }

    public boolean isOwner(Player player) {
        return player != null && getOwnerId() == player.getUniqueId();
    }

    public boolean isBlock(Block block) {

        if (block == null)
            return false;

        return type == block.getType() && getLocation().equals(block.getLocation());
    }

    @Override
    @NonNull
    public Map<String, Object> serialize() {
        return Map.of(
                "type", getType().getKey().toString(),
                "worldId", getWorldId().toString(),
                "world", getWorld(),
                "x", getX(),
                "y", getY(),
                "z", getZ(),
                "ownerId", ownerId == null ? "" : ownerId.toString(),
                "owner", owner == null ? "" : owner
        );
    }
}
