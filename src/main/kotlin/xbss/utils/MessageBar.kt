package xbss.utils

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.layout.*
import java.util.*

/**
 * @author  Xbss
 * @create 2023-03-27-11:24
 * @version  1.0
 * @describe
 */
class MessageBar(private val type: Type):HBox() {
    private val allWidth = 170.0
//    private var name:String ?= null
    private val progressBar:ProgressBar
    private val perLabel:Label = Label()
    private val tipLabel:Label = Label()
    private val barArea:AnchorPane
    constructor(name:String,type: Type):this(type){
        val name = Label(name)
        name.translateY = 4.0
        this.prefWidth = allWidth+50.0
        this.children.add(0,name)
        this.spacing = 5.0
//        this.alignment = Pos.BOTTOM_CENTER
    }
    enum class Type{
        CPU,MEMORY,GPU
    }
    init {
        progressBar = ProgressBar().apply {
            setPrefSize(allWidth,20.0)
            styleClass.add(type.name.lowercase(Locale.getDefault()))
        }
        val pane = Pane()
        val hBox = HBox(perLabel, pane,tipLabel).apply {
            alignment = Pos.CENTER
            padding = Insets(0.0,10.0,0.0,10.0)
            maxWidth = allWidth
            HBox.setHgrow(pane, Priority.ALWAYS)
            minWidth = allWidth
            translateY = 2.0
        }
        barArea = AnchorPane(progressBar, hBox)
        this.children.addAll(barArea)
        this.prefWidth = allWidth
    }

    /**
     * 更新eg per：85.3 tip：123/452
     *
     * @param per
     * @param tip
     */
    fun update(per:Double,tip: String){
        perLabel.text = "${per}%"
        progressBar.progress = (per/100).reserveOne()
        tipLabel.text = tip
    }
    /**
     * double保留1位小数
     *
     */
    private fun Double.reserveOne() = String.format("%.1f", this).toDouble()
}