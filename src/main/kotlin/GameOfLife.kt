import com.aparapi.Kernel

typealias Board = List<GameOfLife.Cell>

const val DEAD: Boolean = false
const val ALIVE: Boolean = true

fun standartGOLSpawnRule(aliveNeighbours: Int): Boolean {
    return aliveNeighbours == 3
}

fun standartGOLSurviveRule(aliveNeighbours: Int): Boolean {
    return aliveNeighbours == 2 || aliveNeighbours == 3
}


val standartGOLNeighbourhoodRule: List<Pair<Int, Int>> = listOf(
    -1 to -1,
    -1 to 0,
    -1 to 1,
    0 to -1,
    0 to 1,
    1 to -1,
    1 to 0,
    1 to 1
)

public class GameOfLife(
    var width: Int, var height: Int, var isRandomSeed: Boolean,
    var spawnRule: (Int) -> Boolean = ::standartGOLSpawnRule,
    var surviveRule: (Int) -> Boolean = ::standartGOLSurviveRule,
    var neighborRule: List<Pair<Int, Int>> = standartGOLNeighbourhoodRule
) : Kernel() {

    lateinit var board: MutableList<Cell>

    //private val states : MutableList<Boolean>

    var size = width * height

    var fromIndex = 0
    var toIndex = size

    var epoch = 0

    fun wrapPos(pos: Int): Int {
        return if (pos < fromIndex) pos + size else if (pos >= fromIndex + size) pos - size else pos
    }

    inner class Cell(val pos: Int) {

        var alive: Boolean = DEAD

        var lastTimeAlive: Int = -1

        constructor(pos: Int, alive: Boolean) : this(pos) {
            this.alive = alive
        }

        constructor(pos: Int, alive: Boolean, lastTimeAlive: Int) : this(pos, alive) {
            this.lastTimeAlive = lastTimeAlive
        }

        fun countNeighbors(): Int {
            var count = 0
            for ((i, j) in neighborRule) {
                val nPos = wrapPos(this.pos - toIndex + fromIndex + i * width + j)
                if (board[nPos].alive == ALIVE)
                    count++

            }

            //println("x = ${pos % width}, y = ${pos / width}, count = $count")
            return count
        }

        fun gameRule(alive: Boolean, aliveNeighbours: Int): Boolean {
            return if (alive) surviveRule(aliveNeighbours) else spawnRule(aliveNeighbours)
        }

        fun calcNextState(): Boolean {
            val aliveNeighbours = this.countNeighbors()
            this.alive = gameRule(board[pos - toIndex + fromIndex].alive, aliveNeighbours)
            if (this.alive && board[pos - toIndex + fromIndex].alive)
                this.lastTimeAlive = board[pos - toIndex + fromIndex].lastTimeAlive
            else if (this.alive)
                this.lastTimeAlive = epoch + 1
            else
                this.lastTimeAlive = -1
            return alive
        }

        fun invert() {
            alive = !alive
            lastTimeAlive = if (alive) epoch else -1
        }

        fun getAge(): Int {
            return if (lastTimeAlive == -1) -1 else epoch - lastTimeAlive + 1
        }
    }

    fun makeBoard() {
        board = if (this.isRandomSeed)
            (0 until 2 * size).map { if (Math.random() < 0.5) Cell(it, ALIVE) else Cell(it, DEAD) }.toMutableList()
        else
            (0 until 2 * size).map { Cell(it) }.toMutableList()
    }

    fun loadBoard(boardData: List<String>) {
        board =
            (0 until 2 * size).map { Cell(it, boardData[it].split(";")[0] == "1", boardData[it].split(";")[1].toInt()) }
                .toMutableList()
    }

    fun setCellState(x: Int, y: Int, state: Boolean) {
        if (x < 0 || x >= width || y < 0 || y >= height) return
        board[y * width + x + fromIndex].alive = state
        board[y * width + x + toIndex].alive = state
    }

    init {
        makeBoard()
        //System.setProperty("com.aparapi.enableShowGeneratedOpenCL", "false")
    }

    fun getCell(x: Int, y: Int): Cell? {
        if (x < 0 || x >= width || y < 0 || y >= height) return null
        return board[y * width + x + fromIndex]
    }

    fun measureLifetime(cell: Cell?): Int? {
        if (cell == null) return null
        return cell.getAge()
    }

    fun swapStates() {
        val tmp = fromIndex
        fromIndex = toIndex
        toIndex = tmp
    }

    override fun run() {
        val gid: Int = getGlobalId()
        val to: Int = gid + toIndex
        board[to].calcNextState()
    }

    private fun calcNextState() {
        for (i in 0 until size)
            board[i + toIndex].calcNextState()
    }


    fun setNextState() {
        //executeFallbackAlgorithm(Range.create(size, 256), 0)
        //execute(Range.create(size))
        calcNextState()
        swapStates()
        epoch++
    }

    fun collectBoard(): Board {
        return board.subList(fromIndex, fromIndex + size)
    }

    fun invertCellState(x: Int, y: Int) {
        if (x < 0 || x >= width || y < 0 || y >= height) return
        board[y * width + x + fromIndex].invert()
    }

    fun makeSpawnRule(rule: (Int) -> Boolean) {
        spawnRule = rule
    }

    fun makeSurviveRule(rule: (Int) -> Boolean) {
        surviveRule = rule
    }

    fun makeNeighbourRule(rule: List<Pair<Int, Int>>) {
        neighborRule = rule
    }

}