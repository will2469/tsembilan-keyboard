package id.local.tsembilankeyboard.core.ime

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class KeyMapperTest {
    @Test
    fun testKey2ReturnsABC() {
        val chars = KeyMapper.getCharacters(2)
        assertEquals(listOf("a", "b", "c"), chars)
    }

    @Test
    fun testKey0ReturnsNull() {
        val chars = KeyMapper.getCharacters(0)
        assertNull(chars)
    }
}
