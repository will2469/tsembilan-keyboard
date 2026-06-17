package id.local.tsembilankeyboard.keyboard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView

interface KeyboardListener {
    fun onNumberKeyClick(number: Int)
    fun onSpaceClick() // 0 button
    fun onDeleteClick()
    fun onSendClick()
    fun onStarClick()
    fun onAbcClick()
    fun onLeftClick()
}

object KetikKeyboardFactory {
    fun createKeyboardView(context: Context, listener: KeyboardListener): View {
        val view = LayoutInflater.from(context).inflate(R.layout.keyboard_view, null)
        
        // Map number keys
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
            view.findViewById<View>(id)?.setOnClickListener {
                listener.onNumberKeyClick(number)
            }
        }
        
        view.findViewById<View>(R.id.key_0)?.setOnClickListener {
            // As per requirements: tombol 0 commit spasi (and also works in multitap? "tombol 0 commit spasi")
            // We can just pass 0 to onSpaceClick or use the multi-tap engine.
            // Let's pass it to onNumberKeyClick(0) and let the engine handle it.
            listener.onNumberKeyClick(0)
        }
        
        view.findViewById<View>(R.id.key_delete)?.setOnClickListener {
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
        
        return view
    }
    
    fun updateSendButtonLabel(view: View, label: String) {
        view.findViewById<TextView>(R.id.text_send)?.text = label
    }
}
