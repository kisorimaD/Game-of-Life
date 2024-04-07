
import boxes.*
import org.jetbrains.skija.Canvas
import org.jetbrains.skija.Image
import org.jetbrains.skija.Paint

class BoxMaster {

    fun createTextBox(text: String, x: Float, y: Float, width: Float, height: Float, backgroundPaint: Paint, textPaint: Paint, type : String = "centre", isFilled : Boolean = true) : Int {
        return TextBox(text, x, y, width, height, backgroundPaint, textPaint, type, isFilled).itemNumber
    }

    fun createActionOneImageButton(image: Image, imageOnHold : Image, x: Float, y: Float, width: Float, height: Float, f: () -> Unit) : Int {
        return ActionImageButton(image, imageOnHold, x, y, width, height, f).itemNumber
    }

    fun createActionTwoImageButton(image1: Image, imageOnHold1: Image, image2: Image, imageOnHold2 : Image, x: Float, y: Float, width: Float, height: Float, f: () -> Unit) : Int {
        return ActionImageButton(image1, imageOnHold1, image2, imageOnHold2, x, y, width, height, f).itemNumber
    }

    fun createButtonNeighborRuleMaster(image: Image, imageOnHold : Image, x: Float, y: Float, width: Float, height: Float, initStates : List<Pair<Int, Int>>) : Int {
        return ButtonNeighborRuleMaster(image, imageOnHold, x, y, width, height, initStates).itemNumber
    }

    fun createButtonSpawnRuleMaster(image: Image, imageOnHold : Image, x: Float, y: Float, width: Float, height: Float, initStates : List<Int>) : Int {
        return ButtonSpawnRuleMaster(image, imageOnHold, x, y, width, height, initStates).itemNumber
    }

    fun createButtonSurviveRuleMaster(image: Image, imageOnHold : Image, x: Float, y: Float, width: Float, height: Float, initStates: List<Int>) : Int {
        return ButtonSurviveRuleMaster(image, imageOnHold, x, y, width, height, initStates).itemNumber
    }


    fun checkClicks(mouseX: Float, mouseY: Float, screenWidth: Float, screenHeight: Float) {
        for (box in Box.boxes) {
            if (box.isInside(mouseX, mouseY, screenWidth, screenHeight)) {
                box.onClick()
            }
            box.onRelease()
            //println(box)
        }
    }

    fun checkLocations(mouseX: Float, mouseY: Float, screenWidth: Float, screenHeight: Float) {
        for (box in Box.boxes) {
            box.checkLocation(mouseX, mouseY, screenWidth, screenHeight)
        }
    }

    fun checkHolds() {
        for (box in Box.boxes) {
            if (box.isMouseInside) {
                box.onHold()
            }
            else
                box.onRelease()
        }
    }

    fun draw(canvas: Canvas, screenWidth: Float, screenHeight: Float) {
        for (box in Box.boxes) {
            box.draw(canvas, screenWidth, screenHeight)
        }
    }

}