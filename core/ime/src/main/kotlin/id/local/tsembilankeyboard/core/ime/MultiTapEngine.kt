package id.local.tsembilankeyboard.core.ime

import android.os.Handler
import android.os.Looper

enum class InputMode {
    LOWERCASE, // abc
    CAPITALIZE, // Abc
    UPPERCASE  // ABC
}

class MultiTapEngine(
    private val commitCallback: (String) -> Unit,
    private val composingCallback: (String) -> Unit,
    private val previewCallback: (String) -> Unit,
    private val hidePreviewCallback: () -> Unit
) {
    companion object {
        const val MULTI_TAP_TIMEOUT_MS = 1000L
    }

    private val handler = Handler(Looper.getMainLooper())
    
    private var currentKey: Int = -1
    private var currentIndex: Int = 0
    private var isComposing: Boolean = false
    
    var currentMode: InputMode = InputMode.LOWERCASE
        private set
        
    fun setMode(mode: InputMode) {
        currentMode = mode
    }

    fun cycleMode(): InputMode {
        currentMode = when (currentMode) {
            InputMode.LOWERCASE -> InputMode.CAPITALIZE
            InputMode.CAPITALIZE -> InputMode.UPPERCASE
            InputMode.UPPERCASE -> InputMode.LOWERCASE
        }
        return currentMode
    }

    private val timeoutRunnable = Runnable {
        commitCurrent()
    }

    fun onKeyPress(keyCode: Int) {
        val chars = KeyMapper.getCharacters(keyCode)
        if (chars == null) {
            // Unmapped key (like 0, handled separately, but just in case)
            commitCurrent()
            return
        }

        if (keyCode == currentKey && isComposing) {
            // cycle
            currentIndex = (currentIndex + 1) % chars.size
            updateComposingAndPreview()
            resetTimer()
        } else {
            // commit previous if composing
            if (isComposing) {
                commitCurrent()
            }
            // start new
            currentKey = keyCode
            currentIndex = 0
            isComposing = true
            updateComposingAndPreview()
            resetTimer()
        }
    }

    private fun getActiveChar(): String {
        val chars = KeyMapper.getCharacters(currentKey) ?: return ""
        val char = chars[currentIndex]
        return when (currentMode) {
            InputMode.LOWERCASE -> char.lowercase()
            InputMode.CAPITALIZE, InputMode.UPPERCASE -> char.uppercase()
        }
    }

    private fun getPreviewText(): String {
        val chars = KeyMapper.getCharacters(currentKey) ?: return getActiveChar()
        return chars.mapIndexed { index, s ->
            val charToDisplay = when (currentMode) {
                InputMode.LOWERCASE -> s.lowercase()
                InputMode.CAPITALIZE, InputMode.UPPERCASE -> s.uppercase()
            }
            if (index == currentIndex) "[$charToDisplay]" else charToDisplay
        }.joinToString("  ")
    }

    private fun updateComposingAndPreview() {
        if (!isComposing) return
        val activeChar = getActiveChar()
        if (activeChar.isNotEmpty()) {
            composingCallback(activeChar)
            previewCallback(getPreviewText())
        }
    }

    fun commitCurrent() {
        handler.removeCallbacks(timeoutRunnable)
        if (isComposing) {
            val activeChar = getActiveChar()
            
            // Auto switch Abc to abc after commit
            if (currentMode == InputMode.CAPITALIZE) {
                currentMode = InputMode.LOWERCASE
            }

            if (activeChar.isNotEmpty()) {
                commitCallback(activeChar)
            }
            isComposing = false
            currentKey = -1
            currentIndex = 0
            hidePreviewCallback()
        }
    }
    
    fun cancelComposing() {
        handler.removeCallbacks(timeoutRunnable)
        isComposing = false
        currentKey = -1
        currentIndex = 0
        hidePreviewCallback()
    }

    fun cleanup() {
        handler.removeCallbacksAndMessages(null)
    }

    private fun resetTimer() {
        handler.removeCallbacks(timeoutRunnable)
        handler.postDelayed(timeoutRunnable, MULTI_TAP_TIMEOUT_MS)
    }
    
    fun isComposing(): Boolean = isComposing
}
