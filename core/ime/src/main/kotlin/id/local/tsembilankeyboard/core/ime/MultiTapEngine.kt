package id.local.tsembilankeyboard.core.ime

import android.os.Handler
import android.os.Looper

class MultiTapEngine(
    private val commitCallback: (String) -> Unit,
    private val composingCallback: (String) -> Unit
) {
    private val timeoutMs = 800L
    private val handler = Handler(Looper.getMainLooper())
    
    private var currentKey: Int = -1
    private var currentIndex: Int = 0
    private var isComposing: Boolean = false
    
    private val keyMap = mapOf(
        1 to listOf(".", ",", "!", "?", "1"),
        2 to listOf("a", "b", "c", "2"),
        3 to listOf("d", "e", "f", "3"),
        4 to listOf("g", "h", "i", "4"),
        5 to listOf("j", "k", "l", "5"),
        6 to listOf("m", "n", "o", "6"),
        7 to listOf("p", "q", "r", "s", "7"),
        8 to listOf("t", "u", "v", "8"),
        9 to listOf("w", "x", "y", "z", "9"),
        0 to listOf(" ", "0")
    )

    private val timeoutRunnable = Runnable {
        commitCurrent()
    }

    fun onKeyPress(keyCode: Int) {
        if (keyCode == currentKey) {
            // cycle
            val chars = keyMap[keyCode] ?: return
            currentIndex = (currentIndex + 1) % chars.size
            updateComposing()
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
            updateComposing()
            resetTimer()
        }
    }

    private fun updateComposing() {
        val chars = keyMap[currentKey] ?: return
        val char = chars[currentIndex]
        composingCallback(char)
    }

    fun commitCurrent() {
        handler.removeCallbacks(timeoutRunnable)
        if (isComposing) {
            val chars = keyMap[currentKey]
            if (chars != null) {
                commitCallback(chars[currentIndex])
            }
            isComposing = false
            currentKey = -1
            currentIndex = 0
        }
    }
    
    fun cancelComposing() {
        handler.removeCallbacks(timeoutRunnable)
        isComposing = false
        currentKey = -1
        currentIndex = 0
    }

    private fun resetTimer() {
        handler.removeCallbacks(timeoutRunnable)
        handler.postDelayed(timeoutRunnable, timeoutMs)
    }
    
    fun isComposing(): Boolean = isComposing
}
