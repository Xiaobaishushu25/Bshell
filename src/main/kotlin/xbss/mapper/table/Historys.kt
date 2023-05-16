package xbss.mapper.table

import org.ktorm.schema.Table
import org.ktorm.schema.varchar
import xbss.server.mapper.pojo.History

/**
 * @author  Xbss
 * @create 2022-11-24-22:57
 * @version  1.0
 * @describe
 */
object Historys:Table<History>("history") {
    val id = varchar("id").primaryKey().bindTo { it.id }
    val command =  varchar("command").bindTo { it.command }
}