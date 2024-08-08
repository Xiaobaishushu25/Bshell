package xbss.view

import javafx.animation.FadeTransition
import javafx.animation.Timeline
import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Bounds
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Tab
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton
import javafx.scene.layout.*
import javafx.scene.paint.Paint
import javafx.scene.shape.Circle
import javafx.stage.Stage
import javafx.util.Duration
import xbss.MainAPP
import xbss.config.AppData
import xbss.config.TextColor
import xbss.server.mapper.pojo.Account
import xbss.ssh.SSH
import xbss.utils.InitPaneRightMenu
import xbss.utils.PopConfirm

/**
 * @author  Xbss
 * @create 2023-03-24-0:12
 * @version  1.0
 * @describe
 */
class InitPane(private val tab: Tab):AnchorPane() {
    companion object{
        val green:Paint = Paint.valueOf("#00FF00")
        val red:Paint = Paint.valueOf("#FF0000")
    }
    private lateinit var vBox:VBox //放所有的账户item
    private lateinit var ft:FadeTransition //登录动画
//    private val popup = Popup()
    private lateinit var bounds: Bounds //弹窗弹出的位置依据
    private var mainWindow:MainWindow ?= null
    private var tabAccount:Account ?= null
    private var loadMessage = SimpleStringProperty("")
    init {
        tab.setOnCloseRequest {
            closeTab()
        }
        initView()
    }

    private fun initView() {
        initVBox()
        val tip = Label().apply {
            style = "-fx-font-size: 17px;-fx-text-fill:#FF8247; -fx-border-width: 0 0 2 0"
            textProperty().bind(loadMessage)
        }
        this.children.addAll(vBox,tip)
        setLeftAnchor(vBox,450.0)
        setLeftAnchor(tip,470.0)
        setTopAnchor(vBox,250.0)
        setTopAnchor(tip,210.0)
//        this.minWidthProperty().bind(MainAPP.stage.widthProperty().map { it.toDouble()-20 })
//        this.minWidthProperty().bind(MainAPP.stage.heightProperty().map { it.toDouble()-20 })
    }

    private fun initVBox() {
        val label = Label("快速连接").apply { style = "-fx-font-size: 17px;-fx-font-weight:bold" }
        val connect = Button("连接").apply {
            style = " -fx-border-radius: 3px;-fx-background-radius:3px;-fx-cursor: hand; -fx-font-size: 15px;-fx-background-color: #00BFFF;"
            setOnAction {
                val newConnect = NewConnect()
                newConnect.start(Stage())
                newConnect.success.addListener { _,_,_ -> loadItems() }
            }
        }
        val pane = Pane()
        val hBox = HBox(label,pane,connect).apply {
            padding = Insets(10.0)
            prefWidth = 400.0
            prefHeight = 26.0
            style = "-fx-background-color:#BEBEBE;-fx-background-radius:2px"
            HBox.setHgrow(pane,Priority.ALWAYS)
            alignment = Pos.CENTER
        }
        vBox = VBox(hBox)
        loadItems()
    }
    private fun initItem(account: Account):HBox{
        return HBox(10.0,
            getLabel(account.nickname,200.0),
            getLabel(account.host,200.0),
            ).apply {
            setOnContextMenuRequested {
                InitPaneRightMenu(this@InitPane,account,this).show(this,it.screenX, it.screenY)
            }
            padding = Insets(5.0)
            addMouseListen()
            setOnMouseClicked {
                if (it.button==MouseButton.PRIMARY){
                    val node = this
                    loading(node)
                    tabAccount = account
                    SSH(account).apply {
//                        Platform.runLater { loadMessage.bind(progressP) }
                        progressP.addListener { _, _, newValue ->
                            Platform.runLater { loadMessage.value = newValue }
                        }
                        successP.addListener{ _, _, newValue ->
                            Platform.runLater {
                                loaded(node)
                                newValue.ssh?.let {
                                    if (firstConnect){
//                                        loadMessage.value = "正在初始化界面..."
                                        mainWindow = MainWindow(account,it)
                                        tab.content = mainWindow
                                        tab.text = account.nickname
                                        tab.reStatus(true)
                                        registerObserve()
                                        firstConnect = false
                                    }
                                }?: run {
                                    MainAPP.highlightIcon()
                                    println(newValue.tip)
                                }
                            }
                        }
                        this.initSSH()
                        println("this.initSSH()")
                    }
                    it.consume()
//                mainWindow = MainWindow(account)
//                tab.content = mainWindow
//                tab.text = account.nickname
//                tab.reStatus(true)
                }
            }
            setOnKeyPressed {
                if (it.code.equals(KeyCode.DELETE)){
                    bounds = this.localToScreen(this.boundsInLocal)
                    val popConfirm = PopConfirm()
//                    popup.content.add(popConfirm)
                    popConfirm.show(this,bounds.minX+bounds.width/3,bounds.maxY)
                    popConfirm.choose.addListener { _,_,newValue ->
                        if (newValue == 2){
                            //这里看似是按照账户删的，其实是按照id属性删的
                            AppData.deleteAccountById(account)
                            loadItems()
                        }
                    }
                }
            }
        }
    }
    private fun loading(node:Pane){
        node.removeMouseListen()
        vBox.isDisable = true
        ft = FadeTransition(Duration.millis(2000.0), node).apply {
            fromValue = 1.2
            toValue = 0.6
            cycleCount = Timeline.INDEFINITE
            isAutoReverse = true
            play()
        }
    }
    private fun loaded(node:Pane){
        ft.stop()
        vBox.isDisable = false
        node.addMouseListen()
    }

