package id.local.tsembilankeyboard

import android.inputmethodservice.InputMethodService
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.PopupWindow
import android.widget.TextView
import id.local.tsembilankeyboard.core.ime.ImeActionResolver
import id.local.tsembilankeyboard.core.ime.InputTypeResolver
import id.local.tsembilankeyboard.core.ime.MultiTapEngine
import id.local.tsembilankeyboard.keyboard.KetikKeyboardFactory
import id.local.tsembilankeyboard.keyboard.KeyboardListener

class TsembilanKeyboardImeService : InputMethodService(), KeyboardListener {

    private lateinit var keyboardView: View
    
    private var previewPopup: PopupWindow? = null
    private var previewTextView: TextView? = null
    private var currentAnchor: View? = null
    
    private val multiTapEngine: MultiTapEngine = MultiTapEngine(
        commitCallback = { text ->
            currentInputConnection?.commitText(text, 1)
            // if mode was Abc and it just committed, auto switch to abc
            KetikKeyboardFactory.updateAbcButtonLabel(keyboardView, modeToString(multiTapEngine.currentMode))
        },
        composingCallback = { text ->
            currentInputConnection?.setComposingText(text, 1)
        },
        previewCallback = { text ->
            showPreview(text, currentAnchor)
        },
        hidePreviewCallback = {
            hidePreview()
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
        KetikKeyboardFactory.updateAbcButtonLabel(keyboardView, modeToString(multiTapEngine.currentMode))
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        multiTapEngine.commitCurrent()
        multiTapEngine.cleanup()
        hidePreview()
    }

    override fun onDestroy() {
        super.onDestroy()
        multiTapEngine.cleanup()
        hidePreview()
    }

    private fun showPreview(text: String, anchor: View?) {
        if (anchor == null) return
        if (InputTypeResolver.isPassword(currentInputEditorInfo)) return

        if (previewPopup == null) {
            val popupView = layoutInflater.inflate(id.local.tsembilankeyboard.keyboard.R.layout.preview_popup, null)
            previewTextView = popupView.findViewById(id.local.tsembilankeyboard.keyboard.R.id.text_preview)
            previewPopup = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            previewPopup?.isTouchable = false
        }
        
        previewTextView?.text = text
        
        val location = IntArray(2)
        anchor.getLocationInWindow(location)
        
        previewPopup?.contentView?.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val popupWidth = previewPopup?.contentView?.measuredWidth ?: 0
        val popupHeight = previewPopup?.contentView?.measuredHeight ?: 0
        
        val x = location[0] + (anchor.width - popupWidth) / 2
        val y = location[1] - popupHeight
        
        if (previewPopup?.isShowing == true) {
            previewPopup?.update(x, y, -1, -1)
        } else {
            previewPopup?.showAtLocation(anchor, Gravity.NO_GRAVITY, x, y)
        }
    }

    private fun hidePreview() {
        previewPopup?.dismiss()
    }

    override fun onNumberKeyClick(number: Int, anchor: View?) {
        currentAnchor = anchor
        if (InputTypeResolver.isNumeric(currentInputEditorInfo)) {
            multiTapEngine.commitCurrent()
            currentInputConnection?.commitText(number.toString(), 1)
        } else {
            multiTapEngine.onKeyPress(number)
        }
    }

    override fun onSpaceClick() {
        multiTapEngine.commitCurrent()
        if (InputTypeResolver.isNumeric(currentInputEditorInfo)) {
            currentInputConnection?.commitText("0", 1)
        } else {
            currentInputConnection?.commitText(" ", 1)
        }
    }

    override fun onSpaceLongClick() {
        multiTapEngine.commitCurrent()
        if (InputTypeResolver.supportsMultiline(currentInputEditorInfo)) {
            currentInputConnection?.commitText("\n", 1)
        } else {
            if (InputTypeResolver.isNumeric(currentInputEditorInfo)) {
                currentInputConnection?.commitText("0", 1)
            } else {
                currentInputConnection?.commitText(" ", 1)
            }
        }
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
        val newMode = multiTapEngine.cycleMode()
        KetikKeyboardFactory.updateAbcButtonLabel(keyboardView, modeToString(newMode))
    }

    private fun modeToString(mode: id.local.tsembilankeyboard.core.ime.InputMode): String {
        return when (mode) {
            id.local.tsembilankeyboard.core.ime.InputMode.LOWERCASE -> "abc"
            id.local.tsembilankeyboard.core.ime.InputMode.CAPITALIZE -> "Abc"
            id.local.tsembilankeyboard.core.ime.InputMode.UPPERCASE -> "ABC"
        }
    }

    override fun onLeftClick() {
        multiTapEngine.commitCurrent()
        currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT))
        currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_LEFT))
    }

    override fun onRightClick() {
        multiTapEngine.commitCurrent()
        currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT))
        currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_RIGHT))
    }
}
