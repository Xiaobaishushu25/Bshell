package xbss.config

import xbss.myterminal.jediterm.terminal.TerminalColor
import xbss.myterminal.jediterm.terminal.TextStyle

/**
 * @author  Xbss
 * @create 2023-05-17-11:24
 * @version  1.0
 * @describe:专为terminal写的颜色类，用于添加文本时的颜色设置
 */
enum class TextColor(private val foreground: TerminalColor, private val background:TerminalColor) {
    RED(TerminalColor(238, 44, 44), TerminalColor(255,246,142,0)),
    GREEN(TerminalColor(46, 139,87), TerminalColor(255,246,142,0)),
    INFO(TerminalColor(0,0,139), TerminalColor(255,246,142,0));
    fun getForeground() = foreground
    fun getBackground() = background
    fun getTextStyle() = TextStyle(foreground, background)
}