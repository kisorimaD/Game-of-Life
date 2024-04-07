package boxes

import org.jetbrains.skija.Image


class StateButton : ActionImageButton {
    var state: Boolean = true

    constructor() : super()

    private fun stateChange() {
        state = !state
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
        initialState: Boolean
    ) : super(image1, imageOnHold1, image2, imageOnHold2, x, y, width, height, {}) {
        state = initialState
        if (state)
            swapImages()
    }

    init {
        buttonFunc = ::stateChange
    }

    fun loadState(state: Boolean) {
        if (state != this.state)
            swapImages()
        this.state = state
    }

}