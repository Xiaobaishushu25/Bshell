package xbss.test

import java.awt.Desktop
import java.io.File

/**
 * @author  Xbss
 * @create 2023-05-04-15:18
 * @version  1.0
 * @describe
 */
fun main() {
    val file = File("F:\\删除\\attack.py")
    Desktop.getDesktop().open(file)
}