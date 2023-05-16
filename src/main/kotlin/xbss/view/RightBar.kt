package xbss.view

import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseButton
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import xbss.config.ImageIcon
import xbss.utils.NewMessageArea

/**
 * @author  Xbss
 * @create 2023-05-01-13:21
 * @version  1.0
 * @describe ：这个vbox不管怎么设置宽度都是40.0，我用各种办法都设置不成30.0，不知道什么情况
 *            所有面板叠加在content:Pane = StackPane()上，其可见性绑定自己button的select
 */
class RightBar(mainWindow: MainWindow):VBox() {
    private val content:Pane = StackPane()
    private val messageB:ToggleButton
    private val messageArea = NewMessageArea() //通知展示区域
    init {
        val toggleGroup = ToggleGroup()
        messageB = getButton(ImageIcon.MESSAGE24,messageArea).apply {
            setOnMouseClicked {
                if (it.button == MouseButton.SECONDARY)
                    messageArea.clearNotices()
            }
        }
        toggleGroup.toggles.addAll(messageB)
        content.style = "-fx-background-color:#DEB88700"
        //当没有任何按钮被选中时，toggleGroup.selectedToggleProperty()的最新值是null，此时应该让面板不可见，不然会遮挡终端的滚动条
        content.visibleProperty().bind(toggleGroup.selectedToggleProperty().map { it != null })
        this.children.addAll(messageB)
//        mainWindow.children.add(messageArea)
        mainWindow.children.add(content)

        //因为自己是宽是40.0
//        AnchorPane.setRightAnchor(messageArea,40.0)
//        AnchorPane.setTopAnchor(messageArea,5.0)
        AnchorPane.setRightAnchor(content,40.0)
        AnchorPane.setTopAnchor(content,5.0)
        //不管用
//        this.maxWidth = 30.0
//        this.prefWidth = 30.0
    }
//    private fun getButton(image: Image):ToggleButton{
    private fun getButton(image: Image,cContent:Node):ToggleButton{
        return ToggleButton().apply {
            graphic = ImageView(image)
//            prefHeight = 30.0
//            prefWidth = 30.0
            cursor = Cursor.HAND
            style = "-fx-background-color:#DEB88700;"
            hoverProperty().addListener { _,_,newValue ->
                if (!isSelected){
                    style = if (newValue)
                        "-fx-background-color:#DEB88780;-fx-background-radius:3px"
                    else
                        "-fx-background-color:#DEB88700;"
                }
            }
            selectedProperty().addListener { _,_,newValue ->
                style = if (newValue)
                    "-fx-background-color:#DEB887;-fx-background-radius:3px"
                else
                    "-fx-background-color:#DEB88700;"
            }
            addContent(cContent)
            cContent.visibleProperty().bind(selectedProperty())
        }
    }
    fun addNotice(pane: Pane){
        messageArea.addNotice(pane)
        messageB.isSelected = true
    }
    private fun addContent(cContent: Node){
        content.children.add(cContent)
    }
}