import boxes.Box
import boxes.ButtonNeighborRuleMaster
import boxes.ButtonSpawnRuleMaster
import boxes.ButtonSurviveRuleMaster
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter


class SaveMaster(private val game: GameOfLife, private val grid: Grid, private val settings: GameSettings) {

    var neighborRuleStates: List<Pair<Int, Int>>
        get() = (Box.boxes[settings.neighbourButtonID] as ButtonNeighborRuleMaster).getStates()
        set(value) {
            (Box.boxes[settings.neighbourButtonID] as ButtonNeighborRuleMaster).setStates(value)
        }

    var spawnRuleStates: List<Int>
        get() = (Box.boxes[settings.spawnButtonID] as ButtonSpawnRuleMaster).getStates()
        set(value) {
            (Box.boxes[settings.spawnButtonID] as ButtonSpawnRuleMaster).setStates(value)
        }

    var surviveRuleStates: List<Int>
        get() = (Box.boxes[settings.surviveButtonID] as ButtonSurviveRuleMaster).getStates()
        set(value) {
            (Box.boxes[settings.surviveButtonID] as ButtonSurviveRuleMaster).setStates(value)
        }

    var board: MutableList<GameOfLife.Cell>
        get() = game.board
        set(value) {
            game.board = value
        }

    fun deleteLastSave() {
        File("last_save.txt").writeText("!no saves")
    }

    init {
        if (!File("last_save.txt").isFile)
            deleteLastSave()
    }

    val separator = StringBuilder(":")
    fun getSave(): String {
        //return (encryptNeighborRule() + separator + encryptSpawnRule() + separator + encryptSurviveRule() + separator + encryptBoard()).toString()
        val result = StringBuilder()
        result.append(encryptNeighborRule())
        result.append(separator)
        result.append(encryptSpawnRule())
        result.append(separator)
        result.append(encryptSurviveRule())
        result.append(separator)
        result.append(encryptBoard())
        result.append(separator)
        result.append(encryptCameraCords())
        result.append(separator)
        result.append(encryptBoardHWToFrom())
        return result.toString()
    }

    fun encryptNeighborRule(): StringBuilder {
        val result = StringBuilder()
        for (i in neighborRuleStates) {
            result.append(i.first.toString() + "," + i.second.toString() + ";")
        }
        return result
    }

    fun encryptSpawnRule(): StringBuilder {
        val result = StringBuilder()
        for (i in spawnRuleStates) {
            result.append(i.toString() + ",")
        }
        return result
    }

    fun encryptSurviveRule(): StringBuilder {
        val result = StringBuilder()
        for (i in surviveRuleStates) {
            result.append(i.toString() + ",")
        }
        return result
    }

    fun encryptBoard(): StringBuilder {
        val result = StringBuilder()
        for (i in board) {
            result.append((if (i.alive) "1" else "0") + ";" + i.lastTimeAlive + ",")
        }
        return result
    }

    fun encryptCameraCords(): StringBuilder {
        val result = StringBuilder()
        result.append(grid.cameraXMove)
        result.append(",")
        result.append(grid.cameraYMove)
        result.append(",")
        result.append(grid.scale)
        return result
    }

    fun encryptBoardHWToFrom(): StringBuilder {
        val result = StringBuilder()
        result.append(grid.boardWidth)
        result.append(",")
        result.append(grid.boardHeight)
        result.append(",")
        result.append(game.toIndex)
        result.append(",")
        result.append(game.fromIndex)
        return result
    }

    fun save(): Boolean {
        val saveFile = saveDirectorySelectionMenu() ?: return false
        saveLast(saveFile.toString())

        println("Saving to ${saveFile.name}")
        if (saveFile.name.endsWith(".png")) {
            saveAsImage(saveFile)

            return true
        } else {
            saveFile.writeText(getSave())
            return true
        }
    }

    private fun saveLast(filename: String) {
        File("last_save.txt").writeText(filename)
    }

    fun loadLast() {
        val filename = File("last_save.txt").readText()
        val file = File(filename)
        if (!file.isFile) return

        load(file)
    }

