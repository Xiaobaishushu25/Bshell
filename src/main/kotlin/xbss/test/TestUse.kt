package xbss.test

import javafx.application.Application
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.stage.Stage

/**
 * @author  Xbss
 * @create 2022-11-26-19:49
 * @version  1.0
 * @describe
 */
class TestFont :Application(){
    enum class Type{
        CPU,MEMORY,GPU
    }
    private fun  initMessageBar(per:Double,text: String,type: Type):StackPane{
        val progressBar = ProgressBar(per).apply {
            prefWidth = 180.0
            styleClass.add("gpu")
            prefHeight = 20.0
        }
        val pane = Pane()
        val hBox = HBox(Label((per*100).toString()+"%"), pane, Label(text)).apply {
            HBox.setHgrow(pane, Priority.ALWAYS)
            alignment = Pos.CENTER
            padding = Insets(0.0,10.0,0.0,10.0)
        }
        return StackPane(progressBar,hBox).apply { prefWidth = 180.0 }
    }
    override fun start(primaryStage: Stage?) {
        val stage = primaryStage!!
        val progressBar = ProgressBar().apply {
            progress = 0.15
            prefWidth = 180.0
            prefHeight = 20.0
            styleClass.add("cpu")
        }
        val per = Label("15%")
        val pane = Pane()
        val label = Label("11607MiB/24576MiB")
        val hBox = HBox(per, pane, label).apply {
            HBox.setHgrow(pane, Priority.ALWAYS)
            alignment = Pos.CENTER
            padding = Insets(0.0,10.0,0.0,10.0)
        }
        val stackPane = StackPane(progressBar,hBox).apply {
            prefWidth = 180.0
            translateX = 50.0
            translateY = 50.0
        }
        val initMessageBar = initMessageBar(0.5, "15G/256G", TestFont.Type.CPU)
        initMessageBar.translateY = 100.0
        stage.apply {
            scene = Scene(AnchorPane(stackPane,initMessageBar),400.0,400.0).apply {
                stylesheets.add(this::class.java.getResource("/css/xbss.css").toExternalForm())
            }
            width = 500.0
            height = 500.0
            title = "你好"
            show()
            isAlwaysOnTop = true
        }
    }
}

fun main() {
    Application.launch(TestFont::class.java)
}
