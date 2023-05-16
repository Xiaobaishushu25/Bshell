package xbss.myterminal.jediterm.terminal.ui.settings;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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


public class DefaultSettingsProvider implements SettingsProvider {
  private Font font;
  private float fontSize = 15.0f;

  //  private TextStyle defaultStyle = new TextStyle(TerminalColor.BLACK, TerminalColor.rgb(142,299,238,0));
  private TextStyle defaultStyle = new TextStyle(new TerminalColor(28,28,28),new TerminalColor(255,246,142,0));
  private TextStyle cursorStyle = new TextStyle(new TerminalColor(148, 0, 211),new TerminalColor(148, 0, 211));
  private TextStyle selectionColor = new TextStyle(TerminalColor.BLACK, TerminalColor.rgb(151, 255, 255));
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
  public void setTerminalFontSize(float size) {
    fontSize = size;
    font = font.deriveFont(fontSize);
  }

  @Override
  public TextStyle getDefaultStyle() {
    //return new TextStyle(TerminalColor.BLACK, TerminalColor.WHITE);
//    return new TextStyle(TerminalColor.BLACK, TerminalColor.rgb(142,299,238));
//    return new TextStyle(TerminalColor.BLACK, TerminalColor.rgb(142,299,238,0));
    return defaultStyle;
//    return new TextStyle(TerminalColor.BLACK, null);
    // return new TextStyle(TerminalColor.WHITE, TerminalColor.rgb(24, 24, 24));
  }
  public void setDefaultStyle(TerminalColor foreground, TerminalColor background) {
    defaultStyle = new TextStyle(foreground, background);
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
  public TextStyle getSelectionColor() {
    return selectionColor;
  }
  public void setSelectionColor(TerminalColor foreground,TerminalColor background){
    selectionColor = new TextStyle(foreground,background);
  }

  /**
   * 这两个我自己加的，用于设置光标的颜色（未被选择时），其中foreground是光标失去焦点后那个框的颜色，
   *       background是光标获得焦点后那个实心块的颜色
   * @return
   */
  public TextStyle getCursorStyle(){
    return cursorStyle;
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
