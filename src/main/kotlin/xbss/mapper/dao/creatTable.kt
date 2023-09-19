package xbss.mapper.dao

import xbss.config.DatabasePro
import java.sql.DriverManager

/**
 * @author  Xbss
 * @create 2023-03-22-15:00
 * @version  1.0
 * @describe
 */
fun main() {
    println(DatabasePro.URL)
    val connection = DriverManager.getConnection(DatabasePro.URL, DatabasePro.USER, DatabasePro.PASSWORD)
    val statement = connection.createStatement()
//    statement.execute("DROP TABLE account")
    statement.execute("CREATE TABLE `history` (`id` varchar(3) DEFAULT NULL,`command` varchar(100) DEFAULT NULL)")
    statement.execute("CREATE TABLE `account` (`id` varchar(20) DEFAULT NULL,`nickname` varchar(20) DEFAULT NULL,`username` varchar(40) DEFAULT NULL,`password` varchar(40) DEFAULT NULL,`host` varchar(40) DEFAULT NULL,`port` varchar(10) DEFAULT NULL,`defaultPath` varchar(80) DEFAULT NULL,`initCommand` varchar(100) DEFAULT NULL,`comments` varchar(100) DEFAULT NULL)")
//    statement.executeUpdate("INSERT INTO history(`id`,`command`) VALUES('515','java -version')")
//    val resultSet = statement.executeQuery("select * from account")
//    val resultSet = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='account';")
//    while (resultSet.next()) {
//        val columnName = resultSet.getString("COLUMN_NAME")
//        val dataType = resultSet.getString("TYPE_NAME")
//        val isNullable = resultSet.getString("IS_NULLABLE")
//        val defaultValue = resultSet.getString("COLUMN_DEFAULT")
//        println("$columnName $dataType $isNullable $defaultValue")
//    }
//    while (resultSet.next())
//        println(resultSet.getString("id")+"------>"+resultSet.getString("command"))
    statement.close()
    connection.close()
}