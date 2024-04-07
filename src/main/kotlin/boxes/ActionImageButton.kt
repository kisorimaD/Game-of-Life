package boxes

import org.jetbrains.skija.Image

open class ActionImageButton : ImageBox {

    var buttonFunc: () -> Unit = {}

    var isChangingImage = false

    var image2: Image? = null
    var imageCopy2: Image? = null
    var imageOnHold2: Image? = null


    constructor() : super()

    constructor(
        image: Image,
        imageOnHold: Image,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        f: () -> Unit
    ) : super(image, imageOnHold, x, y, width, height) {
        this.buttonFunc = f
    }

    constructor(
        image1: Image,
        imageOnHold1: Image,
        image2: Image,
        imageOnHold2: Image,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        f: () -> Unit
    ) : super(image1, imageOnHold1, x, y, width, height) {
        this.buttonFunc = f
        isChangingImage = true
        this.image2 = image2
        this.imageCopy2 = image2
        this.imageOnHold2 = imageOnHold2
    }

    fun swapImages() {
        if (!isChangingImage) return
        val tmp = image
        image = image2
        image2 = tmp

        val tmp2 = imageCopy
        imageCopy = imageCopy2
        imageCopy2 = tmp2

        val tmp3 = imageOnHold
        imageOnHold = imageOnHold2
        imageOnHold2 = tmp3
    }

    override fun onClick() {
        buttonFunc()
        swapImages()
        println("Clicked on Button #${this.itemNumber}")
    }


}