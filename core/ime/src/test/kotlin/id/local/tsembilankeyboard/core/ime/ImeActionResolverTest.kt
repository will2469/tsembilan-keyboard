package id.local.tsembilankeyboard.core.ime

import android.view.inputmethod.EditorInfo
import org.junit.Assert.assertEquals
import org.junit.Test

class ImeActionResolverTest {

    @Test
    fun testActionDone() {
        val info = EditorInfo()
        info.imeOptions = EditorInfo.IME_ACTION_DONE
        assertEquals("DONE", ImeActionResolver.getActionLabel(info))
    }

    @Test
    fun testActionGo() {
        val info = EditorInfo()
        info.imeOptions = EditorInfo.IME_ACTION_GO
        assertEquals("GO", ImeActionResolver.getActionLabel(info))
    }

    @Test
    fun testActionSend() {
        val info = EditorInfo()
        info.imeOptions = EditorInfo.IME_ACTION_SEND
        assertEquals("KIRIM", ImeActionResolver.getActionLabel(info))
    }

    @Test
    fun testActionNone() {
        val info = EditorInfo()
        info.imeOptions = EditorInfo.IME_ACTION_NONE
        assertEquals("KIRIM", ImeActionResolver.getActionLabel(info))
    }
}
