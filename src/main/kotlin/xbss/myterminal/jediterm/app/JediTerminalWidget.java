package xbss.myterminal.jediterm.app;

import com.jediterm.app.JediTerminalPanel;
import org.jetbrains.annotations.NotNull;
import xbss.myterminal.intellij.openapi.Disposable;
import xbss.myterminal.intellij.openapi.util.Disposer;
import xbss.myterminal.jediterm.terminal.model.StyleState;
import xbss.myterminal.jediterm.terminal.model.TerminalTextBuffer;
import xbss.myterminal.jediterm.terminal.ui.JediTermWidget;
import xbss.myterminal.jediterm.terminal.ui.settings.SettingsProvider;

public class JediTerminalWidget extends JediTermWidget implements Disposable {

  public JediTerminalWidget(SettingsProvider settingsProvider, Disposable parent) {
    super(settingsProvider);
    setName("terminal");

    Disposer.register(parent, this);
  }

  @Override
  protected JediTerminalPanel createTerminalPanel(@NotNull SettingsProvider settingsProvider,
                                                  @NotNull StyleState styleState,
                                                  @NotNull TerminalTextBuffer textBuffer) {
    JediTerminalPanel panel = new JediTerminalPanel(settingsProvider, styleState, textBuffer);
    Disposer.register(this, panel);
    return panel;
  }

  @Override
  public void dispose() {
  }
}
