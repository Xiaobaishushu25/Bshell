package xbss.view

import javafx.geometry.Orientation
import javafx.scene.control.SplitPane
import javafx.scene.control.TabPane
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import xbss.MainAPP
import xbss.config.InitSize
import xbss.config.Setting
import xbss.config.TextColor
import xbss.controller.FileTaskHandler
import xbss.server.mapper.pojo.Account
import xbss.ssh.SSH
import xbss.utils.SystemData

/**
 * @author  Xbss
 * @create 2023-03-22-23:41
 * @version  1.0
 * @describe :本来背景图片是用的CommandArea的背景属性的，但是这样不能设置透明度以及调整大小不方便，所以用stackpane叠加imageView当背景图片，但是发现
 * 当splitPane里面有Imageview后，不能自由拖拽了（只能拉大不能缩小），所以直接设置成MainWindow的背景得了
 */
class MainWindow(val account: Account,val ssh:SSH):AnchorPane(){
    val logger: Logger = LoggerFactory.getLogger(account.nickname)
//    val treeArea = TreeArea(this, FileTaskHandler(this),if (account.defaultPath==null)"/" else account.defaultPath!!)
    val treeArea:TreeArea
    //    private val ssh:SSH = SSH(account)
//    private val commandArea = CommandArea(ssh,this)
    val commandArea:CommandArea
//    private val treeArea = TreeArea(this,ssh.getChSftp()!!,if (account.defaultPath==null)"/" else account.defaultPath!!)
//    private val treeArea = TreeArea(this,if (account.defaultPath==null)"/" else account.defaultPath!!)
    private val systemData:SystemData //系统信息区域
    private val rightBar:RightBar //右侧通知展示区域工具条
    private lateinit var backgroundImage:ImageView
    private var splitPane: SplitPane
    private var controlInit = false //组件自身是否绘制完成
    init {
        this.prefWidth = InitSize.APP_WIDTH
        this.prefHeight = 800.0
        initBackImage()
        this.children.add(backgroundImage)

        treeArea = TreeArea(
            this,
            FileTaskHandler(this),
            if (account.defaultPath == null) "/" else account.defaultPath!!
        )
        commandArea = CommandArea(ssh,this)
        systemData = SystemData(ssh) //系统信息区域

        splitPane = SplitPane(
//            HBox(SplitPane(treeArea, commandArea).apply {
            SplitPane(treeArea, commandArea).apply {
                setDividerPositions(0.2, 0.8)
                //试出来的
                prefWidth = 1450.0 - 36.0
//                prefWidth = 1450.0
                prefHeight = 760.0
            },
            systemData
        ).apply {
            setDividerPositions(0.95, 0.05)
            orientation = Orientation.VERTICAL //两个面板竖直排列
            prefWidth = 1450.0 -36.0
        }
        this.children.addAll(splitPane)
        rightBar = RightBar(this)
        this.children.addAll(rightBar)
        setTopAnchor(rightBar,5.0)
        setRightAnchor(rightBar,0.0)
//        registerShortCut()
        writeInfoLog("${account.nickname}登录成功")
        initSizeListener()
//        this.style = "-fx-background-color:red"
//        splitPane.prefWidthProperty().bind(MainAPP.stage.widthProperty().map { it.toDouble()-20 })
//        splitPane.prefHeightProperty().bind(MainAPP.stage.heightProperty().map { it.toDouble()-20 })
    }

    /**
     * 初始化大小监听器
     * 将MainWindow与父组件的父组件（因为MainWindow父组件是TabContentRegion，但是无法强转出来）绑定大小
     * 将splitPane与父组件（MainWindow）绑定大小
     * 将backgroundImage与父组件（MainWindow）绑定大小
     */
    private fun initSizeListener() {
        this.layoutBoundsProperty().addListener { _, _, _ ->
            if (!controlInit) {
                controlInit = true
                val tabPane =
                    this.parent.parent as TabPane //this.parent ：TabPaneSkin$TabContentRegion@5d11809b[styleClass=tab-content-area]
                this.prefWidthProperty().bind(tabPane.widthProperty())
                //高度需要减去Tabpane的tab头
                this.prefHeightProperty().bind(tabPane.heightProperty().map { it.toDouble() - 30 })
                splitPane.prefWidthProperty().bind(this.prefWidthProperty())
                splitPane.prefHeightProperty().bind(this.prefHeightProperty())
                backgroundImage.fitWidthProperty().bind(this.prefWidthProperty())
                backgroundImage.fitHeightProperty().bind(this.prefHeightProperty())
            }
        }
    }
    private fun initBackImage(){
        backgroundImage  = ImageView(Setting.image)
//        if (Setting.isAutoResize.value){
//            //我也不知道为甚么要减这两个数，试出来的
//            backgroundImage.fitWidth = MainAPP.stage.width-16.0
//            backgroundImage.fitHeight = MainAPP.stage.height-20
//        }
        backgroundImage.opacityProperty().bind(Setting.opacityP)
        //0.5.3: 删除自适应选项，现在默认自适应且不可取消
//        Setting.isAutoResize.addListener { _,_,newValue ->
//            if (newValue){
//                backgroundImage.fitWidthProperty().bind(MainAPP.stage.widthProperty().map { it.toDouble()-16.0 })
//                backgroundImage.fitHeightProperty().bind(MainAPP.stage.heightProperty().map { it.toDouble()-20 })
//            }else{
//                backgroundImage.fitWidthProperty().unbind()
//                backgroundImage.fitHeightProperty().unbind()
//            }
//        }
        Setting.backgroundImagePathP.addListener { _,_,_ -> backgroundImage.image = Setting.image }
    }

    /**
     * 注册快捷键： 在stage上注册Ctrl+I（input）：命令区的输入框获得焦点
     *            在stage上注册Ctrl+T（terminal）：命令区的终端获得焦点
     *
     */
//    private fun registerShortCut(){
//        // 双击事件的最短时间间隔（毫秒）
//        val doubleClickTime = 800
//
//        // Ctrl键按下次数
//        var ctrlCount = 0
//
//        // 上次按下Ctrl键的时间戳
//        var lastCtrlPressTime:Long = 0
//        val input= KeyCodeCombination(KeyCode.I, KeyCombination.SHORTCUT_DOWN)
//    }
    fun closeAll(){
        systemData.timer?.cancel()
        ssh.close()
        commandArea.closeTerminal()
    }
    fun addMessage(pane: Pane){
        rightBar.addNotice(pane)
    }
    fun addNewLine(textColor: TextColor, text:String){
        commandArea.addNewLine(textColor,text)
    }
    fun writeInfoLog(info: String){
        MainAPP.service.submit {
            logger.info(info)
        }
    }
    fun writeErrorLog(error: String){
        MainAPP.service.submit {
            logger.error(error)
        }
    }
}