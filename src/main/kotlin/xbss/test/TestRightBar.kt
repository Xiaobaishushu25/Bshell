package xbss.test

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.VBox
import javafx.stage.Stage
import xbss.config.ImageIcon

/**
 * @author  Xbss
 * @create 2023-05-01-12:48
 * @version  1.0
 * @describe
 */
class TestRightBar:Application() {
    override fun start(primaryStage: Stage?) {
        val stage = primaryStage!!
        val toggleGroup = ToggleGroup()
        val button = getButton(ImageIcon.MESSAGE24)
        val button1 = getButton(ImageIcon.MESSAGE24)
        val button2 = getButton(ImageIcon.MESSAGE24)
        toggleGroup.toggles.addAll(button,button1,button2)
        //没选中任何按钮时是null
        toggleGroup.selectedToggleProperty().addListener { _,_,newValue ->
            println(newValue)
        }
//        val hBox = HBox(toggleGroup,Button("test"))
        val hBox = VBox(button,button1,button2)
        val anchorPane = AnchorPane(hBox)
        stage.scene = Scene(anchorPane).apply {
            stylesheets.add(this::class.java.getResource("/css/xbss.css").toExternalForm())
        }
        stage.show()
    }
    private fun getButton(image: Image):ToggleButton{
        return ToggleButton().apply {
            graphic = ImageView(image)
            prefHeight = 28.0
            prefWidth = 28.0
            style = " -fx-background-color:#DEB88700;"
            selectedProperty().addListener { _,_,newValue ->
                style = if (newValue)
                    "-fx-background-color:#DEB887;-fx-background-radius:3px"
                else
                    "-fx-background-color:#DEB88700;"
            }
            setOnAction {
            }
        }
    }
}
fun main() {
    Application.launch(TestRightBar::class.java)
}