package xbss.server.mapper.table

import org.ktorm.schema.Table
import org.ktorm.schema.varchar
import xbss.server.mapper.pojo.Account

/**
 * @author  Xbss
 * @create 2022-11-27-22:24
 * @version  1.0
 * @describe
 */
object Accounts : Table<Account>("account"){
    var id = varchar("id").bindTo { it.id }
    var nickname = varchar("nickname").bindTo { it.nickname }
    var username = varchar("username").bindTo { it.username }
    var password = varchar("password").bindTo { it.password }
    var host = varchar("host").bindTo { it.host }
    var port = varchar("port").bindTo { it.port }
    var defaultPath = varchar("DEFAULTPATH").bindTo { it.defaultPath }
    var initCommand = varchar("INITCOMMAND").bindTo { it.initCommand }
    var comments = varchar("comments").bindTo { it.comments }
}