    private fun saveDirectorySelectionMenu(): File? {
        val fileChooser = JFileChooser()
        fileChooser.currentDirectory = File("saves/")

        val pngFilter = FileNameExtensionFilter("Image save", "gol")
        val golFilter = FileNameExtensionFilter("Game of Life save", ".gol")
        fileChooser.addChoosableFileFilter(pngFilter)
        fileChooser.addChoosableFileFilter(golFilter)

        fileChooser.removeChoosableFileFilter(fileChooser.acceptAllFileFilter)
        fileChooser.fileSelectionMode = JFileChooser.APPROVE_OPTION
        fileChooser.showOpenDialog(null)



        if (fileChooser.selectedFile == null)
            return null

        val path: String = fileChooser.selectedFile.absolutePath +
                when (fileChooser.fileFilter) {
                    pngFilter -> ".png"
                    golFilter -> ".gol"
                    else -> ""
                }

        val name: String = fileChooser.selectedFile.name +
                when (fileChooser.fileFilter) {
                    pngFilter -> ".png"
                    golFilter -> ".gol"
                    else -> ""
                }

        if (!name.endsWith(".png") && !name.endsWith(".gol")) {
            println("Invalid file name: $name")
            return null
        }

        if (name.contains("[|/*:\"<>\\\\]".toRegex())) {
            println("Invalid file name: $name")
            return null
        }

        return File(path)

    }

    fun loadFileSelectionMenu(): File? {
        val fileChooser = JFileChooser()
        fileChooser.currentDirectory = File("saves/")
        fileChooser.fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES
        fileChooser.showOpenDialog(null)
        if (fileChooser.selectedFile == null || !fileChooser.selectedFile.isFile)
            return null
        return fileChooser.selectedFile
    }

    fun load(loadFile: File? = null) {
        val file = loadFile ?: (loadFileSelectionMenu() ?: return)

        when {
            file.name.endsWith(".png") -> loadFromImage(file)
            file.name.endsWith(".gol") -> loadFromGOL(file)
            else -> println("Invalid file type: ${file.name}")
        }

    }

    fun loadFromGOL(file: File) {

        val save = file.readText()
        val saveList = save.split(separator.toString())
        val neighborRule = saveList[0].split(";")
        val spawnRule = saveList[1].split(",")
        val surviveRule = saveList[2].split(",")
        val saveBoard = saveList[3].split(",")
        val cameraCords = saveList[4].split(",")
        val boardHW = saveList[5].split(",")

        grid.boardWidth = boardHW[0].toInt()
        grid.boardHeight = boardHW[1].toInt()
        game.width = grid.boardWidth
        game.height = grid.boardHeight
        game.size = grid.boardWidth * grid.boardHeight
        game.toIndex = boardHW[2].toInt()
        game.fromIndex = boardHW[3].toInt()

        val neighborRuleList = mutableListOf<Pair<Int, Int>>()
        for (i in neighborRule) {
            if (i == "") continue
            val pair = i.split(",")
            neighborRuleList.add(Pair(pair[0].toInt(), pair[1].toInt()))
        }
        neighborRuleStates = neighborRuleList
        val spawnRuleList = mutableListOf<Int>()
        for (i in spawnRule) {
            if (i == "") continue
            spawnRuleList.add(i.toInt())
        }
        spawnRuleStates = spawnRuleList
        val surviveRuleList = mutableListOf<Int>()
        for (i in surviveRule) {
            if (i == "") continue
            surviveRuleList.add(i.toInt())
        }
        surviveRuleStates = surviveRuleList

        game.loadBoard(saveBoard)

        grid.cameraXMove = cameraCords[0].toFloat()
        grid.cameraYMove = cameraCords[1].toFloat()
        grid.scale = cameraCords[2].toFloat()
    }


    var cellPixelSize: Int = 5
    fun saveAsImage(outputFile: File) {

        val width = grid.boardWidth * cellPixelSize
        val height = grid.boardHeight * cellPixelSize

        val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)

        val graphics = bufferedImage.createGraphics()

        graphics.color = Color.BLACK
        graphics.fillRect(0, 0, width, height)

        for (i in 0 until grid.boardWidth) {
            for (j in 0 until grid.boardHeight) {
                val cell = game.board[j * grid.boardWidth + i + game.fromIndex]
                if (cell.alive) {
                    graphics.color = Color.WHITE
                    graphics.fillRect(i * cellPixelSize, j * cellPixelSize, cellPixelSize, cellPixelSize)
                }
            }
        }

        graphics.dispose()

        try {
            ImageIO.write(bufferedImage, "png", outputFile)
            println("Image saved successfully.")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun loadFromImage(file: File) {
        val image = ImageIO.read(file)
        println(file.path)
        val width = image.width / cellPixelSize
        val height = image.height / cellPixelSize

        grid.boardWidth = width
        grid.boardHeight = height
        game.width = grid.boardWidth
        game.height = grid.boardHeight
        game.size = grid.boardWidth * grid.boardHeight

        game.toIndex = 0
        game.fromIndex = game.size
        game.makeBoard()

        for (i in 0 until width) {
            for (j in 0 until height) {
                val color = Color(image.getRGB(i * cellPixelSize, j * cellPixelSize))
                if (color == Color.WHITE)
                    game.setCellState(i, j, ALIVE)
                else
                    game.setCellState(i, j, DEAD)
            }
        }
    }
}