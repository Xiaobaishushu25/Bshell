package xbss.mapper.dao

import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.*
import xbss.config.DatabasePro
import xbss.mapper.table.Historys
import xbss.server.mapper.pojo.Account
import xbss.server.mapper.pojo.History
import xbss.server.mapper.table.Accounts

/**
 * @author  Xbss
 * @create 2022-11-27-22:26
 * @version  1.0
 * @describe :这样写全是顶层函数，在项目任何地方都可以访问数据库了，怎么控制可见性呢？
 */
val database = Database.connect(DatabasePro.URL, user = DatabasePro.USER, password = DatabasePro.PASSWORD)

val Database.accounts
    get() = this.sequenceOf(Accounts)
val Database.historys
    get() = this.sequenceOf(Historys)

fun insertAccount(account: Account):Boolean{
    return database.accounts.add(account) == 1
}
fun insertHistory(history: History):Boolean{
    return database.historys.add(history) == 1
}
fun queryAccounts():MutableList<Account>{
    return database.accounts.toMutableList()
}
fun queryHistorys():MutableList<History>{
    return database.historys.toMutableList()
}
fun deleteHistorys(commandD:String){
    database.historys.removeIf { it.command eq  commandD }
}
fun deleteAccountById(id:String){
    database.accounts.removeIf { it.id eq id }
}

