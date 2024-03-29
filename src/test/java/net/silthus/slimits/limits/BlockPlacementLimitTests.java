package net.silthus.slimits.limits;

import be.seeseemelk.mockbukkit.block.BlockMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.silthus.slimits.SLimitsPlugin;
import net.silthus.slimits.config.BlockPlacementLimitConfig;
import net.silthus.slimits.TestBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class BlockPlacementLimitTests extends TestBase {

    private BlockPlacementLimit limit;
    private PermissionAttachment permission;

    @BeforeEach
    public void setUp() {
        super.setUp();

        limit = new BlockPlacementLimit(Material.STONE, 100, "test");
        server.getPluginManager().registerEvents(limit, plugin);

        permission = player.addAttachment(plugin, limit.getPermission(), true);
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
                        BlockPlacementLimit::getLimit,
                        BlockPlacementLimit::getKey
                ).contains(
                        Material.BEDROCK,
                        10,
                        "bedrock-10"
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

        assertThat(player.hasPermission(limit.getPermission())).isTrue();
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
        player2.addAttachment(plugin, limit.getPermission(), true);
        BlockPlaceEvent blockPlaceEvent2 = createBlockPlaceEvent(createBlock(Material.STONE, 10, 20, 30), player2);
        callEvent(blockPlaceEvent2);

        assertThat(limit.getCount(player2)).isEqualTo(1);

        BlockBreakEvent blockBreakEvent = new BlockBreakEvent(createBlock(Material.STONE, 1, 2, 3), player2);
        callEvent(blockBreakEvent);

        assertThat(limit.getCount(player)).isEqualTo(0);
        assertThat(limit.getCount(player2)).isEqualTo(1);
    }

    @Test
    void placeBlock_onlyTracksBlocks_ifPlayerHasPermission() {

        player.removeAttachment(permission);
        assertThat(player.hasPermission(limit.getPermission())).isFalse();

        placeBlocks(Material.STONE, 1);

        assertThat(limit.getPlacedBlocks(player)).isEmpty();
    }

    @Test
    void save_storesPlacedBlocks_toDisk(@TempDir File temp) throws IOException {

        placeBlocks(Material.STONE, 2);
        File storageFile = new File(temp, limit.getKey() + ".yml");

        limit.save(temp);

        assertThat(storageFile.exists()).isTrue();
        String content = Files.readString(storageFile.toPath());

        BlockMock block = placeBlock(Material.STONE, 1, 2, 3);
        assertThat(content)
                .startsWith("placed_blocks:")
                .contains("worldId: " + block.getWorld().getUID())
                .contains("ownerId: " + player.getUniqueId())
                .contains("type: minecraft:stone");
    }

    @Test
    void load_loadsPlacedBlocks_fromConfigStore() throws IOException, InvalidConfigurationException {

        PlayerMock player = new PlayerMock(server, "Silthus", UUID.fromString("4fcdfd06-f620-40fc-827c-4dbf514c904a"));
        server.addPlayer(player);

        YamlConfiguration config = getPlacedBlocksFileStore();

        limit.load(config);

        assertThat(limit.getPlacedBlocks())
                .hasSize(1)
                .first()
                .extracting(
                        PlacedBlock::getType,
                        PlacedBlock::getOwner,
                        PlacedBlock::getX,
                        PlacedBlock::getY,
                        PlacedBlock::getZ,
                        PlacedBlock::getWorld
                ).contains(
                        Material.STONE,
                        Optional.of(player),
                        1,
                        2,
                        3,
                        "world"
                );
    }

    @Test
    void load_clearsExistingBlocksBeforeLoading() throws IOException, InvalidConfigurationException {

        placeBlocks(Material.STONE, 10);
        assertThat(limit.getPlacedBlocks())
                .hasSize(10);

        YamlConfiguration config = getPlacedBlocksFileStore();

        limit.load(config);

        assertThat(limit.getPlacedBlocks())
                .hasSize(1);
    }

    @Test
    void load_filtersBlocks_thatDoNotMatch_type() throws IOException, InvalidConfigurationException {

        YamlConfiguration config = getPlacedBlocksFileStore();

        limit.load(config);

        assertThat(limit.getPlacedBlocks())
                .hasSize(1)
                .allMatch(placedBlock -> placedBlock.getType() == Material.STONE);
    }

    @Test
    void blockPlace_isCancelled_ifLimitIsReached() {

        for (int i = 0; i < 100; i++) {
            limit.getPlacedBlocks().add(new PlacedBlock(createBlock(Material.STONE, i, i, i), player));
        }
        BlockPlaceEvent blockPlaceEvent = createBlockPlaceEvent(createBlock(Material.STONE, -1, -2, -3));
        callEvent(blockPlaceEvent);

        assertThat(blockPlaceEvent.isCancelled()).isTrue();
        assertThat(player.nextMessage())
                .isEqualTo(ChatColor.RED + "You reached your limit for placing stone: 100/100.");
    }

    @Test
    void blockPlace_sendsMessageIfLimitIsIncreased() {

        placeBlock(Material.STONE, 1, 2, 3);

        assertThat(player.nextMessage())
                .isEqualTo(ChatColor.GRAY + "Your limit for placing stone "
                        + ChatColor.RED + "increased" + ChatColor.GRAY + ": 1/100.");
    }

    @Test
    void blockBreak_sendsMessageIfLimitIsDecreased() {

        limit.getPlacedBlocks().add(new PlacedBlock(createBlock(Material.STONE, 1, 2, 3), player));

        breakBlock(Material.STONE, 1, 2, 3);

        assertThat(player.nextMessage())
                .isEqualTo(ChatColor.GRAY + "Your limit for placing stone "
                        + ChatColor.GREEN + "decreased" + ChatColor.GRAY + ": 0/100.");
    }

    @Test
    void hasPermission_checksThePlayersPermission() {

        assertThat(limit.hasPermission(player)).isTrue();
    }

    @Test
    void hasPermission_withNullPlayer_returnsFalse() {

        assertThat(limit.hasPermission(null)).isFalse();
    }

    @Test
    void hasPermission_withDifferentPlayer_returnsFalse() {

        assertThat(limit.hasPermission(server.addPlayer())).isFalse();
    }

    @Test
    void player_withIgnorePermission_isIgnoredFromLimits() {

        player.addAttachment(plugin, SLimitsPlugin.PERMISSION_IGNORE_LIMITS, true);

        placeBlocks(Material.STONE, 10);

        assertThat(limit.getCount(player)).isZero();
    }

    @Test
    void blockBreak_deniesBreakingLimitedBlocksOfOtherPlayer_ifConfigured() {

        HandlerList.unregisterAll(limit);

        BlockPlacementLimitConfig config = new BlockPlacementLimitConfig(Material.STONE, 10, limit.getPermission());
        config.setDenyBreakingByOthers(true);
        limit = new BlockPlacementLimit(config);
        Bukkit.getPluginManager().registerEvents(limit, plugin);

        placeBlock(Material.STONE, 1, 2, 3);
        assertThat(limit.getCount(player)).isOne();

        PlayerMock otherPlayer = server.addPlayer();
        BlockBreakEvent blockBreakEvent = createBlockBreakEvent(createBlock(Material.STONE, 1, 2, 3), otherPlayer);

        callEvent(blockBreakEvent);
        assertThat(blockBreakEvent.isCancelled()).isTrue();
        assertThat(otherPlayer.nextMessage())
                .contains(ChatColor.RED + "This is a limited block placed by " + ChatColor.AQUA + player.getName()
                        + ChatColor.RED + ". You cannot break it.");
        assertThat(limit.getCount(player)).isOne();
    }

    @Test
    void multipleLimitsOfTheSameType_ignoresLowerLimits() {

        BlockPlacementLimit limit = new BlockPlacementLimit(Material.STONE, 150, "foo");
        player.addAttachment(plugin, limit.getPermission(), true);
        plugin.getLimitsService().registerAndLoadLimit(limit);

        placeBlocks(Material.STONE, 120);

        assertThat(limit.getCount(player)).isEqualTo(120);
    }

    private YamlConfiguration getPlacedBlocksFileStore() throws IOException, InvalidConfigurationException {

        File blockStore = new File("src/test/resources", "placed_blocks.yml");
        YamlConfiguration config = new YamlConfiguration();
        config.load(blockStore);

        return config;
    }
}
