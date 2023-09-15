package xbss.test

import javafx.application.Application
import javafx.collections.FXCollections
import javafx.event.Event
import javafx.event.EventDispatchChain
import javafx.event.EventDispatcher
import javafx.event.EventTarget
import javafx.geometry.Bounds
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Background
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.stage.Popup
import javafx.stage.Stage
import javafx.util.Callback

/**
 * @author  Xbss
 * @create 2023-03-22-12:21
 * @version  1.0
 * @describe
 */
class testList :Application(){
    override fun start(primaryStage: Stage?) {
        val stage = primaryStage!!
        var local:Bounds? =null
        val popup = Popup()
        val observableList =
            FXCollections.observableArrayList<String>("python","isa" ,"isb","isc","nvidia-smi", "java -version", "conda env list")
        val listView = ListView<String>().apply {
            cellFactory = Callback {
                val cell = object : ListCell<String>(){
                    override fun updateItem(item: String?, empty: Boolean) {
                        super.updateItem(item, empty)
                        graphic = if (item!=null){
                            Label(item).apply {
                                prefHeight = 25.0
                            }
                        }else{
                            Label().apply {
                                background = Background.EMPTY
                            }
                        }
                    }
                }
                cell.focusedProperty().addListener { _,_,newValue ->
                    cell.style = if (newValue) "-fx-background-color:#2e436e;" else "-fx-background-color:null"
                }
                cell.selectedProperty().addListener { _,_,newValue ->
                    cell.style = if (newValue) "-fx-background-color:#2e436e;" else "-fx-background-color:null"
                }
                cell
            }
        }
        popup.content.add(listView)
        val textField = TextField().apply {
            var autoFill = false //如果是自动补全的话，就别弹出补全列表了
            //这个方法可以获得节点的getMinX(),getMinY(),getMaxX(),getMaxY(),getWidth()和getHeight()属性
            textProperty().addListener { _,_,newValue ->
                if (newValue.isNotEmpty()){
                    //采用popup的方法后，弹出 listView后它会自动获得焦点，但是同时textfield的焦点也存在，但是只能输入，其他键盘事件都没有
                    //只能在listview上处理了
                    if(!popup.isShowing&&!autoFill)
                        popup.show(this,local!!.minX,local!!.minY+local!!.height)
                    listView.items = FXCollections.observableArrayList<String>(observableList.filter { it.contains(newValue) })
                    listView.selectionModel.select(0)
                    listView.prefHeight = 25.0*listView.items.size
                    autoFill = false
                    listView.lookup(".increment-arrow").style = "-fx-background-color: transparent;"
                    listView.lookup(".decrement-arrow").style = "-fx-background-color: transparent;"
//                    listView.lookup(".increment-button").isVisible = false
//                    listView.lookup(".decrement-button").isVisible = false
                }else
                    popup.hide()
            }
            listView.setOnKeyPressed {
                when(it.code){
                    KeyCode.LEFT -> positionCaret(caretPosition-1)
                    KeyCode.RIGHT -> positionCaret(caretPosition+1)
                    KeyCode.HOME -> positionCaret(0)
                    KeyCode.END -> positionCaret(this.text.length)
                    KeyCode.ENTER -> {
                        println("进来")
                        println(listView.selectionModel.selectedItem)
                        autoFill = true
                        this.text = listView.selectionModel.selectedItem
                        positionCaret(this.text.length)
                        popup.hide()
                    }
                    else -> {}
                }
            }
            addEventFilter(KeyEvent.KEY_PRESSED) {
                println(it.code)
                println(this.isFocused)
                println(listView.isFocused)
                when(it.code) {
//                    KeyCode.DOWN -> listView.selectionModel.select(listView.selectionModel.selectedIndex+1)
//                    KeyCode.UP -> {listView.selectionModel.select(listView.selectionModel.selectedIndex-1)
//                        println("UP")}
                    KeyCode.ENTER -> {
                        println("进来")
                        println(text)
                        text = ""
                    }
                    else -> {}
                }
//                it.consume()
            }
        }
        stage.apply {
//            scene = Scene(VBox(15.0,textField,listView))
            scene = Scene(VBox(15.0,textField))
            show()
        }
        local = textField.localToScreen(textField.boundsInLocal)
        listView.prefWidth = local.width
        println(local)

    }
}

fun main() {
    Application.launch(testList::class.java)
}