package xbss.ssh

import com.jcraft.jsch.JSch
import javafx.application.Platform
import javafx.beans.property.SimpleIntegerProperty
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import xbss.MainAPP
import xbss.config.AppData
import xbss.server.mapper.pojo.Account
import java.lang.Exception
import java.util.*

/**
 * @author  Xbss
 * @create 2023-03-14-20:48
 * @version  1.0
 * @describe :测试连接 :先返回一个SimpleIntegerProperty，然后开启线程连接，动态监听其值变化，初始值是0，1是连接失败2是成功
 */
fun testConnect(account: Account):SimpleIntegerProperty{
    val logger: Logger = LoggerFactory.getLogger(account.nickname)
    val flag = SimpleIntegerProperty(0)
    val jSch = JSch()
    val session = jSch.getSession(account.username, account.host, account.port.toInt())
    session.setPassword(account.password)
    val properties = Properties().apply {
        put("StrictHostKeyChecking", "no")
    }
    session.setConfig(properties)
    MainAPP.service.submit {
        session.timeout = 20000
        try {
            session.connect()
            if (session.isConnected){
                logger.info("连接成功")
                Platform.runLater {
                    AppData.addAccount(account)
                    flag.value = 2
                }
            }
            else{
                logger.error("连接失败")
                Platform.runLater { flag.value = 1 }
            }
        }catch (e:Exception){
            logger.error("连接失败,原因是${e}")
            println("连接失败 原因是${e}")
            Platform.runLater { flag.value = 1 }
        }
    }
    return flag
}