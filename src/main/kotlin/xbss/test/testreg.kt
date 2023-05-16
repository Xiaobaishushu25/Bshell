package xbss.test

/**
 * @author  Xbss
 * @create 2023-03-26-13:56
 * @version  1.0
 * @describe
 */
fun main() {
//    val text1 = "This is a test string.\r\nIt contains \ra single carriage return."
//    val text1 = "This is a test string.\rIt contains a single carriage return."
//    val text2 = "This is another test string.\r\nIt contains a carriage return and a line feed."
//
    val text1 = "Last login: Mon Mar 27 02:56:35 2023 from 10.0.87.148 \r\r\n(base) [msfgroup@msfgroup1 ~] "
    val regex = Regex("(?<!\\r)\\r(?!\\n)")

    if (text1.contains(regex)) {
        println(true)
    } else {
        println(false)
    }

//    val regex = "\r(?!\n)".toRegex()
//    if (text1.contains(regex)) {
//        println("text1 contains a single carriage return")
//    } else {
//        println("text1 does not contain a single carriage return")
//    }
//
//    if (text2.contains(regex)) {
//        println("text2 contains a single carriage return")
//    } else {
//        println("text2 does not contain a single carriage return")
//    }
}
