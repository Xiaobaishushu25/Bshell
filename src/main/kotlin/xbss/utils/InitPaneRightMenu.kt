package xbss.utils

import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.stage.Stage
import xbss.config.AppData
import xbss.config.ImageIcon
import xbss.server.mapper.pojo.Account
import xbss.view.InitPane
import xbss.view.NewConnect

/**
 * @author  Xbss
 * @create 2023-04-17-16:14
 * @version  1.0
 * @describe
 */
class InitPaneRightMenu(private val initPane: InitPane,private val account: Account,private val hBox: HBox): ContextMenu() {
    private lateinit var transfer: MenuItem
    private lateinit var delete: MenuItem
    init {
        initItem()
        this.items.addAll(transfer,SeparatorMenuItem(),delete)
    }
    private fun initItem() {
        transfer = MenuItem().apply {
            graphic = HBox(10.0, ImageView(ImageIcon.TRANSFER), getBlackTextLabel("迁移连接"))
            setOnAction {
                val newConnect = NewConnect(account,false)
                newConnect.start(Stage())
                newConnect.success.addListener { _,_,_ -> initPane.loadItems() }
            }
        }
        delete = MenuItem().apply {
            graphic = HBox(10.0,ImageView(ImageIcon.DELETE16), Label("删除").apply {  style = "-fx-text-fill:red" })
            setOnAction {
                val bounds = hBox.localToScreen(hBox.boundsInLocal)
                val popConfirm = PopConfirm()
                popConfirm.show(hBox,bounds.minX+bounds.width/3,bounds.maxY)
                popConfirm.choose.addListener { _,_,newValue ->
                    if (newValue == 2){
                        AppData.deleteAccountById(account)
                        initPane.loadItems()
                    }
                }
            }
        }
    }
    private fun getBlackTextLabel(text:String) = Label(text).apply {  style = "-fx-text-fill:black"  }
}