package xbss.view

import javafx.animation.*
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.embed.swing.SwingNode
import javafx.geometry.Bounds
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.shape.Rectangle
import javafx.scene.shape.SVGPath
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import javafx.stage.Popup
import javafx.util.Duration
import xbss.MainAPP
import xbss.config.AppData
import xbss.config.Setting
import xbss.config.Setting.parseColor
import xbss.config.TextColor
import xbss.myterminal.jediterm.terminal.ui.JediTermWidget
import xbss.myterminal.jediterm.terminal.ui.settings.DefaultSettingsProvider
import xbss.ssh.SSH


/**
 * @author  Xbss
 * @create 2023-03-23-11:16
 * @version  1.0
 * @describe
 */
//class CommandArea(private val ssh: SSH,private val mainWindow: MainWindow):VBox() {
class CommandArea(private val ssh: SSH,private val mainWindow: MainWindow):StackPane() {
//    private val output = ssh.getOutput() //用户获得输出用的管道
    private lateinit var widget:JediTermWidget
    private var input = ssh.getInput()  //用户输入命令用的管道
    private val popup = Popup() //用于弹出历史命令列表
    private lateinit var terminal:SwingNode
    private lateinit var textField:TextField //发送命令的textField
    private lateinit var svgPath:SVGPath //发送命令的textField
    private lateinit var commandHBox:HBox
    private lateinit var listView:ListView<String> //历史命令列表
    private val observableList:ObservableList<String> = FXCollections.observableArrayList() //历史命令列表
    private var textFieldLocal: Bounds? =null //发送命令的textField的位置
//    private var flow:Text = Text()

    private var autoFill = false //如果是自动补全的话，就别弹出补全列表了
    private var isInit = false //是否已经初始化

    init {
//        监听该组件是否已经绘制出来了
        this.boundsInLocalProperty().addListener { _,_,_ ->
            if (!isInit){
                isInit = true
                initStageShow()
                sendCommand(if (mainWindow.account.defaultPath!!.isNotEmpty()) "cd ${mainWindow.account.defaultPath};${mainWindow.account.initCommand}" else mainWindow.account.initCommand)
            }
            reLocal()
        }
        MainAPP.stage.xProperty().addListener { _,_,_ ->
            reLocal()
        }
        MainAPP.stage.yProperty().addListener { _,_,_ ->
            reLocal()
        }
        initView()
        /**
         * 当窗口从最小化恢复时将焦点给终端，因为有时终端不刷新
         */
        MainAPP.stage.iconifiedProperty().addListener { _,_,newValue ->
            if (newValue)
                terminal.requestFocus()
        }
//        initController()
    }

    /**
     *  进行一些需要界面绘制完成后才能进行的操作
     *
     */
    private fun initStageShow() {
//        textFieldLocal = textField.localToScreen(textField.boundsInLocal)
//        println(textFieldLocal)
//        textField.requestFocus()
//        val scrollPane = textArea.lookup(".scroll-pane") as ScrollPane
//        scrollPane.background = Background.EMPTY
//        val viewPort: Node = scrollPane.lookup(".viewport")
//        viewPort.style =  "-fx-background-color: transparent;"
//        val content = scrollPane.content as Region
//        content.style = "-fx-background-color:transparent"
//        val backgroundImage = BackgroundImage(
////            Image(this::class.java.getResourceAsStream("/img/back.png"),textFieldLocal!!.width,textFieldLocal!!.height,false,true),  // 图片路径
//            Image(this::class.java.getResourceAsStream("/img/back.png")),  // 图片路径
//            BackgroundRepeat.NO_REPEAT,  // 图片重复方式
//            BackgroundRepeat.NO_REPEAT,
//            BackgroundPosition.DEFAULT,
//            BackgroundSize.DEFAULT
//        )
//        val background = Background(backgroundImage)
//        scrollPane.background = background

//        this.background = background
//        this.children.add(0,backgroundImage)
        MainAPP.highlightIcon()
        terminal.requestFocus() //这句很重要，不获取焦点会有一大块黑的像是没绘制的地方（其实是绘制了但是不知道为甚么还是全黑）
    }


    private fun initView() {
        initTerminal()
        initCommandHBox()
        val vbox = VBox().apply {
            padding = Insets(15.0)
            spacing = 10.0
            children.addAll(
                terminal,
//                textField,
                commandHBox,
            )
        }
        this.children.addAll(vbox)
//        this.prefWidth = InitSize.COMMANDAREA_WIDTH
    }

