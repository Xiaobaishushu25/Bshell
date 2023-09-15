package xbss.myterminal.jediterm.terminal.ui.settings;

import xbss.myterminal.jediterm.terminal.TtyConnector;
import xbss.myterminal.jediterm.terminal.ui.TerminalActionPresentation;
import org.jetbrains.annotations.NotNull;

/**
 * @author traff
 */
public interface TabbedSettingsProvider extends SettingsProvider {
  boolean shouldCloseTabOnLogout(TtyConnector ttyConnector);

  String tabName(TtyConnector ttyConnector, String sessionName);

  @NotNull TerminalActionPresentation getNewSessionActionPresentation();

  @NotNull TerminalActionPresentation getCloseSessionActionPresentation();

  @NotNull TerminalActionPresentation getPreviousTabActionPresentation();

  @NotNull TerminalActionPresentation getNextTabActionPresentation();
}
