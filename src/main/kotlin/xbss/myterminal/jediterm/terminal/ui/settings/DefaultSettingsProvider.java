package xbss.myterminal.jediterm.terminal.ui.settings;

import org.jetbrains.annotations.NotNull;
import xbss.myterminal.jediterm.terminal.HyperlinkStyle;
import xbss.myterminal.jediterm.terminal.TerminalColor;
import xbss.myterminal.jediterm.terminal.TextStyle;
import xbss.myterminal.jediterm.terminal.emulator.ColorPalette;
import xbss.myterminal.jediterm.terminal.emulator.ColorPaletteImpl;
import xbss.myterminal.jediterm.terminal.model.LinesBuffer;
import xbss.myterminal.jediterm.terminal.model.TerminalTypeAheadSettings;
import xbss.myterminal.jediterm.terminal.ui.TerminalActionPresentation;
import xbss.myterminal.jediterm.terminal.ui.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Collections;

import static xbss.myterminal.jediterm.terminal.ui.AwtTransformers.fromAwtToTerminalColor;

//我加的函数有两种，一种set开头一种init开头，set开头可以重复调用实时刷新，init就初始化时调用一次，不实时刷新
public class DefaultSettingsProvider implements SettingsProvider {
  private Font font;
  private float fontSize = 15.0f;

  //  private TextStyle defaultStyle = new TextStyle(TerminalColor.BLACK, TerminalColor.rgb(142,299,238,0));
  //终端的字体颜色和背景颜色，默认是透明的
  private TextStyle defaultStyle = new TextStyle(new TerminalColor(28,28,28),new TerminalColor(255,246,142,0));
  private TextStyle cursorStyle = new TextStyle(new TerminalColor(148, 0, 211),new TerminalColor(148, 0, 211));
  private TextStyle selectionStyle = new TextStyle(TerminalColor.BLACK, TerminalColor.rgb(151, 255, 255));
  @Override
  public @NotNull TerminalActionPresentation getOpenUrlActionPresentation() {
    return new TerminalActionPresentation("Open as URL", Collections.emptyList());
  }

  @Override
  public @NotNull TerminalActionPresentation getCopyActionPresentation() {
    KeyStroke keyStroke = UIUtil.isMac
            ? KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.META_DOWN_MASK)
            // CTRL + C is used for signal; use CTRL + SHIFT + C instead
            : KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
    return new TerminalActionPresentation("Copy", keyStroke);
  }

  @Override
  public @NotNull TerminalActionPresentation getPasteActionPresentation() {
    KeyStroke keyStroke = UIUtil.isMac
            ? KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.META_DOWN_MASK)
            // CTRL + V is used for signal; use CTRL + SHIFT + V instead
            : KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
    return new TerminalActionPresentation("Paste", keyStroke);
  }

  @Override
  public @NotNull TerminalActionPresentation getClearBufferActionPresentation() {
    return new TerminalActionPresentation("Clear Buffer", UIUtil.isMac
            ? KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.META_DOWN_MASK)
            : KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK));
  }

  @Override
  public @NotNull TerminalActionPresentation getPageUpActionPresentation() {
    return new TerminalActionPresentation("Page Up",
            KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, InputEvent.SHIFT_DOWN_MASK));
  }

  @Override
  public @NotNull TerminalActionPresentation getPageDownActionPresentation() {
    return new TerminalActionPresentation("Page Down",
            KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, InputEvent.SHIFT_DOWN_MASK));
  }

  @Override
  public @NotNull TerminalActionPresentation getLineUpActionPresentation() {
    return new TerminalActionPresentation("Line Up", UIUtil.isMac
            ? KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.META_DOWN_MASK)
            : KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_DOWN_MASK));
  }

  @Override
  public @NotNull TerminalActionPresentation getLineDownActionPresentation() {
    return new TerminalActionPresentation("Line Down", UIUtil.isMac
            ? KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.META_DOWN_MASK)
            : KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_DOWN_MASK));
  }

  @Override
  public @NotNull TerminalActionPresentation getFindActionPresentation() {
    return new TerminalActionPresentation("Find", UIUtil.isMac
            ? KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.META_DOWN_MASK)
            : KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
  }

  @Override
  public @NotNull TerminalActionPresentation getSelectAllActionPresentation() {
    return new TerminalActionPresentation("Select All", Collections.emptyList());
  }

  @Override
  public ColorPalette getTerminalColorPalette() {
    return UIUtil.isWindows ? ColorPaletteImpl.WINDOWS_PALETTE : ColorPaletteImpl.XTERM_PALETTE;
  }
  //todo :设置终端字体
  public void setTerminalFont(){

  }

  @Override
  public Font getTerminalFont() {
    if (font!=null){
      return font;
    }
    String fontName;
    if (UIUtil.isWindows) {
      //fontName = "Consolas";
      fontName = "宋体";
    } else if (UIUtil.isMac) {
      fontName = "Menlo";
    } else {
      fontName = "Monospaced";
    }
    try {
      font = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("/font/FiraFZH2.ttf")).deriveFont(Font.PLAIN, (int)getTerminalFontSize());
      return font;
    }
    catch (FontFormatException e) {
      throw new RuntimeException(e);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
//    return new Font(fontName, Font.PLAIN, (int)getTerminalFontSize());
  }

  @Override
  public float getTerminalFontSize() {
    return fontSize;
  }

  /**
   * 用于初始化字体大小，因为setTerminalFontSize需要在font初始化后才能调用
   *
   * @param size
   */
  public void initTerminalFontSize(float size) {
    fontSize = size;
  }
  public void setTerminalFontSize(float size) {
    fontSize = size;
    font = font.deriveFont(fontSize);
  }

  @Override
  public TextStyle getDefaultStyle() {
    return defaultStyle;
    // return new TextStyle(TerminalColor.WHITE, TerminalColor.rgb(24, 24, 24));
  }

  public void initDefaultStyle(TerminalColor foreground, TerminalColor background) {
    defaultStyle = new TextStyle(foreground, background);
  }

  /**
   * 设置未选中的终端文本颜色
   *
   * @param textColor
   */
  public void initTextColorUnSelected(TerminalColor textColor) {
    defaultStyle = new TextStyle(textColor, defaultStyle.getBackground());
  }

  /**
   * 设置终端整个背景颜色（最好不要设置，默认是透明的）
   *
   * @param backColor
   */
  public void initTerminalColor(TerminalColor backColor) {
    defaultStyle = new TextStyle(defaultStyle.getForeground(), backColor);
  }

  /**
   * 设置选中时的终端文本颜色，这个函数应该用不到，因为初始化时用initSelectionStyle就够了
   *
   * @param textColor
   */
  public void initTextColorSelecting(TerminalColor textColor) {
    selectionStyle = new TextStyle(textColor, selectionStyle.getBackground());
  }

  /**
   * 设置选中时的终端背景颜色，这个函数应该用不到，因为初始化时用initSelectionStyle就够了
   */
  public void initTerminalBackColorSelecting(TerminalColor backColor) {
    selectionStyle = new TextStyle(selectionStyle.getForeground(), backColor);
  }


  /**
   * 设置选中的高亮前景（文字）及背景色
   * 是否启用选中颜色反转 {@link #useInverseSelectionColor()}
   * 在TerminalPanel中会使用此TextStyle{@link xbss.myterminal.jediterm.terminal.ui.TerminalPanel#getSelectionStyle(TextStyle)}
   * 注意：仅当此类的useInverseSelectionColor（）函数返回false时起作用，否则会使用反转颜色
   * @return
   */
  @Override
