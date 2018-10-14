package gameboard

import java.io.File

class Gameboard(inputField: String) {

    val field = mutableListOf<MutableList<Char>>()
    var stones = ArrayList<Point>()
        private set
    var lambdaStones = ArrayList<Point>()
        private set
    var trampolinesPoints = mutableMapOf<Char, Point>() // координаты трамплинов
        private set
    var trampolines = mutableMapOf<Char, Char>() // какой трамплин куда ведёт
        private set
    var robot = Point(-1, -1)
        private set
    val beards = mutableListOf<Point>()
    var score = 0
        private set
    var growth = 25 // количество шагов через которое вырастет борода
        private set
    var currentGrowth = 0
        private set
    var razors = 0
        private set
    var flooding = 0    // скорость прибывания воды (количество шагов через которое вода поднимается на 1 уровень)
        private set
    var waterLevel = 0   // уровень воды
        private set
    var waterproof = 10 // сколько шагов без выныривания можно пройти
        private set
    var currentWaterproof = 10
        private set
    var numberOfSteps = 0
        private set
    var collectedLambdas = 0
        private set
    var allLambdas = 0
        private set
    var lift = Point(-1, -1)
        private set

    init {
        val lineList = mutableListOf<String>()
        File(inputField).useLines { lines -> lines.forEach { lineList.add(it) } }
        var j = -1
        for (line in lineList) {
            if (line != "") j++
            else break
        }
        for (i in 0..j) field.add(mutableListOf())
        for (line in lineList) {
            if (j >= 0) {
                for ((k, char) in line.toCharArray().withIndex()) {
                    field[j].add(k, char)
                    when (char) {
                        Token.ROBOT.symbol -> robot.setNewPoint(j, k)
                        Token.STONE.symbol -> stones.add(Point(j, k))
                        Token.LAMBDA_STONE.symbol -> {
                            lambdaStones.add(Point(j, k))
                            allLambdas++
                        }
                        Token.BEARD.symbol -> beards.add(Point(j, k))
                        Token.LAMBDA.symbol -> allLambdas++
                        Token.CLOSED_LIFT.symbol -> lift.setNewPoint(j, k)
                        in 'A'..'I' -> trampolinesPoints[char] = Point(j, k)
                        in '1'..'9' -> trampolinesPoints[char] = Point(j, k)
                    }
                }
                j--
            } else { // наверно, это можно сделать покрасивее
                var matchResult = Regex("""Growth (\d+)""").find(line)
                if (matchResult != null) {
                    growth = matchResult.groupValues[1].toInt()
                } else {
                    matchResult = Regex("""Razors (\d+)""").find(line)
                    if (matchResult != null) {
                        razors = matchResult.groupValues[1].toInt()
                    } else {
                        matchResult = Regex("""Water (\d+)""").find(line)
                        if (matchResult != null) {
                            waterLevel = matchResult.groupValues[1].toInt()
                        } else {
                            matchResult = Regex("""Flooding (\d+)""").find(line)
                            if (matchResult != null) {
                                flooding = matchResult.groupValues[1].toInt()
                            } else {
                                matchResult = Regex("""Waterproof (\d+)""").find(line)
                                if (matchResult != null) {
                                    waterproof = matchResult.groupValues[1].toInt()
                                    currentWaterproof = waterproof
                                } else {
                                    matchResult = Regex("""Trampoline ([A-I]) targets (\d)""").find(line)
                                    if (matchResult != null) {
                                        trampolines[matchResult.groupValues[1][0]] = matchResult.groupValues[2][0]
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
    }

    enum class Token(val symbol: Char) {
        ROBOT('R'),
        LAMBDA('\\'),
        STONE('*'),
        LAMBDA_STONE('@'),
        EARTH('.'),
        EMPTY(' '),
        WALL('#'),
        BEARD('W'),
        RAZOR('!'),
        CLOSED_LIFT('L'),
        OPENED_LIFT('O');
    }

    fun isPassable(sign: Char): Boolean {
        return sign == Token.LAMBDA.symbol || sign == Token.EARTH.symbol || sign == Token.EMPTY.symbol || sign == Token.RAZOR.symbol || sign == Token.OPENED_LIFT.symbol
    }

    fun isStoneOrLambdaStone(sign: Char): Boolean {
        return sign == Token.STONE.symbol || sign == Token.LAMBDA_STONE.symbol
    }

    enum class State {
        LIVE,
        ABORTED,
        WON,
        DEAD;
    }

    enum class Move(val y: Int, val x: Int) {
        RIGHT(0, 1),
        LEFT(0, -1),
        UP(1, 0),
        DOWN(-1, 0);
    }

    var state: State = State.LIVE
        private set

    fun act(commands: String) {
        for (command in commands) {
            if (state == State.LIVE) {
                when (command) {
                    'R' -> go(Move.RIGHT)
                    'L' -> go(Move.LEFT)
                    'U' -> go(Move.UP)
                    'D' -> go(Move.DOWN)
                    'S' -> shave()
                    'A' -> abort()
                    'W' -> waiting()
                }
            }
        }
    }

    private fun go(move: Move) {
        if (currentWaterproof >= 0) {
            val yPoint = robot.y + move.y
            val xPoint = robot.x + move.x
            if (isPassable(field[yPoint][xPoint]) &&
                    field[yPoint][xPoint] !in '1'..'9' ||
                    field[yPoint][xPoint] in 'A'..'I' ||
                    isStoneOrLambdaStone(field[yPoint][xPoint]) && field[yPoint][xPoint + move.x] == Token.EMPTY.symbol) { // если след координата робота НЕ стена, НЕ борода, НЕ закрытый лифт, НЕ выход трамплина
                when (field[yPoint][xPoint]) {
                    Token.EARTH.symbol -> updateRobot(yPoint, xPoint) // земля
                    Token.LAMBDA.symbol -> { // лямбда
                        updateRobot(yPoint, xPoint)
                        score += 25
                        collectedLambdas++
                    }
                    Token.RAZOR.symbol -> { // бритва
                        updateRobot(yPoint, xPoint)
                        razors++
                    }
                    Token.OPENED_LIFT.symbol -> { // открытый лифт
                        field[robot.y][robot.x] = Token.EMPTY.symbol
                        robot.setNewPoint(yPoint, xPoint)
                        state = State.WON
                    }
                    Token.STONE.symbol -> pushing(move, stones, stones.indexOf(Point(yPoint, xPoint))) // двигаем камни
                    Token.LAMBDA_STONE.symbol -> pushing(move, lambdaStones, lambdaStones.indexOf(Point(yPoint, xPoint)))
                    in 'A'..'I' -> { // трамплин
                        val char = field[yPoint][xPoint]
                        val robotY = trampolinesPoints[trampolines[char]]!!.y
                        val robotX = trampolinesPoints[trampolines[char]]!!.x
                        val trampolinesToRemove = mutableListOf<Char>()
                        field[trampolinesPoints[trampolines[char]]!!.y][trampolinesPoints[trampolines[char]]!!.x] = Token.EMPTY.symbol
                        for (entry in trampolines.entries) {
                            if (entry.value == trampolines[char]) {
                                trampolinesToRemove.add(entry.key)
                                field[trampolinesPoints[entry.key]!!.y][trampolinesPoints[entry.key]!!.x] = Token.EMPTY.symbol
                            }
                        }
                        for (trampolineToRemove in trampolinesToRemove) {
                            trampolines.remove(trampolineToRemove)
                        }
                        updateRobot(robotY, robotX)
                    }
                    else -> updateRobot(yPoint, xPoint)
                }
            }
            else { return } // сломать метод чтобы не считать индикаторы
        }
        updateIndicators()
    }

    private fun growBeard() {
        val temporaryBeards = mutableListOf<Point>()
        for (beard in beards) {
            for (y in (beard.y - 1)..(beard.y + 1))
                for (x in (beard.x - 1)..(beard.x + 1))
                    if (field[y][x] == Token.EMPTY.symbol) {
                        temporaryBeards.add(Point(y, x))
                        field[y][x] = Token.BEARD.symbol
                    }
        }
        beards.addAll(temporaryBeards)
    }

    private fun shave() {
        updateIndicators()
        if (razors > 0) {
            razors--
            for (y in (robot.y - 1)..(robot.y + 1))
                for (x in (robot.x - 1)..(robot.x + 1)) {
                    if (field[y][x] == Token.BEARD.symbol) {
                        beards.remove(Point(y, x))
                        field[y][x] = Token.EMPTY.symbol
                    }
                }
        }
    }

    private fun waiting() = updateIndicators()

    private fun abort() {
        state = State.ABORTED
        score += 25 * collectedLambdas
        gameover()
    }

    private fun updateIndicators() {
        numberOfSteps++
        score--
        currentGrowth++
        falling()
        if (currentGrowth == growth) {
            growBeard()
            currentGrowth = 0
        }
        if (waterLevel >= robot.y + 1) { // если робот в воде, уменьшаем вотерпруф
            currentWaterproof--
        }
        if (waterLevel < robot.y + 1) { // если вынырнул - обновляем
            currentWaterproof = waterproof
        }
        if (currentWaterproof < 0) state = State.DEAD
        if (flooding > 0 && numberOfSteps == flooding) { // если установлена скорость прибывания воды и
            // количество шагов равно ей, тогда поднять воду на 1 лвл и обнулить кол-во шагов
            waterLevel++
            numberOfSteps = 0
        }
        if (collectedLambdas == allLambdas) field[lift.y][lift.x] = Token.OPENED_LIFT.symbol
        if (state == State.DEAD) gameover()
        if (state == State.WON) {
            score += 50 * collectedLambdas
            gameover()
        }
    }

    private fun updateRobot(newY: Int, newX: Int) {
        field[newY][newX] = Token.ROBOT.symbol
        field[robot.y][robot.x] = Token.EMPTY.symbol
        robot.setNewPoint(newY, newX)
    }

    private fun falling() { // chtobi dvigat kamni nado dumat kak kamen
        for (i in 0 until stones.size) {
            val currentStone = stones.get(i)
            val cellUnderStone = field[currentStone.y - 1][currentStone.x]
            when (cellUnderStone) {
                Token.EMPTY.symbol -> {
                    field[currentStone.y][currentStone.x] = Token.EMPTY.symbol
                    stones.set(i, Point(currentStone.y - 1, currentStone.x))
                    if (Point(currentStone.y - 2, currentStone.x) == robot) state = State.DEAD
                }
                Token.LAMBDA.symbol -> {
                    if (field[currentStone.y][currentStone.x + 1] == Token.EMPTY.symbol && field[currentStone.y - 1][currentStone.x + 1] == Token.EMPTY.symbol) {
                        field[currentStone.y][currentStone.x] = Token.EMPTY.symbol
                        stones.set(i, Point(currentStone.y - 1, currentStone.x + 1))
                        if (Point(currentStone.y - 2, currentStone.x + 1) == robot) state = State.DEAD
                    }
                }
                Token.STONE.symbol, Token.LAMBDA_STONE.symbol -> {
                    if (field[currentStone.y][currentStone.x + 1] == Token.EMPTY.symbol && field[currentStone.y - 1][currentStone.x + 1] == Token.EMPTY.symbol) {
                        field[currentStone.y][currentStone.x] = Token.EMPTY.symbol
                        stones.set(i, Point(currentStone.y - 1, currentStone.x + 1))
                        if (Point(currentStone.y - 2, currentStone.x + 1) == robot) state = State.DEAD
                    } else if (field[currentStone.y][currentStone.x - 1] == Token.EMPTY.symbol && field[currentStone.y - 1][currentStone.x - 1] == Token.EMPTY.symbol) {
                        field[currentStone.y][currentStone.x] = Token.EMPTY.symbol
                        stones.set(i, Point(currentStone.y - 1, currentStone.x - 1))
                        if (Point(currentStone.y - 2, currentStone.x - 1) == robot) state = State.DEAD
                    }

                }
            }
        }
        val lambdas = arrayListOf<Point>()
        for (i in 0 until lambdaStones.size) {
            val currentStone = lambdaStones.get(i)
            val cellUnderStone = field[currentStone.y - 1][currentStone.x]
            when (cellUnderStone) {
                Token.EMPTY.symbol -> {
                    if (field[currentStone.y - 2][currentStone.x] != Token.EMPTY.symbol) { // разбиение
                        field[currentStone.y][currentStone.x] = Token.EMPTY.symbol
                        lambdas.add(Point(currentStone.y - 1, currentStone.x))
                        lambdaStones.set(i, Point(field.size - 1, 0)) //поставить в верхний левый угол
                        if (Point(currentStone.y - 2, currentStone.x) == robot) state = State.DEAD
                    } else { // падение
                        field[currentStone.y][currentStone.x] = Token.EMPTY.symbol
                        lambdaStones.set(i, Point(currentStone.y - 1, currentStone.x))
                    }
                }
                Token.LAMBDA.symbol -> {
                    if (field[currentStone.y][currentStone.x + 1] == Token.EMPTY.symbol && field[currentStone.y - 1][currentStone.x + 1] == Token.EMPTY.symbol) {
                        field[currentStone.y][currentStone.x] = Token.EMPTY.symbol
                        if (field[currentStone.y - 2][currentStone.x + 1] != Token.EMPTY.symbol) { // разбиение
                            lambdas.add(Point(currentStone.y - 1, currentStone.x + 1))
                            lambdaStones.set(i, Point(field.size - 1, 0)) //поставить в верхний левый угол
                            if (Point(currentStone.y - 2, currentStone.x + 1) == robot) state = State.DEAD
                        } else lambdaStones.set(i, Point(currentStone.y - 1, currentStone.x + 1))
                    }
                }
                Token.STONE.symbol, Token.LAMBDA_STONE.symbol -> {
                    if (field[currentStone.y][currentStone.x + 1] == Token.EMPTY.symbol && field[currentStone.y - 1][currentStone.x + 1] == Token.EMPTY.symbol) {
                        field[currentStone.y][currentStone.x] = Token.EMPTY.symbol
                        if (field[currentStone.y - 2][currentStone.x + 1] != Token.EMPTY.symbol) { // разбиение
                            lambdas.add(Point(currentStone.y - 1, currentStone.x + 1))
                            lambdaStones.set(i, Point(field.size - 1, 0)) //поставить в верхний левый угол
                            if (Point(currentStone.y - 2, currentStone.x + 1) == robot) state = State.DEAD
                        } else lambdaStones.set(i, Point(currentStone.y - 1, currentStone.x + 1))
                    } else if (field[currentStone.y][currentStone.x - 1] == Token.EMPTY.symbol && field[currentStone.y - 1][currentStone.x - 1] == Token.EMPTY.symbol) {
                        field[currentStone.y][currentStone.x] = Token.EMPTY.symbol
                        if (field[currentStone.y - 2][currentStone.x - 1] != Token.EMPTY.symbol) { // разбиение
                            lambdas.add(Point(currentStone.y - 1, currentStone.x - 1))
                            lambdaStones.set(i, Point(field.size - 1, 0)) //поставить в верхний левый угол
                            if (Point(currentStone.y - 2, currentStone.x - 1) == robot) state = State.DEAD
                        } else lambdaStones.set(i, Point(currentStone.y - 1, currentStone.x - 1))
                    }
                }
            }
        }
        for (lambda in lambdas) {
            field[lambda.y][lambda.x] = Token.LAMBDA.symbol
        }
        lambdaStones = ArrayList(lambdaStones.toSet()) // удаление одинаковых камней
        for (lambdaStone in lambdaStones) {
            field[lambdaStone.y][lambdaStone.x] = Token.LAMBDA_STONE.symbol
        }
        stones = ArrayList(stones.toSet()) // удаление одинаковых камней
        for (stone in stones) {
            field[stone.y][stone.x] = Token.STONE.symbol
        }
    }

    private fun pushing(move: Move, array: ArrayList<Point>, i: Int) {
        val yPoint = robot.y + move.y
        val xPoint = robot.x + move.x
        val newXPoint = robot.x + 2 * move.x
        if (field[yPoint][newXPoint] == Token.EMPTY.symbol && (move == Move.RIGHT || move == Move.LEFT)) {
            val oldPoint = Point(yPoint, xPoint)
            val newPoint = Point(yPoint, newXPoint)
            field[oldPoint.y][oldPoint.x] = Token.EMPTY.symbol
            array.set(i, newPoint)
            if (array == stones) field[yPoint][newXPoint] = Token.STONE.symbol // появление камня на новой позиции
            else field[yPoint][newXPoint] = Token.LAMBDA_STONE.symbol
            updateRobot(yPoint, xPoint)
        } else {
        }
    }

    private fun gameover() {
        stones.clear()
        lambdaStones.clear()
        //println(score)
    }

    fun getListOfLambdas(): MutableList<Point> {
        val listOfLambdas = mutableListOf<Point>()
        for (i in 0..(field.size - 1))
            for (j in 0..(field[i].size - 1))
                if (field[i][j] == Token.LAMBDA.symbol) listOfLambdas.add(Point(i, j))
        return listOfLambdas
    }

    fun printField() {
        for (i in (field.size - 1) downTo 0) {
            for (j in 0..(field[i].size - 1))
                print(field[i][j])
            println()
        }
        println("Score: $score")
        println("State: $state")
    }
}

data class Point(var y: Int, var x: Int) {
    fun setNewPoint(newY: Int, newX: Int) {
        y = newY
        x = newX
    }
}
