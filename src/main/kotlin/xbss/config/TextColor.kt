package xbss.config

import xbss.myterminal.jediterm.terminal.TerminalColor
import xbss.myterminal.jediterm.terminal.TextStyle

/**
 * @author  Xbss
 * @create 2023-05-05-16:15
 * @version  1.0
 * @describe :专为terminal写的颜色类，用于添加文本时的颜色设置
 */
object TextColor {
    val RED = TextStyle(
        TerminalColor(238, 44, 44),
        TerminalColor(255,246,142,0)
    )
    val GREEN = TextStyle(
        TerminalColor(46, 139, 87),
        TerminalColor(255,246,142,0)
    )
    val INFO = TextStyle(
        TerminalColor(	0, 0, 139),
        TerminalColor(255,246,142,0)
    )
}