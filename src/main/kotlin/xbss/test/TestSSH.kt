//package xbss.test
//
//import com.jcraft.jsch.Channel
//import com.jcraft.jsch.ChannelSftp
//import com.jcraft.jsch.JSch
//import javafx.application.Application
//import javafx.application.Platform
//import javafx.scene.Node
//import javafx.scene.Scene
//import javafx.scene.control.*
//import javafx.scene.image.Image
//import javafx.scene.image.ImageView
//import javafx.scene.input.KeyCode
//import javafx.scene.layout.*
//import javafx.scene.text.Font
//import javafx.scene.text.FontWeight
//import javafx.stage.Stage
//import xbss.utils.LsTreeItem
//import xbss.config.ImageIcon
//import xbss.config.FileType
//import java.io.ByteArrayOutputStream
//import java.io.PipedInputStream
//import java.io.PipedOutputStream
//import java.util.*
//import kotlin.concurrent.thread
//
//
///**
// * @author  Xbss
// * @create 2022-12-24-17:35
// * @version  1.0
// * @descirbe
// */
//class TestSSH:Application() {
//
////    val input = "".byteInputStream()
//    val output = ByteArrayOutputStream()
//    var output2:PipedOutputStream
//    var jschChannel:Channel
//    var chSftp:ChannelSftp
//    lateinit var textArea:TextArea
//    init {
//        var username = "msfgroup"
//        var password = "msfg302"
//        var host = "10.255.248.48"
//        var port = 22
//        val jSch = JSch()
//        var result = false
//        val session = jSch.getSession(username, host, port)
//        session.setPassword(password)
//        val properties = Properties().apply {
//            put("StrictHostKeyChecking", "no")
//        }
//        session.setConfig(properties)
//        session.timeout = 6000
//        session.connect()
//        val reulst = session.isConnected
//        if (reulst) {
//            println("【SSH连接】连接成功");
//        } else {
//            println("【SSH连接】连接失败");
//        }
//        jschChannel = session.openChannel("shell")
//        chSftp = session.openChannel("sftp") as ChannelSftp
//        chSftp.connect()
//        val input = PipedInputStream()
//        output2 = PipedOutputStream()
//        input.connect(output2)
//        jschChannel.outputStream = output
//        jschChannel.inputStream = input
//        jschChannel.connect();
//    }
//    override fun start(primaryStage: Stage?) {
//        val stage = primaryStage!!
//        val textField = TextField().apply {
//            setOnKeyPressed {
//                if (it.code.equals(KeyCode.ENTER))
//                    output2.write("${this.text}\r".toByteArray())
//            }
//        }
//        textArea = TextArea().apply {
//            style = "-fx-font-size:15px"
//            font = Font.font("Monospaced", FontWeight.NORMAL, 12.0)
//            focusedProperty().addListener { _,_,_ ->
//                font = Font.font("Monospaced", FontWeight.NORMAL, 12.0)
//            }
//            isWrapText = false
//            isEditable = false
//        }
//
//        textArea.prefHeight = 400.0
//        thread {
//            while (true){
////                val newmessage = output.toString()
//////                if (newmessage.isNotEmpty()){
//////                    println(newmessage)
//////                    textArea.appendText(newmessage)
//////                    output.reset()
//////                }
////                if (newmessage!=message){
////                    message = newmessage
////                    println(message)
////                    textArea.text = message
////                }
//                // 在本端异步读取数据
//                val sequence = generateSequence {
//                    val data = output.toString(charset("utf-8"))
//                    output.reset()
//                    data.ifEmpty { null }
//                }
//                val iterator = sequence.iterator()
//                while (iterator.hasNext()) {
//                    val data = iterator.next()
//                    // 处理读取到的数据
////                    println(data)
//                    Platform.runLater {
//                        //这里快速appendText时会报错，放在Platform.runLater就不会了
//                        textArea.appendText(data)
//                    }
//                }
////                println("循环")
//            }
//        }
////        val treeView = TreeView<String>()
//        val treeView = TreeView<LsTreeItem.FileItem>()
//        val lsTreeItem = LsTreeItem(chSftp, "/")
//        treeView.setCellFactory {
////            object :TreeCell<String>(){
//            object :TreeCell<LsTreeItem.FileItem>(){
//                //                override fun updateItem(p0: String?, p1: Boolean) {
//                override fun updateItem(item: LsTreeItem.FileItem?, empty: Boolean) {
//                    super.updateItem(item, empty)
//                    graphic = if (!empty){
//                        val imageView = ImageView()
////                        val fullName = p0.split("|")
//                        when(item!!.fileType){
//                            FileType.PY -> imageView.image = ImageIcon.PY
//                            FileType.IMG -> imageView.image = ImageIcon.IMAGE
//                            FileType.FOLDER -> imageView.image = ImageIcon.FOLDER
//                            FileType.FILE -> imageView.image = ImageIcon.FILE
//                            FileType.TXT -> imageView.image = ImageIcon.TXT
//                            FileType.MD -> imageView.image = ImageIcon.MD
//                            FileType.VIDEO -> imageView.image = ImageIcon.VIDEO
//                            FileType.DENY -> imageView.image = ImageIcon.DENY
//                            else -> { }
//                        }
//                        val label = Label(item.fileName)
//                        val contextMenu = ContextMenu()
//                        val runItem = MenuItem("运行")
//                        runItem.setOnAction {
//                            println("运行py文件")
//                        }
//                        contextMenu.items.add(runItem)
//                        this.setOnContextMenuRequested {
//                            contextMenu.show(label, it.screenX+3.0, it.screenY+3.0)
//                        }
//                        HBox(5.0,imageView,label)
//                    }else
//                        null
//                }
//                //                override fun updateItem(p0: LsTreeItem.FileItem?, p1: Boolean) {
////                    super.updateItem(p0, p1)
////                    p0?.let {
////                        val imageView = ImageView()
//////                        val fullName = p0.split("|")
////                        when()
////                        if (fullName.size>1){
////                            when(fullName[1]){
////                                FileType.PY.name -> imageView.image = ImageIcon.PY
////                                FileType.IMG.name -> imageView.image = ImageIcon.IMAGE
////                                FileType.FOLDER.name -> imageView.image = ImageIcon.FOLDER
////                                FileType.FILE.name -> imageView.image = ImageIcon.FILE
////                                FileType.TXT.name -> imageView.image = ImageIcon.TXT
////                                FileType.MD.name -> imageView.image = ImageIcon.MD
////                                FileType.VIDEO.name -> imageView.image = ImageIcon.VIDEO
////                                FileType.DENY.name -> imageView.image = ImageIcon.DENY
////                                else -> { }
////                            }
////                        }
////                        val label = Label(fullName[0])
////                        val contextMenu = ContextMenu()
////                        val runItem = MenuItem("运行")
////                        runItem.setOnAction {
////                            println("运行py文件")
////                        }
////                        contextMenu.items.add(runItem)
////                        label.setOnContextMenuRequested {
////                            contextMenu.show(label, it.screenX, it.screenY)
////                        }
////                        graphic = HBox(5.0,imageView,label)
////                    }
////                    if (p1)
////                        graphic = null
////                }
//            }
//        }
//        treeView.root = lsTreeItem
//        val button = Button("发送").apply {
//            setOnAction {
////                println(textArea.text)
////                input.read("${textArea.text}\r\n".encodeToByteArray())
////                jschChannel.inputStream = textArea.text.byteInputStream()
////                jschChannel.inputStream = "${textArea.text}\r\n".byteInputStream()
////                println("输出是${output.toString()}")
////                output2.write("${textField.text}\n\r".toByteArray())
//                output2.write("${textField.text}\r".toByteArray())
//            }
//        }
//        val add = Button().apply {
//            setOnAction {
//
//            }
//        }
//        add.graphic = ImageView(Image(TestSSH::class.java.getResourceAsStream("/img/add24.png")))
//        val hBox = VBox(textArea,HBox(10.0,textField,button,add),treeView)
////        val hBox = VBox(StackPane(view,textArea),HBox(10.0,textField,button,add),treeView)
////        val hBox = VBox(textArea,HBox(10.0,textField,button))
//        stage.scene = Scene(hBox,1500.0,850.0).apply {
//            stylesheets.add("css/xbss.css")
//        }
////        stage.setOnShown { println("setOnShowng") }
//        stage.setOnShown {
////            //todo 场景渲染出来了，进行一些操作，计算textfield位置、设置背景
//            val local = textField.localToScreen(textArea.boundsInLocal)
//            val scrollPane = textArea.lookup(".scroll-pane") as ScrollPane
//            val viewPort: Node = scrollPane.lookup(".viewport")
//            viewPort.style =  "-fx-background-color: transparent;"
//            val content = scrollPane.content as Region
//            content.style = "-fx-background-color:#87CEFA00"
//            val backgroundImage = BackgroundImage(
//                Image(this::class.java.getResourceAsStream("/img/back.png"),local.width,local.height,false,true),  // 图片路径
//                BackgroundRepeat.NO_REPEAT,  // 图片重复方式
//                BackgroundRepeat.NO_REPEAT,
//                BackgroundPosition.DEFAULT,
//                BackgroundSize.DEFAULT
//            )
//            val background = Background(backgroundImage)
//            scrollPane.background = background
////            content.background = background
//        }
//        stage.show()
//    }
//}
//
//fun main() {
//    Application.launch(TestSSH::class.java)
//}