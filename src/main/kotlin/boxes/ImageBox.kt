package boxes

import org.jetbrains.skija.Canvas
import org.jetbrains.skija.Image

open class ImageBox() : Box() {
    var image: Image? = null
    var imageCopy: Image? = null

    var imageOnHold: Image? = null

    constructor(image: Image, imageOnHold: Image, x: Float, y: Float, width: Float, height: Float) : this() {
        this.image = image
        this.imageCopy = image
        this.imageOnHold = imageOnHold
        this.x = x
        this.y = y
        this.width = width
        this.height = height
    }


    override fun draw(canvas: Canvas, screenWidth: Float, screenHeight: Float) {
        val realRect = getRect(screenWidth, screenHeight)
        canvas.drawImageRect(image!!, realRect)
    }

    override fun onClick() {
        println("Clicked on boxes.ImageBox #${this.itemNumber}!")
    }

    override fun onHold() {
        image = imageOnHold
    }

    override fun onRelease() {
        image = imageCopy
    }


}