package boxes

import org.jetbrains.skija.Canvas
import org.jetbrains.skija.Font
import org.jetbrains.skija.Paint
import org.jetbrains.skija.Typeface
import kotlin.math.min

open class TextBox() : Box() {
    var text: String = ""
    var textPaint = Paint()
    var typeface = Typeface.makeFromFile("fonts/ArialBold.ttf")

    var type = "centre"
    var isFilled = true

    constructor(
        text: String,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        backgroundPaint: Paint,
        textPaint: Paint,
        type: String = "centre",
        isFilled: Boolean = true
    ) : this() {
        this.text = text
        this.x = x
        this.y = y
        this.width = width
        this.height = height
        this.backgroundPaint = backgroundPaint
        this.textPaint = textPaint
        this.type = type
        this.isFilled = isFilled
    }

    fun drawText(canvas: Canvas, screenWidth: Float, screenHeight: Float) {
        val font = Font(typeface, 100f)
        var textBound = font.measureText(text)

        val needRect = getRect(screenWidth, screenHeight)

        val needScale = min(needRect.width / textBound.width, needRect.height / textBound.height)

        font.size *= needScale

        textBound = font.measureText(text)

        val rect = getRect(screenWidth, screenHeight)

        val realLeft = rect.left

        val realY = when {
            type == "centre" -> (rect.top + rect.bottom) / 2
            type == "top" -> rect.top
            type == "bottom" -> rect.bottom
            else -> (rect.top + rect.bottom) / 2
        }
        canvas.drawString(
            text, realLeft,
            realY, font, textPaint
        )
    }

    override fun draw(canvas: Canvas, screenWidth: Float, screenHeight: Float) {
        if (isFilled)
            drawInner(canvas, screenWidth, screenHeight)
        drawText(canvas, screenWidth, screenHeight)
    }

    override fun onClick() {
        println("Clicked on boxes.TextBox #${this.itemNumber}!")
    }


}