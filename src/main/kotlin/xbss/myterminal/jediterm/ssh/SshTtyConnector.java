package xbss.myterminal.jediterm.ssh;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import xbss.myterminal.jediterm.core.util.TermSize;
import xbss.myterminal.jediterm.terminal.Questioner;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

public class SshTtyConnector implements DisposableTtyConnector {
	private InputStreamReader myInputStreamReader;
	private InputStream myInputStream = null;
	private OutputStream myOutputStream = null;
	//private SessionChannel shell;
	private ChannelShell shell;
	//private Session channel;
	private AtomicBoolean isInitiated = new AtomicBoolean(false);
	private AtomicBoolean isCancelled = new AtomicBoolean(false);
	private AtomicBoolean stopFlag = new AtomicBoolean(false);
	private Dimension myPendingTermSize;
	private Dimension myPendingPixelSize;
	//private SshClient2 wr;
	private Session wr;
	//private String initialCommand;
	//private SessionInfo info;
	//private SessionContentPanel sessionContentPanel;

	//public SshTtyConnector(SessionInfo info, String initialCommand, SessionContentPanel sessionContentPanel) {
	//	this.initialCommand = initialCommand;
	//	this.info = info;
	//	this.sessionContentPanel = sessionContentPanel;
	//}

	//public SshTtyConnector(SessionInfo info, SessionContentPanel sessionContentPanel) {
	//	this(info, null, sessionContentPanel);
	//}


  @Override
	public boolean init(Questioner q) {
		try {
      System.out.println("thar");
			//this.wr = new SshClient2(this.info, App.getInputBlocker(), sessionContentPanel);
      JSch jsch = new JSch();
      String user = "xjl";
      String host = "10.12.140.84";
      int port = 22;
      String password = "xujinlong2019";
			this.wr = jsch.getSession(user, host, port);
      this.wr.setPassword(password);
      this.wr.setTimeout(16000);
      this.wr.setConfig("StrictHostKeyChecking", "no");
			this.wr.connect();
      System.out.println(this.wr.isConnected());
      this.shell = (ChannelShell) wr.openChannel("shell");
      //this.shell.setPtySize(80, 24, 640, 480);

			//this.channel = wr.openSession();
			//this.channel.setAutoExpand(true);


			//this.channel.allocatePTY(App.getGlobalSettings().getTerminalType(), App.getGlobalSettings().getTermWidth(),
			//		App.getGlobalSettings().getTermHeight(), 0, 0,   Collections.<PTYMode, Integer>emptyMap());
			//this.channel.setEnvVar("LANG", "en_US.UTF-8");
			//this.shell = (SessionChannel) this.channel.startShell();


			myInputStream = shell.getInputStream();// channel.getInputStream();
			myOutputStream = shell.getOutputStream();// channel.getOutputStream();
      //myOutputStream.write("nvidia-smi".getBytes());
			myInputStreamReader = new InputStreamReader(myInputStream, "utf-8");
      resizeImmediately();
			System.out.println("Initiated");

			//if (initialCommand != null) {
			//	myOutputStream.write((initialCommand + "\n").getBytes("utf-8"));
			//	myOutputStream.flush();
			//}

			isInitiated.set(true);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			isInitiated.set(false);
			isCancelled.set(true);
			return false;
		}
	}

	@Override
	public void close() {
		try {
			stopFlag.set(true);
			System.out.println("Terminal wrapper disconnecting");
			wr.disconnect();
		} catch (Exception e) {
		}
	}

	@Override
	public void resize(Dimension termSize, Dimension pixelSize) {
		myPendingTermSize = termSize;
		myPendingPixelSize = pixelSize;
		//if (channel != null) {
		//	resizeImmediately();
		//}

//		if (channel == null) {
//			return;
//		}
//		System.out.println("Terminal resized");
//		channel.setPtySize(termSize.width, termSize.height, pixelSize.width, pixelSize.height);
	}

	@Override
	public String getName() {
		return "Remote";
	}

	@Override
	public int read(char[] buf, int offset, int length) throws IOException {
    System.out.println("read");
		return myInputStreamReader.read(buf, offset, length);
	}

	@Override
	public void write(byte[] bytes) throws IOException {
    System.out.println("write");
		myOutputStream.write(bytes);
		myOutputStream.flush();
	}

	@Override
	//public boolean isConnected() {
	//	return channel != null && channel.isOpen() && isInitiated.get();
	//}
	public boolean isConnected() {
		return wr.isConnected()  && isInitiated.get();
	}

  @Override
  public void resize(@NotNull TermSize termSize) {
    DisposableTtyConnector.super.resize(termSize);
  }

  @Override
	public void write(String string) throws IOException {
		write(string.getBytes("utf-8"));
	}

	@Override
	public int waitFor() throws InterruptedException {
		System.out.println("Start waiting...");
		while (!isInitiated.get() || isRunning()) {
			System.out.println("waiting");
			Thread.sleep(100); // TODO: remove busy wait
		}
		System.out.println("waiting exit");
		//try {
		//	shell.join();
		//} catch (ConnectionException e) {
		//	// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
		return shell.getExitStatus();
	}

  @Override
  public boolean ready() throws IOException {
    return false;
  }

  public boolean isRunning() {
		return shell != null && wr.isConnected();
	}

	public boolean isBusy() {
		return wr.isConnected();
	}

	public boolean isCancelled() {
		return isCancelled.get();
	}

	public void stop() {
		stopFlag.set(true);
		close();
	}

	public int getExitStatus() {
		if (shell != null) {
			Integer exit = shell.getExitStatus();
			return exit == null ? -1 : exit;
		}
		return -2;
	}

	private void resizeImmediately() {
		//if (myPendingTermSize != null && myPendingPixelSize != null) {
		//	setPtySize(shell, myPendingTermSize.width, myPendingTermSize.height, myPendingPixelSize.width,
		//			myPendingPixelSize.height);
		//	myPendingTermSize = null;
		//	myPendingPixelSize = null;
		//}
	}

	//private void setPtySize(Shell shell, int col, int row, int wp, int hp) {
	private void setPtySize(ChannelShell shell, int col, int row, int wp, int hp) {
		System.out.println("Exec pty resized:- col: " + col + " row: " + row + " wp: " + wp + " hp: " + hp);
		if (shell != null) {
				//shell.changeWindowDimensions(col, row, wp, hp);
				shell.setPtySize(col, row, wp, hp);

		}
		// channel.setPtySize(col, row, wp, hp);
	}

	@Override
	public boolean isInitialized() {
		return isInitiated.get();
	}

}