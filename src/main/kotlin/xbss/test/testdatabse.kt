package xbss.test

import xbss.mapper.dao.insertAccount
import xbss.mapper.dao.insertHistory
import xbss.mapper.dao.queryAccounts
import xbss.mapper.dao.queryHistorys
import xbss.server.mapper.pojo.Account
import xbss.server.mapper.pojo.History

/**
 * @author  Xbss
 * @create 2023-03-23-13:53
 * @version  1.0
 * @describe
 */
fun main() {
//    insertAccount(Account{
//        nickname = "msfg"
//        username = "msfgroup"
//        password = "msfg302"
//        host = "10.255.248.48"
//        port = "22"
//    })
//    insertHistory(History{
//        id = "515"
//        command = "java -version"
//    })
    println(queryAccounts())
//    println(queryHistorys()[0])
}