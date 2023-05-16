package xbss.myterminal.jediterm.terminal.ui;

import xbss.myterminal.jediterm.terminal.SubstringFinder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.KeyListener;

public interface JediTermSearchComponent {
  @NotNull JComponent getComponent();

  void addListener(@NotNull JediTermSearchComponentListener listener);

  void addKeyListener(@NotNull KeyListener listener);

  void onResultUpdated(@Nullable SubstringFinder.FindResult results);
}
