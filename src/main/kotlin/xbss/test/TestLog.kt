package xbss.test

import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import xbss.server.mapper.pojo.Account
import xbss.ssh.SSH
import xbss.utils.SystemData
import xbss.view.CommandArea
import xbss.view.RightBar
import xbss.view.TreeArea

/**
 * @author  Xbss
 * @create 2023-04-27-15:52
 * @version  1.0
 * @describe
 */
fun main() {
    val logger = LoggerFactory.getLogger("测试日志")
    logger.info("这是测试1")
    logger.info("这是测试2")
    logger.info("这是测试3")
    logger.error("这是测试1")
    logger.error("这是测试2")
    logger.error("这是测试3")
}
class MainWindow(val ssh: SSH): AnchorPane(){
    private val systemData = SystemData(ssh) //系统信息区域
    init {
        //初始化代码

    }
}