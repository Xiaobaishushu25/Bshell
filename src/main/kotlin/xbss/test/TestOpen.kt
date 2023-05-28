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
    val file = File("F:\\删除\\")
//    Desktop.getDesktop().open(file)
//    Runtime.getRuntime().exec("start ${file.path}")
    //执行命令时需要使用 command line 的语法，而 "start" 命令是 Windows 系统中的命令，
    // 它不能直接在命令行中执行，而是需要在 cmd.exe 中执行。因此，正确的方式是：
    Runtime.getRuntime().exec(arrayOf("cmd.exe", "/C", "start ${file.path}"))
}