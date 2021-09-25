package net.silthus.slimits;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Optional;

@EqualsAndHashCode(of = {"type", "location"})
public class PlacedBlock {

    @Getter
    private final Material type;
    @Getter
    private final Location location;
    @Setter
    private Player owner;

    public PlacedBlock(Block block) {
        this.type = block.getType();
        this.location = block.getLocation();
    }

    public PlacedBlock(Player player, Block block) {
        this(block);
        this.owner = player;
    }

    public Block toBlock() {
        return location.getBlock();
    }

    public Optional<Player> getOwner() {
        return Optional.ofNullable(owner);
    }

    public boolean isOwner(Player player) {
        return player != null && player == owner;
    }

    public boolean isBlock(Block block) {

        if (block == null)
            return false;

        return type == block.getType() && location.equals(block.getLocation());
    }
}
