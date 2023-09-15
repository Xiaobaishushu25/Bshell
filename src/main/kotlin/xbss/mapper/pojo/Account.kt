package xbss.server.mapper.pojo

import org.ktorm.entity.Entity

/**
 * @author  Xbss
 * @create 2022-12-01-23:49
 * @version  1.0
 * @describe
 */
interface Account:Entity<Account> {
    companion object:Entity.Factory<Account>()
    var id:String
    var nickname:String
    var username:String
    var password:String
    var host:String
    var port:String
    var defaultPath:String?
    var initCommand:String?
    var comments: String?
}