    /**
     *  textField改变位置后重新定位listview应该出现的位置
     *
     */
    private fun reLocal(){
        textFieldLocal = textField.localToScreen(textField.boundsInLocal)
    }

    private fun initListView() {
        listView = ListView<String>(observableList).apply {
            prefWidth = 500.0
            setCellFactory {
                val cell = object : ListCell<String>(){
                    override fun updateItem(item: String?, empty: Boolean) {
                        super.updateItem(item, empty)
                        graphic = if (item!=null){
                            val textFlow = TextFlow()
                            val children = textFlow.children
                            highlightKeyword(item,textField.text).forEach {
                                if (it.first){
                                    children.add(Text(it.second).apply { style = "-fx-font-size:13px;-fx-fill:red" })
                                }else {
                                    children.add(Text(it.second).apply {  style = "-fx-font-size:13px;" })
                                }
                            }
//                            val newItem = item.replace(textField.text, "-|||-|", true)
//                            newItem.split("-|").apply {
//                                for(s in this){
//                                    if(s == "||"){
//                                        children.add(Text(textField.text).apply { fill=Color.RED })
//                                    }else if (s.isNotEmpty()){
//                                        children.add(Text(s))
//                                    }
//                                }
//                            }
//                            Label(item).apply {
//                            prefHeight = 25.0
//                            //双击命令可以直接选择
//                            setOnMouseClicked {
//                                if (it.clickCount==2){
//                                    autoFill = true
//                                    textField.text = item
//                                    textField.positionCaret(this.text.length)
//                                    popup.hide()
//                                }
//                            }
//                        }
                            textFlow.apply {
                                prefHeight = 20.0
                                //双击命令可以直接选择
                                setOnMouseClicked {
                                    if (it.clickCount==2){
                                        autoFill = true
                                        textField.text = item
                                        textField.positionCaret(item.length)
                                        popup.hide()
                                    }
                                }
                            }
                        } else null
                    }
                }
                cell.focusedProperty().addListener { _,_,newValue ->
//                    cell.style = if (newValue) "-fx-background-color:#2e436e;" else "-fx-background-color:null"
                    cell.style = if (newValue) "-fx-background-color:#7AC5CD;" else "-fx-background-color:null"
                }
                cell.selectedProperty().addListener { _,_,newValue ->
//                    cell.style = if (newValue) "-fx-background-color:#2e436e;" else "-fx-background-color:null"
                    cell.style = if (newValue) "-fx-background-color:#7AC5CD;" else "-fx-background-color:null"
                }
                cell
            }
        }
        popup.content.add(listView)
    }

    private fun initTextField() {
        textField = TextField().apply {
            style = "-fx-background-color: #FFFACD00;-fx-font-size:16px;-fx-prompt-text-fill:#8B4500"
            promptText = "TAB补全、回车发送、delete删除命令、双击Ctrl切换输入焦点"
            textProperty().addListener { _,_,newValue ->
                if (newValue!=null&&newValue.isNotEmpty()){
                    //采用popup的方法后，弹出 listView后它会自动获得焦点，但是同时textfield的焦点也存在，但是只能输入，其他键盘事件都没有
                    //所以只能在listview上处理了
                    observableList.clear()
                    observableList.addAll(AppData.historyCommand.filter { it.command.contains(newValue,true) }.map { it.command })
                    listViewReDraw()
                    autoFill = false
                }else
                    popup.hide()
            }
            listView.setOnKeyPressed {
                when(it.code){
                    KeyCode.LEFT -> positionCaret(caretPosition-1)
                    KeyCode.RIGHT -> positionCaret(caretPosition+1)
                    KeyCode.HOME -> positionCaret(0)
                    KeyCode.END -> positionCaret(this.text.length)
                    KeyCode.ENTER -> sendCommand()
                    KeyCode.TAB -> {
                        autoFill = true
                        this.text = listView.focusModel.focusedItem
                        positionCaret(this.text.length)
                        popup.hide()
                    }
                    KeyCode.DELETE -> {
                        val text = listView.focusModel.focusedItem
                        observableList.removeIf { it.equals(text) }
                        listViewReDraw()
                        AppData.deleteCommand(text)
                        it.consume()
                    }
                    else -> {}
                }
            }
            addEventFilter(KeyEvent.KEY_PRESSED) {
                when(it.code) {
                    KeyCode.ENTER -> {
                        sendCommand()
                        it.consume()
                    }
                    KeyCode.BACK_SPACE -> {
                        if (it.isControlDown){
                            text = ""
                            it.consume()
                        }
                    }
                    else -> {}
                }
            }
            focusedProperty().addListener { _,_,newValue ->
                if(newValue)
                    reLocal()
                if (!newValue)
                    popup.hide()
            }
        }
    }

