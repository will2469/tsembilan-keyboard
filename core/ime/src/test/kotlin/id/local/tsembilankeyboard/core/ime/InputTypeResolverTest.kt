package id.local.tsembilankeyboard.core.ime

import android.text.InputType
import android.view.inputmethod.EditorInfo
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InputTypeResolverTest {

    @Test
    fun testIsNumeric() {
        val info = EditorInfo()
        
        info.inputType = InputType.TYPE_CLASS_NUMBER
        assertTrue(InputTypeResolver.isNumeric(info))

        info.inputType = InputType.TYPE_CLASS_PHONE
        assertTrue(InputTypeResolver.isNumeric(info))

        info.inputType = InputType.TYPE_CLASS_TEXT
        assertFalse(InputTypeResolver.isNumeric(info))
    }

    @Test
    fun testIsPassword() {
        val info = EditorInfo()
        
        info.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        assertTrue(InputTypeResolver.isPassword(info))

        info.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        assertTrue(InputTypeResolver.isPassword(info))

        info.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        assertTrue(InputTypeResolver.isPassword(info))

        info.inputType = InputType.TYPE_CLASS_TEXT
        assertFalse(InputTypeResolver.isPassword(info))
    }

    @Test
    fun testSupportsMultiline() {
        val info = EditorInfo()

        info.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
        assertTrue(InputTypeResolver.supportsMultiline(info))

        info.inputType = InputType.TYPE_CLASS_TEXT
        assertFalse(InputTypeResolver.supportsMultiline(info))
    }
}
