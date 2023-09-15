package xbss.utils

import javafx.beans.binding.Bindings
import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.value.ChangeListener
import javafx.geometry.Insets
import javafx.geometry.Pos
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
 * @create 2023-04-25-18:32
 * @version  1.0
 * @describe :一开始写了FileIOPane，有进度条并且使用jsch的函数操作的，不太好改，所以直接新加了FileCommandPane用于展示直接
 *            使用linux命令的结果。
 */
class FileCommandPane(
    private val type: FileCommandType,
    private val sumTitle:String,
    /**
     * 正常情况下是正在复制/删除的文件名，出错时就作为出错原因的载体
     */
    private val name: ReadOnlyStringProperty,
    private val nowIndex: SimpleIntegerProperty,
    private val totalNum: Int,
    private val status:SimpleIntegerProperty //状态: 1 等待中，2 初始化，3 ..中，4 完成，5出错
    ): VBox() {
    private lateinit var titleHBox: HBox
    private lateinit var centerBox: VBox
    private lateinit var ingArea: VBox
    private lateinit var ingAreaHBox: HBox
    private lateinit var edArea: Pane
    private lateinit var bottomHBox: HBox
    private var describe = ""
    private lateinit var statusListener: ChangeListener<Number>

    enum class FileCommandType {
        COPY, DELETE
    }
    init {
        describe = when (type) {
            FileCommandType.COPY -> "复制"
            FileCommandType.DELETE -> "删除"
        }
        initView()
    }

    private fun initView() {
        initTitleHBox()
        initIngArea()
        initBottomHBox()
        centerBox = VBox(ingArea)
        this.children.addAll(titleHBox, centerBox, bottomHBox)
//        this.setPrefSize(240.0, 120.0)
        this.setPrefSize(240.0, 110.0)
        this.padding = Insets(0.0, 5.0, 5.0, 5.0)
        this.spacing = 10.0
        style = "-fx-background-color:#FFC1C190;-fx-border-color:#CDAA7D"
        statusListener = ChangeListener<Number> { _, _, newValue ->
            when (newValue) {
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
        var title = ""
        when (type) {
            FileCommandType.COPY -> {
                imageView.image = ImageIcon.COPY24
                title = "复制${totalNum}个文件到$sumTitle"
            }

            FileCommandType.DELETE -> {
                imageView.image = ImageIcon.DELETE24
                title = "删除${totalNum}个文件"
            }
        }
        titleHBox = HBox(10.0, imageView, Label(title).apply {
            style = "-fx-font-size: 13"
            tooltip = Tooltip(this.text).apply {
                style = "-fx-background-color: #FFFACD;-fx-text-fill:black;-fx-font-size: 13"
            }
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
        ingAreaHBox = HBox(10.0, Label("等待${describe}中..."))
        ingArea = VBox(10.0, ingAreaHBox)
    }

    //    private fun updateIngAreaHBox(){
//        (ingAreaHBox.children[0] as Label).text = "初始化${describe}数据..."
//    }
    private fun startingAreaHBoxListen() {
        ingAreaHBox.children.clear()
        ingAreaHBox.children.addAll(Label("正在$describe"), Label().apply { textProperty().bind(name) })
    }

    /**
     *  完成区
     */
    private fun initEdArea() {
        edArea =
            HBox(10.0, ImageView(ImageIcon.SUCCESS24), Label("${describe}完成").apply { style = "-fx-font-size:19px" })
        edArea.translateX = 40.0
        centerBox.children.clear()
        centerBox.children.add(edArea)
    }

    /**
     * 出错区
     */
    private fun initErrorArea() {
        edArea = VBox(5.0,
            HBox(10.0, ImageView(ImageIcon.CLOSE24), Label("${describe}出错！").apply { style = "-fx-font-size:19px" }),
            Label().apply {
                textProperty().bind(name.map { "($it)" })
                translateX = 40.0
            })
        edArea.translateX = 30.0
        centerBox.children.clear()
        centerBox.children.add(edArea)
    }

    private fun initBottomHBox() {
        bottomHBox = HBox(
            10.0,
            Label().apply {
                style = "-fx-font-size: 13"
                textProperty().bind(
                    Bindings.concat(
                        describe,
                        "第",
                        nowIndex.asString(),
                        "/ 总",
                        totalNum.toString()
                    )
                )
            },
        ).apply {
            alignment = Pos.BOTTOM_RIGHT
        }
    }
}