    private fun initTerminal() {
        //terminal的设置一般在两个类里面，JediTermWidget和DefaultSettingsProvider
        val settingsProvider = DefaultSettingsProvider()
        settingsProvider.initTerminalFontSize(Setting.terminalFontSizeP.value)
        settingsProvider.initCursorColor(Setting.terminalCursorColorP.value.parseColor())
        settingsProvider.initSelectionStyle(Setting.terminalTextColorSelecting, Setting.terminalBackColorSelecting)
        settingsProvider.initTextColorUnSelected(Setting.terminalTextColorUnSelected)
        settingsProvider.initTerminalBackColorSelecting(Setting.terminalBackColorSelecting)
        //这个列是影响每行最多字符数,行数貌似影响高度
        widget = JediTermWidget(119, 54, settingsProvider)
        widget.ttyConnector = ssh.getPtyProcessTtyConnector()
        widget.start()
        terminal= SwingNode()
        terminal.content = widget
        widget.terminalTextBuffer.myWidthProperty.addListener{ _,_,newValue -> ssh.setPtySize(newValue.toInt()) }
        Setting.terminalFontSizeP.addListener { _, _, newValue ->
            widget.setTerminalFontSize(newValue.toFloat())
        }
        Setting.terminalCursorColorP.addListener { _, _, newValue ->
            widget.setCursorStyle(newValue.parseColor())
        }
    }
    private fun initCommandHBox(){
        initListView()
        initTextField()
        initSvg()
        val stop = Rectangle(16.0, 16.0, Paint.valueOf("#FF0000")).apply {
            style = "-fx-cursor:hand"
            setOnMouseClicked {
//                svgPath.fill = Color.RED
                startClickAnimation()
                ssh.stopNowProcess()
            }
        }
        svgPath.setOnMouseClicked {
            svgPath.startClickAnimation()
            reConnect()
//            println("现在的终端宽度是${widget.width}  ${widget.terminalTextBuffer.width}")
        }
        commandHBox = HBox(8.0,textField,stop,svgPath).apply {
            style = "-fx-background-color: #EEE5DE60;-fx-background-radius: 4;"
            HBox.setHgrow(textField,Priority.ALWAYS)
//            HBox.setMargin(stop, Insets(8.0,10.0,8.0,0.0))
//            HBox.setMargin(svgPath, Insets(3.0,10.0,0.0,0.0))
            HBox.setMargin(svgPath, Insets(0.0,10.0,0.0,0.0))
//            style = "-fx-border-width:1px;-fx-border-color:#B0B0B0;"
//            alignment = Pos.BOTTOM_RIGHT
            alignment = Pos.CENTER
        }
    }

    private fun initSvg() {
        svgPath = SVGPath()
        svgPath.content = "M 18 0 L 0 12 L 13 15 L 12 24 L 26 12 L 16 10 L 18 0"
        svgPath.fill = Paint.valueOf("#00FF00")
        svgPath.cursor = Cursor.HAND
        svgPath.isPickOnBounds = true
//        svgPath.setOnMouseClicked {
//            svgPath.startClickAnimation()
//        }
        ssh.isConnectProperty.addListener { _,_,newValue ->
            Platform.runLater {
                svgPath.fill = if (newValue) Paint.valueOf("#00FF00") else Color.RED
            }
        }
        registerReConnectListener()
    }

    /**
     * 发送当前文本框的命令，要注意在后面手动加个\n
     *
     */
    private fun sendCommand(initialCommand:String? = null){
        val command = initialCommand?.let { it }?:run{textField.text}
//        val command = textField.text
//        input.write("$command\r".toByteArray())
        mainWindow.writeInfoLog("执行linux命令：$command")
        input.write("$command\n".toByteArray())
        AppData.addCommand(command)
        textField.text = ""
    }