//  public TextStyle getSelectionColor() {
//    return new TextStyle(TerminalColor.BLACK, TerminalColor.rgb(151, 255, 255));
//  }
  public TextStyle getSelectionStyle() {
    return selectionStyle;
  }

  /**
   * 设置选中的背景
   *
   * @param textcolor  :选中的文字颜色
   * @param background ：选中的背景颜色
   */
  public void initSelectionStyle(TerminalColor textcolor, TerminalColor background) {
    selectionStyle = new TextStyle(textcolor, background);
  }

  /**
   * 下面三个函数我自己加的，用于设置光标的颜色（未被选择时），其中foreground是光标失去焦点后那个框的颜色，
   *       background是光标获得焦点后那个实心块的颜色（试了一下foreground不起效果，直接全用一个得了）
   * @return
   */
  public TextStyle getCursorStyle() {
    return cursorStyle;
  }

  public void initCursorColor(TerminalColor cursorColor) {
    cursorStyle = new TextStyle(cursorColor,cursorColor);
  }
  public void setCursorStyle(TerminalColor foreground,TerminalColor background){
    cursorStyle = new TextStyle(foreground,background);
  }

  @Override
  public TextStyle getFoundPatternColor() {
    return new TextStyle(TerminalColor.BLACK, TerminalColor.rgb(255, 255, 0));
  }

  @Override
  public TextStyle getHyperlinkColor() {
    return new TextStyle(fromAwtToTerminalColor(Color.BLUE), TerminalColor.WHITE);
  }

  @Override
  public HyperlinkStyle.HighlightMode getHyperlinkHighlightingMode() {
    return HyperlinkStyle.HighlightMode.HOVER;
  }

  @Override
  public boolean useInverseSelectionColor() {
//    return true;
    return false;
  }

  @Override
  public boolean copyOnSelect() {
    return emulateX11CopyPaste();
  }

  @Override
  public boolean pasteOnMiddleMouseClick() {
    return emulateX11CopyPaste();
  }

  @Override
  public boolean emulateX11CopyPaste() {
    return false;
  }

  @Override
  public boolean useAntialiasing() {
    return true;
  }

  @Override
  public int maxRefreshRate() {
    return 50;
  }

  @Override
  public boolean audibleBell() {
    return true;
  }

  @Override
  public boolean enableMouseReporting() {
    return true;
  }

  @Override
  public int caretBlinkingMs() {
    return 505;
  }

  @Override
  public boolean scrollToBottomOnTyping() {
    return true;
  }

  @Override
  public boolean DECCompatibilityMode() {
    return true;
  }

  @Override
  public boolean forceActionOnMouseReporting() {
    return false;
  }

  @Override
  public int getBufferMaxLinesCount() {
    return LinesBuffer.DEFAULT_MAX_LINES_COUNT;
  }

  @Override
  public boolean altSendsEscape() {
    return true;
  }

  @Override
  public boolean ambiguousCharsAreDoubleWidth() {
    return false;
  }

  @Override
  public @NotNull TerminalTypeAheadSettings getTypeAheadSettings() {
    return TerminalTypeAheadSettings.DEFAULT;
  }

  @Override
  public boolean sendArrowKeysInAlternativeMode() {
    return true;
  }
}
