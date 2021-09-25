package net.silthus.slimits;

import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.block.BlockMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.silthus.slimits.testing.TestBase;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PlacedBlockTests extends TestBase {

    @Test
    void create_storesType() {

        PlacedBlock block = new PlacedBlock(new BlockMock(Material.STONE));

        assertThat(block.getType())
                .isNotNull()
                .isEqualTo(Material.STONE);
    }

    @Test
    void create_storesLocation() {

        Location location = new Location(new WorldMock(), 10, 20, 30);
        PlacedBlock block = new PlacedBlock(new BlockMock(Material.STONE, location));

        assertThat(block.getLocation())
                .isNotNull()
                .isEqualTo(location);
    }

    @Test
    void equals_sameTypeSameLocation_isEquals() {

        WorldMock world = new WorldMock();
        Location location = new Location(world, 10, 20, 30);
        PlacedBlock block = new PlacedBlock(new BlockMock(Material.STONE, location));
        PlacedBlock block2 = new PlacedBlock(new BlockMock(Material.STONE, new Location(world, 10, 20, 30)));

        assertThat(block).isEqualTo(block2);
    }

    @Test
    void toBlock_returnsValidBlock() {

        Location location = new Location(new WorldMock(Material.STONE, 256), 10, 20, 30);
        PlacedBlock placedBlock = new PlacedBlock(location.getBlock());

        Block block = placedBlock.toBlock();
        assertThat(block)
                .isNotNull()
                .extracting(Block::getType, Block::getLocation)
                .contains(Material.STONE, location);
    }

    @Test
    void tracksBlockOwner() {

        PlacedBlock placedBlock = createPlacedBlock();

        assertThat(placedBlock.getOwner())
                .isNotEmpty().get()
                .isEqualTo(player);
    }

    @Test
    void setOwner_replacesBlockOwner() {

        PlacedBlock placedBlock = createPlacedBlock();

        PlayerMock owner = server.addPlayer();
        placedBlock.setOwner(owner);

        assertThat(placedBlock.getOwner())
                .isNotEmpty().get()
                .isEqualTo(owner);

        placedBlock.setOwner(null);
        assertThat(placedBlock.getOwner())
                .isNotNull()
                .isEmpty();
    }

    @Test
    void isOwner_checksIfPlayerIsOwnerOfTheBlock() {

        PlacedBlock placedBlock = createPlacedBlock();

        assertThat(placedBlock.isOwner(player)).isTrue();
    }

    @Test
    void isOwner_nullOwner_returnsFalse() {

        PlacedBlock placedBlock = createPlacedBlock();
        placedBlock.setOwner(null);

        assertThat(placedBlock.isOwner(player)).isFalse();
    }

    @Test
    void isOwner_nullOwner_and_nullPlayer_returnsFalse() {

        PlacedBlock placedBlock = createPlacedBlock();
        placedBlock.setOwner(null);

        assertThat(placedBlock.isOwner(null)).isFalse();
    }

    @Test
    void isBlock() {

        PlacedBlock placedBlock = new PlacedBlock(createBlock(Material.STONE, 10, 20, 30));

        assertThat(placedBlock.isBlock(createBlock(Material.STONE, 10, 20, 30)))
                .isTrue();
    }

    @Test
    void isBlock_withNullBlock_returnsFalse() {

        assertThat(createPlacedBlock().isBlock(null)).isFalse();
    }

    private PlacedBlock createPlacedBlock() {
        Location location = new Location(new WorldMock(Material.STONE, 256), 10, 20, 30);
        return new PlacedBlock(player, location.getBlock());
    }
}
