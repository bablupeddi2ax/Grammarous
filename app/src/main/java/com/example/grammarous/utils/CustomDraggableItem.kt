package com.example.grammarous.utils

import android.content.ClipData
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class CustomDraggableItem(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var text: String = ""
    private var paint: Paint = Paint().apply {
        color = Color.BLUE
        textSize = 40f
        isAntiAlias = true
    }

    init {
        setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val data = ClipData.newPlainText("text", text)
                val shadowBuilder = CustomDragShadowBuilder(this)
                startDragAndDrop(data, shadowBuilder, this, 0)
            }

            true
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
