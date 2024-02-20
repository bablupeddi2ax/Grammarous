package com.example.grammarous.utils

import android.content.ClipDescription
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.DragEvent
import android.view.View
import android.widget.TextView

class CustomDroppableView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var text: String = ""
    private var paint: Paint = Paint().apply {
        color = Color.RED
        textSize = 40f
        isAntiAlias = true
    }

    init {
        setOnDragListener { _, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_ENTERED -> {
                    setBackgroundColor(Color.LTGRAY)
                    true
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    setBackgroundColor(Color.WHITE)
                    true
                }
                DragEvent.ACTION_DROP -> {
                    val item = event.clipData.getItemAt(0)
                    if (item.text.toString() == text) {
                        setBackgroundColor(Color.GREEN)
                    } else {
                        setBackgroundColor(Color.RED)
                    }
                    true
                }
                else -> false
            }
        }
    }

    fun setText(text: String) {
        this.text = text
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        canvas.drawText(text, 20f, height / 2f, paint)
    }
}
