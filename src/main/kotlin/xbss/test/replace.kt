package xbss.test

/**
 * @author  Xbss
 * @create 2023-03-24-15:37
 * @version  1.0
 * @describe
 */
fun main() {
//    val x= "0, 11607 MiB, 24576 MiB\n" +
//            "1, 1767 MiB, 24576 MiB\n" +
//            "2, 2869 MiB, 24576 MiB\n" +
//            "3, 21177 MiB, 24576 MiB"
//    for (s in x.split("\n")) {
//        println(s)
//    }
//    val x:String? = null
//    val command = x?.let {
//        it
//    }?:run{"textField.texsadt"}
//    println(command)
//    val s = 10.145784215
//    println(s.reserveOne())
    println("---\r---")
    val s = "s\rds"
    println(s.contains((0x0D).toChar()))
    println(0x0D)
    println((0x0D).toChar())
    println("\r")
    println("------")
    println('\r'==(0x0D).toChar())
}
private fun Double.reserveOne():Double{
    println(String.format("%.1f", this))
    return 0.0
}