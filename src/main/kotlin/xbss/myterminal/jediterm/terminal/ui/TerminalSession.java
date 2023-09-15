package xbss.myterminal.jediterm.terminal.ui;

import xbss.myterminal.jediterm.terminal.Terminal;
import xbss.myterminal.jediterm.terminal.TtyConnector;
import xbss.myterminal.jediterm.terminal.debug.DebugBufferType;
import xbss.myterminal.jediterm.terminal.model.TerminalTextBuffer;

/**
 * @author traff
 */
public interface TerminalSession {
  void start();

  String getBufferText(DebugBufferType type, int stateIndex);

  TerminalTextBuffer getTerminalTextBuffer();

  Terminal getTerminal();

  TtyConnector getTtyConnector();

  void close();
}
