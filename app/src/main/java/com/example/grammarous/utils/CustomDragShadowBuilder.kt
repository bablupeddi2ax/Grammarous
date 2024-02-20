package com.example.grammarous.utils

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.view.View

class CustomDragShadowBuilder(view: View) : View.DragShadowBuilder(view) {

    // Override onProvideShadowMetrics to define the size and touch point of the drag shadow
    override fun onProvideShadowMetrics(outShadowSize: Point, outShadowTouchPoint: Point) {
        val width = view.width
        val height = view.height

        // Set the size of the drag shadow
        outShadowSize.set(width, height)

        // Set the touch point of the drag shadow to the center
        outShadowTouchPoint.set(width / 2, height / 2)
    }

    // Override onDrawShadow to draw the custom drag shadow
    override fun onDrawShadow(canvas: Canvas) {
        // Draw a gray rectangle as the drag shadow
        val paint = Paint().apply {
            color = Color.GRAY
        }
        canvas.drawRect(0f, 0f, view.width.toFloat(), view.height.toFloat(), paint)

        // Draw the text of the view on top of the rectangle
        super.onDrawShadow(canvas)
    }
}
