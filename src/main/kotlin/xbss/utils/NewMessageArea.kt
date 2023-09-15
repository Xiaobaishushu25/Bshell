package xbss.utils

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.Button
import javafx.scene.control.ScrollPane
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import xbss.config.ImageIcon
import xbss.config.InitSize
import xbss.utils.FileIOPane
import java.lang.annotation.ElementType
import java.lang.ref.WeakReference

/**
 * @author  Xbss
 * @create 2023-03-28-16:44
 * @version  1.0
 * @describe :单tab使用大概230m内存，添加一千个下载面板后内存会飚到450m左右，使用弱引用技术并提醒gc没有提升，使用对象池缓存面板的话不太好用，因为面板数量
 *  波动过大。
 */
class NewMessageArea:ScrollPane() {
    private val mainPane = VBox(10.0)
//    private val scrollPane:ScrollPane = ScrollPane()
    private val paneRefs = mutableListOf<WeakReference<Pane>>() // 用于存储所有 Pane 对象的弱引用
    init {
        this.apply {
//            style = "-fx-background-color:null;"
//            style = "-fx-background-color:null;-fx-border-width: 0px;"
            setPrefSize(255.0,InitSize.MESSAGEAREA_HEIGHT)
//            isManaged = false
            content = mainPane
//            visibleProperty().bind(managedProperty())
        }
//        mainPane.style = "-fx-background-color:null;"
        mainPane.style = "-fx-background-color:null;-fx-border-width: 0px;"
    }
    fun addNotice(pane:Pane){
        val weakPaneRef = WeakReference<Pane>(pane)
        paneRefs.add(weakPaneRef)
        mainPane.children.add(0,weakPaneRef.get())
        weakPaneRef.get()!!.setOnMouseClicked {
            if (it.button==MouseButton.SECONDARY){
                mainPane.children.remove(weakPaneRef.get()) // 从 mainPane 中移除 Pane 对象
                weakPaneRef.clear() // 清除弱引用持有的引用
            }
        }
//        mainPane.children.add(0,pane)
//        scrollPane.isManaged = true
//        pane.setOnMouseClicked {
//            if (it.button==MouseButton.SECONDARY)
//                mainPane.children.remove(pane)
//        }
    }
    fun clearNotices() {
        mainPane.children.clear() // 清空 mainPane 中的所有子节点
        paneRefs.forEach { it.clear() } // 清空所有 Pane 对象的弱引用持有的引用
        paneRefs.clear() // 清空列表中的所有弱引用
        System.gc()
    }
}