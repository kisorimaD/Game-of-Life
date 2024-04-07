import org.jetbrains.skija.*
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

//const val DEAD = 0.toByte()


val typeface = Typeface.makeFromFile("fonts/ArialBold.ttf")
//val font = Font(typeface, 40f)

class Grid(var boardWidth: Int, var boardHeight: Int) {

    /** Screen width in pixels */
    var w: Float = 0f

    /** Screen height in pixels*/
    var h: Float = 0f

    val indent: Float = 0.9f
    val length: Float
        get() = min(w, h)

    val cellSize: Float
        get() = min(w / boardWidth / scale, h / boardHeight / scale) * indent

    var moveXVelocity: Float = 0f
    var moveYVelocity: Float = 0f

    var cameraXMove: Float = 0f
    var cameraYMove: Float = 0f

    val cameraX: Float
        get() = length / (2 * scale) + cameraXMove

    val cameraY: Float
        get() = length / (2 * scale) + cameraYMove


    var scale: Float = 1f


    val beginX: Float
        get() = (w - boardWidth * cellSize * scale) / 2

    val beginY: Float
        get() = (h - boardHeight * cellSize * scale) / 2

    val endX: Float
        get() = beginX + boardWidth * cellSize * scale

    val endY: Float
        get() = beginY + boardHeight * cellSize * scale

    val wallThickness: Float
        get() = length / 300

    val beginWallThickness: Float
        get() = 32f / min(boardHeight, boardWidth)

    fun getCellCords(mX: Float, mY: Float): Pair<Int, Int> {
        val x = ((cameraX - length / 2 + mX - beginX) / cellSize).toInt()
        val y = ((cameraY - length / 2 + mY - beginY) / cellSize).toInt()

        if (x < 0 || x >= boardWidth || y < 0 || y >= boardHeight) return Pair(-1, -1)

        return Pair(x, y)
    }

    fun fillScreen(canvas: Canvas, w: Float, h: Float) {
        this.w = w * 0.7f
        this.h = h
        canvas.drawRect(Rect.makeXYWH(0f, 0f, w.toFloat(), h.toFloat()), screenColor)
    }

    fun drawWalls(canvas: Canvas) {
        canvas.drawRect(Rect.makeXYWH(beginX, beginY - wallThickness, endX - beginX, wallThickness), wallColor)
        canvas.drawRect(Rect.makeXYWH(beginX - wallThickness, beginY, wallThickness, endY - beginY), wallColor)
        canvas.drawRect(Rect.makeXYWH(endX + wallThickness, beginY, wallThickness, endY - beginY), wallColor)
        canvas.drawRect(Rect.makeXYWH(beginX, endY + wallThickness, endX - beginX, wallThickness), wallColor)
    }

    data class CellCords(val x: Float, val y: Float, val w: Float, val h: Float)

    fun getCellCords(x: Int, y: Int): CellCords {
        val cellX = beginX + length / 2 - cameraX + x * cellSize
        val cellY = beginY + length / 2 - cameraY + y * cellSize

        val cellW = min(cellX + cellSize, endX) - max(cellX, beginX)
        val cellH = min(cellY + cellSize, endY) - max(cellY, beginY)

        return CellCords(cellX, cellY, cellW, cellH)
    }

    fun drawCell(canvas: Canvas, x: Int, y: Int, color: Paint) {

        if (x < 0 || x >= boardWidth || y < 0 || y >= boardHeight) return

        val (cellX, cellY, cellW, cellH) = getCellCords(x, y)

        if (cellW <= 0 || cellH <= 0) return

        canvas.drawRect(Rect.makeXYWH(max(cellX, beginX), max(cellY, beginY), cellW, cellH), color)
    }

    fun drawCursor(canvas: Canvas, mouseX: Float, mouseY: Float) {

        if (mouseX < beginX || mouseX > endX || mouseY < beginY || mouseY > endY) return

        val (x, y) = getCellCords(mouseX, mouseY)

        drawCell(canvas, x, y, CursorCellColor)
    }

