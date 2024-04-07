import org.jetbrains.skija.Image
import org.jetbrains.skija.Paint
import org.jetbrains.skija.PaintMode
import java.awt.BorderLayout
import java.io.File
import javax.swing.*

val DeadCell = Paint().apply {
    color = 0xFF211111.toInt()
    mode = PaintMode.FILL
}

val AliveCell = Paint().apply {
    color = 0xFFFFFFFF.toInt()
    mode = PaintMode.FILL
}

val screenColor = Paint().apply {
    color = 0xFF111111.toInt()
    mode = PaintMode.FILL
}

val CursorCellColor = Paint().apply {
    color = 0xFF00FF00.toInt()
    mode = PaintMode.STROKE
    strokeWidth = 1f
}

val gridColor = Paint().apply {
    color = 0xFFFFFFFF.toInt()
    mode = PaintMode.STROKE
    strokeWidth = 1f
}

val wallColor = Paint().apply {
    color = 0xFFFFFFFF.toInt()
    mode = PaintMode.FILL
}

val standartCellColor = Paint().apply {
    color = 0xFF000000.toInt()
    mode = PaintMode.FILL
}

val BLACK = Paint().apply {
    color = 0xFF000000.toInt()
    mode = PaintMode.FILL
}

val WHITE = Paint().apply {
    color = 0xFFFFFFFF.toInt()
    mode = PaintMode.FILL
}

fun invertAutoTurns() {
    autoTurns = !autoTurns
}

fun reloadRandomBoard() {
    game.isRandomSeed = true
    game.makeBoard()
}

fun reloadEmptyBoard() {
    game.isRandomSeed = false
    game.makeBoard()
}

fun zoomIn() {
    grid.scale /= 1.1f
}

fun zoomOut() {
    grid.scale *= 1.1f
}

fun importGame() {
    saveMaster.load()
}

fun exportGame() {
    saveMaster.save()
}

object MultipleTurnsFrame : JDialog() {
    init {
        title = "Enter number of turns"
        defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
        setSize(400, 200)
        setLocationRelativeTo(null)
        isModal = true
        isVisible = false

        val saveButton = JButton("OK")

        val textField = JTextField("1")
        textField.columns = 1
        textField.isVisible = true

        saveButton.addActionListener {
            val turns = textField.text.toIntOrNull()
            if (turns != null) {
                delayedMoves = turns
            }
            dispose()
        }

        val buttonPanel = JPanel()
        buttonPanel.add(saveButton)

        layout = BorderLayout()
        add(buttonPanel, BorderLayout.EAST)
        add(textField, BorderLayout.CENTER)

        pack()
    }
}


fun callMultipleTurnsField() {
    MultipleTurnsFrame.isVisible = true
}


//val nullImage = Image.makeFromEncoded(File("resources/null.png").readBytes())

val playButtonRegularImage = Image.makeFromEncoded(File("resources/playButtonRegular.png").readBytes())
val playButtonOnHoldImage = Image.makeFromEncoded(File("resources/playButtonOnHold.png").readBytes())

val pauseButtonRegularImage = Image.makeFromEncoded(File("resources/pauseButtonRegular.png").readBytes())
val pauseButtonOnHoldImage = Image.makeFromEncoded(File("resources/pauseButtonOnHold.png").readBytes())

val reloadButtonRegularImage = Image.makeFromEncoded(File("resources/reloadButtonRegular.png").readBytes())
val reloadButtonOnHoldImage = Image.makeFromEncoded(File("resources/reloadButtonOnHold.png").readBytes())

val plusButtonRegularImage = Image.makeFromEncoded(File("resources/plusButtonRegular.png").readBytes())
val plusButtonOnHoldImage = Image.makeFromEncoded(File("resources/plusButtonOnHold.png").readBytes())

val minusButtonRegularImage = Image.makeFromEncoded(File("resources/minusButtonRegular.png").readBytes())
val minusButtonOnHoldImage = Image.makeFromEncoded(File("resources/minusButtonOnHold.png").readBytes())

val nullButtonRegularImage = Image.makeFromEncoded(File("resources/nullButtonRegular.png").readBytes())
val nullButtonOnHoldImage = Image.makeFromEncoded(File("resources/nullButtonOnHold.png").readBytes())

val crossButtonRegularImage = Image.makeFromEncoded(File("resources/crossButtonRegular.png").readBytes())
val crossButtonOnHoldImage = Image.makeFromEncoded(File("resources/crossButtonOnHold.png").readBytes())

val saveButtonRegularImage = Image.makeFromEncoded(File("resources/saveButtonRegular.png").readBytes())
val saveButtonOnHoldImage = Image.makeFromEncoded(File("resources/saveButtonOnHold.png").readBytes())

