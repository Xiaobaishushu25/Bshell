package xbss.view

import javafx.application.Platform
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.*
import javafx.scene.layout.*
import javafx.stage.Stage
import xbss.MainAPP
import xbss.config.FileType
import xbss.config.ImageIcon
import xbss.config.InitSize
import xbss.controller.FileTaskHandler
import xbss.utils.*
import java.io.ByteArrayInputStream

/**
 * @author  Xbss
 * @create 2023-03-23-20:05
 * @version  1.0
 * @describe
 */
class TreeArea(mainWindow: MainWindow,val taskHandler: FileTaskHandler,private var defaultPath:String = "/"): VBox() {
    val ssh = mainWindow.ssh

    /**
     * 路径指示文本框
     */
    private lateinit var pathIndication:TextField
    private lateinit var cdHBox: HBox
    private val treeView = TreeView<LsTreeItem.FileItem>()
    private lateinit var rootTreeItem: LsTreeItem
    /**
     * 右键的菜单
     */
    private var treeAreaRightMenu: TreeAreaRightMenu ?=null
//    private var waitToCopyPathList:List<String> ?= null

    init {
        this.boundsInLocalProperty().addListener { _,_,_ ->
            reDraw()
        }
        initView()
    }

    private fun initView() {
        initFileTree()
        initPathHBox()
        this.apply {
            padding = Insets(5.0,0.0,0.0,5.0)
            spacing = 5.0
            children.addAll(
                cdHBox,
                treeView
            )
        }
    }
    private fun reDraw(){
        pathIndication.prefWidth = this.width - 16*2
    }
    private fun initPathHBox() {
        initTextField()
        val copy = Button().apply {
            translateY = 3.0
            style = "-fx-cursor:hand"
            tooltip = Tooltip("复制当前路径").apply {
                style = "-fx-background-color: #FFFACD;-fx-text-fill:black;-fx-font-size: 13"
            }
            background = Background(BackgroundImage(
                ImageIcon.COPY16,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                null,
                null))
            setOnAction {
                val content = ClipboardContent()
                content.putString(pathIndication.text)
                Clipboard.getSystemClipboard().setContent(content)
                treeView.scrollTo(treeView.selectionModel.selectedIndex)
            }
        }
        val up = Button().apply {
            translateY = 3.0
            style = "-fx-cursor:hand"
            tooltip = Tooltip("返回父目录").apply {
                style = "-fx-background-color: #FFFACD;-fx-text-fill:black;-fx-font-size: 13"
            }
            background = Background(BackgroundImage(
                ImageIcon.UPTO,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                null,
                null))
            setOnAction {
                val parent = treeView.focusModel.focusedItem.parent
                treeView.selectionModel.clearSelection()
                treeView.selectionModel.select(parent)
                treeView.scrollTo(treeView.selectionModel.selectedIndex)
            }
        }
        val update = Button().apply {
            translateY = 3.0
            translateX = -3.0
            style = "-fx-cursor:hand"
            tooltip =
                Tooltip("更新").apply { style = "-fx-background-color: #FFFACD;-fx-text-fill:black;-fx-font-size: 13" }
            background = Background(BackgroundImage(
                ImageIcon.UPDATE,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                null,
                null))
            setOnAction {
                refreshItem()
            }
        }
        copy.alignment = Pos.BOTTOM_RIGHT
        cdHBox = HBox(5.0,pathIndication,copy,up,update).apply {
            style = "-fx-border-width:1px;-fx-border-color:#B0B0B0;"
            alignment = Pos.BOTTOM_RIGHT
        }
    }

