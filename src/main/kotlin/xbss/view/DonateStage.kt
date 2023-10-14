package xbss.view

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
import javafx.scene.shape.SVGPath
import javafx.stage.Stage
import xbss.config.ImageIcon
import xbss.utils.PopConfirm
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * @author  Xbss
 * @create 2022-12-24-17:35
 * @version  1.0
 * @descirbe
 */
class DonateStage:Application() {
    override fun start(primaryStage: Stage?) {
        val stage = primaryStage!!
        val imageView = ImageView(ImageIcon.PAY)
        val vBox = VBox(imageView)
        vBox.prefHeight = 738.0
        vBox.prefWidth = 577.0
        stage.apply {
            scene = Scene(vBox).apply {
                stylesheets.add(this::class.java.getResource("/css/setting.css")?.toExternalForm())
            }
            icons.add(ImageIcon.DONATE24)
            title = "捐赠（非常感谢您的支持！）"
            show()
        }
    }
}