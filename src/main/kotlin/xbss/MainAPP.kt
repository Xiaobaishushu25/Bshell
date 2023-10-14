package xbss

import javafx.application.Application
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.scene.Cursor
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.image.ImageView
import javafx.scene.input.*
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.SVGPath
import javafx.stage.Stage
import xbss.config.AppVersion
import xbss.config.ImageIcon
import xbss.config.InitSize
import xbss.view.DonateStage
import xbss.view.InitPane
import xbss.view.MainWindow
import xbss.view.SettingPane
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.system.exitProcess


/**
 * @author  Xbss
 * @create 2023-03-23-18:55
 * @version  1.0
 * @describe
 */
class MainAPP:Application() {
    private lateinit var tabPane:TabPane
    private lateinit var addTab:Tab
    private lateinit var nowSelectTab: Tab
    companion object{
        val service: ExecutorService = Executors.newFixedThreadPool(15)
        var stage = Stage()
        /**
         * 在任务栏高亮图标
         */
        fun highlightIcon(){
            if (!stage.isFocused)
                stage.requestFocus()
        }
    }
    override fun start(primaryStage: Stage?) {
        stage = primaryStage!!
        val initTab = Tab("新标签页")
        initTab.content = InitPane(initTab)
        nowSelectTab = initTab
        addTab = Tab().apply {
            isClosable = false
//            graphic = addButton
            graphic = getPlusSvg()
            selectedProperty().addListener { _,_,newValue ->
                if (newValue)
                    toAddTab()
            }
        }

        tabPane = TabPane(initTab,addTab).apply {
            prefWidth = InitSize.APP_WIDTH
            prefHeight = InitSize.TABPANE_HEIGHT
            tabDragPolicy = TabPane.TabDragPolicy.REORDER // 启用重新排序标签页
            /**
             *  当切换tab时将焦点设置在terminal上，以便渲染，不然会有内容需要点一下才能看到
             */
            selectionModel.selectedItemProperty().addListener { _,_,newValue ->
                nowSelectTab = newValue
                newValue.content?.let {it ->
                    if (it is MainWindow){
                        /**
                         * 如果该组件是新选项卡的一部分，那么在触发监听器之前，该选项卡上的所有组件可能还没有完全初始化，从而导致获得焦点失败。
                         * 需要新选项卡组件初始化后再请求焦点。使用Platform.runLater方法来推迟焦点请求的执行，
                         * 直到JavaFX应用程序线程处于空闲状态。这样可以确保组件已经完全初始化和布局
                         */
                        Platform.runLater {
                            it.commandArea.terminalRequestFocus()
                        }
                    }
                }
            }
            stylesheets.add(this::class.java.getResource("/css/xbss.css")?.toExternalForm())
        }
        val donate = Button().apply {
            style = "-fx-background-color:null;-fx-cursor:hand"
            graphic = ImageView(ImageIcon.DONATE24)
            setOnAction {
                val donateStage = DonateStage()
                donateStage.start(Stage())
            }
        }
        val setting = Button().apply {
            style = "-fx-background-color:null;-fx-cursor:hand"
            graphic = ImageView(ImageIcon.SET24)
            setOnAction {
                val settingPane = SettingPane()
                settingPane.start(Stage())
            }
        }
        val buttonGroup = HBox(0.0, donate, setting)
//        val anchorPane = AnchorPane(tabPane,donate,setting)
        val anchorPane = AnchorPane(tabPane,buttonGroup)
//        AnchorPane.setRightAnchor(setting,0.0)
        AnchorPane.setRightAnchor(buttonGroup,0.0)
        AnchorPane.setLeftAnchor(tabPane,0.0)
        stage.apply {
            val vBox = VBox(10.0, anchorPane)
            scene = Scene(vBox,InitSize.APP_WIDTH,840.0)
            icons.add(ImageIcon.B)
            width = InitSize.STAGE_WIDTH
            title = "BShell ${AppVersion.VERSION}"
            show()
            setOnCloseRequest {
                service.shutdown()
                tabPane.tabs.forEach {
                    it.content?.let {it -> if (it is MainWindow) it.closeAll() }
                }
                exitProcess(0)
            }
        }
        stage.toFront()
        registerShortCut()
        initSizeListener()
    }

