package net.silthus.slimits.limits;

import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.block.BlockMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.silthus.slimits.TestBase;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.*;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SuppressWarnings("ALL")
public class PlacedBlockTests extends TestBase {

    @Test
    void create_storesType() {

        PlacedBlock block = new PlacedBlock(new BlockMock(Material.STONE, new Location(server.addSimpleWorld("test"), 1, 2, 3)));

        assertThat(block.getType())
                .isNotNull()
                .isEqualTo(Material.STONE);
    }

    @Test
    void create_storesLocation() {

        WorldMock test = server.addSimpleWorld("test");
        Location location = new Location(test, 10, 20, 30);
        PlacedBlock block = new PlacedBlock(new BlockMock(Material.STONE, location));

        assertThat(block.getLocation())
                .isNotNull()
                .isEqualTo(location);
    }

    @Test
    void create_withUnknownWorld_throws() {

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new PlacedBlock(Material.BEDROCK, new Location(null, 1, 2, 3), null));
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

        WorldMock world = server.addSimpleWorld("test");
        Location location = new Location(world, 10, 20, 30);
        Block b = location.getBlock();
        b.setType(Material.STONE);
        PlacedBlock placedBlock = new PlacedBlock(b);

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
        placedBlock = placedBlock.withOwner(owner);

        assertThat(placedBlock.getOwner())
                .isNotEmpty().get()
                .isEqualTo(owner);

        placedBlock = placedBlock.withOwner(null);
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

        PlacedBlock placedBlock = createPlacedBlock().withOwner(null);

        assertThat(placedBlock.isOwner(player)).isFalse();
    }

    @Test
    void isOwner_nullOwner_and_nullPlayer_returnsFalse() {

        PlacedBlock placedBlock = createPlacedBlock().withOwner(null);

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

    @Test
    void implements_ConfigurationSerializable_serialize() {

        BlockMock block = createBlock(Material.STONE, 1, 2, 3);
        PlacedBlock placedBlock = new PlacedBlock(block, player);

        assertThat(placedBlock.serialize())
                .isNotNull()
                .contains(
                        entry("type", "minecraft:stone"),
                        entry("worldId", block.getWorld().getUID().toString()),
                        entry("world", block.getWorld().getName()),
                        entry("x", 1),
                        entry("y", 2),
                        entry("z", 3),
                        entry("ownerId", player.getUniqueId().toString()),
                        entry("owner", player.getName())
                );
    }

    @Test
    void implements_ConfigurationSerializable_deserialize() {

        WorldMock world = server.addSimpleWorld("test");

        PlacedBlock placedBlock = PlacedBlock.deserialize(Map.of(
                "type", "minecraft:stone",
                "worldId", world.getUID().toString(),
                "world", world.getName(),
                "x", 1,
                "y", 2,
                "z", 3,
                "ownerId", player.getUniqueId().toString(),
                "owner", player.getName()
        ));

        assertThat(placedBlock)
                .isNotNull()
                .extracting(
                        PlacedBlock::getType,
                        PlacedBlock::getLocation
                ).contains(
                        Material.STONE,
                        new Location(world, 1, 2, 3)
                );
        assertThat(placedBlock.getOwner())
                .isNotEmpty()
                .get()
                .isEqualTo(player);
    }

    @Test
    void implements_ConfigurationSerializable_valueOf() {

        WorldMock world = server.addSimpleWorld("foobar");

        PlacedBlock placedBlock = PlacedBlock.valueOf(Map.of(
                "type", "bedrock",
                "worldId", world.getUID().toString(),
                "world", world.getName(),
                "x", 1,
                "y", 2,
                "z", 3,
                "ownerId", player.getUniqueId().toString(),
                "owner", player.getName()
        ));

        assertThat(placedBlock)
                .isNotNull()
                .extracting(
                        PlacedBlock::getType,
                        PlacedBlock::getLocation
                ).contains(
                        Material.BEDROCK,
                        new Location(world, 1, 2, 3)
                );
        assertThat(placedBlock.getOwner())
                .isNotEmpty()
                .get()
                .isEqualTo(player);
    }

    @Test
    void implements_ConfigurationSerialiazble_andIsRegisteredInClass() {

        assertThat(ConfigurationSerialization.getClassByAlias("PlacedBlock"))
                .isNotNull()
                .isAssignableFrom(PlacedBlock.class);

        assertThat(ConfigurationSerialization.getClassByAlias("net.silthus.slimits.limits.PlacedBlock"))
                .isNotNull()
                .isAssignableFrom(PlacedBlock.class);
    }

    @Test
    void serialization_storesPlacedBlocksInConfig() {

        PlacedBlock placedBlock = createPlacedBlock();
        List<PlacedBlock> placedBlocks = List.of(
                placedBlock
        );

        YamlConfiguration config = new YamlConfiguration();
        config.set("placed_blocks", placedBlocks);
        String output = config.saveToString();

        assertThat(output).startsWith("""
                placed_blocks:
                - ==: PlacedBlock
                """.stripIndent());
        assertThat(output)
                .contains("ownerId: " + placedBlock.getOwnerId().toString())
                .contains("worldId: " + placedBlock.getWorldId().toString())
                .contains("world: World")
                .contains("owner: Player0")
                .contains("x: 10")
                .contains("y: 20")
                .contains("z: 30")
                .contains("type: minecraft:stone");
    }

    @Test
    void serialization_loadsFromConfig() throws Exception {

        YamlConfiguration config = new YamlConfiguration();
        config.load(new File("src/test/resources/placed_blocks.yml"));
        List<PlacedBlock> placedBlocks = (List<PlacedBlock>) config.getList("placed_blocks", new ArrayList<>());

        PlayerMock player = new PlayerMock(server, "Silthus", UUID.fromString("4fcdfd06-f620-40fc-827c-4dbf514c904a"));
        server.addPlayer(player);

        assertThat(placedBlocks)
                .isNotNull()
                .hasSize(2)
                .first()
                .extracting(
                        PlacedBlock::getType,
                        PlacedBlock::getX,
                        PlacedBlock::getY,
                        PlacedBlock::getZ,
                        PlacedBlock::getWorld,
                        PlacedBlock::getWorldId,
                        PlacedBlock::getOwner,
                        PlacedBlock::getOwnerId
                ).contains(
                        Material.STONE,
                        1,
                        2,
                        3,
                        "world",
                        UUID.fromString("0e06e315-fdf7-483a-a139-eb2f48be2841"),
                        Optional.of(player),
                        UUID.fromString("4fcdfd06-f620-40fc-827c-4dbf514c904a")
                );
    }

    private PlacedBlock createPlacedBlock() {
        WorldMock world = new WorldMock(Material.STONE, 256);
        server.addWorld(world);
        Location location = new Location(world, 10, 20, 30);
        return new PlacedBlock(location.getBlock(), player);
    }
}
