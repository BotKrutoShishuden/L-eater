import GameMap.*
import Bot.*

import gameboard.*
import org.junit.AfterClass
import robot.*
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.FileReader
import java.io.FileWriter

import java.lang.StringBuilder

internal class BotsComparingTest {

    companion object {


        val outputAddress = "BattleOfBots/Battle.txt"
        val TIME_LIMIT = 10

        var resultBuilder = StringBuilder()
        var numberOfLeaterWin = 0
        var numberOfLestwaldWin = 0
        var numberOfDraw = 0
        var numberOfGames = 0


        @AfterClass
        @JvmStatic
        fun reWriteOfLeaterResults() {
            resultBuilder.append("\nLeaterBot win = ").append(numberOfLeaterWin).append(" / ").append(numberOfGames)
            resultBuilder.append("\nLestwald win = ").append(numberOfLestwaldWin).append(" / ").append(numberOfGames)
            resultBuilder.append("\nDraw = ").append(numberOfDraw)
            val outputBuffer = BufferedWriter(FileWriter(outputAddress))
            outputBuffer.write(resultBuilder.toString())
            outputBuffer.flush()
            outputBuffer.close()

        }
    }

    fun testBotsOnMap(mapAddress: String, testName: String, humanScore: Int) {
        val gameMap = GameMap.cutNormalMap(mapAddress)
        val leaterBot = LeaterBot(gameMap)
        val thread = Thread(leaterBot)
        thread.start()

        try {
            Thread.sleep((TIME_LIMIT * 1000).toLong())
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        thread.stop()

        for (nextStep in leaterBot.bestStepsList)
            gameMap.moveAllObjects(nextStep)


        val gameboard = Gameboard(mapAddress)
        val lestwaldBot = Robot(mapAddress)
        val path = lestwaldBot.go()
        gameboard.act(path)


        if (gameMap.score < gameboard.score)
            numberOfLestwaldWin++
        else if (gameMap.score > gameboard.score)
            numberOfLeaterWin++
        else
            numberOfDraw++

        numberOfGames++

        resultBuilder.append(testName)
        resultBuilder.append("\nLestwaldScore = ").append(gameboard.score)
        resultBuilder.append("\nLeaterBotScore = ").append(gameMap.score)
        resultBuilder.append("\n-------------------------------------------------------------------\n")


    }

    //CONFIRMED
    @org.junit.Test
    fun beard0() {
        testBotsOnMap("maps/beard0.map", "beard0", 858)
    }

    //CONFIRMED
    @org.junit.Test
    fun beard1() {
        testBotsOnMap("maps/beard1.map", "beard1", 858)
    }

    //CONFIRMED
    @org.junit.Test
    fun beard2() {
        testBotsOnMap("maps/beard2.map", "beard2", 4497)
    }

    //CONFIRMED
    @org.junit.Test
    fun beard3() {
        testBotsOnMap("maps/beard3.map", "beard3", 1162)
    }

    //CONFIRMED
    @org.junit.Test
    fun beard4() {
        testBotsOnMap("maps/beard4.map", "beard4", 2013)
    }

    //CONFIRMED
    @org.junit.Test
    fun beard5() {
        testBotsOnMap("maps/beard5.map", "beard5", 657)
    }

    //CONFIRMED
    @org.junit.Test
    fun contest1() {
        testBotsOnMap("maps/contest1.map", "contest1", 210)
    }

    //CONFIRMED
    @org.junit.Test
    fun contest2() {
        testBotsOnMap("maps/contest2.map", "contest2", 280)
    }

    //CONFIRMED
    @org.junit.Test
    fun contest3() {
        testBotsOnMap("maps/contest3.map", "contest3", 275)
    }

    //CONFIRMED
    @org.junit.Test
    fun contest4() {
        testBotsOnMap("maps/contest4.map", "contest4", 575)
    }

    //CONFIRMED
    @org.junit.Test
    fun contest5() {
        testBotsOnMap("maps/contest5.map", "contest5", 1297)
    }

    //CONFIRMED
    @org.junit.Test
    //Не ест дальнюю лямбду, а надо бы
    fun contest6() {
        testBotsOnMap("maps/contest6.map", "contest6", 1177)
    }

    //CONFIRMED
    @org.junit.Test
    fun contest7() {
        testBotsOnMap("maps/contest7.map", "contest7", 869)
    }

    //Мало шагов
    @org.junit.Test
    fun contest8() {
        testBotsOnMap("maps/contest8.map", "contest8", 1952)
    }

    //Мало шагов
    @org.junit.Test
    fun contest9() {
        testBotsOnMap("maps/contest9.map", "contest9", 3088)
    }

    //Мало шагов
    @org.junit.Test
    fun contest10() {
        testBotsOnMap("maps/contest10.map", "contest10", 3594)
    }

    @org.junit.Test
    fun ems1() {
        testBotsOnMap("maps/ems1.map", "ems1", 334)
    }

    //TODO Шаг влево и было бы на лямбду лучше
    @org.junit.Test
    fun flood1() {
        testBotsOnMap("maps/flood1.map", "flood1", 937)
    }

    //CONFIRMED
    @org.junit.Test
    fun flood2() {
        testBotsOnMap("maps/flood2.map", "flood2", 281)
    }

    //CONFIRMED
    @org.junit.Test
    fun flood3() {
        testBotsOnMap("maps/flood3.map", "flood3", 1293)
    }

    //Мало шагов
    @org.junit.Test
    fun flood4() {
        testBotsOnMap("maps/flood4.map", "flood4", 826)
    }

    //TODO не хочет искать открытый лифт
    @org.junit.Test
    fun flood5() {
        testBotsOnMap("maps/flood5.map", "flood5", 567)
    }


    //CONFIRMED
    @org.junit.Test
    fun trampoline1() {
        testBotsOnMap("maps/trampoline1.map", "trampoline1", 407)
    }

    //CONFIRMED
    @org.junit.Test
    fun trampoline2() {
        testBotsOnMap("maps/trampoline2.map", "trampoline2", 1724)
    }

    //CONFIRMED
    @org.junit.Test
    fun trampoline3() {
        testBotsOnMap("maps/trampoline3.map", "trampoline3", 5467)
    }

    //CONFIRMED
    @org.junit.Test
    fun horock1() {
        testBotsOnMap("maps/horock1.map", "horock1", 735)
    }

    //CONFIRMED
    @org.junit.Test
    fun horock2() {
        testBotsOnMap("maps/horock2.map", "horock2", 735)
    }

    //CONFIRMED
    @org.junit.Test
    fun horock3() {
        testBotsOnMap("maps/horock3.map", "horock3", 2365)
    }


}