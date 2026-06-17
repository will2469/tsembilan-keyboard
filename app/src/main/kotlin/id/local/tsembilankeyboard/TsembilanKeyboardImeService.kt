package id.local.tsembilankeyboard

import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import id.local.tsembilankeyboard.core.ime.ImeActionResolver
import id.local.tsembilankeyboard.core.ime.MultiTapEngine
import id.local.tsembilankeyboard.keyboard.KetikKeyboardFactory
import id.local.tsembilankeyboard.keyboard.KeyboardListener

class TsembilanKeyboardImeService : InputMethodService(), KeyboardListener {

    private lateinit var keyboardView: View
    
    private val multiTapEngine = MultiTapEngine(
        commitCallback = { text ->
            currentInputConnection?.commitText(text, 1)
        },
        composingCallback = { text ->
            currentInputConnection?.setComposingText(text, 1)
        }
    )

    override fun onCreateInputView(): View {
        keyboardView = KetikKeyboardFactory.createKeyboardView(this, this)
        return keyboardView
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        
        multiTapEngine.cancelComposing()
        
        val actionLabel = ImeActionResolver.getActionLabel(info)
        KetikKeyboardFactory.updateSendButtonLabel(keyboardView, actionLabel)
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        multiTapEngine.commitCurrent()
    }

    override fun onNumberKeyClick(number: Int) {
        multiTapEngine.onKeyPress(number)
    }

    override fun onSpaceClick() {
        // Not used, 0 is routed to onNumberKeyClick(0)
    }

    override fun onDeleteClick() {
        if (multiTapEngine.isComposing()) {
            multiTapEngine.cancelComposing()
            currentInputConnection?.commitText("", 1)
        } else {
            val textBeforeCursor = currentInputConnection?.getTextBeforeCursor(1, 0)
            if (textBeforeCursor?.isNotEmpty() == true) {
                currentInputConnection?.deleteSurroundingText(1, 0)
            } else {
                // If there's no text, we can try to send backspace key event
                currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
            }
        }
    }

    override fun onSendClick() {
        multiTapEngine.commitCurrent()
        
        val ic = currentInputConnection ?: return
        val editorInfo = currentInputEditorInfo ?: return
        
        val action = editorInfo.imeOptions and EditorInfo.IME_MASK_ACTION
        if (action == EditorInfo.IME_ACTION_NONE || action == EditorInfo.IME_ACTION_UNSPECIFIED) {
            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
        } else {
            ic.performEditorAction(action)
        }
    }

    override fun onStarClick() {
        multiTapEngine.commitCurrent()
        currentInputConnection?.commitText("*", 1)
    }

    override fun onAbcClick() {
        multiTapEngine.commitCurrent()
        // Feature for next milestone: switch input mode
    }

    override fun onLeftClick() {
        multiTapEngine.commitCurrent()
        currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT))
        currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_LEFT))
    }
}
