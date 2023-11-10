package xbss.utils

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.control.Tooltip
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import xbss.config.ImageIcon

/**
 * 用作一次性的通知，且内部数据不改变
 */
class Notification(private val type: Type, private val title: String, private val message: String) : VBox() {
    enum class Type {
        SUCCESS,
        ERROR,
        INFO
    }

    private lateinit var titleHBox: HBox
    private lateinit var edArea: HBox

    init {
        initTitleHBox()
        initEdArea()
        this.children.addAll(titleHBox, edArea)
        this.setPrefSize(240.0, 110.0)
        this.padding = Insets(0.0, 5.0, 5.0, 5.0)
        this.spacing = 10.0
        this.style = "-fx-background-color:#FFC1C190;-fx-border-color:#CDAA7D"
    }

    private fun initTitleHBox() {
        val imageView = when (type) {
            Type.SUCCESS -> ImageView(ImageIcon.SUCCESS24)
            Type.ERROR -> ImageView(ImageIcon.CLOSE24)
            Type.INFO -> ImageView(ImageIcon.INFO24)
        }
        titleHBox = HBox(10.0, imageView, Label(title).apply {
            style = "-fx-font-size: 13px"
            tooltip = Tooltip(this.text).apply {
                style = "-fx-background-color: #FFFACD;-fx-text-fill:black;-fx-font-size: 13"
            }
        }).apply {
            prefHeight = 35.0
            alignment = Pos.CENTER_LEFT
            style = "-fx-border-width:0 0 1 0;-fx-border-color:green"
        }
    }

    private fun initEdArea() {
        edArea = HBox(
            TextFlow(
                Text(message).apply {
                    style = "-fx-font-size:15px"
                })
//            TextArea(message).apply {
//            style = "-fx-font-size:15px"
//            isWrapText = true
//            isEditable = false
//        }
        )
//        edArea.translateX = 40.0
    }
}