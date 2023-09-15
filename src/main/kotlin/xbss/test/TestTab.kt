package xbss.test

import javafx.application.Application
import javafx.application.Platform
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.input.MouseButton
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Paint
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import javafx.stage.Stage
import xbss.config.AppData
import xbss.config.InitSize
import xbss.view.InitPane
import xbss.view.NewConnect
import java.util.*
import kotlin.concurrent.schedule
import kotlin.concurrent.timerTask

/**
 * @author  Xbss
 * @create 2023-04-20-18:15
 * @version  1.0
 * @describe
 */
class TestTab:Application() {
    private lateinit var stage:Stage
    private lateinit var tabPane:TabPane
    private lateinit var tab: Tab
    private var flag = false
    private val p = SimpleBooleanProperty(false)
    companion object{
        private val green = Paint.valueOf("#00FF00")
        private val red = Paint.valueOf("#FF0000")
    }
    override fun start(primaryStage: Stage?) {
        stage = primaryStage!!
        var i = 0
        val timerTask = timerTask {
            println(i++)
        }
        val timer = Timer()
        timer.schedule(timerTask,5000,1000)
        tab = Tab()
        tabPane = TabPane(tab).apply {
            prefWidth = InitSize.APP_WIDTH
            prefHeight = InitSize.TABPANE_HEIGHT
            tabDragPolicy = TabPane.TabDragPolicy.REORDER // 启用重新排序标签页
            stylesheets.add(this::class.java.getResource("/css/xbss.css").toExternalForm())
        }
        val button = Button("change").apply {
            setOnAction {
                tab.reStatus(flag)
                flag = !flag
                p.value = flag
                val timer1 = Timer()
                timer1.schedule(timerTask,5000,1000)
//                timer.schedule(timerTask,5000,1000)
            }
        }
        val button2 = Button("change2").apply {
            setOnAction {
                println("此时flag $flag 此时p ${p.value}")
                p.value = flag
            }
        }
        tabPane.setOnMouseClicked {
            tab.reStatus(flag)
            flag = !flag
        }
        p.addListener { _,_,newValue ->
            println("值变化 $newValue")
        }
        val textField = TextField().apply {
//            style = "-fx-background-color: #EEE5DE60;-fx-font-size:16px;-fx-prompt-text-fill:#8B4500"
            style = "-fx-background-color: #FFFACD00;-fx-font-size:16px;-fx-prompt-text-fill:#8B4500"
            promptText = "TAB补全，回车发送，delete删除命令"
        }
        val stop = Rectangle(16.0, 16.0, Paint.valueOf("#FF0000")).apply {
            setOnMouseClicked {
                timer.cancel()
            }
        }
        val commandHBox = HBox(5.0,textField,stop).apply {
            HBox.setHgrow(textField, Priority.ALWAYS)
            HBox.setMargin(stop, Insets(8.0,10.0,8.0,0.0))
            style = "-fx-background-color: #EEE5DE60;-fx-background-radius: 3;"
//            style = "-fx-border-width:1px;-fx-border-color:#B0B0B0;"
//            alignment = Pos.BOTTOM_RIGHT
        }
        val anchorPane = AnchorPane(commandHBox)
        stage.scene = Scene(VBox(10.0, tabPane, button,button2,anchorPane))
        stage.show()
    }
    private fun Tab.reStatus(connect:Boolean){
        val nickName = if (!connect)Label("断开连接") else Label("this.text")
        val hBox = HBox(5.0, Circle(5.0).apply {

            fill = if (connect) green else red
            setOnMouseClicked {
            }
        }, nickName).apply { alignment = Pos.CENTER }
        this.graphic = hBox
        println("tab $tab g ${tab.graphic} ")
        this.text = "" //标签页名用label写了，就不需要了
    }
}

fun main() {
    Application.launch(TestTab::class.java)
}