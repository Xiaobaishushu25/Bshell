package xbss.config

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
}