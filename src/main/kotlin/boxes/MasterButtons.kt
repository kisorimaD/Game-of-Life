package boxes

import crossButtonOnHoldImage
import crossButtonRegularImage
import game
import nullButtonOnHoldImage
import nullButtonRegularImage
import org.jetbrains.skija.Image


class ButtonNeighborRuleMaster : ActionImageButton {
    constructor() : super()

    private val buttonsList = mutableListOf<Pair<Pair<Int, Int>, Int>>()

    public var initStates: List<Pair<Int, Int>> = listOf()

    constructor(
        image: Image, imageOnHold: Image, x: Float, y: Float, width: Float, height: Float,
        initStates: List<Pair<Int, Int>>
    ) : super(image, imageOnHold, x, y, width / 3, height / 3, {}) {
        this.initStates = initStates
    }

    fun getRule() = buttonsList.map { it.first to (boxes[it.second] as StateButton) }
        .filter { it.second.state == it.first in initStates }.map { it.first }

    fun setRule() {
        game.makeNeighbourRule(getRule())
    }

    fun getStates(): List<Pair<Int, Int>> {
        return buttonsList.mapIndexed { index, i -> i.first to (boxes[i.second] as StateButton).state }
            .filter { it.second }.map { it.first }
    }

    fun setStates(states: List<Pair<Int, Int>>) {

        for (i in buttonsList.indices) {
            if (buttonsList[i].first in states)
                (boxes[buttonsList[i].second] as StateButton).loadState(true)
            else
                (boxes[buttonsList[i].second] as StateButton).loadState(false)
        }
        setRule()
    }


    init {
        buttonFunc = ::setRule

        val noRegularImage = nullButtonRegularImage
        val noOnHoldImage = nullButtonOnHoldImage

        val yesRegularImage = crossButtonRegularImage
        val yesOnHoldImage = crossButtonOnHoldImage

        for (i in -1..1) {
            for (j in -1..1) {
                if (i == 0 && j == 0) continue
                buttonsList.add(
                    (j to i) to StateButton(
                        noRegularImage,
                        noOnHoldImage,
                        yesRegularImage,
                        yesOnHoldImage,
                        x + i * width,
                        y + j * height,
                        width,
                        height,
                        initialState = true
                    ).itemNumber
                )
            }
        }
    }
}

class ButtonSpawnRuleMaster : ActionImageButton {
    constructor() : super()

    private val buttonsList = mutableListOf<Int>()

    var initStates = listOf(3)

    constructor(
        image: Image, imageOnHold: Image, x: Float, y: Float, width: Float, height: Float,
        initStates: List<Int>
    ) : super(image, imageOnHold, x + 9 * width / 20, y, width / 10, height, {}) {
        this.initStates = initStates
    }

    fun calculateRule() {
        val ruleList = getRuleList()
        game.makeSpawnRule { aliveNeighbours: Int -> aliveNeighbours in ruleList }

    }

    fun getRuleList(): List<Int> {
        return buttonsList.map { (boxes[it] as StateButton).state }.mapIndexed { index, i -> index to i }
            .filter { it.second }.map { it.first }
    }

    fun getStates(): List<Int> {
        return buttonsList.mapIndexed { index, i -> index to (boxes[i] as StateButton).state }.filter { it.second }
            .map { it.first }
    }

    fun setStates(states: List<Int>) {
        for (i in buttonsList.indices) {
            if (i in states)
                (boxes[buttonsList[i]] as StateButton).loadState(true)
            else
                (boxes[buttonsList[i]] as StateButton).loadState(false)
        }
        calculateRule()
    }


    init {
        buttonFunc = ::calculateRule

        val noRegularImage = nullButtonRegularImage
        val noOnHoldImage = nullButtonOnHoldImage

        val yesRegularImage = crossButtonRegularImage
        val yesOnHoldImage = crossButtonOnHoldImage

        for (i in -9..-1) {
            buttonsList.add(
                StateButton(
                    noRegularImage,
                    noOnHoldImage,
                    yesRegularImage,
                    yesOnHoldImage,
                    x + i * width - width / 2,
                    y,
                    width,
                    height,
                    initialState = ((9 + i) in initStates)
                ).itemNumber
            )
        }
    }
}


class ButtonSurviveRuleMaster : ActionImageButton {
    constructor() : super()

    private val buttonsList = mutableListOf<Int>()

    var initStates = listOf(2, 3)

    constructor(
        image: Image, imageOnHold: Image, x: Float, y: Float, width: Float, height: Float,
        initStates: List<Int>
    ) : super(image, imageOnHold, x + 9 * width / 20, y, width / 10, height, {}) {
        this.initStates = initStates
    }

    fun getRuleList(): List<Int> {
        return buttonsList.map { (boxes[it] as StateButton).state }.mapIndexed { index, i -> index to i }
            .filter { it.second }.map { it.first }
    }

    fun getStates(): List<Int> {
        return buttonsList.mapIndexed { index, i -> index to (boxes[i] as StateButton).state }.filter { it.second }
            .map { it.first }
    }

    fun setStates(states: List<Int>) {
        for (i in buttonsList.indices) {
            if (i in states)
                (boxes[buttonsList[i]] as StateButton).loadState(true)
            else
                (boxes[buttonsList[i]] as StateButton).loadState(false)
        }
        calculateRule()
    }

    fun calculateRule() {
        val ruleList = getRuleList()
        game.makeSurviveRule { aliveNeighbours: Int -> aliveNeighbours in ruleList }

    }


    init {
        buttonFunc = ::calculateRule

        val noRegularImage = nullButtonRegularImage
        val noOnHoldImage = nullButtonOnHoldImage

        val yesRegularImage = crossButtonRegularImage
        val yesOnHoldImage = crossButtonOnHoldImage

        for (i in -9..-1) {
            buttonsList.add(
                StateButton(
                    noRegularImage,
                    noOnHoldImage,
                    yesRegularImage,
                    yesOnHoldImage,
                    x + i * width - width / 2,
                    y,
                    width,
                    height,
                    initialState = ((9 + i) in initStates)
                ).itemNumber
            )
        }
    }
}