    /**
     * 当observableList改变后应该调用此方法，重绘listview，并计算出现的位置
     *
     */
    private fun listViewReDraw(){
        listView.prefHeight = (30.0 * observableList.size).coerceAtMost(300.0)
        if (observableList.size == 0)
            popup.hide()
//        if(!popup.isShowing&&!autoFill&&observableList.size>0)
        if(!autoFill&&observableList.size>0){
//            println("补全列表应该在${textFieldLocal!!.minY-listView.prefHeight}出现")
            popup.show(this,textFieldLocal!!.minX,textFieldLocal!!.minY-listView.prefHeight)
        }
        listView.focusModel.focus(0) //有焦点时整个item变蓝色高亮
        listView.selectionModel.select(0) //有select时整个item有个淡蓝色边框？？不太确定，反正这两个控制边框和高亮
    }

    /**
     * 抖动效果，node向下并向右1.5，同时缩小至0.8，然后弹回来
     */
    private fun Node.startClickAnimation(){
        val duration = Duration.seconds(0.02)
        val scaleDuration = Duration.seconds(0.02)
        val scale = 0.8
        val scaleTransition = ScaleTransition(scaleDuration, this)
        scaleTransition.toX = scale
        scaleTransition.toY = scale

        val translateTransition = TranslateTransition(duration, this)
        translateTransition.byX = 1.5
        translateTransition.byY = 1.5

        val parallelTransition = ParallelTransition(scaleTransition, translateTransition)

        val scaleBackTransition = ScaleTransition(scaleDuration, this)
        scaleBackTransition.toX = 1.0
        scaleBackTransition.toY = 1.0

        val translateBackTransition = TranslateTransition(duration, this)
        translateBackTransition.byX = -1.5
        translateBackTransition.byY = -1.5

        val sequenceTransition = SequentialTransition(parallelTransition, PauseTransition(duration),
            SequentialTransition(scaleBackTransition, translateBackTransition)
        )
        sequenceTransition.play()
    }

    /**
     * 以指定的样式添加一行text
     * @param textStyle
     * @param text
     */
    fun addNewLine(textColor: TextColor, text:String){
        widget.addNewLine(textColor.getTextStyle(),text)
    }

    /**
     * 添加重连监听
     */
    private fun registerReConnectListener(){
        ssh.successP.addListener{ _, _, newValue ->
            Platform.runLater {
                newValue.ssh?.let {
                    //这里要添加一个新空行，不然读到的第一行会跟在提示的后面
                    widget.addNullNewLine()
                    widget.ttyConnector = ssh.getPtyProcessTtyConnector()
                    widget.reStart()
                    input = ssh.getInput()
                    sendCommand(if (mainWindow.account.defaultPath!!.isNotEmpty()) "cd ${mainWindow.account.defaultPath};${mainWindow.account.initCommand}" else mainWindow.account.initCommand)
                } ?: run {
                    MainAPP.highlightIcon()
                    addNewLine(TextColor.INFO, newValue.tip!!)
                }
            }
        }
    }

    /**
     * 重新连接
     */
    private fun reConnect(){
        if (ssh.isConnectProperty.value){
            ssh.close()
            addNewLine(TextColor.INFO,"关闭连接...")
        }
        else
            ssh.reConnect()
    }

    /**
     * 切换焦点
     */
    fun requestChangeFocus(){
        if (textField.isFocused)
            terminal.requestFocus()
        else
            textField.requestFocus()

    }
    fun terminalRequestFocus() = terminal.requestFocus()
    fun closeTerminal(){
        widget.close()
    }

    /**
     * 给定一个关键词和输入，忽略关键词大小写将输入拆分。
     * @param input
     * @param keyword
     * @return 一个列表，item是pair，第一个Boolean表示此字符串是否需要高亮
     */
    fun highlightKeyword(input: String, keyword: String): List<Pair<Boolean,String>> {
        val regex = Regex("(?i)($keyword)")
        val matchResult = regex.find(input)
        if (matchResult != null) {

            val startIndex = matchResult.range.first
            val endIndex = matchResult.range.last + 1
            val beforeKeyword = input.substring(0, startIndex)
            val highlightedKeyword = input.substring(startIndex, endIndex)
            val afterKeyword = input.substring(endIndex)

            return listOf(Pair(false,beforeKeyword),Pair(true,highlightedKeyword),Pair(false,afterKeyword))
        }
        return listOf(Pair(false,input))
    }

}