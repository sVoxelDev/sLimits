package net.silthus.slimits.config;

import net.silthus.slimits.TestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

class StorageConfigTest extends TestBase {

    @Test
    void create() {

        StorageConfig config = new StorageConfig();
        assertThat(config)
                .extracting(StorageConfig::getBlockPlacement)
                .isEqualTo("storage/block_placement/");
    }

    @Test
    void prepareAndGetStoragePath_returnsPathRelativeToPluginDir() {

        StorageConfig config = new StorageConfig();

        File path = config.getStoragePath(plugin);
        assertThat(path)
                .isNotNull()
                .isDirectory()
                .exists()
                .isEmptyDirectory()
                .isAbsolute();
        assertThat(path.getAbsolutePath())
                .contains(plugin.getDataFolder().getAbsolutePath());
    }

    @Test
    void prepareAndGetStoragePath_withAbsolutePath_returnsPathDirectly(@TempDir File temp) {

        StorageConfig config = new StorageConfig();
        config.setBlockPlacement(temp.getAbsolutePath());

        File path = config.getStoragePath(plugin);

        assertThat(path)
                .isNotNull()
                .isDirectory()
                .exists()
                .isEmptyDirectory()
                .isAbsolute();
        assertThat(path.getAbsolutePath())
                .doesNotContain(plugin.getDataFolder().getAbsolutePath());
    }
}