

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.swing.Swing
import org.jetbrains.skija.Canvas
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkiaRenderer
import org.jetbrains.skiko.SkiaWindow
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.*
import java.awt.event.KeyListener
import java.awt.event.MouseListener
import java.awt.event.MouseMotionAdapter
import javax.swing.*
import kotlin.time.ExperimentalTime

const val boardWidth = 64
const val boardHeight = 64

val grid = Grid(boardWidth, boardHeight)
val game = GameOfLife(boardWidth, boardHeight, isRandomSeed = true)
val gameSettings = GameSettings()
val boxMaster : BoxMaster = gameSettings.getBoxMaster()
val saveMaster = SaveMaster(game, grid, gameSettings)

var autoTurns = false
var delayedMoves : Int = 0

lateinit var window : SkiaWindow


fun main(args : Array<String>) {
    saveMaster.loadLast()
    createWindow("Game Of Life")
}
fun createWindow(title: String) = runBlocking(Dispatchers.Swing) {
    window = SkiaWindow()
    window.defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE
    window.title = title

    window.layer.renderer = Renderer(window.layer)
    window.layer.addMouseMotionListener(MouseMotionAdapter)
    window.layer.addKeyListener(KeyListener)
    window.layer.addMouseListener(MouseListener)
    window.addWindowListener(WindowListener)

    window.preferredSize = Dimension(1400, 800)
    window.minimumSize = Dimension(800, 450)
    window.pack()
    window.layer.awaitRedraw()
    window.isVisible = true

}
class Renderer(val layer: SkiaLayer) : SkiaRenderer {



    @ExperimentalTime
    override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {

        val contentScale = layer.contentScale
        canvas.scale(contentScale, contentScale)

        State.screenWidth = (width / layer.contentScale)
        State.screenHeight = (height / layer.contentScale)

        val (x, y) = grid.getCellCords(State.mouseX, State.mouseY)

        grid.draw(canvas, State.mouseX, State.mouseY, State.screenWidth, State.screenHeight, game.collectBoard(), game.measureLifetime(game.getCell(x, y)).toString())

        boxMaster.draw(canvas, State.screenWidth, State.screenHeight)
        boxMaster.checkLocations(State.mouseX, State.mouseY, State.screenWidth, State.screenHeight)


        layer.needRedraw()

        if(autoTurns || delayedMoves > 0) {
            game.setNextState()
            if(delayedMoves > 0)
                delayedMoves--
        }

    }


}

object State {
    var mouseX = 0f
    var mouseY = 0f

    var mousePressed = false
    val pressedCellList : MutableList <Pair<Int, Int>> = mutableListOf()

    var screenWidth = 0f
    var screenHeight = 0f
}

object MouseMotionAdapter : MouseMotionAdapter() {
    override fun mouseMoved(event: MouseEvent) {
        State.mouseX = event.x.toFloat()
        State.mouseY = event.y.toFloat()
    }

    override fun mouseDragged(event: MouseEvent) {
        State.mouseX = event.x.toFloat()
        State.mouseY = event.y.toFloat()

        val cellCords = grid.getCellCords(State.mouseX, State.mouseY)

        if(cellCords !in State.pressedCellList) {
            game.invertCellState(cellCords.first, cellCords.second)
            State.pressedCellList.add(cellCords)
        }
    }


}

val keysPressed : MutableMap <Char, Boolean> = mutableMapOf('+' to false, '-' to false,
    'f' to false)
object KeyListener : KeyListener {
    override fun keyTyped(e: KeyEvent?) {
    }