    private fun initTextField() {
        pathIndication = TextField().apply {
            border = Border.EMPTY
            background = Background.EMPTY
            textProperty().bind(treeView.selectionModel.selectedItemProperty().map { it.value.path })
        }
        pathIndication.tooltip = Tooltip().apply {
            textProperty().bind(pathIndication.textProperty())
            style = "-fx-background-color: #FFFACD;-fx-text-fill:black;-fx-font-size: 13" }
    }
    private fun initFileTree() {
////        rootTreeItem = LsTreeItem(ssh, "/")
////        treeView.root = rootTreeItem
//        rootTreeItem = LsTreeItem(ssh, "/")
//
//        treeView.root = rootTreeItem
//        rootTreeItem.isExpanded = true
//        expandItem(defaultPath)
        rootTreeItem = LsTreeItem("正在初始化.deny")
        treeView.root = rootTreeItem
        MainAPP.service.submit {
            rootTreeItem = LsTreeItem(ssh, "/")
            Platform.runLater {
                treeView.root = rootTreeItem
                rootTreeItem.isExpanded = true
                expandItem(defaultPath)
            }
        }
        treeView.setCellFactory {
            val label = Label()
            val cell = object : TreeCell<LsTreeItem.FileItem>() {
                override fun updateItem(item: LsTreeItem.FileItem?, empty: Boolean) {
                    super.updateItem(item, empty)
                    graphic = if (!empty) {
                        label.text = item!!.fileName
                        val imageView = ImageView()
                        when (item.fileType) {
                            FileType.PY -> imageView.image = ImageIcon.PY
                            FileType.RS -> imageView.image = ImageIcon.RUST
                            FileType.JAVA -> imageView.image = ImageIcon.JAVA
                            FileType.KOTLIN -> imageView.image = ImageIcon.KT
                            FileType.GO -> imageView.image = ImageIcon.GO
                            FileType.IMG -> imageView.image = ImageIcon.IMAGE
                            FileType.FOLDER -> imageView.image = ImageIcon.FOLDER
                            FileType.FILE -> imageView.image = ImageIcon.FILE
                            FileType.TXT -> imageView.image = ImageIcon.TXT
                            FileType.MD -> imageView.image = ImageIcon.MD
                            FileType.VIDEO -> imageView.image = ImageIcon.VIDEO
                            FileType.DENY -> imageView.image = ImageIcon.DENY
                            else -> {
                                imageView.image = ImageIcon.FILE
                            }
                        }
                        this.setOnMouseClicked {
                            if (it.clickCount == 2) {
                                when (item.fileType) {
                                    FileType.IMG -> {
                                        val sftp = ssh.getChSftpOrNull()
                                        sftp?.let {
                                            try {
                                                val inputStream = it.get(item.path)
                                                val bytes = inputStream.readBytes() // 将 InputStream 中的数据缓存到内存中
                                                ShowImage(
                                                    ImageView(Image(ByteArrayInputStream(bytes))),
                                                    item.fileName
                                                ).start(Stage())
                                                inputStream.close()
                                            } catch (e: Exception) {
                                                println("捕获到异常 $e")
                                            } finally {
                                                ssh.releaseChannel(it)
                                            }
                                        }

                                    }
                                    //下面这些双击用文本编辑器打开
                                    FileType.TXT, FileType.RS, FileType.JAVA, FileType.GO, FileType.PY, FileType.MD, FileType.KOTLIN -> {
                                        val sftp = ssh.getChSftpOrNull()
                                        sftp?.let {
                                            val fileContentStage = FileContentStage(item, ssh, it)
                                            fileContentStage.start(Stage())
                                        } ?: {
                                            //todo 弹出dialog提示没有多余的通道
                                        }
                                    }

                                    else -> {}
                                }
                            }
//                            if (it.clickCount == 2 && item.fileType == FileType.IMG) {
//                                val sftp = ssh.getChSftpOrNull()
//                                sftp?.let {
//                                    try {
//                                        val inputStream = it.get(item.path)
//                                        val bytes = inputStream.readBytes() // 将 InputStream 中的数据缓存到内存中
//                                        ShowImage(ImageView(Image(ByteArrayInputStream(bytes))), item.fileName).start(Stage())
//                                        inputStream.close()
//                                    } catch (e: Exception) {
//                                        println("捕获到异常 $e")
//                                    } finally {
//                                        ssh.releaseChannel(it)
//                                    }
//                                }
//                            } else if (it.clickCount == 2 && (item.fileType != FileType.IMG
//                                        || item.fileType == FileType.PY
//                                        || item.fileType == FileType.JAVA
//                                        || item.fileType == FileType.RS
//                                        || item.fileType == FileType.GO)
//                            ) {
//                                val sftp = ssh.getChSftpOrNull()
//                                sftp?.let {
//                                    val fileContentStage = FileContentStage(item, ssh, it)
//                                    fileContentStage.start(Stage())
//                                } ?: {
//                                }
//                            }
                        }
                        HBox(5.0, imageView, label).apply {
                            tooltip = Tooltip(item.fileName).apply {
                                style = "-fx-background-color: #FFFACD;-fx-text-fill:black;-fx-font-size: 13"
                            }
                        }
                    } else
                        null
                }
            }
            cell.addEventHandler<DragEvent>(DragEvent.DRAG_OVER, EventHandler { event: DragEvent ->
                if (cell.item.fileType==FileType.FOLDER)
                    label.text = "上传到${cell.item.fileName}"
                event.acceptTransferModes(TransferMode.COPY)
            })
            cell.addEventHandler<DragEvent>(DragEvent.DRAG_EXITED, EventHandler { event: DragEvent ->
                label.text =  cell.item.fileName
            })
            cell.addEventHandler<DragEvent>(DragEvent.DRAG_DROPPED, EventHandler { event: DragEvent ->
                if (cell.item.fileType==FileType.FOLDER){
                    event.dragboard.files.forEach {
                        taskHandler.addIOTask(FileIOPane.FileIOType.UPLOAD,it.absolutePath,cell.item.path)
                    }
                }
                event.isDropCompleted = true
            })
            cell
        }
        treeView.selectionModel.selectionMode = SelectionMode.MULTIPLE
        treeView.setOnContextMenuRequested { it ->
            treeAreaRightMenu?.hide()
            treeAreaRightMenu = TreeAreaRightMenu(this@TreeArea, treeView.selectionModel.selectedItems)
            treeAreaRightMenu!!.show(this@TreeArea, it.screenX + 3.0, it.screenY + 3.0)
        }
        treeView.setOnMouseClicked {
            /**
             * 因为这个右键菜单只有在treeView失去焦点时才失去不符合习惯，强制消除一下
             */
            if (it.button!=MouseButton.SECONDARY)
                treeAreaRightMenu?.hide()
        }
        treeView.stylesheets.add(this::class.java.getResource("/css/xbss.css")?.toExternalForm())
        treeView.prefWidth = InitSize.TREEVIEW_WIDTH
        treeView.prefHeight = InitSize.TEXTAREA_HEIGHT-30.0
        // AppVersion记录的bug9：打jar包后文件树多选操作不会即刻生效，只有滚动一下或者改变文件树大小后才会将选中item高亮
        // v0.5.5：不使用jar包，使用javapackage打包，未发现此问题，将相关代码注掉了
//        treeView.selectionModel.selectedItemProperty().addListener { _,_,_ ->
//            treeView.refresh()
//        }
        registerShortCut()
        // 用反射监听item左边的小三角是否被点击，觉得不是很有必要，不写了
//        val behavior = treeView.skinProperty().addListener { _,_,newSkin ->
//            val skin = newSkin as TreeViewSkin<*>
//            val behaviorField = TreeViewSkin::class.java.getDeclaredField("behavior")
//            behaviorField.isAccessible = true
//            val behavior = behaviorField.get(treeViewSkin) as TreeViewBehavior<*>
//            behavior.onDisclosureNode = { event ->
//                val treeItem = event.treeItem
//                if (treeItem.isExpanded) {
//                    println("Node ${treeItem.value} is expanded")
//                } else {
//                    println("Node ${treeItem.value} is collapsed")
//                }
//            }
//        }
    }
    private fun registerShortCut(){
        treeView.setOnKeyPressed { keyEvent ->
            when(keyEvent.code){
                KeyCode.C -> {
                    if (keyEvent.isControlDown&&!keyEvent.isShiftDown){
                        TreeAreaRightMenu.waitToCopyPathList = treeView.selectionModel.selectedItems.map { it.value.path }
                    }else if (keyEvent.isControlDown&&keyEvent.isShiftDown){
                        val content = ClipboardContent()
                        content.putString(treeView.selectionModel.selectedItem.value.fileName)
                        Clipboard.getSystemClipboard().setContent(content)
                    }
                }
                KeyCode.V -> {
                    if (keyEvent.isControlDown){
                        TreeAreaRightMenu.waitToCopyPathList.let {
                            val item = treeView.selectionModel.selectedItem
                            if (item.value.fileType==FileType.FOLDER){
                                taskHandler.addCommandTask(TreeAreaRightMenu.waitToCopyPathList,item.value.path)
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }

    /**
     *
     *  展开该路径最终点的文件夹（可以在没有初试化的时候使用，会自动加载需要的结点）  对外的接口
     * @param path /data1/pjf/SSA2  这种格式的
     */
    private fun expandItem(path:String){
        val list = (path.split("/") as MutableList<String>).filter { it.isNotEmpty() } as MutableList<String>
        expandItemS(rootTreeItem,list)
    }

    /**
     *  真正的递归展开函数
     *  todo: bug10:应该是在v0.5.3后，之前版本的逻辑是在每个递归找到指定目录后自动滚动到指定目录，bug表现为滚动完成到指定目录后，会朝下滚动若干距离。
     *  可能是fx21的问题？
     *  优化了一下滚动逻辑，现在v0.5.5，不会在每个递归都滚动到指定目录，只在递归结束时滚动到focus目录。（这并没有修复此bug）
     *  修复：在递归结束时使用fx线程，休眠1s后在滚动到focus目录。（试过0.5s不行）
     * @param item
     * @param path
     * @return
     */
    private fun expandItemS(item: LsTreeItem, path: MutableList<String>) {
        //退出条件
        if (path.isEmpty()) {
            Platform.runLater { //bug10
                Thread.sleep(1000) //（试过0.5s不行）
                treeView.scrollTo(treeView.selectionModel.selectedIndex)
            }
            return
        }
        if (item.loadComplete.value) {//如果该结点已加载完成（一般都是未加载完成）
            item.children.find { it.value.path.contains(path.first()) }?.let {
                it.isExpanded =true
                path.removeFirst()
                expandItemS(it as LsTreeItem,path)
            }
        }else{
            item.loadComplete.addListener { _,_,_ ->
                item.children.find { it.value.path.contains(path.first()) }?.let {
                    Platform.runLater {//由于外面不是fx线程，要转移一下
                        treeView.selectionModel.clearSelection()
                        treeView.selectionModel.select(it)
                        treeView.focusModel.focus(treeView.selectionModel.selectedIndex)
//                        treeView.scrollTo(treeView.selectionModel.selectedIndex)
                        it.isExpanded = true
                    }
//                    it.isExpanded =true
//                    println("找到了${path.first()},展开了${it.value}")
                    path.removeFirst()
                    expandItemS(it as LsTreeItem,path)
                }
            }
        }
    }


    /**
     * 刷新当前选择的节点：把当前选中结点删除，然后重新按索引添加进来
     */
    fun refreshItem(){
//        val index = treeView.selectionModel.selectedIndex //这个index是整个树的idnex，要用父亲的index才行
        val selectedItem = treeView.selectionModel.selectedItem  as LsTreeItem// 当前选择的文件夹
        val path = pathIndication.text
        val parent = selectedItem.parent
        val index = parent.children.indexOf(selectedItem) //用父亲的index才行
        parent.children.remove(selectedItem)
        val newItem = LsTreeItem(ssh, path)
//        println("正在将${newItem}重新添加到${parent}的第${index}位置，当前一共有${parent.children.size}个孩子")
        parent.children.add(index,newItem)
        newItem.isExpanded = true

        treeView.selectionModel.clearSelection()

        treeView.selectionModel.select(newItem)
        treeView.focusModel.focus(treeView.selectionModel.selectedIndex)
    }

    /**
     * 刷新当前选择的节点的父节点：用于删除、重命名结点后
     */
    fun refreshFatherItem(){
        val selectedItem = treeView.selectionModel.selectedItem  as LsTreeItem// 当前选择的文件夹
        val parent = selectedItem.parent as LsTreeItem
        val path = parent.value.path
        val gradFather = parent.parent
        val index = gradFather.children.indexOf(parent) //用爷爷的index才行
        gradFather.children.remove(parent)
        val newItem = LsTreeItem(ssh, path)
        gradFather.children.add(index,newItem)
        newItem.isExpanded = true

        treeView.selectionModel.clearSelection()

        treeView.selectionModel.select(newItem)
        treeView.focusModel.focus(treeView.selectionModel.selectedIndex)
    }
}