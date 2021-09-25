package net.silthus.slimits;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LimitTypeTest {

    @Test
    void fromString() {

        LimitType type = LimitType.fromString("block_Placement");

        assertThat(type)
                .isNotNull()
                .isEqualTo(LimitType.BLOCK_PLACEMENT);
    }

    @Test
    void fromString_unknownType_returnsNull() {

        assertThat(LimitType.fromString("foobar"))
                .isNull();
    }

    @Test
    void configKey_matches() {

        assertThat(LimitType.BLOCK_PLACEMENT.getConfigKey())
                .isEqualTo("block_placement");
    }
}