package xbss.utils

import javafx.beans.property.SimpleIntegerProperty
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.effect.DropShadow
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.shape.Rectangle
import javafx.stage.Popup
import xbss.config.ImageIcon

/**
 * @author  Xbss
 * @create 2023-03-24-21:33
 * @version  1.0
 * @describe
 */
class PopConfirm(isDialogue:Boolean=true, tip:String = "删除后无法恢复，是否继续？"):Popup() {
    val choose = SimpleIntegerProperty(0) //选项监听  1是取消2是确认
    private var rectangle:Rectangle?=null
    init {
        val icon = getIcon()
        val label = Label(tip).apply {style = "-fx-font-size:15px"}
        val hBox = HBox(5.0,icon,label)
        hBox.alignment = Pos.CENTER
        val cancel = Button("取消").apply {
            style = "-fx-background-color:#EEEED150;"
            setOnAction {
                choose.value = 1
                this@PopConfirm.hide()
            }
        }
        val ok = Button("确认").apply {
            style = "-fx-background-color:#CDCDB4;-fx-text-fill:red"
            setOnAction {
                choose.value = 2
                this@PopConfirm.hide()
            }
        }
        val hBox2 = HBox(5.0,cancel,ok)
        val anchorPane = AnchorPane(hBox,hBox2).apply {
            setPrefSize(220.0,65.0)
            style = "-fx-background-color:#EED8AE;-fx-background-radius:5px"
            AnchorPane.setLeftAnchor(hBox,12.0)
            AnchorPane.setTopAnchor(hBox,10.0)
            AnchorPane.setLeftAnchor(hBox2,120.0)
            AnchorPane.setTopAnchor(hBox2,38.0)
        }
        if (isDialogue){
            rectangle = Rectangle(10.0, 10.0, 12.0, 12.0).apply {
                rotate = 45.0
                fill = Paint.valueOf("#EED8AE")
            }
        }
        // 添加阴影效果
        DropShadow().apply {
            color = Color.GRAY
            radius = 10.0
            offsetX = 3.0
            offsetY = 3.0
            anchorPane.effect = this
            rectangle?.effect = this
        }
        val bigAnchorPane = AnchorPane().apply {
            setPrefSize(230.0,80.0)
            if (rectangle!=null){
                children.addAll(rectangle!!,anchorPane)
                AnchorPane.setTopAnchor(rectangle!!, 5.0)
                AnchorPane.setLeftAnchor(rectangle!!, 160.0)
            }else{
                children.addAll(anchorPane)
            }
            AnchorPane.setTopAnchor(anchorPane, 8.0)
        }
        this.content.add(bigAnchorPane)
    }
    private fun getIcon():ImageView{
        return ImageView(ImageIcon.CLOSE16)
    }
}