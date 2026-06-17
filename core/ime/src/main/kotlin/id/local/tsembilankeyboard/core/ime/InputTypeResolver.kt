package id.local.tsembilankeyboard.core.ime

import android.text.InputType
import android.view.inputmethod.EditorInfo

object InputTypeResolver {

    fun isNumeric(editorInfo: EditorInfo?): Boolean {
        val inputType = editorInfo?.inputType ?: return false
        val inputClass = inputType and InputType.TYPE_MASK_CLASS
        return inputClass == InputType.TYPE_CLASS_NUMBER || inputClass == InputType.TYPE_CLASS_PHONE
    }

    fun isPassword(editorInfo: EditorInfo?): Boolean {
        val inputType = editorInfo?.inputType ?: return false
        val inputClass = inputType and InputType.TYPE_MASK_CLASS
        if (inputClass == InputType.TYPE_CLASS_TEXT) {
            val variation = inputType and InputType.TYPE_MASK_VARIATION
            return variation == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                   variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
                   variation == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
        } else if (inputClass == InputType.TYPE_CLASS_NUMBER) {
            val variation = inputType and InputType.TYPE_MASK_VARIATION
            return variation == InputType.TYPE_NUMBER_VARIATION_PASSWORD
        }
        return false
    }

    fun supportsMultiline(editorInfo: EditorInfo?): Boolean {
        val inputType = editorInfo?.inputType ?: return false
        val inputClass = inputType and InputType.TYPE_MASK_CLASS
        if (inputClass == InputType.TYPE_CLASS_TEXT) {
            val flags = inputType and InputType.TYPE_MASK_FLAGS
            return (flags and InputType.TYPE_TEXT_FLAG_MULTI_LINE) != 0
        }
        return false
    }
}
