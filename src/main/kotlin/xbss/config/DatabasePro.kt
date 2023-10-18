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
    val userPath: String = System.getProperty("user.dir")
    val URL = "jdbc:h2:$userPath${File.separator}database${File.separator}data"
    const val USER = "root"
    const val PASSWORD = "abc123"


    init {
        if (!File("$userPath${File.separator}database${File.separator}data.mv.db").exists()) {
            GlobalLog.writeInfoLog("数据库文件不存在，创建数据库.")
            val connection = DriverManager.getConnection(URL, USER, PASSWORD)
            val statement = connection.createStatement()
            statement.execute("CREATE TABLE `history` (`id` varchar(3) DEFAULT NULL,`command` varchar(100) DEFAULT NULL)")
            statement.execute("CREATE TABLE `account` (`id` varchar(20) DEFAULT NULL,`nickname` varchar(20) DEFAULT NULL,`username` varchar(40) DEFAULT NULL,`password` varchar(40) DEFAULT NULL,`host` varchar(40) DEFAULT NULL,`port` varchar(10) DEFAULT NULL,`defaultPath` varchar(80) DEFAULT NULL,`initCommand` varchar(100) DEFAULT NULL,`comments` varchar(100) DEFAULT NULL)")
            statement.close()
            connection.close()
        }
//        val configFile = File("$userPath${File.separator}database${File.separator}config")
//        if (!configFile.exists()) {
//            val defaultDirPath = "${userPath}${File.separator}download"
//            val downDir = File(defaultDirPath)
//            if (!downDir.exists())
//                downDir.mkdir()
//            GlobalLog.writeInfoLog("配置文件不存在，创建配置文件.")
//            if (configFile.createNewFile()){
//                val setting = Setting.SettingPersistence(
//                    null,
//                    0.5,
//                    defaultDirPath,
//                    defaultDirPath,
//                    16.0f,
//                    "148,0,211",
//                    true,
//                    "28,28,28",
//                    "255,246,142,0",
//                    "28,28,28",
//                    "151,255,255"
//                )
//                configFile.writeText(json.encodeToString(setting), charset("utf-8"))
//            }
//        }
    }
}