    fun drawGrid(canvas: Canvas) {
//        if((1 / scale) * beginWallThickness < 0.03f)
//            return

        val firstCellX = max(ceil(((cameraX - length / 2) / cellSize)).toInt() - 1, 0)
        val firstCellY = max(ceil(((cameraY - length / 2) / cellSize)).toInt() - 1, 0)

        val lastCellX = min(((cameraX - length / 2 + endX - beginX) / cellSize).toInt() + 1, boardWidth)
        val lastCellY = min(((cameraY - length / 2 + endY - beginY) / cellSize).toInt() + 1, boardHeight)
//
//        val firstX = beginX + length / 2 - cameraX +  firstCellX * cellSize
//        val firstY = beginY + length / 2 - cameraY + firstCellY * cellSize
//
//        val lastX = beginX + length / 2 - cameraX +  lastCellX * cellSize
//        val lastY = beginY + length / 2 - cameraY + lastCellY * cellSize

        val (firstX, firstY) = getCellCords(firstCellX, firstCellY)
        val (lastX, lastY) = getCellCords(lastCellX, lastCellY)

        var x = firstX

        if (firstY > endY || lastY < beginY)
            return

        while (x <= lastX + (1 / scale) * beginWallThickness) {
            if (x > beginX && x < endX)
                canvas.drawLine(x, max(firstY, beginY), x, min(lastY, endY), Paint().apply {
                    color = 0xFFFFFFFF.toInt()
                    mode = PaintMode.STROKE
                    strokeWidth = (1 / scale) * beginWallThickness
                })
            x += cellSize
        }

        var y = firstY

        if (firstX > endX || lastX < beginX)
            return

        while (y <= lastY + (1 / scale) * beginWallThickness) {
            if (y > beginY && y < endY)
                canvas.drawLine(max(firstX, beginX), y, min(lastX, endX), y, Paint().apply {
                    color = 0xFFFFFFFF.toInt()
                    mode = PaintMode.STROKE
                    strokeWidth = (1 / scale) * beginWallThickness
                })
            y += cellSize
        }
    }

    fun getColor(b: Byte): Paint {
        return if (b == 0.toByte()) DeadCell else AliveCell
    }

    fun drawCells(canvas: Canvas, board: Board) {

        val firstX = ceil(((cameraX - length / 2) / cellSize)).toInt()
        val firstY = ceil(((cameraY - length / 2) / cellSize)).toInt()

        val lastX = ((cameraX - length / 2 + endX - beginX) / cellSize).toInt()
        val lastY = ((cameraY - length / 2 + endY - beginY) / cellSize).toInt()

        for (x in max(0, firstX - 1)..min(lastX + 1, boardWidth - 1)) {
            for (y in max(0, firstY - 1)..min(lastY + 1, boardHeight - 1)) {
                if (board[y * boardWidth + x].alive == DEAD) continue
                drawCell(canvas, x, y, AliveCell)
            }
        }
    }

    fun drawTextOnCell(canvas: Canvas, x: Int, y: Int, text: String, color: Paint = standartCellColor): Boolean {
//        val cellX = beginX + length / 2 - cameraX +  x * cellSize
//        val cellY = beginY + length / 2 - cameraY + y * cellSize
//
//        val cellW = min(cellX + cellSize, endX) - max(cellX, beginX)
//        val cellH = min(cellY + cellSize, endY) - max(cellY, beginY)

        val (cellX, cellY, cellW, cellH) = getCellCords(x, y)

        if (cellW <= 0 || cellH <= 0) return false

        val font = Font(typeface, cellH * 0.8f)

        var textBounds = font.measureText(text)

        font.size *= min(cellW * 0.9f / textBounds.width, cellH * 0.9f / textBounds.height)

        textBounds = font.measureText(text)

        canvas.drawString(
            text,
            cellX + (cellW - textBounds.width) / 2,
            cellY + (cellH + textBounds.height) / 2,
            font,
            color
        )

        return true
    }

    fun computeVelocity() {
        cameraXMove += moveXVelocity
        cameraYMove += moveYVelocity

        moveXVelocity *= 0.9f
        moveXVelocity = if (moveXVelocity < 0.01f && moveXVelocity > -0.01f) 0f else moveXVelocity
        moveYVelocity *= 0.9f
        moveYVelocity = if (moveYVelocity < 0.01f && moveYVelocity > -0.01f) 0f else moveYVelocity

        val scale2 = scale * 2

        if (cameraXMove < -w / scale2) cameraXMove = -(w / scale2)
        if (cameraXMove > w / scale2) cameraXMove = w / scale2

        if (cameraYMove < -h / scale2) cameraYMove = -h / scale2
        if (cameraYMove > h / scale2) cameraYMove = h / scale2
    }

    fun draw(canvas: Canvas, mouseX: Float, mouseY: Float, w: Float, h: Float, board: Board, textOnCell: String) {
        val (x, y) = getCellCords(mouseX, mouseY)

        fillScreen(canvas, w, h)
        drawGrid(canvas)
        drawWalls(canvas)
        drawCells(canvas, board)
        drawCursor(canvas, mouseX, mouseY)
        drawTextOnCell(canvas, x, y, textOnCell)
        computeVelocity()

    }
}