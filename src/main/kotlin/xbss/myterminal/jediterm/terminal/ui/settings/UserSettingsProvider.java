package xbss.myterminal.jediterm.terminal.ui.settings;

import xbss.myterminal.jediterm.terminal.HyperlinkStyle;
import xbss.myterminal.jediterm.terminal.TextStyle;
import xbss.myterminal.jediterm.terminal.emulator.ColorPalette;
import xbss.myterminal.jediterm.terminal.model.TerminalTypeAheadSettings;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public interface UserSettingsProvider {
  ColorPalette getTerminalColorPalette();

  Font getTerminalFont();

  float getTerminalFontSize();

  /**
   * @return vertical scaling factor 设置行间距
   */
  default float getLineSpacing() {
    return 1.0f;
  }

  default boolean shouldDisableLineSpacingForAlternateScreenBuffer() {
    return false;
  }

  default boolean shouldFillCharacterBackgroundIncludingLineSpacing() {
    return true;
  }

  TextStyle getDefaultStyle();

  TextStyle getSelectionColor();
  TextStyle getCursorStyle();

  TextStyle getFoundPatternColor();

  TextStyle getHyperlinkColor();

  HyperlinkStyle.HighlightMode getHyperlinkHighlightingMode();

  boolean useInverseSelectionColor();

  boolean copyOnSelect();

  boolean pasteOnMiddleMouseClick();

  boolean emulateX11CopyPaste();

  boolean useAntialiasing();

  int maxRefreshRate();

  boolean audibleBell();

  boolean enableMouseReporting();

  int caretBlinkingMs();

  boolean scrollToBottomOnTyping();

  boolean DECCompatibilityMode();

  boolean forceActionOnMouseReporting();

  int getBufferMaxLinesCount();
  
  boolean altSendsEscape();

  boolean ambiguousCharsAreDoubleWidth();

  @NotNull TerminalTypeAheadSettings getTypeAheadSettings();

  boolean sendArrowKeysInAlternativeMode();
}
