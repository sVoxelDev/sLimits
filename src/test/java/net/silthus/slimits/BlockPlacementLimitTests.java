package net.silthus.slimits;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.silthus.slimits.testing.TestBase;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BlockPlacementLimitTests extends TestBase {

    private BlockPlacementLimit limit;

    @BeforeEach
    public void setUp() {
        super.setUp();

        limit = new BlockPlacementLimit(Material.STONE, 100);
        server.getPluginManager().registerEvents(limit, plugin);
    }

    @Test
    void newBlockPlaceLimit_isEmpty() {

        assertThat(limit.getCount(server.addPlayer()))
                .isEqualTo(0);
    }

    @Test
    void create_fromLimitConfig() {

        BlockPlacementLimit limit = new BlockPlacementLimit(new BlockPlacementLimitConfig(Material.BEDROCK, 10));
        assertThat(limit)
                .extracting(
                        BlockPlacementLimit::getType,
                        BlockPlacementLimit::getLimit
                ).contains(
                        Material.BEDROCK,
                        10
                );
    }

    @Test
    void blockBreak_shouldNotGoBelowZero() {

        breakBlock(Material.STONE, 10, 20, 30);

        assertThat(limit.getCount(player))
                .isEqualTo(0);
    }

    @Test
    void newLimit_setsBlockType() {

        BlockPlacementLimit limit = new BlockPlacementLimit(Material.BEDROCK, 100);

        assertThat(limit.getType())
                .isEqualTo(Material.BEDROCK);
    }

    @Test
    void blockPlaceEvent_increasesCounter() {

        placeBlock(Material.STONE, 10, 20, 30);

        assertThat(limit.getCount(player))
                .isEqualTo(1);
    }

    @Test
    void blockPlaceEvent_increasesCounter_onlyForGivenMaterial() {

        placeBlock(Material.BEDROCK, 10, 20, 30);

        assertThat(limit.getCount(player))
                .isEqualTo(0);
    }

    @Test
    void blockBreakEvent_decreasesCounter() {

        placeBlock(Material.STONE, 10, 20, 30);

        assertThat(limit.getCount(player))
                .isEqualTo(1);

        breakBlock(Material.STONE, 10, 20, 30);

        assertThat(limit.getCount(player))
                .isEqualTo(0);
    }

    @Test
    void blockPlace_tracksPlacedBlockLocation() {

        placeBlock(Material.STONE, 1, 2, 3);

        assertThat(limit.getPlacedBlocks(player))
                .isNotNull()
                .isNotEmpty()
                .containsOnly(new PlacedBlock(createBlock(Material.STONE, 1, 2, 3)));
        assertThat(limit.getCount(player))
                .isEqualTo(1);
    }

    @Test
    void breakBlock_removesTrackedBlock() {

        placeBlock(Material.STONE, 1, 2, 3);
        breakBlock(Material.STONE, 1, 2, 3);

        assertThat(limit.getPlacedBlocks(player))
                .isNotNull()
                .isEmpty();
    }

    @Test
    void breakBlock_brokenByOtherPlayer_removesBlock() {

        BlockPlaceEvent blockPlaceEvent = createBlockPlaceEvent(createBlock(Material.STONE, 1, 2, 3));
        callEvent(blockPlaceEvent);
        BlockBreakEvent blockBreakEvent = new BlockBreakEvent(createBlock(Material.STONE, 1, 2, 3), server.addPlayer());
        callEvent(blockBreakEvent);

        assertThat(limit.getPlacedBlocks(player))
                .isNotNull()
                .isEmpty();
    }

    @Test
    void breakBlock_notTracked_doesNothing() {

        breakBlock(Material.STONE, 1, 2, 3);

        assertThat(limit.getPlacedBlocks(player))
                .isNotNull()
                .isEmpty();
    }

    @Test
    void breakBlock_twoPlacedBlocks_removesOneBlock() {

        placeBlock(Material.STONE, 1, 2, 3);
        placeBlock(Material.STONE, 10, 20, 30);
        breakBlock(Material.STONE, 1, 2, 3);

        assertThat(limit.getPlacedBlocks(player))
                .isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .containsOnly(new PlacedBlock(createBlock(Material.STONE, 10, 20, 30)));
    }

    @Test
    void breakBlock_reducesCounterOfPlayerWhoBrokeTheBlock() {

        placeBlock(Material.STONE, 1, 2, 3);
        assertThat(limit.getCount(player)).isEqualTo(1);

        PlayerMock player2 = server.addPlayer();
        BlockPlaceEvent blockPlaceEvent2 = createBlockPlaceEvent(createBlock(Material.STONE, 10, 20, 30), player2);
        callEvent(blockPlaceEvent2);

        assertThat(limit.getCount(player2)).isEqualTo(1);

        BlockBreakEvent blockBreakEvent = new BlockBreakEvent(createBlock(Material.STONE, 1, 2, 3), player2);
        callEvent(blockBreakEvent);

        assertThat(limit.getCount(player)).isEqualTo(0);
        assertThat(limit.getCount(player2)).isEqualTo(1);
    }
}
