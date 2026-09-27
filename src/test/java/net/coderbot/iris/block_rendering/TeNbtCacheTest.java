package net.coderbot.iris.block_rendering;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.coderbot.iris.block_rendering.BlockRenderingSettings.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TeNbtCacheTest {

    @BeforeEach
    void clearCache() {
        clearTeNbtCache();
    }

    @Test
    void distinctFullHeightPositionsKeepTheirIds() {
        int[] heights = {Integer.MIN_VALUE, -4097, -4096, -1, 0, 4095, 4096, Integer.MAX_VALUE};
        for (int i = 0; i < heights.length; i++) {
            cacheTeNbtId(30_000_000, heights[i], -30_000_000, i, 10);
        }
        for (int i = 0; i < heights.length; i++) {
            assertEquals(i, getCachedTeNbtId(30_000_000, heights[i], -30_000_000, 10));
        }
        assertEquals(CACHE_MISS, getCachedTeNbtId(29_999_999, 0, -30_000_000, 10));
        assertEquals(CACHE_MISS, getCachedTeNbtId(30_000_000, 0, -29_999_999, 10));
    }

    @Test
    void invalidationOnlyRemovesTheExactPosition() {
        cacheTeNbtId(-1, -1, -1, 11, 0);
        cacheTeNbtId(-1, 4095, -1, 12, 0);
        invalidateTeNbtCache(-1, -1, -1);
        assertEquals(CACHE_MISS, getCachedTeNbtId(-1, -1, -1, 0));
        assertEquals(12, getCachedTeNbtId(-1, 4095, -1, 0));
    }

    @Test
    void expiryAndMissingShaderIdAreUnchanged() {
        cacheTeNbtId(0, -32, 0, -1, 10);
        assertEquals(-1, getCachedTeNbtId(0, -32, 0, 29));
        assertEquals(CACHE_MISS, getCachedTeNbtId(0, -32, 0, 30));
        clearTeNbtCache();
        assertEquals(CACHE_MISS, getCachedTeNbtId(0, -32, 0, 10));
    }

    @Test
    void cacheStillHasItsGlobalSizeLimit() {
        for (int y = 0; y <= 8192; y++) {
            cacheTeNbtId(0, y, 0, y, 0);
        }
        assertEquals(CACHE_MISS, getCachedTeNbtId(0, 0, 0, 0));
        assertEquals(1, getCachedTeNbtId(0, 1, 0, 0));
        assertEquals(8192, getCachedTeNbtId(0, 8192, 0, 0));
    }
}
