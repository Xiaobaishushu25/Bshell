package xbss.test

import javafx.application.Application
import javafx.geometry.Rectangle2D
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundImage
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.robot.Robot
import javafx.scene.shape.Arc
import javafx.stage.Stage
import xbss.utils.PopConfirm
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * @author  Xbss
 * @create 2022-12-24-17:35
 * @version  1.0
 * @descirbe
 */
class TestGif:Application() {
    override fun start(primaryStage: Stage?) {
        val stage = primaryStage!!
//        val testnotify = PopConfirm()
//        stage.scene = Scene(testnotify,500.0,600.0)
        stage.show()
    }
}

fun main() {
    Application.launch(TestGif::class.java)
}