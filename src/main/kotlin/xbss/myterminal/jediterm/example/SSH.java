package xbss.myterminal.jediterm.example;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import xbss.myterminal.jediterm.pty.PtyProcessTtyConnector;
import xbss.myterminal.jediterm.terminal.TtyConnector;
import xbss.myterminal.jediterm.terminal.ui.UIUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Xbss
 * @version 1.0
 * @create 2023-04-17-10:55
 * @describe
 */
public class SSH {
  public static TtyConnector getssh() throws JSchException, IOException {
    // 创建JSch对象
    JSch jsch = new JSch();

// 设置登录信息
    String user = "xjl";
    String host = "10.12.140.84";
    int port = 22;
    String password = "xujinlong2019";
    //jsch.getSession(user, host, port).setPassword(password);

    Session session = jsch.getSession(user, host, port);
    session.setPassword(password);
    session.setTimeout(16000);
    session.setConfig("StrictHostKeyChecking", "no");

    session.connect();

    System.out.println(session.isConnected());

    ChannelShell channelShell = (ChannelShell) session.openChannel("shell");

    //channelShell.setPtySize(80, 24, 640, 480);
    //InputStream in = channelShell.getInputStream();
    //OutputStream out = channelShell.getOutputStream();
    //OutputStream err = channelShell.getErrStream();

    Map<String, String> envs = System.getenv();
    String[] command;
    if (UIUtil.isWindows) {
      command = new String[]{"cmd.exe"};
    } else {
      command = new String[]{"/bin/bash", "--login"};
      envs = new HashMap<>(System.getenv());
      envs.put("TERM", "xterm-256color");
    }
    //PtyProcess process = new PtyProcessBuilder().setCommand(command).setEnvironment(envs).start();
    String[] c = {""};
    //PtyProcess ptyProcess = new PtyProcessBuilder(c).start();
    //PtyProcess process = new PtyProcessBuilder().setCommand(new String[]{"/bin/bash", "-i"}).start();
    channelShell.connect();
    MyProcess process = new MyProcess(channelShell);
    //InputStream processIn = process.getInputStream();
    //OutputStream processOut = process.getOutputStream();
    //channelShell.setOutputStream(processOut);
    //channelShell.setOutputStream(process.getOutputStream());
    //channelShell.setInputStream(process.getInputStream());

    //PipedInputStream pipedInputStream = new PipedInputStream();
    //PipedOutputStream pipedOutputStream = new PipedOutputStream();
    //pipedOutputStream.connect();
    //Thread inputThread = new Thread(() -> {
    //  byte[] buffer = new byte[1024];
    //  int len;
    //  try {
    //    while ((len = in.read(buffer)) != -1) {
    //      System.out.println("this");
    //      processOut.write(buffer, 0, len);
    //      processOut.flush();
    //    }
    //  } catch (IOException e) {
    //    e.printStackTrace();
    //  }
    //});

    //Thread outputThread = new Thread(() -> {
    //  byte[] buffer = new byte[1024];
    //  int len;
    //  try {
    //    while ((len = processIn.read(buffer)) != -1) {
    //      System.out.println("that");
    //      out.write(buffer, 0, len);
    //      out.flush();
    //    }
    //  } catch (IOException e) {
    //    e.printStackTrace();
    //  }
    //});

    //inputThread.start();
    //outputThread.start();
    return new PtyProcessTtyConnector(process, StandardCharsets.UTF_8);
  }
}
