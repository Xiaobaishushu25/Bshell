package xbss.myterminal

import javafx.application.Application
import javafx.embed.swing.SwingNode
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.layout.VBox
import javafx.stage.Stage
import xbss.myterminal.jediterm.example.SSH
import xbss.myterminal.jediterm.terminal.ui.JediTermWidget
import xbss.myterminal.jediterm.terminal.ui.settings.DefaultSettingsProvider

/**
 * @author  Xbss
 * @create 2023-04-19-15:16
 * @version  1.0
 * @describe
 */
class Test:Application() {
    override fun start(primaryStage: Stage?) {
        val stage = primaryStage!!
        val widget = JediTermWidget(80, 24, DefaultSettingsProvider())
        widget.ttyConnector = SSH.getssh()
        widget.start()
        val swingNode = SwingNode()
        swingNode.content = widget
        val vBox = VBox(swingNode).apply {
            padding= Insets(20.0)
            spacing=20.0
            style = "-fx-background-color:red"
        }
        val scene = Scene(vBox).apply { stylesheets.addAll("css/core.css","css/color.css","css/Xbss.css") }
        stage.apply {
            this.scene = scene
            width = 500.0
            height = 500.0
            title = "你好"
            show()
        }
    }
}
fun main() {
    Application.launch(Test::class.java)
}