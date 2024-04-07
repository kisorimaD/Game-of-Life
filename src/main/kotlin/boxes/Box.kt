package boxes

import org.jetbrains.skija.Canvas
import org.jetbrains.skija.Paint
import org.jetbrains.skija.Rect
import kotlin.math.min

abstract class Box {
    // x,y,w,h - relative to screen size
    var x: Float = 0f
    var y: Float = 0f
    var width: Float = 0f
    var height: Float = 0f

    var backgroundPaint = Paint()

    var isMouseInside = false
    var itemNumber = numberOfBoxes

    init {
        numberOfBoxes++
        boxes.add(this)
    }

    abstract fun onClick()

    open fun onEnter() {}
    open fun onExit() {}

    open fun onHold() {}

    open fun onRelease() {}

    fun checkLocation(mouseX: Float, mouseY: Float, screenWidth: Float, screenHeight: Float) {
        if (isInside(mouseX, mouseY, screenWidth, screenHeight)) {
            if (!isMouseInside) {
                isMouseInside = true
                onEnter()
            }
        } else {
            if (isMouseInside) {
                isMouseInside = false
                onExit()
            }
        }
    }

    private val relativePositionOnScreen = 0.7f

    fun getRealWH(screenWidth: Float, screenHeight: Float): Pair<Float, Float> {
        return Pair(width * screenWidth, height * screenHeight)
//        val rel = height / width
//        return if(width * screenWidth * rel > screenHeight * height)
//            Pair(screenHeight * height / rel, screenHeight * height)
//        else
//            Pair(screenWidth * width, screenWidth * width * rel)
    }

    fun getRect(screenWidth: Float, screenHeight: Float): Rect {
        val buttonScreenWidth: Float = screenWidth - screenWidth * relativePositionOnScreen / 2 - min(
            screenWidth * relativePositionOnScreen,
            screenHeight
        ) / 2
        val buttonScreenSize: Float = min(buttonScreenWidth, screenHeight)
        val (realWidth, realHeight) = getRealWH(buttonScreenSize, buttonScreenSize)

        return Rect.makeLTRB(
            (screenWidth - buttonScreenWidth) + x * buttonScreenSize - realWidth / 2,
            (screenHeight - buttonScreenSize) / 2 + y * buttonScreenSize - realHeight / 2,
            (screenWidth - buttonScreenWidth) + x * buttonScreenSize + realWidth / 2,
            (screenHeight - buttonScreenSize) / 2 + y * buttonScreenSize + realHeight / 2
        )

//        val (realWidth, realHeight) = getRealWH(screenWidth, screenHeight)
//        return  Rect.makeLTRB(x * screenWidth - realWidth / 2, y * screenHeight - realHeight / 2,
//            x * screenWidth + realWidth / 2, y * screenHeight + realHeight / 2)
    }

    fun drawInner(canvas: Canvas, screenWidth: Float, screenHeight: Float) {
        canvas.drawRect(getRect(screenWidth, screenHeight), backgroundPaint)
    }

    abstract fun draw(canvas: Canvas, screenWidth: Float, screenHeight: Float)

    fun isInside(mouseX: Float, mouseY: Float, screenWidth: Float, screenHeight: Float): Boolean {
        val needRect = getRect(screenWidth, screenHeight)

        return (mouseX >= needRect.left && mouseX <= needRect.right &&
                mouseY >= needRect.top && mouseY <= needRect.bottom)

//        val (realWidth, realHeight) = getRealWH(screenWidth, screenHeight)
//        return mouseX >= x * screenWidth - realWidth / 2 && mouseX <= x * screenWidth + realWidth / 2 &&
//                mouseY >= y * screenHeight - realHeight / 2 && mouseY <= y * screenHeight + realHeight / 2
    }

    override fun toString(): String {
        return "boxes.Box | x=$x, y=$y | #$itemNumber"
    }

    companion object {
        var numberOfBoxes = 0

        var boxes = mutableListOf<Box>()
    }
}