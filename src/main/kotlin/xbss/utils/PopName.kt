package xbss.utils

import javafx.application.Application
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.Window
import xbss.config.ImageIcon

/**
 * @author  Xbss
 * @create 2023-03-29-20:20
 * @version  1.0
 * @describe :新建文件夹、重命名文件夹弹出的窗口
 */
class PopName(private val olName:String?=null):Application() {
    val textObservable = SimpleStringProperty(null) //在外面监听改变
    override fun start(primaryStage: Stage?) {
        val stage = primaryStage!!
        val textField = TextField(olName ?: "" )
        textField.prefHeight = 35.0
        textField.style = "-fx-font-size:16px;-fx-border-radius: 4px;-fx-border-color: #708090;-fx-background-radius: 4px;-fx-background-insets:0;-fx-background-color:transparent"
        val ok = Button("确定").apply {
            style = "-fx-background-color:#FFEC8B;"
            setOnAction {
                textObservable.value = textField.text
                stage.close()
            }
            disableProperty().bind(textField.textProperty().map { it.isEmpty() })
        }
        val cancel = Button("取消").apply {
            style = "-fx-background-color:#696969;-fx-text-fill:white"
            setOnAction {
                textObservable.value = ""
                stage.close()
            }
        }
//        HBox(10.0,ok,cancel)
        stage.apply {
            scene = Scene(VBox(15.0,
                textField,
                HBox(10.0,ok,cancel).apply { alignment = Pos.BOTTOM_RIGHT }
            ).apply { padding = Insets(50.0,20.0,20.0,20.0) },
                400.0,160.0
            )
            title = olName?.let { "重命名-$olName" }?:run{ "输入文件夹名" }
            initModality(Modality.APPLICATION_MODAL)
            icons.add(ImageIcon.B)
            show()
            isResizable = false
        }
    }
}