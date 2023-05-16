package xbss.utils

import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import xbss.config.ImageIcon

/**
 * @author  Xbss
 * @create 2023-03-30-0:14
 * @version  1.0
 * @describe
 */
class CommandAreaRightMenu(): ContextMenu() {
//    private val ssh = treeArea.ssh
//    private val taskHandler = treeArea.taskHandler
    private lateinit var create:MenuItem
    private lateinit var reName:MenuItem
    private lateinit var down:MenuItem
    private lateinit var reSave:MenuItem
    private lateinit var upFile:MenuItem
    private lateinit var upDir:MenuItem
    private lateinit var delete:MenuItem
    init {
        initItem()
        this.items.addAll(create,reName,SeparatorMenuItem(),down,reSave,SeparatorMenuItem(),upFile,upDir,SeparatorMenuItem(),delete)
    }
    private fun initItem(){
        create = MenuItem().apply{
            graphic = HBox(10.0,ImageView(ImageIcon.CREATE), getBlackTextLabel("新建文件夹"))
            setOnAction {
            }
        }
        reName = MenuItem().apply {
            graphic = HBox(10.0,ImageView(ImageIcon.RENAME16),getBlackTextLabel("重命名"))
            setOnAction {
            }
        }
        down = MenuItem().apply {
            graphic = HBox(10.0,ImageView(ImageIcon.DOWNLOAD),getBlackTextLabel("下载"))
            setOnAction {
            }
        }
        reSave = MenuItem().apply {
            graphic = HBox(10.0,ImageView(ImageIcon.RESAVE16),getBlackTextLabel("另存为"))
            setOnAction {
            }
        }
        upFile = MenuItem().apply {
            graphic = HBox(10.0,ImageView(ImageIcon.UPLOAD),getBlackTextLabel("上传文件"))
            setOnAction {
            }
        }
        upDir = MenuItem().apply {
            graphic = HBox(10.0,ImageView(ImageIcon.UPDIR),getBlackTextLabel("上传文件夹"))
            setOnAction {
            }
        }
        delete = MenuItem().apply {
            graphic = HBox(10.0,ImageView(ImageIcon.DELETE16),Label("删除").apply {  style = "-fx-text-fill:red" })
            setOnAction {
            }
        }
    }
    private fun getBlackTextLabel(text:String) = Label(text).apply {  style = "-fx-text-fill:black"  }
}