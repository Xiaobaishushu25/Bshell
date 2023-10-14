package xbss.config

import java.io.File
import java.sql.DriverManager

/**
 * @author  Xbss
 * @create 2023-03-23-13:20
 * @version  1.0
 * @describe
 */
object DatabasePro {
    val URL = "jdbc:h2:"+System.getProperty("user.dir")+"\\database\\data"
    const val USER = "root"
    const val PASSWORD = "abc123"

    init {
        if (!File(System.getProperty("user.dir") + "\\database\\data.mv.db").exists()) {
            GlobalLog.writeInfoLog("数据库文件不存在，创建数据库.")
            val connection = DriverManager.getConnection(URL, USER, PASSWORD)
            val statement = connection.createStatement()
            statement.execute("CREATE TABLE `history` (`id` varchar(3) DEFAULT NULL,`command` varchar(100) DEFAULT NULL)")
            statement.execute("CREATE TABLE `account` (`id` varchar(20) DEFAULT NULL,`nickname` varchar(20) DEFAULT NULL,`username` varchar(40) DEFAULT NULL,`password` varchar(40) DEFAULT NULL,`host` varchar(40) DEFAULT NULL,`port` varchar(10) DEFAULT NULL,`defaultPath` varchar(80) DEFAULT NULL,`initCommand` varchar(100) DEFAULT NULL,`comments` varchar(100) DEFAULT NULL)")
            statement.close()
            connection.close()
        }
    }
}