    /**
     * 给item注册鼠标移入移出事件
     *
     */
    private fun Pane.addMouseListen(){
        this.setOnMouseEntered {
            style = "-fx-background-color:#00CED1;-fx-cursor:hand"
            this.requestFocus() //必须先获得焦点，不然无法获取键盘事件
        }
        this.setOnMouseExited {
            style = "-fx-background-color:null"
        }
    }

    /**
     * 先后顺序不能换
     */
    private fun Pane.removeMouseListen(){
        this.setOnMouseExited { }
        this.style = "-fx-background-color:#FF6347"
//        this.children.forEach { it.style = "-fx-text-fill:red;-fx-font-size:16px" }
    }

    /**
     * 第一次、账户数量有变动后调用此函数重新加载账户列表
     *
     */
    fun loadItems() {
        vBox.apply {
            val hbox = children[0]
            children.clear()
            children.add(hbox)
            AppData.accounts.forEach { children.add(initItem(it)) }
        }
    }
    private fun getLabel(text: String,width:Double = 150.0):Label{
        return Label(text).apply {
            prefWidth = width
            prefHeight = 25.0
            style = "-fx-font-size:16px"
        }
    }

    /**
     * 建立连接后启动监视，当连接断开时更新tab
     */
    private fun registerObserve(){
        mainWindow?.ssh!!.isConnectProperty.addListener { _,_,newValue ->
            Platform.runLater {
                tab.reStatus(newValue)
                if (!newValue)
                    mainWindow?.addNewLine(TextColor.RED,"连接断开")
//                    mainWindow?.commandArea?.addNewLine(TextColor.RED,"连接断开")
                else
                    mainWindow?.addNewLine(TextColor.GREEN,"连接成功")
            }
        }
    }

    /**
     * 设置、更新面板上的连接指示状态以及连接名
     * @param connect
     */
    private fun Tab.reStatus(connect:Boolean){
        val nickName = Label(tabAccount!!.nickname)
//        val nickName = if (!connect)Label("断开连接") else Label(this.text)
        this.graphic = HBox(5.0, Circle(5.0).apply {
            fill = if (connect) green else red
            setOnMouseClicked {
                if (it.button.equals(MouseButton.SECONDARY)) {
                    val newConnect = NewConnect(tabAccount!!)
                    newConnect.start(Stage())
                    newConnect.update.addListener { _, _, newValue ->
                        nickName.text = newValue
                        // 这一步是为了连续改时能及时看到新的数据
                        tabAccount = AppData.queryAccount(tabAccount!!.id)
                    }
                }
            }
        }, nickName).apply { alignment = Pos.CENTER }
        this.text = "" //标签页名用label写了，就不需要了
    }
    private fun closeTab(){
        mainWindow?.closeAll()
    }
}