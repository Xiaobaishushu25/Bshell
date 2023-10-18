package xbss.utils

import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import javafx.scene.layout.HBox
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import javafx.stage.Stage
import xbss.config.FileType
import xbss.config.ImageIcon
import xbss.config.Setting
import xbss.view.TreeArea
import java.io.File

/**
 * @author  Xbss
 * @create 2023-03-30-0:14
 * @version  1.0
 * @describe
 */
class TreeAreaRightMenu(private val treeArea: TreeArea): ContextMenu() {
    companion object{
        lateinit var waitToCopyPathList:List<String>
    }
    /**
     *每个右键菜单有三种可能：①全部支持②仅支持文件、文件夹（即不支持多选）③仅支持文件夹
     */
    enum class SupportType {
        ALL,NOTMULTIPLE,FOLDER,
    }
    private val ssh = treeArea.ssh
    private val taskHandler = treeArea.taskHandler
    /**
     * 是否多选
     */
    private var isMultiple = false
    private var item:LsTreeItem.FileItem? = null
    private var itemList:List<TreeItem<LsTreeItem.FileItem>>? = null
    private lateinit var tip:MenuItem
    /**
     * 仅支持文件夹
     */
    private lateinit var createDir: MenuItem

    /**
     * 仅支持文件夹
     */
    private lateinit var createFile: MenuItem
    /**
     * 仅支持文件夹、文件
     */
    private lateinit var reName:MenuItem
    /**
     * 仅支持文件夹、文件
     */
    private lateinit var copyName:MenuItem
    /**
     * 支持文件、文件夹、多选
     */
    private lateinit var copyFile:MenuItem
    /**
     * 仅支持文件夹
     */
    private lateinit var pasteFile:MenuItem
    /**
     * 仅支持文件夹
     */
    private lateinit var cdIInto:MenuItem
    /**
     * 全部支持
     */
    private lateinit var down:MenuItem
    /**
     * 全部支持
     */
    private lateinit var reSave:MenuItem
    /**
     * 仅支持文件夹
     */
    private lateinit var upFile:MenuItem
    /**
     * 仅支持文件夹
     */
    private lateinit var upDir:MenuItem
    /**
     * 全部支持
     */
    private lateinit var delete:MenuItem

    /**
     * 这个cItem中的c代表constructor的意思
     */
    constructor(treeArea: TreeArea, cItem: LsTreeItem.FileItem):this(treeArea){
        this.item = cItem
        initMenu()
    }
    constructor(treeArea: TreeArea, cItemList:List<TreeItem<LsTreeItem.FileItem>>):this(treeArea){
        if (cItemList.size!=1){
            isMultiple = true
            itemList = cItemList
        }else
            this.item = cItemList[0].value
        initMenu()
    }
    private fun initMenu(){
        initItem()
//        if (!isMultiple)
//            this.items.addAll(createDir,reName,cdIInto,SeparatorMenuItem(),copyName,copyFile,pasteFile,SeparatorMenuItem(),down,reSave,SeparatorMenuItem(),upFile,upDir,SeparatorMenuItem(),delete)
//        else{
//            initTip()
//            this.items.addAll(tip,createDir,reName,cdIInto,SeparatorMenuItem(),copyName,copyFile,pasteFile,SeparatorMenuItem(),down,reSave,SeparatorMenuItem(),upFile,upDir,SeparatorMenuItem(),delete)
//        }
        if (isMultiple) {
            initTip()
            this.items.add(tip)
        }
        this.items.addAll(
            createDir,
            createFile,
            reName,
            cdIInto,
            SeparatorMenuItem(),
            copyName,
            copyFile,
            pasteFile,
            SeparatorMenuItem(),
            down,
            reSave,
            SeparatorMenuItem(),
            upFile,
            upDir,
            SeparatorMenuItem(),
            delete
        )

    }
    private fun initTip(){
        tip = MenuItem().apply{
            isDisable = true
            graphic = getBlackTextLabel("已选择${itemList!!.size}个文件")
        }
    }

