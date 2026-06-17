package id.local.tsembilankeyboard.core.ime

import android.view.inputmethod.EditorInfo

object ImeActionResolver {
    fun getActionLabel(editorInfo: EditorInfo?): String {
        if (editorInfo == null) return "KIRIM"
        
        return when (editorInfo.imeOptions and EditorInfo.IME_MASK_ACTION) {
            EditorInfo.IME_ACTION_GO -> "PERGI"
            EditorInfo.IME_ACTION_NEXT -> "LANJUT"
            EditorInfo.IME_ACTION_SEARCH -> "CARI"
            EditorInfo.IME_ACTION_SEND -> "KIRIM"
            EditorInfo.IME_ACTION_DONE -> "TUTUP"
            else -> "KIRIM"
        }
    }
}