val importButtonRegularImage = Image.makeFromEncoded(File("resources/importButtonRegular.png").readBytes())
val importButtonOnHoldImage = Image.makeFromEncoded(File("resources/importButtonOnHold.png").readBytes())

val exportButtonRegularImage = Image.makeFromEncoded(File("resources/exportButtonRegular.png").readBytes())
val exportButtonOnHoldImage = Image.makeFromEncoded(File("resources/exportButtonOnHold.png").readBytes())

val multipleTurnButtonRegularImage = Image.makeFromEncoded(File("resources/multipleTurnButtonRegular.png").readBytes())
val multipleTurnButtonOnHoldImage = Image.makeFromEncoded(File("resources/multipleTurnButtonOnHold.png").readBytes())

class GameSettings {

    private val boxMaster = BoxMaster()

    var neighbourButtonID = 0
    var spawnButtonID = 0
    var surviveButtonID = 0

    var neighborInitStates = (-1..1).map { i -> (-1..1).map { j -> i to j } }.flatten().filter { it != 0 to 0 }
    var spawnInitStates = listOf(3)
    var surviveInitStates = listOf(2, 3)

    fun createBoxes() {
        this.boxMaster.createTextBox(
            "Game Of Life",
            0.5f,
            0f,
            0.3f,
            0.1f,
            BLACK,
            WHITE,
            type = "bottom",
            isFilled = false
        )

        this.boxMaster.createActionTwoImageButton(
            playButtonRegularImage,
            playButtonOnHoldImage,
            pauseButtonRegularImage,
            pauseButtonOnHoldImage,
            0.05f,
            0.2f,
            0.1f,
            0.1f,
            ::invertAutoTurns
        )

        this.boxMaster.createActionOneImageButton(
            reloadButtonRegularImage,
            reloadButtonOnHoldImage,
            0.05f,
            0.3f,
            0.1f,
            0.1f,
            ::reloadRandomBoard
        )

        this.boxMaster.createActionOneImageButton(
            reloadButtonRegularImage,
            reloadButtonOnHoldImage,
            0.15f,
            0.3f,
            0.1f,
            0.1f,
            ::reloadEmptyBoard
        )

        this.boxMaster.createActionOneImageButton(
            plusButtonRegularImage,
            plusButtonOnHoldImage,
            0.05f,
            0.4f,
            0.1f,
            0.1f,
            ::zoomIn
        )

        this.boxMaster.createActionOneImageButton(
            minusButtonRegularImage,
            minusButtonOnHoldImage,
            0.05f,
            0.5f,
            0.1f,
            0.1f,
            ::zoomOut
        )

        this.boxMaster.createActionOneImageButton(
            importButtonRegularImage,
            importButtonOnHoldImage,
            0.05f,
            0.1f,
            0.1f,
            0.1f,
            ::importGame
        )

        this.boxMaster.createActionOneImageButton(
            exportButtonRegularImage,
            exportButtonOnHoldImage,
            0.05f,
            0f,
            0.1f,
            0.1f,
            ::exportGame
        )

        this.boxMaster.createActionOneImageButton(
            multipleTurnButtonRegularImage,
            multipleTurnButtonOnHoldImage,
            0.15f,
            0.2f,
            0.1f,
            0.1f,
            ::callMultipleTurnsField
        )

        this.boxMaster.createTextBox(
            "Neighbor Rules",
            0.8f,
            0.09f,
            0.3f,
            0.1f,
            BLACK,
            WHITE,
            type = "bottom",
            isFilled = false
        )
        neighbourButtonID = this.boxMaster.createButtonNeighborRuleMaster(
            saveButtonRegularImage,
            saveButtonOnHoldImage,
            0.8f,
            0.3f,
            0.3f,
            0.3f,
            neighborInitStates
        )

        this.boxMaster.createTextBox(
            "Spawn Rules",
            0.5f,
            0.6f,
            0.3f,
            0.1f,
            BLACK,
            WHITE,
            type = "bottom",
            isFilled = false
        )
        spawnButtonID = this.boxMaster.createButtonSpawnRuleMaster(
            saveButtonRegularImage,
            saveButtonOnHoldImage,
            0.5f,
            0.7f,
            0.9f,
            0.09f,
            spawnInitStates
        )

        this.boxMaster.createTextBox(
            "Survive Rules",
            0.5f,
            0.8f,
            0.3f,
            0.1f,
            BLACK,
            WHITE,
            type = "bottom",
            isFilled = false
        )
        surviveButtonID = this.boxMaster.createButtonSurviveRuleMaster(
            saveButtonRegularImage,
            saveButtonOnHoldImage,
            0.5f,
            0.9f,
            0.9f,
            0.09f,
            surviveInitStates
        )
    }

    fun getBoxMaster(): BoxMaster {
        createBoxes()
        return this.boxMaster
    }

}