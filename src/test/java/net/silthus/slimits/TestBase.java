package net.silthus.slimits;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.block.BlockMock;
import be.seeseemelk.mockbukkit.block.state.BlockStateMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.silthus.slimits.SLimitsPlugin;
import net.silthus.slimits.limits.BlockPlacementLimit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.mockito.Mockito.spy;

public class TestBase {

    protected ServerMock server;
    protected PlayerMock player;
    protected SLimitsPlugin plugin;
    protected File storageDir;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.loadWith(SLimitsPlugin.class, new File("build/resources/test/plugin.yml"));
        plugin.setLimitsService(spy(plugin.getLimitsService()));
        storageDir = new File(plugin.getDataFolder(), plugin.getLimitsConfig().getStorage().getBlockPlacement());

        player = server.addPlayer();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    protected void callEvent(Event event) {
        server.getPluginManager().callEvent(event);
    }

    protected BlockMock placeBlock(Material material, int x, int y, int z) {
        BlockMock block = createBlock(material, x, y, z);
        BlockPlaceEvent blockPlaceEvent = createBlockPlaceEvent(block, player);
        callEvent(blockPlaceEvent);
        return block;
    }

    protected void placeBlocks(Material material, int amount) {
        for (int i = 0; i < amount; i++) {
            placeBlock(material, i, i, i);
        }
    }

    protected BlockMock breakBlock(Material material, int x, int y, int z) {
        BlockMock block = createBlock(material, x, y, z);
        BlockBreakEvent blockBreakEvent = createBlockBreakEvent(block);
        callEvent(blockBreakEvent);
        return block;
    }

    protected BlockMock createBlock(Material material, int x, int y, int z) {
        return new BlockMock(material, new Location(server.getWorld("world"), x, y, z));
    }

    protected BlockPlaceEvent createBlockPlaceEvent(BlockMock block) {
        return createBlockPlaceEvent(block, player);
    }

    protected BlockPlaceEvent createBlockPlaceEvent(BlockMock block, Player player) {
        return new BlockPlaceEvent(block, new BlockStateMock(Material.AIR), new BlockMock(Material.STONE), new ItemStack(Material.AIR), player, true, EquipmentSlot.HAND);
    }

    protected BlockBreakEvent createBlockBreakEvent(BlockMock block) {
        return new BlockBreakEvent(block, player);
    }

    protected void loadConfiguredLimits() {

        plugin.getLimitsService().loadLimits(plugin.getLimitsConfig());
        for (BlockPlacementLimit limit : plugin.getLimitsService().getBlockPlacementLimits()) {
            player.addAttachment(plugin, limit.getPermission(), true);
        }
    }
}
