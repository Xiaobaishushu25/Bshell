package xbss.server.mapper.pojo

import org.ktorm.entity.Entity

/**
 * @author  Xbss
 * @create
 * @version  1.0
 * @describe
 */
interface History:Entity<History> {
    companion object:Entity.Factory<History>()
    var id:String
    var command:String
}