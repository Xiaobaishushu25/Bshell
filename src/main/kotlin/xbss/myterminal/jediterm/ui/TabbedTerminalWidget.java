package xbss.myterminal.jediterm.ui;

import xbss.myterminal.jediterm.terminal.ui.JediTermWidget;
import xbss.myterminal.jediterm.terminal.ui.settings.TabbedSettingsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * @author traff
 */
public class TabbedTerminalWidget extends AbstractTabbedTerminalWidget<JediTermWidget> {
  public TabbedTerminalWidget(@NotNull TabbedSettingsProvider settingsProvider, @NotNull Function<AbstractTabbedTerminalWidget, JediTermWidget> createNewSessionAction) {
    super(settingsProvider, createNewSessionAction::apply);
  }

  @Override
  public JediTermWidget createInnerTerminalWidget() {
    return new JediTermWidget(getSettingsProvider());
  }

  @Override
  protected AbstractTabs<JediTermWidget> createTabbedPane() {
    return new TerminalTabsImpl();
  }
}
