package xbss.test

import javafx.application.Application
import javafx.application.Platform
import javafx.stage.Stage
import java.util.Timer
import kotlin.concurrent.timerTask


/**
 * @author  Xbss
 * @create 2022-12-24-17:35
 * @version  1.0
 * @descirbe
 */
class TestSSH2:Application() {
    override fun start(primaryStage: Stage?) {
        val stage = primaryStage!!
        //这是测试
    }
}

fun main() {
    Application.launch(TestSSH2::class.java)
}