package xbss.utils

import java.util.*


/**
 * @author  Xbss
 * @create 2023-03-23-15:28
 * @version  1.0
 * @describe
 */
fun randomNumberGenerator():String{
    val random = Random()
    return (random.nextInt(900) + 100).toString()
}