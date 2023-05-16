package xbss.ssh

import com.jcraft.jsch.JSch
import javafx.application.Platform
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import xbss.MainAPP
import xbss.config.AppData
import xbss.server.mapper.pojo.Account
import java.lang.Exception
import java.util.*
import kotlin.concurrent.thread

/**
 * @author  Xbss
 * @create 2023-03-14-20:48
 * @version  1.0
 * @describe :测试连接 :返回一个SimpleIntegerProperty，动态监听其值变化，1是连接失败2是成功
 */
fun testConnect(account: Account):SimpleIntegerProperty{
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
                Platform.runLater {
                    AppData.addAccount(account)
                    flag.value = 2
                }
            }
            else{
                Platform.runLater { flag.value = 1 }
            }
        }catch (e:Exception){
            Platform.runLater { flag.value = 1 }
        }
    }
    return flag
}