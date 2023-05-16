package xbss.test

import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import java.io.*
import java.util.Properties
import kotlin.concurrent.thread

/**
 * @author  Xbss
 * @create 2023-03-14-12:38
 * @version  1.0
 * @describe
 */
fun main() {
    var username = "msfgroup"
    var password = "msfg302"
//    var host = "118.195.188.157"
    var host = "10.255.248.48"
//    var port = 11248
    var port = 22
    val jSch = JSch()
//    var session:Session? = null
    var result = false
    val session = jSch.getSession(username, host, port)
    session.setPassword(password)
    val properties = Properties().apply {
        put("StrictHostKeyChecking", "no")
    }
    session.setConfig(properties)
    session.timeout = 6000
    session.connect()
    val reulst = session.isConnected
    if (reulst) {
        println("【SSH连接】连接成功");
    } else {
        println("【SSH连接】连接失败");
    }
    val jschChannel = session.openChannel("shell")
    jschChannel.setInputStream(System.`in`);
    jschChannel.setOutputStream(System.out);
//    jschChannel.setOutputStream(baos)
    jschChannel.connect();
    val baos = ByteArrayOutputStream()
}