package id.local.tsembilankeyboard.keyboard

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.TextView

interface KeyboardListener {
    fun onNumberKeyClick(number: Int, anchor: View?)
    fun onNumberKeyLongClick(number: Int, anchor: View?)
    fun onSpaceClick() // 0 button single tap
    fun onSpaceLongClick(anchor: View?) // 0 button long tap
    fun onDeleteClick()
    fun onSendClick()
    fun onStarClick(anchor: View?)
    fun onAbcClick()
    fun onLeftClick()
    fun onRightClick()
}

object KetikKeyboardFactory {
    fun createKeyboardView(context: Context, listener: KeyboardListener): View {
        val view = LayoutInflater.from(context).inflate(R.layout.keyboard_view, null)
        
        // Map number keys 1-9
        val numberKeys = mapOf(
            R.id.key_1 to 1,
            R.id.key_2 to 2,
            R.id.key_3 to 3,
            R.id.key_4 to 4,
            R.id.key_5 to 5,
            R.id.key_6 to 6,
            R.id.key_7 to 7,
            R.id.key_8 to 8,
            R.id.key_9 to 9
        )
        
        for ((id, number) in numberKeys) {
            val keyView = view.findViewById<View>(id)
            keyView?.setOnClickListener {
                it.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
                listener.onNumberKeyClick(number, keyView)
            }
            keyView?.setOnLongClickListener {
                it.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS)
                listener.onNumberKeyLongClick(number, keyView)
                true
            }
        }
        
        val key0 = view.findViewById<View>(R.id.key_0)
        key0?.setOnClickListener {
            it.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
            listener.onSpaceClick() // Handled via space click in IME
        }
        key0?.setOnLongClickListener {
            it.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS)
            listener.onSpaceLongClick(key0)
            true
        }
        
        // Set up long press for delete key
        val deleteKey = view.findViewById<View>(R.id.key_delete)
        setupRepeatTouchListener(deleteKey) {
            listener.onDeleteClick()
        }
        
        view.findViewById<View>(R.id.key_send)?.setOnClickListener {
            it.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
            listener.onSendClick()
        }
        
        val keyStar = view.findViewById<View>(R.id.key_star)
        keyStar?.setOnClickListener {
            it.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
            listener.onStarClick(keyStar)
        }
        
        view.findViewById<View>(R.id.key_abc)?.setOnClickListener {
            it.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
            listener.onAbcClick()
        }
        
        view.findViewById<View>(R.id.key_left)?.setOnClickListener {
            it.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
            listener.onLeftClick()
        }

        view.findViewById<View>(R.id.key_right)?.setOnClickListener {
            it.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
            listener.onRightClick()
        }
        
        return view
    }
    
    private fun setupRepeatTouchListener(view: View?, action: () -> Unit) {
        if (view == null) return
        val handler = Handler(Looper.getMainLooper())
        var isPressed = false
        
        val repeatRunnable = object : Runnable {
            override fun run() {
                if (isPressed) {
                    action()
                    handler.postDelayed(this, 100)
                }
            }
        }
        
        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
                    isPressed = true
                    v.isPressed = true
                    action()
                    handler.postDelayed(repeatRunnable, 500)
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    isPressed = false
                    v.isPressed = false
                    handler.removeCallbacks(repeatRunnable)
                    true
                }
                else -> false
            }
        }
    }
    
    fun updateSendButtonLabel(view: View, label: String) {
        view.findViewById<TextView>(R.id.text_send)?.text = label
    }

    fun updateAbcButtonLabel(view: View, label: String) {
        val abcContainer = view.findViewById<View>(R.id.key_abc)
        if (abcContainer is android.view.ViewGroup) {
            for (i in 0 until abcContainer.childCount) {
                val child = abcContainer.getChildAt(i)
                if (child is TextView) {
                    child.text = label
                    break
                }
            }
        }
    }

    fun updateLetterKeysCase(view: View, isUpperCase: Boolean) {
        val keys = listOf(R.id.key_2, R.id.key_3, R.id.key_4, R.id.key_5, R.id.key_6, R.id.key_7, R.id.key_8, R.id.key_9)
        for (id in keys) {
            val keyContainer = view.findViewById<View>(id)
            if (keyContainer is android.view.ViewGroup && keyContainer.childCount > 1) {
                val child = keyContainer.getChildAt(1)
                if (child is TextView) {
                    child.text = if (isUpperCase) child.text.toString().uppercase() else child.text.toString().lowercase()
                }
            }
        }
    }
}