    /**
     * 初始化所有基础item
     */
    private fun initItem(){
        createDir = MenuItem().apply {
            graphic = getHBox(ImageView(ImageIcon.CREATEDIR), getBlackTextLabel("新建文件夹"))
            setSupport(SupportType.FOLDER)
            setOnAction {
                PopName().apply {
                    textObservable.addListener { _,_,newValue ->
                        if (newValue.isNotEmpty()){
                            ssh.execCommand("mkdir "+item!!.path+"/"+newValue)
                            treeArea.refreshItem()
                        }
                    }
                    start(Stage())
                }
            }
        }
        createFile = MenuItem().apply {
            graphic = getHBox(ImageView(ImageIcon.CREATEFILE), getBlackTextLabel("新建文件"))
            setSupport(SupportType.FOLDER)
            setOnAction {
                PopName().apply {
                    textObservable.addListener { _, _, newValue ->
                        if (newValue.isNotEmpty()) {
                            ssh.execCommand("touch " + item!!.path + "/" + newValue)
                            treeArea.refreshItem()
                        }
                    }
                    start(Stage())
                }
            }
        }
        reName = MenuItem().apply {
            graphic = getHBox(ImageView(ImageIcon.RENAME16),getBlackTextLabel("重命名"))
            setSupport(SupportType.NOTMULTIPLE)
            setOnAction {
                PopName(item!!.fileName).apply {
                    textObservable.addListener { _,_,newValue ->
                        if (newValue.isNotEmpty()&&newValue!=item!!.fileName){
                            ssh.execCommand("mv ${item!!.path} ${item!!.path.replace(item!!.fileName,"")}/$newValue")
                            treeArea.refreshFatherItem()
                        }
                    }
                    start(Stage())
                }
            }
        }
        copyName = MenuItem().apply {
            graphic = getHBox(ImageView(ImageIcon.COPY16),getBlackTextLabel("复制文件名"))
            setSupport(SupportType.NOTMULTIPLE)
            setOnAction {
                val content = ClipboardContent()
                content.putString(item!!.fileName)
                Clipboard.getSystemClipboard().setContent(content)
            }
        }
        copyFile = MenuItem().apply {
//            graphic = getHBox(ImageView(ImageIcon.COPY),getBlackTextLabel("复制文件"))
            graphic = getBlackTextLabel("复制文件").apply { translateX = 16.0 + 6.0 }
            setSupport()
            setOnAction {
                waitToCopyPathList = if (!isMultiple){
                    listOf(item!!.path)
                }else{
                    itemList!!.map { it.value.path }
                }
            }
        }
        pasteFile = MenuItem().apply {
            graphic = getHBox(ImageView(ImageIcon.PASTE16),getBlackTextLabel("粘贴文件"))
            setSupport(SupportType.FOLDER)
            setOnAction {
                taskHandler.addCommandTask(waitToCopyPathList,item!!.path)
//                MainAPP.service.submit {
//                    if (waitToCopyPath.isNotEmpty()){
//                        ssh.execCommand("cp -r $waitToCopyPath ${item!!.path}")
//                        waitToCopyPath = ""
//                    }else if(waitToCopyPathList.isNotEmpty()){
//                        /**
//                         * 要一边遍历一边删除，所以要用倒序删除防止集合修改异常
//                         */
//                        for (i in waitToCopyPathList.size - 1 downTo 0) {
//                            ssh.execCommand("cp -r ${waitToCopyPathList.removeAt(i)} ${item!!.path}")
//                        }
//                    }
//                    Platform.runLater { treeArea.refreshFatherItem() }
//                }
            }
//            if (!isMultiple){
//                setOnAction {
//                    MainAPP.service.submit {
//                        if (waitToCopyPath.isNotEmpty()){
//                            ssh.execCommand("cp -r $waitToCopyPath ${item!!.path}")
//                            waitToCopyPath = ""
//                        }else if(waitToCopyPathList.isNotEmpty()){
//                            for (i in waitToCopyPathList.size - 1 downTo 0) {
//                                ssh.execCommand("cp -r ${waitToCopyPathList.removeAt(i)} ${item!!.path}")
//                            }
//                        }
//                        Platform.runLater { treeArea.refreshItem() }
//                    }
//                }
//            }else
//                isDisable = true
        }
        cdIInto = MenuItem().apply {
            graphic = getHBox(ImageView(ImageIcon.INTO16),getBlackTextLabel("进入此目录")).apply { alignment = Pos.CENTER }
            setSupport(SupportType.FOLDER)
            setOnAction {
                ssh.getInput().write("cd ${item!!.path}\n".toByteArray())
            }
        }
        down = MenuItem().apply {
            graphic = getHBox(ImageView(ImageIcon.DOWNLOAD),getBlackTextLabel("下载"))
            setSupport()
            setOnAction {
                val path = Setting.savePathP.value
                File(path).apply {
                    if (!exists()){
                        //todo : 不存在的话弹出窗口询问是否坚持使用此目录或者修改
                        if (mkdirs()){
                            //todo ：创建失败给出提示(比如没有那么多盘符)
                            if (!isMultiple) {
                                taskHandler.addIOTask(FileIOPane.FileIOType.DOWN,item!!.path,path)
                            }else{
                                for (treeItem in itemList!!){
                                    taskHandler.addIOTask(FileIOPane.FileIOType.DOWN,treeItem.value.path,path)
                                }
                            }
                        }
                    } else {
                        if (!isMultiple) {
                            taskHandler.addIOTask(FileIOPane.FileIOType.DOWN, item!!.path, path)
                        } else {
                            for (treeItem in itemList!!) {
                                taskHandler.addIOTask(FileIOPane.FileIOType.DOWN, treeItem.value.path, path)
                            }
                        }
                    }
                }
//                if (!isMultiple) {
//                    taskHandler.addIOTask(FileIOPane.FileIOType.DOWN,item!!.path,path)
//                }else{
//                    for (treeItem in itemList!!){
//                        taskHandler.addIOTask(FileIOPane.FileIOType.DOWN,treeItem.value.path,path)
//                    }
//                }
            }
        }
        reSave = MenuItem().apply {
            graphic = getHBox(ImageView(ImageIcon.RESAVE16),getBlackTextLabel("另存为"))
            setSupport()
            setOnAction {
                DirectoryChooser().apply {
                    File(Setting.reSavePathP.value).apply {
                        if (exists())
                            initialDirectory = this
                    }
                    showDialog(treeArea.scene.window)?.let {
                        val newPath = it.absolutePath
                        if (newPath != Setting.reSavePathP.value)
                            Setting.setNewReSavePath(newPath)
                        if (!isMultiple){
                            taskHandler.addIOTask(FileIOPane.FileIOType.DOWN, item!!.path, newPath)
                        }else{
                            for (item in itemList!!){
                                taskHandler.addIOTask(FileIOPane.FileIOType.DOWN, item.value.path, newPath)
                            }
                        }
                    }
                }
            }
        }
        upFile = MenuItem().apply {
            graphic = getHBox(ImageView(ImageIcon.UPLOAD),getBlackTextLabel("上传文件"))
            setSupport(SupportType.FOLDER)
            setOnAction {
                FileChooser().apply {
                    showOpenDialog(treeArea.scene.window)?.let {
                        taskHandler.addIOTask(FileIOPane.FileIOType.UPLOAD,it.absolutePath,item!!.path)
//                        treeArea.refreshItem()
                    }
                }
            }
        }
        upDir = MenuItem().apply {
            graphic = getHBox(ImageView(ImageIcon.UPDIR),getBlackTextLabel("上传文件夹"))
            setSupport(SupportType.FOLDER)
//            if (item!!.fileType!=FileType.FOLDER)
//                isDisable = true
            setOnAction {
                DirectoryChooser().apply {
                    showDialog(treeArea.scene.window)?.let {
                        taskHandler.addIOTask(FileIOPane.FileIOType.UPLOAD,it.absolutePath,item!!.path)
//                        treeArea.refreshItem()
                    }
                }
            }
        }
        delete = MenuItem().apply {
            graphic = getHBox(ImageView(ImageIcon.DELETE16),Label("删除").apply {  style = "-fx-text-fill:red"  })
            setSupport()
            setOnAction {
                val bounds = treeArea.localToScreen(treeArea.boundsInLocal)
                val popConfirm = PopConfirm(false)
                popConfirm.show(treeArea,bounds.minX+bounds.width/3,bounds.minY+bounds.height/3)
                popConfirm.choose.addListener { _,_,newValue ->
                    if (newValue == 2){
                        if (!isMultiple){
                            taskHandler.addCommandTask(listOf(item!!.path))
                        }else {
                            taskHandler.addCommandTask(itemList!!.map { it.value.path })
                        }
//                        MainAPP.service.submit {
//                            if (!isMultiple){
//                                taskHandler.addCommandTask(listOf(item!!.path))
////                                ssh.execCommand("rm -r ${item!!.path}")
//                            }else{
//                                taskHandler.addCommandTask(itemList!!.map { it.value.path })
////                                for (item in itemList!!){
////                                    println("删除${item.value.path}")
////                                    ssh.execCommand("rm -r ${item.value.path}")
////                                }
//                            }
////                            Platform.runLater { treeArea.refreshFatherItem()}
//                        }
                    }
                }
            }
        }
    }
    private fun getBlackTextLabel(text:String) = Label(text).apply {  style = "-fx-text-fill:black"  }
    private fun getHBox(node1:Node,node2:Node) = HBox(6.0,node1,node2).apply {  alignment = Pos.CENTER  }

    /**
     * 根据传来的支持类型设置一个结点是否可以被点击，默认是全部支持
     * @param supportType
     */
    private fun MenuItem.setSupport(supportType: SupportType = SupportType.ALL){
        when(supportType){
            SupportType.FOLDER -> {
                if (isMultiple)
                    isDisable = true
                else if (item!!.fileType!=FileType.FOLDER)
                    isDisable = true
            }
            SupportType.NOTMULTIPLE -> {
                if (isMultiple)
                    isDisable = true
            }
            else -> {}
        }
    }
}