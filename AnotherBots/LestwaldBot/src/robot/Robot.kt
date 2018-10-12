package robot

import gameboard.Gameboard
import gameboard.Point

class Robot(inputField: String) {

    private var gameboard = Gameboard(inputField)
    private var field = gameboard.field
    private var oldField = gameboard.field
    private var robot = gameboard.robot
    private var score = gameboard.score
    private val lift = gameboard.lift
    private var listOfLambdas = gameboard.getListOfLambdas()
    private var listOfUnreachableLambdas = mutableListOf<Point>()
    private val listOfChanges = mutableListOf<Change>()
    private var globalPath = ""
    private var fieldHeight = 0

    init {
        for (k in 0..(field.size - 1))
            if (field[k].size > fieldHeight) fieldHeight = field[k].size
    }

    private fun findPathToPoint(points: List<Point>): Pair<Point, String> {
        val distances = Array(field.size) { Array(fieldHeight) { Int.MAX_VALUE } }
        distances[robot.y][robot.x] = 0
        for (point in points) {
            distances[point.y][point.x] = -1
        }
        val waveFront = mutableListOf(robot)
        var path = ""
        var distance = 0
        var y: Int
        var x: Int
        var isPointReached = false
        var nearestPoint = Point(-1, -1)
        while (!isPointReached) {
            val temporaryPoints = mutableListOf<Point>()
            for (currentPoint in waveFront) {
                y = currentPoint.y
                x = currentPoint.x
                for (i in (y - 1)..(y + 1))
                    for (j in (x - 1)..(x + 1))
                        if (i == y || j == x) {
                            if (distances[i][j] == Int.MAX_VALUE && gameboard.isPassable(field[i][j])) {
                                distances[i][j] = distance + 1
                                temporaryPoints.add(Point(i, j))
                            }
                            if (distances[i][j] == -1) {
                                distances[i][j] = distance + 1
                                isPointReached = true
                                nearestPoint = Point(i, j)
                            }
                        }
            }
            waveFront.clear()
            waveFront.addAll(temporaryPoints)
            distance++
            if (distance > (field.size * fieldHeight)) return Pair(points[0], "NOPATH")
        }

        var isPathBuilt = false
        y = nearestPoint.y
        x = nearestPoint.x
        while (!isPathBuilt) {
            when {
                y > 0 && distances[y - 1][x] == distance - 1 -> {
                    y--
                    path = "U$path"
                }
                y < field.size && distances[y + 1][x] == distance - 1 -> {
                    y++
                    path = "D$path"
                }
                x > 0 && distances[y][x - 1] == distance - 1 -> {
                    x--
                    path = "R$path"
                }
                x < distances[y].size && distances[y][x + 1] == distance - 1 -> {
                    x++
                    path = "L$path"
                }
            }
            distance--
            if (distance == 0) isPathBuilt = true
        }
        return Pair(nearestPoint, path)
    }

    private fun updateField() {
        for (i in 0..(field.size - 1))
            for (j in 0..(field[i].size - 1))
                oldField[i][j] = field[i][j]
        field = gameboard.field
        robot = gameboard.robot
        score = gameboard.score
        listOfLambdas = gameboard.getListOfLambdas()
        listOfLambdas.removeAll(listOfUnreachableLambdas)
        listOfChanges.add(Change(globalPath, score, getChangedPoints()))
    }

    private fun goBackTo(step: Int) {
        if (listOfChanges.size > step) {
            val temp = listOfChanges.subList(step + 1, listOfChanges.size)
            listOfChanges.removeAll(temp)
            globalPath = listOfChanges[step].path
        } else globalPath = ""
    }

    private fun goBackToMaxScore() {
        var step = 0
        var maxScore = 0
        for (k in 0 until listOfChanges.size) {
            if (listOfChanges[k].score > maxScore) {
                maxScore = listOfChanges[k].score
                step = k
            }
        }
        goBackTo(step)
    }

    private fun canMakeMove(): String {
        var result = ""
        if (gameboard.isPassable(field[robot.y][robot.x + 1]) || gameboard.isStoneOrLambdaStone(field[robot.y][robot.x + 1]) && field[robot.y][robot.x + 2] == ' ')
            result += 'R'
        if (gameboard.isPassable(field[robot.y][robot.x - 1]) || gameboard.isStoneOrLambdaStone(field[robot.y][robot.x - 1]) && field[robot.y][robot.x - 2] == ' ')
            result += 'L'
        if (gameboard.isPassable(field[robot.y + 1][robot.x]))
            result += 'U'
        if (gameboard.isPassable(field[robot.y - 1][robot.x]))
            result += 'D'
        return result
    }

    private fun isRobotBlocked() = canMakeMove() == ""

