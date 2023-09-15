package xbss.view
import javafx.application.Application
import javafx.beans.binding.Bindings
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import javafx.stage.Stage
import xbss.config.ImageIcon
import xbss.config.Setting

/**
 * @author  Xbss
 * @create 2023-04-22-11:31
 * @version  1.0
 * @describe
 */
class SettingPane:Application() {
    private lateinit var leftPane:VBox
    private lateinit var rightPane:VBox
    private lateinit var bottomPane: HBox

    override fun start(primaryStage: Stage?) {
        val stage = primaryStage!!
        initLeftPane()
        initRightPane()
        initBottomPane()
        val vBox = VBox(10.0,HBox(10.0,leftPane,rightPane),bottomPane).apply {
            padding = Insets(15.0)
        }
        stage.apply {
            scene = Scene(vBox,460.0,500.0).apply {
                stylesheets.add(this::class.java.getResource("/css/setting.css").toExternalForm())
            }
            icons.add(ImageIcon.B)
            title = "设置"
            show()
        }
    }
    private fun initBottomPane(){
        bottomPane = HBox(10.0, Label("(不保存此次修改仅当前使用期间有效)"),
            Button("确定").apply { styleClass.add("sure") },
            Button("取消").apply { styleClass.add("back") })
    }
    private fun initLeftPane(){
        val treeView = TreeView<String>()
        val appearance = TreeItem<String>("外观")
        val backGround = TreeItem<String>("背景图片")
        val color = TreeItem<String>("配色")
        val font = TreeItem<String>("字体")
        appearance.children.addAll(backGround,color,font)
        val shortCut = TreeItem<String>("快捷键")
        shortCut.addEventHandler(MouseEvent.MOUSE_CLICKED){
            println("点击了快捷键")
        }
        val historyCommand = TreeItem<String>("历史命令")
        val root = TreeItem<String>()
        root.children.addAll(appearance,shortCut,historyCommand)
        treeView.root = root
        treeView.isShowRoot = false
        leftPane = VBox(treeView).apply {
            prefWidth = 150.0
        }
    }
    private fun initRightPane(){
        val path = TextField()
        val open = Button().apply {
            graphic = ImageView(ImageIcon.OPENFOLDER)
            style = "-fx-background-color:null;-fx-cursor:hand"
            setOnAction {
                FileChooser().apply {
                    extensionFilters.addAll(FileChooser.ExtensionFilter("图片","*.png","*.jpg","*.jpeg"))
                    showOpenDialog(leftPane.scene.window)?.let {
                        path.text = it.absolutePath
                    }
                }
            }
        }
        val hBox = HBox(5.0, path, open)
        val checkBox = CheckBox("自适应宽高")
        val slider = Slider(0.0,100.0,50.0)
        slider.prefWidth = 230.0
        val value = Label()
        // 将Slider的值与Label的文本绑定

        value.textProperty().bind(Bindings.format("%.0f", slider.valueProperty()));
        val hBox1 = HBox(10.0, slider,value).apply { alignment = Pos.CENTER }
        rightPane = VBox(20.0,Label("图片路径:"),hBox,Label("不透明度:"),hBox1,checkBox)
        Setting.isAutoResize.bindBidirectional(checkBox.selectedProperty())
        path.textProperty().bindBidirectional(Setting.backgroundImagePathP)
        Setting.opacityP.bind(value.textProperty().map { it.toDouble().div(100) })
    }

}

fun main() {
    Application.launch(SettingPane::class.java)
}