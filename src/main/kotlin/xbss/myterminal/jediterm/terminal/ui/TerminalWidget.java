package xbss.myterminal.jediterm.terminal.ui;

import xbss.myterminal.jediterm.terminal.TerminalDisplay;
import xbss.myterminal.jediterm.terminal.TtyConnector;

import javax.swing.*;
import java.awt.*;

/**
 * @author traff
 */
public interface TerminalWidget {
  JediTermWidget createTerminalSession(TtyConnector ttyConnector);

  JComponent getComponent();

  default JComponent getPreferredFocusableComponent() {
    return getComponent();
  }

  boolean canOpenSession();

  void setTerminalPanelListener(TerminalPanelListener terminalPanelListener);

  Dimension getPreferredSize();

  TerminalDisplay getTerminalDisplay();

  void addListener(TerminalWidgetListener listener);
  void removeListener(TerminalWidgetListener listener);
}
