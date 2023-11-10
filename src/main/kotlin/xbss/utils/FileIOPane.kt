package xbss.utils

import javafx.beans.binding.Bindings
import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.value.ChangeListener
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.control.Tooltip
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import xbss.MainAPP
import xbss.config.ImageIcon
import java.awt.Desktop
import java.io.File

/**
 * @author  Xbss
 * @create 2023-03-28-16:59
 * @version  1.0
 * @describe :进行文件上传、下载的面板
 */
class FileIOPane(
    private val type: FileIOType,
    private val title:String, //下载的文件（夹）名称
    /**
     * 正常情况下是正在上传/下载的文件名，出错时就作为出错原因的载体
     */
    private val name:ReadOnlyStringProperty,
    private val nowNum:SimpleIntegerProperty,
    private val totalNum:SimpleIntegerProperty,
    private val ioPer:ReadOnlyDoubleProperty,
    private val path:String,
    private val status:SimpleIntegerProperty //状态: 1 等待中，2 初始化，3 下载中，4下载完成，5出错
):VBox() {
    private lateinit var titleHBox:HBox
    private lateinit var centerBox:VBox
    private lateinit var ingArea:VBox
    private lateinit var ingAreaHBox:HBox
    private lateinit var edArea:Pane
    private lateinit var bottomHBox:HBox
    private var describe = ""
    private lateinit var statusListener:ChangeListener<Number>
    enum class FileIOType{
        DOWN,UPLOAD
    }
    init {
//        describe = if (type==FileIOType.DOWN)"下载" else "上传"
        describe = when(type){
            FileIOType.DOWN -> "下载"
            FileIOType.UPLOAD -> "上传"
        }
        initView()
    }

    private fun initView() {
        initTitleHBox()
        initIngArea()
        initBottomHBox()
        centerBox = VBox(ingArea)
        this.children.addAll(titleHBox,centerBox,bottomHBox)
//        this.setPrefSize(240.0,120.0)
        this.setPrefSize(240.0,110.0)
        this.padding = Insets(0.0,5.0,5.0,5.0)
        this.spacing = 10.0
        style = "-fx-background-color:#FFC1C190;-fx-border-color:#CDAA7D"
        statusListener = ChangeListener<Number> { _, _, newValue ->
            when(newValue){
                2 -> updateIngAreaHBox()
                3 -> startingAreaHBoxListen()
                4 -> {
                    initEdArea()
                    MainAPP.highlightIcon()
                }
                5 -> {
                    initErrorArea()
                    MainAPP.highlightIcon()
                    //出错后直接取消监听，不在更新面板，因为在FileTask出错捕获到异常了，会直接进到succeeded导致又监听到4更新成成功，需要处理一下
                    status.removeListener(statusListener)
                }
            }
        }
        status.addListener(statusListener)
    }

    private fun initTitleHBox() {
        val imageView = ImageView()
//        val close = Button()
//        close.style = "-fx-background-color:null;-fx-cursor:hand"
        imageView.image = if (type==FileIOType.DOWN) ImageIcon.DOWNLOAD  else ImageIcon.UPLOAD
        titleHBox = HBox(10.0,imageView,Label("$describe $title").apply {
            style = "-fx-font-size: 13px"
            tooltip = Tooltip(this.text).apply { style = "-fx-background-color: #FFFACD;-fx-text-fill:black;-fx-font-size: 13" }
        }).apply {
            prefHeight = 35.0
            alignment = Pos.CENTER_LEFT
            style = "-fx-border-width:0 0 1 0;-fx-border-color:green"
        }
    }
    /**
     *  正在进行时区
     */
    private fun initIngArea() {
//        ingAreaHBox= HBox(10.0, Label("正在$describe"), Label().apply { textProperty().bind(name) })
        ingAreaHBox= HBox(10.0, Label("等待${describe}中..."))
        ingArea = VBox(10.0,
            ingAreaHBox,
            HBox(10.0,Label("进度"),ProgressBar().apply {
                progressProperty().bind(ioPer)
                styleClass.add("file")
                prefWidth = 150.0
            })
        )
    }
    private fun updateIngAreaHBox(){
        (ingAreaHBox.children[0] as Label).text = "初始化${describe}数据..."
    }
    private fun startingAreaHBoxListen(){
        ingAreaHBox.children.clear()
        ingAreaHBox.children.addAll(Label("正在$describe"), Label().apply { textProperty().bind(name) })
    }

    /**
     *  完成区
     */
    private fun initEdArea() {
        edArea = HBox(10.0,ImageView(ImageIcon.SUCCESS24),Label("${describe}完成").apply { style = "-fx-font-size:19px" })
        edArea.translateX = 40.0
        centerBox.children.clear()
        centerBox.children.add(edArea)
    }

    /**
     * 出错区
     */
    private fun initErrorArea() {
        edArea = VBox(5.0,HBox(10.0,ImageView(ImageIcon.CLOSE24),Label("${describe}出错！").apply { style = "-fx-font-size:19px" }),
            Label().apply {
                textProperty().bind(name.map { "($it)" })
                translateX = 40.0
            })
        edArea.translateX = 30.0
        centerBox.children.clear()
        centerBox.children.add(edArea)
    }

    private fun initBottomHBox(){
        bottomHBox = HBox(10.0,
            Label().apply {
                style = "-fx-font-size: 13"
                textProperty().bind(Bindings.concat(describe, "第", nowNum.asString(),"/ 总", totalNum.asString()))
                          },
        ).apply {
            if (type==FileIOType.DOWN)
                this.children.add(0, ImageView(ImageIcon.OPENFOLDER).apply {
                    style = "-fx-cursor:hand"
                    setOnMouseClicked {
                        //这样可以在已有窗口以一个tab方式打开
                        //todo :这样仅支持windows系统
                        Runtime.getRuntime().exec(arrayOf("cmd.exe", "/C", "start $path"))
                        //新打开一个窗口
//                        Desktop.getDesktop().open(File(path))
                    }
                })
            alignment = Pos.BOTTOM_RIGHT
        }
    }
}