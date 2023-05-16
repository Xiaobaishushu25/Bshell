package xbss.myterminal.jediterm.example;

import com.jcraft.jsch.ChannelShell;
import com.pty4j.PtyProcess;
import com.pty4j.WinSize;
import org.jetbrains.annotations.NotNull;

import java.io.*;

/**
 * @author Xbss
 * @version 1.0
 * @create 2023-04-17-12:57
 * @describe
 */
public class MyProcess extends PtyProcess {
  private ByteArrayOutputStream output = new ByteArrayOutputStream(); //用户获得输出用的管道
  private PipedOutputStream userWrite; //用户写入命令用的管道
  private InputStream inputStream  = new PipedInputStream();
  private OutputStream outputStream =new ByteArrayOutputStream();
  private ChannelShell shell;
  public MyProcess(ChannelShell shell) throws IOException {
    this.shell = shell;
    PipedInputStream pipedInput = new PipedInputStream();
    userWrite = new PipedOutputStream();
    pipedInput.connect(userWrite);
    outputStream = shell.getOutputStream();
    inputStream = shell.getInputStream();
  }

  @Override
  public void setWinSize(WinSize winSize) {
    System.out.println("调用setWinSize");
    shell.setPtySize(80, 24, 640, 480);
  }

  @Override
  public @NotNull WinSize getWinSize() throws IOException {
    System.out.println("调用getWinSize");
    return null;
  }

  @Override
  public OutputStream getOutputStream() {
    return outputStream;
    //return null;
  }

  @Override
  public InputStream getInputStream() {
    System.out.println("here");
    return inputStream;
    //return null;
  }

  @Override
  public InputStream getErrorStream() {
    return null;
  }

  @Override
  public int waitFor() throws InterruptedException {
    return 0;
  }

  @Override
  public int exitValue() {
    return 0;
  }

  @Override
  public void destroy() {

  }
}
