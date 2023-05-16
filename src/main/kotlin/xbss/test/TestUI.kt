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
//        val popName = PopName("就文件夹名")
//        popName.start(Stage())
//        popName.textObservable.addListener { _,_,newValue ->
//            println(newValue)
//        }
//        val button = Button("右击")
//        val treeAreaRightMenu = TreeAreaRightMenu()
//        button.setOnContextMenuRequested {
//            treeAreaRightMenu.show(button, it.screenX+3.0, it.screenY+3.0)
//        }
//        val fileIOPane = FileIOPane(
//            FileIOPane.FileIOType.UPLOAD,
//            "Test",
//            SimpleStringProperty("demo.py"),
//            SimpleIntegerProperty(1),
//            15,
//            SimpleDoubleProperty(0.3),
//            "nihaosdasd/tyet"
//        )
//        val anchorPane = AnchorPane(button)
//        stage.scene = Scene(anchorPane,900.0,700.0).apply { stylesheets.add(this::class.java.getResource("/css/xbss.css").toExternalForm()) }
        val timerTask = timerTask {
            println(stage.isFocused)
            Platform.runLater {  stage.requestFocus() }

//            if (!stage.isFocused) {
//                println("进来了")
//                Platform.runLater {  stage.requestFocus() }
//            }
        }
        val timer = Timer()
        timer.schedule(timerTask,5000,5000)
        stage.show()
    }
}

fun main() {
    Application.launch(TestSSH2::class.java)
}