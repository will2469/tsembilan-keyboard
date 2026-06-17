package id.local.tsembilankeyboard.core.ime

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MultiTapEngineTest {

    private lateinit var engine: MultiTapEngine
    private var committedText: String = ""
    private var composingText: String = ""
    private var previewText: String = ""
    private var previewActiveStart: Int = 0
    private var previewActiveEnd: Int = 0
    private var isPreviewHidden: Boolean = false

    @Before
    fun setup() {
        committedText = ""
        composingText = ""
        previewText = ""
        previewActiveStart = 0
        previewActiveEnd = 0
        isPreviewHidden = false

        engine = MultiTapEngine(
            commitCallback = { committedText = it },
            composingCallback = { composingText = it },
            previewCallback = {
                previewText = it.text
                previewActiveStart = it.activeStart
                previewActiveEnd = it.activeEnd
            },
            hidePreviewCallback = { isPreviewHidden = true }
        )
    }

    @Test
    fun testCycleLowercase() {
        engine.setMode(InputMode.LOWERCASE)

        // First tap
        engine.onKeyPress(2)
        assertTrue(engine.isComposing())
        assertEquals("a", composingText)
        assertEquals("a  b  c", previewText)
        assertEquals(0, previewActiveStart)
        assertEquals(1, previewActiveEnd)

        // Second tap
        engine.onKeyPress(2)
        assertEquals("b", composingText)
        assertEquals("a  b  c", previewText)
        assertEquals(3, previewActiveStart)
        assertEquals(4, previewActiveEnd)

        // Commit manually
        engine.commitCurrent()
        assertEquals("b", committedText)
        assertFalse(engine.isComposing())
        assertTrue(isPreviewHidden)
    }

    @Test
    fun testCycleCapitalize() {
        engine.setMode(InputMode.CAPITALIZE)

        engine.onKeyPress(3)
        assertEquals("D", composingText)

        engine.onKeyPress(3)
        assertEquals("E", composingText)

        engine.commitCurrent()
        assertEquals("E", committedText)

        // Mode should reset to lowercase after commit
        assertEquals(InputMode.LOWERCASE, engine.currentMode)
    }

    @Test
    fun testCycleUppercase() {
        engine.setMode(InputMode.UPPERCASE)

        engine.onKeyPress(4)
        assertEquals("G", composingText)

        engine.commitCurrent()
        assertEquals("G", committedText)

        // Mode should stay uppercase
        assertEquals(InputMode.UPPERCASE, engine.currentMode)
    }

    @Test
    fun testDifferentKeyPressCommitsPrevious() {
        engine.onKeyPress(2) // 'a'
        assertEquals("a", composingText)

        engine.onKeyPress(3) // commits 'a', starts 'd'
        assertEquals("a", committedText)
        assertEquals("d", composingText)
    }
}
