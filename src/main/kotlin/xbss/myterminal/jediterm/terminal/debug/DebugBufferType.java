package xbss.myterminal.jediterm.terminal.debug;



import xbss.myterminal.jediterm.terminal.LoggingTtyConnector;
import xbss.myterminal.jediterm.terminal.ui.TerminalSession;

import java.util.List;

/**
 * @author traff
 */
public enum DebugBufferType {
  Back() {
    public String getValue(TerminalSession session, int stateIndex) {
      List<LoggingTtyConnector.TerminalState> states = ((LoggingTtyConnector) session.getTtyConnector()).getStates();
      if (stateIndex == states.size()) {
        return session.getTerminalTextBuffer().getScreenLines();
      } else {
        return states.get(stateIndex).myScreenLines;
      }
    }
  },
  BackStyle() {
    public String getValue(TerminalSession session, int stateIndex) {
      List<LoggingTtyConnector.TerminalState> states = ((LoggingTtyConnector) session.getTtyConnector()).getStates();
      if (stateIndex == states.size()) {
        return session.getTerminalTextBuffer().getStyleLines();
      } else {
        return states.get(stateIndex).myStyleLines;
      }
    }
  },
  Scroll() {
    public String getValue(TerminalSession session, int stateIndex) {
      List<LoggingTtyConnector.TerminalState> states = ((LoggingTtyConnector) session.getTtyConnector()).getStates();
      if (stateIndex == states.size()) {
        return session.getTerminalTextBuffer().getHistoryBuffer().getLines();
      } else {
        return states.get(stateIndex).myHistoryLines;
      }
    }
  };

  public abstract String getValue(TerminalSession session, int stateIndex);
}