    private fun initSizeListener() {
        tabPane.prefWidthProperty().bind(stage.scene.widthProperty())
        tabPane.prefHeightProperty().bind(stage.scene.heightProperty())
    }

    /**
     * 给tabPane加一个新的tab
     */
    private fun toAddTab() {
        tabPane.tabs.add(tabPane.tabs.size-1,Tab("新标签页").apply {
            content = InitPane(this)
        })
        tabPane.selectionModel.select(tabPane.tabs.size-2)
    }

    private fun getPlusSvg():SVGPath{
        return SVGPath().apply {
            // SVG 路径描述了一个由两条线段组成的形状，其中第一条线段从点 (10, 10) 开始，向右水平绘制，长度为 18；第二条线段从点 (18, 2) 开始，向下垂直绘制，长度也为 18。
            content = "M10 10 H26 M18 2 V18"
            stroke = Color.GRAY
//          strokeLineCap = StrokeLineCap.ROUND // 设置线段端点为圆形
            strokeWidth = 3.0 // 设置粗细
            isPickOnBounds = true
            cursor = Cursor.HAND
            hoverProperty().addListener { _,_,newValue -> stroke = if (newValue){ Color.GREEN }else Color.GRAY }
//            translateX = 4.0
            translateY = -2.0
//            this.addEventFilter(MouseEvent.MOUSE_CLICKED) {
//                toAddTab()
//                it.consume()
//            }
        }
    }

    /**
     * 注册快捷键：目前在tabPane上注册Ctrl+Tab：切换面板
     *              在stage上注册Ctrl+W：关闭当前选中面板
     *              在stage上注册Ctrl+N：添加一个面板
     *              在stage上注册双击Ctrl：切换当前面板的输入焦点
     */
    private fun registerShortCut(){
        //JavaFX的TabPane默认支持使用Ctrl+Tab来切换Tab面板,需要用addEventFilter来覆盖，因为有一个addTab不需要被选中
        tabPane.addEventFilter(KeyEvent.KEY_PRESSED){
            if (it.isControlDown && it.code == KeyCode.TAB) {
                val nextIndex = tabPane.selectionModel.selectedIndex + 1
//                tabPane.selectionModel.select(if(nextIndex>tabPane.tabs.size||nextIndex==tabPane.tabs.size-1)0 else nextIndex)
                tabPane.selectionModel.select(if(nextIndex==tabPane.tabs.size-1)0 else nextIndex)
                it.consume();
            }
        }
        // 双击事件的最短时间间隔（毫秒）
        val doubleClickTime = 800
        // 上次按下Ctrl键的时间戳
        var lastCtrlPressTime:Long = 0
        stage.scene.onKeyPressed = EventHandler{ keyEvent ->
            if (keyEvent.code == KeyCode.CONTROL){
                val now = System.currentTimeMillis()
                // 如果距离上次按下Ctrl键的时间小于最短时间间隔，则认为是双击事件
                if (now - lastCtrlPressTime < doubleClickTime) {
                    nowSelectTab.content?.let {
                        if (it is MainWindow)
                            it.commandArea.requestChangeFocus()
                    }
                }
                lastCtrlPressTime = now
            }
        }
        val kccClose= KeyCodeCombination(KeyCode.W, KeyCombination.SHORTCUT_DOWN)
        stage.scene.accelerators[kccClose] = Runnable {
            val tab = tabPane.selectionModel.selectedItem
            if (tab!=addTab)
                tabPane.tabs.remove(tab)
        }
        val kccAdd= KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN)
        stage.scene.accelerators[kccAdd] = Runnable {
            toAddTab()
        }
    }
}

fun main() {
    Application.launch(MainAPP::class.java)
}