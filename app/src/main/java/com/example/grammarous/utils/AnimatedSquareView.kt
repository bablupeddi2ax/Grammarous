package com.example.grammarous.utils
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.util.*

class AnimatedSquaresView : View {

    private val squarePaint = Paint().apply {
        color = resources.getColor(android.R.color.white)
    }

    private val squares = mutableListOf<Square>()

    private val random = Random()

    private data class Square(
        var x: Float,
        var y: Float,
        var size: Float,
        var speed: Float,
        var rotation: Float
    )

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.apply {
            squares.forEach { square ->
                rotate(square.rotation, square.x + square.size / 2, square.y + square.size / 2)
                drawRect(
                    square.x,
                    square.y,
                    square.x + square.size,
                    square.y + square.size,
                    squarePaint
                )

                square.y -= square.speed

                // If the square moves above the top of the screen, reset its position
                if (square.y + square.size < 0) {
                    resetSquarePosition(square)
                }
            }
        }
        postInvalidateOnAnimation()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // Initialize squares when the view size changes
        squares.clear()
        repeat(NUMBER_OF_SQUARES) {
            squares.add(
                Square(
                    x = (random.nextInt(width - MAX_SQUARE_SIZE)).toFloat(),
                    y = (h + random.nextInt(h)).toFloat(),
                    size = random.nextInt(MAX_SQUARE_SIZE).toFloat(),
                    speed = (MIN_SPEED + random.nextInt(MAX_SPEED - MIN_SPEED)).toFloat(),
                    rotation = random.nextFloat() * 360
                )
            )
        }
    }



    private fun findTouchedSquare(touchX: Float, touchY: Float): Square? {
        squares.forEach { square ->
            if (touchX >= square.x && touchX <= square.x + square.size &&
                touchY >= square.y && touchY <= square.y + square.size
            ) {
                return square
            }
        }
        return null
    }

    private fun resetSquarePosition(square: Square) {
        square.x = (random.nextInt(width - MAX_SQUARE_SIZE)).toFloat()
        square.y = height.toFloat()
    }

    companion object {
        private const val NUMBER_OF_SQUARES = 10
        private const val MAX_SQUARE_SIZE = 250
        private const val MIN_SPEED = 1
        private const val MAX_SPEED = 3
    }
}
