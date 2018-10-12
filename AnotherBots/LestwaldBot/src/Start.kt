import gameboard.Gameboard
import robot.Robot

fun start(inputFileName: String) {
    val gameboard = Gameboard(inputFileName)
    val robot = Robot(inputFileName)
    val path = robot.go()
    gameboard.act(path)
    println("$inputFileName: ${gameboard.score} $path")
}