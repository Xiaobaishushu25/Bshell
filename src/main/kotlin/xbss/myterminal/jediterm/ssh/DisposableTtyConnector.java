package xbss.myterminal.jediterm.ssh;

import xbss.myterminal.jediterm.terminal.Questioner;
import xbss.myterminal.jediterm.terminal.TtyConnector;

public interface DisposableTtyConnector extends TtyConnector {
	public void stop();

	public boolean isCancelled();

	public boolean isBusy();

  boolean init(Questioner q);

  public boolean isRunning();

	public int getExitStatus();

	public boolean isInitialized();
}