    override fun keyPressed(e: KeyEvent?) {
        if(e?.keyChar == '+') {
            if(!keysPressed['+']!!) {
                grid.scale /= 1.1f
                //grid.cameraXMove -= grid.cameraXMove / grid.scale
                //grid.cameraYMove -= grid.cameraYMove / grid.scale
                keysPressed['+'] = true
            }
        }

        if(e?.keyChar == '-') {
            if(!keysPressed['-']!!) {
                grid.scale *= 1.1f
                //grid.cameraXMove += grid.cameraXMove / grid.scale
                //grid.cameraYMove += grid.cameraYMove / grid.scale
                keysPressed['-'] = true
            }
        }

        if(e?.keyChar == 'f' || e?.keyChar == 'а' || e?.keyChar == 'F' || e?.keyChar == 'А') {
            if(!keysPressed['f']!!) {
                autoTurns = !autoTurns
                keysPressed['f'] = true
            }
        }

        if(e?.keyCode == KeyEvent.VK_UP || e?.keyCode == KeyEvent.VK_W){
            grid.moveYVelocity -= 1
        }
        if(e?.keyCode == KeyEvent.VK_LEFT || e?.keyCode == KeyEvent.VK_A){
            grid.moveXVelocity -= 1
        }
        if(e?.keyCode == KeyEvent.VK_DOWN || e?.keyCode == KeyEvent.VK_S){
            grid.moveYVelocity += 1
        }
        if(e?.keyCode == KeyEvent.VK_RIGHT || e?.keyCode == KeyEvent.VK_D){
            grid.moveXVelocity += 1
        }

        if(e?.keyChar == ' ' && !autoTurns) {
            game.setNextState()
        }
    }

    override fun keyReleased(e: KeyEvent?) {
        if(e?.keyChar == '+') {
            keysPressed['+'] = false
        }
        if(e?.keyChar == '-') {
            keysPressed['-'] = false
        }
        if(e?.keyChar == 'f') {
            keysPressed['f'] = false
        }
        if(e?.keyChar == 's') {
            //saveMaster.save()
        }

        if(e?.keyChar == 'l')
        {
            saveMaster.load()
        }

        if(e?.keyChar == 'i')
        {
//            saveMaster.saveAsImage()
        }

        if(e?.keyChar == 'j')
        {
//            saveMaster.loadFromImage()
        }
    }
}

object MouseListener : MouseListener {
    override fun mouseClicked(e: MouseEvent?) {
    }

    override fun mousePressed(e: MouseEvent?) {
        if(e?.button == MouseEvent.BUTTON1) {
            boxMaster.checkHolds()

            println("MousePressed. x = ${e.x}, y = ${e.y}")
            println("CellCords: ${grid.getCellCords(e.x.toFloat(), e.y.toFloat())}")
            println()

            val cellCords = grid.getCellCords(e.x.toFloat(), e.y.toFloat())

            game.invertCellState(cellCords.first, cellCords.second)

            State.mousePressed = true
            State.pressedCellList.clear()
        }
    }

    override fun mouseReleased(e: MouseEvent?) {
        boxMaster.checkClicks(State.mouseX, State.mouseY, State.screenWidth, State.screenHeight)
        println("Mouse released!")
    }

    override fun mouseEntered(e: MouseEvent?) {
    }

    override fun mouseExited(e: MouseEvent?) {
    }
}

object WindowListener : WindowAdapter() {
    override fun windowClosing(e: WindowEvent?) {
        println("Window closing!")
        SaveDialog.isVisible = true
        //super.windowClosing(e)
    }

    override fun windowClosed(e: WindowEvent?) {
        println("Window closed!")
        super.windowClosed(e)
    }
}

object SaveDialog : JDialog() {
    private fun readResolve(): Any = SaveDialog

    init {
        title = "Save"
        defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
        setSize(400, 200)
        setLocationRelativeTo(null)
        isModal = true
        isVisible = false

        val message = JLabel("Do you want to save your progress?")

        val saveButton = JButton("Save")
        val dontSaveButton = JButton("Don't save")

        saveButton.addActionListener {
            if (saveMaster.save())
                window.dispose()
            dispose()
        }

        dontSaveButton.addActionListener {
            window.dispose()
            saveMaster.deleteLastSave()
            dispose()
        }
        val buttonPanel = JPanel()
        buttonPanel.add(saveButton)
        buttonPanel.add(dontSaveButton)

        layout = BorderLayout()
        add(message, BorderLayout.CENTER)
        add(buttonPanel, BorderLayout.SOUTH)

        pack()
    }
}



//fun distanceSq(x1: Float, y1: Float, x2: Float, y2: Float) = (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2)