package xbss.myterminal.jediterm.terminal.ui;

import xbss.myterminal.jediterm.terminal.RequestOrigin;
import org.jetbrains.annotations.NotNull;


public interface TerminalPanelListener {
  void onPanelResize(@NotNull RequestOrigin origin);

  void onTitleChanged(String title);
}
