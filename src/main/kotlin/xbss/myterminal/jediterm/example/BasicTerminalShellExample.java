package xbss.myterminal.jediterm.example;

import com.jcraft.jsch.JSchException;
import org.jetbrains.annotations.NotNull;
import xbss.myterminal.jediterm.terminal.ui.JediTermWidget;
import xbss.myterminal.jediterm.terminal.ui.settings.DefaultSettingsProvider;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class BasicTerminalShellExample {

  private static @NotNull JediTermWidget createTerminalWidget() throws JSchException, IOException {
    JediTermWidget widget = new JediTermWidget(80, 24, new DefaultSettingsProvider());
    //widget.setTtyConnector(createTtyConnector());
    widget.setTtyConnector(SSH.getssh());
    //widget.setTtyConnector(new SshTtyConnector());
    //ProcessBuilder processBuilder = new ProcessBuilder().redirectInput(new ProcessBuilder.Redirect().);
    widget.start();
    return widget;
  }


  private static void createAndShowGUI() {
    JFrame frame = new JFrame("Basic Terminal Shell Example");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    Button button = new Button();
    try {
      frame.setContentPane(createTerminalWidget());
    }
    catch (JSchException e) {
      throw new RuntimeException(e);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
    frame.pack();
    frame.setVisible(true);
  }

  public static void main(String[] args) {
    // Create and show this application's GUI in the event-dispatching thread.
    SwingUtilities.invokeLater(BasicTerminalShellExample::createAndShowGUI);
  }
}