    private fun isLiftBlocked() = findPathToPoint(listOf(lift)).second == "NOPATH"

    private fun getChangedPoints(): MutableMap<Point, Char> {
        val result = mutableMapOf<Point, Char>()
        for (i in 0..(field.size - 1))
            for (j in 0..(field[i].size - 1))
                if (field[i][j] != oldField[i][j]) {
                    result.put(Point(i, j), field[i][j])
                }
        return result
    }

    private fun isUnderStone() = field[robot.y + 1][robot.x] == '*'

    private fun isRightUnderStone() = (field[robot.y + 1][robot.x - 1] == '*' && (field[robot.y][robot.x - 1] == '*' || field[robot.y][robot.x - 1] == '\\'))

    private fun isLeftUnderStone() = (field[robot.y + 1][robot.x + 1] == '*' && field[robot.y][robot.x + 1] == '*')

    private fun isLambdaUnreachable(lambda: Point): Boolean {
        val x = lambda.x
        val y = lambda.y
        return (field[y + 1][x] == '*' && !(gameboard.isPassable(field[y][x + 1]) || field[y][x + 1] == 'R')
                && !(gameboard.isPassable(field[y][x - 1]) || field[y][x - 1] == 'R'))
    }

    fun go(): String {
        while (true) {
            val result = if (listOfLambdas.isEmpty() && listOfUnreachableLambdas.isEmpty()) findPathToPoint(listOf(lift)) else findPathToPoint(listOfLambdas)

            val currentPath = result.second
            val nearestPoint = result.first

            if (currentPath == "NOPATH" || isLambdaUnreachable(nearestPoint) && nearestPoint != lift) {
                listOfUnreachableLambdas.add(nearestPoint)
                listOfLambdas.remove(nearestPoint)
                if (!listOfLambdas.isEmpty()) continue
            }

            val numberOfLambdas = listOfLambdas.size
            var numberOfLambdas1: Int
            for (move in currentPath) {
                if (canMakeMove().contains(move)) {
                    if (isUnderStone() && move == 'D') {
                        if (field[robot.y + 1][robot.x + 1] == '*' && canMakeMove().contains('L')) {
                                gameboard.act("L")
                                globalPath += 'L'
                                updateField()
                                break
                            } else if (field[robot.y + 1][robot.x - 1] == '*' && canMakeMove().contains('R')) {
                                gameboard.act("R")
                                globalPath += 'R'
                                updateField()
                                break
                            } else if (canMakeMove().contains('R')) {
                                gameboard.act("R")
                                globalPath += 'R'
                                updateField()
                                break
                            } else if (canMakeMove().contains('L')) {
                                gameboard.act("L")
                                globalPath += 'L'
                                updateField()
                                break
                            } else {
                                goBackToMaxScore()
                                globalPath += 'A'
                                return globalPath
                            }
                        } else if (isLeftUnderStone() && move == 'D') {
                        if (canMakeMove().contains('U')) {
                            gameboard.act("U")
                            globalPath += 'U'
                            updateField()
                            if (canMakeMove().contains('L')) {
                                gameboard.act("L")
                                globalPath += 'L'
                                updateField()
                            } else if (canMakeMove().contains('U')) {
                                gameboard.act("U")
                                globalPath += 'U'
                                updateField()
                            } else {
                                goBackToMaxScore()
                                globalPath += 'A'
                                return globalPath
                            }
                            break
                        }
                    } else if (isRightUnderStone() && move == 'D') {
                        if (canMakeMove().contains('U')) {
                            gameboard.act("U")
                            globalPath += 'U'
                            updateField()
                            if (canMakeMove().contains('R')) {
                                gameboard.act("R")
                                globalPath += 'R'
                                updateField()
                            } else if (canMakeMove().contains('U')) {
                                gameboard.act("U")
                                globalPath += 'U'
                                updateField()
                            } else {
                                goBackToMaxScore()
                                globalPath += 'A'
                                return globalPath
                            }
                            break
                        }
                    } else {
                        gameboard.act(move.toString())
                        globalPath += move
                    }
                    updateField()
                } else break
                numberOfLambdas1 = listOfLambdas.size
                if (numberOfLambdas1 != numberOfLambdas) break
            }
            if (gameboard.state == Gameboard.State.WON) return globalPath
            if (listOfLambdas.isEmpty() && (isLiftBlocked() || listOfUnreachableLambdas.isNotEmpty()) || isRobotBlocked() || gameboard.state == Gameboard.State.DEAD) {
                goBackToMaxScore()
                globalPath += 'A'
                return globalPath
            }
        }
    }
}

data class Change(val path: String, val score: Int, val changedPoints: Map<Point, Char>)
