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
    fun onSpaceClick() // 0 button single tap
    fun onSpaceLongClick() // 0 button long tap
    fun onDeleteClick()
    fun onSendClick()
    fun onStarClick()
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
                listener.onNumberKeyClick(number, keyView)
            }
        }
        
        val key0 = view.findViewById<View>(R.id.key_0)
        key0?.setOnClickListener {
            listener.onSpaceClick() // Handled via space click in IME
        }
        key0?.setOnLongClickListener {
            listener.onSpaceLongClick()
            true
        }
        
        // Set up long press for delete key
        val deleteKey = view.findViewById<View>(R.id.key_delete)
        setupRepeatTouchListener(deleteKey) {
            listener.onDeleteClick()
        }
        
        view.findViewById<View>(R.id.key_send)?.setOnClickListener {
            listener.onSendClick()
        }
        
        view.findViewById<View>(R.id.key_star)?.setOnClickListener {
            listener.onStarClick()
        }
        
        view.findViewById<View>(R.id.key_abc)?.setOnClickListener {
            listener.onAbcClick()
        }
        
        view.findViewById<View>(R.id.key_left)?.setOnClickListener {
            listener.onLeftClick()
        }

        view.findViewById<View>(R.id.key_right)?.setOnClickListener {
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
}
