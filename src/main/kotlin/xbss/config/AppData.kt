package xbss.config

import xbss.mapper.dao.*
import xbss.server.mapper.pojo.Account
import xbss.server.mapper.pojo.History
import xbss.utils.randomNumberGenerator

/**
 * @author  Xbss
 * @create 2023-03-23-14:57
 * @version  1.0
 * @describe
 */
object AppData {
    val accounts = queryAccounts()
    val historyCommand = queryHistorys()
    fun addCommand(string: String) {
        if (historyCommand.none{ it.command == string }){
            val history = History {
                id = randomNumberGenerator()
                command = string
            }
            historyCommand.add(history)
            insertHistory(history)
        }
    }
    fun deleteCommand(string: String) {
        historyCommand.removeIf{it.command == string}
        deleteHistorys(string)
    }

    /**
     *  因为这里数据和数据库是同步的。不涉及增删改就不需要走数据库，
     * @param id
     * @return
     */
    fun queryAccount(id:String):Account{
//        println("查询到的"+accounts.find { it.id == id }!!.nickname)
        return accounts.find { it.id == id }!!
    }
    fun addAccount(account: Account):Boolean{
        accounts.add(account)
        return insertAccount(account)
    }
    fun deleteAccountById(account: Account){
        accounts.removeIf { it.id == account.id }
        deleteAccountById(account.id)
    }
    fun updateAccount(account: Account){
        deleteAccountById(account)
        addAccount(account)
    }

}