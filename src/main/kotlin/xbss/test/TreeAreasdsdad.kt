package xbss.test

import javafx.application.Application
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.layout.*
import javafx.stage.Stage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import xbss.MainAPP
import xbss.config.ImageIcon
import xbss.config.InitSize


/**
 * @author  Xbss
 * @create 2023-03-23-20:05
 * @version  1.0
 * @describe
 */
class TreeAreasdsdad(): Application() {
    val logger: Logger = LoggerFactory.getLogger("你好")
    private lateinit var textField:TextField
    private lateinit var cdHBox: HBox
    private lateinit var treeView: TreeView<String>
    private lateinit var vBox: VBox

    init {
        MainAPP.service.submit {
            logger.info("xinde rtizhi ")
            logger.error("新的日志")
        }
//        vBox.boundsInLocalProperty().addListener { _,_,_ ->
//            initStageShow()
//        }
        initView()
    }

//    private fun initStageShow(){
//        textField.prefWidth = this.width - 16*2
//    }
    private fun initView() {
        initCdHBox()
        initFileTree()
        vBox = VBox().apply {
            padding = Insets(10.0)
            spacing = 5.0
            children.addAll(
                cdHBox,
                treeView
            )
        }
            val backgroundImage = BackgroundImage(
//            Image(this::class.java.getResourceAsStream("/img/back.png"),textFieldLocal!!.width,textFieldLocal!!.height,false,true),  // 图片路径
            Image(this::class.java.getResourceAsStream("/img/back.jpg")),  // 图片路径
            BackgroundRepeat.NO_REPEAT,  // 图片重复方式
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.DEFAULT,
            BackgroundSize.DEFAULT
        )
        val background = Background(backgroundImage)
    vBox.background = background
    MainAPP.service.submit {
        logger.info("asdasdasd")
        logger.error("asdasdasd")
    }
    }

    private fun initCdHBox() {
        initTextField()
        val up = Button().apply {
            background = Background(BackgroundImage(
                Image(this@TreeAreasdsdad::class.java.getResourceAsStream("/img/upTo.png")),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                null,
                null))
            setOnAction {
                //返回
            }
        }
        val update = Button().apply {
            background = Background(BackgroundImage(
                Image(this@TreeAreasdsdad::class.java.getResourceAsStream("/img/update.png")),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                null,
                null))
            setOnAction {
                //刷新文件夹
            }
        }
        cdHBox = HBox(5.0,textField,up,update).apply {
            style = "-fx-border-width:1px;-fx-border-color:#B0B0B0;"
            alignment = Pos.BOTTOM_RIGHT
        }
    }

    private fun initTextField() {
        textField = TextField("sdfdfsf").apply {
            border = Border.EMPTY
            background = Background.EMPTY
//            setStyle("-fx-background-color: #EEE5DE60;");
        }
    }
    private fun initFileTree() {
//        defaultPath = "/data1/pjf"
//        rootTreeItem = LsTreeItem(ssh,sftp, "/")
        treeView = TreeView()
        treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE)
        treeView.root = TreeItem("asdasdsadasasdasdasdasdasdasdasdasdadasdadadadad")
//        treeView.selectionModel.selectedIndices.addListener(ListChangeListener<Int>{
//            println(treeView.selectionModel.selectedIndices)
//        })
        val treeItem = TreeItem("你好")
        val treeItem1 = TreeItem("我好")
        treeView.root.children.addAll(treeItem,treeItem1)
        for (s in 0..35){
            treeView.root.children.addAll(TreeItem("你好"))
        }
//        val list = (defaultPath.split("/") as MutableList<String>).filter { it.isNotEmpty() } as MutableList<String>
//        expandItemS(lsTreeItem,list)
        treeView.setCellFactory {
            val label = Label()
            val cell = object : TreeCell<String>() {
                override fun updateItem(item: String?, empty: Boolean) {
                    super.updateItem(item, empty)
                    graphic = if (!empty) {
                        label.text = item
//                        label.style = "-fx-background-color:blue"
                        val imageView = ImageView()
                        imageView.image = ImageIcon.PY
                        HBox(5.0, imageView, label)
                    } else
                        null
                }
            }
            cell
        }
        val contextMenu = ContextMenu()
        val menuItem1 = MenuItem("菜单项1")
        val menuItem2 = MenuItem("菜单项2")
        contextMenu.items.addAll(menuItem1, menuItem2)
        contextMenu.showingProperty().addListener { _,_,newValue ->
            println(newValue)
        }
        treeView.contextMenu = contextMenu
        treeView.setOnContextMenuRequested {
            println("右键")
//            val contextMenu = ContextMenu()
//            val menuItem1 = MenuItem("菜单项1")
//            val menuItem2 = MenuItem("菜单项2")
//            contextMenu.items.addAll(menuItem1, menuItem2)
//            contextMenu.show(treeView,it.screenX+10.0,it.screenY+10.0)
        }
        treeView.stylesheets.add(this::class.java.getResource("/css/xbss.css").toExternalForm())
//        treeView.style = "-fx-background-color:red"
        treeView.prefWidth = InitSize.TREEVIEW_WIDTH
        treeView.prefHeight = InitSize.TEXTAREA_HEIGHT-30.0
        var ctrlIsDown = false
        treeView.setOnKeyPressed {
            when(it.code){
                KeyCode.CONTROL -> ctrlIsDown = true
                KeyCode.C -> {
                    if (ctrlIsDown){
                        println("复制")
                        println(treeView.selectionModel.selectedItems)
                    }
                }
                KeyCode.V -> {
                    if (ctrlIsDown){
                        println(treeView.selectionModel.selectedItems)
                        println("粘贴")
                    }
                }
                else -> {}
            }
        }
        treeView.setOnKeyReleased {
            when(it.code){
                KeyCode.CONTROL -> ctrlIsDown = false
                else -> {}
            }
        }
    }

    override fun start(primaryStage: Stage?) {
        val stage = primaryStage!!
        stage.apply {
            scene = Scene(vBox)
            show()
        }
    }
}

fun main() {
    Application.launch(TreeAreasdsdad::class.java)
}